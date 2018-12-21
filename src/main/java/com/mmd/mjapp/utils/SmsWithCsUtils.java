package com.mmd.mjapp.utils;

import com.alibaba.fastjson.JSON;
import com.mmd.mjapp.pjo.Result;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 创世短信
 */
public class SmsWithCsUtils {

    /**
     * 发送订单提醒短信
     * @return
     */
    public static Result sendInformBook(String phone, String bookno) {
        return send(phone, "【魔晶】尊敬的店家小主，订单"+bookno+"的买家提醒你发货啦！");
    }

    /**
     * 发送注册短信验证码
     */
    public static Result sendRegYzm(String phone, String code) {
        return send(phone, "【魔晶】验证码"+code+"，您正在注册成为新用户，感谢您的使用！");
    }

    /**
     * 发送登录短信验证码
     */
    public static Result sendLoginYzm(String phone, String code) {
        return send(phone, "【魔晶】验证码"+code+"，您正在登录，若非本人操作，请勿泄露。");
    }

    /**
     * 发送支付短信验证码
     */
    public static Result sendPayYzm(String phone, String code) {
        return send(phone, "【魔晶】验证码"+code+"，您正在支付，若非本人操作，请勿泄露。");
    }

    /**
     * 下单成功通知
     */
    public static Result sendOrderYzm(String phone) {
        return send(phone, "【魔晶】亲爱的小主，感谢你的支持，您购买的产品已经通知操作人员发货啦！");
    }


    private static Result send(String phone, String content) {
        String url = "http://dx110.ipyy.net/smsJson.aspx";
        Map<String, String> map = new HashMap<>();
        map.put("content", content);
        map.put("account", PropertyLoad.getProperty("SMS.account"));
        map.put("password", DigestUtils.md5DigestAsHex(PropertyLoad.getProperty("SMS.password").getBytes()));
        map.put("mobile", phone);
        map.put("sendTime", "");
        map.put("action", "send");
        map.put("extno", "");

        String result = HttpClientUtil.sendPostRequest(map, url);
        Map<String, Object> res = JSON.parseObject(result);
        if("Success".equals(res.get("retrunestatus"))) {
            return new Result(1, "发送成功！");
        }
        return new Result(0, "短信发送失败！"+ res.get("message"));
    }


}
