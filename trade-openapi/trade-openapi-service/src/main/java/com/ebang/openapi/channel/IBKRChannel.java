package com.ebang.openapi.channel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import com.ebang.openapi.req.*;
import com.ebang.openapi.util.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * IB证券
 *
 * @author chenlanqing
 */
@Component("ibkr")
@Slf4j
public class IBKRChannel extends BaseChannel {

    private boolean RUN_TASK = false;

    private String getBaseUrl() {
        // 被调用时启动心跳
        RUN_TASK = true;
        // 获取配置
        Map<String, String> routeConfig = getRouteConfig();
        String host = Objects.requireNonNullElse(routeConfig.get("host"), "https://localhost:5000");
        return host + "/v1/api";
    }

    /**
     * 定时任务保证心跳
     */
    @Scheduled(cron = "0 * * * * ?")
    public void heartbeat() {
        // 没被调用过不启动心跳
        if (!RUN_TASK) {
            return;
        }
        try {
            JSONObject authStatus = getAuthStatus();
            Boolean authenticated = authStatus.getBoolean("authenticated");
            if (authenticated == null || !authenticated) {
                // 初始化认证
                ssodhInit();
            }
        } catch (Exception e) {
            log.error("heartbeat error", e);
        }
    }

    private JSONObject handleResponse(JSONObject res) {
        // 校验是不是正常请求
        // errorCode为内部封装HttpClient接收到异常时的响应
        // error为渠道接收到异常时的响应
        if (res.containsKey("errorCode")) {
            throw new OpenApiException(OpenApiErrorCodeEnums.SYSTEM_ERROR.getErrorCode(), res.getString("errorMessage"));
        }
        else if (res.containsKey("error")){
            throw new OpenApiException(OpenApiErrorCodeEnums.INVALID_CHANNEL_CONFIG.getErrorCode(), res.getString("error"));
        }
        return res;
    }


    /**
     * 获取会话状态
     */
    public JSONObject getAuthStatus() {
        String url = getBaseUrl() + "/iserver/auth/status";
        return HttpClient.post(url);
    }
    /**
     * 初始认证会话
     */
    public JSONObject ssodhInit() {
        String url = getBaseUrl() + "/iserver/auth/ssodh/init";
        Map<String, Object> map = new HashMap<>();
        map.put("publish", "True");
        map.put("compete", "True");
        return HttpClient.post(url);
    }
    @Override
    public Object getOrderInfo(OrderInfoQueryReq req) throws Exception {

        String url = getBaseUrl() + "/iserver/account/order/status/" + req.getOrderId();
        return handleResponse(HttpClient.get(url));
    }

    @Override
    public Object getOrderList(OrderListQueryReq req) throws Exception {


        String url = getBaseUrl() + "/iserver/account/orders";
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(req.getAccountId())) {
            map.put("accountId", req.getAccountId());
        }
        JSONObject orderList = HttpClient.get(url, map);

