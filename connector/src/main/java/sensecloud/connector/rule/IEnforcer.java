package sensecloud.connector.rule;

public interface IEnforcer<R extends IRule, D, V> {

    V enforce(R rule, D data);

}
