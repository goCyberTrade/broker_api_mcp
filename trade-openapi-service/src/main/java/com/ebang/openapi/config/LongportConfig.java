package com.ebang.openapi.config;

import com.longport.Config;
import com.longport.ConfigBuilder;
import com.longport.OpenApiException;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 长桥证券配置
 *
 * @author chenlanqing 2025/7/4 10:05
 * @version 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "longport")
public class LongportConfig {

    /**
     * 超时时间，单位：秒
     */
    private long timeout = 5;
    private String appKey;
    private String appSecret;
    private String accessToken;


    @Bean
    public Config getConfig(LongportConfig longportConfig) throws OpenApiException {
        return new ConfigBuilder(
                longportConfig.getAppKey(),
                longportConfig.getAppSecret(),
                longportConfig.getAccessToken()
        ).build();
    }
}
