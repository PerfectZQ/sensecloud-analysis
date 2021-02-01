package sensecloud.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sensecloud.web.entity.ConnectorAttachmentEntity;
import sensecloud.web.mapper.ConnectorAttachmentMapper;

@Slf4j
@Service
public class ConnectorAttachmentServiceImpl extends ServiceImpl<ConnectorAttachmentMapper, ConnectorAttachmentEntity> {

    public boolean deleteAll(String connectorId) {
        return this.getBaseMapper().deleteAll(connectorId);
    }

}
