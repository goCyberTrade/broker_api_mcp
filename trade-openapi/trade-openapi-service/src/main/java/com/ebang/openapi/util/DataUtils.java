package com.ebang.openapi.util;

import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import io.modelcontextprotocol.server.McpAsyncServerExchange;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpServerSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * @Author: zyz
 * @Date: 2025/7/5 16:38
 * @Description:
 **/
@Slf4j
public class DataUtils {

    /**
     * Integer为空时判断数据
     */
    public static int toInt(Integer data, int defaultValue) {
        return data == null ? defaultValue : data;
    }

    /**
     * Bigdecimal转Double(为空直接返回)
     */
    public static Double decimalToDouble(BigDecimal data) {
        return data == null ? null : data.doubleValue();
    }

    /**
     * Bigdecimal转String(为空直接返回)
     */
    public static String decimalToString(BigDecimal data) {
        return data == null ? null : data.stripTrailingZeros().toString();
    }

    /**
     * Bigdecimal转String(为空直接返回默认值)
     */
    public static String decimalToStringDef(BigDecimal data, String defaultVal) {
        return data == null ? defaultVal : data.stripTrailingZeros().toString();
    }

    /**
     * 获取有值得Bigdecimal
     */
    public static BigDecimal getDecimalValue(BigDecimal data, BigDecimal defaultVal) {
        return data == null ? defaultVal : data;
    }

    /**
     * 判断dividend是否是divisor的整数倍
     */
    public static boolean isIntegerMultiple(BigDecimal dividend, BigDecimal divisor) {
        if (dividend == null || divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("参数不能为null，且除数不能为零");
        }

        // 执行除法运算，结果保留0位小数，使用DOWN舍入模式（直接截断小数部分）
        BigDecimal quotient = dividend.divide(divisor, 0, RoundingMode.HALF_DOWN.DOWN);

        // 计算余数：dividend - (quotient * divisor)
        BigDecimal remainder = dividend.subtract(quotient.multiply(divisor));

        // 检查余数是否为零（即是否整除）
        return remainder.compareTo(BigDecimal.ZERO) == 0;
    }


    /**
     * 获取对象中指定字段的值，并映射到指定类型的结果对象中
     *
     * @param <T>         结果对象的类型
     * @param sourceObj   源对象
     * @param fieldName   要获取的字段名称数组
     * @param resultClass 结果对象的Class
     * @return 包含指定字段值的结果对象
     * @throws Exception 如果发生反射异常
     */
    public static <T> T getFieldValues(Object sourceObj, String fieldName, Class<T> resultClass) throws Exception {
        // 创建结果对象实例
        T result = null;

        // 获取源对象的类
        Class<?> sourceClass = sourceObj.getClass();
        try {
            // 获取源对象中的字段
            Field sourceField = sourceClass.getDeclaredField(fieldName);
            sourceField.setAccessible(true);
            // 获取源字段的值
            Object fieldValue = sourceField.get(sourceObj);
            // 设置结果对象中的对应字段
            result = (T) fieldValue;
        } catch (NoSuchFieldException e) {
            // 结果类中没有该字段，忽略
            System.err.println("警告: 结果类 " + resultClass.getName() + " 中没有字段 " + fieldName);
        }

        return result;
    }
}
