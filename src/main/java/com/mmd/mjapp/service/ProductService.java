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

    Result getSightWord() throws Exception;

    Result getProdEvaluate(Page page, String pid);
}
