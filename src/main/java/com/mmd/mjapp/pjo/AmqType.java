package com.mmd.mjapp.pjo;

public enum AmqType {

    type1("1", "申请产品"), type2("2", "租赁申请"), type3("3", "客户还款");

    String code;
    String name;

    AmqType(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
