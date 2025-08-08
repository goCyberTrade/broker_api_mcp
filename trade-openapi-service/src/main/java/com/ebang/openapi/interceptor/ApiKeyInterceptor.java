package com.ebang.openapi.interceptor;

/**
 * @Author: zyz
 * @Date: 2025/7/14 15:31
 * @Description:
 **/
import com.ebang.openapi.context.RequestContext;
import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import com.ebang.openapi.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

// API Key请求拦截器
public class ApiKeyInterceptor implements HandlerInterceptor {

    private AuthenticationService authenticationService;

    public ApiKeyInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取apiKey
        String apiKey = request.getHeader("apiKey");
        String channel = request.getHeader("channel");

        //TODO 校验
        if (!authenticationService.checkApiKey(apiKey)) {
            throw new OpenApiException(OpenApiErrorCodeEnums.UNAUTHORIZED);


        }
        RequestContext.setContext(apiKey, channel);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 请求处理完成后，清除线程上下文
        RequestContext.remove();
    }
}