package com.ebang.openapi.channel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.config.USmartConstants;
import com.ebang.openapi.config.USmartOpenApiService;
import com.ebang.openapi.entity.USmartSymbol;
import com.ebang.openapi.req.*;
import com.ebang.openapi.util.DataUtils;
import com.ebang.openapi.util.MapToJsonUtils;
import com.ebang.openapi.util.ObjectToMapUtils;
import com.longport.trade.BalanceType;
import com.longport.trade.CashFlow;
import com.longport.trade.GetCashFlowOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 盈立证券
 *
 * @author chenlanqing
 */
@Component("usmart")
public class USmartChannel extends BaseChannel {

    @Autowired
    USmartOpenApiService uSmartOpenApiService;
    final Map<String, USmartOpenApiService.Token> tokenMap = new ConcurrentHashMap<>();

    Map<String, Map<String, USmartSymbol>> symbolMap = new HashMap<>();

    @Override
    public Object getAllPosition(PositionListQueryReq req) throws Exception {
        var params = new HashMap<String, Object>();
        //设置用户信息
        setUserInfo(params);
        params.put("exchangeType", req.getExchangeType());
        params.put("url", USmartConstants.stock_holding);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    @Override
    public Object getAccountPerformance(AccountPerformanceReq req) throws Exception {
        var params = new HashMap<String, Object>();
        //设置用户信息
        setUserInfo(params);
        params.put("exchangeType", req.getExchangeType());
        params.put("url", USmartConstants.stock_asset_list);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * 获取标的信息
     */
    private USmartSymbol getSymbol(Integer exchangeType, String symbol) {
        String market = null;
        switch (exchangeType) {
            case 0:
                market = "hk";
                break;
            case 5:
                market = "us";
                break;
            case 6:
                market = "sh";
                break;
            case 7:
                market = "sz";
        }
        // hk:香港 us:美国 sh:上海 sz:深圳
        if (StringUtils.isEmpty(market) || StringUtils.isEmpty(symbol)) {
            throw new RuntimeException("标的信息无法获取");
        }

        if (!symbolMap.containsKey(exchangeType)) {
            Map<String, Object> params = new HashMap();

            //设置用户信息
            setUserInfo(params);
            params.put("market", exchangeType);
            params.put("url", USmartConstants.basicinfo);
            USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
            openHttpRequest.setParameters(params);
            USmartOpenApiService.OpenHttpResponse<JSONObject> openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
            if (!"0".equals(openHttpResponse.getCode()) ||  openHttpResponse.getData() == null || CollectionUtils.isEmpty(
                    openHttpResponse.getData().getJSONArray("list"))) {
                throw new RuntimeException("标的信息获取失败");
            }
            JSONArray list = openHttpResponse.getData().getJSONArray("list");
            Map<String, USmartSymbol> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                JSONObject item = list.getJSONObject(i);
                String symbolKey = item.getString("symbol");
                USmartSymbol uSmartSymbol = item.toJavaObject(USmartSymbol.class);
                map.put(symbolKey, uSmartSymbol);
            }
            symbolMap.put(market, map);
        }

        USmartSymbol uSmartSymbol = symbolMap.get(exchangeType).get(symbol);
        if (uSmartSymbol == null) {
            throw new RuntimeException("标的信息无法识别");
        }
        return uSmartSymbol;
    }

    @Override
    public Object balances(BalanceReq req) {

        Map<String, Object> params = new HashMap<String, Object>();

        //设置用户信息
        setUserInfo(params);
        params.put("exchangeType", req.getExchangeType());
        params.put("url", USmartConstants.stock_asset);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);

        return openHttpResponse.getData();
    }
    @Override
    public Object getOrderInfo(OrderInfoQueryReq req) throws Exception {

        // 查询当日委托订单
        Map<String, Object> todayParams = new HashMap();
        //设置用户信息
        setUserInfo(todayParams);
        todayParams.put("entrustId",req.getEntrustId());
        todayParams.put("serialNo", req.getSerialNo());
        todayParams.put("url", USmartConstants.order_detail);

        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(todayParams);
        USmartOpenApiService.OpenHttpResponse response = uSmartOpenApiService.openapi(openHttpRequest);
        return response;
    }
    @Override
    public Object getOrderList(OrderListQueryReq req) throws Exception {
        // 查询当日委托订单
        Map<String, Object> todayParams = new HashMap();
        //设置用户信息
        setUserInfo(todayParams);
        todayParams.put("exchangeType", req.getExchangeType());
        todayParams.put("url", USmartConstants.today_entrust);

        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(todayParams);
        USmartOpenApiService.OpenHttpResponse todayResponse = uSmartOpenApiService.openapi(openHttpRequest);

        // 查询历史委托订单
        Map<String, Object> hisParams = new HashMap();
        //设置用户信息
        setUserInfo(hisParams);
        hisParams.put("exchangeType", req.getExchangeType());
        hisParams.put("url", USmartConstants.his_entrust);

        openHttpRequest.setParameters(hisParams);
        USmartOpenApiService.OpenHttpResponse hisResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return hisResponse;
    }
    @Override
    public Object modifyOrder(ModifyOrderReq req) throws Exception {

        OrderInfoQueryReq orderInfoQueryReq = new OrderInfoQueryReq();
        orderInfoQueryReq.setEntrustId(req.getEntrustId());
        Object orderInfo = getOrderInfo(orderInfoQueryReq);
        if (orderInfo == null) {
            throw new RuntimeException("查询不到订单");
        }
        USmartOpenApiService.OpenHttpResponse<JSONObject> order = (USmartOpenApiService.OpenHttpResponse<JSONObject>)orderInfo;
        if (order.getCode() != "0") {
            throw new RuntimeException("查询订单异常");
        }
        JSONObject orderData = order.getData();
        Map<String, Object> request = new HashMap();
        //设置用户信息
        setUserInfo(request);
        // 正常下单
        request.put("url", USmartConstants.modify_order);
        request.put("actionType", 1);
        request.put("entrustAmount", DataUtils.getDecimalValue(req.getEntrustAmount(), orderData.getBigDecimal("entrustAmount")));
        request.put("entrustId", req.getEntrustId());
        request.put("entrustPrice", DataUtils.getDecimalValue(req.getEntrustPrice(), orderData.getBigDecimal("entrustPrice")));
        request.put("password", req.getPassword());
        request.put("forceEntrustFlag", req.getForceEntrustFlag());

        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(request);
        USmartOpenApiService.OpenHttpResponse todayResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return todayResponse;
    }
    @Override
    public Object cancelOrder(CancelOrderReq req) throws Exception {

        USmartOpenApiService.OpenHttpResponse<JSONObject> item;

        // 无法判断订单是否碎股订单
        // 正常订单撤单
        if (true) {
            Map<String, Object> request = new HashMap();
            //设置用户信息
            setUserInfo(request);
            // 正常订单撤单
            request.put("url", USmartConstants.modify_order);
            request.put("actionType", 0);
            request.put("entrustAmount", "0");
            request.put("entrustId", req.getEntrustId());
            request.put("entrustPrice", "0");
            request.put("password", req.getPassword());

            USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
            openHttpRequest.setParameters(request);
            USmartOpenApiService.OpenHttpResponse<JSONObject> response = uSmartOpenApiService.openapi(openHttpRequest);
            if ("0".equals(response.getCode())) {
                return response;
            }
            item = response;
        }
        // 碎股撤单
        if (true) {
            Map<String, Object> request = new HashMap();
            //设置用户信息
            setUserInfo(request);
            // 正常订单撤单
            request.put("url", USmartConstants.odd_modify);
            request.put("actionType", 0);
            request.put("oddId", req.getEntrustId());

            USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
            openHttpRequest.setParameters(request);
            USmartOpenApiService.OpenHttpResponse<JSONObject> response = uSmartOpenApiService.openapi(openHttpRequest);
            if ("0".equals(response.getCode())) {
                return response;
            }
        }
        return item;
    }

