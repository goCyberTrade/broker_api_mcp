package com.ebang.openapi.service;

import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.config.ApiKeyPropertiesConfig;
import com.ebang.openapi.util.SpringContextHolder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaobo
 * @description
 * @date 2025/7/10 12:26
 */
@Service
public class AuthenticationService {

    @Autowired
    private Environment environment;
    @Autowired
    private ApiKeyPropertiesConfig apiKeyPropertiesConfig;
    public static final Map<String, JSONObject> APIKEY_DATA = new ConcurrentHashMap<>();

    public boolean checkApiKey(String apiKey) {
        //TODO 后续待修改
        return APIKEY_DATA.containsKey(apiKey);

//        if (StringUtils.isBlank(apiKey)) {
//            return false;
//        }
//        JSONObject jsonObject = APIKEY_USER.get(apiKey);
//        if (jsonObject == null) {
//            return false;
//        }
    }

    @PostConstruct
    public void setApikeyData() {

        Map<String, String> apiKey = apiKeyPropertiesConfig.getApiKey();
        for (Map.Entry<String, String> entry : apiKey.entrySet()) {
            APIKEY_DATA.put(entry.getKey(), JSONObject.parseObject( entry.getValue()));
        }
    }

}
