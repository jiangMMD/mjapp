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
import java.util.*;

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
     * 购物车结算
     * @param entry_ids
     */
    @Override
    public Result shopBalance(String entry_ids) {
        //最终要返回的map信息
        Map<String, Object> resMap = new HashMap<>();
        List<String> entryIds = PublicUtil.toListByIds(entry_ids);
        //查询产品订单信息
        List<Map<String, Object>> merItems = bookDao.getProdInfo(entryIds);
        System.out.println(merItems);
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

        User user = getUserInfo();
        dealBookIntegral(resMap, entry_ids, user, merItems);
        //查询默认收货地址
        Map<String, Object> shipAddress = userDao.getDefaultShipAddress(user.getuId());
        resMap.put("shipaddress", shipAddress == null ? "" : shipAddress);
        return new Result().success(resMap);
    }

    private void dealBookIntegral(Map<String, Object> resMap, String entry_ids, User user, List<Map<String, Object>> merItems) {
        resMap.put("entry_ids", entry_ids);
        resMap.put("merItems", merItems);
        //查询本次订单的总费用
        Map<String, Object> fee = bookDao.getAllFee(PublicUtil.toListByIds(entry_ids));
        resMap.put("fee", fee); //产品总费用

        //用户当前可用总积分
        resMap.put("totalIntegral", user.getuIntegral()); //用户总积分
        //如果使用积分那么对应金额。
        List<String> rateInfos = bookDao.getRates();
        if (rateInfos == null || rateInfos.size() < 2) {
            log.error("RMB和积分比例表数据出现错误！");
//            return new Result().fail("服务器内部数据异常！");
            throw new ResultException("服务器内部数据异常！");
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
    }

    /**
     * 确认订单
     *
     * @return
     */
    @Override
    public Result confirmBook(Map<String, Object> params) throws Exception {
        User user = getUserInfo();
        //校验用户是否绑定了MMD, 第一版本必须绑定MMD; 测试账户不做绑定
        if(user.getuMmdNo() == null && Objects.equals("13788957291", user.getuPhone())) {
            return new Result().fail("请先绑定MMD账户！");
        }
        String entry_ids = String.valueOf(params.get("entry_ids"));
        List<String> entryIds = PublicUtil.toListByIds(entry_ids);
        //执行减库存操作。
        dealProdRepertory(entryIds, params);
        //处理用户积分信息,
        dealUserIntegral(user, params, entryIds);

        //生成主订单信息。
        bookDao.createBook(user.getuId(), params); //创建主订单表

        //生成订单项详细
        Map<String, Object> bookInfos = (Map<String, Object>) params.get("bookInfo");
        System.out.println(bookInfos);
        for (Map.Entry<String, Object> entry : bookInfos.entrySet()) {
            //生成订单编号
            List<Map<String, Object>> prodInfo = (List<Map<String, Object>>) entry.getValue();
            //创建订单; 一个商家一个订单。
            Map<String, Object> book = createBook(prodInfo, params);
            //创建订单ITEM信息，
            bookDao.createBookItem(book, user.getuId(), String.valueOf(params.get("address_id")), String.valueOf(params.get("bookid")));
            //添加产品项信息
            bookDao.createProdInfo(String.valueOf(book.get("bid")), prodInfo);
        }
        //删除购物车中的产品
        bookDao.delShopCat(entryIds);
        //更新购物车中的总价
        bookDao.updateCatPrice(user.getuId());

        //返回本次应支付的总金额
        Map<String, Object> res = new HashMap<>();
        res.put("bid", params.get("bookid"));
        res.put("rmbprice", params.get("rmbprice"));
        res.put("mmdprice", params.get("mmdprice"));
        redisUtils.setUserInfo(user); //当数据库没有错误的时候，那么更新用户信息到redis中。
        return new Result().success(res);
    }

    /**
     * 创建订单
     */
    private Map<String, Object> createBook(List<Map<String, Object>> prodInfo, Map<String, Object> params) {
        Map<String, Object> res = new HashMap<>();
        Double totalrmbprice = 0D;
        Double totalmmdprice = 0D;
        for (Map<String, Object> map : prodInfo) {
            totalrmbprice = totalrmbprice +
                    Double.valueOf(String.valueOf(map.get("price")))
                            * Double.valueOf(String.valueOf(map.get("num")));
            totalmmdprice = totalmmdprice +
                    Double.valueOf(String.valueOf(map.get("mmdprice")))
                            * Double.valueOf(String.valueOf(map.get("num")));
        }
        res.put("totalrmbprice", totalrmbprice);
        res.put("totalmmdprice", totalmmdprice);
        res.put("bookno", PublicUtil.getBookNo());
        //获取卖家ID
        String mer_id = String.valueOf(prodInfo.get(0).get("mer_id"));
        res.put("mer_id", mer_id);
        //处理卖家留言信息
        if(!PublicUtil.isEmptyObj(params.get("leaveword"))) {
            try {
                List<Map<String, Object>> mapList = (List<Map<String, Object>>) params.get("leaveword");
                for (Map<String, Object> map : mapList) {
                    if(Objects.equals(mer_id, String.valueOf(map.get("mer_id")))){
                        res.put("word", map.get("word"));
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ResultException("参数LeaveWord格式不正确!");
            }
        }
        return res;
    }


    /**
     * 处理产品库存操作
     */
    private void dealProdRepertory(List<String> entryIds, Map<String, Object> params) {
        List<Map<String, Object>> buySkuInfo = bookDao.getBuySkuInfo(entryIds);
        if (buySkuInfo == null || buySkuInfo.size() < entryIds.size()) {
            throw new ResultException("订单中有无效的商品！");
        }
        //判断购买的产品是否具有失效的产品
        for (Map<String, Object> map : buySkuInfo) {
            if ("2".equals(map.get("prod_state"))) {
                throw new ResultException("产品库存不足!");
            } else if ("3".equals(map.get("prod_state"))) {
                throw new ResultException("产品已失效!");
            }
        }
        Map<String, Object> bookInfo = new HashMap<>();
        List<Map<String, Object>> merInfo = null;
        for (Map<String, Object> skuInfo : buySkuInfo) {
            if (bookInfo.get(String.valueOf(skuInfo.get("mer_id"))) != null) {
                merInfo = (List<Map<String, Object>>) bookInfo.get(String.valueOf(skuInfo.get("mer_id")));
                merInfo.add(skuInfo);
            } else {
                merInfo = new ArrayList<>();
                merInfo.add(skuInfo);
            }
            bookInfo.put(String.valueOf(skuInfo.get("mer_id")), merInfo);
        }
        params.put("bookInfo", bookInfo);
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
        //这是本次用户可获得的积分
        params.put("getInteger", getInteger);

//        if (getInteger != 0) {
//            //那么记录本次积分获得情况
//            bookDao.insertUseIntegral(user.getuId(), getInteger, 2);
//        }

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
            //更新用户的最终剩余积分
            //本次用户获得的积分，需要在支付之后才能获取
            userDao.updateUserInteger(user.getuId(), usableIntegral.intValue());

            //记录本次积分消耗情况
            bookDao.insertUseIntegral(user.getuId(), usableIntegral.intValue(), 1);

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
    }


    /**
     * 查询全部订单
     * 订单信息
     * * 按钮信息：  1：取消订单 2：付款 3：查物流  4：提醒发货 5：查看物流 6：确认收货 7：删除订单 8：评价
     * 1:待付款时--按钮 （1,2）
     * 2:待发货 -- （4）
     * 3:待收货-- （5，6）
     * 4:待评价-- （5，7，（8--尚未评价才有））
     * @param state
     */
    @Override
    public Result getAllBookList(String state) {
        User user = getUserInfo();
        List<Map<String, Object>> list;
        list = bookDao.getAllBookList(user.getuId(), state);
        //设置订单按钮状态
        for (Map<String, Object> map : list) {
            String bookState = String.valueOf(map.get("state"));
            if ("1".equals(bookState)) { //待付款
                map.put("btnInfo", createBtn(new String[]{"1", "2"}));
            } else if ("2".equals(bookState)) { //待发货
                map.put("btnInfo", createBtn(new String[]{"4"}));
            } else if ("3".equals(bookState)) {
                map.put("btnInfo", createBtn(new String[]{"5", "6"}));
            } else if ("4".equals(bookState)) {
                if ("1".equals(map.get("isevaluate"))) {
                    //如果已经评价，那么就不在让继续评价
                    map.put("btnInfo", createBtn(new String[]{"5", "7", "8"}));
                } else {
                    map.put("btnInfo", createBtn(new String[]{"5", "7"}));
                }
            }
        }
        return new Result().success(new ResultPage<>(list));
    }

    private List<Map<String, Object>> createBtn(String[] ids) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String id : ids) {
            Map<String, Object> map = new HashMap<>();
            String btnName = "";
            switch (id) {
                case "1":
                    btnName = "取消订单";
                    break;
                case "2":
                    btnName = "付款";
                    break;
                case "3":
                    btnName = "查物流";
                    break;
                case "4":
                    btnName = "提醒发货";
                    break;
                case "5":
                    btnName = "查看物流";
                    break;
                case "6":
                    btnName = "确认收货";
                    break;
                case "7":
                    btnName = "删除订单";
                    break;
                case "8":
                    btnName = "评价";
                    break;
            }
            map.put("operId", id);
            map.put("operName", btnName);
            list.add(map);
        }
        return list;
    }

    @Override
    public Result getBookDetail(String bid) {
        return new Result().success(bookDao.getBookDetail(bid));
    }

    private User getUserInfo() {
        return (User) request.getAttribute("user");
    }
}
