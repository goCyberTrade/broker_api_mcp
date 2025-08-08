package com.ebang.openapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.channel.TigerChannel;
import com.ebang.openapi.channel.USmartChannel;
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

/**
 * @author xiaobo
 * @description
 * @date 2025/7/11 9:28
 */
@Service
public class USmartMcpService {

    @Autowired
    private USmartChannel uSmartChannel;

    public record USmartAccountSummaryReq(
            @ToolParam(description = "交易类别,包含：0-港股/5-美股/67-A股,默认0") String exchangeType
    ) {
    }

    @Tool(description = "获取账户概览")
    public ResultResponse<JSONObject> usmartGetAccountSummary(USmartAccountSummaryReq req, ToolContext toolContext) {
        AccountSummaryReq accountSummaryReq = RequestUtils.requestHandle(req, AccountSummaryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.getAccountSummary(accountSummaryReq))));
    }
    public record USmartBalanceReq(
            @ToolParam(description = "交易类别,包含：0-港股/5-美股/67-A股,默认0") int exchangeType
    ){}

    @Tool(description = "获取账户余额")
    public ResultResponse<JSONObject> usmartBalances(USmartBalanceReq req, ToolContext toolContext) {
        BalanceReq balanceReq = RequestUtils.requestHandle(req, BalanceReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.balances(balanceReq))));
    }

    public record USmartMarginsReq(
            @ToolParam(description = "交易类别,包含：0-港股/5-美股/67-A股,默认0") int exchangeType
    ){}

    @Tool(description = "获取账户保证金")
    public ResultResponse<JSONObject> usmartMargins(USmartMarginsReq req, ToolContext toolContext) {
        MarginsReq marginsReq = RequestUtils.requestHandle(req, MarginsReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.margins(marginsReq))));
    }
    public record USmartMaximumTradableQuantityReq(
            @ToolParam(description = "委托价格，非必填（竞价单可不填），但不能为 0，类型为数字，用于设置股票交易的委托价格", required = false) Integer entrustPrice,
            @ToolParam(description = "委托属性，必填，类型为字符串。可选值：'0'-美股限价单, 'd'-竞价单, 'e'-增强限价单, 'g'-竞价限价单, 'u'-碎股单，用于指定委托的具体属性类型") String entrustProp,
            @ToolParam(description = "交易类别，必填，类型为 int32。可选值：0-香港,5-美股,6-沪港通,7-深港通，用于标识股票交易所属的市场类别") Integer exchangeType,
            @ToolParam(description = "证券代码，必填，类型为字符串，用于唯一标识要交易的证券") String stockCode
    ) { }

    @Tool(description = "获取最大可用数量")
    public ResultResponse<JSONObject> usmartGetMaximumTradableQuantity(USmartMaximumTradableQuantityReq req, ToolContext toolContext) throws Exception {
        MaximumTradableQuantityReq maximumTradableQuantityReq = RequestUtils.requestHandle(req, MaximumTradableQuantityReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.getMaximumTradableQuantity(maximumTradableQuantityReq))));
    }

    @Tool(description = "获取全部持仓")
    public ResultResponse<JSONArray> usmartGetAllPosition(@ToolParam(description = "交易类别(0-香港,5-美股, 67-A股，100-查询所有交易类别)") String exchangeType,
                                                          ToolContext toolContext) throws Exception {
        RequestUtils.requestHandle(toolContext);
        PositionListQueryReq positionListQueryReq = new PositionListQueryReq();
        positionListQueryReq.setExchangeType(exchangeType);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(uSmartChannel.getAllPosition(positionListQueryReq))));
    }

    public record USmartOrderInfoQueryReq(
        @ToolParam(description = "订单编号") String orderId,
        @ToolParam(description = "账号") String account,
        @ToolParam(description = "交易员秘钥(机构用户专用)") String secretKey,
        @ToolParam(description = "账户ID") String accountId,
        @ToolParam(description = "客户端订单ID") String clientOrderId,
        @ToolParam(description = "订单委托编号") String entrustId,
        @ToolParam(description = "订单流水号") String serialNo
    ){}

    @Tool(description = "获取订单信息")
    public ResultResponse<JSONObject> usmartGetOrderInfo(USmartOrderInfoQueryReq req, ToolContext toolContext) throws Exception {
        OrderInfoQueryReq orderInfoQueryReq = RequestUtils.requestHandle(req, OrderInfoQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.getOrderInfo(orderInfoQueryReq))));
    }

    public record USmartOrderListQueryReq(
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
    public ResultResponse<JSONObject> usmartGetOrderList(USmartOrderListQueryReq req, ToolContext toolContext) throws Exception {
        OrderListQueryReq orderListQueryReq = RequestUtils.requestHandle(req, OrderListQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.getOrderList(orderListQueryReq))));
    }

    public record USmartModifyOrderReq(
        @ToolParam(description = "原始委托编号") String entrustId,
        @ToolParam(description = "委托数量") java.math.BigDecimal entrustAmount,
        @ToolParam(description = "委托价格，<如果为条件单，该值可为空>") java.math.BigDecimal entrustPrice,
        @ToolParam(description = "交易密码（RSA公钥加密）") String password,
        @ToolParam(description = "强制委托标志") Boolean forceEntrustFlag
    ){}

    @Tool(description = "修改订单")
    public ResultResponse<JSONObject> usmartModifyOrder(USmartModifyOrderReq req, ToolContext toolContext) throws Exception {
        ModifyOrderReq modifyOrderReq = RequestUtils.requestHandle(req, ModifyOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.modifyOrder(modifyOrderReq))));
    }

    public record USmartCancelOrderReq(
        @ToolParam(description = "原始委托编号") String entrustId,
        @ToolParam(description = "交易密码（RSA公钥加密）") String password
    ){}

    @Tool(description = "撤销订单")
    public ResultResponse<JSONObject> usmartCancelOrder(USmartCancelOrderReq req, ToolContext toolContext) throws Exception {
        CancelOrderReq cancelOrderReq = RequestUtils.requestHandle(req, CancelOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.cancelOrder(cancelOrderReq))));
    }

    public record USmartCreateOrderReq(
        @ToolParam(description = "证券代码") String symbol,
        @ToolParam(description = "委托数量") java.math.BigDecimal entrustAmount,
        @ToolParam(description = "委托价格") java.math.BigDecimal entrustPrice,
        @ToolParam(description = "委托属性") String entrustProp,
        @ToolParam(description = "委托类型") String entrustType,
        @ToolParam(description = "交易类别") Integer exchangeType,
        @ToolParam(description = "交易密码（RSA公钥加密）") String password,
        @ToolParam(description = "股票名称") String stockName,
        @ToolParam(description = "强制委托标志") Boolean forceEntrustFlag,
        @ToolParam(description = "盘前盘后交易类型") String sessionType,
        @ToolParam(description = "订单有效期类型") String timeInForce,
        @ToolParam(description = "有效日期") String validDate,
        @ToolParam(description = "交易所") String exchange
    ){}

    @Tool(description = "创建订单")
    public ResultResponse<JSONObject> usmartCreateOrder(USmartCreateOrderReq req, ToolContext toolContext) throws Exception {
        CreateOrderReq createOrderReq = RequestUtils.requestHandle(req, CreateOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.createOrder(createOrderReq))));
    }

    public record USmartHistoryTransactionReq(
        @ToolParam(description = "交易类别， 交易类别(0-香港,5-美股, 67-A股，100-查询所有交易类别)") String exchangeType,
        @ToolParam(description = "证券代码", required = false) String symbol,
        @ToolParam(description = "起始时间", required = false) String startDate,
        @ToolParam(description = "结束时间", required = false) String endDate,
        @ToolParam(description = "当前页 1开始，默认值1", required = false) Integer pageNum,
        @ToolParam(description = "每页结果数，默认值10", required = false) Integer limit
    ){}

    @Tool(description = "获取历史成交")
    public ResultResponse<JSONObject> usmartGetHistoryTransaction(USmartHistoryTransactionReq req, ToolContext toolContext) throws Exception {
        HistoryTransactionReq historyTransactionReq = RequestUtils.requestHandle(req, HistoryTransactionReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(uSmartChannel.getHistoryTransaction(historyTransactionReq))));
    }

    @Tool(description = "解锁交易")
    public Object unlockTrading() throws Exception {
        return uSmartChannel.unlockTrading(null);
    }

    public record USmartUpdateOrResetTradePasswordReq(
            @ToolParam(description = "交易密码，必填")
            String password,
            @ToolParam(description = "旧交易密码，非必填", required = false)
            String oldPassword,
            @ToolParam(description = "手机验证码，非必填，根据验证码重置交易密码时必填", required = false)
            String phoneCaptcha
    ) { }

    @Tool(description = "修改交易密码")
    public Object usmartUpdateTradePassword(USmartUpdateOrResetTradePasswordReq req) throws Exception {
        UpdateOrResetTradePasswordReq updateOrResetTradePasswordReq = new UpdateOrResetTradePasswordReq();
        BeanUtils.copyProperties(req, updateOrResetTradePasswordReq);
        return uSmartChannel.updateTradePassword(updateOrResetTradePasswordReq);
    }

    @Tool(description = "重置交易密码")
    public Object usmartResetTradePassword(USmartUpdateOrResetTradePasswordReq req) throws Exception {
        UpdateOrResetTradePasswordReq updateOrResetTradePasswordReq = new UpdateOrResetTradePasswordReq();
        BeanUtils.copyProperties(req, updateOrResetTradePasswordReq);
        return uSmartChannel.resetTradePassword(updateOrResetTradePasswordReq);
    }

    public record USmartMortgageListReq(
            @ToolParam(description = "市场类型，非必填，类型为 int32。可选值：0-港股, 5-美股, 67-A股, 100-全部，用于指定要查询证券所属的市场范围", required = false)
            Integer exchangeType,
            @ToolParam(description = "证券代码，非必填，类型为 string，用于精确查询某一证券的信息", required = false)
            String stockCode,
            @ToolParam(description = "状态，非必填，类型为 int32。可选值：1-生效中, 0-已下架，默认值为 1，用于筛选证券的状态", required = false)
            Integer status
    ) { }

    @Tool(description = "获取股票抵押比率列表")
    public Object usmartMortgageList(USmartMortgageListReq req) throws Exception {
        MortgageListReq mortgageListReq = new MortgageListReq();
        BeanUtils.copyProperties(req, mortgageListReq);
        return uSmartChannel.mortgageList(mortgageListReq);
    }

    @Tool(description = "查询汇率")
    public Object USmartGetRate() throws Exception {
        return uSmartChannel.getRate(null);
    }

    public record USmartIpoInfoReq(
            @ToolParam(description = "市场类型，非必填，类型为 int32。取值：0-HK（港股）,5-US（美股）。规则：如果 ipold 不传，该字段必传，用于指定股票所属市场", required = false)
            Integer exchangeType,
            @ToolParam(description = "新股 ID，非必填，类型为 int64。规则：与 (stockCode & exchangeType) 不能同时为空；当有值时，优先按此查询，stockCode & exchangeType 条件不生效，用于通过新股 ID 精确查询", required = false)
            Long ipold,
            @ToolParam(description = "股票代码，非必填，类型为 string。规则：如果 ipold 不传，该字段必传，用于指定要查询的股票代码", required = false)
            String stockCode
    ) { }

    @Tool(description = "获取新股详细信息")
    public Object usmartIpoInfo(USmartIpoInfoReq req) throws Exception {
        IpoInfoReq ipoInfoReq = new IpoInfoReq();
        BeanUtils.copyProperties(req, ipoInfoReq);
        return uSmartChannel.ipoInfo(ipoInfoReq);
    }

    public record USmartApplyIpoReq(
            @ToolParam(description = "认购数量，必填，类型为 number，用于指定新股认购的数量")
            BigDecimal applyQuantity,
            @ToolParam(description = "认购类型，必填，类型为 int32。可选值：1-现金，2-融资，用于标识新股认购的资金方式")
            Integer applyType,
            @ToolParam(description = "IPO 交易系统唯一编号，必填，类型为 int64，用于关联具体的新股 IPO 交易")
            Long ipold,
            @ToolParam(description = "流水号，必填，类型为 int64。最长19位，需确保唯一，推荐使用雪花算法生成，用于标识本次认购操作的唯一性")
            Long serialNo,
            @ToolParam(description = "认购现金，非必填，类型为 number。规则：融资认购时必填，用于指定融资认购方式下的现金部分金额", required = false)
            BigDecimal cash
    ) { }

    @Tool(description = "新股认购")
    public Object usmartApplyIpo(USmartApplyIpoReq req) throws Exception {
        ApplyIpoReq applyIpoReq = new ApplyIpoReq();
        BeanUtils.copyProperties(req, applyIpoReq);
        return uSmartChannel.applyIpo(applyIpoReq);
    }

    public record USmartModifyIpoReq(
            @ToolParam(description = "操作类型，必填，类型为 int32。可选值：0-改单，1-撤单，标识当前操作是改单还是撤单")
            Integer actionType,
            @ToolParam(description = "认购记录 ID，必填，类型为 int64，关联要操作的新股认购记录")
            Long applyId,
            @ToolParam(description = "认购数量，必填，类型为 number，改单或撤单涉及的认购数量")
            BigDecimal applyQuantity,
            @ToolParam(description = "认购现金，非必填，类型为 number。规则：改融资认购单时必填，改单场景下（融资认购改单）需填写的现金金额", required = false)
            BigDecimal cash
    ) { }

    @Tool(description = "获取新股详细信息")
    public Object usmartIpoInfo(USmartModifyIpoReq req) throws Exception {
        ModifyIpoReq modifyIpoReq = new ModifyIpoReq();
        BeanUtils.copyProperties(req, modifyIpoReq);
        return uSmartChannel.modifyIpo(modifyIpoReq);
    }

    public record USmartIpoRecordListReq(
            @ToolParam(description = "认购开始时间，非必填，类型为 string。格式要求：yyyy-MM-dd HH:mm:ss ，用于筛选认购开始时间大于等于该值的记录", required = false)
            String applyTimeMin,
            @ToolParam(description = "认购结束时间，非必填，类型为 string。格式要求：yyyy-MM-dd HH:mm:ss ，用于筛选认购结束时间小于等于该值的记录", required = false)
            String applyTimeMax
    ) { }

    @Tool(description = "获取新股详细信息")
    public Object usmartIpoInfo(USmartIpoRecordListReq req) throws Exception {
        IpoRecordListReq ipoRecordListReq = new IpoRecordListReq();
        BeanUtils.copyProperties(req, ipoRecordListReq);
        return uSmartChannel.ipoRecordList(ipoRecordListReq);
    }

    public record USmartIpoRecordReq(
            @ToolParam(description = "申购编号，非必填，。规则：与 serialNo 传其中一个即可，用于通过申购编号查询", required = false)
            Long applyId,
            @ToolParam(description = "流水号，非必填，。规则：与 applyId 传其中一个即可，用于通过流水号查询", required = false)
            Long serialNo
    ) { }

    @Tool(description = "获取新股详细信息")
    public Object usmartIpoInfo(USmartIpoRecordReq req) throws Exception {
        IpoRecordReq ipoRecordReq = new IpoRecordReq();
        BeanUtils.copyProperties(req, ipoRecordReq);
        return uSmartChannel.ipoRecord(ipoRecordReq);
    }

    @Tool(description = "客户股票资产查询批量")
    public ResultResponse<JSONArray> usmartGetAccountPerformance(@ToolParam(description = "交易类别(0-香港,5-美股, 67-A股，100-查询所有交易类别)") String exchangeType,
                                                                 ToolContext toolContext) throws Exception {
        RequestUtils.requestHandle(toolContext);
        AccountPerformanceReq accountPerformanceReq = new AccountPerformanceReq();
        accountPerformanceReq.setExchangeType(exchangeType);
        return ResultResponse.success(JSON.parseArray(JSON.toJSONString(uSmartChannel.getAccountPerformance(accountPerformanceReq))));
    }



}
