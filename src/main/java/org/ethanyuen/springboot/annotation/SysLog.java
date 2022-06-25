package org.ethanyuen.springboot.annotation;

import org.ethanyuen.springboot.enums.BusinessType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface SysLog {
    BusinessType type() ;
}