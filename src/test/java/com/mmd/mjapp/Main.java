package com.mmd.mjapp;

import com.mmd.mjapp.utils.HttpClientUtil;
import com.mmd.mjapp.utils.PropertyLoad;
import com.mmd.mjapp.utils.PublicUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        //归属公司查询
        Map<String, Object> reqPar = new HashMap<>();
        reqPar.put("key", PropertyLoad.getProperty("KD100.key"));
        reqPar.put("num","3388704947257");
        String res = HttpClientUtil.sendPostRequest(reqPar, "http://www.kuaidi100.com/autonumber/auto");
        System.out.println(res);
    }


    @Test
    public void test() {


    }
}
