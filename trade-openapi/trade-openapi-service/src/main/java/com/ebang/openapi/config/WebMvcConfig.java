package com.ebang.openapi.config;

import com.ebang.openapi.interceptor.ApiKeyInterceptor;
import com.ebang.openapi.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: zyz
 * @Date: 2025/7/14 15:24
 * @Description:
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthenticationService authenticationService;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiKeyInterceptor(authenticationService))
                .addPathPatterns("/api/v1/**");
    }
}
