package sensecloud.connector;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sensecloud.connector.annotation.Enforcer;
import sensecloud.connector.rule.IEnforcer;
import sensecloud.connector.rule.IRule;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Component
@Data @Accessors(fluent = true)
public class Connector<R extends IRule> {

    private String name;
    private SourceType sourceType;
    private JSONObject sourceAccountConf;
    private JSONObject sourceConf;
    private SinkType sinkType;
    private JSONObject sinkAccountConf;
    private JSONObject sinkConf;

    private R rule;
    private JSONObject connectConf;

    private IEnforcer<R, JSONObject, String> enforcer;

    private void lookupEnforcer() {
        Enforcer annotation = this.rule.getClass().getAnnotation(Enforcer.class);
        Class<? extends IEnforcer> enforcerClass = annotation.value();
        try {
            enforcer = enforcerClass.getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public boolean configure() {
        if(rule == null || sourceConf == null || sinkConf == null) {
            log.warn("One of rule, sourceConf, sinkConf is null. Nothing to do.");
            return false;
        }

        this.lookupEnforcer();
        if (this.enforcer == null) {
            log.warn("Can't not find enforcer with rule: {}", this.rule);
            return false;
        }

        JSONObject data = new JSONObject();
        data.put("source", sourceConf);
        data.put("sourceAccount", sourceAccountConf);
        data.put("sink", sinkConf);
        data.put("sinkAccount", sinkAccountConf);

        String plan = this.enforcer.enforce(rule, data);
        log.info(">>> plan : {}", plan);
        this.connectConf = JSONObject.parseObject(plan);

        return this.connectConf != null;
    }


}
