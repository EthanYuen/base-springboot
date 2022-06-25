package org.ethanyuen.springboot.mvc;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import org.ethanyuen.springboot.module.LogModule;
import org.ethanyuen.springboot.utilbean.EntityDataException;
import org.ethanyuen.springboot.utilbean.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value=Throwable.class)
    Object handleException(Throwable ex){
        return processEx(ex);
    }

    public static Result processEx(Throwable ex) {
        Result result=new Result();
        result.setBad();
        if (ex instanceof NotLoginException) {
            result.setStatus(-9);
            result.setInfo("登录信息已失效,请刷新页面");
        }else if (ex instanceof SaTokenException) {
            result.setInfo("您没有权限访问，请联系管理员");
        }else if (ex instanceof EntityDataException) {
            result.setInfo(ex.getMessage());
        }else if (ex instanceof BindException) {
            BindingResult bindingResult = ((BindException)ex).getBindingResult();
            StringBuilder stringBuilder = new StringBuilder();
            if (bindingResult.hasErrors()) {
                List<ObjectError> errors = bindingResult.getAllErrors();
                for (ObjectError oe : errors) {
                    stringBuilder.append(oe.getDefaultMessage()).append(";");
                }
            }
            result.setInfo(stringBuilder.toString());
        }else {
            LogModule.insertExceptionLog(ex,null);
        }
        return result;
    }

}
