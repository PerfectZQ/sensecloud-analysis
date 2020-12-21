package sensecloud.web.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sensecloud.connector.Connector;
import sensecloud.connector.SinkType;
import sensecloud.connector.SourceType;
import sensecloud.connector.rule.RuleProvider;
import sensecloud.connector.rule.pebble.PebbleExpRule;
import sensecloud.connector.submitter.airflow.AirflowSubmitter;
import sensecloud.web.bean.ConnectorBean;
import sensecloud.web.entity.ConnectorEntity;
import sensecloud.web.mapper.ConnectorMapper;
import sensecloud.web.service.IConnectorService;
import sensecloud.web.service.remote.ClickHouseRemoteService;
import sensecloud.web.service.remote.MysqlCDCService;

@Slf4j
@Service
public class ConnectorServiceImpl extends ServiceImpl<ConnectorMapper, ConnectorEntity> implements IConnectorService {

    private Connector connector;

    @Autowired
    private AirflowSubmitter submitter;

    @Autowired
    private RuleProvider ruleProvider;

    @Autowired
    private MysqlCDCService mysqlCDCService;

    @Autowired
    private ClickHouseRemoteService clickHouseRemoteService;

    public boolean submitKafkaJob(ConnectorBean bean) {
        String connectorName = bean.getName();

        String ruleName = bean.getSourceType().toLowerCase() + "2" + bean.getSinkType().toLowerCase();
        String ruleExpr = ruleProvider.getRuleExpression(ruleName);
        PebbleExpRule rule = new PebbleExpRule().expression(ruleExpr);

        this.connector = new Connector()
                                .name(connectorName)
                                .sourceType(SourceType.valueOf(bean.getSourceType().toUpperCase()))
                                .sourceAccountConf(bean.getSourceAccountConf())
                                .sourceConf(bean.getSourceConf())
                                .sinkType(SinkType.valueOf(bean.getSinkType().toUpperCase()))
                                .sinkAccountConf(bean.getSinkAccountConf())
                                .sinkConf(bean.getSinkConf())
                                .rule(rule);

        JSONObject jobConf = new JSONObject();
        if(this.connector.configure()) {
            jobConf = this.connector.connectConf();
        }

        //Todo: Get current user's group from database
        String jobId = connectorName;
        return this.submitter.submit(jobId, ruleName, jobConf);
    }

    public boolean addMysqlCDC (ConnectorBean bean) {
        JSONObject params = this.buildMysqlCDCServiceParams(bean);
        JSONObject callback = this.mysqlCDCService.add(params);

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
        JSONObject callback = this.mysqlCDCService.update(params);
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
        JSONObject callback = this.mysqlCDCService.delete(bean.getId());
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
        JSONObject callback = this.clickHouseRemoteService.getClickHouseUser(username);
        JSONObject result = new JSONObject();

        if(callback != null) {
            int code = callback.getInteger("code");
            String message = callback.getString("msg");
            if (code == 0) {
                JSONObject data = result.getJSONObject("data");
                log.debug("Callback data: {}", data);
            } else {
                log.error("Error occurred while calling getClickHouseUser: {}", message);
                result = null;
            }
        }
        return result;
    }




    private JSONObject buildMysqlCDCServiceParams(ConnectorBean bean) {
        JSONObject params = new JSONObject();
        JSONObject accountConf = bean.getSourceAccountConf();
        JSONObject sourceConf = bean.getSourceConf();

        params.put("id", Long.valueOf(bean.getId()));
        params.put("dbPassword", accountConf.getString("password"));
        params.put("dbUser", accountConf.getString("username"));

        params.put("dbUrl", sourceConf.getString("url"));
        params.put("targerDb", sourceConf.getString("database"));

        JSONArray tables = sourceConf.getJSONArray("tables");
        params.put("table", tables);

        return params;
    }

}
