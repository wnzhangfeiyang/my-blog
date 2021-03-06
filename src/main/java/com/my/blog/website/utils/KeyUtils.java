package com.my.blog.website.utils;

import com.my.blog.website.exception.TipException;
import org.apache.commons.lang3.StringUtils;

public class KeyUtils {
    private static final String fkey_pre = "::";

    private static final String SYNC_SCHED_LOCK_KEY = "SYNC_SCHED_LOCK_KEY";

    public static String initKey(String key){
        if(StringUtils.isBlank(key)){
            throw new TipException("key不能为空，则初始化key失败");
        }
        return SYNC_SCHED_LOCK_KEY + fkey_pre + key;
    }
}
