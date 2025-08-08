package com.ebang.openapi.channel;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ebang.openapi.context.RequestContext;
import com.ebang.openapi.enums.MarketEnums;
import com.ebang.openapi.enums.ProductTypeEnums;
import com.ebang.openapi.exception.OpenApiException;
import com.ebang.openapi.req.*;
import com.ebang.openapi.util.DataUtils;
import com.ebang.openapi.util.EnumUtils;
import com.tigerbrokers.stock.openapi.client.config.ClientConfig;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.item.ContractItem;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.model.ContractModel;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.model.ContractsModel;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.model.QuoteContractModel;
import com.tigerbrokers.stock.openapi.client.https.domain.trade.item.TradableQuantityItem;
import com.tigerbrokers.stock.openapi.client.https.domain.trade.model.TradeOrderModel;
import com.tigerbrokers.stock.openapi.client.https.request.TigerHttpRequest;
import com.tigerbrokers.stock.openapi.client.https.request.contract.ContractRequest;
import com.tigerbrokers.stock.openapi.client.https.request.contract.ContractsRequest;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteContractRequest;
import com.tigerbrokers.stock.openapi.client.https.request.trade.*;
import com.tigerbrokers.stock.openapi.client.https.response.TigerHttpResponse;
import com.tigerbrokers.stock.openapi.client.https.response.contract.ContractResponse;
import com.tigerbrokers.stock.openapi.client.https.response.contract.ContractsResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteContractResponse;
import com.tigerbrokers.stock.openapi.client.https.response.trade.*;
import com.tigerbrokers.stock.openapi.client.struct.enums.*;
import com.tigerbrokers.stock.openapi.client.util.StringUtils;
import com.tigerbrokers.stock.openapi.client.util.builder.AccountParamBuilder;
import com.tigerbrokers.stock.openapi.client.util.builder.TradeParamBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ebang.openapi.exception.OpenApiErrorCodeEnums.REQUIRED_SYMBOL;

/**
 * 老虎证券
 *
 * @author chenlanqing
 */
@Component("tiger")
public class TigerChannel extends BaseChannel {

    //key:apiKey
    final Map<String, TigerHttpClient> tigerHttpClientMap = new ConcurrentHashMap<>();


