package sensecloud.web.bean;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import sensecloud.web.config.db.JSONObjectHandler;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(autoResultMap = true)
public class ConnectorBean implements Serializable {

    private Long id;
    private String name;
    private String sourceName;
    private String sourceType;

    @TableField(typeHandler = JSONObjectHandler.class)
    private JSONObject sourceConf;
    private String sinkName;
    private String sinkType;

    @TableField(typeHandler = JSONObjectHandler.class)
    private JSONObject sinkConf;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private boolean deleted;
    private String deleteBy;
    private LocalDateTime deleteTime;

}
