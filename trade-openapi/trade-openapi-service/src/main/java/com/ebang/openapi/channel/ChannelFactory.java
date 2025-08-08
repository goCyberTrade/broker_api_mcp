package com.ebang.openapi.channel;


import com.ebang.openapi.config.FutuBase;
import com.ebang.openapi.config.TradeConfig;
import com.ebang.openapi.context.RequestContext;
import com.ebang.openapi.enums.ChannelEnums;
import com.ebang.openapi.exception.OpenApiException;
import com.ebang.openapi.req.BaseRequest;
import com.ebang.openapi.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

import static com.ebang.openapi.exception.OpenApiErrorCodeEnums.UN_SUPPORT_CHANNEL;

@RequiredArgsConstructor
@Component
public class ChannelFactory {

    @Autowired
    FutuBase futuBase;

    private final Map<String, BaseChannel> channelMap;
    private final TradeConfig tradeConfig;

    public BaseChannel getChannel(BaseRequest request) {
        String channel = Optional.ofNullable(RequestContext.getChannel())
                .orElse(tradeConfig.getDefaultChannel());

        BaseChannel baseChannel = channelMap.get(channel);
        if (baseChannel == null) {
            throw new OpenApiException(UN_SUPPORT_CHANNEL);
        }
        // 校验参数
        ValidationUtil.validateByChannel(request, RequestContext.getChannel());

        // 预先请求处理
        baseChannel.beforeHandle();
        return  baseChannel;
    }
}
