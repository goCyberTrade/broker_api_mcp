package com.ebang.openapi.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识字段是否需要转换到 Map 中
 */
@Target(ElementType.FIELD) // 作用于字段
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，可通过反射获取
public @interface MapConvert {
    /**
     * 是否需要转换到 Map 中
     * @return true（默认）：转换；false：不转换
     */
    boolean value() default false;
}
