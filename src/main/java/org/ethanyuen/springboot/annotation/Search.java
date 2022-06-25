package org.ethanyuen.springboot.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Search {
    boolean key() default false;
}
