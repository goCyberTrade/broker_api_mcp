package com.ebang.openapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.channel.FutuChannel;
import com.ebang.openapi.context.RequestContext;
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
import java.util.List;

/**
 * @author xiaobo
 * @description
 * @date 2025/7/11 9:28
 */
@Service
public class FutuMcpService {

    @Autowired
    private FutuChannel futuChannel;

    public record FutuCashFlowQueryReq(
            @ToolParam(description = "账户ID") Long accId,
            @ToolParam(description = "市场") Integer market,
            @ToolParam(description = "清算日期，为必填项。格式要求为 \"yyyy-MM-dd\"，如 \"2017-05-20\"，用于指定交易清算的具体日期") String clearingDate,
            @ToolParam(description = "现金流方向，为选填项。参见 TrdCashFlowDirection 的枚举定义，用于指定现金流的方向", required = false) Integer cashFlowDirection
    ) { }

    @Tool(description = "查询交易业务账户在指定日期的现金流水数据。数据覆盖出入金、调拨、货币兑换、买卖金融资产、融资融券利息等所有导致现金变动的事项。")
    public ResultResponse<JSONArray> futuGetCashFlowSummary(FutuCashFlowQueryReq req, ToolContext toolContext) throws Exception {
        CashFlowSummaryReq cashFlowSummaryReq = RequestUtils.requestHandle(req, CashFlowSummaryReq.class, toolContext);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(futuChannel.getCashFlowSummary(cashFlowSummaryReq))));
    }


    public record FutuOrderInfoQueryReq(
            @ToolParam(description = "账户ID，来自获取账户列表") String accId,
            @ToolParam(description = "交易市场，选择一个使用账户支持的交易市场(1:香港 2:美国 3:中国 4:香港CC 5:期货 6:新加坡 8:澳大利亚 10:香港模拟期货 11:美国模拟期货 12:新加坡模拟期货 13:日本模拟期货 15:日本 111:马来西亚 112:加拿大 113:香港基金 123:美国基金)") String trdMarket,
            @ToolParam(description = "订单编号") String orderId
    ){}

    @Tool(description = "获取订单详情。" +
            "返回信息:订单信息，包含订单trdSide(1:买入 2:卖出 3:卖空 4:买回)、orderType(1:普通 2:市价 5:绝对限价 6:竞价 7:竞价限价 8:特殊限价 9:特殊全量限价 10:止损 11:止损限价 12:触价市价 13:触价限价 14:追踪止损 15:追踪止损限价 16:时间加权平均价格市价 17:时间加权平均价格限价 18:成交量加权平均价格市价 19:成交量加权平均价格限价)、orderStatus(0:未提交 1:等待提交 2:提交中 3:提交失败 4:超时 5:已提交 10:部分成交 11:全部成交 12:部分撤单中 13:全部撤单中 14:部分已撤单 15:全部已撤单 21:失败 22:已禁用 23:已删除 24:成交后撤单)、orderID、orderIDEx、code、name、qty、price、createTime、updateTime、fillQty、fillAvgPrice、secMarket(1:香港 2:美国 31:中国上海 32:中国深圳 41:新加坡 51:日本 61:澳大利亚 71:马来西亚 81:加拿大 91:外汇)、createTimestamp、updateTimestamp、remark、timeInForce(0:当日有效 1:撤销前有效)、fillOutsideRTH、session（1:常规交易时段 2:盘后交易时段 3:所有时段 4:隔夜时段）、trailType（1:百分比 2:金额）等信息")
    public ResultResponse<JSONObject> futuGetOrderInfo(FutuOrderInfoQueryReq req, ToolContext toolContext) throws Exception {
        OrderInfoQueryReq orderInfoQueryReq = RequestUtils.requestHandle(req, OrderInfoQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.getOrderInfo(orderInfoQueryReq))));
    }
    public record FutuOrderListQueryReq(
            @ToolParam(description = "账户ID，来自获取账户列表") String accId,
            @ToolParam(description = "交易市场，选择一个使用账户支持的交易市场(1:香港 2:美国 3:中国 4:香港CC 5:期货 6:新加坡 8:澳大利亚 10:香港模拟期货 11:美国模拟期货 12:新加坡模拟期货 13:日本模拟期货 15:日本 111:马来西亚 112:加拿大 113:香港基金 123:美国基金)") String trdMarket,
            @ToolParam(description = "订单状态列表(0:未提交 1:等待提交 2:提交中 3:提交失败 4:超时 5:已提交 10:部分成交 11:全部成交 12:部分撤单中 13:全部撤单中 14:部分已撤单 15:全部已撤单 21:失败 22:已禁用 23:已删除 24:成交后撤单)", required = false) java.util.List<Integer> statusList,
            // TrdFilterConditions
            @ToolParam(description = "代码过滤，只返回包含这些代码的数据，没传不过滤", required = false) List<String> codeList,
            @ToolParam(description = "ID 主键过滤，只返回包含这些 ID 的数据，没传不过滤，订单是 orderID、成交是 fillID、持仓是 positionID", required = false) List<Long> idList,
            @ToolParam(description = "开始时间，yyyy-MM-dd HH:mm:ss格式", required = false) String beginTime,
            @ToolParam(description = "结束时间，yyyy-MM-dd HH:mm:ss格式", required = false) String endTime,
            @ToolParam(description = "服务器订单ID列表，可以用来替代orderID列表，二选一", required = false) List<String> orderIDExList,
            @ToolParam(description = "指定交易市场(1:香港 2:美国 3:中国 4:香港CC 5:期货 6:新加坡 8:澳大利亚 10:香港模拟期货 11:美国模拟期货 12:新加坡模拟期货 13:日本模拟期货 15:日本 111:马来西亚 112:加拿大 113:香港基金 123:美国基金)", required = false) Integer filterMarket
    ){}

    @Tool(description = "获取订单列表。" +
            "返回信息:订单信息，包含订单trdSide(1:买入 2:卖出 3:卖空 4:买回)、orderType(1:普通 2:市价 5:绝对限价 6:竞价 7:竞价限价 8:特殊限价 9:特殊全量限价 10:止损 11:止损限价 12:触价市价 13:触价限价 14:追踪止损 15:追踪止损限价 16:时间加权平均价格市价 17:时间加权平均价格限价 18:成交量加权平均价格市价 19:成交量加权平均价格限价)、orderStatus(0:未提交 1:等待提交 2:提交中 3:提交失败 4:超时 5:已提交 10:部分成交 11:全部成交 12:部分撤单中 13:全部撤单中 14:部分已撤单 15:全部已撤单 21:失败 22:已禁用 23:已删除 24:成交后撤单)、orderID、orderIDEx、code、name、qty、price、createTime、updateTime、fillQty、fillAvgPrice、secMarket(1:香港 2:美国 31:中国上海 32:中国深圳 41:新加坡 51:日本 61:澳大利亚 71:马来西亚 81:加拿大 91:外汇)、createTimestamp、updateTimestamp、remark、timeInForce(0:当日有效 1:撤销前有效)、fillOutsideRTH、session（1:常规交易时段 2:盘后交易时段 3:所有时段 4:隔夜时段）、trailType（1:百分比 2:金额）等信息")
    public ResultResponse<JSONObject> futuGetOrderList(FutuOrderListQueryReq req, ToolContext toolContext) throws Exception {
        OrderListQueryReq request = RequestUtils.requestHandle(req, OrderListQueryReq.class, toolContext);
        TrdFilterConditions trdFilterConditions = new TrdFilterConditions();
        trdFilterConditions.setCodeList(req.codeList);
        trdFilterConditions.setIdList(req.idList);
        trdFilterConditions.setBeginTime(req.beginTime);
        trdFilterConditions.setEndTime(req.endTime);
        trdFilterConditions.setOrderIDExList(req.orderIDExList);
        trdFilterConditions.setFilterMarket(req.filterMarket);
        request.setTrdFilterConditions(trdFilterConditions);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.getOrderList(request))));
    }

    public record FutuModifyOrderReq(
            @ToolParam(description = "账户ID") Long accId,
            @ToolParam(description = "交易市场（1:香港 2:美国 3:中国 4:香港CC 5:期货 6:新加坡 8:澳大利亚 10:香港模拟期货 11:美国模拟期货 12:新加坡模拟期货 13:日本模拟期货 15:日本 111:马来西亚 112:加拿大 113:香港基金 123:美国基金）") Integer trdMarket,
            @ToolParam(description = "订单号") String orderId,
            @ToolParam(description = "数量，期权单位是'张'（精确到小数点后0位，超出部分会被舍弃）", required = false) BigDecimal qty,
            @ToolParam(description = "价格（证券账户精确到小数点后3位，期货账户精确到小数点后9位，超出部分会被舍弃）", required = false) BigDecimal price,
            @ToolParam(description = "是否调整价格，如果价格不合法，是否调整到合法价位，true调整，false不调整。如果价格不合法又不允许调整，则会返回错误", required = false) Boolean adjustPrice,
            @ToolParam(description = "调整方向和调整幅度百分比限制，正数代表向上调整，负数代表向下调整，具体值代表调整幅度限制，如：0.015代表向上调整且幅度不超过1.5%；-0.01代表向下调整且幅度不超过1%", required = false) BigDecimal adjustSideAndLimit,
            @ToolParam(description = "触发价格", required = false) BigDecimal auxPrice,
            @ToolParam(description = "跟踪类型（1:比例 2:金额）", required = false) Integer trailType,
            @ToolParam(description = "跟踪金额/百分比", required = false) BigDecimal trailValue,
            @ToolParam(description = "指定价差", required = false) BigDecimal trailSpread
    ){}

    @Tool(description = "修改订单" +
            "返回信息:订单修改结果")
    public ResultResponse<JSONObject> futuModifyOrder(FutuModifyOrderReq req, ToolContext toolContext) throws Exception {
        ModifyOrderReq modifyOrderReq = RequestUtils.requestHandle(req, ModifyOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.modifyOrder(modifyOrderReq))));
    }

    public record FutuCancelOrderReq(
            @ToolParam(description = "账户ID") Long accId,
            @ToolParam(description = "交易市场（1:香港 2:美国 3:中国 4:香港CC 5:期货 6:新加坡 8:澳大利亚 10:香港模拟期货 11:美国模拟期货 12:新加坡模拟期货 13:日本模拟期货 15:日本 111:马来西亚 112:加拿大 113:香港基金 123:美国基金）") Integer trdMarket,
            @ToolParam(description = "订单号，forAll 为 true 时，传0") String orderId,
            @ToolParam(description = "是否对此业务账户的全部订单操作，true：对全部订单，false：对单个订单，不传默认为对单个订单。", required = false) Boolean forAll
    ){}

    @Tool(description = "撤销订单" +
            "返回信息:订单撤销结果")
    public ResultResponse<JSONObject> futuCancelOrder(FutuCancelOrderReq req, ToolContext toolContext) throws Exception {
        CancelOrderReq cancelOrderReq = RequestUtils.requestHandle(req, CancelOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.cancelOrder(cancelOrderReq))));
    }

    public record FutuCreateOrderReq(
            @ToolParam(description = "账户ID") Long accId,
            @ToolParam(description = "交易市场（1:香港 2:美国 3:中国 4:香港CC 5:期货 6:新加坡 8:澳大利亚 10:香港模拟期货 11:美国模拟期货 12:新加坡模拟期货 13:日本模拟期货 15:日本 111:马来西亚 112:加拿大 113:香港基金 123:美国基金）") Integer trdMarket,
            @ToolParam(description = "订单编号") String orderId,
            @ToolParam(description = "交易方向（1:买入 2:卖出 3:卖空 4:买回）") Integer trdSide,
            @ToolParam(description = "订单类型（0:未知 1:普通 2:市价 5:绝对限价 6:竞价 7:竞价限价 8:特殊限价 9:特殊全量限价 10:止损 11:止损限价 12:触价市价 13:触价限价 14:追踪止损 15:追踪止损限价 16:时间加权平均价格市价 17:时间加权平均价格限价 18:成交量加权平均价格市价 19:成交量加权平均价格限价）") String orderType,
            @ToolParam(description = "代码，港股必须是5位数字(例如00700)，A股必须是6位数字(例如600519)，美股(AAPL)") String code,
            @ToolParam(description = "数量，期权单位是'张'（精确到小数点后0位，超出部分会被舍弃。期权期货单位是'张'）") BigDecimal qty,
            @ToolParam(description = "价格（证券账户精确到小数点后3位，期货账户精确到小数点后9位，超出部分会被舍弃）", required = false) BigDecimal price,
            @ToolParam(description = "是否调整价格，如果价格不合法，是否调整到合法价位，true调整，false不调整。如果价格不合法又不允许调整，则会返回错误", required = false) Boolean adjustPrice,
            @ToolParam(description = "调整方向和调整幅度百分比限制，正数代表向上调整，负数代表向下调整，具体值代表调整幅度限制，如：0.015代表向上调整且幅度不超过1.5%；-0.01代表向下调整且幅度不超过1%", required = false) BigDecimal adjustSideAndLimit,
            @ToolParam(description = "证券所属市场（1:香港 2:美国 31:中国上海 32:中国深圳 41:新加坡 51:日本 61:澳大利亚 71:马来西亚 81:加拿大 91:外汇）", required = false) Integer secMarket,
            @ToolParam(description = "用户备注字符串，最多只能传64字节。可用于标识订单唯一信息等，下单填上，订单结构就会带上。", required = false) String remark,
            @ToolParam(description = "订单有效期限（0:日有效 1:撤销前有效）") String timeInForce,
            @ToolParam(description = "是否允许盘前盘后成交。仅适用于美股限价单。默认false", required = false) Boolean fillOutsideRTH,
            @ToolParam(description = "触发价格", required = false) BigDecimal auxPrice,
            @ToolParam(description = "跟踪类型（1:比例 2:金额）", required = false) Integer trailType,
            @ToolParam(description = "跟踪金额/百分比", required = false) BigDecimal trailValue,
            @ToolParam(description = "指定价差", required = false) BigDecimal trailSpread,
            @ToolParam(description = "美股订单时段（1:常规交易时段 2:盘后交易时段 3:所有时段 4:隔夜时段）", required = false) Integer session
    ){}

    @Tool(description = "创建订单" +
            "返回信息:订单创建结果以及orderId")
    public ResultResponse<JSONObject> futuCreateOrder(FutuCreateOrderReq req, ToolContext toolContext) throws Exception {
        CreateOrderReq createOrderReq = RequestUtils.requestHandle(req, CreateOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.createOrder(createOrderReq))));
    }

    public record FutuOrderFeesReq(
            @ToolParam(description = "账户ID") Long accId,
            @ToolParam(description = "交易市场（1:香港 2:美国 3:中国 4:香港CC 5:期货 6:新加坡 8:澳大利亚 10:香港模拟期货 11:美国模拟期货 12:新加坡模拟期货 13:日本模拟期货 15:日本 111:马来西亚 112:加拿大 113:香港基金 123:美国基金）") Integer trdMarket,
            @ToolParam(description = "订单号") String orderId
    ){}

    @Tool(description = "查询订单费用" +
            "返回信息:订单各种收费条目以及收费金额")
    public ResultResponse<JSONObject> futuOrderFees(FutuOrderFeesReq req, ToolContext toolContext) throws Exception {
        OrderFeesReq request = RequestUtils.requestHandle(req, OrderFeesReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.orderFees(request))));
    }

    public record FutuAvailableFundsReq(
        @ToolParam(description = "账户") String accountId
    ){}

    @Tool(description = "获取可用资金")
    public ResultResponse<JSONObject> futuAvailableFunds(FutuAvailableFundsReq req, ToolContext toolContext) throws Exception {
        AvailableFundsReq availableFundsReq = RequestUtils.requestHandle(req, AvailableFundsReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.availableFunds(availableFundsReq))));
    }

    @Tool(description = "获取账户列表")
    public ResultResponse<JSONObject> futuAccounts( ToolContext toolContext) throws Exception {
        RequestUtils.requestHandle(toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.accounts(new AccountsReq()))));
    }

    public record FutuMaximumTradableQuantityReq(
            @ToolParam(description = "账户ID") Long accId,
            @ToolParam(description = "订单类型，为必填项。1 = 限价单；2 = 市价单；5 = 绝对限价订单；6 = 竞价订单；7 = 竞价限价订单；8 = 特别限价订单；9 = 特别限价且要求全部成交订单；10 = 止损市价单；11 = 止损限价单；12 = 触及市价单；13 = 触及限价单；14 = 跟踪止损市价单；15 = 跟踪止损限价单；16 = 时间加权市价算法单；17 = 时间加权限价算法单；18 = 成交量加权市价算法单；19 = 成交量加权限价算法单")
            String futuOrderType,
            @ToolParam(description = "代码，为必填项。港股必须是5位数字，A股必须是6位数字，美股没限制，用于唯一标识交易的证券")
            String code,
            @ToolParam(description = "价格，为必填项。证券账户精确到小数点后3位，期货账户精确到小数点后9位，超出部分会被舍弃。如果是竞价、市价单，也请填入一个当前价格，以便服务器计算")
            Double price,
            @ToolParam(description = "订单号，为选填项。新下订单不需要，如果是修改订单就需要把原订单号带上，因为改单的最大买卖数量会包含原订单数量", required = false)
            String orderId,
            @ToolParam(description = "是否调整价格，为选填项。如果价格不合法，是否调整到合法价位，true调整，false不调整，对港、A股有意义，因为港股有价位，A股2位精度，美股可不传", required = false)
            boolean adjustPrice,
            @ToolParam(description = "调整方向和调整幅度百分比限制，为选填项。正数代表向上调整，负数代表向下调整，具体值代表调整幅度限制。如：0.015代表向上调整且幅度不超过1.5%；-0.01代表向下调整且幅度不超过1%", required = false)
            double adjustSideAndLimit,
            @ToolParam(description = "证券所属市场，为选填项。参见 TrdSecMarket 的枚举定义，用于指定证券所属的市场", required = false)
            int secMarket,
            @ToolParam(description = "服务器订单id，为选填项。可以用来代替orderID，和orderID二选一", required = false)
            String orderIDEx
    ) { }

    @Tool(description = "查询指定交易业务账户下的最大可买卖数量，亦可查询指定交易业务账户下指定订单的最大可改成的数量。")
    public ResultResponse<JSONObject> futuGetMaximumTradableQuantity(FutuMaximumTradableQuantityReq req, ToolContext toolContext) throws Exception {
        MaximumTradableQuantityReq maximumTradableQuantityReq = RequestUtils.requestHandle(req, MaximumTradableQuantityReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.getMaximumTradableQuantity(maximumTradableQuantityReq))));
    }

    public record FutuGetAllPositionReq(
            @ToolParam(description = "股票代码，多个使用逗号分割", required = false) String symbol,
            @ToolParam(description = "持仓id，多个使用逗号分割", required = false) Long positionId
    ) {}

    @Tool(description = "查询交易业务账号的持仓列表")
    public ResultResponse<JSONObject> futuGetAllPosition(FutuGetAllPositionReq req, ToolContext toolContext) throws Exception {
        PositionListQueryReq positionListQueryReq = RequestUtils.requestHandle(req, PositionListQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.getAllPosition(positionListQueryReq))));
    }

    public record FutuHistoryTransactionReq(
            @ToolParam(description = "股票代码", required = false) String symbol,
            @ToolParam(description = "起始时间，严格按照 yyyy-MM-dd HH:mm:ss", required = false) String startDate,
            @ToolParam(description = "起始时间，严格按照 yyyy-MM-dd HH:mm:ss", required = false) String endDate,
            @ToolParam(description = "富途证券：1-香港市场、2-美国市场、3-A股市场（仅模拟交易）、5-期货市场（环球期货）、113-香港基金市场、123-美国基金市场", required = false) Integer market,
            @ToolParam(description = "富途证券：成交记录 id，多个使用逗号分割", required = false) String idList
    ) {
    }

    @Tool(description = "查询指定交易业务账号历史成交列表")
    public ResultResponse<JSONObject>  futuGetHistoryTransaction(FutuHistoryTransactionReq req, ToolContext toolContext) throws Exception {
        HistoryTransactionReq historyTransactionReq = RequestUtils.requestHandle(req, HistoryTransactionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.getHistoryTransaction(historyTransactionReq))));
    }

    public record FutuTodayTransactionReq(
            @ToolParam(description = "股票代码", required = false) String symbol,
            @ToolParam(description = "富途证券：1-香港市场、2-美国市场、3-A股市场（仅模拟交易）、5-期货市场（环球期货）、113-香港基金市场、123-美国基金市场", required = false) Integer market,
            @ToolParam(description = "富途证券：成交记录 id，多个使用逗号分割", required = false) String idList
    ) {
    }

    @Tool(description = "查询指定交易业务账号当日成交列表")
    public ResultResponse<JSONObject>  futuGetTodayTransaction(FutuTodayTransactionReq req, ToolContext toolContext) throws Exception {
        TodayTransactionReq todayTransactionReq = RequestUtils.requestHandle(req, TodayTransactionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(futuChannel.getTodayTransaction(todayTransactionReq))));
    }

    public record FutuUnlockTradingReq(
            @ToolParam(description = "true 解锁交易，false 锁定交易") boolean unlock
    ) { }
    @Tool(description = "解锁交易")
    public Object unlockTrading(FutuUnlockTradingReq req) throws Exception {
        UnlockTradingReq unlockTradingReq = new UnlockTradingReq();
        BeanUtils.copyProperties(req, unlockTradingReq);
        return futuChannel.unlockTrading(unlockTradingReq);
    }

    public record FutuMarginTradingDataReq(
            @ToolParam(description = "账户ID") Long accId,
            @ToolParam(description = "市场") Integer market,
            @ToolParam(description = "代码") String code
    ) { }

    @Tool(description = "查询股票的融资融券数据")
    public Object futuGetMarginTradingData(FutuMarginTradingDataReq req) throws Exception {
        MarginTradingDataReq marginTradingDataReq = new MarginTradingDataReq();
        BeanUtils.copyProperties(req, marginTradingDataReq);
        return futuChannel.getMarginTradingData(marginTradingDataReq);
    }


}
