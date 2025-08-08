package com.ebang.openapi.config;

import com.google.common.collect.Lists;
import com.huasheng.quant.open.gateway.api.HSQuantOpenApiHandle;
import com.huasheng.quant.open.gateway.api.PushNotifyMessageHandler;
import com.huasheng.quant.open.gateway.sdk.constant.RateType;
import com.huasheng.quant.open.gateway.sdk.constant.algo.AlgoActionType;
import com.huasheng.quant.open.gateway.sdk.constant.algo.AlgoEntrustType;
import com.huasheng.quant.open.gateway.sdk.constant.algo.AlgoStrategySensitivityType;
import com.huasheng.quant.open.gateway.sdk.constant.algo.AlgoStrategyType;
import com.huasheng.quant.open.gateway.sdk.constant.futures.FuturesEntrustBs;
import com.huasheng.quant.open.gateway.sdk.constant.futures.FuturesEntrustType;
import com.huasheng.quant.open.gateway.sdk.constant.futures.FuturesValidTimeType;
import com.huasheng.quant.open.gateway.sdk.constant.hq.*;
import com.huasheng.quant.open.gateway.sdk.constant.trade.*;
import com.huasheng.quant.open.gateway.sdk.domain.ModelResult;
import com.huasheng.quant.open.gateway.sdk.vo.CommonStringVo;
import com.huasheng.quant.open.gateway.sdk.vo.algo.*;
import com.huasheng.quant.open.gateway.sdk.vo.futures.*;
import com.huasheng.quant.open.gateway.sdk.vo.hq.*;
import com.huasheng.quant.open.gateway.sdk.vo.trade.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zyz
 * @Date: 2025/7/6 15:35
 * @Description:
 **/

@Service
@Slf4j
public class HSQuantOpenTradeAndHqApi {
    private HSQuantOpenApiHandle quantOpenApiHandle = null;

    public HSQuantOpenApiHandle getQuantOpenApiHandle(Map<String, String> map) {

        if (quantOpenApiHandle == null) {
            PushNotifyMessageHandler notifyHandle = new PushNotifyMessageHandler(2, 10000, 1);
            quantOpenApiHandle = new HSQuantOpenApiHandle(map.get("ip"), Integer.valueOf(map.get("httpPort")), Integer.valueOf(map.get("tcpPort")), notifyHandle);
        }
        if (!quantOpenApiHandle.isActive()) {
            quantOpenApiHandle.startConnect();
        }
        if (!quantOpenApiHandle.isActive()) {
            throw new RuntimeException("连接未建立");
        }

        return quantOpenApiHandle;
    }

    public boolean start(String ip, int httpPort, int tcpPort) {
        // 消息推送处理类
        PushNotifyMessageHandler notifyHandle = new PushNotifyMessageHandler(2, 10000, 1);
        quantOpenApiHandle = new HSQuantOpenApiHandle(ip, httpPort, tcpPort, notifyHandle);
        // [启动客户端连接]
        return this.quantOpenApiHandle.startConnect();
    }

    public boolean isActive() {
        return quantOpenApiHandle.isActive();
    }

    public void stop() {
        try {
            // 断开与Server端得TCP连接【！！！该方法请在程序停止时执行！！！】
            quantOpenApiHandle.disconnectAndStop();
            log.info("**************** Process exit. ****************");
        } catch (Exception e) {
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 断开与Server端得TCP连接【！！！该方法请在程序停止时执行！！！】
                quantOpenApiHandle.disconnectAndStop();
                log.info("**************** Shutdown hook exit. ****************");
            } catch (Exception e) {
            }
        }, "StartMain-shutdown-hook"));
    }

    public void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
        }
    }

}