    //获取老虎客户端
    public TigerHttpClient getTigerClient() {

        //如果已初始化过客户端，无需再次初始化
        String apiKey = getApiKey();
        if (tigerHttpClientMap.containsKey(apiKey)) {
            return tigerHttpClientMap.get(apiKey);
        }
        // 获取配置
        Map<String, String> routeConfig = getRouteConfig();

        // 开启日志. log file name: tiger_openapi.2023-02-22.log
        //ApiLogger.setEnabled(true, "/data/tiger_openapi/logs/");
        // ApiLogger.setDebugEnabled(false);        // 开启debug级别日志
        // The tiger_openapi_config.properties file is stored in your local directory.
        ClientConfig clientConfig = ClientConfig.DEFAULT_CONFIG;
        // clientConfig.isSslSocket = true;         // default is true
        // clientConfig.isAutoGrabPermission = true;// default is true
        // clientConfig.failRetryCounts = 2;        // fail retry count, default is 2
        // clientConfig.timeZone = TimeZoneId.Shanghai; // default time zone
        // clientConfig.language = Language.en_US;  // default language
        // clientConfig.isAutoRefreshToken = false;  // default is false, only support 'TBHK' license
        // clientConfig.refreshTokenIntervalDays = 5; // default is 5; refresh the token every 5 days
        // clientConfig.refreshTokenTime = "12:30:00";  // default is empty, 格式为：HH:mm:ss
        // clientConfig.secretKey = "xxxxxx";// 机构用户私钥

        // 原来旧的使用方式（不使用tiger_openapi_config.properties文件），必须配置tigerId, defaultAccount, privateKey三项，如果同时配置了configFilePath路径properties文件配置内容优先
        // clientConfig.tigerId = "20155918";
        // clientConfig.defaultAccount = "21648203424915730";
        // clientConfig.privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKVEss2VHFHJC7IYN2CIarUPJK9voWpVdGJpkO8yDx6U93BRauI7efPx7PTV3zjAuNpmEnSapNtcq/QyeV0IdYm2Uvu6gDbeJqsbS1ranbYwgSLL/4IZcgK1fc1h8kjGCqqC4kU7DZPI/fse2bE8nOHF4eDLB3CgbQlpoT8UuIqbAgMBAAECgYBeekRhJCHZWz41ZISbycB+mxaUuBMlr45mCAVTyGE+UViWu+SHSgrwetfEK1N9pSbHq1xXjmQ6BuNCKWyZ05Ek5m/8oeB3mHZB7fiU9zD7ykkIq6mG/c7j23ASm5i1AV4BG/g0dwmwlPoSP2pBDpZ5leteToDUYBf//h9Ax6Tg0QJBAPfc9svgDBrzgPV971JDDl5Hsq0LV69YCWTimX+JEeB0mp7JiG1+ys8bLdJFyzrhcq8S0HkwLQD38NYaEG+hUCkCQQCqsZwZulIZz0iKG6bp0G4WQx7mCj0gvGJLpxzDfwO7AKlAhKtXxogZQII/nKz6sMQxJMohrH84oZXaPBZ05I0jAkEA4IBjiYYFcOPdin3mprvV589JJzN+2HMFDzuvjLS6XHNBGVGxIHwXdj9H4Y0V5t8M4UZJTXs1SAtLOKGjAXk7AQJAUCBHkpwv5gWzm4EXfTJmOvUPEBVGVJOZ4MF1cx+wdJv+11ZvyChdwtzRR2MYoCVSM299owkCP2c6nNMQB5cx2QJAP9rDnvr9EZsTpMBXr9T53G+KWM0Sznk1mOg4+lQb3ubxsy9yuYkCpBaPhLlLdqt4QkZSveMW7Subk/w40TLmhQ==";
        clientConfig.tigerId = routeConfig.get("tigerId");
        clientConfig.defaultAccount = routeConfig.get("account");
        clientConfig.privateKey = routeConfig.get("privateKey");
        TigerHttpClient tigerHttpClient = TigerHttpClient.getInstance().clientConfig(clientConfig);
        tigerHttpClientMap.put(apiKey, tigerHttpClient);
        return tigerHttpClient;
    }

    @Override
    public Object getAllPosition(PositionListQueryReq req) throws Exception {

        TigerHttpClient client = getTigerClient();

        PositionsRequest request = new PositionsRequest();
        String bizContent = AccountParamBuilder.instance()
                .account(req.getAccountId())
                .symbol(req.getSymbol())
                .secType((SecType) ProductTypeEnums.getSecType(req.getProductType(), RequestContext.getChannel()))
                .market((Market) MarketEnums.getChannelMarket(req.getMarket(), RequestContext.getChannel()))
                .buildJson();
        request.setBizContent(bizContent);

        PositionsResponse response = client.execute(request);
        return response.getItem();
    }

    @Override
    public Object getAccountSummary(AccountSummaryReq accountSummaryReq) {

        //客户端获取
        TigerHttpClient client = getTigerClient();
        if (StringUtils.isEmpty(accountSummaryReq.getAccountId())) {
            // 获取配置
            Map<String, String> routeConfig = getRouteConfig();
            //账户id不传时，默认使用client config配置账户
            accountSummaryReq.setAccountId(routeConfig.get("account"));
        }

        //账户类型 GLOBAL：环球账号；STANDARD：综合账号；PAPER：模拟账号
        String accountType = getAccountType(accountSummaryReq.getAccountId());
        //账户不存在
        if (StringUtils.isEmpty(accountType)) {
            return null;
        }
        //环球账户资产
        if (AccountType.GLOBAL.name().equals(accountType)) {
            TigerHttpRequest request = new TigerHttpRequest(MethodName.ASSETS);

            String bizContent = AccountParamBuilder.instance()
                    .account(accountSummaryReq.getAccountId())
                    .segment(accountSummaryReq.isSegment())
                    .marketValue(accountSummaryReq.isSegment())
                    .buildJson();

            request.setBizContent(bizContent);
            TigerHttpResponse response = client.execute(request);

            // 解析具体字段
            JSONArray assets = JSONObject.parseObject(response.getData()).getJSONArray("items");
            JSONObject asset1 = assets.getJSONObject(0);
            return asset1;
        } else {
            //综合/模拟账号获取资产
            PrimeAssetRequest assetRequest = PrimeAssetRequest.buildPrimeAssetRequest(accountSummaryReq.getAccountId(), Currency.USD);
            assetRequest.setConsolidated(Boolean.TRUE);
            PrimeAssetResponse primeAssetResponse = client.execute(assetRequest);
            return primeAssetResponse.getItem();
        }
    }

