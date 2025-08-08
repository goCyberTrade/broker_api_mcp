package com.ebang.openapi.constant;

import com.ebang.openapi.enums.ChannelEnums;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;

import java.util.Map;

/**
 * @author chenlanqing 2025/7/7 09:09
 * @version 1.0.0
 */
public interface Constants {

    Map<String, Object> STOCK_MAPPING = Map.of(
            ChannelEnums.TIGER.getChannel(), SecType.STK
    );

    Map<String, Object> FUNDS_MAPPING = Map.of(
            ChannelEnums.TIGER.getChannel(), SecType.FUND
    );

    Map<String, Object> US_MARKET_MAPPING = Map.of(
            ChannelEnums.TIGER.getChannel(), Market.US
    );

    Map<String, Object> HK_MARKET_MAPPING = Map.of(
            ChannelEnums.TIGER.getChannel(), Market.HK
    );

    Map<String, Object> CN_MARKET_MAPPING = Map.of(
            ChannelEnums.TIGER.getChannel(), Market.CN
    );

    public static final String API_KEY="ApiKey";
    public static final String CHANNEL="channel";

}
