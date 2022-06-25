package org.ethanyuen.springboot.service;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.ethanyuen.springboot.utilbean.Result;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 腾讯短信服务
 */
@IocBean
public class TxSMSService {
    @Value("${tx-sm-appId}")
    private static String APPID;
    @Value("${tx-sm-appKey}")
    private static String APPKEY;
    @Value("${tx-sm-templateId}")
    private static long tpl_id;//腾讯云备案的短信模版
    @Autowired
    private Dao dao;

    /**
     * 用post提交 单发短信
     *
     * @param paramsArr 替换短信模版里的文字信息
     * @param phone     接收短信的手机号码
     * @return
     */
    public static Result SendSingleMessage(String[] paramsArr, String phone) throws UnsupportedEncodingException {
        Result re = new Result();
        //获取随机数
        String random = String.valueOf((int) (new Random().nextDouble() * 100000));
        for (int i = 0; i < paramsArr.length; i++) {
            paramsArr[i] = getMsgString(paramsArr[i]);
            paramsArr[i].getBytes("utf8");
        }
        HttpUtil.post("https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid=" + APPID + "&random=" + random, JSONUtil.toJsonStr(CreateSingleMessageParam(paramsArr, phone, tpl_id,random)));
        return re;
    }

    public static Map<String, Object> CreateSingleMessageParam(String[] paramsArr, String phone, long tpl_id,String random) {
        String strTime = System.currentTimeMillis() / 1000 + "";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ext", "");
        map.put("extend", "");
        map.put("params", paramsArr);
        map.put("sig", getSig(phone, strTime,random));
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("mobile", phone);
        map1.put("nationcode", 86 + "");
        map.put("tel", map1);
        map.put("time", strTime);
        map.put("tpl_id", tpl_id);
        return map;
    }

    public static String getSig(String phone, String strTime,String random) {
        String sigContent = "appkey=" + APPKEY + "&random=" + random + "&time=" + strTime + "&mobile=" + phone;
        return getSHA256StrJava(sigContent);
    }

    /**
     * 利用java原生的摘要实现SHA256加密
     *
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256StrJava(String str) {
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 将byte转为16进制
     *
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    /**
     * 处理短信发送数据当超过12个时中间星号显示
     */
    public static String getMsgString(String msg) {
        if (msg.length() > 12) {
            String msgFirst = msg.substring(0, 4);
            String msgLast = msg.substring(msg.length() - 4, msg.length());
            return msgFirst + "***" + msgLast;
        } else {
            return msg;
        }
    }
}
