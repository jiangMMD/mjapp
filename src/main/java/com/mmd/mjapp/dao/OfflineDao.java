package com.mmd.mjapp.dao;

import com.mmd.mjapp.model.OfflineMerchant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface OfflineDao {
    List<Map<String, Object>> getProdByMer(@Param("mer_id") String mer_id);

    List<OfflineMerchant> getShop();

    List<Map<String, Object>> getEvaluate(String mer_id);

    List<Map<String, Object>> getDetailed(String mer_id);
}
