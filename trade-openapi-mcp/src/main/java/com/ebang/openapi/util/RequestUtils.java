package com.ebang.openapi.util;

import com.ebang.openapi.config.WebMvcSseServerTransportProviderLocal;
import com.ebang.openapi.context.RequestContext;
import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import io.modelcontextprotocol.server.McpAsyncServerExchange;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpServerSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Author: zyz
 * @Date: 2025/7/5 16:38
 * @Description:
 **/
@Slf4j
public class RequestUtils {

    /**
     * 获取对象中指定字段的值，并映射到指定类型的结果对象中
     *
     * @param <T>         结果对象的类型
     * @param sourceObj   源对象
     * @param fieldName   要获取的字段名称数组
     * @param resultClass 结果对象的Class
     * @return 包含指定字段值的结果对象
     * @throws Exception 如果发生反射异常
     */
    public static <T> T getFieldValues(Object sourceObj, String fieldName, Class<T> resultClass) throws Exception {
        // 创建结果对象实例
        T result = null;

        // 获取源对象的类
        Class<?> sourceClass = sourceObj.getClass();
        try {
            // 获取源对象中的字段
            Field sourceField = sourceClass.getDeclaredField(fieldName);
            sourceField.setAccessible(true);
            // 获取源字段的值
            Object fieldValue = sourceField.get(sourceObj);
            // 设置结果对象中的对应字段
            result = (T) fieldValue;
        } catch (NoSuchFieldException e) {
            // 结果类中没有该字段，忽略
            System.err.println("警告: 结果类 " + resultClass.getName() + " 中没有字段 " + fieldName);
        }

        return result;
    }
    public static <T> T requestHandle(Object source, Class<T> targetClass, ToolContext toolContext) {
        try {
            // 参数处理
            T target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            // toolContext处理
            Map<String, Object> context = toolContext.getContext();
            McpSyncServerExchange mcpSyncServerExchange = (McpSyncServerExchange)context.get("exchange");
            McpAsyncServerExchange mcpASyncServerExchange = getFieldValues(mcpSyncServerExchange, "exchange", McpAsyncServerExchange.class);
            McpServerSession mcpServerSession = getFieldValues(mcpASyncServerExchange, "session", McpServerSession.class);
            WebMvcSseServerTransportProviderLocal.WebMvcMcpSessionTransport transport = getFieldValues(mcpServerSession, "transport",  WebMvcSseServerTransportProviderLocal.WebMvcMcpSessionTransport.class);
            RequestContext.setContext(transport.getApiKey(), transport.getChannel());
            return target;
        } catch (Exception e) {
            log.error("处理发生异常", e);
            throw new OpenApiException(OpenApiErrorCodeEnums.SYSTEM_ERROR);
        }
    }
    public static void requestHandle(ToolContext toolContext) {
        try {
            // toolContext处理
            Map<String, Object> context = toolContext.getContext();
            McpSyncServerExchange mcpSyncServerExchange = (McpSyncServerExchange)context.get("exchange");
            McpAsyncServerExchange mcpASyncServerExchange = getFieldValues(mcpSyncServerExchange, "exchange", McpAsyncServerExchange.class);
            McpServerSession mcpServerSession = getFieldValues(mcpASyncServerExchange, "session", McpServerSession.class);
            WebMvcSseServerTransportProviderLocal.WebMvcMcpSessionTransport transport = getFieldValues(mcpServerSession, "transport",  WebMvcSseServerTransportProviderLocal.WebMvcMcpSessionTransport.class);
            RequestContext.setContext(transport.getApiKey(), transport.getChannel());
        } catch (Exception e) {
            log.error("处理发生异常", e);
            throw new OpenApiException(OpenApiErrorCodeEnums.SYSTEM_ERROR);
        }
    }
}