    //获取账户列表
    @Override
    public Object accounts(AccountsReq accountsReq) {

        //客户端获取
        TigerHttpClient client = getTigerClient();

        TigerHttpRequest request = new TigerHttpRequest(MethodName.ACCOUNTS);
        String bizContent;
        if (StringUtils.isEmpty(accountsReq.getAccountId())) {
            //查询所有账号列表
            bizContent = AccountParamBuilder.instance().buildJsonWithoutDefaultAccount();
        } else {
            //不支持传模拟账户
            bizContent = AccountParamBuilder.instance()
                    .account(accountsReq.getAccountId())
                    .buildJsonWithoutDefaultAccount();
        }

        request.setBizContent(bizContent);
        TigerHttpResponse response = client.execute(request);

        //获取账户列表
        JSONArray accounts = JSONObject.parseObject(response.getData()).getJSONArray("items");
        return accounts;
    }

    @Override
    public Object availableFunds(AvailableFundsReq availableFundsReq) {

        if (StringUtils.isEmpty(availableFundsReq.getAccountId())) {
            // 获取配置
            Map<String, String> routeConfig = getRouteConfig();
            //账户id不传时，默认使用client config配置账户
            availableFundsReq.setAccountId(routeConfig.get("account"));
        }

        //只支持 综合/模拟账号
        //账户类型 GLOBAL：环球账号；STANDARD：综合账号；PAPER：模拟账号
        String accountType = getAccountType(availableFundsReq.getAccountId());
        //账户不存在或不支持
        if (StringUtils.isEmpty(accountType) || AccountType.GLOBAL.name().equals(accountType)) {
            return null;
        }
        //客户端获取
        TigerHttpClient client = getTigerClient();

        SegmentFundAvailableRequest request = SegmentFundAvailableRequest.buildRequest(
                SegmentType.valueOf(availableFundsReq.getFromSegment()), Currency.valueOf(availableFundsReq.getCurrency()));

        SegmentFundAvailableResponse response = client.execute(request);
        System.out.println(JSONObject.toJSONString(response));
        return response.getSegmentFundAvailableItems();
    }

    /**
     * 获取账户类型GLOBAL：环球账号；STANDARD：综合账号；PAPER：模拟账号
     */
    public String getAccountType(String accountId) {

        if (StringUtils.isEmpty(accountId)) {
            // 获取配置
            Map<String, String> routeConfig = getRouteConfig();
            accountId = routeConfig.get("account");
        }
        //获取账户列表
        AccountsReq accountsReq = new AccountsReq();
        JSONArray accounts = (JSONArray) accounts(accountsReq);
        //账户不存在
        if (CollectionUtils.isEmpty(accounts)) {
            return null;
        }
        for (Object object : accounts) {
            JSONObject account = (JSONObject) object;
            if (account.getString("account").equals(accountId)) {
                return account.getString("accountType");
            }
        }
        return null;
    }

    @Override
    public Object getOrderInfo(OrderInfoQueryReq req) throws Exception {

        //客户端获取
        TigerHttpClient client = getTigerClient();
        TigerHttpRequest request = new TigerHttpRequest(MethodName.ORDERS);
        String bizContent = AccountParamBuilder.instance()
                .account(req.getAccount())
                .id(req.getId())
                .secretKey(req.getSecretKey())
                .isShowCharges(Boolean.TRUE)
                .buildJson();

        request.setBizContent(bizContent);
        TigerHttpResponse response = client.execute(request);
        JSONArray accounts = JSONObject.parseObject(response.getData()).getJSONArray("items");
        return accounts;
    }

