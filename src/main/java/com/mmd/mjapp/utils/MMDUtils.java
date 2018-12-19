package com.mmd.mjapp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmd.mjapp.exception.ResultException;
import com.mmd.mjapp.pjo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * mmd相关接口类
 */
@Slf4j
public class MMDUtils {

    /**
     * 在线支付接口
     */
    public static Result mmdPayToCompany(String payno, String amount) {
        String appId = PropertyLoad.getProperty("MMD.appId");
        String key = PropertyLoad.getProperty("MMD.key");
        String companyNo = PropertyLoad.getProperty("MMD.companyno");
        String orderId = PublicUtil.getBookNo();
        String param = "amount=" + amount + "&appId=" + appId + "&orderId=" + orderId + "&payee=" + companyNo + "&payer=" + payno + "&key=" + key;
        String sign = DigestUtils.md5DigestAsHex(param.getBytes());
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("appId", appId);
        reqMap.put("orderId", orderId);
        reqMap.put("payer", payno);
        reqMap.put("payee", companyNo);
        reqMap.put("amount", amount);
        reqMap.put("sign", sign);
        System.out.println(reqMap);
        String result = HttpClientUtil.sendPostRequest(reqMap, PropertyLoad.getProperty("MMD.transfer"));
//        String result = HttpClientUtil.sendGetRequest(PropertyLoad.getProperty("MMD.transfer") + "?" + param);
        log.info(result);
        Map<String, Object> res = JSONObject.parseObject(result, Map.class);
        if (res.get("success") != null && (Boolean) res.get("success")) {
            return new Result().success("支付成功！");
        }
        throw new ResultException("支付失败：" + (String.valueOf(res.get("message")).contains("未知错误,请重试") ? "余额不足！": res.get("message")));
    }

    public static void main(String[] args) {
        MMDUtils mmdUtils = new MMDUtils();
        mmdUtils.mmdPayToCompany("737012253", "1");
    }

}
