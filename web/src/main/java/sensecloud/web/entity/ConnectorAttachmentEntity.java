package sensecloud.web.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import sensecloud.web.bean.ConnectorAttachmentBean;

import java.time.LocalDateTime;

@Data
@TableName(value = "connector_attachment", autoResultMap = true)
public class ConnectorAttachmentEntity extends ConnectorAttachmentBean {

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private byte[] content;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private boolean deleted;
    private String deleteBy;
    private LocalDateTime deleteTime;

}
