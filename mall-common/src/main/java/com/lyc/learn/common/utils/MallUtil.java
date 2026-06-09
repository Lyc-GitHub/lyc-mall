package com.lyc.learn.common.utils;

import java.util.Collection;

public class MallUtil {
    
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }
    
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }
}
