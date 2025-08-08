package com.ebang.openapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.channel.HSTongChannel;
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
public class HSTongMcpService {

    @Autowired
    private HSTongChannel hsTongChannel;

    public record HSTongAccountSummaryReq(
            @ToolParam(description = "交易市场类型 包含:'K'-港股/'P'-美股/'v'-深股通/'t'-沪股通,默认K") String exchangeType

    ) {
    }

    @Tool(description = "获取账户汇总信息")
    public ResultResponse<JSONObject> hstongGetAccountSummary(HSTongAccountSummaryReq req, ToolContext toolContext) {
        AccountSummaryReq accountSummaryReq = RequestUtils.requestHandle(req, AccountSummaryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(hsTongChannel.getAccountSummary(accountSummaryReq))));
    }


    public record HSTongCashFlowQueryReq(
            @ToolParam(description = "交易类型，为必填项。可能值：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通，用于标识要查询数据所属的交易市场类型")
                    String exchangeType,
            @ToolParam(description = "起始日期，格式要求为：yyyyMMdd，用于限定查询数据的起始时间范围", required = false)
            Integer startDate,
            @ToolParam(description = "结束日期，格式要求为：yyyyMMdd，用于限定查询数据的结束时间范围", required = false)
            Integer endDate
    ) { }

    @Tool(description = "查询交易账户的当日资金流水和历史资金流水")
    public ResultResponse<JSONArray> hstongGetCashFlowSummary(HSTongCashFlowQueryReq req, ToolContext toolContext) throws Exception {
        CashFlowSummaryReq cashFlowSummaryReq = RequestUtils.requestHandle(req, CashFlowSummaryReq.class, toolContext);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(hsTongChannel.getCashFlowSummary(cashFlowSummaryReq))));
    }


    public record HSTongOrderInfoQueryReq(
        @ToolParam(description = "订单编号") String orderId,
        @ToolParam(description = "账号") String account,
        @ToolParam(description = "交易员秘钥(机构用户专用)") String secretKey,
        @ToolParam(description = "账户ID") String accountId,
        @ToolParam(description = "客户端订单ID") String clientOrderId,
        @ToolParam(description = "订单委托编号") String entrustId,
        @ToolParam(description = "订单流水号") String serialNo
    ){}

    @Tool(description = "获取订单信息")
    public ResultResponse<JSONObject> hstongGetOrderInfo(HSTongOrderInfoQueryReq req, ToolContext toolContext) throws Exception {
        OrderInfoQueryReq orderInfoQueryReq = RequestUtils.requestHandle(req, OrderInfoQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(hsTongChannel.getOrderInfo(orderInfoQueryReq))));
    }

    public record HSTongOrderListQueryReq(
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
    ){}

    @Tool(description = "获取订单列表")
    public ResultResponse<JSONObject> hstongGetOrderList(HSTongOrderListQueryReq req, ToolContext toolContext) throws Exception {
        OrderListQueryReq orderListQueryReq = RequestUtils.requestHandle(req, OrderListQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(hsTongChannel.getOrderList(orderListQueryReq))));
    }

    public record HSTongModifyOrderReq(
        @ToolParam(description = "原始委托编号") String entrustId,
        @ToolParam(description = "委托数量") java.math.BigDecimal entrustAmount,
        @ToolParam(description = "委托价格，<如果为条件单，该值可为空>") java.math.BigDecimal entrustPrice,
        @ToolParam(description = "委托类型") String entrustType,
        @ToolParam(description = "盘前盘后交易，0:否 1:是 3:只支持盘中 5:港股支持盘中及暗盘 7:美股支持盘中及盘前盘后") String sessionType,
        @ToolParam(description = "有效天数") String validDays,
        @ToolParam(description = "条件单触发价格") java.math.BigDecimal condValue,
        @ToolParam(description = "条件单跟踪类型") String condTrackType
    ){}

    @Tool(description = "修改订单")
    public ResultResponse<JSONObject> hstongModifyOrder(HSTongModifyOrderReq req, ToolContext toolContext) throws Exception {
        ModifyOrderReq modifyOrderReq = RequestUtils.requestHandle(req, ModifyOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(hsTongChannel.modifyOrder(modifyOrderReq))));
    }

    public record HSTongCancelOrderReq(
        @ToolParam(description = "原始委托编号") String entrustId
    ){}

    @Tool(description = "撤销订单")
    public ResultResponse<JSONObject> hstongCancelOrder(HSTongCancelOrderReq req, ToolContext toolContext) throws Exception {
        CancelOrderReq cancelOrderReq = RequestUtils.requestHandle(req, CancelOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(hsTongChannel.cancelOrder(cancelOrderReq))));
    }

    public record HSTongCreateOrderReq(
        @ToolParam(description = "交易类型，为必填项。可能值：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通") String exchangeType,
        @ToolParam(description = "证券代码，为必填项") String stockCode,
        @ToolParam(description = "委托数量") java.math.BigDecimal entrustAmount,
        @ToolParam(description = "委托价格") java.math.BigDecimal entrustPrice,
        @ToolParam(description = "买卖方向") String entrustBs,
        @ToolParam(description = "委托类型") String entrustType,
        @ToolParam(description = "交易所") String exchange,
        @ToolParam(description = "盘前盘后交易类型") String sessionType,
        @ToolParam(description = "冰山委托显示数量") java.math.BigDecimal iceBergDisplaySize,
        @ToolParam(description = "有效天数") String validDays,
        @ToolParam(description = "条件单触发价格") java.math.BigDecimal condValue,
        @ToolParam(description = "条件单跟踪类型") String condTrackType
    ){}

    @Tool(description = "创建订单")
    public ResultResponse<JSONObject> hstongCreateOrder(HSTongCreateOrderReq req, ToolContext toolContext) throws Exception {
        CreateOrderReq createOrderReq = RequestUtils.requestHandle(req, CreateOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(hsTongChannel.createOrder(createOrderReq))));
    }

    public record HSTongMaximumTradableQuantityReq(
            @ToolParam(description = "交易类型，为必填项。可能值：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通") String exchangeType,
            @ToolParam(description = "证券代码，为必填项，用于标识具体交易的证券") String stockCode,
            @ToolParam(description = "委托价格，为必填项，即交易时设定的价格") String entrustPrice,
            @ToolParam(description = "委托类型，为必填项，不同交易类型对应不同可能值：港股：'0'-竞价限价、'1'-竞价、'2'-增强限价盘、'3'-限价盘、'4'-特别限价盘、'6'-暗盘、'7'-碎股；美股：'3'-限价盘、'5'-市价盘、'8'-冰山市价、'9'-冰山限价、'10'-隐藏市价、'11'-隐藏限价；A股：'3'-限价盘") String entrustType
    ) { }

    @Tool(description = "查询最大可买可卖数量、期权保证金值")
    public ResultResponse<JSONObject> hstongGetMaximumTradableQuantity(HSTongMaximumTradableQuantityReq req, ToolContext toolContext) throws Exception {
        MaximumTradableQuantityReq maximumTradableQuantityReq = RequestUtils.requestHandle(req, MaximumTradableQuantityReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(hsTongChannel.getMaximumTradableQuantity(maximumTradableQuantityReq))));
    }

    public record HSTongHistoryTransactionReq(
        @ToolParam(description = "华盛通证券：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通") String exchangeType,
        @ToolParam(description = "起始日期，格式为：yyyyMMdd") String startDate,
        @ToolParam(description = "结束日期，格式为：yyyyMMdd") String endDate,
        @ToolParam(description = "初始值为0 开始，分页时传上一页最后一条数据的queryParamStr") String queryParamStr,
        @ToolParam(description = "每页返回数量") Integer limit
    ){}

    @Tool(description = "获取历史成交")
    public ResultResponse<JSONObject> hstongGetHistoryTransaction(HSTongHistoryTransactionReq req, ToolContext toolContext) throws Exception {
        HistoryTransactionReq historyTransactionReq = RequestUtils.requestHandle(req, HistoryTransactionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(hsTongChannel.getHistoryTransaction(historyTransactionReq))));
    }

    public record HSTongTodayTransactionReq(
        @ToolParam(description = "华盛通证券：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通") String exchangeType,
        @ToolParam(description = "初始值为0 开始，分页时传上一页最后一条数据的queryParamStr") String queryParamStr,
        @ToolParam(description = "每页返回数量") Integer limit
    ){}

    @Tool(description = "获取当日成交")
    public ResultResponse<JSONObject> hstongGetTodayTransaction(HSTongTodayTransactionReq req, ToolContext toolContext) throws Exception {
        TodayTransactionReq todayTransactionReq = RequestUtils.requestHandle(req, TodayTransactionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(hsTongChannel.getTodayTransaction(todayTransactionReq))));
    }

    public record HSTongUnlockTradingReq(
           // @ToolParam(description = "交易密码") String password
    ) { }
    @Tool(description = "解锁交易")
    public Object hsTongUnlockTrading(HSTongUnlockTradingReq req) throws Exception {
        UnlockTradingReq unlockTradingReq = new UnlockTradingReq();
        BeanUtils.copyProperties(req, unlockTradingReq);
        return hsTongChannel.unlockTrading(unlockTradingReq);
    }

    public record HSTongMarginTradingDataReq(
            @ToolParam(description = "股票类型，为必填项。值需参考对应的数据字典中股票类型的定义，用于区分不同种类的股票")
            String dataType,
            @ToolParam(description = "股票代码，为必填项。用于唯一标识具体的股票，不同市场、不同股票有各自对应的代码")
            String stockCode
    ) { }

    @Tool(description = "查询股票融资融券信息")
    public Object hsTongGetMarginTradingData(HSTongMarginTradingDataReq req) throws Exception {
        MarginTradingDataReq marginTradingDataReq = new MarginTradingDataReq();
        BeanUtils.copyProperties(req, marginTradingDataReq);
        return hsTongChannel.getMarginTradingData(marginTradingDataReq);
    }

    public record HSTongTradeQueryBeforeAndAfterSupportReq(
            @ToolParam(description = "交易类型	'K'-港股、'P'-美股、'v'-深股通、't'-沪股通") String exchangeType,
            @ToolParam(description = "证券代码") String stockCode
    ) { }

    @Tool(description = "查询标的是否支持盘前盘后交易")
    public Object hsTongTradeQueryBeforeAndAfterSupport(HSTongTradeQueryBeforeAndAfterSupportReq req) throws Exception {
        TradeQueryBeforeAndAfterSupportReq tradeQueryBeforeAndAfterSupportReq = new TradeQueryBeforeAndAfterSupportReq();
        BeanUtils.copyProperties(req, tradeQueryBeforeAndAfterSupportReq);
        return hsTongChannel.tradeQueryBeforeAndAfterSupport(tradeQueryBeforeAndAfterSupportReq);
    }


    public record HSTongRateReq(
            @ToolParam(description = "0:换汇汇率, 1:即期汇率。如果不传，默认查询换汇汇率", required = false)
                    Integer rateType
    ) { }

    @Tool(description = "查询各币种的当前汇率（CNY、HKD、USD）")
    public Object hsTongGetRate(HSTongRateReq req) throws Exception {
        RateReq rateReq = new RateReq();
        BeanUtils.copyProperties(req, rateReq);
        return hsTongChannel.getRate(rateReq);
    }

    @Tool(description = "查询交易账户的持仓股票列表")
    public Object hsTongGetAllPosition(@ToolParam(description = "交易市场类别，可能的值：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通", required = false) String exchangeType,
                                       ToolContext toolContext) throws Exception {
        RequestUtils.requestHandle(toolContext);
        PositionListQueryReq req = new PositionListQueryReq();
        req.setExchangeType(exchangeType);
        return hsTongChannel.getAllPosition(req);
    }

}
