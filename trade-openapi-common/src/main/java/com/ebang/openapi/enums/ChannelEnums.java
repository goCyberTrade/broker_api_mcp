package com.ebang.openapi.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ChannelEnums {
    FUTU("futu", "富途证券"),
    IBKR("ibkr", "IB证券"),
    WEBULL("webull", "微牛证券"),
    TIGER("tiger", "老虎证券"),
    HSTONG("hstong", "华盛通证券"),
    USMART("usmart", "盈立证券"),
    LONG_PORT("longport", "长桥证券"),
    ROBINHOOD("robinhood", "Robinhood");

    private final String channel;
    private final String desc;

    ChannelEnums(String channel, String desc) {
        this.channel = channel;
        this.desc = desc;
    }

    public static Optional<ChannelEnums> getChannelEnums(String channel) {
        return Arrays.stream(ChannelEnums.values())
                .filter(channelEnums -> channelEnums.channel.equals(channel))
                .findFirst();
    }
}
