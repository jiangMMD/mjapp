package com.mmd.mjapp.service.impl;

import com.github.pagehelper.PageHelper;
import com.mmd.mjapp.config.RedisUtils;
import com.mmd.mjapp.constant.RedisKey;
import com.mmd.mjapp.dao.ProductDao;
import com.mmd.mjapp.model.Productsinfo;
import com.mmd.mjapp.model.User;
import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;
import com.mmd.mjapp.pjo.ResultPage;
import com.mmd.mjapp.service.ProductService;
import com.mmd.mjapp.utils.PublicUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Result queryProdListByStore(Page page, String id) {
        PageHelper.startPage(page);
        List<Map<String, Object>> list = productDao.queryProdListByStore(id);
        return new Result().success(new ResultPage<>(list));
    }

    /**
     * 查询产品详细
     *
     * @param pid
     * @return
     */
    @Override
    public Result queryProdDetail(String pid) {
        Productsinfo product = productDao.queryProdDetail(pid);
        if (product == null) {
            return new Result().fail("没有找到该产品信息!");
        }
        product.dealCarousals();
        product.dealServices();
        product.dealDetailimgs();

        //加载该产品的所有属性信息
        List<Map<String, Object>> list = productDao.getProdPropInfo(pid);
        product.setPropList(list);

        //记录浏览信息
        User user = getUserInfo();
        productDao.insertBrows(pid, user.getuId(), PublicUtil.getIpAddr(request));
        return new Result().success(product);
    }

    @Override
    public Result collectProd(String pid) {
        User user = getUserInfo();
        productDao.collectProd(pid, user.getuId());
        return new Result().success();
    }

    @Override
    public Result delCollectProd(String ids) {
        productDao.delCollectProd(PublicUtil.toListByIds(ids));
        return new Result().success();
    }

    @Override
    public Result getBrowProdList() {
        User user = getUserInfo();
        //按日期分组， 查询最近30天有数据的分组
        List<Map<String, Object>> list = productDao.getBrowDateByGorupVisDate(user.getuId());
        //最多展示10条数据
        if (list == null || list.size() == 0) {
            return new Result().success(new ArrayList<>());
        }
        List<String> dateList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            if ((Long) map.get("ct") > 0) {
                if (dateList.size() > 10) {
                    break;
                } else {
                    dateList.add((String) map.get("vs_day"));
                }
            }
        }
        if (dateList.size() == 0) {
            return new Result().success(new ArrayList<>());
        }
        return new Result().success(productDao.getBrowList(dateList, user.getuId()));
    }

    /**
     * 加载常用评语
     *
     * @param param
     * @return
     */
    @Override
    public Result getSightWord(Map<String, Object> param) throws Exception {
        String type = String.valueOf(param.get("type"));
        if ("0".equals(type)) {
            Object object = redisUtils.getObj(RedisKey.SIGHTWORD_ON);
            if (object == null) {
                List<Map<String, Object>> list = productDao.getSigthWord(type);
                if (list != null && list.size() > 0) {
                    redisUtils.setTempObj(RedisKey.SIGHTWORD_ON, list);
                }
                return new Result().success(list);
            }
            List<Map<String, Object>> list = (List<Map<String, Object>>) object;
            return new Result().success(list);
        } else if("1".equals(type)) {
            Object object = redisUtils.getObj(RedisKey.SIGHTWORD_OFF);
            if (object == null) {
                List<Map<String, Object>> list = productDao.getSigthWord(type);
                if (list != null && list.size() > 0) {
                    redisUtils.setTempObj(RedisKey.SIGHTWORD_OFF, list);
                }
                return new Result().success(list);
            }
            List<Map<String, Object>> list = (List<Map<String, Object>>) object;
            return new Result().success(list);
        }else{
            return new Result().fail("Type值错误！");
        }

    }

    /**
     * 产品评价信息
     */
    @Override
    public Result getProdEvaluate(Page page, String pid) {
        PageHelper.startPage(page);
        List<Map<String, Object>> list = productDao.getProdEvaluate(pid);
        return new Result().success(new ResultPage<>(list));
    }

    /**
     * 保存评价
     *
     * @param params
     * @return
     */
    @Override
    public Result saveEvaluate(Map<String, Object> params) {
        User user = getUserInfo();
        Map<String, Object> resMap = productDao.getOldEvaluate(String.valueOf(params.get("bid")));
        if (resMap != null) {
            //更新产品的总评论
            productDao.updateProdOldEvel(resMap);
        }
        //新的评分
        Integer starlevel = (Integer) params.get("starlevel");
        //新增评论信息
        productDao.saveEvaluate(params, user.getuId());
        productDao.updateProdEvel(starlevel, String.valueOf(params.get("bid")), String.valueOf(params.get("id")));
        productDao.updateAvgEvaluate(String.valueOf(params.get("bid")));
        return new Result().success();
    }

    /**
     * 查询收藏的产品
     *
     * @param page
     */
    @Override
    public Result getCollections(Page page) {
        User user = getUserInfo();
        PageHelper.startPage(page);
        List<Map<String, Object>> list = productDao.getCollections(user.getuId());
        return new Result().success(new ResultPage<>(list));
    }

    @Override
    public Result getAppraise(Page page, String pid) {
        PageHelper.startPage(page);
        List<Map<String, Object>> list = productDao.getAppraise(pid);
        return new Result().success(new ResultPage<>(list));
    }

    @Override
    public Result collectMerchant(Map<String, Object> params) {
        User user = getUserInfo();
        String mer_id = String.valueOf(params.get("mer_id"));
        productDao.collectMerchant(mer_id, user.getuId());
        return new Result().success();
    }

    @Override
    public Result delCollectMerchant(Map<String, Object> params) {
        String ids = String.valueOf(params.get("ids"));
        productDao.delCollectMerchant(PublicUtil.toListByIds(ids));
        return new Result().success();
    }

    @Override
    public Result getCollectMerchantList(Page page) {
        PageHelper.startPage(page);
        List<Map<String, Object>> list = productDao.getCollectMerchantList(getUserInfo().getuId());
        return new Result().success(new ResultPage<>(list));
    }

    //加载用户信息
    private User getUserInfo() {
        return (User) request.getAttribute("user");
    }

}
