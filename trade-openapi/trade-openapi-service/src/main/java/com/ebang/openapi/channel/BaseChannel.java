package com.ebang.openapi.channel;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ebang.openapi.config.FutuBase;
import com.ebang.openapi.constant.Constants;
import com.ebang.openapi.context.RequestContext;
import com.ebang.openapi.enums.ChannelEnums;
import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import com.ebang.openapi.req.*;
import com.ebang.openapi.service.AuthenticationService;
import com.ebang.openapi.util.SpringContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.function.Function;

public abstract class BaseChannel {

    @Autowired
    private FutuBase futuBase;

    protected final Function<String, OpenApiException> exceptionFunction = channel -> {
        String desc = ChannelEnums.getChannelEnums(channel).map(ChannelEnums::getDesc).orElse(channel);
        String message = desc + "不支持当前操作！";
        return new OpenApiException(message);
    };

    protected String getApiKey(){
        return RequestContext.getApiKey();
    }

    /**
     * 预处理方法
     */
    public void beforeHandle() {
        String channel = RequestContext.getChannel();
        if (ChannelEnums.FUTU.getChannel().equals(channel)) {
            futuBase.initConnect(getRouteConfig());
        }
    }

    /**
     * 获取各渠道用户信息
     *
     * @return
     */
    public Map<String, String> getRouteConfig() {

        // 1.从请求上下文中获取ApiKey、当前需要调用的渠道
        String apiKey = getApiKey();
        String channel = RequestContext.getChannel();
        // 2.根据ApiKey 获取渠道配置
        JSONObject jsonConfig = AuthenticationService.APIKEY_DATA.get(apiKey);
        // 3.返回渠道配置
        if (jsonConfig.containsKey(channel)) {
            return jsonConfig.getJSONObject(channel).toJavaObject(new TypeReference<Map<String, String>>() {
            });
        }
        throw new OpenApiException(OpenApiErrorCodeEnums.INVALID_CHANNEL_CONFIG);
    }


    public Object getContractList(ContractListReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getPortfolioAllocation(PortfolioAllocationReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getAccountLedger(AccountLedgerReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getAccountAttributes(AccountAttributesReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getAllPosition(PositionListQueryReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object portfolioAccountSummary(AccountSummaryReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getInstrumentPosition(InstrumentPositionReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getAccountPerformance(AccountPerformanceReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getPeriodAccountPerformance(PeriodAccountPerformanceReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getHistoryTransaction(HistoryTransactionReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getTodayTransaction(TodayTransactionReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getOrderInfo(OrderInfoQueryReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object getOrderList(OrderListQueryReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object modifyOrder(ModifyOrderReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    public Object cancelOrder(CancelOrderReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    public Object createOrder(CreateOrderReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    public Object whatifOrder(WhatifOrderReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    public Object replyOrder(ReplyOrderReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    public Object orderFees(OrderFeesReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    public Object getPortfolioAccounts(GetPortfolioAccountsReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    public Object getPositionInfo(GetPositionInfo req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    public Object getSubAccounts(GetSubAccounts req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    public Object getAccountSummary(AccountSummaryReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object accounts(AccountsReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object availableFunds(AvailableFundsReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object balances(BalanceReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 获取资金流水
     */
    public Object getCashFlowSummary(CashFlowSummaryReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 获取最大可交易数量
     */
    public Object getMaximumTradableQuantity(MaximumTradableQuantityReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 查询交易标的
     */
    public Object getSymbol(GetSymbolReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 查询标的基础信息
     */
    public Object getInstrumentInfo(InstrumentInfoReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 解锁交易
     */
    public Object unlockTrading(UnlockTradingReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 查询股票融资融券信息
     */
    public Object getMarginTradingData(MarginTradingDataReq req) throws Exception {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 查询标的是否支持盘前盘后交易
     */
    public Object tradeQueryBeforeAndAfterSupport(TradeQueryBeforeAndAfterSupportReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 查询汇率
     */
    public Object getRate(RateReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object signaturesAndOwners(SignaturesOwnersReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object switchAccount(SwitchAccountReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object accountProfitAndLoss(ProfitAndLossReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object margins(MarginsReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    public Object marketValue(MarketValueReq req) {
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 获取保证金比例
     */
    public Object marginRatio(MarginRatioReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 修改交易密码
     */
    public Object updateTradePassword(UpdateOrResetTradePasswordReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 重置交易密码
     */
    public Object resetTradePassword(UpdateOrResetTradePasswordReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
    /**
     * 获取股票抵押比率列表
     */
    public Object mortgageList(MortgageListReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 获取新股详细信息
     */
    public Object ipoInfo(IpoInfoReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 新股认购
     */
    public Object applyIpo(ApplyIpoReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     *  ipo改单/撤单
     */
    public Object modifyIpo(ModifyIpoReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     *  获取客户ipo申购列表-分页查询
     */
    public Object ipoRecordList(IpoRecordListReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     *  获取客户ipo申购明细
     */
    public Object ipoRecord(IpoRecordReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 重置密码
     */
    public Object passwordReset(PasswordResetReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }

    /**
     * 重置密码请求
     */
    public Object passwordResetRequest(PasswordResetRequestReq req) throws Exception{
        throw exceptionFunction.apply(RequestContext.getChannel());
    }
}