    @Override
    public Object createOrder(CreateOrderReq req) throws Exception {

        Map<String, Object> request = new HashMap();
        //设置用户信息
        setUserInfo(request);
        // 获取标的
        USmartSymbol symbol = getSymbol(NumberUtils.createInteger(req.getExchangeType()), req.getSymbol());
        // 校验是否为需要碎股票下单 1.市场为香港 美股 2.下单数量为碎股
        if (List.of(0, 5).contains(req.getExchangeType()) &&
                BigDecimal.ZERO.compareTo(req.getEntrustAmount().remainder(BigDecimal.valueOf(symbol.getLotSize()))) != 0) {
            // 碎股下单
            request.put("url", USmartConstants.odd_entrust);
            request.put("entrustAmount", req.getEntrustAmount());
            request.put("entrustPrice", req.getEntrustPrice());
            request.put("entrustType", req.getEntrustType());
            request.put("exchangeType", req.getExchangeType());
            request.put("stockCode", req.getStockCode());
        }
        else {
            // 正常下单
            request.put("url", USmartConstants.order);
            request.put("serialNo", req.getSerialNo());
            request.put("entrustAmount", req.getEntrustAmount());
            request.put("entrustPrice", req.getEntrustPrice());
            request.put("entrustProp", req.getEntrustProp());
            request.put("entrustType", req.getEntrustType());
            request.put("exchangeType", req.getExchangeType());
            request.put("stockCode", req.getStockCode());
            request.put("password", req.getPassword());
            request.put("stockName", req.getStockName());
            request.put("forceEntrustFlag", req.getForceEntrustFlag());
            request.put("sessionType", req.getSessionType());
            request.put("orderType", req.getTimeInForce());
            request.put("validDate", req.getValidDate());
            request.put("exchange", req.getExchange());
        }

        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(request);
        USmartOpenApiService.OpenHttpResponse todayResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return todayResponse;
    }

