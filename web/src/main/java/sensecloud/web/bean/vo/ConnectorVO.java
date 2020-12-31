package sensecloud.web.bean.vo;

import lombok.Data;
import sensecloud.web.bean.ConnectorBean;
import sensecloud.web.entity.ConnectorAttachmentEntity;
import sensecloud.web.entity.ConnectorEntity;

import java.util.ArrayList;
import java.util.List;

@Data
public class ConnectorVO extends ConnectorEntity {

    public List<ConnectorAttachmentEntity> attachments = new ArrayList<>();

}
