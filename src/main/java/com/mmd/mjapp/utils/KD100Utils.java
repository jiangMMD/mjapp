package com.mmd.mjapp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmd.mjapp.exception.ResultException;
import com.mmd.mjapp.pjo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 快递100的各种查询接口
 */
@Slf4j
public class KD100Utils {

    /**
     * 查询快递单号所属
     */
    public static List<String> queryKDComCode() {
        String url = "http://www.kuaidi100.com/autonumber/auto?num=" + PropertyLoad.getProperty("KD100.key") + "&key=" + PropertyLoad.getProperty("KD100.customer");
        String result = HttpClientUtil.sendGetRequest(url);
        List<Map<String, String>> resMap = JSONObject.parseObject(result, List.class);
        System.out.println(resMap);
        List<String> list = new ArrayList<>();
        for (Map<String, String> map : resMap) {
            list.add(map.get("comCode"));
        }
        return list;
    }

    /**
     * 查询订单信息
     */
    public static Map<String, Object> queryKDExpress(String com, String num) {
        String url = "https://poll.kuaidi100.com/poll/query.do";
        String key = PropertyLoad.getProperty("KD100.key");
        String customer = PropertyLoad.getProperty("KD100.customer");
        Map param = new HashMap();
        param.put("com", com);
        param.put("num", num);
        param.put("resultv2", 1);
        String jsonPar = JSON.toJSONString(param);
        System.out.println(param);
        String sign = DigestUtils.md5DigestAsHex((jsonPar + key + customer).getBytes()).toUpperCase();
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("param", jsonPar);
        reqParams.put("sign", sign);
        reqParams.put("customer", customer);
        System.out.println(reqParams);
        String result = HttpClientUtil.sendPostRequest(reqParams, url);

        log.info(result);
        Map<String, Object> resMap = JSON.parseObject(result, Map.class);
        if (resMap.get("result") != null && (boolean) resMap.get("result") == false) {
            throw new ResultException("查询失败！"+resMap.get("message"));
        }
        if ("ok".equals(resMap.get("message"))) {
            Map<String, Object> resultMap = new HashMap<>();
            List<Map<String, Object>> jList = (List<Map<String, Object>>) resMap.get("data");
            resultMap.put("data", jList);
            resultMap.put("ischeck", resMap.get("ischeck")); //是否已签收
            resultMap.put("ship_no", resMap.get("nu")); //订单号
            resultMap.put("state", resMap.get("state")); //订单状态
            return resultMap;
        }
        throw new ResultException("查询失败！");
    }

    /**
     * 订阅订单
     */
    public static Result subLogistics(String com, String num) {
        //订阅接口
        String url = "https://poll.kuaidi100.com/poll";
        Map<String, Object> map = new HashMap<>();
        map.put("schema", "json");
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("company", com);
        paramMap.put("number", num);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("callbackurl", "http://47.99.103.214:8070/mjapp/common/logisticsCall");
        paramMap.put("parameters", parameters);
        paramMap.put("key", PropertyLoad.getProperty("KD100.key"));
        map.put("param", JSON.toJSONString(paramMap));

        System.out.println(map);
        String result = HttpClientUtil.sendPostRequest(map, url);
        Map<String, Object> res = JSONObject.parseObject(result, Map.class);
        if ((boolean) res.get("result")) {
            return new Result().success();
        } else {
            return new Result(0, String.valueOf(res.get("message")));
        }
    }

}
