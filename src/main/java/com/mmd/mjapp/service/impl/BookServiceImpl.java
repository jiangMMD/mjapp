package com.mmd.mjapp.service.impl;

import com.github.pagehelper.PageHelper;
import com.mmd.mjapp.config.RedisUtils;
import com.mmd.mjapp.constant.RedisKey;
import com.mmd.mjapp.dao.BookDao;
import com.mmd.mjapp.dao.ProductDao;
import com.mmd.mjapp.dao.UserDao;
import com.mmd.mjapp.exception.ResultException;
import com.mmd.mjapp.model.User;
import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.pjo.ResultPage;
import com.mmd.mjapp.service.BookService;
import com.mmd.mjapp.utils.PublicUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class BookServiceImpl implements BookService {

    @Autowired
    private BookDao bookDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserDao userDao;

    @Override
    public Result addShopCat(String sku_id) {
        //判断该产品是不是库存不足或者已失效
        Map<String, Object> resMap = productDao.getSkuState(sku_id);
        if (resMap == null) {
            return new Result().fail("添加失败，该产品不存在或已失效！");
        } else if ((Integer) resMap.get("repertory") == 0) {
            return new Result().fail("添加失败，该产品库存已不足！");
        }
        User user = getUserInfo();
        //添加产品到购物车中
        bookDao.addShopCat(sku_id, user.getuId());
        //更新购物车中价格
        bookDao.updateCatPrice(user.getuId());
        return new Result().success();
    }

    @Override
    public Result getShopCat(Page page) {
        PageHelper.startPage(page);
        User user = getUserInfo();
        List<Map<String, Object>> list = bookDao.getShopCat(user.getuId());
        return new Result().success(new ResultPage<>(list));
    }


    @Override
    public Result addShopCatNum(String entry_id, Integer num) {
        //判断该产品是不是库存不住或者已失效
        Map<String, Object> resMap = productDao.getSkuStateByEntryId(entry_id);
        if (resMap == null) {
            return new Result().fail("添加失败，该产品不存在或已失效！");
        } else if ((Integer) resMap.get("repertory") == 0) {
            return new Result().fail("添加失败，该产品库存已不足！");
        }
        User user = getUserInfo();
        //添加产品到购物车中
        bookDao.updShopCatNum(entry_id, user.getuId(), num);
        //更新购物车中总价格
        bookDao.updateCatPrice(user.getuId());
        return new Result().success();
    }

    @Override
    public Result delShopCat(String entry_ids) {
        bookDao.delShopCat(PublicUtil.toListByIds(entry_ids));
        User user = getUserInfo();
        //更新购物车中总价格
        bookDao.updateCatPrice(user.getuId());
        return new Result().success();
    }

    /**
     * 购物车条目
     *
     * @param entry_ids
     */
    @Override
    public Result shopBalance(String entry_ids) {
        //最终要返回的map信息
        Map<String, Object> resMap = new HashMap<>();
        List<String> entryIds = PublicUtil.toListByIds(entry_ids);
        //查询产品订单信息
        List<Map<String, Object>> merItems = bookDao.getProdInfo(entryIds);
        //检查选择的产品是否有失效或者库存不足的产品
        for (Map<String, Object> map : merItems) {
            List<Map<String, Object>> prodList = (List<Map<String, Object>>) map.get("prodList");
            for (Map<String, Object> prod : prodList) {
                if ("2".equals(prod.get("prod_state"))) {
                    return new Result().success("有库存不足的下单产品");
                } else if ("3".equals(prod.get("prod_state"))) {
                    return new Result().success("有已失效的下单产品");
                }
            }
        }
        resMap.put("entry_ids", entry_ids);
        resMap.put("merItems", merItems);
        //查询本次订单的总费用
        Map<String, Object> fee = bookDao.getAllFee(entryIds);
        resMap.put("fee", fee); //产品总费用

        //用户当前可用总积分
        User user = getUserInfo();
        resMap.put("totalIntegral", user.getuIntegral()); //用户总积分
        //如果使用积分那么对应金额。
        List<String> rateInfos = bookDao.getRates();
        if (rateInfos == null || rateInfos.size() < 2) {
            log.error("RMB和积分比例表数据出现错误！");
            return new Result().fail("服务器内部数据异常！");
        }
        //RMB/MMD比例
        resMap.put("mmdToRmbRate", rateInfos.get(0));
        //1RMB = ？积分
        resMap.put("integralRate", rateInfos.get(1));
        //计算如果使用积分，那么最终的RMB或MMD价格将是多少？
        Double rmbprice = (Double) fee.get("rmbprice");
        Double mmdprice = (Double) fee.get("mmdprice");
        BigDecimal rmbDecimal = new BigDecimal(rmbprice);
        //剩余积分
        Double remainIntegral = user.getuIntegral() % Double.valueOf(rateInfos.get(1));
        Integer rmIntegral = remainIntegral.intValue();
        resMap.put("remainIntegral", rmIntegral); //剩余积分
        Long usableIntegral = user.getuIntegral() - rmIntegral;
        resMap.put("usableIntegral", usableIntegral); //可用积分
        //兑换的积分对应着RMB价格
        BigDecimal usIntegralRmb = new BigDecimal(usableIntegral)
                .divide(new BigDecimal(rateInfos.get(1)), BigDecimal.ROUND_HALF_DOWN);
        resMap.put("usIntegralRmb", usIntegralRmb);
        BigDecimal rmbpriceUseIg = rmbDecimal.subtract(usIntegralRmb).setScale(2, BigDecimal.ROUND_HALF_DOWN);
        resMap.put("rmbpriceUseIg", rmbpriceUseIg);
        BigDecimal mmdpriceUseIg = new BigDecimal(mmdprice)
                .subtract(usIntegralRmb.divide(new BigDecimal(rateInfos.get(0)), BigDecimal.ROUND_HALF_DOWN)
                        .setScale(0, BigDecimal.ROUND_HALF_DOWN))
                .setScale(2, BigDecimal.ROUND_HALF_DOWN);
        resMap.put("mmdpriceUseIg", mmdpriceUseIg);

        //查询默认收货地址
        resMap.put("shipaddress", userDao.getDefaultShipAddress(user.getuId()));
        return new Result().success(resMap);
    }

    /**
     * 确认订单
     *
     * @return
     */
    @Override
    public Result confirmBook(Map<String, Object> params) throws Exception {
        User user = getUserInfo();
        String entry_ids = String.valueOf(params.get("entry_ids"));
        List<String> entryIds = PublicUtil.toListByIds(entry_ids);
        //执行减库存操作。
        dealProdRepertory(entryIds, params);

        //处理用户积分信息
        dealUserIntegral(user, params, entryIds);
        //生成订单编号
        String bno = PublicUtil.getBookNo();
        //生成订单信息。
        bookDao.createBook(user.getuId(), bno, params); //创建主订单表
        //生成订单项详细
        List<Map<String, Object>> prodInfos = (List<Map<String, Object>>) params.get("buySkuInfo");
        for (Map<String, Object> prodInfo : prodInfos) {
            //添加产品项信息
            bookDao.createBookItem(prodInfo, user.getuId(), String.valueOf(params.get("address_id")), String.valueOf(params.get("bid")));
        }
        //删除购物车中的产品
        bookDao.delShopCat(entryIds);
        //更新购物车中的总价
        bookDao.updateCatPrice(user.getuId());
        Map<String, Object> res = new HashMap<>();
        res.put("bid", params.get("bid"));
        res.put("rmbprice", params.get("rmbprice"));
        res.put("mmdprice", params.get("mmdprice"));
        redisUtils.setUserInfo(user); //当数据库没有错误的时候，那么更新用户信息到redis中。
        return new Result().success(res);
    }


    /**
     * 处理产品库存操作
     */
    private void dealProdRepertory(List<String> entryIds, Map<String, Object> params) {
        List<Map<String, Object>> buySkuInfo = bookDao.getBuySkuInfo(entryIds);
        if(buySkuInfo == null || buySkuInfo.size() < entryIds.size()) {
            throw new ResultException("订单中有无效的商品！");
        }
        //判断购买的产品是否具有失效的产品
        for(Map<String, Object> map : buySkuInfo) {
            if("2".equals(map.get("prod_state"))) {
                throw new ResultException("产品库存不足!");
            }else if("3".equals(map.get("prod_state"))) {
                throw new ResultException("产品已失效!");
            }
        }
        params.put("buySkuInfo", buySkuInfo);
        //判断购买产品是否出现库存不足情况
        for (Map<String, Object> map : buySkuInfo) {
            //使用mysql for update 查询锁限制重复购买情况。 后期增加购买量之后，需要更换掉。
            int num = bookDao.getSkuStore(String.valueOf(map.get("sku_id")));
            if (num >= (Integer) map.get("num")) {
                //进行减SKU库存操作
                bookDao.updateSkuStore(map);
                //进行减产品库存操作
                bookDao.reduceStore(map);
            } else {
                throw new ResultException("产品库存不足");
            }
        }
    }

    /**
     * 处理用户积分信息
     */
    private void dealUserIntegral(User user, Map<String, Object> params, List<String> entryIds) throws Exception {
        Map<String, Object> fee = bookDao.getAllFee(entryIds);
        Double rmbprice = (Double) fee.get("rmbprice");
        Double mmdprice = (Double) fee.get("mmdprice");
        params.put("totalrmbprice", rmbprice);
        params.put("totalmmdprice", mmdprice);
        params.put("rmbprice", rmbprice);
        params.put("mmdprice", mmdprice);
        Object buyIntegral = redisUtils.getObj(RedisKey.BUYINTEGRAL);
        if (buyIntegral == null) {
            buyIntegral = bookDao.getBuyIntegral();
            if (buyIntegral == null) {
                throw new ResultException("操作失败，系统内部数据错误!");
            }
        }
        //本次购物会生成的积分
        Integer getInteger = 0;
        String backform = (String) ((Map<String, Object>) buyIntegral).get("backform");
        Double value = (Double) ((Map<String, Object>) buyIntegral).get("value");
        if ("1".equals(backform)) {
            //按固定积分， 否则按百分比
            getInteger = value.intValue();
        } else {
            //按百分比
            getInteger = ((Double) (value * rmbprice / 100)).intValue();
        }
        if (getInteger != 0) {
            //那么记录本次积分获得情况
            bookDao.insertUseIntegral(user.getuId(), getInteger, 2);
        }
        //是否使用积分。
        if (params.get("useIntegral") != null && "1".equals(String.valueOf(params.get("useIntegral")))) {
            //使用积分,
            //如果使用积分那么对应金额。
            List<String> rateInfos = bookDao.getRates();
            if (rateInfos == null || rateInfos.size() < 2) {
                log.error("RMB和积分比例表数据出现错误！");
                throw new ResultException("服务器内部数据异常！");
            }
            Double remainIntegral = user.getuIntegral() % Double.valueOf(rateInfos.get(1));
            //使用完的剩余积分
            Integer rmIntegral = remainIntegral.intValue();//用户使用积分之后，剩余的积分。
            Long usableIntegral = user.getuIntegral() - rmIntegral;//可用积分
            //记录本次积分消耗情况
            bookDao.insertUseIntegral(user.getuId(), usableIntegral.intValue(), 1);
            getInteger = getInteger - usableIntegral.intValue();

            //剩余积分
            //兑换的积分对应着RMB价格
            BigDecimal usIntegralRmb = new BigDecimal(usableIntegral)
                    .divide(new BigDecimal(rateInfos.get(1)), BigDecimal.ROUND_HALF_DOWN);
            BigDecimal rmbpriceUseIg = new BigDecimal(rmbprice).subtract(usIntegralRmb).setScale(2, BigDecimal.ROUND_HALF_DOWN);
            params.put("rmbprice", rmbpriceUseIg);
            BigDecimal mmdpriceUseIg = new BigDecimal(mmdprice)
                    .subtract(usIntegralRmb.divide(new BigDecimal(rateInfos.get(0)), BigDecimal.ROUND_HALF_DOWN)
                            .setScale(0, BigDecimal.ROUND_HALF_DOWN))
                    .setScale(2, BigDecimal.ROUND_HALF_DOWN);
            params.put("mmdprice", mmdpriceUseIg);
        }
        getInteger = getInteger + user.getuIntegral().intValue();
        //更新用户的最终剩余积分
        userDao.updateUserInteger(user.getuId(), getInteger);
        user.setuIntegral(getInteger.longValue());
    }


    /**
     * 查询全部订单
     * @param state
     */
    @Override
    public Result getAllBookList(String state) {
        User user = getUserInfo();
        List<Map<String, Object>> list = bookDao.getAllBookList(user.getuId(), state);
        return new Result().success(new ResultPage<>(list));
    }

    @Override
    public Result getBookDetail(String bid) {
        return new Result().success(bookDao.getBookDetail(bid));
    }

    private User getUserInfo() {
        return (User) request.getAttribute("user");
    }
}