    @Override
    public Object getOrderList(OrderListQueryReq req) throws Exception {

        Integer maxLimit = 300;
        JSONArray rtnData = new JSONArray();

        String pageToken = null;
        Integer limit = req.getLimit() == null ? Integer.MAX_VALUE : req.getLimit();

        do {
            //客户端获取
            TigerHttpClient client = getTigerClient();
            TigerHttpRequest request = new TigerHttpRequest(MethodName.ORDERS);
            String bizContent = AccountParamBuilder.instance()
                    .account(req.getAccount())
                    .segType(EnumUtils.safeValueOfIgnoreCase(SegmentType.class, req.getSegType()))
                    .secType(EnumUtils.safeValueOfIgnoreCase(SecType.class, req.getSecType()))
                    .market(EnumUtils.safeValueOfIgnoreCase(Market.class, req.getMarket()))
                    .symbol(req.getSymbol())
                    .expiry(req.getExpiry())
                    .strike(DataUtils.decimalToDouble(req.getStrike()))
                    .right(req.getRight())
                    .startDate(req.getStartDate())
                    .endDate(req.getEndDate())
                    .states(req.getStatus())
                    .secretKey(req.getSecretKey())
                    .limit(limit >= maxLimit? maxLimit : limit)
                    .pageToken(pageToken)
                    .buildJson();

            request.setBizContent(bizContent);
            TigerHttpResponse response = client.execute(request);
            JSONObject rtnRes = JSONObject.parseObject(response.getData());

            limit = limit - maxLimit;
            JSONArray items = rtnRes.getJSONArray("items");
            rtnData.addAll(items);
            // 本次返回的列表记录数不够限制或者不需要分页时直接结束循环
            if (limit <=0 || items.size() <= maxLimit) {
                break;
            }

            pageToken = rtnRes.getString("nextPageToken");
            // 查询下一页用的token，获取不到也结束
            if (StringUtils.isEmpty(pageToken)) {
                break;
            }
        } while (true);
        return rtnData;
    }

    @Override
    public Object modifyOrder(ModifyOrderReq req) throws Exception {

        //客户端获取
        TigerHttpClient client = getTigerClient();

        TigerHttpRequest request = new TigerHttpRequest(MethodName.MODIFY_ORDER);
        String bizContent = TradeParamBuilder.instance()
                .account(req.getAccount())
                .id(req.getId())
                .totalQuantity(req.getTotalQuantity())
                .totalQuantityScale(req.getTotalQuantityScale())
                .limitPrice(NumberUtils.toDouble(req.getLimitPrice()))
                .auxPrice(NumberUtils.toDouble(req.getAuxPrice()))
                .trailingPercent(NumberUtils.toDouble(req.getTrailingPercent()))
                .secretKey(req.getSecretKey())
                .buildJson();

        request.setBizContent(bizContent);
        TigerHttpResponse response = client.execute(request);
        JSONObject orderRes = JSONObject.from(response.getData());
        return orderRes;
    }

    public Object cancelOrder(CancelOrderReq req) throws Exception {


        //客户端获取
        TigerHttpClient client = getTigerClient();

        TigerHttpRequest request = new TigerHttpRequest(MethodName.CANCEL_ORDER);
        String bizContent = TradeParamBuilder.instance()
                .account(req.getAccount())
                .id(req.getId())
                .secretKey(req.getSecretKey())
                .buildJson();

        request.setBizContent(bizContent);
        TigerHttpResponse response = client.execute(request);
        JSONObject orderRes = JSONObject.from(response.getData());
        return orderRes;
    }

