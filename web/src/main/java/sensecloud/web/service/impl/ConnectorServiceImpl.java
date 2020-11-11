package sensecloud.web.service.impl;

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

@Slf4j
@Service
public class ConnectorServiceImpl extends ServiceImpl<ConnectorMapper, ConnectorEntity> implements IConnectorService {

    private Connector connector;

    @Autowired
    private AirflowSubmitter submitter;

    @Autowired
    private RuleProvider ruleProvider;

    public boolean submit(ConnectorBean bean) {
        String connectorName = bean.getSourceName() + " -> " + bean.getSinkName();

        String name = bean.getSourceType().toLowerCase() + "2" + bean.getSinkType().toLowerCase();
        String ruleExpr = ruleProvider.getRuleExpression(name);
        PebbleExpRule rule = new PebbleExpRule().expression(ruleExpr);

        this.connector = new Connector()
                                .name(connectorName)
                                .sourceType(SourceType.valueOf(bean.getSourceType().toUpperCase()))
                                .sourceConf(bean.getSourceConf())
                                .sinkType(SinkType.valueOf(bean.getSinkType().toUpperCase()))
                                .sinkConf(bean.getSinkConf())
                                .rule(rule);

        JSONObject jobConf = new JSONObject();
        if(this.connector.configure()) {
            jobConf = this.connector.connectConf();
        }

        String jobId = connectorName + "" + bean.getId();
        return this.submitter.submit(jobId, name, jobConf);
    }



}
