package com.mmd.mjapp.service;

import com.mmd.mjapp.pjo.Page;
import com.mmd.mjapp.pjo.Result;

import java.util.List;
import java.util.Map;

public interface OfflineMerchantService {

    Result getProdByMer(Page page, String mer_id) throws Exception;

    Result getShop(Page page) throws Exception;

    Result getEvaluate(Page page, String mer_id) throws Exception;

    Result getDetailed(Page page, String mer_id) throws Exception;
}
