package com.mmd.mjapp.controller;

import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.service.BookService;
import com.mmd.mjapp.utils.PublicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * 订单及其购物车模块
 */
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 添加购物车
     *
     * @return
     */
    @PostMapping("/addShopCat")
    public Result addShopCat(@RequestBody Map<String, Object> params) {
        if (PublicUtil.isEmptyObj(params.get("sku_id"))) {
            return new Result().fail("SKUID不能为空！");
        }
        return bookService.addShopCat(String.valueOf(params.get("sku_id")));
    }

    /**
     * 修改购物车中某一产品数量
     */
    @PostMapping("/updateShopCatNum")
    public Result addShopCatNum(@RequestBody Map<String, Object> params) {
        if (PublicUtil.isEmptyObj(params.get("entry_id"))) {
            return new Result().fail("购物车条目ID不能为空！");
        }else if(params.get("num") == null) {
            return new Result().fail("参数NUM不能为空！");
        }
        try {
            Integer num = (Integer) params.get("num");
            if(num < 0) {
                return new Result().fail("NUM必须是大于0的");
            }
            return bookService.addShopCatNum(String.valueOf(params.get("entry_id")), num);
        } catch (Exception e) {
            return new Result().fail("NUM必须为数字类型");
        }
    }

    /**
     * 删除购物车中产品数量
     */
    @PostMapping("/delShopCat")
    public Result delShopCat(@RequestBody Map<String, Object> params) {
        if(PublicUtil.isEmptyObj(params.get("entry_ids"))) {
            return new Result().fail("条目ID不能为空");
        }
        return bookService.delShopCat(String.valueOf(params.get("entry_ids")));
    }

    /**
     * 加载购物车中信息
     */
    @PostMapping("/getShopCat")
    public Result getShopCat(@RequestBody Map<String, Object> params) throws Exception {
        Page page = PublicUtil.mapToEntity(params, Page.class);
        return bookService.getShopCat(page);
    }

    /**
     * 购物车结算
     */
    @PostMapping("/shopBalance")
    public Result shopBalance(@RequestBody Map<String, Object> params) {
        if(PublicUtil.isEmptyObj(params.get("entry_ids"))) {
            return new Result().fail("条目ID不能为空");
        }
        return bookService.shopBalance(String.valueOf(params.get("entry_ids")));
    }

    /**
     * 确认订单
     */
    @PostMapping("/confirmBook")
    public Result confirmBook(@RequestBody Map<String, Object> params) throws Exception {
        if(PublicUtil.isEmptyObj(params.get("entry_ids"))) {
            return new Result().fail("条目ID不能为空");
        }else if(PublicUtil.isEmptyObj(params.get("address_id"))) {
            return new Result().fail("收货地址不能为空");
        }
        return bookService.confirmBook(params);
    }

    /**
     * 查询订单信息， 我的全部订单
     */
    @PostMapping("/getBookList")
    public Result getAllBookList(@RequestBody(required = false) Map<String, Object> params) {
        String state = (params == null || params.get("state") == null) ? "" : String.valueOf(params.get("state"));
        return bookService.getAllBookList(state);
    }


    @PostMapping("/getBookDetail")
    public Result getBookDetail(@RequestBody Map<String, Object> params) {
        return bookService.getBookDetail(String.valueOf(params.get("bid")));
    }

    /**
     * 确认收货
     */
    @PostMapping("/cfmBook")
    public Result cfmBook(@RequestBody Map<String, Object> param) {
        if(PublicUtil.isEmptyObj(param.get("bid"))) {
            return new Result().fail("订单ID不能为空！");
        }
        return bookService.cfmBook(param);
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancelBook")
    public Result cancelBook(@RequestBody Map<String, Object> param) {
        if(PublicUtil.isEmptyObj(param.get("bid"))) {
            return new Result().fail("订单ID不能为空！");
        }
        return bookService.cancelBook(param);
    }

    /**
     * 删除订单
     */
    @PostMapping("/delBook")
    public Result delBook(@RequestBody Map<String, Object> param) {
        if(PublicUtil.isEmptyObj(param.get("bid"))) {
            return new Result().fail("订单ID不能为空！");
        }
        return bookService.delBook(param);
    }


    /**
     * 提醒发货
     */
    @PostMapping("remindDeliver")
    public Result remindDeliver(@RequestBody Map<String, Object> param) {
        if(PublicUtil.isEmptyObj(param.get("bid"))) {
            return new Result().fail("订单ID不能为空！");
        }
        return bookService.remindDeliver(param);
    }

    /**
     * 查看物流
     */
    @PostMapping("/queryLogistics")
    public Result queryLogistics(@RequestBody Map<String, Object> param) {
        if(PublicUtil.isEmptyObj(param.get("bid"))) {
            return new Result().fail("订单ID不能为空！");
        }
        return bookService.queryLogistics(param);
    }


}
