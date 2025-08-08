package com.ebang.openapi.config;

import com.ebang.openapi.enums.ChannelEnums;
import com.futu.openapi.pb.TrdCommon;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenlanqing 2025/7/3 10:46
 * @version 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "openapi.trade")
public class TradeConfig {

    /**
     * 默认渠道
     */
    private String defaultChannel = ChannelEnums.LONG_PORT.getChannel();

    /**
     * 富途默认环境
     */
    private Integer futuDefaultEnv = TrdCommon.TrdEnv.TrdEnv_Simulate.getNumber();
}
