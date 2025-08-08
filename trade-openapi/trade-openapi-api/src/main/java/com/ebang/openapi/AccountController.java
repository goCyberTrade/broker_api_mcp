package com.ebang.openapi;

import com.ebang.openapi.channel.ChannelFactory;
import com.ebang.openapi.context.RequestContext;
import com.ebang.openapi.req.*;
import com.ebang.openapi.resp.ResultResponse;
import com.ebang.openapi.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/account")
@Slf4j
public class AccountController {

    private final ChannelFactory channelFactory;

    @PostMapping("/signatures-and-owners")
    public ResultResponse<Object> signaturesAndOwners(@RequestBody SignaturesOwnersReq request) {
        log.info("signatures-and-owners request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).signaturesAndOwners(request));
    }

    @PostMapping("/switch-account")
    public ResultResponse<Object> switchAccount(@RequestBody SwitchAccountReq request) {
        log.info("switch-account request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).switchAccount(request));
    }

    @PostMapping("/account-profit-and-loss")
    public ResultResponse<Object> accountProfitAndLoss(@RequestBody ProfitAndLossReq request) {
        log.info("account-profit-and-loss request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).accountProfitAndLoss(request));
    }

    @PostMapping("/get-account-summary")
    public ResultResponse<Object> getAccountSummary(@RequestBody AccountSummaryReq request) {
        log.info("get-account-summary request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).getAccountSummary(request));
    }

    @PostMapping("/available-funds")
    public ResultResponse<Object> availableFunds(@RequestBody AvailableFundsReq request) throws Exception {
        log.info("available-funds request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).availableFunds(request));
    }

    @PostMapping("/balances")
    public ResultResponse<Object> balances(@RequestBody BalanceReq request) {
        log.info("balances request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).balances(request));
    }

    @PostMapping("/margins")
    public ResultResponse<Object> margins(@RequestBody MarginsReq request) {
        log.info("margins request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).margins(request));
    }

    @PostMapping("/market-value")
    public ResultResponse<Object> marketValue(@RequestBody MarketValueReq request) {
        log.info("market-value request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).marketValue(request));
    }

    @PostMapping("/accounts")
    public ResultResponse<Object> accounts(@RequestBody AccountsReq request) throws Exception{
        log.info("accounts request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).accounts(request));
    }

    /**
     * 解锁交易
     */
    @PostMapping("/unlock-trading")
    public ResultResponse<Object> unlockTrading(@RequestBody UnlockTradingReq request) throws Exception{
        log.info("unlockTrading request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).unlockTrading(request));
    }
    /**
     * 获取最大可交易数量
     */
    @PostMapping("/get-maximum-tradable-quantity")
    public ResultResponse<Object> getMaximumTradableQuantity(@RequestBody MaximumTradableQuantityReq request) throws Exception{
        log.info("getMaximumTradableQuantity request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).getMaximumTradableQuantity(request));
    }
    /**
     * 查询股票融资融券信息
     */
    @PostMapping("/get-margin-trading-data")
    public ResultResponse<Object> getMarginTradingData(@RequestBody MarginTradingDataReq request) throws Exception{
        log.info("getMarginTradingData request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).getMarginTradingData(request));
    }

    /**
     * 查询交易标的
     */
    @PostMapping("/get-symbol")
    public ResultResponse<Object> getSymbol(@RequestBody GetSymbolReq request) throws Exception{
        log.info("get symbol request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).getSymbol(request));
    }
    /**
     * 查询标的基础信息
     */
    @PostMapping("/get-instrument-info")
    public ResultResponse<Object> getInstrumentInfo(@RequestBody InstrumentInfoReq request) throws Exception{
        log.info("getInstrumentInfo request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).getInstrumentInfo(request));
    }
    /**
     * 查询标的是否支持盘前盘后交易
     */
    @PostMapping("/trade-query-before-and-after-support")
    public ResultResponse<Object> tradeQueryBeforeAndAfterSupport(@RequestBody @Validated TradeQueryBeforeAndAfterSupportReq request) throws Exception{
        log.info("tradeQueryBeforeAndAfterSupport request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).tradeQueryBeforeAndAfterSupport(request));
    }
    /**
     * 获取资金流水
     */
    @PostMapping("/get-cash-flow-summary")
    public ResultResponse<Object> getCashFlowSummary(@RequestBody CashFlowSummaryReq request) throws Exception{
        log.info("getCashFlowSummary request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).getCashFlowSummary(request));
    }
    /**
     * 查询汇率
     */
    @PostMapping("/get-rate")
    public ResultResponse<Object> getRate(@RequestBody RateReq request) throws Exception{
        log.info("getRate request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).getRate(request));
    }

    /**
     * 获取保证金比例
     */
    @PostMapping("/margin-ratio")
    public ResultResponse<Object> marginRatio(@RequestBody @Validated MarginRatioReq request) throws Exception{
        log.info("marginRatio request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).marginRatio(request));
    }

    /**
     * 修改交易密码
     */
    @PostMapping("/update-trade-password")
    public ResultResponse<Object> updateTradePassword(@RequestBody UpdateOrResetTradePasswordReq request) throws Exception{
        log.info("updateTradePassword request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).updateTradePassword(request));
    }

    /**
     * 重置交易密码
     */
    @PostMapping("/reset-trade-password")
    public ResultResponse<Object> resetTradePassword(@RequestBody UpdateOrResetTradePasswordReq request) throws Exception{
        log.info("resetTradePassword request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).resetTradePassword(request));
    }

    /**
     * 获取股票抵押比率列表
     */
    @PostMapping("/mortgage-list")
    public ResultResponse<Object> mortgageList(@RequestBody MortgageListReq request) throws Exception{
        log.info("mortgageList request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).mortgageList(request));
    }

    /**
     * 重置密码
     */
    @PostMapping("/password-reset")
    public ResultResponse<Object> passwordResetReq(@RequestBody @Validated PasswordResetReq request) throws Exception{
        log.info("passwordResetReq request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).passwordReset(request));
    }

    /**
     * 重置密码请求
     */
    @PostMapping("/password-reset-request")
    public ResultResponse<Object> passwordResetRequestReq(@RequestBody @Validated PasswordResetRequestReq request) throws Exception{
        log.info("passwordResetRequestReq request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).passwordResetRequest(request));
    }

    /**
     * 获取合约列表
     */
    @PostMapping("/get-contract-list")
    public ResultResponse<Object> getContractList(@RequestBody ContractListReq request) throws Exception{
        return ResultResponse.success(channelFactory.getChannel(request).getContractList(request));
    }




}
