package sensecloud.web.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TaskBean {

    private Long id;
    private String name;
    private List<Long> dependencyIds;
    private String type;
    private JSONObject conf;

}
