package com.mmd.mjapp.service;

import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface ProductService {

    Result queryProdListByStore(Page page, String id);

    Result queryProdDetail(@Param("pid") String pid);

    Result collectProd(String pid);

    Result delCollectProd(String ids);

    Result getBrowProdList();

    Result getSightWord(Map<String, Object> param) throws Exception;

    Result getProdEvaluate(Page page, String pid);

    Result saveEvaluate(Map<String, Object> params);

    Result getCollections(Page page);

    Result getAppraise(Page page, String pid);

    Result collectMerchant(Map<String, Object> params);

    Result delCollectMerchant(Map<String, Object> params);

    Result getCollectMerchantList(Page page);
}
