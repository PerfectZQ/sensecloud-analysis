package sensecloud.connector.rule.pebble;

import com.alibaba.fastjson.JSONObject;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.apache.commons.io.FileUtils;
import sensecloud.connector.rule.IEnforcer;
import sensecloud.connector.utils.TextRenderer;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

public class PebbleExpRuleEnforcer implements IEnforcer<PebbleExpRule, JSONObject, String> {

    private TextRenderer renderer;

    public PebbleExpRuleEnforcer() {
        this.renderer = new TextRenderer();
    }

    private String render(String tpl, JSONObject context) {
        return this.renderer.renderStringTemplate(tpl, context);
    }

    @Override
    public String enforce(PebbleExpRule rule, JSONObject data) {
        String tpl = rule.expression();
        String result = this.render(tpl, data);
        return result;
    }


    public static void main(String[] args) {
        URL url = PebbleExpRuleEnforcer.class.getClassLoader().getResource("connector");
        try {
            String json = FileUtils.readFileToString(new File(url.getFile() + "/sample/source-kafka.json"), "utf-8");
            String expr =  FileUtils.readFileToString(new File(url.getFile() + "/rule/kafka2clickhouse.rule"), "utf-8");
            PebbleExpRule rule = new PebbleExpRule();
            rule.expression(expr);

            PebbleExpRuleEnforcer enforcer = new PebbleExpRuleEnforcer();

            JSONObject data = JSONObject.parseObject(json);
            String result = enforcer.enforce(rule, data);

            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
