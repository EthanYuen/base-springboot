package org.ethanyuen.springboot.module;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.ethanyuen.springboot.aop.RequestAop;
import org.ethanyuen.springboot.bean.SystemLog;
import org.ethanyuen.springboot.bean.User;
import org.ethanyuen.springboot.enums.BusinessType;
import org.ethanyuen.springboot.service.TxSMSService;
import org.ethanyuen.springboot.utilbean.Params;
import org.ethanyuen.springboot.utilbean.Result;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiSort(1)
@RestController
@RequestMapping("auth")
@Api(tags = "授权管理")
public class AuthModule {

    Map captchaMap= new ConcurrentHashMap<String,String>();

    @Autowired
    Dao dao;

    @Autowired
    UserModule userModule;
    /**
     * 登陆
     *
     * @param account 用户名
     * @param password 密码
     * @param code     登录密钥
     * @param captcha  验证码
     * @return
     */
    @SneakyThrows
    @RequestMapping
    @ApiOperation(value = "登陆")
    public Result login(String account, String password, String code, String captcha, @ApiIgnore  HttpServletRequest req) {
        Result re = new Result();
        re.setStatus(-1);
        //根据用户名密码匹配
        User user = userModule.getByNameAndPwd(account, password);
        if (user!=null) {
            return setSession(re,user,req);
        }
        //根据验证码匹配
        if (StrUtil.isNotBlank(captcha)&&captcha.equals( captchaMap.get(account))) {
            user = userModule.addNewUser(account);
            return setSession(re,user,req);
        }

        //根据openid匹配
        if (!StrUtil.isBlank(code)) {
            String openId = getWXOpenId(code);
            user = dao.fetch(User.class, Cnd.where(User.Fields.wxOpenId, "=", openId));
            if (user!=null) {
                User prewxUser = dao.fetch(User.class, Cnd.where(User.Fields.wxOpenId, "=", openId));
                if (prewxUser != null&&!prewxUser.getName().equals(user.getName() )) {
                    prewxUser.setWxOpenId(null);
                    dao.update(prewxUser, User.Fields.wxOpenId);
                }
                user.setWxOpenId(openId);
                dao.update(user, User.Fields.wxOpenId);
                return setSession(re,user,req);
            }
        }
        return re.setBad("信息错误");
    }


    @RequestMapping
    @ApiOperation(value = "退出登陆")
    public Result logout(@ApiIgnore HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent").toLowerCase();
        if(userAgent.indexOf("micromessenger")!= -1){
            //微信
            User user = RequestAop.getLogUser();
            user.setWxOpenId("");
            dao.update(user, User.Fields.wxOpenId);
        }
        StpUtil.logout();
        return Result.setOk("");
    }


    @SneakyThrows
    @RequestMapping
    @ApiOperation(value = "发送验证码")
    public Result sendMsg(String phone) {
        Result result = new Result();
        String randomCode = RandomUtil.randomNumbers(6);
        String[] param = {randomCode};
        TxSMSService.SendSingleMessage(param, phone);
        captchaMap.put(phone, randomCode);
        return result.setOk();
    }


    private Result setSession( Result re, User user, HttpServletRequest request) {
        if (user.getLocked()!=null&&user.getLocked()) {
            return re.setBad("该账户被锁定，请联系管理员");
        }
        StpUtil.login(user.getId());
        StpUtil.getSession().set(Params.LOGIN_USER, user);
        user.setPassword(null);
        user.setWxOpenId(null);
        user.setSalt(null);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token",StpUtil.getTokenValue());
        map.put("user", user);
        re.setObj(map);
        SystemLog systemLog = new SystemLog("登录", BusinessType.LOGIN, user.getCompanyId());
        systemLog.setSource("AuthModule#login");
        systemLog.setInfo("登录IP:" + Lang.getIP(request));
        dao.insert(systemLog);
        return re.setOk();
    }
    @Value("${wx-appId}")
    private String appId;
    @Value("${wx-secret}")
    private String secret ;

    /**
     * 通过js_code获取openId
     *
     * @param code
     * @return
     */
    public String getWXOpenId(String code) {
        String jsonResult = HttpUtil.get("https://api.weixin.qq.com/sns/jscode2session?appId=" + appId + "&secret="
                + secret + "&js_code=" + code + "&grant_type=authorization_code");
        return JSONUtil.parseObj(jsonResult).getStr("openid");
    }





}
