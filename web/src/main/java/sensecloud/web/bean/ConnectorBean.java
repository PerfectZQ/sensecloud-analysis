package sensecloud.web.bean;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import sensecloud.connector.Connector;
import sensecloud.connector.SinkType;
import sensecloud.connector.SourceType;
import sensecloud.connector.rule.pebble.PebbleExpRule;
import sensecloud.web.config.db.JSONObjectHandler;
import sensecloud.web.utils.DesUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ConnectorBean implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private String id;
    private String name;
    private String saas;
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
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String updateBy;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private boolean deleted;
    private String deleteBy;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteTime;


}
