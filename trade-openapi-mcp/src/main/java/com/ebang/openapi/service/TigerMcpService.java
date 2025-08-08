package com.ebang.openapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.channel.TigerChannel;
import com.ebang.openapi.req.*;
import com.ebang.openapi.resp.ResultResponse;
import com.ebang.openapi.util.RequestUtils;
import com.tigerbrokers.stock.openapi.client.struct.TagValue;
import com.tigerbrokers.stock.openapi.client.struct.enums.Currency;
import com.tigerbrokers.stock.openapi.client.struct.enums.SegmentType;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
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
public class TigerMcpService {

    @Autowired
    private TigerChannel tigerChannel;

    public record TigerAccountSummaryReq(
            @ToolParam(description = "账户id(可选)，不传时使用系统默认账户",required = false) String accountId,
            @ToolParam(description = "是否返回按照品种（证券、期货）分类的数据，默认 false，为true时，返回一个dict，C表示期货， S表示股票") boolean segment,
            @ToolParam(description = "是否返回按照币种（美元、港币、人民币）分类的数据，默认为 false") boolean marketValue
    ) {
    }

    @Tool(description = "获取账户概览")
    public ResultResponse<JSONObject> tigerGetAccountSummary(TigerAccountSummaryReq req, ToolContext toolContext) {
        AccountSummaryReq accountSummaryReq = RequestUtils.requestHandle(req, AccountSummaryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(tigerChannel.getAccountSummary(accountSummaryReq))));
    }


    public record TigerAccountsReq(
            @ToolParam(description = "账户id,默认为空") String accountId
    ) {
    }

    @Tool(description = "获取账户列表")
    public ResultResponse<JSONArray> tigerAccounts(TigerAccountsReq req, ToolContext toolContext) {
        AccountsReq accountsReq = RequestUtils.requestHandle(req, AccountsReq.class, toolContext);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(tigerChannel.accounts(accountsReq))));
    }

    public record TigerAvailableFundsReq(
            @ToolParam(description = "账户id(可选)，不传时使用系统默认账户",required = false) String accountId,
            @ToolParam(description = "转出segment, 包含FUT/SEC,默认为SEC") String fromSegment,
            @ToolParam(description = "转出币种，包含USD/HKD,默认为USD") String currency
    ){}

    @Tool(description = "获取可用资金")
    public ResultResponse<JSONArray> tigerAvailableFunds(TigerAvailableFundsReq req, ToolContext toolContext) {
        AvailableFundsReq availableFundsReq = RequestUtils.requestHandle(req, AvailableFundsReq.class, toolContext);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(tigerChannel.availableFunds(availableFundsReq))));
    }

    public record TigerMaximumTradableQuantityReq(
            @ToolParam(description = "账户", required = false) String account,
            @ToolParam(description = "股票代码，为必填项") String symbol,
            @ToolParam(description = "到期日，交易品种是OPT/WAR/IOPT类型时必传，格式为yyyyMMdd", required = false) String expiry,
            @ToolParam(description = "CALL/PUT，交易品种是OPT/WAR/IOPT类型时必传", required = false) String right,
            @ToolParam(description = "行权价，交易品种是OPT/WAR/IOPT类型时必传", required = false) String strike,
            @ToolParam(description = "分段类型，暂只支持SEC", required = false) String seg_type,
            @ToolParam(description = "证券类型，STK:股票/FUT:期货/OPT:期权/WAR:窝轮/IOPT:牛熊证，期货暂不支持", required = false) String secType,
            @ToolParam(description = "交易方向，BUY/SELL，为必填项") String action,
            @ToolParam(description = "订单类型，为必填项, \"MKT\"=\"Market Order\";\"LMT\"=\"Limit Order\";\"STP\"=\"Stop Loss Order\";\"STP_LMT\"=\"Stop Limit Order\";\"TRAIL\"=\"Trailing Stop Order\";\"AM\"=\"Auction Market Order\";\"AL\"=\"Auction Limit Order\";\"TWAP\"=\"Time Weighted Average Price\";\"VWAP\"=\"Volume Weighted Average Price\"") String orderType,
            @ToolParam(description = "限价，当order_type为LMT,STP_LMT时该参数必需", required = false) Double limitPrice,
            @ToolParam(description = "止损价，当order_type为STP,STP_LMT时该参数必需", required = false) Double stopPrice,
            @ToolParam(description = "机构用户专用，交易员密钥", required = false) String secretKey
    ) {
    }

    @Tool(description = "查询账户下的对某个标的的最大可买卖数量，支持股票、期权，暂不支持期货")
    public ResultResponse<JSONObject> tigerGetMaximumTradableQuantity(TigerMaximumTradableQuantityReq req, ToolContext toolContext) throws Exception {
        MaximumTradableQuantityReq maximumTradableQuantityReq = RequestUtils.requestHandle(req, MaximumTradableQuantityReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(tigerChannel.getMaximumTradableQuantity(maximumTradableQuantityReq))));
    }

    public record TigerPositionListQueryReq(
            @ToolParam(description = "产品类型，stock: 股票、funds: 基金") String productType,
            @ToolParam(description = "股票代码或基金代码，多个用英文逗号分隔，不填则查询所有。长桥证券：股票代码，使用 ticker.region 格式，例如：AAPL.US；基金代码，使用 ISIN 格式，例如：HK0000676327") String symbol,
            @ToolParam(description = "老虎证券：账号 id") String accountId,
            @ToolParam(description = "老虎证券：市场分类，包括：ALL/US/HK/CN 默认 ALL") String market,
            @ToolParam(description = "华盛通证券：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通。盈立证券：交易类别(0-香港,5-美股, 67-A股，100-查询所有交易类别)") String exchangeType,
            @ToolParam(description = "每页大小") Integer pageSize,
            @ToolParam(description = "微牛证券：上一页最后一条记录的 instrumentId，首次请求时为空") String lastInstrumentId,
            @ToolParam(description = "IB：分页返回Position信息。从 0 开始索引。每页最多返回 100 个Position。默认值为 0") Integer pageId
    ) {
    }

    @Tool(description = "获取全部持仓")
    public ResultResponse<JSONObject> tigerGetAllPosition(TigerPositionListQueryReq req, ToolContext toolContext) throws Exception {
        PositionListQueryReq positionListQueryReq = RequestUtils.requestHandle(req, PositionListQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(tigerChannel.getAllPosition(positionListQueryReq))));
    }

    public record TigerOrderInfoQueryReq(
            @ToolParam(description = "订单编号") Long id,
            @ToolParam(description = "账号") String account,
            @ToolParam(description = "交易员秘钥(机构用户专用)", required = false) String secretKey
    ) {
    }

    @Tool(description = "获取订单信息" +
            "返回信息:订单信息，包含id(订单全局唯一ID，下单成功后返回)、orderId(用户本地的自增订单ID，非全局唯一)、accountorderType、outsideRth、filledQuantity、filledQuantityScale(成交数量偏移量，如 filledQuantity=11123， filledQuantityScale=2，那么实际 filledQuantity=11123*10^(-2)=111.23)、status、openTime、updateTime、symbol、currency、market、canModify、canCancel、isOpen、replaceStatus、cancelStatus、charges等信息")
    public ResultResponse<JSONObject> tigerGetOrderInfo(TigerOrderInfoQueryReq req, ToolContext toolContext) throws Exception {
        OrderInfoQueryReq orderInfoQueryReq = RequestUtils.requestHandle(req, OrderInfoQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(tigerChannel.getOrderInfo(orderInfoQueryReq))));
    }

    public record TigerOrderListQueryReq(
            @ToolParam(description = "账号") String account,
            @ToolParam(description = "账户划分类型,枚举值有:ALL、SEC、FUT、FUND", required = false) String segType,
            @ToolParam(description = "证券类型,枚举值有:ALL、STK、OPT、WAR、IOPT、CASH、FUT、FOP、FUND、MLEG、FOREX", required = false) String secType,
            @ToolParam(description = "市场,枚举值有:ALL、US、HK、CN", required = false) String market,
            @ToolParam(description = "标的代码", required = false) String symbol,
            @ToolParam(description = "过期日，格式：yyyy-MM-dd", required = false) String expiry,
            @ToolParam(description = "行权价格", required = false) BigDecimal strike,
            @ToolParam(description = "期权方向,枚举值有:PUT、CALL", required = false) String right,
            @ToolParam(description = "订单开始时间格式为yyyy-MM-dd 或者 yyyy-MM-dd HH:mm:ss。默认为东八区", required = false) String startDate,
            @ToolParam(description = "订单结束时间格式为yyyy-MM-dd 或者 yyyy-MM-dd HH:mm:ss。默认为东八区", required = false) String endDate,
            @ToolParam(description = "限制条数", required = false) Integer limit,
            @ToolParam(description = "订单状态列表（-2: 无效 -1: 初始 3: 待撤销 4: 已撤销 5: 已提交 6: 已成交 7: 未激活 8: 待提交）", required = false) List<Integer> states,
            @ToolParam(description = "交易员秘钥(机构用户专用)", required = false) String secretKey
    ) {
    }

    @Tool(description = "获取订单列表数据" +
            "返回信息:订单列表信息，包含id(订单全局唯一ID，下单成功后返回)、orderId(用户本地的自增订单ID，非全局唯一)、accountorderType、outsideRth、filledQuantity、filledQuantityScale(成交数量偏移量，如 filledQuantity=11123， filledQuantityScale=2，那么实际 filledQuantity=11123*10^(-2)=111.23)、status、openTime、updateTime、symbol、currency、market、canModify、canCancel、isOpen、replaceStatus、cancelStatus、charges等信息")
    public ResultResponse<JSONArray> tigerGetOrderList(TigerOrderListQueryReq req, ToolContext toolContext) throws Exception {
        OrderListQueryReq orderListQueryReq = RequestUtils.requestHandle(req, OrderListQueryReq.class, toolContext);
        return ResultResponse.success(JSONArray.parseArray(JSON.toJSONString(tigerChannel.getOrderList(orderListQueryReq))));
    }

    public record TigerModifyOrderReq(
            @ToolParam(description = "用户授权账户") String account,
            @ToolParam(description = "订单编号") Long id,
            @ToolParam(description = "改单数量(港股、沪港通、窝轮、牛熊证有最小数量限制)", required = false) Long totalQuantity,
            @ToolParam(description = "改单数量的偏移量，默认0。碎股单结合totalQuantity代表真实数量，如 totalQuantity=111、scale=2 → 真实 1.11", required = false) Integer totalQuantityScale,
            @ToolParam(description = "限价，orderType 为 LMT、STP_LMT 时必需", required = false) BigDecimal limitPrice,
            @ToolParam(description = "股票订单止损触发价，orderType 为 STP、STP_LMT 时必需；为 TRAIL 时是跟踪额；与 trailingPercent 互斥（后者优先）", required = false) BigDecimal auxPrice,
            @ToolParam(description = "跟踪止损单 - 止损百分比，orderType 为 TRAIL 时与 auxPrice 互斥（优先用该字段）", required = false) BigDecimal trailingPercent,
            @ToolParam(description = "机构用户专用 - 交易员密钥", required = false) String secretKey
    ) {
    }

    @Tool(description = "修改订单" +
            "返回信息:订单修改结果")
    public ResultResponse<JSONObject> tigerModifyOrder(TigerModifyOrderReq req, ToolContext toolContext) throws Exception {
        ModifyOrderReq modifyOrderReq = RequestUtils.requestHandle(req, ModifyOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(tigerChannel.modifyOrder(modifyOrderReq))));
    }

    public record TigerCancelOrderReq(
            @ToolParam(description = "订单编号") String orderId,
            @ToolParam(description = "账户ID") String accountId,
            @ToolParam(description = "机构用户专用 - 交易员密钥", required = false) String secretKey
    ) {
    }

    @Tool(description = "撤销订单" +
            "返回信息:订单撤销结果")
    public ResultResponse<JSONObject> tigerCancelOrder(TigerCancelOrderReq req, ToolContext toolContext) throws Exception {
        CancelOrderReq cancelOrderReq = RequestUtils.requestHandle(req, CancelOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(tigerChannel.cancelOrder(cancelOrderReq))));
    }

    public record TigerCreateOrderReq(
            @ToolParam(description = "用户授权账户") String account,
            @ToolParam(description = "股票代码 如：AAPL；（sec_typ为窝轮牛熊证时,在app窝轮/牛熊证列表中名称下面的5位数字）") String symbol,
            @ToolParam(description = "合约类型,STK 股票;OPT 美股期权; WAR 港股窝轮; IOPT 港股牛熊证; FUT 期货; FUND 基金") String secType,
            @ToolParam(description = "交易方向，枚举值有BUY、SELL") String action,
            @ToolParam(description = "订单类型，枚举值有 MKT（市价单）, LMT（限价单）, STP(止损单), STP_LMT(止损限价单), TRAIL(跟踪止损单)") String orderType,
            @ToolParam(description = "下单数量(港股、沪港通、窝轮、牛熊证有最小数量限制)") Long totalQuantity,
            @ToolParam(description = "下单数量的偏移量，默认0。碎股单结合totalQuantity代表真实数量，如 totalQuantity=111、scale=2 → 真实 1.11", required = false) Integer totalQuantityScale,
            @ToolParam(description = "订单金额(基金等金额订单场景用)", required = false) BigDecimal cashAmount,
            @ToolParam(description = "限价，orderType 为 LMT、STP_LMT 时必需", required = false) BigDecimal limitPrice,
            @ToolParam(description = "股票订单止损触发价，orderType 为 STP、STP_LMT 时必需；为 TRAIL 时是跟踪额；与 trailingPercent 互斥（后者优先）", required = false) BigDecimal auxPrice,
            @ToolParam(description = "跟踪止损单 - 止损百分比，orderType 为 TRAIL 时与 auxPrice 互斥（优先用该字段）", required = false) BigDecimal trailingPercent,
            @ToolParam(description = "是否允许盘前盘后交易(美股专属)，true 允许、false 不允许，默认允许；市价/止损/跟踪止损单忽略该参数", required = false) Boolean outsideRth,
            @ToolParam(description = "美股订单时段，枚举值有PRE_RTH_POST、OVERNIGHT、RTH、FULL", required = false) String tradingSessionType,
            @ToolParam(description = "价格微调幅度，默认0不调整；正数向上、负数向下，自动调价格到合法位（如 0.001 代表幅度≤0.1%）", required = false) BigDecimal adjustLimit,
            @ToolParam(description = "市场（美股 US 港股 HK 沪港通 CN）", required = false) String market,
            @ToolParam(description = "货币（具体需参考Currency枚举映射）", required = false) String currency,
            @ToolParam(description = "订单有效期，可选 DAY（当日）、GTC（取消前，最长180天）、GTD（指定时间前），默认 DAY", required = false) String timeInForce,
            @ToolParam(description = "订单有效截止时间（13位时间戳，精确到秒），timeInForce 为 GTD 时必填，否则不填", required = false) Long expireTime,
            @ToolParam(description = "交易所 (美股 SMART 港股 SEHK 沪港通 SEHKNTL 深港通 SEHKSZSE)", required = false) String exchange,
            @ToolParam(description = "过期日(期权、窝轮、牛熊证专属)", required = false) String expiry,
            @ToolParam(description = "底层价格(期权、窝轮、牛熊证专属)", required = false) String strike,
            @ToolParam(description = "期权方向 PUT/CALL(期权、窝轮、牛熊证专属)", required = false) String right,
            @ToolParam(description = "1手单位(期权、窝轮、牛熊证专属)", required = false) BigDecimal multiplier,
            @ToolParam(description = "本地标的 窝轮牛熊证必填，填app列表中名称下5位数字", required = false) String localSymbol,
            @ToolParam(description = "机构用户专用 - 交易员密钥", required = false) String secretKey,
            @ToolParam(description = "下单备注，下单后不可改，查询订单返回该信息", required = false) String userMark,

            // 附加订单参数
            @ToolParam(description = "附加订单类型，下附加单时必填（orderType 需为 LMT）：PROFIT-止盈、LOSS-止损、BRACKETS-括号单（含止盈+止损）", required = false) String attachType,
            @ToolParam(description = "止盈单价格，下止盈单时必填", required = false) Double profitTakerPrice,
            @ToolParam(description = "止盈单有效期，同 timeInForce，仅支持 DAY、GTC，下止盈单时必填", required = false) String profitTakerTif,
            @ToolParam(description = "止盈单是否允许盘前盘后，同 outsideRth 逻辑", required = false) Boolean profitTakerRth,
            @ToolParam(description = "止损单触发价，下止损单时必填", required = false) BigDecimal stopLossPrice,
            @ToolParam(description = "止损单执行限价（仅综合账号场景用），止损单的限价没有填写时，为附加止损市价单", required = false) BigDecimal stopLossLimitPrice,
            @ToolParam(description = "止损单有效期，同 timeInForce，仅支持 DAY、GTC，下止损单时必填", required = false) String stopLossTif,
            @ToolParam(description = "跟踪止损单 - 止损百分比，下跟踪止损单时与 stopLossTrailingAmount 二选一（填了都优先用该字段）", required = false) BigDecimal stopLossTrailingPercent,
            @ToolParam(description = "跟踪止损单 - 止损额，下跟踪止损单时与 stopLossTrailingPercent 二选一", required = false) BigDecimal stopLossTrailingAmount,
            @ToolParam(description = "TWAP/VWAP订单算法参数（包含start_time策略开始时间、end_time策略结束时间、participation_rate最大参与率(成交量为日军成交量的最大比例)）", required = false) List<TagValue> algoParams
    ) {
    }

