package com.mmd.mjapp.pjo;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * 存入redis中的待支付订单信息
 */
@Data
public class BookModel {

    private String bid;
    private Date date;

    public BookModel(String bid) {
        this.bid = bid;
        this.date = new Date();
    }
}
