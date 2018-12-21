package com.mmd.mjapp.dao;

import org.apache.ibatis.annotations.Param;

public interface PayDao {

    void insertPayInfo(@Param("bid") String bid, @Param("price") Double price, @Param("paytype") Integer paytype, @Param("btype") Integer btype, @Param("state") int state);
}
