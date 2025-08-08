package com.ebang.openapi.context;

/**
 * @Author: zyz
 * @Date: 2025/7/14 15:29
 * @Description:
 **/
public class RequestContext {

    // 线程上下文存储
    private static final ThreadLocal<Context> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    public static void setContext(String apiKey, String channel) {
        CONTEXT_THREAD_LOCAL.set(new Context(apiKey, channel));
    }
    public static Context getContext() {
        return CONTEXT_THREAD_LOCAL.get();
    }
    public static String getApiKey() {
        return CONTEXT_THREAD_LOCAL.get().getApiKey();
    }
    public static String getChannel() {
        return CONTEXT_THREAD_LOCAL.get().getChannel();
    }
    public static void remove() {
        CONTEXT_THREAD_LOCAL.remove();
    }
}
