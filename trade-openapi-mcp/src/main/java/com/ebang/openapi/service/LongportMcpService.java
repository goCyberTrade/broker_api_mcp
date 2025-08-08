package com.ebang.openapi.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ebang.openapi.channel.LongportChannel;
import com.ebang.openapi.enums.ProductTypeEnums;
import com.ebang.openapi.req.*;
import com.ebang.openapi.resp.ResultResponse;
import com.ebang.openapi.util.RequestUtils;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaobo
 * @description
 * @date 2025/7/11 9:28
 */
@Service
public class LongportMcpService {

    @Autowired
    private LongportChannel longportChannel;

    public record LongportCashFlowSummaryReq(
            @ToolParam(description = "开始时间，例如：2025-05-10 00:00:00，为必填项") String startTime,
            @ToolParam(description = "结束时间，例如：2025-05-11 00:00:00，为必填项") String endTime,
            @ToolParam(description = "资金类型，可选值：1 - 现金、2 - 股票、3 - 基金，非必填项", required = false) Integer businessType,
            @ToolParam(description = "标的代码，例如：AAPL.US，非必填项", required = false) String symbol){
    }

    @Tool(description = "该接口用于获取资金流入/流出方向、资金类别、资金金额、发生时间、关联股票代码和资金流水说明信息")
    public ResultResponse<JSONArray> longportGetCashFlowSummary(LongportCashFlowSummaryReq req, ToolContext toolContext) throws Exception {
        CashFlowSummaryReq cashFlowSummaryReq = RequestUtils.requestHandle(req, CashFlowSummaryReq.class, toolContext);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(longportChannel.getCashFlowSummary(cashFlowSummaryReq))));
    }

    public record LongportAvailableFundsReq(
            @ToolParam(description = "币种，包含USD/HKD/CNH,默认为空") String currency
    ){}

    @Tool(description = "获取可用资金")
    public ResultResponse<JSONArray> longportAvailableFunds(LongportAvailableFundsReq req,ToolContext toolContext) throws Exception {
        //设置上下文
        AvailableFundsReq availableFundsReq=RequestUtils.requestHandle(req,AvailableFundsReq.class,toolContext);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(longportChannel.availableFunds(availableFundsReq))));
    }

    public record LongportOrderInfoQueryReq(
            @ToolParam(description = "订单ID") String orderId
    ){}

    @Tool(description = "获取订单详情。" +
            "返回信息:订单详情信息，包含order_id、status、stock_name、quantity、executed_quantity、price、executed_price、submitted_at、side、symbol、order_type、last_done、trigger_price、msg、tag、time_in_force、expire_date、updated_at、trigger_at、trailing_amount、trailing_percent、limit_offset、trigger_status、outside_rth、currency、remark等信息。" +
            "订单历史状态变动列表 history，包含price、quantity、status、msg、time。" +
            "订单收费明细 charge_detail，包含各收费条目明细以及收费总金额、收费币种")
    public ResultResponse<JSONObject> longportGetOrderInfo(LongportOrderInfoQueryReq req, ToolContext toolContext) throws Exception {
        OrderInfoQueryReq orderInfoQueryReq = RequestUtils.requestHandle(req, OrderInfoQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(longportChannel.getOrderInfo(orderInfoQueryReq))));
    }

    public record LongportOrderListQueryReq(
            @ToolParam(description = "标的代码，ticker.region 格式，例如：AAPL.US、00700.HK", required = false) String symbol,
            @ToolParam(description = "需要筛选的订单状态 枚举值有:NotReported,ReplacedNotReported,ProtectedNotReported,VarietiesNotReported,Filled,WaitToNew,New,WaitToReplace,PendingReplace,Replaced,PartialFilled,WaitToCancel,PendingCancel,Rejected,Canceled,Expired,PartialWithdrawal", required = false) List<String> status,
            @ToolParam(description = "买卖方向 枚举值有:Buy、Sell", required = false) String side,
            @ToolParam(description = "市场 枚举值有:HK、US、CN、SG", required = false) String market,
            @ToolParam(description = "订单编号，此字段只能查询当日内的订单", required = false) String orderId,
            @ToolParam(description = "订单开始时间，毫秒时间戳。开始时间为空时，默认为结束时间或当前时间前九十天", required = false) Long startAt,
            @ToolParam(description = "订单结束时间，毫秒时间戳。结束时间为空时，默认为开始时间后九十天或当前时间", required = false) Long endAt
    ){}

    @Tool(description = "获取订单列表。" +
            "返回信息:订单信息，包含订单currency、executed_price、executed_quantity、expire_date、last_done、limit_offset、msg、order_id、order_type、outside_rth、price、quantity、side、status、stock_name、submitted_at、symbol、tag、time_in_force、trailing_amount、trailing_percent、trigger_at、trigger_price、trigger_status、updated_at、remark:等信息")
    public ResultResponse<JSONArray> longportGetOrderList(LongportOrderListQueryReq req, ToolContext toolContext) throws Exception {
        OrderListQueryReq orderListQueryReq = RequestUtils.requestHandle(req, OrderListQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(longportChannel.getOrderList(orderListQueryReq))));
    }

    public record LongportModifyOrderReq(
            @ToolParam(description = "订单ID") String orderId,
            @ToolParam(description = "改单数量", required = false) BigDecimal quantity,
            @ToolParam(description = "改单价格，LO、ELO、ALO、ODD、LIT 类型订单必填", required = false) BigDecimal price,
            @ToolParam(description = "触发价格，LIT、MIT 类型订单必填", required = false) BigDecimal triggerPrice,
            @ToolParam(description = "指定价差 TSLPAMT、TSLPPCT 类型订单必填", required = false) BigDecimal limitOffset,
            @ToolParam(description = "跟踪金额 TSLPAMT 类型订单必填", required = false) BigDecimal trailingAmount,
            @ToolParam(description = "跟踪涨跌幅，单位为百分比，例如2.5表示2.5%。TSLPPCT 类型订单必填", required = false) BigDecimal trailingPercent,
            @ToolParam(description = "备注", required = false) String remark
    ){}

    @Tool(description = "修改订单" +
            "返回信息:订单修改结果")
    public ResultResponse<JSONObject> longportModifyOrder(LongportModifyOrderReq req, ToolContext toolContext) throws Exception {
        ModifyOrderReq modifyOrderReq = RequestUtils.requestHandle(req, ModifyOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(longportChannel.modifyOrder(modifyOrderReq))));
    }

    public record LongportCancelOrderReq(
            @ToolParam(description = "订单ID") String orderId
    ){}

    @Tool(description = "修改订单" +
            "返回信息:订单撤销结果")
    public ResultResponse<JSONObject> longportCancelOrder(LongportCancelOrderReq req, ToolContext toolContext) throws Exception {
        CancelOrderReq cancelOrderReq = RequestUtils.requestHandle(req, CancelOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(longportChannel.cancelOrder(cancelOrderReq))));
    }

    public record LongportCreateOrderReq(

            @ToolParam(description = "股票代码，使用ticker.regin格式，例如:AAPL.US、00700.HK") String symbol,
            @ToolParam(description = "订单类型，枚举值有:LO、ELO、MO、AO、ALO、ODD、LIT、MIT、TSLPAMT、TSLPPCT、TSMAMT、TSMPCT、SLO") String orderType,
            @ToolParam(description = "下单价格，LO、ELO/ALO/ODD/LIT 类型订单必填", required = false) BigDecimal submittedPrice,
            @ToolParam(description = "下单数量") BigDecimal submittedQuantity,
            @ToolParam(description = "触发价格 LIT、MIT 类型订单必填", required = false) BigDecimal triggerPrice,
            @ToolParam(description = "指定价差 TSLPAMT、TSLPPCT 类型订单必填", required = false) BigDecimal limitOffset,
            @ToolParam(description = "跟踪金额 TSLPAMT 类型订单必填", required = false) BigDecimal trailingAmount,
            @ToolParam(description = "跟踪涨跌幅，单位为百分比，例如2.5表示2.5%。TSLPPCT 类型订单必填", required = false) BigDecimal trailingPercent,
            @ToolParam(description = "长期单过期时间 格式为yyyy-MM-dd，timeInForce为GTD时必填", required = false) LocalDate expireDate,
            @ToolParam(description = "买卖方向，枚举值有:Buy、Sell") String side,
            @ToolParam(description = "是否允许盘前盘后，枚举值有:RTH_ONLY(不允许盘前盘后)、ANY_TIME(允许盘前盘后)、OVERNIGHT(夜盘)", required = false) String outsideRTHStr,
            @ToolParam(description = "订单有效期类型，枚举值有:Day、GTC、GTD") String timeInForce,
            @ToolParam(description = "备注", required = false) String remark

    ){}

    @Tool(description = "创建订单" +
            "返回信息:订单创建结果")
    public ResultResponse<JSONObject> longportCreateOrder(LongportCreateOrderReq req, ToolContext toolContext) throws Exception {
        CreateOrderReq createOrderReq = RequestUtils.requestHandle(req, CreateOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(longportChannel.createOrder(createOrderReq))));
    }

    public record LongportMaximumTradableQuantityReq(
            @ToolParam(description = "股票代码，为必填项。使用 ticker.region 格式，例如：AAPL.US，用于唯一标识要交易的股票") String symbol,
            @ToolParam(description = "订单类型，为必填项。具体类型可参考相关定义（如链接 订单类型 指向的内容 ），用于指定订单的类型（如限价单、市价单等 ）") String order_type,
            @ToolParam(description = "\"LO\"=\"限价单\";\"ELO\"=\"增强限价单\";\"MO\"=\"市价单\";\"AO\"=\"竞价市价单\";\"ALO\"=\"竞价限价单\";\"ODD\"=\"碎股单挂单\";\"LIT\"=\"触价限价单\";\"MIT\"=\"触价市价单\";\"TSLPAMT\"=\"跟踪止损限价单(跟踪金额)\";\"TSLPPCT\"=\"跟踪止损限价单(跟踪涨跌幅)\";\"SLO\"=\"特殊限价单，不支持改单\"") String longportOrderType,
            @ToolParam(description = "预估下单价格，为选填项。例如：388.5，用于预先设定下单的价格参考", required = false) BigDecimal price,
            @ToolParam(description = "买卖方向，为必填项。可选值：Buy - 买入，Sell - 卖出（卖出只支持美股卖空查询 ），用于指定股票交易的方向") String side,
            @ToolParam(description = "结算货币，为选填项，用于指定交易结算时使用的货币", required = false) String currency,
            @ToolParam(description = "订单 ID，为选填项，获取改单预估最大购买数量时必填，用于标识具体的订单，改单场景下需要", required = false) String orderId
    ) { }

    /**
     * 预估最大购买数量
     */
    @Tool(description = "该接口用于港美股，窝轮，期权的预估最大购买数量")
    public ResultResponse<JSONObject> longportGetMaximumTradableQuantity(LongportMaximumTradableQuantityReq req, ToolContext toolContext) throws Exception {
        MaximumTradableQuantityReq maximumTradableQuantityReq = RequestUtils.requestHandle(req, MaximumTradableQuantityReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(longportChannel.getMaximumTradableQuantity(maximumTradableQuantityReq))));
    }

    public record LongportMarginRatioReq(
            @ToolParam(description = "股票代码，例如：AAPL.US") String symbol
    ){}

    @Tool(description = "该接口用于获取股票初始保证金比例、维持保证金比例、强平保证金比例")
    public ResultResponse<JSONObject> longportMarginRatio(LongportMarginRatioReq req, ToolContext toolContext) throws Exception {
        MarginRatioReq marginRatioReq = RequestUtils.requestHandle(req, MarginRatioReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(longportChannel.marginRatio(marginRatioReq))));
    }

    public record LongportHistoryTransactionReq(
            @ToolParam(description = "TODO: symbol") String symbol,
            @ToolParam(description = "TODO: startDate") String startDate,
            @ToolParam(description = "TODO: endDate") String endDate
    ){}

    @Tool(description = "获取历史成交，用于获取历史订单的成交明细，包括买入和卖出的成交记录，不支持当日成交明细查询")
    public ResultResponse<JSONObject> longportGetHistoryTransaction(LongportHistoryTransactionReq req, ToolContext toolContext) throws Exception {
        HistoryTransactionReq historyTransactionReq = RequestUtils.requestHandle(req, HistoryTransactionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(longportChannel.getHistoryTransaction(historyTransactionReq))));
    }

    public record LongportTodayTransactionReq(
            @ToolParam(description = "TODO: symbol") String symbol,
            @ToolParam(description = "TODO: orderId") String orderId
    ){}

    @Tool(description = "获取当日成交，用于获取当日订单的成交明细")
    public ResultResponse<JSONObject> longportGetTodayTransaction(LongportTodayTransactionReq req, ToolContext toolContext) throws Exception {
        TodayTransactionReq todayTransactionReq = RequestUtils.requestHandle(req, TodayTransactionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(longportChannel.getTodayTransaction(todayTransactionReq))));
    }

    @Tool(description = "获取股票持仓，用于获取包括账户、股票代码、持仓股数、可用股数、持仓均价（按账户设置计算均价方式）、币种在内的股票持仓信息")
    public ResultResponse<JSONArray> longportGetStockPosition(@ToolParam(description = "股票代码，使用 ticker.region 格式，例如：AAPL.US，接受一个数组") String[] symbolArray, ToolContext toolContext) throws Exception {
        return getPosition(symbolArray, ProductTypeEnums.STOCK, toolContext);
    }

    @Tool(description = "获取基金持仓， 用于获取包括账户、基金代码、持有份额、成本净值、当前净值、币种在内的基金持仓信息")
    public ResultResponse<JSONArray> longportGetFundsPosition(@ToolParam(description = "股票代码，使用 ticker.region 格式，例如：AAPL.US，接受一个数组") String[] symbolArray, ToolContext toolContext) throws Exception {
        return getPosition(symbolArray, ProductTypeEnums.FUNDS, toolContext);
    }

    private ResultResponse<JSONArray> getPosition(String[] symbolArray, ProductTypeEnums productTypeEnums, ToolContext toolContext) throws Exception {
        RequestUtils.requestHandle(toolContext);
        String symbol = null;
        if (symbolArray != null) {
            symbol = Arrays.stream(symbolArray).distinct().collect(Collectors.joining(","));
        }
        PositionListQueryReq queryReq = new PositionListQueryReq();
        queryReq.setProductType(productTypeEnums.getProductType());
        queryReq.setSymbol(symbol);
        Object allPosition = longportChannel.getAllPosition(queryReq);
        return ResultResponse.success(JSON.parseArray(JSONObject.toJSONString(allPosition)));
    }

}
