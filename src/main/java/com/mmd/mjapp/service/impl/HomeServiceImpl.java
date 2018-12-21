package com.mmd.mjapp.service.impl;


import com.github.pagehelper.PageHelper;
import com.mmd.mjapp.config.RedisUtils;
import com.mmd.mjapp.constant.RedisKey;
import com.mmd.mjapp.dao.HomeDao;
import com.mmd.mjapp.dao.ProductDao;
import com.mmd.mjapp.model.Merchant;
import com.mmd.mjapp.model.User;
import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.pjo.ResultPage;
import com.mmd.mjapp.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HomeServiceImpl implements HomeService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HomeDao homeDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private HttpServletRequest request;

    @Override
    public List<Map<String, Object>> getClassify() throws Exception {
        //从redis加载该热点信息， 如果没有，那么就加载数据库
        Object classifys = redisUtils.getObj(RedisKey.CLASSIFY);
        if (classifys == null) {
            //那么从数据库加载数据
            List<Map<String, Object>> list = homeDao.getCliassify();
            redisUtils.setTempObj(RedisKey.CLASSIFY, list);
            return list;
        } else {
            return (List<Map<String, Object>>) classifys;
        }
    }

    @Override
    public List<Map<String, Object>> getCarousel() throws Exception {
        //从redis中加载轮播图热点信息。
        Object carousel = redisUtils.getObj(RedisKey.CAROUSAL);
        if (carousel == null) {
            //从数据库中加载数据
            List<Map<String, Object>> list = homeDao.getCarousel();
            redisUtils.setTempObj(RedisKey.CAROUSAL, list);
            return list;
        }
        return (List<Map<String, Object>>) carousel;
    }

    /**
     * 加载品牌信息
     *
     * @return
     */
    @Override
    public Result getBrand(Page page) throws Exception {
//        Object brand = redisUtils.getObj(RedisKey.BRAND);
//        if (brand == null) {
            PageHelper.startPage(page);
            List<Merchant> list = homeDao.getBrand();
//            redisUtils.setTempObj(RedisKey.BRAND, list);
            return new Result().success(new ResultPage<>(list));
//        }
    }

    @Override
    public Result getHotWord() throws Exception {
        Object hotWordList = redisUtils.getObj(RedisKey.HOTWORD);
        if(hotWordList == null) {
            //从数据库中加载
            List<Map<String, Object>> list = homeDao.getHotWord();
            redisUtils.setTempObj(RedisKey.HOTWORD, list);
            return new Result().success(list);
        }
        List<Map<String, Object>> hots = (List<Map<String, Object>>) hotWordList;
        return new Result().success(hots);
    }

    @Override
    public Result getProdByKey(Page page, String key) {
        PageHelper.startPage(page);
        List<Map<String, Object>> list = productDao.queryProdListByKey(key);
        return new Result().success(new ResultPage<>(list));
    }

    //获取用户消息
    private User getUserInfo() {
        return (User) request.getAttribute("user");
    }

    @Override
    public Result hasNews() {
        //获取用户消息
        User user = getUserInfo();
        int count = homeDao.hasNews(user.getuId());
        Map<String, Object> res = new HashMap<>();
        if(count > 0) {
            res.put("isnews", true);
            return new Result().success(res);
        }
        res.put("isnews", false);
        return new Result().success(res);
    }

    @Override
    public Result getBaseMsg() {
        User user = getUserInfo();
        //加载系统消息第一个信息
        Map<String, Object> sysMsg = homeDao.getSystemMsg();
        //加载消息通知
        Map<String, Object> basicMsg = homeDao.getBasicMsg(user.getuId());
        //加载物流消息
        Map<String, Object> logstMsg =  homeDao.getLogisticsMsg(user.getuId());
        Map<String, Object> res = new HashMap<>();
        res.put("sysMsg", sysMsg);
        res.put("basicMsg", basicMsg);
        res.put("logstMsg", logstMsg);
        return new Result().success(res);
    }

    @Override
    public Result getSysMsgList(Page page, String type) {
        PageHelper.startPage(page);
        List<Map<String, Object>> sysList = homeDao.getSysMsgList(type);
        return new Result().success(new ResultPage<>(sysList));
    }

    /**
     * 查询消息详细
     * @return
     */
    @Override
    public Result getMsgDetail(String id) {
        return new Result().success(homeDao.getMsgDetail(id));
    }

    /**
     * 根据产品类目查询产品信息
     */
    @Override
    public Result getProdListBycate(Page page, String id) {
        PageHelper.startPage(page);
        List<Map<String, Object>> list = homeDao.getProdListBycate(id);
        return new Result().success(new ResultPage<>(list));
    }

    @Override
    public Result getProdByMer(Page page, String mer_id) {
        PageHelper.startPage(page);
        Map<String, Object> merProds = homeDao.getProdByMer(mer_id);
        return new Result().success(merProds);
    }
}
