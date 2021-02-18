package sensecloud.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sensecloud.auth2.model.UserInfo;
import sensecloud.connector.Connector;
import sensecloud.connector.SinkType;
import sensecloud.connector.SourceType;
import sensecloud.connector.rule.RuleProvider;
import sensecloud.connector.rule.pebble.PebbleExpRule;
import sensecloud.submitter.airflow.RestfulApiSubmitter;
import sensecloud.web.bean.ConnectorBean;
import sensecloud.web.entity.ConnectorEntity;
import sensecloud.web.mapper.ConnectorMapper;
import sensecloud.web.service.IConnectorService;
import sensecloud.web.service.UserSupport;
import sensecloud.web.service.remote.ClickHouseRemoteService;
import sensecloud.web.service.remote.MysqlCDCService;
import sensecloud.web.utils.DesUtil;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ConnectorServiceImpl extends ServiceImpl<ConnectorMapper, ConnectorEntity> implements IConnectorService {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private RestfulApiSubmitter submitter;

    @Autowired
    private RuleProvider ruleProvider;

    @Autowired
    private MysqlCDCService mysqlCDCService;

    @Autowired
    private ClickHouseRemoteService clickHouseRemoteService;

    @Value("${service.connector.clickhouse.des.key}")
    private String ch_pwd_decrypt_key;

    public boolean addConnectorJob(ConnectorBean bean) {
        String saas = StringUtils.isNotBlank(bean.getSaas())? bean.getSaas() : "undefined";
        Connector connector = this.buildConnector(bean);

        UserInfo userInfo = this.userSupport.currentUserInfo();
        JSONObject submitterInfo = (JSONObject) JSON.toJSON(userInfo);
        return this.submitter.submitConnectorJob(
                saas,
                userInfo.getUsername(),
                submitterInfo,
                connector);
    }

    public boolean updateConnectorJob(ConnectorBean bean) {
        String saas = StringUtils.isNotBlank(bean.getSaas())? bean.getSaas() : "undefined";
        Connector connector = this.buildConnector(bean);
        UserInfo userInfo = this.userSupport.currentUserInfo();
        JSONObject submitterInfo = (JSONObject) JSON.toJSON(userInfo);
        return this.submitter.updateConnectorJob(saas, userInfo.getUsername(), submitterInfo, connector);
    }

    public boolean deleteConnectorJob(ConnectorBean bean) {
        String connectorName = bean.getName();
        String saas = StringUtils.isNotBlank(bean.getSaas())? bean.getSaas() : "undefined";
        String appName = connectorName;
        return this.submitter.removeConnectorJob(saas, userSupport.getCurrentUserName(), appName);
    }

    private Connector buildConnector(ConnectorBean bean) {
        String connectorName = bean.getName();
        String ruleName = bean.getSourceType().toLowerCase() + "2" + bean.getSinkType().toLowerCase();
        String ruleExpr = ruleProvider.getRuleExpression(ruleName);
        PebbleExpRule rule = new PebbleExpRule().expression(ruleExpr);

        JSONObject sinkAccountConf = bean.getSinkAccountConf();
        //decrypt clickhouse password
        String encryptedPwd = sinkAccountConf.getString("jdbc.password");
        log.info("encryptedPwd = {}", encryptedPwd);
        if(StringUtils.isNotBlank(encryptedPwd)) {
            bean.getSinkAccountConf().put("jdbc.password", DesUtil.decrypt(this.ch_pwd_decrypt_key, encryptedPwd));
        }

        Connector connector = new Connector()
                .name(connectorName)
                .sourceType(SourceType.valueOf(bean.getSourceType().toUpperCase()))
                .sourceAccountConf(bean.getSourceAccountConf())
                .sourceConf(bean.getSourceConf())
                .sinkType(SinkType.valueOf(bean.getSinkType().toUpperCase()))
                .sinkAccountConf(sinkAccountConf)
                .sinkConf(bean.getSinkConf())
                .rule(rule);
        connector.configure();

        return connector;
    }

    public boolean addMysqlCDC (ConnectorBean bean) {
        JSONObject params = this.buildMysqlCDCServiceParams(bean);
        log.info("Start to call remote mysqlCDCService.add with parameters: {}", params);
        JSONObject callback = this.mysqlCDCService.add(params);
        log.info("Start to call remote mysqlCDCService.add and callback: {}", callback);
        boolean result = false;
        if(callback != null) {
            int code = callback.getInteger("code");
            String message = callback.getString("msg");
            if (code == 0) {
                result = true;
            } else {
                log.error("Error occurred while calling getClickHouseUser: {}", message);
                result = false;
            }
        }

        return result;
    }

    public boolean updateMysqlCDC(ConnectorBean bean) {
        JSONObject params = this.buildMysqlCDCServiceParams(bean);
        log.debug(">>> Call mysql CDC update with parameters: {}", params);
        JSONObject callback = this.mysqlCDCService.update(params);
        log.debug(">>> Call mysql CDC update and callback : {}", callback);
        boolean result = false;
        if(callback != null) {
            int code = callback.getInteger("code");
            String message = callback.getString("msg");
            if (code == 0) {
                result = true;
            } else {
                log.error("Error occurred while calling getClickHouseUser: {}", message);
                result = false;
            }
        }

        return result;
    }

    public boolean deleteMysqlCDC(ConnectorBean bean) {
        log.debug(">>> Call mysql CDC delete with parameters: {}", bean.getId());
        JSONObject callback = this.mysqlCDCService.delete(bean.getId());
        log.debug(">>> Call mysql CDC update and callback : {}", callback);
        boolean result = false;
        if(callback != null) {
            int code = callback.getInteger("code");
            String message = callback.getString("msg");
            if (code == 0) {
                result = true;
            } else {
                log.error("Error occurred while calling getClickHouseUser: {}", message);
                result = false;
            }
        }

        return result;
    }

    public JSONObject getClickHouseUser(String username) {
        log.info("Fetch username = {} to call getClickHouseUser", username);
        JSONObject callback = null;
        try {
            callback = this.clickHouseRemoteService.getClickHouseUser(username);
            log.info("Call getClickHouseUser finished. And callback is {}", callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject result = new JSONObject();

        if(callback != null) {
            int code = callback.getInteger("code");
            String message = callback.getString("msg");
            if (code == 0) {
                JSONObject data = callback.getJSONObject("data");
                result.putAll(data);
                log.debug("Callback data: {}", data);
            } else {
                log.error("Error occurred while calling getClickHouseUser: {}", message);
                result = null;
            }
        }
//        else {
//            result.put("id", 10);
//            result.put("ckUser", "writer");
//            result.put("ckPassword", "2c82mirS");
//            result.put("userType", "admin");
//        }
        log.debug("Call [getClickHouseUser] and return : {}", result);
        return result;
    }

    private JSONObject buildMysqlCDCServiceParams(ConnectorBean bean) {
        JSONObject params = new JSONObject();
        JSONObject accountConf = bean.getSourceAccountConf();
        JSONObject sourceConf = bean.getSourceConf();

        params.put("id", Long.valueOf(bean.getId()));
        params.put("dbPassword", accountConf.getString("jdbc.pwd"));
        params.put("dbUser", accountConf.getString("jdbc.user"));

        params.put("dbUrl", sourceConf.getString("jdbc.url"));

        String db = sourceConf.getString("db");
        params.put("targerDb", db);

        JSONArray tables = sourceConf.getJSONArray("tables");

        JSONArray tbs = new JSONArray();
        for (int i = 0 ; i < tables.size() ; i ++) {
            String tb = tables.getString(i);
            tbs.add(db + "." + tb);
        }
        params.put("table", tbs);
        return params;
    }

}
