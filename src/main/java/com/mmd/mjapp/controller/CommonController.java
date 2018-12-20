package com.mmd.mjapp.controller;

import com.mmd.mjapp.pjo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 无序验证的接口，用于回调等操作
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @RequestMapping("/logisticsCall")
    public Result  logisticsCall(@RequestParam Map<String, Object> params) {
        System.out.println(params);
        return new Result();
    }


}
