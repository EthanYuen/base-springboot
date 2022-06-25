package org.ethanyuen.springboot.aop;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import org.ethanyuen.springboot.annotation.SysLog;
import org.ethanyuen.springboot.bean.Param;
import org.ethanyuen.springboot.bean.SystemLog;
import org.ethanyuen.springboot.bean.User;
import org.ethanyuen.springboot.utilbean.EntityDataException;
import org.ethanyuen.springboot.utils.Utils;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.nutz.dao.Dao;
import org.nutz.plugins.validation.Errors;
import org.nutz.plugins.validation.annotation.AnnotationValidation;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Component
@Aspect
public class RequestAop {
    private static AnnotationValidation av = new AnnotationValidation();
    static Dao dao;

    @Before("execution(* com.example.springboot.module..*.*(..))")
    public void before(JoinPoint point) {
        Method method = getMethod(point);
        checkParamsError(point,method);
        insertLog(point,method);
    }

    public void insertLog(JoinPoint point,Method method) {
        ApiOperation permissionAnnotation = method.getAnnotation(ApiOperation.class);
        SysLog sysLogAnnotation = method.getAnnotation(SysLog.class);
        User user=getLogUser();
        if (permissionAnnotation != null&&sysLogAnnotation!= null) {
            SystemLog systemLog = new SystemLog(permissionAnnotation.value(),sysLogAnnotation.type(),user == null ? 0 : user.getCompanyId());
            StringBuilder stringBuilder = new StringBuilder();
            for (Object arg : point.getArgs()) {
                if (arg!=null) {
                    stringBuilder.append(arg.getClass().getSimpleName()).append(":").append(JSONUtil.toJsonStr(arg)).append(";");
                }
                if (arg instanceof Errors) {
                    if(((Errors) arg).hasError()){
                        throw new EntityDataException(((Errors) arg).getErrorsList().toString());
                    }
                }
            }
            systemLog.setInfo(stringBuilder.toString());
            systemLog.setSource(method.getDeclaringClass().getSimpleName() + "#" + method.getName());
            Utils.getIocBean(Dao.class).insert(systemLog);
        }
    }

    public static User getLogUser() {
        if (dao==null) {
            dao=Utils.getIocBean(Dao.class);
        }
        SaSession session=null;
        try {
            session= StpUtil.getSession();
        }catch (Throwable e){

        }
        User user=null;
        if(session!=null&&StpUtil.isLogin()){
            user=dao.fetch(User.class, StpUtil.getLoginIdAsLong());
//            user= (User) StpUtil.getSession().get(Params.LOGIN_USER);
        }
        return user;
    }

    public void checkParamsError(JoinPoint point,Method method) {
        Errors es = new Errors();
        for (Object obj : point.getArgs()) {
            if (!(obj instanceof Param)) {
                continue;
            }
            if (obj instanceof Object[]) {
                Object[] objects = (Object[]) obj;
                for (Object object : objects) {
                    av.validate(object, es);
                }
            } else if (obj instanceof List) {
                List objects = (List) obj;
                for (Object object : objects) {
                    av.validate(object, es);
                }
            } else {
                av.validate(obj, es);
            }
        }
        if (es.hasError()) {
            throw new EntityDataException(es.getErrorsList().toString());
        }
    }
    @SneakyThrows
    public static Method getMethod(JoinPoint point) {
        Signature sig = point.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        return target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
    }
}
