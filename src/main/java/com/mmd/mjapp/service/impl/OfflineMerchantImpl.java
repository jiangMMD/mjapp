package com.mmd.mjapp.service.impl;

import com.github.pagehelper.PageHelper;
import com.mmd.mjapp.config.RedisUtils;
import com.mmd.mjapp.constant.RedisKey;
import com.mmd.mjapp.dao.HomeDao;
import com.mmd.mjapp.dao.OfflineDao;
import com.mmd.mjapp.model.OfflineMerchant;
import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.pjo.ResultPage;
import com.mmd.mjapp.service.OfflineMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class OfflineMerchantImpl implements OfflineMerchantService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private OfflineDao offlineDao;

    @Autowired
    private HttpServletRequest request;

    //根据商铺查询产品
    @Override
    public Result getProdByMer(Page page, String mer_id) {
        PageHelper.startPage(page);
        List<Map<String, Object>> list = offlineDao.getProdByMer(mer_id);
        return new Result().success(new ResultPage<>(list));
    }

    /**
     * 加载线下商场首页
     */
    @Override
    public Result getShop(Page page) throws Exception {
        PageHelper.startPage(page);
        List<OfflineMerchant> list = offlineDao.getShop();
        return new Result().success(new ResultPage<>(list));
    }

    /**
     * 加载用户评论
     */
    @Override
    public Result getEvaluate(Page page, String mer_id) throws Exception {
        PageHelper.startPage(page);
        List<Map<String, Object>> list = offlineDao.getEvaluate(mer_id);
        return new Result().success(new ResultPage<>(list));
    }

    @Override
    public Result getDetailed(Page page, String mer_id) throws Exception {
        PageHelper.startPage(page);
        List<Map<String, Object>> list = offlineDao.getDetailed(mer_id);
        return new Result().success(new ResultPage<>(list));
    }


}
