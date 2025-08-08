package com.ebang.openapi.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

import static com.ebang.openapi.constant.Constants.*;

/**
 * @author chenlanqing 2025/7/7 09:24
 * @version 1.0.0
 */
@Getter
public enum MarketEnums {
    US("US", US_MARKET_MAPPING),
    HK("HK", HK_MARKET_MAPPING),
    CN("CN", CN_MARKET_MAPPING),
    ;
    private final String market;
    private final Map<String, Object> channelMapping;

    MarketEnums(String market, Map<String, Object> channelMapping) {
        this.market = market;
        this.channelMapping = channelMapping;
    }

    public static Object getChannelMarket(String market, String channel) {
        return Arrays.stream(values())
                .filter(s -> s.getMarket().equalsIgnoreCase(market))
                .findAny()
                .map(s -> s.getChannelMapping().get(channel))
                .orElse(null);
    }
}
