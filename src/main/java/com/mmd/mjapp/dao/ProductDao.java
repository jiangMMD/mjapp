package com.mmd.mjapp.dao;

import com.mmd.mjapp.model.Productsinfo;
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

    List<Map<String,Object>> getProdEvaluate(@Param("pid") String pid);

    void saveEvaluate(@Param("params") Map<String, Object> params, @Param("uid") Long uid);

    void updateProdEvel(@Param("starlevel") Integer starlevel, @Param("bid") String bid, @Param("id") String id);

    Map<String,Object> getSkuState(@Param("sku_id") String sku_id);

    Map<String,Object> getSkuStateByEntryId(@Param("entry_id") String entry_id);

    List<Map<String,Object>> getCollections(@Param("uid") Long uid);

    List<Map<String,Object>> getAppraise(@Param("pid") String pid);

    Map<String,Object> getOldEvaluate(@Param("bid") String bid);

    void updateAvgEvaluate(@Param("bid") String bid);

    void updateProdOldEvel(@Param("resMap") Map<String, Object> resMap);

    void collectMerchant(@Param("mer_id") String mer_id, @Param("uid") Long uid);

    void delCollectMerchant(@Param("ids") List<String> ids);

    List<Map<String, Object>> getCollectMerchantList(@Param("uid") Long uid);
}
