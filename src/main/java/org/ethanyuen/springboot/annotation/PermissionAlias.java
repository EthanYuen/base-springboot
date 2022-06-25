package org.ethanyuen.springboot.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PermissionAlias {
	String value() default "";
}