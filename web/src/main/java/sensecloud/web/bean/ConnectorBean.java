package sensecloud.web.bean;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import sensecloud.web.config.db.JSONObjectHandler;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ConnectorBean implements Serializable {

    private String id;
    private String name;
    private String sourceName;
    private String sourceType;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject sourceConf;
    @TableField(value = "source_account_conf", typeHandler = FastjsonTypeHandler.class)
    private JSONObject sourceAccountConf;

    private String sinkName;
    private String sinkType;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject sinkConf;
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject sinkAccountConf;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private boolean deleted;
    private String deleteBy;
    private LocalDateTime deleteTime;

}
