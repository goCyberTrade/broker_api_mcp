package com.ebang.openapi.aspect;

import com.alibaba.fastjson2.JSON;
import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author chenlanqing 2025/7/4 14:28
 * @version 1.0.0
 */
@Slf4j
@Aspect
@Component
public class ExceptionAspect {

    /**
     * 捕获 LongportChannel 类中所有方法抛出的异常
     */
    @AfterThrowing(pointcut = "execution(* com.ebang.openapi.channel.LongportChannel.*(..))", throwing = "e")
    public void handleLongportException(JoinPoint joinPoint, Exception e) {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        log.error("longport {} error: {}", methodName, JSON.toJSONString(args), e);
        if (e.getCause() != null && (e.getCause() instanceof com.longport.OpenApiException openApiException)) {
            throw new OpenApiException(Math.toIntExact(openApiException.getCode()), openApiException.getMessage());
        }
        throw new OpenApiException(OpenApiErrorCodeEnums.SYSTEM_ERROR);
    }
}
