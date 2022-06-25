package org.ethanyuen.springboot.module;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.ethanyuen.springboot.annotation.SysLog;
import org.ethanyuen.springboot.aop.RequestAop;
import org.ethanyuen.springboot.bean.BaseNameEntity;
import org.ethanyuen.springboot.bean.SystemLog;
import org.ethanyuen.springboot.bean.User;
import org.ethanyuen.springboot.bean.form.UserFormParam;
import org.ethanyuen.springboot.bean.query.UserQueryParam;
import org.ethanyuen.springboot.enums.BusinessType;
import org.ethanyuen.springboot.utilbean.BaseCondition;
import org.ethanyuen.springboot.utilbean.Result;
import org.ethanyuen.springboot.utils.SQLUtil;
import org.ethanyuen.springboot.utils.Utils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("user")
@RestController
@Api(tags ="用户管理")
public class UserModule extends BaseModule<User> {

    @Autowired
    Dao dao;
    @Autowired
    AuthModule authModule;
    @Autowired
    CompanyModule companyModule;

    @RequestMapping
    @SaCheckLogin
    @SysLog(type = BusinessType.SET)
    @ApiOperation("用户修改密码")
    public Result updatePwd(String newPwd, String oldPwd) {
        User user= RequestAop.getLogUser();
        Result re = new Result();
        byte[] key = user.getSalt().getBytes();
        HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
        if (!mac.digestHex(oldPwd).equals(user.getPassword())) {
            return re.setBad("密码验证失败");
        }
        updatePassword(newPwd, user);
        return re.setOk();
    }

    @RequestMapping
    @ApiOperation(value = "忘记密码")
    public Result forgetPwd(String password, String account,String captcha) {
        Result re = new Result();
        if (StrUtil.isNotBlank(captcha)&&captcha.equals( authModule.captchaMap.get(account))) {
            User user = addNewUser(account);
            updatePassword(password, user);
            return  re.setOk();
        }else{
            return  re.setBad("验证码错误");
        }
    }

    @SneakyThrows
    public User addNewUser(String account) {
        User user = dao.fetch(User.class, SQLUtil.baseCnd().and(BaseNameEntity.Fields.name, "=", account));
        if (user==null) {
            UserFormParam userFormParam = new UserFormParam();
            userFormParam.setName(account);
            Result result = setUser(userFormParam);
            user = Utils.transferPojo(User.class, userFormParam);
            user.setId((Long) result.getObj());
        }
        return user;
    }

    public void updatePassword(String password, User user) {
        user.setSalt(IdUtil.simpleUUID());
        byte[] key = user.getSalt().getBytes();
        HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
        user.setPassword(mac.digestHex(password));
        dao.update(user, "^(password|salt)$");
    }

    @SneakyThrows
    @RequestMapping
    @SaCheckPermission("user:set")
    @SysLog(type = BusinessType.SET)
    @ApiOperation("设置用户")
    public Result setUser(UserFormParam userForm) {
        User user = Utils.transferPojo(User.class,userForm);
        if (user.getId()==null) {
            user.setSalt(IdUtil.simpleUUID());
            byte[] key = user.getSalt().getBytes();
            HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
            user.setPassword(mac.digestHex("123456"));
        }
        return setEntity(user);
    }


    @RequestMapping
    @SaCheckPermission("user:delete")
    @SysLog(type = BusinessType.DELETE)
    @ApiOperation("删除用户")
    public Result deleteUser(String ids) {
        return deleteEntities(ids);
    }

    @RequestMapping
    @SaCheckPermission("user:get")
    @ApiOperation(value = "获取用户")
    public Result<User> getUsers(UserQueryParam condition, BaseCondition baseCondition) {
        SqlExpressionGroup sql =Cnd.exps(SystemLog.Fields.companyId, "in",companyModule.getUserCompanies());
        return getEntities(condition,baseCondition,sql);
    }


    @RequestMapping
    @SaCheckPermission("user:resetPwd")
    @SysLog(type = BusinessType.SET)
    @ApiOperation(value = "重置账号密码")
    public Result resetPwd(Long userId) {
        Result re = new Result();
        User user = dao.fetch(User.class, userId);
        if (user == null) {
            return re.setBad("该账号不存在");
        }
        updatePassword("123456", user);
        return re.setOk("操作成功，重置密码为123456");
    }

    @RequestMapping
    @SaCheckPermission("user:lockUser")
    @SysLog(type = BusinessType.SET)
    @ApiOperation(value = "锁定账号")
    public Result lockUser(Long id,Boolean locked) {
        Result re = new Result();
        User user = new User();
        user.setId(id);
        user.setLocked(locked);
        dao.update(user, User.Fields.locked);
        StpUtil.kickout(id);
        return re.setOk();
    }
    public User getByNameAndPwd(String username, String password) {
        if (StrUtil.isBlank(username)||StrUtil.isBlank(password)) {
            return null;
        }
        User user = dao.fetch(User.class, Cnd.where(BaseNameEntity.Fields.name, "=", username));
        if (user == null) {
            return null;
        }
        byte[] key = user.getSalt().getBytes();
        HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
        String _pass = mac.digestHex(password);
        if (_pass.equalsIgnoreCase(user.getPassword())) {
            return user;
        }
        return null;
    }
}