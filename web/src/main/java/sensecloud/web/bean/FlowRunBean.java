package sensecloud.web.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlowRunBean {

    private Long id;
    private String runId;
    private String dagId;
    private String state;
    private LocalDateTime executionDate;
    private Boolean externalTrigger;
    private JSONObject conf;

}
