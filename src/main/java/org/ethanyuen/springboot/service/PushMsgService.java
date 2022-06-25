package org.ethanyuen.springboot.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.ethanyuen.springboot.module.LogModule;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.Constants;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * app推送服务
 */
public class PushMsgService {
    @Value("${uni-appId}")
    private static String appId ;
    @Value("${uni-appKey}")
    private static String appKey ;
    @Value("${uni-masterSecret}")
    private static String masterSecret ;
    @Value("${wx-appId}")
    String wxAppId;
    @Value("${wx-secret}")
    String wxSecret;
    @Value("${wx-templateId}")
    String wxTemplateId;
    // 如果需要使用HTTPS，直接修改url即可
    // private static String url = "https://api.getui.com/apiex.htm";
    static String host = "https://api.getui.com/apiex.htm";

    /**
     * uniapp推送消息
     *
     * @param cid     用户登录clientid
     * @param title   标题
     * @param content 内容
     */
    public void pushMessage(String cid, String title, String content) {
        // 设置后，根据别名推送，会返回每个cid的推送结果
        System.setProperty(Constants.GEXIN_PUSH_SINGLE_ALIAS_DETAIL, "true");
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        NotificationTemplate template = getNotificationTemplate(title, content);
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间，单位为毫秒
        message.setOfflineExpireTime(24 * 3600 * 1000);
        message.setData(template);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        // 厂商通道下发策略
        message.setStrategyJson("{\"default\":1,\"ios\":1,\"st\":1}");
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);
        // target.setAlias(Alias);
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret == null) {
            LogModule.info("个推服务器响应异常");
        }
    }

    public static NotificationTemplate getNotificationTemplate(String title, String content) {
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(content);
        // 配置通知栏图标
        style.setLogo("filepath.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
//        style.setChannel("通知渠道id");
//        style.setChannelName("通知渠道名称");
        style.setChannelLevel(3); // 设置通知渠道重要性
        template.setStyle(style);

        template.setTransmissionType(1); // 透传消息接受方式设置，1：立即启动APP，2：客户端收到消息后需要自行处理
        template.setTransmissionContent(content);

        // template.setAPNInfo(getAPNPayload()); //详见【推送模板说明】iOS通知样式设置
        return template;
    }

    /**
     * 推送微信订阅消息
     *
     * @param openId
     * @param paras
     * @param type   message公众号消息subscribe订阅消息
     */
    public void pushWxMessage(String openId, List<Map> paras, String type) {
        //获取access_token
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("grant_type", "client_credential");
        paramMap.put("appid", wxAppId);
        paramMap.put("secret", wxSecret);
        JSONObject jsStr =JSONUtil.parseObj(HttpUtil.post("https://api.weixin.qq.com/cgi-bin/token", JSONUtil.toJsonStr(paramMap))) ;
        String access_token = jsStr.getStr("access_token");
        //发送订阅信息
        Map<String, Object> data = new HashMap<>();
        data.put("touser",openId);
        data.put("template_id",wxTemplateId);
        data.put("page","pages/login/login");
        data.put("data",paras);
        JSONObject jsonResult = JSONUtil.parseObj(HttpUtil.post("https://api.weixin.qq.com/cgi-bin/message/" + type + "/send?access_token=" + access_token, JSONUtil.toJsonStr(data))) ;
        if (jsonResult != null) {
            int errorCode = jsonResult.getInt("errcode");
            String errorMessage = jsonResult.getStr("errmsg");
            if (errorCode != 0) {
                LogModule.info("订阅消息发送失败:" + errorCode + "," + errorMessage);
            }
        }
    }
}
