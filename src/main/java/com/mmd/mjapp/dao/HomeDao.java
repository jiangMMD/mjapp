package com.mmd.mjapp.dao;


import com.mmd.mjapp.model.Merchant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HomeDao {

    List<Map<String,Object>> getCliassify();

    List<Map<String,Object>> getCarousel();

    List<Merchant> getBrand();

    List<Map<String,Object>> getHotWord();

    int hasNews(@Param("uid") Long uid);

    Map<String,Object> getSystemMsg();

    Map<String,Object> getBasicMsg(@Param("uid") Long uid);

    Map<String,Object> getLogisticsMsg(@Param("uid") Long uid);

    List<Map<String,Object>> getSysMsgList(@Param("type") String type);

    List<Map<String, Object>> getMsgDetail(@Param("id") String id);

    List<Map<String,Object>> getProdListBycate(@Param("id") String id);

    List<Map<String,Object>> getProdByMer(@Param("mer_id") String mer_id);
}
