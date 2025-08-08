package com.ebang.openapi.context;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: zyz
 * @Date: 2025/7/14 15:29
 * @Description:
 **/
@Data
public class Context {

    public Context(String apiKey, String channel) {
        this.apiKey = apiKey;
        this.channel = channel;
    }

    private String apiKey;
    private String channel;

}
