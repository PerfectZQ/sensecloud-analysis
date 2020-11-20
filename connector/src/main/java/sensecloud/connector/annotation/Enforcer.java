package sensecloud.connector.annotation;


import sensecloud.connector.rule.IEnforcer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Enforcer {

    Class<? extends IEnforcer> value();

}
