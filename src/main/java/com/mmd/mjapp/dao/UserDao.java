package com.mmd.mjapp.dao;


import com.mmd.mjapp.model.OperInfo;
import com.mmd.mjapp.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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

    void addShipAddress(@Param("param") Map<String, Object> param, @Param("uid") Long uid);

    void updateShipAddress(@Param("param") Map<String, Object> param);

    void setDefaultAddress(@Param("id") String id);

    void setNoDefaulAddress(@Param("id") String id);

    List<Map<String, Object>> queryShipAddress(@Param("uid") Long uid);

    void delShipAddress(@Param("ids") List<String> ids);

    int getShipAddress(@Param("uid") Long uid, @Param("id") String id);

    void cancelDefaultShipAddress(@Param("uid") Long uid);
}
