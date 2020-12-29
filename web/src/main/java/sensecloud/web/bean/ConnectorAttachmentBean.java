package sensecloud.web.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConnectorAttachmentBean {

    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    private String connectorId;
    private String catalog;
    private String backupPath;
    private LocalDateTime backupTime;

}
