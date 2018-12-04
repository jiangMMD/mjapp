package com.mmd.mjapp.service.impl;

import com.fasterxml.jackson.annotation.JsonView;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
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
        if(dateList.size() == 0) {
            return new Result().success(new ArrayList<>());
        }
        return new Result().success(productDao.getBrowList(dateList, user.getuId()));
    }

    /**
     * 加载常用评语
     * @return
     */
    @Override
    public Result getSightWord() throws Exception {
        Object object = redisUtils.get(RedisKey.SIGHTWORD);
        if(object == null) {
            List<Map<String, Object>> list = productDao.getSigthWord();
            if(list != null && list.size() > 0) {
                redisUtils.setTempObj(RedisKey.SIGHTWORD, list);
            }
            return new Result().success();
        }
        return new Result().success(object);
    }

    @Override
    public Result getProdEvaluate(Page page, String pid) {

        return null;
    }

    //加载用户信息
    private User getUserInfo() {
        return (User) request.getAttribute("user");
    }



}
