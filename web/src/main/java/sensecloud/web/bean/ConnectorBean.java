package sensecloud.web.bean;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import sensecloud.web.config.db.JSONObjectHandler;

import java.time.LocalDateTime;

@Data
public class ConnectorBean {

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
