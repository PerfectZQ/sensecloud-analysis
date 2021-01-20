package sensecloud.flow;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class Task {

    private String name;
    private String taskId;
    private List<String> dependencyIds;
    private String type;
    private JSONObject conf;
    private String content;

}
