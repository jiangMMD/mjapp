package com.mmd.mjapp.service;

import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;

import java.util.List;
import java.util.Map;

public interface HomeService {

    List<Map<String, Object>> getClassify() throws Exception;

    List<Map<String,Object>> getCarousel() throws Exception;

    Result getBrand(Page page) throws Exception;

    Result getHotWord() throws Exception;

    Result getProdByKey(Page page, String key);

    Result hasNews();

    Result getBaseMsg();

    Result getSysMsgList(Page page, String type);

    Result getMsgDetail(String id);
}
