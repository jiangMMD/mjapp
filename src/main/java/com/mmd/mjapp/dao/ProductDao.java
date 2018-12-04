package com.mmd.mjapp.dao;

import com.mmd.mjapp.model.Productsinfo;
import com.mmd.mjapp.pjo.Result;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ProductDao {

    //查询指定商家中的产品数量
    List<Map<String, Object>> queryProdListByStore(@Param("id") String id);

    Productsinfo queryProdDetail(@Param("pid") String pid);

    List<Map<String,Object>> getProdPropInfo(@Param("pid") String pid);

    List<Map<String,Object>> queryProdListByKey(@Param("key") String key);

    void insertBrows(@Param("pid") String pid, @Param("uid") Long uid, @Param("ipAddr") String ipAddr);

    void collectProd(@Param("pid") String pid, @Param("uid") Long uid);

    void delCollectProd(@Param("ids") List<String> ids);

    List<Map<String,Object>> getBrowDateByGorupVisDate(@Param("uid") Long uid);

    List<Map<String, Object>> getBrowList(@Param("dateList") List<String> dateList, @Param("uid") Long uid);

    List<Map<String, Object>> getSigthWord();
}
