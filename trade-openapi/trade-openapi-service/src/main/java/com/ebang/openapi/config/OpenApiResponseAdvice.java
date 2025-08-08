package com.ebang.openapi.config;

import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import com.ebang.openapi.resp.ResultResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author chenlanqing 2025/7/4 13:27
 * @version 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class OpenApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResultResponse) {
            return body;
        }
        if (body instanceof OpenApiException openApiException) {
            return ResultResponse.fail(openApiException.getErrorCode(), openApiException.getMessage());
        }
        if (body instanceof Throwable throwable) {
            return ResultResponse.fail(OpenApiErrorCodeEnums.SYSTEM_ERROR.getErrorCode(), throwable.getMessage());
        }
        return ResultResponse.success(body);
    }

    @ExceptionHandler(OpenApiException.class)
    public ResultResponse<Object> handleOpenApiException(HttpServletRequest request, OpenApiException e) {
        log.error("OpenApiException request path: {}, error message: {}", request.getRequestURI(), e.getMessage(), e);
        ResultResponse<Object> resultResponse = new ResultResponse<>();
        resultResponse.setCode(e.getErrorCode());
        resultResponse.setMsg(e.getMessage());
        return resultResponse;
    }

    @ExceptionHandler(Exception.class)
    public ResultResponse<Object> handlerException(HttpServletRequest request, Exception e) {
        if (e instanceof OpenApiException openApiException) {
            return handleOpenApiException(request, openApiException);
        }
        return handleThrowable(request, e);
    }

    @ExceptionHandler(Throwable.class)
    public ResultResponse<Object> handleThrowable(HttpServletRequest request, Throwable e) {
        log.error("Service exception request path: {}, error: ", request.getRequestURI(), e);
        ResultResponse<Object> resultResponse = new ResultResponse<>();
        resultResponse.setCode(OpenApiErrorCodeEnums.SYSTEM_ERROR.getErrorCode());
        resultResponse.setMsg(OpenApiErrorCodeEnums.SYSTEM_ERROR.getMessage());
        return resultResponse;
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    public ResultResponse<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ResultResponse<Object> resultResponse = new ResultResponse<>();
        resultResponse.setCode(OpenApiErrorCodeEnums.PARAM_ERROR.getErrorCode());
        resultResponse.setMsg(e.getBindingResult().getFieldError().getField()+ " " +e.getBindingResult().getFieldError().getDefaultMessage());
        return resultResponse;
    }
}
