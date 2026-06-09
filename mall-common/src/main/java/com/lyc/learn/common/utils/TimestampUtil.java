package com.lyc.learn.common.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimestampUtil {
    
    public static long convertLocalTimeToTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toEpochSecond();
    }
}
