package sensecloud.flow;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Task {

    private String name;
    private String taskId;
    private List<String> dependencyIds = new ArrayList<String>();
    private String type;
    private JSONObject conf;
    private String content;

}
