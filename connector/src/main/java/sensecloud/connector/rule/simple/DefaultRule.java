package sensecloud.connector.rule.simple;

import lombok.Data;
import lombok.experimental.Accessors;
import sensecloud.connector.rule.IRule;
import sensecloud.connector.rule.ExpressionType;


/**
 * A simple way to map source -> sink configuration
 * by using json path to read properties,
 * then writing them in a node sequence.
 * NOTE: the node sequence is ordered from root to current node.
 */
//@Data
//@Accessors(fluent = true)
//public class DefaultRule implements IRule<String, String[]> {
//    private ExpressionType type;
//    private String from;
//    private String[] to;
//}
