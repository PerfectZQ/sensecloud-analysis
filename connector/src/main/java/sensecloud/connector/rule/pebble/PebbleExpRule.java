package sensecloud.connector.rule.pebble;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;
import sensecloud.connector.annotation.Enforcer;
import sensecloud.connector.rule.ExpressionType;
import sensecloud.connector.rule.IRule;

@Enforcer(PebbleExpRuleEnforcer.class)
@Data @Accessors(fluent = true)
public class PebbleExpRule implements IRule {

    private String ruleName;
    private String expression;
    private ExpressionType type = ExpressionType.PebbleTemplate;

}
