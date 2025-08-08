package com.ebang.openapi.channel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import com.ebang.openapi.req.*;
import com.ebang.openapi.util.HttpClient;
import com.ebang.openapi.util.ObjectToMapUtils;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chenlanqing 2025/7/3 10:57
 * @version 1.0.0
 */
@Component("robinhood")
public class RobinhoodChannel extends BaseChannel {

    private final static String BASE_HOST = "https://api.robinhood.com";
    //存储token信息
    private final Map<String, RhToken> rhTokenMap = new ConcurrentHashMap<>();

    //获取账户主体
    @Override
    public Object signaturesAndOwners(SignaturesOwnersReq req) {

        String url = BASE_HOST + "/user/basic_info";

        JSONObject result = HttpClient.get(url, null, buildHeaders());

        return result;
    }
    @Override
    public Object getOrderInfo(OrderInfoQueryReq req) throws Exception {

        String url = BASE_HOST + "/orders/" + req.getOrderId();

        JSONObject result = HttpClient.get(url, null, buildHeaders());
        return result;
    }
    @Override
    public Object getOrderList(OrderListQueryReq req) throws Exception {

        String url = BASE_HOST + "/orders";

        JSONObject result = HttpClient.get(url, null, buildHeaders());
        return result;
    }

    public Object cancelOrder(CancelOrderReq req) throws Exception {
        String url = BASE_HOST + "/orders/"+req.getOrderId()+"/cancel";
        JSONObject result = HttpClient.post(url, null, buildHeaders());
        return result;
    }
    @Override
    public Object createOrder(CreateOrderReq req) throws Exception {

        String url = BASE_HOST + "/orders/";
        Map<String, Object> map = new HashMap<>();
        map.put("account", req.getAccount());
        map.put("instrument", req.getInstrument());
        map.put("symbol", req.getSymbol());
        map.put("type", req.getType());
        map.put("time_in_force", req.getTimeInForce());
        map.put("trigger", req.getTrigger());
        map.put("price", req.getPrice());
        map.put("stop_price", req.getStopPrice());
        map.put("quantity", req.getQuantity());
        map.put("side", req.getSide());
        map.put("client_id", req.getClientId());
        map.put("extended_hours", req.getExtendedHours());
        map.put("override_day_trade_checks", req.getOverrideDayTradeChecks());
        map.put("override_dtbp_checks", req.getOverrideDtbpChecks());
        JSONObject result = HttpClient.post(url, null, buildHeaders());
        return result;
    }

    @Override
    public Object getContractList(ContractListReq req) {
        String url = BASE_HOST + "/instruments";
        return HttpClient.get(url, null, buildHeaders());
    }

    //构建请求头
    public Map<String, String> buildHeaders() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", accessToken());
        return headerMap;
    }

    public String accessToken() {
        //暂不支持多因素认证账号（需用户手动输入手机/邮箱验证码）
        String apiKey = getApiKey();
        // 2. token获取
        RhToken token = rhTokenMap.compute(apiKey, (key, existingToken) -> {
            // 如果 token 不存在或即将过期（30秒内），则刷新
            if (existingToken == null || isTokenExpired(existingToken)) {
                String refreshToken = getRefreshToken(existingToken);
                String url = BASE_HOST + "/oauth2/migrate_token";
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Bearer ".concat(refreshToken));
                JSONObject result = HttpClient.post(url, null, headerMap);
                RhToken rhToken = new RhToken();
                //token_type:Bearer/Token
                rhToken.setToken(result.getString("token_type").concat(" ").concat(result.getString("access_token")));
                rhToken.setRefreshToken(result.getString("refresh_token"));
                //设置过期时间
                rhToken.setExpiresIn(System.currentTimeMillis() / 1000 + result.getInteger("expires_in"));
                return rhToken;
            }
            // 否则返回现有 token
            return existingToken;
        });
        return token.getToken();
    }

    public String getRefreshToken(RhToken rhToken) {
        if (Objects.isNull(rhToken)) {
            //第一次使用登录获取token 做为oauth2 的刷新token
            String url = BASE_HOST + "/api-token-auth";
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("username", "username");
            payloadMap.put("password", "password");
            JSONObject result = HttpClient.post(url, payloadMap);
            //多因素认证账号，暂不支持
            if (result.containsKey("mfa_required")) {
                throw new OpenApiException(OpenApiErrorCodeEnums.UN_SUPPORT_MFA_ACCOUNT);
            }
            return result.getString("token");
        }
        return rhToken.getRefreshToken();
    }

    // 判断 token 是否即将过期
    private boolean isTokenExpired(RhToken token) {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        return (currentTimeSeconds - token.getExpiresIn()) <= 30;
    }

    @Data
    public class RhToken {
        private String token;
        //获取token用，第一次为登录获取到的token
        private String refreshToken;
        //过期时间，到s
        private Long expiresIn;
    }

    /**
     * 重置密码
     */
    @Override
    public Object passwordReset(PasswordResetReq req) throws Exception {
        String url = BASE_HOST + "/password_reset/";
        JSONObject result = HttpClient.post(url, ObjectToMapUtils.convert(req), buildHeaders());
        return result;
    }

    /**
     * 重置密码请求
     */
    @Override
    public Object passwordResetRequest(PasswordResetRequestReq req) throws Exception {
        String url = BASE_HOST + "/password_reset/request/";
        JSONObject result = HttpClient.post(url, ObjectToMapUtils.convert(req), buildHeaders());
        return result;
    }

    /**
     * 根据symbol等信息查询交易标的
     */
    @Override
    public Object getInstrumentInfo(InstrumentInfoReq req) {
        String url = BASE_HOST + "/instruments/";
        JSONObject result = HttpClient.get(url, ObjectToMapUtils.convert(req), buildHeaders());
        return result;
    }
}
