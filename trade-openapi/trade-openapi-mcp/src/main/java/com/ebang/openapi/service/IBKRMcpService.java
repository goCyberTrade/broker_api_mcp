package com.ebang.openapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.channel.IBKRChannel;
import com.ebang.openapi.channel.LongportChannel;
import com.ebang.openapi.req.*;
import com.ebang.openapi.resp.ResultResponse;
import com.ebang.openapi.util.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author: zyz
 * @Date: 2025/7/10 16:14
 * @Description:IBKR 相关MCP接口
 **/
@Service
@Slf4j
public class IBKRMcpService {


    @Autowired
    private IBKRChannel ibkrChannel;

    public record IBKROrderInfoQueryReq(
            @ToolParam(description = "订单编号") String orderId){
    }

    @Tool(description = "获取订单详情。" +
            "返回信息:订单信息，包含订单orderId、conid、ticker、orderDesc、sizeAndFills、listingExchange、remainingQuantity、filledQuantity、totalSize、companyName、status、orderType、price、side、lastExecutionTime_r等信息")
    public ResultResponse<JSONObject> ibkrGetOrderInfo(IBKROrderInfoQueryReq req, ToolContext toolContext) throws Exception {
        OrderInfoQueryReq orderInfoQueryReq = RequestUtils.requestHandle(req, OrderInfoQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getOrderInfo(orderInfoQueryReq))));
    }


    public record IBKROrderListQueryReq(
        @ToolParam(description = "账户ID", required = false) String accountId
    ){}

    @Tool(description = "获取订单列表。" +
            "返回信息:订单列表信息，包含各订单orderId、conid、ticker、orderDesc、sizeAndFills、listingExchange、remainingQuantity、filledQuantity、totalSize、companyName、status、orderType、price、side、lastExecutionTime_r等信息")
    public ResultResponse<JSONObject> ibkrGetOrderList(IBKROrderListQueryReq req, ToolContext toolContext) throws Exception {
        OrderListQueryReq orderListQueryReq = RequestUtils.requestHandle(req, OrderListQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getOrderList(orderListQueryReq))));
    }

    public record IBKRModifyOrderReq(
        @ToolParam(description = "需要修改的订单编号") String orderId,
        @ToolParam(description = "用户账户编号") String accountId,
        @ToolParam(description = "订单类型，可以输入的枚举有[LIMIT,MARKET,STOP,STOP_LIMIT,TRAILING_STOP,TRAILING_STOP_LIMIT,MARKETONCLOSE,LIMITONCLOSE]", required = false) String orderType,
        @ToolParam(description = "买卖方向，可以输入的枚举有[BUY,SELL,CLOSE]", required = false) String side,
        @ToolParam(description = "订单时效，可以输入的枚举有[DAY,IOC,GTC,OPG,PAX]", required = false) String tif,
        @ToolParam(description = "下单数量", required = false) java.math.BigDecimal quantity,
        @ToolParam(description = "下单价格", required = false) java.math.BigDecimal price,
        @ToolParam(description = "订单接收的账户，默认为自己的账户", required = false) String acctId,
        @ToolParam(description = "指定合约对应的路由目的地", required = false) String conidex,
        @ToolParam(description = "订单接收交易所", required = false) String secType,
        @ToolParam(description = "客户端订单ID", required = false) String clientOrderId,
        @ToolParam(description = "若该订单是组合订单（bracket order）中的子订单，则 parentId 字段必须设置为与父订单的 client_order_id 一致", required = false) String parentId,
        @ToolParam(description = "上市交易所", required = false) String listingExchange,
        @ToolParam(description = "指示包含数组中的所有订单应被视为OCA组", required = false) Boolean isSingleGroup,
        @ToolParam(description = "是否允许交易时段外成交订单，默认false", required = false) Boolean outsideRTH,
        @ToolParam(description = "某些订单类型中使用的附加价格值，如止损单", required = false) BigDecimal auxPrice,
        @ToolParam(description = "合约的标的代码", required = false) String ticker,
        @ToolParam(description = "追踪订单使用的偏移量", required = false) BigDecimal trailingAmt,
        @ToolParam(description = "指定追踪订单使用的追踪类型，可以输入的枚举有[amt,%]", required = false) String trailingType,
        @ToolParam(description = "渠道内部标识", required = false) String referrer,
        @ToolParam(description = "现金数量订单使用的货币数量", required = false) BigDecimal cashQty,
        @ToolParam(description = "指示路由应用价格管理算法", required = false) Boolean useAdaptive,
        @ToolParam(description = "指示外汇订单用于货币转换，并且在适用的情况下不应在账户中产生虚拟外汇头寸", required = false) Boolean isCurrencyConv,
        @ToolParam(description = "执行算法的名称", required = false) String strategy,
        @ToolParam(description = "管理所选算法的参数", required = false) Map<String, Object> strategyParameters
    ){}

    @Tool(description = "根据订单创建的订单编号修改订单，字段下单接口一致，只需要传入需要修改的字段。例如:想修改订单的价格除了必传参数外只需要传入price。" +
            "返回信息:订单修改结果，当结果为以下格式时代表创建成功。\n" +
            "            {\n" +
            "              \"order_id\": \"987654\",\n" +
            "              \"order_status\": \"Submitted\",\n" +
            "              \"encrypt_message\": \"1\"\n" +
            "            }\n" +
            "            当创建结果为以下格式时表示订单被抑制，需要提示用户确认，用户确认后使用返回里面的id(订单抑制唯一标识)调用订单确认接口(order_reply)\n" +
            "            {\n" +
            "            \"id\": \"07a13a5a-4a48-44a5-bb25-5ab37b79186c\",\n" +
            "            \"message\": [\n" +
            "              \"The following order \\\"BUY 100 AAPL NASDAQ.NMS @ 165.0\\\" price exceeds \\nthe Percentage constraint of 3%.\\nAre you sure you want to submit this order?\"\n" +
            "            ],\n" +
            "            \"isSuppressed\": false,\n" +
            "            \"messageIds\": [\n" +
            "              \"o163\"\n" +
            "            ]}\n" +
            "          }")
    public ResultResponse<JSONObject> ibkrModifyOrder(IBKRModifyOrderReq req, ToolContext toolContext) throws Exception {
        ModifyOrderReq modifyOrderReq = RequestUtils.requestHandle(req, ModifyOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.modifyOrder(modifyOrderReq))));
    }

    public record IBKRCancelOrderReq(
        @ToolParam(description = "订单编号") String orderId,
        @ToolParam(description = "账户ID") String accountId
    ){}

    @Tool(description = "对取消没有成交的订单，提交取消请求。返回信息:提交取消请求的结果")
    public ResultResponse<JSONObject> ibkrCancelOrder(IBKRCancelOrderReq req, ToolContext toolContext) throws Exception {
        CancelOrderReq cancelOrderReq = RequestUtils.requestHandle(req, CancelOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.cancelOrder(cancelOrderReq))));
    }

    public record IBKRCreateOrderReq(
            @ToolParam(description = "用户账户编号") String accountId,
            @ToolParam(description = "需要使用的合约编号，需要通过用户输入的标的代码去请求ibkrGetContractList接口获取其中的conid") String conid,
            @ToolParam(description = "订单类型，可以输入的枚举有[LIMIT,MARKET,STOP,STOP_LIMIT,TRAILING_STOP,TRAILING_STOP_LIMIT,MARKETONCLOSE,LIMITONCLOSE],用户没有指定时默认使用LIMIT") String orderType,
            @ToolParam(description = "买卖方向，可以输入的枚举有[BUY,SELL,CLOSE]") String side,
            @ToolParam(description = "订单时效，可以输入的枚举有[DAY,IOC,GTC,OPG,PAX]，用户没有指定时默认使用DAY") String tif,
            @ToolParam(description = "下单数量") java.math.BigDecimal quantity,
            @ToolParam(description = "当订单类型为MARKET或者MARKETONCLOSE时不用输入。订单类型为其他时必输", required = false) java.math.BigDecimal price,
            @ToolParam(description = "订单接收的账户，默认为自己的账户", required = false) String acctId,
            @ToolParam(description = "指定合约对应的路由目的地", required = false) String conidex,
            @ToolParam(description = "订单接收交易所", required = false) String secType,
            @ToolParam(description = "客户端订单ID", required = false) String clientOrderId,
            @ToolParam(description = "若该订单是组合订单（bracket order）中的子订单，则 parentId 字段必须设置为与父订单的 client_order_id 一致", required = false) String parentId,
            @ToolParam(description = "上市交易所", required = false) String listingExchange,
            @ToolParam(description = "指示包含数组中的所有订单应被视为OCA组", required = false) Boolean isSingleGroup,
            @ToolParam(description = "是否允许交易时段外成交订单，默认false", required = false) Boolean outsideRTH,
            @ToolParam(description = "某些订单类型中使用的附加价格值，如止损单", required = false) BigDecimal auxPrice,
            @ToolParam(description = "合约的标的代码", required = false) String ticker,
            @ToolParam(description = "追踪订单使用的偏移量", required = false) BigDecimal trailingAmt,
            @ToolParam(description = "指定追踪订单使用的追踪类型，可以输入的枚举有[amt,%]", required = false) String trailingType,
            @ToolParam(description = "渠道内部标识", required = false) String referrer,
            @ToolParam(description = "现金数量订单使用的货币数量", required = false) BigDecimal cashQty,
            @ToolParam(description = "指示路由应用价格管理算法", required = false) Boolean useAdaptive,
            @ToolParam(description = "指示外汇订单用于货币转换，并且在适用的情况下不应在账户中产生虚拟外汇头寸", required = false) Boolean isCurrencyConv,
            @ToolParam(description = "执行算法的名称", required = false) String strategy,
            @ToolParam(description = "管理所选算法的参数", required = false) Map<String, Object> strategyParameters
    ){}

    @Tool(description = "创建订单。例如:想修改订单的价格除了必传参数外只需要传入price。" +
            "返回信息:订单创建结果，当创建结果为以下格式时代表创建成功。\n" +
            "            {\n" +
            "              \"order_id\": \"987654\",\n" +
            "              \"order_status\": \"Submitted\",\n" +
            "              \"encrypt_message\": \"1\"\n" +
            "            }\n" +
            "            当创建结果为以下格式时表示订单被抑制，需要提示用户确认，用户确认后使用返回里面的id(订单抑制唯一标识)调用订单确认接口(order_reply)\n" +
            "            {\n" +
            "            \"id\": \"07a13a5a-4a48-44a5-bb25-5ab37b79186c\",\n" +
            "            \"message\": [\n" +
            "              \"The following order \\\"BUY 100 AAPL NASDAQ.NMS @ 165.0\\\" price exceeds \\nthe Percentage constraint of 3%.\\nAre you sure you want to submit this order?\"\n" +
            "            ],\n" +
            "            \"isSuppressed\": false,\n" +
            "            \"messageIds\": [\n" +
            "              \"o163\"\n" +
            "            ]}")
    public ResultResponse<JSONObject> ibkrCreateOrder(IBKRCreateOrderReq req, ToolContext toolContext) throws Exception {
        CreateOrderReq createOrderReq = RequestUtils.requestHandle(req, CreateOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.createOrder(createOrderReq))));
    }

    public record IBKRWhatifOrderReq(
            @ToolParam(description = "用户账户编号") String accountId,
            @ToolParam(description = "需要使用的合约编号，需要通过用户输入的标的代码去请求ibkrGetContractList接口获取其中的conid") String conid,
            @ToolParam(description = "订单类型，可以输入的枚举有[LIMIT,MARKET,STOP,STOP_LIMIT,TRAILING_STOP,TRAILING_STOP_LIMIT,MARKETONCLOSE,LIMITONCLOSE],用户没有指定时默认使用LIMIT") String orderType,
            @ToolParam(description = "买卖方向，可以输入的枚举有[BUY,SELL,CLOSE]") String side,
            @ToolParam(description = "订单时效，可以输入的枚举有[DAY,IOC,GTC,OPG,PAX]，用户没有指定时默认使用DAY") String tif,
            @ToolParam(description = "下单数量") java.math.BigDecimal quantity,
            @ToolParam(description = "当订单类型为MARKET或者MARKETONCLOSE时不用输入。订单类型为其他时必输", required = false) java.math.BigDecimal price,
            @ToolParam(description = "订单接收的账户，默认为自己的账户", required = false) String acctId,
            @ToolParam(description = "指定合约对应的路由目的地", required = false) String conidex,
            @ToolParam(description = "订单接收交易所", required = false) String secType,
            @ToolParam(description = "客户端订单ID", required = false) String clientOrderId,
            @ToolParam(description = "若该订单是组合订单（bracket order）中的子订单，则 parentId 字段必须设置为与父订单的 client_order_id 一致", required = false) String parentId,
            @ToolParam(description = "上市交易所", required = false) String listingExchange,
            @ToolParam(description = "指示包含数组中的所有订单应被视为OCA组", required = false) Boolean isSingleGroup,
            @ToolParam(description = "是否允许交易时段外成交订单，默认false", required = false) Boolean outsideRTH,
            @ToolParam(description = "某些订单类型中使用的附加价格值，如止损单", required = false) BigDecimal auxPrice,
            @ToolParam(description = "合约的标的代码", required = false) String ticker,
            @ToolParam(description = "追踪订单使用的偏移量", required = false) BigDecimal trailingAmt,
            @ToolParam(description = "指定追踪订单使用的追踪类型，可以输入的枚举有[amt,%]", required = false) String trailingType,
            @ToolParam(description = "渠道内部标识", required = false) String referrer,
            @ToolParam(description = "现金数量订单使用的货币数量", required = false) BigDecimal cashQty,
            @ToolParam(description = "指示路由应用价格管理算法", required = false) Boolean useAdaptive,
            @ToolParam(description = "指示外汇订单用于货币转换，并且在适用的情况下不应在账户中产生虚拟外汇头寸", required = false) Boolean isCurrencyConv,
            @ToolParam(description = "执行算法的名称", required = false) String strategy,
            @ToolParam(description = "管理所选算法的参数", required = false) Map<String, Object> strategyParameters
    ){}
    @Tool(description = "订单试算接口，在提交订单前调用可以预估订单需要的佣金和手续费信息，以及进行下单资金的预校验处理" +
            "返回信息:订单试算结果，预估的佣金、手续费、持仓变动情况以及资金预校验情况")
    public ResultResponse<JSONObject> ibkrOrderWhatif(IBKRWhatifOrderReq param, ToolContext toolContext) throws Exception {
        WhatifOrderReq request = RequestUtils.requestHandle(param, WhatifOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.whatifOrder(request))));
    }

    public record IBKRReplyOrderReq(
            @ToolParam(description = "必输项，订单抑制唯一标识") String id
    ){}
    @Tool(description = "当下单时订单被抑制时，确认订单，让订单进行后续流程。" +
            "返回信息:订单确认后提交结果")
    public ResultResponse<JSONObject> ibkrOrderReply(IBKRReplyOrderReq param, ToolContext toolContext) throws Exception {
        ReplyOrderReq request = RequestUtils.requestHandle(param, ReplyOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.replyOrder(request))));
    }

    public record IBKRGetPortfolioAccountsReq(){}

    @Tool(description = "获取投资组合账户列表" +
            "返回信息:该合约编号对应的持仓详情，包含symbol、position、mktPrice、mktValue、avgCost、currency、avgPrice、assetClass等信息")
    public ResultResponse<JSONObject> ibkrGetPortfolioAccounts(IBKRGetPortfolioAccountsReq req, ToolContext toolContext) throws Exception {
        GetPortfolioAccountsReq getPortfolioAccountsReq = RequestUtils.requestHandle(req, GetPortfolioAccountsReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getPortfolioAccounts(getPortfolioAccountsReq))));
    }

    public record IBKRGetPositionInfo(
            @ToolParam(description = "合约编号") Integer conId
    ){}

    @Tool(description = "获取指定的持仓详情" +
            "返回信息:该合约编号对应的持仓详情，包含symbol、position、mktPrice、mktValue、avgCost、currency、avgPrice、assetClass等信息")
    public ResultResponse<JSONObject> ibkrGetPositionInfo(IBKRGetPositionInfo param, ToolContext toolContext) throws Exception {
        GetPositionInfo request = RequestUtils.requestHandle(param, GetPositionInfo.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getPositionInfo(request))));
    }

    public record IBKRGetSubAccounts(
    ){}

    @Tool(description = "获取所有的子账户列表" +
            "返回信息:所有的子账户信息，包含accountId、currency、type、tradingType、businessType、parent(主账户信息)等")
    public ResultResponse<JSONObject> ibkrGetSubAccounts(IBKRGetSubAccounts param, ToolContext toolContext) throws Exception {
        GetSubAccounts request = RequestUtils.requestHandle(param, GetSubAccounts.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getSubAccounts(request))));
    }

    public record IBKRPositionListQueryReq(
        @ToolParam(description = "老虎证券：账号 id") String accountId,
        @ToolParam(description = "IB：分页返回Position信息。从 0 开始索引。每页最多返回 100 个Position。默认值为 0") Integer pageId
    ){}

    @Tool(description = "获取全部持仓")
    public ResultResponse<JSONObject> ibkrGetAllPosition(IBKRPositionListQueryReq req, ToolContext toolContext) throws Exception {
        PositionListQueryReq positionListQueryReq = RequestUtils.requestHandle(req, PositionListQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getAllPosition(positionListQueryReq))));
    }

    public record IBKRSignaturesOwnersReq(
            @ToolParam(description = "账户id") String accountId
    ) {
    }

    @Tool(description = "获取当前账户主体。" +
            "返回信息:所有账户主体信息，包含accountId、users、applicant等")
    public ResultResponse<JSONObject> ibkrSignaturesAndOwners(IBKRSignaturesOwnersReq req, ToolContext toolContext) {
        SignaturesOwnersReq signaturesOwnersReq = RequestUtils.requestHandle(req, SignaturesOwnersReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.signaturesAndOwners(signaturesOwnersReq))));
    }

    public record IBKRSwitchAccountReq(
            @ToolParam(description = "账户id") String accountId
    ) {
    }

    @Tool(description = "切换账户")
    public ResultResponse<JSONObject> ibkrSwitchAccount(IBKRSwitchAccountReq req, ToolContext toolContext) {
        SwitchAccountReq switchAccountReq = RequestUtils.requestHandle(req, SwitchAccountReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.switchAccount(switchAccountReq))));
    }

    public record IBKRAccountSummaryReq(
        @ToolParam(description = "账户") String accountId
    ){}

    @Tool(description = "获取账户概览。" +
            "返回信息:所有账户主体信息，包含accountType、status、balance、SMA、buyingPower、availableFunds、excessLiquidity、netLiquidationValue、equityWithLoanValue等")
    public ResultResponse<JSONObject> ibkrGetAccountSummary(IBKRAccountSummaryReq req, ToolContext toolContext) {
        AccountSummaryReq accountSummaryReq = RequestUtils.requestHandle(req, AccountSummaryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getAccountSummary(accountSummaryReq))));
    }

    @Tool(description = "获取投资组合账户汇总信息")
    public ResultResponse<JSONObject> ibkrPortfolioAccountSummary(IBKRAccountSummaryReq req, ToolContext toolContext) {
        AccountSummaryReq accountSummaryReq = RequestUtils.requestHandle(req, AccountSummaryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.portfolioAccountSummary(accountSummaryReq))));
    }


    public record IBKRAccountPerformanceReq(
        @ToolParam(description = "账号 id，多个使用逗号分割") String accountId,
        @ToolParam(description = "老虎证券：起始日期， 格式 yyyy-MM-dd, 如 '2022-01-01'。如不传则使用end_date往前30天的日期") String startDate,
        @ToolParam(description = "老虎证券：截止日期， 格式 yyyy-MM-dd, 如 '2022-02-01'。如不传则使用当前日期") String endDate,
        @ToolParam(description = "老虎证券：账户划分类型, 可选值有: SegmentType.SEC 代表证券; SegmentType.FUT 代表期货， 可以从 tigeropen.common.consts.SegmentType 下导入") String segType,
        @ToolParam(description = "老虎证券：币种，包括 ALL/USD/HKD/CNH 等, 可以从 tigeropen.common.consts.Currency 下导入") String currency,
        @ToolParam(description = "盈立证券：交易类别(0-香港,5-美股, 67-A股，100-查询所有交易类别)") String exchangeType
    ){}

    @Tool(description = "获取账户业绩")
    public ResultResponse<JSONObject> ibkrGetAccountPerformance(IBKRAccountPerformanceReq req, ToolContext toolContext) throws Exception {
        AccountPerformanceReq accountPerformanceReq = RequestUtils.requestHandle(req, AccountPerformanceReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getAccountPerformance(accountPerformanceReq))));
    }

    public record IBKRPeriodAccountPerformanceReq(
        @ToolParam(description = "账号 id，多个使用逗号分割") String accountId,
        @ToolParam(description = "IB: 周期：Enum: '1D' '7D' 'MTD' '1M' '3M' '6M' '12M' 'YTD'") String period
    ){}

    @Tool(description = "获取账户分周期业绩")
    public ResultResponse<JSONObject> ibkrGetPeriodAccountPerformance(IBKRPeriodAccountPerformanceReq req, ToolContext toolContext) throws Exception {
        PeriodAccountPerformanceReq periodAccountPerformanceReq = RequestUtils.requestHandle(req, PeriodAccountPerformanceReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getPeriodAccountPerformance(periodAccountPerformanceReq))));
    }

    public record IBKRHistoryTransactionReq(
        @ToolParam(description = "IB: 账号 id，多个使用逗号分割") String accountId,
        @ToolParam(description = "IB: 合约 id，多个使用逗号分割") String contractId,
        @ToolParam(description = "币种") String currency,
        @ToolParam(description = "指定历史交易数据的天数") Integer days
    ){}

    @Tool(description = "获取历史成交")
    public ResultResponse<JSONObject> ibkrGetHistoryTransaction(IBKRHistoryTransactionReq req, ToolContext toolContext) throws Exception {
        HistoryTransactionReq historyTransactionReq = RequestUtils.requestHandle(req, HistoryTransactionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getHistoryTransaction(historyTransactionReq))));
    }

    @Tool(description = "获取账户列表。" +
            "返回信息:返回券商下所有账户信息")
    public ResultResponse<JSONObject> ibkrAccounts(ToolContext toolContext) {
        RequestUtils.requestHandle(toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.accounts(new AccountsReq()))));
    }

    public record IBKRAvailableFundsReq(
        @ToolParam(description = "账户") String accountId
    ){}

    @Tool(description = "获取可用资金")
    public ResultResponse<JSONObject> ibkrAvailableFunds(IBKRAvailableFundsReq req, ToolContext toolContext) {
        AvailableFundsReq availableFundsReq = RequestUtils.requestHandle(req, AvailableFundsReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.availableFunds(availableFundsReq))));
    }

    public record IBKRBalanceReq(
        @ToolParam(description = "账户") String accountId
    ){}

    @Tool(description = "获取账户余额")
    public ResultResponse<JSONObject> ibkrBalances(IBKRBalanceReq req, ToolContext toolContext) {
        BalanceReq balanceReq = RequestUtils.requestHandle(req, BalanceReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.balances(balanceReq))));
    }

    public record IBKRMarginsReq(
        @ToolParam(description = "账户") String accountId
    ){}

    @Tool(description = "获取账户保证金")
    public ResultResponse<JSONObject> ibkrMargins(IBKRMarginsReq req, ToolContext toolContext) {
        MarginsReq marginsReq = RequestUtils.requestHandle(req, MarginsReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.margins(marginsReq))));
    }

    public record IBKRMarketValueReq(
        @ToolParam(description = "账户") String accountId
    ){}

    @Tool(description = "获取账户市值。Retrieves the complete valuation snapshot of all account holdings,\n" +
            "categorized by:\n" +
            "- Asset type (stocks, bonds, derivatives, etc.)\n" +
            "- Currency denomination\n" +
            "- Realized/unrealized P&L\n" +
            "\n" +
            "Returns:\n" +
            "    Dictionary with currency codes as top-level keys. Each currency contains:\n" +
            "    {\n" +
            "        \"<CURRENCY_CODE>\": {  # e.g., \"EUR\", \"USD\"\n" +
            "            \"total_cash\": str,           # Total cash balance\n" +
            "            \"settled_cash\": str,         # Cleared/available cash\n" +
            "            \"MTD Interest\": str,         # Month-to-date interest\n" +
            "            \"stock\": str,                # Equity holdings value\n" +
            "            \"options\": str,              # Options contracts value\n" +
            "            \"futures\": str,              # Futures contracts value\n" +
            "            \"future_options\": str,       # Options on futures value\n" +
            "            \"funds\": str,                # ETF/mutual funds value\n" +
            "            \"dividends_receivable\": str, # Pending dividend payments\n" +
            "            \"mutual_funds\": str,         # Mutual fund holdings\n" +
            "            \"money_market\": str,         # Money market instruments\n" +
            "            \"bonds\": str,                # Bond holdings value\n" +
            "            \"Govt Bonds\": str,           # Government bonds\n" +
            "            \"t_bills\": str,              # Treasury bills\n" +
            "            \"warrants\": str,             # Warrant instruments\n" +
            "            \"issuer_option\": str,        # Issuer options\n" +
            "            \"commodity\": str,            # Physical commodities\n" +
            "            \"Notional CFD\": str,         # CFD notional value\n" +
            "            \"cfd\": str,                  # Contract-for-difference\n" +
            "            \"Cryptocurrency\": str,       # Digital assets\n" +
            "            \"net_liquidation\": str,      # Total account value\n" +
            "            \"unrealized_pnl\": str,       # Open position P&L\n" +
            "            \"realized_pnl\": str,         # Closed position P&L\n" +
            "            \"Exchange Rate\": str         # Conversion rate to USD (e.g., 1.092525 EUR/USD)\n" +
            "        },\n" +
            "        \"Total (in USD)\": {             # Aggregate USD-converted values\n" +
            "            # (Same field structure as above)\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "Exchange Rate Clarification:\n" +
            "    All \"Exchange Rate\" values represent the conversion rate FROM that currency TO USD.\n" +
            "    Example: \"EUR\": {\"Exchange Rate\": \"1.092525\"} means 1 EUR = 1.092525 USD\n" +
            "\n" +
            "Example Response:\n" +
            "    {\n" +
            "        \"EUR\": {\n" +
            "            \"total_cash\": \"194\",\n" +
            "            ...\n" +
            "            \"Exchange Rate\": \"1.092525\"  # EUR→USD rate\n" +
            "        },\n" +
            "        \"Total (in USD)\": {\n" +
            "            \"total_cash\": \"-401,646\",\n" +
            "            ...\n" +
            "            \"Exchange Rate\": \"1.00\"      # Base USD rate\n" +
            "        }\n" +
            "    }")
    public ResultResponse<JSONObject> ibkrMarketValue(IBKRMarketValueReq req, ToolContext toolContext) {
        MarketValueReq marketValueReq = RequestUtils.requestHandle(req, MarketValueReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.marketValue(marketValueReq))));
    }

    public record IBKRAccountLedgerReq(
        @ToolParam(description = "账户") String accountId
    ){}

    @Tool(description = "获取账户流水")
    public ResultResponse<JSONObject> ibkrGetAccountLedger(IBKRAccountLedgerReq req, ToolContext toolContext) {
        AccountLedgerReq accountLedgerReq = RequestUtils.requestHandle(req, AccountLedgerReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getAccountLedger(accountLedgerReq))));
    }

    public record IBKRAccountAttributesReq(
        @ToolParam(description = "账户") String accountId
    ){}

    @Tool(description = "获取账户属性")
    public ResultResponse<JSONObject> ibkrGetAccountAttributes(IBKRAccountAttributesReq req, ToolContext toolContext) {
        AccountAttributesReq accountAttributesReq = RequestUtils.requestHandle(req, AccountAttributesReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getAccountAttributes(accountAttributesReq))));
    }


    @Tool(description = "获取账号资产类别、行业组和行业划分的分配晴朗")
    public ResultResponse<JSONObject> ibkrGetPortfolioAllocation(IBKRAccountAttributesReq req, ToolContext toolContext) {
        PortfolioAllocationReq accountAttributesReq = RequestUtils.requestHandle(req, PortfolioAllocationReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel. getPortfolioAllocation(accountAttributesReq))));
    }

    public record IBKRInstrumentPositionReq(
        @ToolParam(description = "账户id") String accountId,
        @ToolParam(description = "合约 id")  String contactId
    ){}

    @Tool(description = "获取单个账号给定金融标的的持仓信息")
    public ResultResponse<JSONObject> ibkrGetInstrumentPosition(IBKRInstrumentPositionReq req, ToolContext toolContext) throws Exception {
        InstrumentPositionReq accountAttributesReq = RequestUtils.requestHandle(req, InstrumentPositionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(ibkrChannel.getInstrumentPosition(accountAttributesReq))));
    }


}
