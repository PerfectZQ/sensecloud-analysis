package sensecloud.connector.rule;

public interface IRule {

    ExpressionType type();
    String expression();
    String ruleName();

    IRule expression(String expression);

}
