package io.github.anno.autoconfigure;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GrantAccessAction {
    Condition condition() default Condition.ALWAYS;
    String resourceId();
    String actorId();
    String accessLevel();
}
