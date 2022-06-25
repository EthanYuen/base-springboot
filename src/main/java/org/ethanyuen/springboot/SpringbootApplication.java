package org.ethanyuen.springboot;

import cn.hutool.core.collection.ListUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ethanyuen.springboot.bean.BaseNameEntity;
import org.ethanyuen.springboot.bean.SystemLog;
import org.ethanyuen.springboot.bean.User;
import org.ethanyuen.springboot.bean.form.UserFormParam;
import org.ethanyuen.springboot.enums.BusinessType;
import org.ethanyuen.springboot.module.LogModule;
import org.ethanyuen.springboot.module.RoleModule;
import org.ethanyuen.springboot.module.UserModule;
import org.ethanyuen.springboot.utilbean.Params;
import org.ethanyuen.springboot.utils.Utils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.interceptor.DaoLogInterceptor;
import org.nutz.plugins.cache.dao.CachedNutDaoExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class SpringbootApplication {
    @SneakyThrows
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t,e)-> LogModule.insertExceptionLog(e,null));
        ConfigurableApplicationContext application = SpringApplication.run(SpringbootApplication.class, args);
        Environment env = application.getEnvironment();
        Params.BASE_PACKAGE = env.getProperty("base-package");
        init();
        log.info("运行成功! 接口文档:");
        log.info("http://localhost:{}{}doc.html",env.getProperty("server.port"), env.getProperty("server.servlet.context-path", ""));
    }

    public static void init() {
        NutDao dao = (NutDao) Utils.getIocBean(Dao.class);
        dao.setInterceptors(ListUtil.toList(Utils.getIocBean(DaoInterceptor.class), new DaoLogInterceptor()));
        CachedNutDaoExecutor.DEBUG = true;
        if (dao.count(SystemLog.class)==0) {
            SystemLog systemLog = new SystemLog(Params.OP_INIT, BusinessType.OTHER,0L);
            dao.insert(systemLog);
        }
        initBaseAuth( dao);

    }

    @SneakyThrows
    public static void initBaseAuth(Dao dao) {
        if (dao.count(User.class) == 0) {
            UserModule userModule = Utils.getIocBean(UserModule.class);
            User user=new User();
            user.setName("admin");
            user.setPassword("123456");
            user.setCompanyId(0L);
            userModule.setUser(Utils.transferPojo(UserFormParam.class,user));
        }
        RoleModule roleModule = Utils.getIocBean(RoleModule.class);
        roleModule.initFormPackage(Params.BASE_PACKAGE +".module");
        roleModule.checkBasicRoles(dao.fetch(User.class, Cnd.where(BaseNameEntity.Fields.name,"=","admin")));
    }


}
