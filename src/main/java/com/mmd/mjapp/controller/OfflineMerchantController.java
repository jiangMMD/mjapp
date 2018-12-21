package com.mmd.mjapp.controller;

import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.service.OfflineMerchantService;
import com.mmd.mjapp.utils.PublicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 线下商家模块
 */
@RestController
@RequestMapping("/offlineMerchant")
public class OfflineMerchantController {

    @Autowired
    private  OfflineMerchantService offlineMerchantService;

    /**
     * 根据店铺名称查询产品
     */
    @PostMapping("/getProdByMer")
    public Result getProdByMer(@RequestBody Map<String, Object> params) throws Exception {
        if (PublicUtil.isEmptyObj(params.get("mer_id"))){
            return new Result().fail("店铺ID不能为空");
        }
        Page page = PublicUtil.mapToEntity(params, Page.class);
        return offlineMerchantService.getProdByMer(page,String.valueOf(params.get("mer_id")));

    }

    /**
     * 获取首页
     */
    @PostMapping("/getShop")
    public Result getShop(@RequestBody Page page) throws Exception{
        return offlineMerchantService.getShop(page);
    }

    /**
     * 根据商铺ID查询商铺详细
     */
    @PostMapping("/getDetailed")
    public Result getDetailed(@RequestBody Map<String, Object> params )throws Exception{
        if (PublicUtil.isEmptyObj(params.get("mer_id"))){
            return new Result().fail("店铺ID不能为空");
        }
        Page page = PublicUtil.mapToEntity(params,Page.class);
        return offlineMerchantService.getDetailed(page,String.valueOf(params.get("mer_id")));
    }


    /**
     * 获取对应店铺的评论信息
     */
    @PostMapping("/getEvaluate")
    public Result getEvaluate(@RequestBody Map<String, Object> params )throws Exception{
        if (PublicUtil.isEmptyObj(params.get("mer_id"))){
            return new Result().fail("店铺ID不能为空");
        }
        Page page = PublicUtil.mapToEntity(params, Page.class);
        return  offlineMerchantService.getEvaluate(page,String.valueOf(params.get("mer_id")));
    }

}