    @Override
    public Object createOrder(CreateOrderReq req) throws Exception {

        //客户端获取
        TigerHttpClient client = getTigerClient();

        // 编辑合约
        ContractItem contractItem = new ContractItem();
        contractItem.setSymbol(req.getSymbol());
        contractItem.setSecType(req.getSecType());
        contractItem.setCurrency(req.getCurrency());
        contractItem.setRight(req.getRight());
        contractItem.setStrike(NumberUtils.createDouble(req.getStrike()));
        contractItem.setExchange(req.getExchange());
        contractItem.setExpiry(req.getExpiry());
        contractItem.setMultiplier(DataUtils.decimalToDouble(req.getMultiplier()));
        contractItem.setLocalSymbol(req.getLocalSymbol());
        contractItem.setMarket(req.getMarket());

        // 设置数据
        TradeOrderModel tradeOrderModel = TradeOrderRequest.buildTradeOrderModel(
                req.getAccount(),
                contractItem,
                EnumUtils.getEnumByField(ActionType.class, "action", req.getAction()),
                req.getTotalQuantity(),
                req.getTotalQuantityScale());

        tradeOrderModel.setCashAmount(DataUtils.decimalToDouble(req.getCashAmount()));
        tradeOrderModel.setLimitPrice(DataUtils.decimalToDouble(req.getLimitPrice()));
        tradeOrderModel.setAuxPrice(DataUtils.decimalToDouble(req.getAuxPrice()));
        tradeOrderModel.setTrailingPercent(DataUtils.decimalToDouble(req.getTrailingPercent()));
        tradeOrderModel.setOutsideRth(Boolean.valueOf(req.getOutsideRTH()));
        tradeOrderModel.setTradingSessionType(EnumUtils.safeValueOfIgnoreCase(TradingSessionType.class, req.getTradingSessionType()));
        tradeOrderModel.setAdjustLimit(DataUtils.decimalToDouble(req.getAdjustLimit()));
        tradeOrderModel.setMarket(req.getMarket());
        tradeOrderModel.setCurrency(EnumUtils.safeValueOfIgnoreCase(Currency.class, req.getCurrency()));
        tradeOrderModel.setTimeInForce(EnumUtils.safeValueOfIgnoreCase(TimeInForce.class, req.getTimeInForce()));
        tradeOrderModel.setExpireTime(req.getExpireTime());
        tradeOrderModel.setSecretKey(req.getSecretKey());
        tradeOrderModel.setUserMark(req.getUserMark());

        tradeOrderModel.setAttachType(EnumUtils.safeValueOfIgnoreCase(AttachType.class, req.getAttachType()));
        tradeOrderModel.setProfitTakerPrice(req.getProfitTakerPrice());
        tradeOrderModel.setProfitTakerTif(EnumUtils.safeValueOfIgnoreCase(TimeInForce.class, req.getProfitTakerTif()));
        tradeOrderModel.setProfitTakerRth(req.getProfitTakerRth());
        tradeOrderModel.setStopLossPrice(DataUtils.decimalToDouble(req.getStopLossPrice()));
        tradeOrderModel.setStopLossLimitPrice(DataUtils.decimalToDouble(req.getStopLossLimitPrice()));
        tradeOrderModel.setStopLossTif(EnumUtils.safeValueOfIgnoreCase(TimeInForce.class, req.getStopLossTif()));
        tradeOrderModel.setStopLossTrailingPercent(DataUtils.decimalToDouble(req.getStopLossTrailingPercent()));
        tradeOrderModel.setStopLossTrailingAmount(DataUtils.decimalToDouble(req.getStopLossTrailingAmount()));
        tradeOrderModel.setAlgoParams(req.getAlgoParams());

        TradeOrderRequest request = new TradeOrderRequest();
        request.setApiModel(tradeOrderModel);
        // 发送请求
        TradeOrderResponse response = client.execute(request);
        JSONObject orderRes = JSONObject.from(response.getItem());
        return orderRes;
    }

