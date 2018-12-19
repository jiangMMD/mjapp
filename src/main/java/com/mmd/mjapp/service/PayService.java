package com.mmd.mjapp.service;

import com.mmd.mjapp.pjo.Result;

import java.util.Map;

public interface PayService {

    Result prodPayWithMMD(Map<String, Object> param) throws Exception;
}