    @Override
    public Object getAccountSummary(AccountSummaryReq req) {
        Map<String, Object> params = new HashMap<String, Object>();

        //设置用户信息
        setUserInfo(params);
        params.put("exchangeType", Integer.parseInt(req.getExchangeType()));
        params.put("url", USmartConstants.user_asset_aggregation);
        //请求唯一标识，防重
        params.put("X-Request-Id", UUID.randomUUID());
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);

        return openHttpResponse.getData();
    }

    @Override
    public Object margins(MarginsReq req) {
        Map<String, Object> params = new HashMap<String, Object>();

        //设置用户信息
        setUserInfo(params);
        //设置body参数
        params.put("exchangeType", req.getExchangeType());
        params.put("url", USmartConstants.margin_detail);
        //请求唯一标识，防重
        params.put("X-Request-Id", UUID.randomUUID());
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);

        return openHttpResponse.getData();
    }

    private void setUserInfo(Map<String, Object> params) {
        // 获取配置
        Map<String, String> routeConfig = getRouteConfig();
        String apiKey = getApiKey();
        // 2. token获取
        USmartOpenApiService.Token token = tokenMap.compute(apiKey, (key, existingToken) -> {
            // 如果 token 不存在或即将过期（60秒内），则刷新
            if (existingToken == null || isTokenExpired(existingToken)) {
                USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
                Map<String, Object> loginParams = new HashMap<>();
                loginParams.put("phoneNumber", routeConfig.get("phoneNumber"));
                loginParams.put("password", routeConfig.get("password"));
                openHttpRequest.setParameters(loginParams);
                return uSmartOpenApiService.accessToken(openHttpRequest);
            }
            return existingToken; // 否则返回现有 token
        });
        //设置token
        params.put("token", token.getToken());
        //设置私钥
        params.put("privateKey", routeConfig.get("privateKey"));
        //设置公钥
        params.put("publicKey", routeConfig.get("publicKey"));
        //others
    }

    // 判断 token 是否即将过期
    private boolean isTokenExpired(USmartOpenApiService.Token token) {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        return (currentTimeSeconds - token.getExpireTime()) <= 30;
    }

    /**
     * 解锁交易
     */
    @Override
    public Object unlockTrading(UnlockTradingReq req) throws Exception {
        Map<String, String> routeConfig = getRouteConfig();
        Map<String, Object> params = new HashMap<String, Object>();
        //设置用户信息
        setUserInfo(params);
        params.put("password", routeConfig.get("password"));
        params.put("X-Request-Id", UUID.randomUUID().toString());
        params.put("url", USmartConstants.trade_login);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * 修改交易密码
     */
    @Override
    public Object updateTradePassword(UpdateOrResetTradePasswordReq req) throws Exception {
        Map<String, Object> params = ObjectToMapUtils.convert(req);
        //设置用户信息
        setUserInfo(params);
        params.put("X-Request-Id", UUID.randomUUID().toString());
        params.put("url", USmartConstants.update_trade_password);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * 重置交易密码
     */
    @Override
    public Object resetTradePassword(UpdateOrResetTradePasswordReq req) throws Exception {
        Map<String, Object> params = ObjectToMapUtils.convert(req);
        //设置用户信息
        setUserInfo(params);
        params.put("X-Request-Id", UUID.randomUUID().toString());
        params.put("url", USmartConstants.reset_trade_password);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * 获取最大可交易数量
     */
    @Override
    public Object getMaximumTradableQuantity(MaximumTradableQuantityReq req) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        //设置用户信息
        setUserInfo(params);
        params.put("entrustPrice", req.getEntrustPrice());
        params.put("entrustProp", req.getEntrustProp());
        params.put("exchangeType", req.getExchangeType());
        params.put("stockCode", req.getStockCode());
        params.put("url", USmartConstants.trade_quantity);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * 获取股票抵押比率列表
     */
    @Override
    public Object mortgageList(MortgageListReq req) throws Exception {
        Map<String, Object> params = ObjectToMapUtils.convert(req);
        params.put("pageSizeZero", true);
        JSONObject convert = MapToJsonUtils.convert(params);
        //设置用户信息
        setUserInfo(params);
        params.put("url", USmartConstants.mortgage_list);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        openHttpRequest.setJsonObject(convert);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * 查询汇率
     */
    @Override
    public Object getRate(RateReq req) {
        Map<String, Object> params = new HashMap<>();
        //设置用户信息
        setUserInfo(params);
        params.put("url", USmartConstants.currency_exchange_info);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * 获取新股详细信息
     */
    @Override
    public Object ipoInfo(IpoInfoReq req) throws Exception {
        Map<String, Object> params = ObjectToMapUtils.convert(req);
        JSONObject convert = MapToJsonUtils.convert(params);
        //设置用户信息
        setUserInfo(params);
        params.put("url", USmartConstants.ipo_info);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        openHttpRequest.setJsonObject(convert);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * 新股认购
     */
    @Override
    public Object applyIpo(ApplyIpoReq req) throws Exception {
        Map<String, Object> params = ObjectToMapUtils.convert(req);
        JSONObject convert = MapToJsonUtils.convert(params);
        //设置用户信息
        setUserInfo(params);
        params.put("url", USmartConstants.apply_ipo);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        openHttpRequest.setJsonObject(convert);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * ipo改单/撤单
     */
    @Override
    public Object modifyIpo(ModifyIpoReq req) throws Exception {
        Map<String, Object> params = ObjectToMapUtils.convert(req);
        JSONObject convert = MapToJsonUtils.convert(params);
        //设置用户信息
        setUserInfo(params);
        params.put("url", USmartConstants.modify_ipo);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        openHttpRequest.setJsonObject(convert);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    /**
     * 获取客户ipo申购列表
     */
    @Override
    public Object ipoRecordList(IpoRecordListReq req) throws Exception {
        JSONArray jsonArray = new JSONArray();
        int pageNum = 1;
        int pageSize = 20;
        int maxRecords = 10000;
        while (jsonArray.size() < maxRecords) {
            Map<String, Object> params = ObjectToMapUtils.convert(req);
            params.put("pageNum", pageNum);
            params.put("pageSize", pageSize);
            JSONObject convert = MapToJsonUtils.convert(params);
            //设置用户信息
            setUserInfo(params);
            params.put("url", USmartConstants.ipo_record_list);
            USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
            openHttpRequest.setParameters(params);
            openHttpRequest.setJsonObject(convert);
            USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
            if (openHttpResponse == null) {
                break;
            }
            Object data = openHttpResponse.getData();
            if (data == null) {
                break;
            }
            JSONObject jsonObject = (JSONObject)data;
            JSONArray list = jsonObject.getJSONArray("list");
            if (list == null) {
                break;
            }
            jsonArray.addAll(list);
            // 如果已经达到最大记录数，或者数据不足一页，则停止查询
            if (jsonArray.size() >= maxRecords || jsonArray.size() < pageSize) {
                break;
            }
            pageNum++;
        }
        return jsonArray;

    }

    /**
     * 获取客户ipo申购明细
     */
    @Override
    public Object ipoRecord(IpoRecordReq req) throws Exception {
        Map<String, Object> params = ObjectToMapUtils.convert(req);
        JSONObject convert = MapToJsonUtils.convert(params);
        //设置用户信息
        setUserInfo(params);
        params.put("url", USmartConstants.ipo_record);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        openHttpRequest.setJsonObject(convert);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }

    @Override
    public Object getHistoryTransaction(HistoryTransactionReq req) throws Exception {
        var params = new HashMap<String, Object>();
        //设置用户信息
        setUserInfo(params);
        params.put("exchangeType", req.getExchangeType());
        params.put("stockCode", req.getSymbol());
        params.put("beginTime", req.getStartDate());
        params.put("endTime", req.getEndDate());
        params.put("pageNum", req.getPageNum());
        params.put("pageSize", req.getLimit());
        params.put("url", USmartConstants.stock_record);
        USmartOpenApiService.OpenHttpRequest openHttpRequest = new USmartOpenApiService.OpenHttpRequest();
        openHttpRequest.setParameters(params);
        USmartOpenApiService.OpenHttpResponse openHttpResponse = uSmartOpenApiService.openapi(openHttpRequest);
        return openHttpResponse.getData();
    }
}
