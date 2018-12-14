package com.mmd.mjapp.dao;

import com.mmd.mjapp.pjo.Result;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface BookDao {

    void createShopCat(@Param("uid") Long uid);

    void addShopCat(@Param("sku_id") String sku_id, @Param("uid") Long uid);

    List<Map<String,Object>> getShopCat(@Param("uid") Long uid);

    void updateCatPrice(@Param("uid") Long uid);

    void updShopCatNum(@Param("entry_id") String entry_id, @Param("uid") Long uid, @Param("num") Integer num);

    void delShopCat(@Param("list") List<String> list);

    Map<String,Object> getAllFee(@Param("entry_ids") List<String> entr_ids);

    List<Map<String,Object>> getProdInfo(@Param("entry_ids") List<String> entry_ids);

    List<String> getRates();

    Map<String, Object> getBuyIntegral();

    void insertUseIntegral(@Param("uid") Long uid, @Param("usableIntegral") Integer usableIntegral, @Param("type") int type);

    List<Map<String,Object>> getBuySkuInfo(@Param("entryIds") List<String> entryIds);

    int getSkuStore(@Param("sku_id") String sku_id);

    void updateSkuStore(@Param("map") Map<String, Object> map);

    void reduceStore(@Param("map") Map<String, Object> map);

    void createBook(@Param("uid") Long uid, @Param("bno") String bno, @Param("param") Map<String, Object> param);

    void createBookItem(@Param("prodInfo") Map<String, Object> prodInfo, @Param("uid") Long uid, @Param("address_id") String address_id, @Param("bid") String bid);

    List<Map<String,Object>> getAllBookList(@Param("uid") Long uid, @Param("state") String state);

    Map<String, Object> getBookDetail(@Param("bid") String bid);
}
