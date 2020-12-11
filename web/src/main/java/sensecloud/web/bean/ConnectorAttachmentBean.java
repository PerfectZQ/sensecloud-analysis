package sensecloud.web.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConnectorAttachmentBean {

    private String id;
    private String connectorId;
    private String catalog;
    private String backupPath;
    private LocalDateTime backupTime;

}
