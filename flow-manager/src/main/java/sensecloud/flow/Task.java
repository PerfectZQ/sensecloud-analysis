package sensecloud.flow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class Task {

    private String name;
    private String taskId;
    private JSONArray dependencyIds;
    private String type;
    private JSONObject conf;
    private String content;

}
