package com.ebang.openapi.enums.ibkr;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

import static com.ebang.openapi.constant.Constants.*;

/**
 * @author chenlanqing 2025/7/7 09:24
 * @version 1.0.0
 */
@Getter
public enum OrderTypeEnums {
    LIMIT("LIMIT", "限价单"),
    MARKET("MARKET", "市价单"),
    STOP("STOP", "止损单"),
    STOP_LIMIT("STOP_LIMIT", "止损限价单"),
    TRAILING_STOP("TRAILING_STOP", "追踪单"),
    TRAILING_STOP_LIMIT("TRAILING_STOP_LIMIT", "追踪限价单"),
    MARKETONCLOSE("MARKETONCLOSE", "收盘市价单"),
    LIMITONCLOSE("LIMITONCLOSE", "收盘限价单");

    private final String code;
    private final String description;

    OrderTypeEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
