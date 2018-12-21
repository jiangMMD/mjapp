package com.mmd.mjapp.service.impl;

import com.mmd.mjapp.config.RedisUtils;
import com.mmd.mjapp.constant.RedisKey;
import com.mmd.mjapp.dao.BookDao;
import com.mmd.mjapp.dao.PayDao;
import com.mmd.mjapp.model.User;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.service.PayService;
import com.mmd.mjapp.utils.MMDUtils;
import com.mmd.mjapp.utils.PublicUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private PayDao payDao;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Result prodPayWithMMD(Map<String, Object> param) throws Exception {
        User user = getUserInfo();
        if (user.getuMmdNo() == null) {
            return new Result().fail("请先绑定MMD账户！");
        }
        //校验验证码
        Result res = check(param);
        if (res.getCode() == 1) {
            //订单ID
            String type = String.valueOf(param.get("type"));
            String bid = String.valueOf(param.get("bid"));
            if ("1".equals(type)) { //表明是多个订单直接支付
                Map<String, Object> bookInfo = bookDao.getBookItemsByPickId(bid);
                if (bookInfo == null) {
                    return new Result().fail("订单不存在或已失效！");
                }
                Double mmdprice = Double.valueOf(String.valueOf(bookInfo.get("mmdprice")));
                Result resMap = MMDUtils.mmdPayToCompany(user.getuMmdNo(), String.valueOf(mmdprice));
                if (resMap.getCode() == 1) {//处理resMap
                    bookDao.updateBookToPayMMDSuccess(bid, mmdprice, "4");
                    payDao.insertPayInfo(bid, mmdprice, 2, 1, 1); //记录支付成功信息
                    //查询该主订单下的所有子订单。
                    List<String> bookitems = bookDao.getAllBookByPickId(bid);
                    dealBookState(bookitems);
                } else {
                    payDao.insertPayInfo(bid, mmdprice, 2, 1, 0); //记录支付失败信息
                    /**
                     * 做短信通知
                     */
                }
                return resMap;
            } else if ("2".equals(type)) { //表明是单个订单支付或者是合并订单支付
                List<String> bookItems = PublicUtil.toListByIds(bid);
                List<Map<String, Object>> bookList = bookDao.getBookItemsByBids(bookItems);
                Double totalDoubleMMDPrice = 0D;
                for (Map<String, Object> map : bookList) {
                    Double topaymmdprice = (Double) map.get("topaymmdprice");
                    totalDoubleMMDPrice = totalDoubleMMDPrice + topaymmdprice;
                }
                Result resMap = MMDUtils.mmdPayToCompany(user.getuMmdNo(), String.valueOf(totalDoubleMMDPrice));
                if (resMap.getCode() == 1) {
                    bookDao.updateBookToPayMMDSuccessByBids(bookItems, totalDoubleMMDPrice, 4);
                    payDao.insertPayInfo(bid, totalDoubleMMDPrice, 2, bookItems.size() > 0 ? 2 : 3, 1); //记录支付成功信息
                    dealBookState(bookItems);
                } else {
                    payDao.insertPayInfo(bid, totalDoubleMMDPrice, 2, bookItems.size() > 0 ? 2 : 3, 0); //记录支付失败信息
                }
                return resMap;
            }
        }
        return res;
    }

    @Override
    public Result offlinePayWithMMD(Map<String, Object> param) throws Exception {
        User user = getUserInfo();
        if (user.getuMmdNo() == null) {
            return new Result().fail("请先绑定MMD账户！");
        }
        //校验验证码
        Result res = check(param);
        if (res.getCode() == 1) {
            //生成订单信息
            bookDao.createOffBook(param);
            payDao.insertPayInfo(String.valueOf(param.get("id")), Double.valueOf(String.valueOf(param.get("money"))),
                    2, null, 1); //添加支付信息
        }
        return res;
    }

    /**
     * 处理Redis中的订单
     */
    private void dealBookState(List<String> bookitems) throws Exception {
        redisUtils.deleteWithSet(RedisKey.TOBEPAY, bookitems.toArray());
        redisUtils.deleteByKeys(bookitems);
    }

    /**
     * 校验信息
     */
    private Result check(Map<String, Object> param) throws Exception {
        if (isVaild(String.valueOf(param.get("yzmToken")))) {
            return new Result(0, "验证码已失效");
        } else if (!checkYzm(String.valueOf(param.get("yzmToken")), String.valueOf(param.get("yzmCode")))) {
            return new Result(0, "验证码不正确");
        }
        return new Result();
    }

    //校验验证码
    private boolean checkYzm(String yzmtoken, String yzmCode) throws Exception {
        //从redis中获取验证码
        if (StringUtils.isEmpty(yzmtoken)) {
            return false;
        }
        String yzmByRedis = redisUtils.get(yzmtoken);
        if (!yzmCode.equals(yzmByRedis)) {
            return false;
        }
        return true;
    }

    private boolean isVaild(String yzmtoken) throws Exception {
        return StringUtils.isEmpty(redisUtils.get(yzmtoken));
    }

    private User getUserInfo() {
        return (User) request.getAttribute("user");
    }
}
