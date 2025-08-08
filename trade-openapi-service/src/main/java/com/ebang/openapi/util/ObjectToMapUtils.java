package com.ebang.openapi.util;

/**
 * @author xiaobo
 * @description
 * @date 2025/7/9 16:53
 */
import com.ebang.openapi.utils.MapConvert;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectToMapUtils {

    /**
     * 将对象转换为 Map，规则：
     * 1. 忽略值为 null 的字段
     * 2. 忽略被 @MapConvert(false) 标识的字段
     * @param obj 待转换的对象
     * @return 转换后的 Map（key：字段名，value：非空字段值）
     */
    public static Map<String, Object> convert(Object obj) {
        Map<String, Object> resultMap = new HashMap<>();
        if (obj == null) {
            return resultMap;
        }
        Class<?> clazz = obj.getClass();
        // 遍历所有字段（包括父类字段）
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                // 1. 检查字段是否被标识为不转换
                MapConvert annotation = field.getAnnotation(MapConvert.class);
                if (annotation != null && !annotation.value()) {
                    continue; // 跳过被排除的字段
                }

                // 2. 访问私有字段
                field.setAccessible(true);
                try {
                    // 3. 获取字段值并检查是否为 null
                    Object value = field.get(obj);
                    if (value != null) {
                        // 特殊处理空字符串
                        if (value instanceof String && ((String) value).isEmpty()) {
                            continue;
                        }
                        resultMap.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    System.err.println("转换字段 [" + field.getName() + "] 失败：" + e.getMessage());
                }
            }
            // 处理父类
            clazz = clazz.getSuperclass();
        }

        return resultMap;
    }
}