package com.ebang.openapi.util;

/**
 * @Author: zyz
 * @Date: 2025/7/5 16:26
 * @Description:
 **/
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EnumUtils {
    /**
     * 通用方法：将字符串列表转换为指定枚举类型的数组
     * 若字符串无法匹配枚举值，会抛出 IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T[] convertToEnumArray(Class<T> enumType, List<String> names) {
        if (names == null || names.isEmpty()) {
            return (T[]) java.lang.reflect.Array.newInstance(enumType, 0);
        }
        return names.stream()
                .map(name -> Enum.valueOf(enumType, name))
                .toArray(size -> (T[]) java.lang.reflect.Array.newInstance(enumType, size));
    }

    /**
     * 安全转换：忽略无法匹配的字符串，返回有效枚举值
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T[] safeConvertToEnumArray(Class<T> enumType, List<String> names) {
        if (names == null || names.isEmpty()) {
            return (T[]) java.lang.reflect.Array.newInstance(enumType, 0);
        }
        T[] enumConstants = enumType.getEnumConstants();
        return names.stream()
                .filter(Objects::nonNull)
                .filter(name -> Arrays.stream(enumConstants)
                        .anyMatch(e -> e.name().equals(name)))
                .map(name -> Enum.valueOf(enumType, name))
                .toArray(size -> (T[]) java.lang.reflect.Array.newInstance(enumType, size));
    }

    /**
     * 忽略大小写的安全转换（列表）
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T[] safeConvertToEnumArrayIgnoreCase(Class<T> enumType, List<String> names) {
        if (names == null || names.isEmpty()) {
            return (T[]) java.lang.reflect.Array.newInstance(enumType, 0);
        }
        return names.stream()
                .filter(Objects::nonNull)
                .map(name -> findEnumValueIgnoreCase(enumType, name))
                .filter(Objects::nonNull)
                .toArray(size -> (T[]) java.lang.reflect.Array.newInstance(enumType, size));
    }

    /**
     * 单个字符串的忽略大小写安全转换
     * @param enumType 枚举类型
     * @param name 待转换的字符串（可为 null）
     */
    public static <T extends Enum<T>> T safeValueOfIgnoreCase(Class<T> enumType, String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(enumType.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private static <T extends Enum<T>> T findEnumValueIgnoreCase(Class<T> enumType, String name) {
        return Arrays.stream(enumType.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据自定义code字段值获取对应的枚举对象（单个值）
     * @param enumClass 枚举类的Class对象
     * @param codeFieldName 自定义code字段的名称
     * @param codeValue 枚举成员的code值
     * @param <E> 枚举类型
     * @return 对应的枚举对象，如果未找到或反射异常则返回null
     */
    public static <E extends Enum<E>> E getEnumByField(Class<E> enumClass, String codeFieldName, Object codeValue) {
        try {
            Field field = enumClass.getDeclaredField(codeFieldName);
            field.setAccessible(true);
            return Arrays.stream(enumClass.getEnumConstants())
                    .filter(e -> {
                        try {
                            return field.get(e).equals(codeValue);
                        } catch (IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                    })
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("枚举类 " + enumClass.getName() + " 中不存在字段: " + codeFieldName, e);
        }
    }

    /**
     * 根据自定义code字段值列表获取对应的枚举对象列表
     * @param enumClass 枚举类的Class对象
     * @param codeFieldName 自定义code字段的名称
     * @param codeValues 枚举成员的code值列表
     * @param <E> 枚举类型
     * @return 对应的枚举对象列表，如果未找到则返回空列表
     */
    public static <E extends Enum<E>> List<E> getEnumsByFields(Class<E> enumClass, String codeFieldName, List<?> codeValues) {
        if (codeValues == null || codeValues.isEmpty()) {
            return List.of();
        }
        return codeValues.stream()
                .map(codeValue -> getEnumByField(enumClass, codeFieldName, codeValue))
                .filter(e -> e != null)
                .collect(Collectors.toList());
    }

}
