package com.ebang.openapi.utils;

/**
 * @Author: zyz
 * @Date: 2025/3/11 21:09
 * @Description:雪花算法工具类
 **/
public class SnowflakeIdGenerator {

    // 起始时间戳，可自定义
    private static final long startTimeStamp = 1609459200000L;

    // 数据中心 ID 所占位数
    private static final long dataCenterIdBits = 5L;
    // 机器 ID 所占位数
    private static final long workerIdBits = 5L;
    // 序列号所占位数
    private static final long sequenceBits = 12L;

    // 数据中心 ID 最大值
    private static final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
    // 机器 ID 最大值
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // 序列号最大值
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 机器 ID 向左移位数
    private static final long workerIdShift = sequenceBits;
    // 数据中心 ID 向左移位数
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;
    // 时间戳向左移位数
    private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    // 数据中心 ID
    private static final long dataCenterId = 1L;
    // 机器 ID
    private static final long workerId = 1L;
    // 序列号
    private static long sequence = 0L;
    // 上一次生成 ID 的时间戳
    private static long lastTimestamp = -1L;

    static {
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException("Data center ID can't be greater than " + maxDataCenterId + " or less than 0");
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Worker ID can't be greater than " + maxWorkerId + " or less than 0");
        }
    }

    public static synchronized long getId() {
        long currentTimestamp = System.currentTimeMillis();

        // 处理时钟回拨问题
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - currentTimestamp) + " milliseconds");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 当前毫秒内序列号用完，等待下一毫秒
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置为 0
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - startTimeStamp) << timestampLeftShift) |
                (dataCenterId << dataCenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private static long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
