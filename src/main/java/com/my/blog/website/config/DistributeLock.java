package com.my.blog.website.config;

import com.my.blog.website.utils.KeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class DistributeLock {


    private static final String LOCK_VALUE = "lock_value";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取锁
     * @param key
     * @param expireTime
     * @return
     */
    public Boolean lock(String key, Long expireTime){
        Boolean success;
        String fkey = KeyUtils.initKey(key);
        try {
            success = stringRedisTemplate.opsForValue().setIfAbsent(fkey, LOCK_VALUE, expireTime, TimeUnit.MILLISECONDS);
        } catch (Exception exception) {
            log.error("DistributeLock lock is error, key:{}, message:{}", fkey, exception.getMessage());
            return false;
        }
        return success;
    }

    /**
     * 释放锁
     * @param key
     * @return
     */
    public Boolean releaseKey(String key){
        String fkey = KeyUtils.initKey(key);
        log.info("release lock success, fKey = {}", fkey);
        return stringRedisTemplate.delete(fkey);
    }

    /**
     * 是否有锁（当前key）
     * @param key
     * @return
     */
    public Boolean isLocked(String key){
        return stringRedisTemplate.opsForValue().get(KeyUtils.initKey(key)) != null;
    }
}
