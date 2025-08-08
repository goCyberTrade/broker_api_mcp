package com.ebang.openapi.util;

/**
 * @author xiaobo
 * @description
 * @date 2025/7/9 17:16
 */
import com.alibaba.fastjson.JSONObject;
import java.util.Map;

public class MapToJsonUtils {

    /**
     * 将 Map 转换为 FastJSON 的 JSONObject
     * @param map 待转换的 Map
     * @return 转换后的 JSONObject，若 map 为 null 则返回空的 JSONObject
     */
    public static JSONObject convert(Map<?, ?> map) {
        return convert(map, true);
    }

    /**
     * 将 Map 转换为 FastJSON 的 JSONObject，支持过滤空值
     * @param map 待转换的 Map
     * @param ignoreNull 是否忽略 null 值（true：忽略，false：保留）
     * @return 转换后的 JSONObject
     */
    public static JSONObject convert(Map<?, ?> map, boolean ignoreNull) {
        JSONObject json = new JSONObject();
        if (map == null || map.isEmpty()) {
            return json;
        }

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            // 处理 key 为 null 的情况（默认转为字符串 "null"）
            String keyStr = (key != null) ? key.toString() : "null";

            // 处理 value 为 null 的情况
            if (value == null && ignoreNull) {
                continue; // 忽略 null 值
            }

            // 处理嵌套 Map
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> nestedMap = (Map<Object, Object>) value;
                json.put(keyStr, convert(nestedMap, ignoreNull));
            } else {
                json.put(keyStr, value);
            }
        }
        return json;
    }



}