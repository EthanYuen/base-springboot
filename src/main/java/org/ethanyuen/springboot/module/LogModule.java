package org.ethanyuen.springboot.module;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.ethanyuen.springboot.annotation.SysLog;
import org.ethanyuen.springboot.aop.RequestAop;
import org.ethanyuen.springboot.bean.SystemLog;
import org.ethanyuen.springboot.bean.User;
import org.ethanyuen.springboot.bean.query.SystemLogQueryParam;
import org.ethanyuen.springboot.enums.BusinessType;
import org.ethanyuen.springboot.utilbean.BaseCondition;
import org.ethanyuen.springboot.utilbean.Params;
import org.ethanyuen.springboot.utilbean.Result;
import org.ethanyuen.springboot.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("log")
@RestController
@Ok("json:full")
@Fail("json:full")
@Api(tags ="日志管理")
public class LogModule extends BaseModule<SystemLog> {
    @Value("${qywx-hook}")
    static String wxHook;
    static   Map pushedLog = new HashMap();
    static List exceptEx = new ArrayList();
    static {
        exceptEx.add("远程主机强迫关闭了一个现有的连接。");
        exceptEx.add("你的主机中的软件中止了一个已建立的连接。");
        exceptEx.add("An existing connection was forcibly closed by the remote host");
    }
    @Autowired
    CompanyModule companyModule;
    @RequestMapping
    @SaCheckPermission("log:get")
    @ApiOperation(value = "获取日志列表")
    public Result<SystemLog> getlogs(BaseCondition baseCondition, SystemLogQueryParam baseEntity){
        SqlExpressionGroup sql =Cnd.exps(SystemLog.Fields.companyId, "in",companyModule.getUserCompanies());
        return getEntities(baseEntity,baseCondition,sql);
    }
    @RequestMapping
    @SaCheckPermission("log:deleteLogs")
    @SysLog(type = BusinessType.DELETE)
    @ApiOperation("删除日志")
    public Result deleteLogs(String ids){
        return deleteEntities(ids);
    }
    @SneakyThrows
    public static void insertExceptionLog(Throwable ex, Method method) {
        if (ex instanceof UndeclaredThrowableException) {
            ex=((UndeclaredThrowableException)ex).getUndeclaredThrowable();
        }
        if (exceptEx.contains(ex.getMessage())) {
            return;
        }
        log.error(null,ex);
        Throwable cause = ex.getCause();
        StackTraceElement stackTraceElement;
        if (cause!=null) {
            stackTraceElement =cause.getStackTrace()[0];
        }else{
            stackTraceElement=ex.getStackTrace()[0];
        }
        User user= RequestAop.getLogUser();
        if (method==null) {
            Class clazz = Class.forName(stackTraceElement.getClassName());
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                if (stackTraceElement.getMethodName().equals(declaredMethod.getName())) {
                    method =declaredMethod;
                    break;
                }
            }
        }
        ApiOperation annotation=method.getAnnotation(ApiOperation.class);
        SystemLog systemLog = new SystemLog(annotation != null ? annotation.value() : "", BusinessType.ERROR,user==null?0:user.getCompanyId());
        systemLog.setSource(stackTraceElement.getClassName()+ "#" + stackTraceElement.getMethodName() + "#Line" + stackTraceElement.getLineNumber());
        systemLog.setInfo(ExceptionUtil.stacktraceToString(ex));
        Utils.getIocBean(Dao.class).insert(systemLog);
        if (!pushedLog.containsKey(systemLog.getSource())) {
            systemLog.setAuthor(Params.HOST_NAME+systemLog.getAuthor());
            systemLog.setInfo(ex.toString());
            notifyWxWork(JSONUtil.toJsonStr(systemLog));
            pushedLog.put(systemLog.getSource(), 1);
        }
    }
    public static void notifyWxWork(String msg) {
        notifyWxWork(msg, wxHook);
    }
    public static void notifyWxWork(String msg,String url) {
        ThreadUtil.execute(()->{
            try {
                JSONObject json = new JSONObject();
                json.set("msgtype", "text");
                JSONObject content = new JSONObject();
                content.set("content",msg );
                json.set("text", content);
                HttpUtil.post(url, json.toString(),Params.HTTP_TIMEOUT);
            }catch(Throwable e){
                log.error(null,e);
            }
        });

    }
    public static void info(String msg) {
        info(msg,true);
    }
    public static void info(String msg,boolean notifyWx) {
        log.warn(msg);
        if (notifyWx&&!pushedLog.containsKey(msg)) {
            notifyWxWork(msg);
            pushedLog.put(msg, 1);
        }
    }
}
