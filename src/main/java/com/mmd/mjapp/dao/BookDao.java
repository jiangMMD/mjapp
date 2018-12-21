package com.mmd.mjapp.dao;

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

    void createBook(@Param("uid") Long uid, @Param("param") Map<String, Object> param);

    void createBookItem(@Param("book") Map<String, Object> book, @Param("uid") Long uid, @Param("address_id") String address_id, @Param("bookid") String bookid);

    List<Map<String,Object>> getAllBookList(@Param("uid") Long uid, @Param("state") String state);

    Map<String, Object> getBookDetail(@Param("bid") String bid);

    void createProdInfo(@Param("bid") String bid, @Param("prodList") List<Map<String, Object>> prodList);

    Map<String, Object> getBookItemsByPickId(@Param("bid") String bid);

    List<Map<String,Object>> getBookItemsByBids(@Param("bookItems") List<String> bookItems);

    void updateBookToPayMMDSuccess(@Param("bid") String bid, @Param("payprice") Double payprice, @Param("payWay") String payWay);

    void updateBookToPayMMDSuccessByBids(@Param("bookItems") List<String> bookItems, @Param("totalDoubleMMDPrice") Double totalDoubleMMDPrice, @Param("payway") int payway);

    int cancelBook(@Param("param") Map<String, Object> param);

    void releaseProdSkuStock(@Param("map") Map<String, Object> map);

    void resetProdStock(@Param("param") Map<String, Object> param);

    List<Map<String,Object>> queryBuyProdSkuNum(@Param("param") Map<String, Object> param);

    Map<String,Object> getBookState(@Param("param") Map<String, Object> param);

    void delBook(@Param("bid") String bid);

    void insertBookOperLog(@Param("bid") String bid, @Param("type") int type, @Param("operdesc") String operdesc);

    void cfmBook(@Param("bid") String bid);

    List<String> getAllBookByPickId(@Param("bid") String bid);

    Map<String, String> getBookNoByBid(@Param("bid") String bid);

    void createOffBook(@Param("param") Map<String, Object> param);
}
