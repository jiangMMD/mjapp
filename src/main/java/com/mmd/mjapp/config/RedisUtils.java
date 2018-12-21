package com.mmd.mjapp.config;

import com.mmd.mjapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate redisTemplate;

    public Set<String> keys(String reg) {
        return redisTemplate.keys(reg);
    }

    /**
     * 设置用户相关信息
     *
     * @param userInfo
     */
    public void setUserInfo(User userInfo) {
        Long uid = userInfo.getuId();
        redisTemplate.opsForValue().set("user:" + uid, userInfo);
        //默认redis中保存用户数据7天
        redisTemplate.expire("user:" + uid, 7 * 24 * 3600, TimeUnit.SECONDS);
    }

    public void setUserInfo(User userInfo, Long timeout) {
        Long uid = userInfo.getuId();
        redisTemplate.opsForValue().set("user:" + uid, userInfo);
        //默认redis中保存用户数据7天
        redisTemplate.expire("user:" + uid, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取用户信息
     */
    public User getUserInfo(String uid) {
        return (User) redisTemplate.opsForValue().get("user:" + uid);
    }


    public void set(String key, Object value, int seconds) throws Exception {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public void set(String key, Object value) throws Exception {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 在redis中仅保存10分钟。用于加载信息
     * @throws Exception
     */
    public void setTempObj(String key, Object value) throws Exception {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, 60 * 10, TimeUnit.SECONDS);
    }

    public String get(String key) throws Exception {
        return (String) redisTemplate.opsForValue().get(key);
    }


    public Object getObj(String key) throws Exception {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteByKeys(Collection keys) throws Exception {
        redisTemplate.delete(keys);
    }

    public void deleteByKey(String key) throws Exception {
        redisTemplate.delete(key);
    }

    public void addList(Object key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public void addSet(Object key, Object... value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public void isExistWithSet(Object key, Object value) {
        redisTemplate.opsForSet().isMember(key, value);
    }

    public void deleteWithSet(Object key, Object... value) {
        redisTemplate.opsForSet().remove(key, value);
    }
}
