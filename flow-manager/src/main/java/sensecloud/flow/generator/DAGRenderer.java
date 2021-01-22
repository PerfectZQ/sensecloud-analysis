package sensecloud.flow.generator;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import sensecloud.flow.utils.TextRenderer;

@Component
public class DAGRenderer {

    private TextRenderer renderer = new TextRenderer();

    public String render(String tpl, JSONObject context) {
        return renderer.renderStringTemplate(tpl, context);
    }

}
