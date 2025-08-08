package com.ebang.openapi.config;


import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.util.HttpClient;
import com.ebang.openapi.util.RSAUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class USmartOpenApiService {
    static class OpenApiConstants {

        public static final String areaCode = "86";
        public static final String Content_type = "application/json; charset=utf-8";
        public static final String X_Dt = "1";
        public static final String X_Lang = "1";
        public static final String X_Type = "1";
        public static final String X_Channel = "914";
    }

    @Data
    public static class OpenHttpRequest {

        private Map<String, Object> parameters;
        private JSONObject jsonObject;
    }

    @Data
    public static class OpenHttpResponse<T> {

        private String code;

        private String msg;

        private T data;

    }

    @Data
    public class Token {
        private String token;
        private Long expireTime;
    }

    Long X_Time = System.currentTimeMillis() / 1000;

    public Token accessToken(OpenHttpRequest openHttpRequest) {
        String publicKey = openHttpRequest.getParameters().get("publicKey").toString();
        String privateKey = openHttpRequest.getParameters().get("privateKey").toString();
        Token token = new Token();
        String phoneNumber = RSAUtil.encrypt((openHttpRequest.getParameters()).get("phoneNumber").toString(), publicKey);
        String password = RSAUtil.encrypt((openHttpRequest.getParameters()).get("password").toString(), publicKey);
        String XSign;

        JSONObject jsonObj = new JSONObject();
        Map<String, String> headers = new HashMap<>();
        jsonObj.put("phoneNumber", phoneNumber);
        jsonObj.put("password", password);
        jsonObj.put("areaCode", OpenApiConstants.areaCode);
        JSONObject jsonObject_login;
        try {
            XSign = RSAUtil.sign(jsonObj.toString(), privateKey);

            //公共属性
            headers.put("Content-type", OpenApiConstants.Content_type);
            headers.put("X-Lang", OpenApiConstants.X_Lang);
            headers.put("X-Channel", OpenApiConstants.X_Channel);
            headers.put("X-Dt", OpenApiConstants.X_Dt);
            headers.put("X-Type", OpenApiConstants.X_Type);
            headers.put("X-Sign", XSign);
            headers.put("X-Time", X_Time.toString());
            jsonObject_login = HttpClient.post(USmartConstants.base_url_jy + openHttpRequest.getParameters().get("url").toString(), jsonObj, headers);
            token.setToken(jsonObject_login.getJSONObject("data").getString("token"));
            token.setExpireTime(jsonObject_login.getJSONObject("data").getLong("expiration"));
        } catch (Exception e) {
            log.error("login error ", e);
        }
        return token;
    }
    public OpenHttpResponse openapi(OpenHttpRequest openHttpRequest) {
        String phoneNumber;
        String password;
        String passwordTrade;
        String XSign;
        String json;

        JSONObject jsonObj = new JSONObject();

        Map<String, String> headers = new HashMap<>();

        //公共属性
        headers.put("Content-type", OpenApiConstants.Content_type);
        headers.put("X-Lang", OpenApiConstants.X_Lang);
        headers.put("X-Channel", OpenApiConstants.X_Channel);
        headers.put("X-Dt", OpenApiConstants.X_Dt);
        headers.put("X-Type", OpenApiConstants.X_Type);
        headers.put("X-Time", X_Time.toString());
        String publicKey = openHttpRequest.getParameters().get("publicKey").toString();
        String privateKey = openHttpRequest.getParameters().get("privateKey").toString();
        OpenHttpResponse openHttpResponse = new OpenHttpResponse();
        try {
            switch (openHttpRequest.getParameters().get("url").toString()) {
                case USmartConstants.login -> {
                    phoneNumber = RSAUtil.encrypt((openHttpRequest.getParameters()).get("phoneNumber").toString(), publicKey);
                    password = RSAUtil.encrypt((openHttpRequest.getParameters()).get("password").toString(), publicKey);
                    jsonObj.put("phoneNumber", phoneNumber);
                    jsonObj.put("password", password);
                    jsonObj.put("areaCode", OpenApiConstants.areaCode);
                    jsonObj.put("captcha", openHttpRequest.getParameters().get("captcha"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("X-Sign", XSign);
                }
                case USmartConstants.send_phone_captcha -> {
                    phoneNumber = RSAUtil.encrypt((openHttpRequest.getParameters()).get("phoneNumber").toString(), publicKey);
                    jsonObj.put("phoneNumber", phoneNumber);
                    jsonObj.put("type", (openHttpRequest.getParameters()).get("type"));
                    jsonObj.put("areaCode", OpenApiConstants.areaCode);
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.login_captcha -> {
                    JSONObject modifyUserConfigParam = new JSONObject();
                    modifyUserConfigParam.put("languageCn", "1");
                    modifyUserConfigParam.put("languageHk", "1");
                    modifyUserConfigParam.put("lineColorHk", "1");
                    phoneNumber = RSAUtil.encrypt((openHttpRequest.getParameters()).get("phoneNumber").toString(), publicKey);
                    jsonObj.put("areaCode", OpenApiConstants.areaCode);
                    jsonObj.put("phoneNumber", phoneNumber);
                    jsonObj.put("captcha", (openHttpRequest.getParameters()).get("captcha"));
                    jsonObj.put("modifyUserConfigParam", modifyUserConfigParam);
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.check_trade_password, USmartConstants.trade_login -> {
                    passwordTrade = RSAUtil.encrypt((openHttpRequest.getParameters()).get("password").toString(), publicKey);
                    jsonObj.put("password", passwordTrade);
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.reset_login_password -> {
                    phoneNumber = RSAUtil.encrypt((openHttpRequest.getParameters()).get("phoneNumber").toString(), publicKey);
                    password = RSAUtil.encrypt((openHttpRequest.getParameters()).get("password").toString(), publicKey);
                    jsonObj.put("password", password);
                    jsonObj.put("phoneNumber", phoneNumber);
                    jsonObj.put("phoneCaptcha", (openHttpRequest.getParameters()).get("captcha"));
                    jsonObj.put("areaCode", OpenApiConstants.areaCode);
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.get_trade_status -> {
                    password = RSAUtil.encrypt((openHttpRequest.getParameters()).get("password").toString(), publicKey);
                    jsonObj.put("password", password);
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.update_trade_password -> {
                    String newPassword = RSAUtil.encrypt((openHttpRequest.getParameters()).get("newPassword").toString(), publicKey);
                    passwordTrade = RSAUtil.encrypt((openHttpRequest.getParameters()).get("oldPassword").toString(), publicKey);
                    jsonObj.put("oldPassword", passwordTrade);
                    jsonObj.put("password", newPassword);
                    jsonObj.put("phoneCaptcha", (openHttpRequest.getParameters()).get("captcha"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.update_login_password -> {
                    String newLoginPassword = RSAUtil.encrypt((openHttpRequest.getParameters()).get("newPassword").toString(), publicKey);
                    password = RSAUtil.encrypt((openHttpRequest.getParameters()).get("oldPassword").toString(), publicKey);
                    jsonObj.put("oldPassword", password);
                    jsonObj.put("password", newLoginPassword);
                    jsonObj.put("phoneCaptcha", (openHttpRequest.getParameters()).get("captcha"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.get_user_stock_type -> {
                    jsonObj.put("marketType", (openHttpRequest.getParameters()).get("marketType"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.get_rate_by_fund_account -> {
                    jsonObj.put("fundAccount", (openHttpRequest.getParameters()).get("fundAccount"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.order -> {
                    passwordTrade = RSAUtil.encrypt((openHttpRequest.getParameters()).get("password").toString(), publicKey);
                    jsonObj.put("password", passwordTrade);
                    jsonObj.put("serialNo", (openHttpRequest.getParameters()).get("serialNo"));
                    jsonObj.put("entrustAmount", (openHttpRequest.getParameters()).get("entrustAmount"));
                    jsonObj.put("entrustPrice", (openHttpRequest.getParameters()).get("entrustPrice"));
                    jsonObj.put("entrustProp", (openHttpRequest.getParameters()).get("entrustProp"));
                    jsonObj.put("entrustType", (openHttpRequest.getParameters()).get("entrustType"));
                    jsonObj.put("exchangeType", (openHttpRequest.getParameters()).get("exchangeType"));
                    jsonObj.put("stockCode", (openHttpRequest.getParameters()).get("stockCode"));
                    jsonObj.put("stockName", (openHttpRequest.getParameters()).get("stockName"));
                    jsonObj.put("forceEntrustFlag", (openHttpRequest.getParameters()).get("forceEntrustFlag"));
                    jsonObj.put("sessionType", (openHttpRequest.getParameters()).get("sessionType"));
                    jsonObj.put("orderType", (openHttpRequest.getParameters()).get("orderType"));
                    jsonObj.put("validDate", (openHttpRequest.getParameters()).get("validDate"));
                    jsonObj.put("exchange", (openHttpRequest.getParameters()).get("exchange"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.modify_order -> {
                    passwordTrade = RSAUtil.encrypt((openHttpRequest.getParameters()).get("password").toString(), publicKey);
                    jsonObj.put("password", passwordTrade);
                    jsonObj.put("actionType", (openHttpRequest.getParameters()).get("actionType"));
                    jsonObj.put("entrustAmount", (openHttpRequest.getParameters()).get("entrustAmount"));
                    jsonObj.put("entrustPrice", (openHttpRequest.getParameters()).get("entrustPrice"));
                    jsonObj.put("forceEntrustFlag", (openHttpRequest.getParameters()).get("forceEntrustFlag"));
                    jsonObj.put("entrustId", (openHttpRequest.getParameters()).get("entrustId"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.odd_modify -> {
//                    passwordTrade = RSAUtil.encrypt((openHttpRequest.getParameters()).get("password").toString(), publicKey);
//                    jsonObj.put("password", passwordTrade);
                    jsonObj.put("actionType", (openHttpRequest.getParameters()).get("actionType"));
                    jsonObj.put("oddId", (openHttpRequest.getParameters()).get("oddId"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.modified_range -> {
                    jsonObj.put("newPrice", (openHttpRequest.getParameters()).get("newPrice"));
                    jsonObj.put("entrustId", (openHttpRequest.getParameters()).get("entrustId"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.odd_entrust -> {
                    jsonObj.put("entrustAmount", (openHttpRequest.getParameters()).get("entrustAmount"));
                    jsonObj.put("entrustPrice", (openHttpRequest.getParameters()).get("entrustPrice"));
                    jsonObj.put("entrustType", (openHttpRequest.getParameters()).get("entrustType"));
                    jsonObj.put("exchangeType", (openHttpRequest.getParameters()).get("exchangeType"));
                    jsonObj.put("stockCode", (openHttpRequest.getParameters()).get("stockCode"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.ipo_list, USmartConstants.ipo_info, USmartConstants.apply_ipo, USmartConstants.modify_ipo, USmartConstants.ipo_record_list, USmartConstants.ipo_record -> {
                    json = openHttpRequest.getJsonObject().toJSONString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                    headers.put("X-Request-Id", openHttpRequest.getParameters().get("X_Request_Id").toString());
                }
                case USmartConstants.stock_asset, USmartConstants.user_asset_aggregation, USmartConstants.margin_detail -> {
                    jsonObj.put("exchangeType", (openHttpRequest.getParameters()).get("exchangeType"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, privateKey);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                }
                case USmartConstants.order_detail -> {
                    jsonObj.put("entrustId", openHttpRequest.getParameters().get("entrustId"));
                    jsonObj.put("serialNo", openHttpRequest.getParameters().get("serialNo"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, null);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                }
                case USmartConstants.today_entrust -> {
                    jsonObj.put("exchangeType", 100);
                    jsonObj.put("pageNum", 1);
                    jsonObj.put("pageSize", Integer.MAX_VALUE);
                    jsonObj.put("stockCode", openHttpRequest.getParameters().get("stockCode"));
                    jsonObj.put("stockName", openHttpRequest.getParameters().get("stockName"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, null);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                }
                case USmartConstants.his_entrust -> {
                    jsonObj.put("exchangeType", 100);
                    jsonObj.put("pageNum", 1);
                    jsonObj.put("pageSize", Integer.MAX_VALUE);
                    jsonObj.put("stockCode", openHttpRequest.getParameters().get("stockCode"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, null);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                }
                case USmartConstants.basicinfo -> {
                    jsonObj.put("market", openHttpRequest.getParameters().get("market"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, null);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                }
                case USmartConstants.trade_quantity -> {
                    if (openHttpRequest.getParameters().get("entrustPrice") != null) {
                        jsonObj.put("entrustPrice", openHttpRequest.getParameters().get("entrustPrice"));
                    }
                    jsonObj.put("entrustProp", openHttpRequest.getParameters().get("entrustProp"));
                    jsonObj.put("exchangeType", openHttpRequest.getParameters().get("exchangeType"));
                    jsonObj.put("stockCode", openHttpRequest.getParameters().get("stockCode"));
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, null);
                    headers.put("Authorization", openHttpRequest.getParameters().get("token").toString());
                    headers.put("X-Sign", XSign);
                }
                case USmartConstants.mortgage_list-> {
                    Map<String, Object> parameters = openHttpRequest.getParameters();
                    jsonObj = openHttpRequest.getJsonObject();
                    json = jsonObj.toString();
                    XSign = RSAUtil.sign(json, null);
                    headers.put("Authorization", parameters.get("token").toString());
                    headers.put("X-Sign", XSign);
                }
                case USmartConstants.currency_exchange_info-> {
                    Map<String, Object> parameters = openHttpRequest.getParameters();
                    XSign = RSAUtil.sign("", null);
                    headers.put("Authorization", parameters.get("token").toString());
                    headers.put("X-Sign", XSign);
                }
            }

            openHttpResponse = openapiImpl((openHttpRequest.getParameters()).get("url").toString(), jsonObj, headers);
            return openHttpResponse;
        } catch (Exception e) {
            openHttpResponse.setCode("1300105");
            openHttpResponse.setMsg("系统异常");
            return openHttpResponse;
        }
    }


    public OpenHttpResponse openapiImpl(String url, Map<String, Object> bodyParams, Map<String, String> headers) {

        OpenHttpResponse openHttpResponse = new OpenHttpResponse();
//        String response_login = HttpClientUtil.postJson(OpenApiUrl.base_url_jy + url, json_login, httpPost_login, null);
        JSONObject response = HttpClient.post(USmartConstants.base_url_jy + url, bodyParams, headers);

        openHttpResponse.setCode(response.get("code").toString());
        openHttpResponse.setMsg(response.get("msg").toString());
        if (openHttpResponse.getCode().equals("0")) {
            openHttpResponse.setData(response.getJSONObject("data"));
        } else {
            log.error("请求url结果异常  url:{} code:{} msg:{}", url, openHttpResponse.getCode(), openHttpResponse.getMsg());
        }
        openHttpResponse.setCode(response.get("code").toString());
        openHttpResponse.setMsg(response.get("msg").toString());
        return openHttpResponse;

    }

}