//    public record TigerCreateOrderTagReq(
//            @ToolParam(description = "用户授权账户", required = false) String account,
//            @ToolParam(description = "股票代码 如：AAPL；（sec_typ为窝轮牛熊证时,在app窝轮/牛熊证列表中名称下面的5位数字）") String symbol,
//    ) {
//    }

    @Tool(description = "创建订单" +
            "返回信息:订单创建结果以及订单编号")
    public ResultResponse<JSONObject> tigerCreateOrder(TigerCreateOrderReq req, ToolContext toolContext) throws Exception {
        CreateOrderReq createOrderReq = RequestUtils.requestHandle(req, CreateOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(tigerChannel.createOrder(createOrderReq))));
    }

    public record TigerHistoryTransactionReq(
            @ToolParam(description = "IB: 账号 id，多个使用逗号分割") String accountId,
            @ToolParam(description = "IB: 合约 id，多个使用逗号分割") String contractId,
            @ToolParam(description = "币种") String currency,
            @ToolParam(description = "指定历史交易数据的天数") Integer days
    ) {
    }

    @Tool(description = "获取历史成交")
    public ResultResponse<JSONObject> tigerGetHistoryTransaction(TigerHistoryTransactionReq req, ToolContext toolContext) throws Exception {
        HistoryTransactionReq historyTransactionReq = RequestUtils.requestHandle(req, HistoryTransactionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(tigerChannel.getHistoryTransaction(historyTransactionReq))));
    }

    public record TigerAccountPerformanceReq(
            @ToolParam(description = "账号 id", required = false) String accountId,
            @ToolParam(description = "老虎证券：起始日期， 格式 yyyy-MM-dd, 如 '2022-01-01'。如不传则使用endDate往前30天的日期", required = false) String startDate,
            @ToolParam(description = "老虎证券：截止日期， 格式 yyyy-MM-dd, 如 '2022-02-01'。如不传则使用当前日期", required = false) String endDate,
            @ToolParam(description = "老虎证券：账户划分类型, 可选值有: SegmentType.SEC 代表证券; SegmentType.FUT 代表期货", required = false) String segType,
            @ToolParam(description = "老虎证券：币种，包括 ALL/USD/HKD/CNH 等", required = false) String currency
    ) {
       }

    @Tool(description = "获取综合历史资产分析")
    public ResultResponse<JSONObject> tigerGetAnalyticsAsset(TigerAccountPerformanceReq req, ToolContext toolContext) throws Exception {
        AccountPerformanceReq accountPerformanceReq = RequestUtils.requestHandle(req, AccountPerformanceReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(tigerChannel.getAccountPerformance(accountPerformanceReq))));
    }

    public record TigerGetContract(
            @ToolParam(description = "账号 id", required = false) String accountId,
            @ToolParam(description = "老虎证券：股票代码 如：00700 / AAPL，多个使用逗号分割", required = false) String symbol,
            @ToolParam(description = "老虎证券：STK/OPT/FUT", required = false) String secType,
            @ToolParam(description = "老虎证券：USD/HKD/CNH", required = false) String currency,
            @ToolParam(description = "老虎证券：到期日 交易品种是期权时必传 yyyyMMdd", required = false) String expiry,
            @ToolParam(description = "老虎证券：行权价 交易品种是期权时必传", required = false) Double strike,
            @ToolParam(description = "老虎证券：CALL/PUT 交易品种是期权时必传", required = false) String right,
            @ToolParam(description = "老虎证券：交易所 (美股 SMART 港股 SEHK 沪港通 SEHKNTL 深港通 SEHKSZSE)", required = false) String exchange
    ){}

    @Tool(description = "获取单个合约信息")
    public ResultResponse<JSONArray>  tigerGetContract(TigerGetContract req, ToolContext toolContext) throws Exception {
        ContractListReq contractListReq = RequestUtils.requestHandle(req, ContractListReq.class, toolContext);
        contractListReq.setQueryContract(1);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(tigerChannel.getContractList(contractListReq))));
    }

    public record TigerGetContractList(
            @ToolParam(description = "账号 id", required = false) String accountId,
            @ToolParam(description = "老虎证券：股票代码 如：00700 / AAPL，多个使用逗号分割", required = false) String symbol,
            @ToolParam(description = "老虎证券：STK/OPT/FUT", required = false) String secType,
            @ToolParam(description = "老虎证券：USD/HKD/CNH", required = false) String currency
    ){}

    @Tool(description = "获取多个合约信息")
    public ResultResponse<JSONArray>  tigerGetContractList(TigerGetContractList req, ToolContext toolContext) throws Exception {
        ContractListReq contractListReq = RequestUtils.requestHandle(req, ContractListReq.class, toolContext);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(tigerChannel.getContractList(contractListReq))));
    }

    public record TigerGetQuoteContractList(
            @ToolParam(description = "老虎证券：股票代码 如：00700 / AAPL，多个使用逗号分割", required = false) String symbol,
            @ToolParam(description = "老虎证券：STK/OPT/FUT", required = false) String secType,
            @ToolParam(description = "老虎证券：到期日 交易品种是期权时必传 yyyyMMdd", required = false) String expiry,
            @ToolParam(description = "老虎证券：行权价 交易品种是期权时必传", required = false) Double strike,
            @ToolParam(description = "语言支持: zh_CN,zh_TW,en_US, 默认: en_US", required = false)  String lang
    ){}

    @Tool(description = "获取期权/窝轮/牛熊证合约列表")
    public ResultResponse<JSONArray>  tigerGetQuoteContractList(TigerGetQuoteContractList req, ToolContext toolContext) throws Exception {
        ContractListReq contractListReq = RequestUtils.requestHandle(req, ContractListReq.class, toolContext);
        contractListReq.setQueryContract(2);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(tigerChannel.getContractList(contractListReq))));
    }


}
