package com.mmd.mjapp.service;

import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;

import java.util.Map;

public interface BookService {
    Result addShopCat(String sku_id);

    Result getShopCat(Page page);

    Result addShopCatNum(String entry_id, Integer num);

    Result delShopCat(String entry_ids);

    Result shopBalance(String entry_ids);

    Result confirmBook(Map<String, Object> params) throws Exception;

    Result getAllBookList(Page page, String state);

    Result getBookDetail(String bid);

    Result cancelBook(Map<String, Object> param) throws Exception;

    Result delBook(Map<String, Object> param);

    Result cfmBook(Map<String, Object> param);

    Result remindDeliver(Map<String, Object> param) throws Exception;

    Result queryLogistics(Map<String, Object> param);

    Result offBookEvaluate(Map<String, Object> param);
}
