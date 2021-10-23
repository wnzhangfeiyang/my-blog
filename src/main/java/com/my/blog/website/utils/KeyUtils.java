package com.my.blog.website.utils;

import com.my.blog.website.exception.TipException;
import org.apache.commons.lang3.StringUtils;

public class KeyUtils {
    private static final String fkey_pre = "::";

    private static final String SYNC_SCHED_LOCK_KEY = "SYNC_SCHED_LOCK_KEY";

    public static String initKey(String key){
        return SYNC_SCHED_LOCK_KEY + fkey_pre + key;
    }
}
