package com.mmd.mjapp.controller;

import com.mmd.mjapp.pjo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 无序验证的接口，用于回调等操作
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @RequestMapping("/logisticsCall")
    public Map<String, Object>  logisticsCall(@RequestParam Map<String, Object> params) {
        System.out.println(params);
        Map<String, Object> callBack = new HashMap<>();


        //推送返回的信息
        callBack.put("result", true);
        callBack.put("returnCode", "200");
        callBack.put("message", "成功");
        return callBack;
    }



}
