package com.ebang.openapi.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

import static com.ebang.openapi.constant.Constants.*;

/**
 * @author chenlanqing 2025/7/4 10:38
 * @version 1.0.0
 */
@Getter
public enum ProductTypeEnums {

    STOCK("stock", "股票", STOCK_MAPPING),
    FUNDS("funds", "基金", FUNDS_MAPPING),
    ;
    private final String productType;
    private final String desc;
    private final Map<String, Object> channelMapping;

    ProductTypeEnums(String productType, String desc, Map<String, Object> channelMapping) {
        this.productType = productType;
        this.desc = desc;
        this.channelMapping = channelMapping;
    }

    public static Object getSecType(String productType, String channel) {
        return Arrays.stream(values())
                .filter(s -> s.productType.equals(productType))
                .findAny()
                .map(s -> s.getChannelMapping().get(channel))
                .orElse(null);
    }

}
