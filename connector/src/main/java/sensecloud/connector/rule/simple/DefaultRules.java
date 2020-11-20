package sensecloud.connector.rule.simple;

import lombok.extern.slf4j.Slf4j;
import sensecloud.connector.rule.ExpressionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class DefaultRules {

//    private List<DefaultRule> rules = new ArrayList<>();
//
//    public DefaultRules add(DefaultRule ... rules) {
//        this.rules.addAll(Arrays.asList(rules));
//        return this;
//    }
//
//    /**
//     * Accepted expression format: json_path -> node_sequences
//     * Node_sequences format: node1::node2[]::node3
//     * eg. $['store']['category'][0]['book'] -> node1::node2[]::node3
//     * @param expressions expressions to be parsed to
//     * @return DefaultRules Object
//     */
//    public DefaultRules add(ExpressionType type, String ... expressions) {
//        for(String exp : expressions) {
//            if (exp.contains("->")) {
//                String[] parts = exp.split("->");
//                if(parts.length > 1) {
//                    DefaultRule rule = new DefaultRule();
//                    String jsonPath = parts[0];
//                    String[] nodeSequences = parts[1].split("::");
//
//                    rule.from(jsonPath).to(nodeSequences).type(type);
//
//                    this.rules.add(rule);
//                } else {
//                    log.warn("Invalid expression: {}", exp);
//                }
//            }
//        }
//        return this;
//    }
//
//    public List<DefaultRule> rules() {
//        return this.rules;
//    }

}
