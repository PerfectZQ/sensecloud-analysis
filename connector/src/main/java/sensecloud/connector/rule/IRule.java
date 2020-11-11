package sensecloud.connector.rule;

public interface IRule {

    ExpressionType type();
    String expression();

    IRule expression(String expression);

}
