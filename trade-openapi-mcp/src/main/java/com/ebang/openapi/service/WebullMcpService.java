package com.ebang.openapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.channel.WebullChannel;
import com.ebang.openapi.req.*;
import com.ebang.openapi.resp.ResultResponse;
import com.ebang.openapi.util.RequestUtils;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xiaobo
 * @description
 * @date 2025/7/11 9:28
 */
@Service
public class WebullMcpService {

    @Autowired
    private WebullChannel webullChannel;

    public record WebullAccountSummaryReq(
            @ToolParam(description = "账户") String accountId
    ) {
    }

    @Tool(description = "获取账户概览")
    public ResultResponse<JSONObject> webullGetAccountSummary(WebullAccountSummaryReq req, ToolContext toolContext) {
        AccountSummaryReq accountSummaryReq = RequestUtils.requestHandle(req, AccountSummaryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.getAccountSummary(accountSummaryReq))));
    }
    public record WebullBalanceReq(
            @ToolParam(description = "账户") String accountId,
            @ToolParam(description = "币种,包含：HKD/USD/CNH，默认HKD") String currency
    ){}

    @Tool(description = "获取账户余额")
    public ResultResponse<JSONObject> webullBalances(WebullBalanceReq req, ToolContext toolContext) {
        BalanceReq balanceReq = RequestUtils.requestHandle(req, BalanceReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.balances(balanceReq))));
    }

    @Tool(description = "获取账户列表")
    public ResultResponse<JSONObject> webullAccounts(ToolContext toolContext) {
        RequestUtils.requestHandle(toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.accounts(new AccountsReq()))));
    }

    public record WebullGetSymbolReq(
            @ToolParam(description = "标的id", required = false) String instrumentId
    ) {
    }

    @Tool(description = "查询交易标的信息")
    public ResultResponse<JSONObject> getSymbol(WebullGetSymbolReq req, ToolContext toolContext) throws Exception {
        GetSymbolReq getSymbolReq = RequestUtils.requestHandle(req, GetSymbolReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.getSymbol(getSymbolReq))));
    }

    public record WebullInstrumentInfoReq(
            @ToolParam(description = "标的代码，为必填项，示例值如：SPX") String symbol,
            @ToolParam(description = "市场，为必填项，示例值如：HK ，可参考 Markets 相关定义") String market,
            @ToolParam(description = "标的大类，为必填项，示例值如：EQUITY; OPTION") String instrumentSuperType,
            @ToolParam(description = "标的二级分类，非必填项，但查询期权信息时必传。示例值如：CALL_OPTION-看涨期权；PUT_OPTION-看跌期权", required = false) String instrumentType,
            @ToolParam(description = "期权行权价，非必填项，但查询期权信息时必传，示例值如：3400", required = false) String strikePrice,
            @ToolParam(description = "期权失效日期，格式 yyyy-MM-dd ，非必填项，但查询期权信息时必传。示例值如：2024-12-20", required = false) String initExpDate,
            @ToolParam(description = "标的id", required = false) String instrumentId
    ) {
    }

    @Tool(description = "根据symbol/markets/instrument_super_type等条件查询交易标的信息。")
    public ResultResponse<JSONObject> getInstrumentInfo(WebullInstrumentInfoReq req, ToolContext toolContext) throws Exception {
        InstrumentInfoReq instrumentInfoReq = RequestUtils.requestHandle(req, InstrumentInfoReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.getInstrumentInfo(instrumentInfoReq))));
    }


    public record WebullOrderInfoQueryReq(
            @ToolParam(description = "订单编号") String orderId,
            @ToolParam(description = "账号") String account,
            @ToolParam(description = "交易员秘钥(机构用户专用)") String secretKey,
            @ToolParam(description = "账户ID") String accountId,
            @ToolParam(description = "客户端订单ID") String clientOrderId,
            @ToolParam(description = "订单委托编号") String entrustId,
            @ToolParam(description = "订单流水号") String serialNo
    ) {
    }

    @Tool(description = "获取订单信息")
    public ResultResponse<JSONObject> webullGetOrderInfo(WebullOrderInfoQueryReq req, ToolContext toolContext) throws Exception {
        OrderInfoQueryReq orderInfoQueryReq = RequestUtils.requestHandle(req, OrderInfoQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.getOrderInfo(orderInfoQueryReq))));
    }

    public record WebullOrderListQueryReq(
            @ToolParam(description = "账户ID") String accountId,
            @ToolParam(description = "公共过滤对象") TrdFilterConditions trdFilterConditions,
            @ToolParam(description = "订单状态") java.util.List<Integer> statusList,
            @ToolParam(description = "标的代码") String symbol,
            @ToolParam(description = "订单状态") java.util.List<String> status,
            @ToolParam(description = "买卖方向") String side,
            @ToolParam(description = "市场") String market,
            @ToolParam(description = "订单编号") String orderId,
            @ToolParam(description = "历史订单开始时间(毫秒时间戳)") Long startAt,
            @ToolParam(description = "历史订单结束时间(毫秒时间戳)") Long endAt,
            @ToolParam(description = "账号") String account,
            @ToolParam(description = "证券类型") String secType,
            @ToolParam(description = "订单开始时间(毫秒时间戳)") String startDate,
            @ToolParam(description = "订单结束时间(毫秒时间戳)") String endDate,
            @ToolParam(description = "限制条数（默认100，最大300）") Integer limit,
            @ToolParam(description = "账户分段") java.util.List<Integer> states,
            @ToolParam(description = "账户分段") String segType,
            @ToolParam(description = "期权方向") String right,
            @ToolParam(description = "过期日") String expiry,
            @ToolParam(description = "交易员秘钥(机构用户专用)") String secretKey,
            @ToolParam(description = "交易市场类型") java.util.List<String> exchangeType,
            @ToolParam(description = "起始日期(yyyyMMdd)") String startDateYmd,
            @ToolParam(description = "截止日期") String endDateYmd,
            @ToolParam(description = "证券代码") String stockCode
    ) {
    }

    @Tool(description = "获取订单列表")
    public ResultResponse<JSONObject> webullGetOrderList(WebullOrderListQueryReq req, ToolContext toolContext) throws Exception {
        OrderListQueryReq orderListQueryReq = RequestUtils.requestHandle(req, OrderListQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.getOrderList(orderListQueryReq))));
    }

    public record WebullModifyOrderReq(
            @ToolParam(description = "账户ID") String accountId,
            @ToolParam(description = "客户端订单ID") String clientOrderId,
            @ToolParam(description = "限价，order_type为 LIMIT、STOP_LOSS_LIMIT、ENHANCED_LIMIT、AT_AUCTION_LIMIT(竞价限价盘)必传") java.math.BigDecimal limitPrice,
            @ToolParam(description = "下单的标的数量，整数，支持的最大值为1000000股") java.math.BigDecimal qty,
            @ToolParam(description = "止损价，order_type为 STOP_LOSS(止损单)、STOP_LOSS_LIMIT(止损限价)时，需要传") java.math.BigDecimal stopPrice,
            @ToolParam(description = "跟踪止损单的价差数值，跟踪止损单要传") java.math.BigDecimal trailingStopStep,
            @ToolParam(description = "跟踪止损单的价差类型，跟踪止损单要传") String trailingType
    ) {
    }

    @Tool(description = "修改订单")
    public ResultResponse<JSONObject> webullModifyOrder(WebullModifyOrderReq req, ToolContext toolContext) throws Exception {
        ModifyOrderReq modifyOrderReq = RequestUtils.requestHandle(req, ModifyOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.modifyOrder(modifyOrderReq))));
    }

    public record WebullCancelOrderReq(
            @ToolParam(description = "账户ID") String accountId,
            @ToolParam(description = "客户端订单ID") String clientOrderId
    ) {
    }

    @Tool(description = "撤销订单")
    public ResultResponse<JSONObject> webullCancelOrder(WebullCancelOrderReq req, ToolContext toolContext) throws Exception {
        CancelOrderReq cancelOrderReq = RequestUtils.requestHandle(req, CancelOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.cancelOrder(cancelOrderReq))));
    }

    public record WebullCreateOrderReq(
            @ToolParam(description = "账户ID") String accountId,
            @ToolParam(description = "客户端订单ID") String clientOrderId,
            @ToolParam(description = "买卖方向") String side,
            @ToolParam(description = "有效期限") String tif,
            @ToolParam(description = "是否允许盘前盘后成交") Boolean extendedHoursTrading,
            @ToolParam(description = "标的ID") String instrumentId,
            @ToolParam(description = "订单类型") String orderType,
            @ToolParam(description = "限价") java.math.BigDecimal limitPrice,
            @ToolParam(description = "下单的标的数量") java.math.BigDecimal qty,
            @ToolParam(description = "止损价") java.math.BigDecimal stopPrice,
            @ToolParam(description = "跟踪止损单的价差类型") String trailingType,
            @ToolParam(description = "跟踪止损单的价差数值") java.math.BigDecimal trailingStopStep
    ) {
    }

    @Tool(description = "创建订单")
    public ResultResponse<JSONObject> webullCreateOrder(WebullCreateOrderReq req, ToolContext toolContext) throws Exception {
        CreateOrderReq createOrderReq = RequestUtils.requestHandle(req, CreateOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.createOrder(createOrderReq))));
    }

    public record WebullPositionListQuery(
            @ToolParam(description = "账户ID") String accountId,
            @ToolParam(description = "每页条数。默认值:10;最大值:100，可填整数", required = false) Integer pageSize,
            @ToolParam(description = "上一页最后一个的标的id，不传默认查第一页", required = false) String lastInstrumentId
    ){}

    @Tool(description = "根据账户 id 分页查询账号持仓列表")
    public ResultResponse<JSONObject> webullGetPositionList(
            @ToolParam(description = "账户ID") String accountId,
            @ToolParam(description = "每页条数。默认值:10;最大值:100，可填整数", required = false) Integer pageSize,
            @ToolParam(description = "上一页最后一个的标的id，不传默认查第一页", required = false) String lastInstrumentId,
            ToolContext toolContext) throws Exception {
        RequestUtils.requestHandle(toolContext);
        PositionListQueryReq positionListQueryReq = new PositionListQueryReq();
        positionListQueryReq.setAccountId(accountId);
        positionListQueryReq.setPageSize(pageSize);
        positionListQueryReq.setLastInstrumentId(lastInstrumentId);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(webullChannel.getAllPosition(positionListQueryReq))));
    }
}
