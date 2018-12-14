package com.mmd.mjapp.dao;


import com.mmd.mjapp.model.OperInfo;
import com.mmd.mjapp.model.User;
import com.sun.corba.se.spi.ior.ObjectKey;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserDao {

    void regist(User user);

    User getUserWithToken(@Param("tokenVal") String tokenVal);

    int checkHasCus(@Param("phone") String phone);

    void updateUserBaseInfo(User user);

    Map<String,Object> getUserRegIntegral();

    User getPhoneByDb(@Param("phone") String phone);

    void relevancyDevice(OperInfo operInfo);

    void saveHeadImg(User user);

    void updateHeadImg(@Param("url") String url, @Param("uid") Long uid);

    void saveMMDInfo(@Param("params") Map<String, Object> params, @Param("uid") Long uid);

    void updateUserInteger(@Param("uid") Long uid, @Param("getInteger") Integer getInteger);

    Map<String, Object> getDefaultShipAddress(@Param("uid") Long uid);
}