    public Object whatifOrder(WhatifOrderReq req) throws Exception {

        //客户端获取
        TigerHttpClient client = getTigerClient();

        // 编辑合约
        ContractItem contractItem = new ContractItem();
        contractItem.setSymbol(req.getSymbol());
        contractItem.setSecType(req.getSecType());
        contractItem.setCurrency(req.getCurrency());
        contractItem.setRight(req.getRight());
        contractItem.setStrike(NumberUtils.createDouble(req.getStrike()));
        contractItem.setExchange(req.getExchange());
        contractItem.setExpiry(req.getExpiry());
        contractItem.setMultiplier(DataUtils.decimalToDouble(req.getMultiplier()));
        contractItem.setLocalSymbol(req.getLocalSymbol());
        contractItem.setMarket(req.getMarket());

        // 设置数据
        TradeOrderModel tradeOrderModel = TradeOrderRequest.buildTradeOrderModel(
                req.getAccount(),
                contractItem,
                EnumUtils.getEnumByField(ActionType.class, "action", req.getAction()),
                req.getTotalQuantity(),
                req.getTotalQuantityScale());

        tradeOrderModel.setCashAmount(DataUtils.decimalToDouble(req.getCashAmount()));
        tradeOrderModel.setLimitPrice(DataUtils.decimalToDouble(req.getLimitPrice()));
        tradeOrderModel.setAuxPrice(DataUtils.decimalToDouble(req.getAuxPrice()));
        tradeOrderModel.setTrailingPercent(DataUtils.decimalToDouble(req.getTrailingPercent()));
        tradeOrderModel.setOutsideRth(Boolean.valueOf(req.getOutsideRTH()));
        tradeOrderModel.setTradingSessionType(EnumUtils.safeValueOfIgnoreCase(TradingSessionType.class, req.getTradingSessionType()));
        tradeOrderModel.setAdjustLimit(DataUtils.decimalToDouble(req.getAdjustLimit()));
        tradeOrderModel.setMarket(req.getMarket());
        tradeOrderModel.setCurrency(EnumUtils.safeValueOfIgnoreCase(Currency.class, req.getCurrency()));
        tradeOrderModel.setTimeInForce(EnumUtils.safeValueOfIgnoreCase(TimeInForce.class, req.getTimeInForce()));
        tradeOrderModel.setExpireTime(req.getExpireTime());
        tradeOrderModel.setSecretKey(req.getSecretKey());
        tradeOrderModel.setUserMark(req.getUserMark());

        tradeOrderModel.setAttachType(EnumUtils.safeValueOfIgnoreCase(AttachType.class, req.getAttachType()));
        tradeOrderModel.setProfitTakerPrice(req.getProfitTakerPrice());
        tradeOrderModel.setProfitTakerTif(EnumUtils.safeValueOfIgnoreCase(TimeInForce.class, req.getProfitTakerTif()));
        tradeOrderModel.setProfitTakerRth(req.getProfitTakerRth());
        tradeOrderModel.setStopLossPrice(DataUtils.decimalToDouble(req.getStopLossPrice()));
        tradeOrderModel.setStopLossLimitPrice(DataUtils.decimalToDouble(req.getStopLossLimitPrice()));
        tradeOrderModel.setStopLossTif(EnumUtils.safeValueOfIgnoreCase(TimeInForce.class, req.getStopLossTif()));
        tradeOrderModel.setStopLossTrailingPercent(DataUtils.decimalToDouble(req.getStopLossTrailingPercent()));
        tradeOrderModel.setStopLossTrailingAmount(DataUtils.decimalToDouble(req.getStopLossTrailingAmount()));
        tradeOrderModel.setAlgoParams(req.getAlgoParams());

        TradeOrderPreviewRequest request = new TradeOrderPreviewRequest();
        request.setApiModel(tradeOrderModel);
        // 发送请求
        TradeOrderPreviewResponse response = client.execute(request);
        JSONObject orderRes = JSONObject.from(response.getItem());
        return orderRes;
    }

