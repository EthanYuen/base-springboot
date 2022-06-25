package org.ethanyuen.springboot.aop;

import org.ethanyuen.springboot.module.LogModule;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class JobErrorAop {
   @Around("execution(* org.ethanyuen.springboot.quartz..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            LogModule.insertExceptionLog(e,RequestAop.getMethod(joinPoint));
            return null;
        }
    }
}