        // 有概率第一次查询不到订单列表，再查询一次
        JSONArray orders = orderList.getJSONArray("orders");
        if (orders == null || orders.isEmpty() && !orderList.getBoolean("snapshot")) {
            return handleResponse(HttpClient.get(url, map));
        } else {
            return handleResponse(orderList);
        }
    }

    @Override
    public Object modifyOrder(ModifyOrderReq req) throws Exception {


        OrderInfoQueryReq orderInfoQueryReq = new OrderInfoQueryReq();
        orderInfoQueryReq.setOrderId(req.getOrderId());
        JSONObject orderInfo = (JSONObject) getOrderInfo(orderInfoQueryReq);


        // 查询历史订单数据  从中取得匹配当前订单编号的这条数据
        OrderListQueryReq orderListQueryReq = new OrderListQueryReq();
        orderListQueryReq.setAccountId(req.getAccountId());
        JSONObject orderList = (JSONObject) getOrderList(orderListQueryReq);
        JSONArray orders = orderList.getJSONArray("orders");
        if (orders == null || orders.isEmpty()) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ORDER_ID_NOT_EXISTS);
        }
        JSONObject matchingOrder = null;
        for (int i = 0; i < orders.size(); i++) {
            JSONObject item = orders.getJSONObject(i);
            if (item.getString("orderId").equals(req.getOrderId())) {
                matchingOrder = item;
            }
        }
        if (matchingOrder == null) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ORDER_ID_NOT_EXISTS);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("acctId", req.getAcctId());
        map.put("conid", matchingOrder.getString("conid"));
        map.put("conidex", req.getConidex());
        map.put("secType", req.getSecType());
        map.put("cOID", req.getClientOrderId());
        map.put("parentId", req.getParentId());
        map.put("listingExchange", req.getListingExchange());
        map.put("isSingleGroup", req.getIsSingleGroup());
        map.put("outsideRTH", req.getOutsideRTH());
        map.put("auxPrice", req.getAuxPrice());
        map.put("ticker", req.getTicker());
        map.put("trailingAmt", req.getTrailingAmt());
        map.put("trailingType", req.getTrailingType());
        map.put("referrer", req.getReferrer());
        map.put("cashQty", req.getCashQty());
        map.put("useAdaptive", req.getUseAdaptive());
        map.put("isCcyConv", req.getIsCcyConv());
        map.put("orderType", req.getOrderType() == null ? matchingOrder.getString("orderType").toUpperCase(Locale.ROOT) : req.getOrderType());
        map.put("price", req.getPrice() == null ? matchingOrder.getBigDecimal("price") : req.getPrice());
        map.put("side", req.getSide() == null ? matchingOrder.getString("side") : req.getSide());
        map.put("tif", req.getTif() == null ? orderInfo.getString("tif") : req.getTif());
        map.put("quantity", req.getQuantity() == null ? matchingOrder.getBigDecimal("totalSize") : req.getQuantity());
        map.put("strategy", req.getStrategy());
        map.put("strategyParameters", req.getStrategyParameters());
        // 移除空值项
        map.entrySet().removeIf(entry -> entry.getValue() == null);

        String url = getBaseUrl() + "/iserver/account/" + req.getAccountId() + "/order/" + req.getOrderId();
        return handleResponse(HttpClient.post(url, map));
    }
    public Object cancelOrder(CancelOrderReq req) throws Exception {
        String url = getBaseUrl() + "/iserver/account/"+req.getAccountId()+"/order/" + req.getOrderId();
        return handleResponse(HttpClient.delete(url));
    }
    @Override
    public Object createOrder(CreateOrderReq req) throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put("acctId", req.getAcctId());
        map.put("conid", req.getConid());
        map.put("conidex", req.getConidex());
        map.put("secType", req.getSecType());
        map.put("cOID", req.getClientOrderId());
        map.put("parentId", req.getParentId());
        map.put("listingExchange", req.getListingExchange());
        map.put("isSingleGroup", req.getIsSingleGroup());
        map.put("outsideRTH", req.getOutsideRTH());
        map.put("auxPrice", req.getAuxPrice());
        map.put("ticker", req.getTicker());
        map.put("trailingAmt", req.getTrailingAmt());
        map.put("trailingType", req.getTrailingType());
        map.put("referrer", req.getReferrer());
        map.put("cashQty", req.getCashQty());
        map.put("useAdaptive", req.getUseAdaptive());
        map.put("isCcyConv", req.getIsCcyConv());
        map.put("orderType", req.getOrderType());
        map.put("price", req.getPrice());
        map.put("side", req.getSide());
        map.put("tif", req.getTif());
        map.put("quantity", req.getQuantity());
        map.put("strategy", req.getStrategy());
        map.put("strategyParameters", req.getStrategyParameters());
        map.entrySet().removeIf(entry -> entry.getValue() == null);
        Map<String, Object> orders = new HashMap<>();
        orders.put("orders", List.of(map));
        String url = getBaseUrl() + "/iserver/account/" + req.getAccountId() + "/orders";
        return handleResponse(HttpClient.post(url, orders));
    }
    public Object whatifOrder(WhatifOrderReq req) throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put("acctId", req.getAcctId());
        map.put("conid", req.getConid());
        map.put("conidex", req.getConidex());
        map.put("secType", req.getSecType());
        map.put("cOID", req.getClientOrderId());
        map.put("parentId", req.getParentId());
        map.put("listingExchange", req.getListingExchange());
        map.put("isSingleGroup", req.getIsSingleGroup());
        map.put("outsideRTH", req.getOutsideRTH());
        map.put("auxPrice", req.getAuxPrice());
        map.put("ticker", req.getTicker());
        map.put("trailingAmt", req.getTrailingAmt());
        map.put("trailingType", req.getTrailingType());
        map.put("referrer", req.getReferrer());
        map.put("cashQty", req.getCashQty());
        map.put("useAdaptive", req.getUseAdaptive());
        map.put("isCcyConv", req.getIsCcyConv());
        map.put("orderType", req.getOrderType());
        map.put("price", req.getPrice());
        map.put("side", req.getSide());
        map.put("tif", req.getTif());
        map.put("quantity", req.getQuantity());
        map.put("strategy", req.getStrategy());
        map.put("strategyParameters", req.getStrategyParameters());
        map.entrySet().removeIf(entry -> entry.getValue() == null);
        Map<String, Object> orders = new HashMap<>();
        orders.put("orders", List.of(map));
        String url = getBaseUrl() + "/iserver/account/" + req.getAccountId() + "/orders/whatif";
        return handleResponse(HttpClient.post(url, orders));
    }
    public Object replyOrder(ReplyOrderReq req) throws Exception {

        Map<String, Object> map = new HashMap<>();
        map.put("confirmed", Boolean.TRUE);

        String url = getBaseUrl() + "/iserver/reply/" + req.getId();
        return handleResponse(HttpClient.post(url, map));
    }
    public Object getPortfolioAccounts(GetPortfolioAccountsReq req) throws Exception {
        String url = getBaseUrl() + "/portfolio/accounts";
        return handleResponse(HttpClient.get(url));
    }
    public Object getPositionInfo(GetPositionInfo req) throws Exception {
        String url = getBaseUrl() + "/portfolio/positions/" + req.getConId();
        return handleResponse(HttpClient.get(url));
    }
    public Object getSubAccounts(GetSubAccounts req) throws Exception {
        String url = getBaseUrl() + "/portfolio/subaccounts";
        return handleResponse(HttpClient.get(url));
    }
    @Override
    public Object signaturesAndOwners(SignaturesOwnersReq req) {

        //账户不存在
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }

        String url = getBaseUrl() + "/acesws/" + req.getAccountId() + "/signatures-and-owners";
        JSONObject result = HttpClient.get(url, null);

        return handleResponse(result);
    }

    @Override
    public Object switchAccount(SwitchAccountReq req) {

        //账户不存在
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }

        String url = getBaseUrl() + "/iserver/account";
        Map<String, Object> map = new HashMap<>();
        map.put("acctId", req.getAccountId());
        JSONObject result = HttpClient.post(url, map);

        return handleResponse(result);
    }

    @Override
    public Object accountProfitAndLoss(ProfitAndLossReq req) {

        String url = getBaseUrl() + "/iserver/account/pnl/partitioned";
        JSONObject result = HttpClient.get(url, null);

        return handleResponse(result);
    }

    @Override
    public Object getAccountSummary(AccountSummaryReq req) {

        //账户不存在
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }

        String url = getBaseUrl() + "/iserver/account/" + req.getAccountId() + "/summary";
        JSONObject result = HttpClient.get(url, null);

        return handleResponse(result);
    }

    @Override
    public Object availableFunds(AvailableFundsReq req) {

        //账户不存在
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }

        String url = getBaseUrl() + "/iserver/account/" + req.getAccountId() + "/summary/available_funds";
        JSONObject result = HttpClient.get(url, null);

        return handleResponse(result);
    }

    @Override
    public Object balances(BalanceReq req) {
        //账户不存在
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }

        String url = getBaseUrl() + "/iserver/account/" + req.getAccountId() + "/summary/balances";
        JSONObject result = HttpClient.get(url, null);

        return handleResponse(result);
    }

    @Override
    public Object margins(MarginsReq req) {
        //账户不存在
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }

        String url = getBaseUrl() + "/iserver/account/" + req.getAccountId() + "/summary/margins";
        JSONObject result = HttpClient.get(url, null);

        return handleResponse(result);
    }

    @Override
    public Object marketValue(MarketValueReq req) {
        //账户不存在
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }

        String url = getBaseUrl() + "/iserver/account/" + req.getAccountId() + "/summary/market_value";
        JSONObject result = HttpClient.get(url, null);

        return handleResponse(result);
    }

    //获取账户列表
    @Override
    public Object accounts(AccountsReq accountsReq) {

        String url = getBaseUrl() + "/iserver/accounts";
        JSONObject result = HttpClient.get(url, null);

        return handleResponse(result);
    }

    @Override
    public Object getAllPosition(PositionListQueryReq req) throws Exception {
        // https://api.ibkr.com/v1/api/portfolio/{accountId}/positions/{pageId}
        //账户不存在
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }
        String url = "%s/portfolio/%s/positions/%s";
        String formatUrl = String.format(url, getBaseUrl(), req.getAccountId(), req.getPageId());
        return handleResponse(HttpClient.get(formatUrl, null));
    }

    @Override
    public Object portfolioAccountSummary(AccountSummaryReq req) {
        // https://api.ibkr.com/v1/api/portfolio/{accountId}/summary
        // 账户不存在
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }
        String url = "%s/portfolio/%s/summary";
        String formatUrl = String.format(url, getBaseUrl(), req.getAccountId());
        return handleResponse(HttpClient.get(formatUrl, null));
    }

    @Override
    public Object getInstrumentPosition(InstrumentPositionReq req) throws Exception {
        // https://api.ibkr.com/v1/api/portfolio/{accountid}/position/{conid}
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }
        String url = "%s/portfolio/%s/position/%s";
        String formatUrl = String.format(url, getBaseUrl(), req.getAccountId(), req.getContactId());
        return HttpClient.get(formatUrl, null);
    }

    @Override
    public Object getAccountPerformance(AccountPerformanceReq req) throws Exception {
        // https://api.ibkr.com/v1/api/pa/allperiods
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }
        List<String> accountIdList = Arrays.stream(req.getAccountId().split(",")).distinct().toList();
        JSONObject body = new JSONObject();
        body.put("acctIds", accountIdList);
        String url = getBaseUrl() + "/pa/allperiods";
        return HttpClient.post(url, body);
    }

    @Override
    public Object getPeriodAccountPerformance(PeriodAccountPerformanceReq req) throws Exception {
        // https://api.ibkr.com/pa/performance
        if (StringUtils.isEmpty(req.getAccountId())) {
            throw new OpenApiException(OpenApiErrorCodeEnums.ACCOUNT_NOT_EXISTS);
        }
        List<String> accountIdList = Arrays.stream(req.getAccountId().split(",")).distinct().toList();
        JSONObject body = new JSONObject();
        body.put("acctIds", accountIdList);
        body.put("period", req.getPeriod());
        String url = getBaseUrl() + "/pa/performance";
        return HttpClient.post(url, body);
    }

    @Override
    public Object getHistoryTransaction(HistoryTransactionReq req) throws Exception {
        // https://api.ibkr.com/pa/transactions
        List<String> accountIdList = Arrays.stream(req.getAccountId().split(",")).distinct().toList();
        List<String> conidList = Arrays.stream(req.getContractId().split(",")).distinct().toList();
        JSONObject body = new JSONObject();
        body.put("acctIds", accountIdList);
        body.put("conids", conidList);
        body.put("currency", req.getCurrency());
        body.put("days", req.getDays());
        String url = getBaseUrl() + "/pa/transactions";
        return HttpClient.post(url, body);
    }

    @Override
    public Object getPortfolioAllocation(PortfolioAllocationReq req) {
        // https://api.ibkr.com/v1/api/portfolio/{accountId}/allocation
        String url = "%s/portfolio/%s/allocation";
        String formatUrl = String.format(url, getBaseUrl(), req.getAccountId());
        return HttpClient.get(formatUrl, null);
    }

    @Override
    public Object getAccountLedger(AccountLedgerReq req) {
        // https://api.ibkr.com/v1/api/portfolio/{accountId}/ledger
        String url = "%s/portfolio/{accountId}/ledger";
        String formatUrl = String.format(url, getBaseUrl(), req.getAccountId());
        return HttpClient.get(formatUrl, null);
    }

    @Override
    public Object getAccountAttributes(AccountAttributesReq req) {
        // https://api.ibkr.com/v1/api/portfolio/{accountId}/meta
        String url = "%s/portfolio/{accountId}/meta";
        String formatUrl = String.format(url, getBaseUrl(), req.getAccountId());
        return HttpClient.get(formatUrl, null);
    }
}
