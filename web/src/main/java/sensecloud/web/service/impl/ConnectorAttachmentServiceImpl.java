package sensecloud.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sensecloud.connector.Connector;
import sensecloud.connector.SinkType;
import sensecloud.connector.SourceType;
import sensecloud.connector.rule.RuleProvider;
import sensecloud.connector.rule.pebble.PebbleExpRule;
import sensecloud.connector.submitter.airflow.AirflowSubmitter;
import sensecloud.web.bean.ConnectorBean;
import sensecloud.web.entity.ConnectorAttachmentEntity;
import sensecloud.web.entity.ConnectorEntity;
import sensecloud.web.mapper.ConnectorAttachmentMapper;
import sensecloud.web.mapper.ConnectorMapper;
import sensecloud.web.service.IConnectorService;

@Slf4j
@Service
public class ConnectorAttachmentServiceImpl extends ServiceImpl<ConnectorAttachmentMapper, ConnectorAttachmentEntity> {



}
