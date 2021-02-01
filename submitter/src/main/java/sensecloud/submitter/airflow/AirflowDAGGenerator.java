package sensecloud.submitter.airflow;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AirflowDAGGenerator {

    @Autowired
    private AirflowDAGProvider airflowDAGProvider;

    public String generateDAG(String appName, String type, JSONObject conf, Map<String, String> env) {
        JSONObject context = new JSONObject();
        context.put("appName", appName);
        context.put("env", env);
        context.put("config", conf);
        return this.airflowDAGProvider.dag(type, context);
    }

}
