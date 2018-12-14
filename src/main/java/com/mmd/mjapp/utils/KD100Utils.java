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
    public List<String> queryKDComCode() {
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
    public List<Map<String, Object>> queryKDExpress(String com, String nu) {
        String url = "https://poll.kuaidi100.com/poll/query.do";
        String key = PropertyLoad.getProperty("KD100.key");
        String customer = PropertyLoad.getProperty("KD100.customer");
        Map param = new HashMap();
        param.put("com", com);
        param.put("nu", nu);
        String jsonPar = JSON.toJSONString(param);
        String sign = DigestUtils.md5DigestAsHex((jsonPar + key + customer).getBytes());
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("param", jsonPar);
        reqParams.put("sign", sign);
        reqParams.put("customer", customer);
        String result = HttpClientUtil.sendPostRequest(reqParams, url);

        log.info(result);
        Map<String, Object> resMap = JSON.parseObject(result, Map.class);
        if (resMap.get("result") != null && (boolean) resMap.get("result") == false) {
            throw new ResultException("没有找到该订单信息，请确保订单号正确");
        }
        if ("ok".equals(resMap.get("message"))) {
            List<Map<String, Object>> jList = (List<Map<String, Object>>) resMap.get("data");
            return jList;
        }
        throw new ResultException("查询失败！");
    }


}
