package com.mmd.mjapp.controller;


import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.pjo.ResultPage;
import com.mmd.mjapp.service.HomeService;
import com.mmd.mjapp.utils.PublicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 首页信息
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private HomeService homeService;
    /**
     * 获取首页分类，
     * @return
     */
    @PostMapping("/getHotData")
    public Result getHotData() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        //加载分类信息
        List<Map<String, Object>> classify = homeService.getClassify();
        resultMap.put("classify", classify);
        //加载轮播信息
        List<Map<String, Object>> carousels = homeService.getCarousel();
        resultMap.put("carousele", carousels);
        return new Result().success(resultMap);
    }

    /**
     * 加载品牌信息
     */
    @PostMapping("/getBrand")
    public Result getBrand(@RequestBody Page page) throws Exception {
        return homeService.getBrand(page);
    }

    /**
     * 加载搜索热词
     */
    @PostMapping("/getHotWord")
    public Result getHotWord() throws Exception {
        return homeService.getHotWord();
    }

    /**
     * 搜索产品
     */
    @PostMapping("/getProdByKey")
    public Result getProdByKey(@RequestBody Map<String, Object> params) throws Exception {
        if(params.get("key") == null && "".equals(params.get("key"))) {
            return new Result().fail("请输入商品信息");
        }
        Page page = PublicUtil.mapToEntity(params, Page.class);
        String key = String.valueOf(params.get("key"));
        return homeService.getProdByKey(page, key);
    }

    /**
     *检查是否有未读消息
     */
    @PostMapping("/hasNews")
    public Result hasNews() {
        return homeService.hasNews();
    }

    /**
     * 点击消息进入，每个消息查询一条
     */
    @PostMapping("/getBaseMsg")
    public Result getBaseMsg() {
        return homeService.getBaseMsg();
    }

    /**
     * 查询系统消息
     */
    @PostMapping("/getMsgList")
    public Result getSysMsgList(@RequestBody Map<String, Object> params) throws Exception {
        if(params.get("type") ==null || "".equals(params.get("type"))) {
            return new Result().fail("Type不能为空");
        }else if(!Objects.equals(1, params.get("type")) && !Objects.equals(2, params.get("type"))) {
            return new Result().fail("Type值错误，只能为Number型的1或者2");
        }
        Page page = PublicUtil.mapToEntity(params, Page.class);
        return homeService.getSysMsgList(page, String.valueOf(params.get("type")));
    }

    /**
     * 查询消息详细
     */
    @PostMapping("/getMsgDetail")
    public Result getMsgDetail(@RequestBody Map<String, Object> params) {
        return homeService.getMsgDetail(String.valueOf(params.get("id")));
    }

}
