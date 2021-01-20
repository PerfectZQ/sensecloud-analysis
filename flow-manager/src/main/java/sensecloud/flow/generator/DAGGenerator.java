package sensecloud.flow.generator;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sensecloud.flow.Flow;

import java.util.Map;

@Slf4j
@Component
public class DAGGenerator {

    @Autowired
    private DAGRenderer renderer;

    @Value("${service.flow.tpl.name}")
    private String tplName;

    /**
     * Generate an Airflow DAG code
     * @param flow
     * @return Airflow DAG code
     */
    public String generate(Flow flow, Map<String, Object> env) {
        String tpl = DAGDefinitionProvider.getTemplate(tplName);
        String code = "";
        if(StringUtils.isNotBlank(tpl)) {
            JSONObject context = new JSONObject();
            context.put("flow", flow);
            context.put("env", env);
            return renderer.render(tpl, context);
        } else {
            log.error("Template name in flow is empty!");
        }
        return code;
    }

}
