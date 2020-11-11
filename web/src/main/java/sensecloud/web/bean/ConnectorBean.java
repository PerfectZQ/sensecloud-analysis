package sensecloud.web.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConnectorBean {

    private Long id;
    private String name;
    private String sourceName;
    private String sourceType;
    private JSONObject sourceConf;
    private String sinkName;
    private String sinkType;
    private JSONObject sinkConf;

    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private boolean deleted;
    private String deleteBy;
    private LocalDateTime deleteTime;

}