    /**
     * 获取最大可交易数量
     */
    @Override
    public Object getMaximumTradableQuantity(MaximumTradableQuantityReq req) {
        if (StringUtils.isEmpty(req.getAccount())) {
            // 获取配置
            Map<String, String> routeConfig = getRouteConfig();
            //账户id不传时，默认使用client config配置账户
            req.setAccount(routeConfig.get("account"));
        }
        TigerHttpClient client = getTigerClient();
        SecType secType = null;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getSecType())) {
            secType = SecType.valueOf(req.getSecType());
        }
        OrderType orderType = null;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getOrderType())) {
            orderType = OrderType.valueOf(req.getOrderType());
        }
        ActionType actionType = null;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(req.getAction())) {
            actionType = ActionType.valueOf(req.getAction());
        }
        EstimateTradableQuantityRequest estimateTradableQuantityRequest = EstimateTradableQuantityRequest
                .buildRequest(req.getAccount(), secType, req.getSymbol(), actionType, orderType, req.getLimitPrice(), req.getStopPrice());
        EstimateTradableQuantityResponse response = client.execute(estimateTradableQuantityRequest);
        return response.getTradableQuantityItem();
    }

    @Override
    public Object getAccountPerformance(AccountPerformanceReq req) throws Exception {
        TigerHttpClient client = getTigerClient();

        PrimeAnalyticsAssetRequest request = PrimeAnalyticsAssetRequest.buildPrimeAnalyticsAssetRequest(req.getAccountId())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .segType(EnumUtils.safeValueOfIgnoreCase(SegmentType.class, req.getSegType()))
                .currency(EnumUtils.safeValueOfIgnoreCase(Currency.class, req.getCurrency()));

        PrimeAnalyticsAssetResponse response = client.execute(request);
        return response.getItem();
    }

    @Override
    public Object getHistoryTransaction(HistoryTransactionReq req) throws Exception {
        TigerHttpClient client = getTigerClient();
        TigerHttpRequest request = new TigerHttpRequest(MethodName.ORDER_TRANSACTIONS);
        String bizContent = AccountParamBuilder.instance()
                .account(req.getAccountId())
                .orderId(Integer.valueOf(req.getOrderId()))
                .symbol(req.getSymbol())
                .secType(EnumUtils.safeValueOfIgnoreCase(SecType.class, req.getSecType()))
                .limit(req.getLimit())
                .expiry(req.getExpiry())
                .right(req.getRight())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .buildJson();
        request.setBizContent(bizContent);
        TigerHttpResponse response = client.execute(request);
        return JSON.parseObject(response.getData()).getJSONArray("items");
    }

    @Override
    public Object getContractList(ContractListReq req) {
        TigerHttpClient client = getTigerClient();
        if (org.apache.commons.lang3.StringUtils.isBlank(req.getSymbol())) {
            throw new OpenApiException(REQUIRED_SYMBOL);
        }
        // 如果没有账号 id，获取期权/窝轮/牛熊证合约列表
        if (org.apache.commons.lang3.StringUtils.isBlank(req.getAccountId())) {
            return getQuoteContract(req);
        }
        List<String> symbolList = Arrays.stream(req.getSymbol().split(",")).distinct().toList();
        if (symbolList.size() != 1) {
            // 多个合约列表
            ContractsModel models = new ContractsModel(symbolList, req.getSecType());
            ContractsRequest contractsRequest = ContractsRequest.newRequest(models, req.getAccountId());
            ContractsResponse contractsResponse = client.execute(contractsRequest);
            return contractsResponse.getItems();
        }
        // 单个合约列表
        if (req.getQueryContract() == null || Integer.valueOf(1).equals(req.getQueryContract())) {
            // ContractModel(String symbol, String secType, String currency, String expiry, Double strike,
            //      String right)
            ContractModel model = new ContractModel(req.getSymbol(), req.getSecType(), req.getCurrency(), req.getExpiry(), req.getStrike(), req.getRight());
            ContractRequest contractRequest = ContractRequest.newRequest(model, req.getAccountId());
            ContractResponse contractResponse = client.execute(contractRequest);
            return contractResponse.getItem();
        }
        // 获取期权/窝轮/牛熊证合约列表
        return getQuoteContract(req);
    }

    private Object getQuoteContract(ContractListReq req) {
        SecType secType = EnumUtils.safeValueOfIgnoreCase(SecType.class, req.getSecType());
        Language lang = EnumUtils.safeValueOfIgnoreCase(Language.class, req.getLang());
        QuoteContractModel model = new QuoteContractModel(req.getSymbol(), secType, req.getExpiry(), lang);

        QuoteContractRequest request = new QuoteContractRequest();
        request.setApiModel(model);
        QuoteContractResponse response = getTigerClient().execute(request);
        return response.getContractItems();
    }
}
