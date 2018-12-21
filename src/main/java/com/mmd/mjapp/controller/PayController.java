package com.mmd.mjapp.controller;

import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.service.PayService;
import com.mmd.mjapp.utils.PublicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * 在线支付
 */
@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;

    /**
     * 线上使用MMD支付
     */
    @PostMapping("/prodPayWithMMD")
    public Result prodPayWithMMD(@RequestBody Map<String, Object> param) throws Exception {
        System.out.println(param);
        if(PublicUtil.isEmptyObj(param.get("type"))) {
            return new Result().fail("TYPE不能为空！");
        }else if(PublicUtil.isEmptyObj(param.get("bid"))) {
            return new Result().fail("订单ID不能为空！");
        }else if(PublicUtil.isEmptyObj(param.get("yzmCode"))) {
            return new Result().fail("验证码不能为空！");
        }else if(PublicUtil.isEmptyObj(param.get("yzmToken"))) {
            return new Result().fail("验证码TOKEN不能为空！");
        }
        //添加到rabbit中去

        return payService.prodPayWithMMD(param);
    }

    /**
     * 线下使用MMD支付
     */
    @PostMapping("/offlinePayWithMMD")
    public Result offlinePayWithMMD(@RequestBody Map<String, Object> param) throws Exception {
        if(PublicUtil.isEmptyObj(param.get("offmer_id"))) {
            return new Result().fail("商家ID不能为空！");
        }else if(PublicUtil.isEmptyObj(param.get("money"))) {
            return new Result().fail("金额不能为空！");
        }else if(PublicUtil.isEmptyObj(param.get("yzmCode"))) {
            return new Result().fail("验证码不能为空！");
        }else if(PublicUtil.isEmptyObj(param.get("yzmToken"))) {
            return new Result().fail("验证码TOKEN不能为空！");
        }
        return payService.offlinePayWithMMD(param);
    }

}
