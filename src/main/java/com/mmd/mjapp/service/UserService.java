package com.mmd.mjapp.service;

import com.mmd.mjapp.model.OperInfo;
import com.mmd.mjapp.pjo.Result;

import java.util.Map;

public interface UserService {

    Result sendRegistCode(OperInfo operInfo) throws Exception;

    Result regist(OperInfo operInfo) throws Exception;

    Result sendLoginCode(OperInfo operInfo) throws Exception;

    Result login(OperInfo operInfo) throws Exception;

    Result updateHeadImg(String url);

    Result revelanceMMD(Map<String, Object> params);

    Result queryMMDNum();

    Result addShipAddress(Map<String, Object> param);

    Result setDefaultAddress(String id);

    Result queryShipAddress();

    Result delShipAddress(String ids);

    Result sendPayCode(OperInfo operInfo) throws Exception;
}
