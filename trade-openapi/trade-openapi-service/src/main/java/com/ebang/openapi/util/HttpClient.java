package com.ebang.openapi.util;

/**
 * @Author: zyz
 * @Date: 2025/7/10 15:00
 * @Description:
 **/
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final int DEFAULT_CONNECT_TIMEOUT = 10; // 10秒
    private static final int DEFAULT_READ_TIMEOUT = 30;    // 30秒

    // 私有构造函数，防止实例化
    private HttpClient() {}

    // 单例模式获取OkHttpClient实例
    private static class OkHttpHolder {
        private static final OkHttpClient INSTANCE = createDefaultClient();
        private static final OkHttpClient TRUST_ALL_CLIENT = createTrustAllClient();
    }

    // 创建默认的OkHttpClient
    private static OkHttpClient createDefaultClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    // 创建信任所有证书的OkHttpClient（仅用于测试环境）
    private static OkHttpClient createTrustAllClient() {
        try {
            // 创建信任管理器
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // 信任所有客户端证书
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // 信任所有服务器证书
                        }
                    }
            };

            // 安装信任管理器
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // 创建信任所有主机名的验证器
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            return new OkHttpClient.Builder()
                    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)trustAllCerts[0])
                    .hostnameVerifier(allHostsValid)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    // 获取默认客户端
    private static OkHttpClient getDefaultClient() {
        return OkHttpHolder.INSTANCE;
    }

    // 获取信任所有证书的客户端
    private static OkHttpClient getTrustAllClient() {
        return OkHttpHolder.TRUST_ALL_CLIENT;
    }

    // 判断是否为HTTPS请求
    private static boolean isHttps(String url) {
        return url != null && url.startsWith("https");
    }

    public static JSONObject get(String url) {
        return sendRequest("GET", url, null, null, null);
    }

    public static JSONObject get(String url, Map<String, Object> payload) {
        return sendRequest("GET", url, payload, null, null);
    }

    public static JSONObject get(String url, Map<String, Object> payload,
                                 Map<String, String> headers) {
        return sendRequest("GET", url, payload, headers, null);
    }

    public static JSONObject post(String url) {
        return sendRequest("POST", url, null, null, new HashMap<>());
    }

    public static JSONObject post(String url, Map<String, Object> payload) {
        return sendRequest("POST", url, null , null, payload == null? new HashMap<>() : payload);
    }

    public static JSONObject post(String url, Map<String, Object> payload,
                                  Map<String, String> headers) {
        return sendRequest("POST", url, null, headers, payload == null? new HashMap<>() : payload);
    }

    public static JSONObject delete(String url) {
        return sendRequest("DELETE", url, null, null, null);
    }

    // 通用请求方法
    private static JSONObject sendRequest(String method, String url,
                                          Map<String, Object> queryParams,
                                          Map<String, String> headers,
                                          Map<String, Object> body) {
        logger.info("准备发送请求 方式:{} 地址:{} 参数:{}", method, url, body);

        JSONObject jsonObject = new JSONObject();
        boolean isHttps = isHttps(url);

        // 构建完整URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, Object> param : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        String fullUrl = urlBuilder.build().toString();

        // 创建请求构建器
        Request.Builder requestBuilder = new Request.Builder()
                .url(fullUrl);

        // 设置请求头
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }

        // 设置请求体
        RequestBody requestBody = null;
        try {
            if (body != null) {
                String bodyStr = objectMapper.writeValueAsString(body);
                requestBody = RequestBody.create(bodyStr, JSON);
            }
        } catch (Exception e) {
            jsonObject.put("errorCode", OpenApiErrorCodeEnums.SYSTEM_ERROR.getErrorCode());
            jsonObject.put("errorMessage", "读取内容异常");
            return jsonObject;
        }

        // 根据HTTP方法设置请求
        switch (method.toUpperCase()) {
            case "GET":
                requestBuilder.get();
                break;
            case "POST":
                requestBuilder.post(requestBody);
                break;
            case "DELETE":
                if (requestBody != null) {
                    requestBuilder.delete(requestBody);
                } else {
                    requestBuilder.delete();
                }
                break;
            default:
                jsonObject.put("errorCode", OpenApiErrorCodeEnums.SYSTEM_ERROR.getErrorCode());
                jsonObject.put("errorMessage", "不支持的HTTP方法" + method);
                return jsonObject;
        }

        // 发送请求
        Request request = requestBuilder.build();
        OkHttpClient client = isHttps ? getTrustAllClient() : getDefaultClient();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            logger.info("请求响应 状态码:{} 响应内容:{}", response.code(), responseBody);

            if (StringUtils.isEmpty(responseBody)) {
                jsonObject.put("errorCode", OpenApiErrorCodeEnums.SYSTEM_ERROR.getErrorCode());
                jsonObject.put("errorMessage", "请求渠道返回状态" + response.code());
                return jsonObject;
            }

            return JSONObject.parseObject(responseBody);
        } catch (Exception e) {
            logger.error("{} 请求 {} 发生未知错误: {}", method.toUpperCase(), url, e.getMessage());
            jsonObject.put("errorCode", OpenApiErrorCodeEnums.SYSTEM_ERROR.getErrorCode());
            jsonObject.put("errorMessage", e.getMessage());
            return jsonObject;
        }
    }

    // 示例使用
    public static void main(String[] args) {
        try {
            // GET请求示例
//            Map<String, String> getParams = new HashMap<>();
//            getParams.put("param1", "value1");
//            String getResponse = OkHttpUtils.doGetHttp("example.com/api", getParams, null);
//            System.out.println("GET响应: " + getResponse);
            String BASE_URL = "https://localhost:5000/v1/api";
            String url = BASE_URL + "/iserver/account/orders";
            Map<String, Object> map = new HashMap<>();
            JSONObject orderList = HttpClient.get(url, map);
            System.out.println(orderList);

            // POST JSON请求示例
            Map<String, Object> postData = new HashMap<>();
            postData.put("key1", "value1");
            postData.put("key2", 123);
            Map<String, String> postHeaders = new HashMap<>();
            postHeaders.put("Authorization", "Bearer token123");
            JSONObject postResponse = HttpClient.post("https://localhost:5000/v1/api/iserver/auth/status");
            System.out.println("POST响应: " + postResponse);

//            // DELETE请求示例
//            String deleteResponse = OkHttpUtils.doDeleteHttp("example.com/api/resource/1", null, null);
//            System.out.println("DELETE响应: " + deleteResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
