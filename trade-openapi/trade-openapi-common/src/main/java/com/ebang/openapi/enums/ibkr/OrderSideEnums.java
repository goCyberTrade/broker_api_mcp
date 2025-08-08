package com.ebang.openapi.enums.ibkr;

import lombok.Getter;

/**
 * @author chenlanqing 2025/7/7 09:24
 * @version 1.0.0
 */
@Getter
public enum OrderSideEnums {
    BUY("BUY", "买入"),
    SELL("SELL", "卖出"),
    CLOSE("CLOSE", "平仓");

    private final String code;
    private final String description;

    OrderSideEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
