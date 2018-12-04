package com.mmd.mjapp.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.mmd.mjapp.model.Productsinfo;
import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.service.ProductService;
import com.mmd.mjapp.utils.PublicUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * 产品相关
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    //查询某一品牌和店铺下的产品信息
    @PostMapping("/queryProdListByStore")
    public Result queryProdListByStore(@RequestBody Map<String, Object> params) throws Exception {
        Page page = PublicUtil.mapToEntity(params, Page.class);
        //店铺id
        if(params.get("id") == null || "".equals(params.get("id"))) {
            return new Result().fail("商品ID不能为空");
        }
        String id = String.valueOf(params.get("id"));
        return productService.queryProdListByStore(page, id);
    }


    /**
     * 查询产品详细
     * @return
     */
    @PostMapping("/queryProdDetail")
    public Result queryProdDetail(@RequestBody Map<String, Object> params) {
        String pid = String.valueOf(params.get("pid"));
        if(StringUtils.isEmpty(pid)) {
            return new Result().fail("参数异常，Pid不能为空");
        }
        //产品产品基本信息
        return productService.queryProdDetail(pid);
    }

    /**
     * 产品收藏
     */
    @PostMapping("/collectProd")
    public Result collectProd(@RequestBody Map<String, Object> params) {
        if(PublicUtil.isEmptyObj(params.get("pid"))) {
            return new Result().fail("产品ID不能缺失");
        }
        return productService.collectProd(String.valueOf(params.get("pid")));
    }

    /**
     * 取消收藏的产品
     */
    @PostMapping("delCollectProd")
    public Result delCollectProd(@RequestBody Map<String, Object> params) {
        if(PublicUtil.isEmptyObj(params.get("ids"))) {
            return new Result().fail("产品ID缺失");
        }
        return productService.delCollectProd(String.valueOf(params.get("ids")));
    }

    /**
     * 查询浏览记录
     */
    @PostMapping("getBrowProdList")
    public Result getBrowProdList() {
        return productService.getBrowProdList();
    }

    /**
     * 查询评价常用词
     */
    @PostMapping("getSightWord")
    public Result getSightWord() throws Exception {
        return productService.getSightWord();
    }

    /**
     * 查询对应产品的评论信息
     */
    @PostMapping("getProdEvaluate")
    public Result getProdEvaluate(@RequestBody Map<String, Object> params) throws Exception {
        Page page = PublicUtil.mapToEntity(params, Page.class);
        return productService.getProdEvaluate(page, String.valueOf(params.get("pid")));
    }
}
