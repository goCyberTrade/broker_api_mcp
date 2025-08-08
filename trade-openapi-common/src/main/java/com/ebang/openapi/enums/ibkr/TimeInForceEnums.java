package com.ebang.openapi.enums.ibkr;

import lombok.Getter;

/**
 * @author chenlanqing 2025/7/7 09:24
 * @version 1.0.0
 */
@Getter
public enum TimeInForceEnums {
    DAY("DAY", "当日有效"),
    IOC("IOC", "立即成交否则取消"),
    GTC("GTC", "取消前有效"),
    OPG("OPG", "开盘时有效"),
    PAX("OVT", "隔夜"),
    OND("OND", "隔夜+当日");
    private final String code;
    private final String description;

    TimeInForceEnums(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
