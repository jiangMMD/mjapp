package com.mmd.mjapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/10/10.
 */
@Component
public class RedisClient {

    @Autowired
    private RedisTemplate redisTemplate;

    public void set(String key, Object value, int seconds) throws Exception {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    public void set(String key, Object value) throws Exception {
        redisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) throws Exception {
        return (String) redisTemplate.opsForValue().get(key);
    }


    public Object getObj(String key) throws Exception {
        return redisTemplate.opsForValue().get(key);
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
