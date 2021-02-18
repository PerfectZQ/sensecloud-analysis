package sensecloud.connector.dag;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConnectorCodeGenerator {

    public static String generateCode(String appName, String type, JSONObject runner, JSONObject conf, Map<String, String> env) {
        JSONObject context = new JSONObject();
        context.put("appName", appName);
        context.put("runner", runner);
        context.put("env", env);
        context.put("config", conf);
        return DAGTemplateProvider.dag(type, context);
    }

}
