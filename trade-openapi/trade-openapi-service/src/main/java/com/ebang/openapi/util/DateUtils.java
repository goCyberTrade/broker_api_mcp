package com.ebang.openapi.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @Author: zyz
 * @Date: 2025/7/5 16:38
 * @Description:
 **/
public class DateUtils {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 将时间戳（毫秒）转换为指定时区偏移的OffsetDateTime
     * @param timestamp 毫秒时间戳
     * @param zoneId 时区
     * @return OffsetDateTime实例
     */
    public static OffsetDateTime timestampToOffsetDateTime(long timestamp, ZoneId zoneId) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return OffsetDateTime.ofInstant(instant, zoneId);
    }

    /**
     * 将时间戳（毫秒）转换为本地时区偏移的OffsetDateTime
     */
    public static OffsetDateTime timestampToLocalOffsetDateTime(Long timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestampToOffsetDateTime(timestamp, ZoneOffset.systemDefault());
    }

    /**
     * 将时间戳（秒）转换为OffsetDateTime（自动乘以1000转为毫秒）
     */
    public static OffsetDateTime secondsTimestampToOffsetDateTime(Long secondsTimestamp, ZoneOffset offset) {
        if (secondsTimestamp == null) {
            return null;
        }
        return timestampToOffsetDateTime(secondsTimestamp * 1000, offset);
    }

    /**
     * 将时间戳（秒）转换为OffsetDateTime（自动乘以1000转为毫秒）
     */
    public static OffsetDateTime dateStrToOffsetDateTime(String date, ZoneOffset offset) {
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
        return OffsetDateTime.of(dateTime, offset);
    }

    public static void main(String[] args) {
        // 示例：当前时间戳（毫秒）
        long timestamp = System.currentTimeMillis();

        // 转换为东八区时间
        OffsetDateTime beijingTime = timestampToOffsetDateTime(timestamp, ZoneOffset.of("+08:00"));
        System.out.println("东八区时间: " + beijingTime);  // 格式：2023-10-01T12:00:00+08:00

        // 转换为UTC时间
        OffsetDateTime utcTime = timestampToOffsetDateTime(timestamp, ZoneOffset.UTC);
        System.out.println("UTC时间: " + utcTime);       // 格式：2023-10-01T04:00:00Z

        System.out.println(dateStrToOffsetDateTime("2025-07-01 12:00:00", ZoneOffset.of("+08:00")));
    }
}
