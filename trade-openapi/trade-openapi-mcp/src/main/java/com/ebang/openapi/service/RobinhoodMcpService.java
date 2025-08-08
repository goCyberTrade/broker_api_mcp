package com.ebang.openapi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ebang.openapi.channel.RobinhoodChannel;
import com.ebang.openapi.channel.WebullChannel;
import com.ebang.openapi.req.*;
import com.ebang.openapi.resp.ResultResponse;
import com.ebang.openapi.util.HttpClient;
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
public class RobinhoodMcpService {

    @Autowired
    private RobinhoodChannel robinhoodChannel;

    @Tool(description = "获取当前账户主体")
    public ResultResponse<JSONObject> robinhoodSignaturesAndOwners(ToolContext toolContext) {
        RequestUtils.requestHandle(toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(robinhoodChannel.signaturesAndOwners(new SignaturesOwnersReq()))));
    }

    public record RobinhoodInstrumentInfoReq(
            @ToolParam(description = "标的代码，为必填项，示例值如：SPX") String symbol
    ) { }

    @Tool(description = "查询标的基础信息")
    public ResultResponse<JSONObject> robinhoodGetMaximumTradableQuantity(RobinhoodInstrumentInfoReq req, ToolContext toolContext) throws Exception {
        InstrumentInfoReq instrumentInfoReq = RequestUtils.requestHandle(req, InstrumentInfoReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(robinhoodChannel.getInstrumentInfo(instrumentInfoReq))));
    }

    public record RobinhoodOrderInfoQueryReq(
        @ToolParam(description = "订单编号") String orderId,
        @ToolParam(description = "账号") String account,
        @ToolParam(description = "交易员秘钥(机构用户专用)") String secretKey,
        @ToolParam(description = "账户ID") String accountId,
        @ToolParam(description = "客户端订单ID") String clientOrderId,
        @ToolParam(description = "订单委托编号") String entrustId,
        @ToolParam(description = "订单流水号") String serialNo
    ){}

    @Tool(description = "获取订单信息")
    public ResultResponse<JSONObject> robinhoodGetOrderInfo(RobinhoodOrderInfoQueryReq req, ToolContext toolContext) throws Exception {
        OrderInfoQueryReq orderInfoQueryReq = RequestUtils.requestHandle(req, OrderInfoQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(robinhoodChannel.getOrderInfo(orderInfoQueryReq))));
    }

    public record RobinhoodOrderListQueryReq(
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
    public ResultResponse<JSONObject> robinhoodGetOrderList(RobinhoodOrderListQueryReq req, ToolContext toolContext) throws Exception {
        OrderListQueryReq orderListQueryReq = RequestUtils.requestHandle(req, OrderListQueryReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(robinhoodChannel.getOrderList(orderListQueryReq))));
    }

    public record RobinhoodCancelOrderReq(
        @ToolParam(description = "订单编号") String orderId
    ){}

    @Tool(description = "撤销订单")
    public ResultResponse<JSONObject> robinhoodCancelOrder(RobinhoodCancelOrderReq req, ToolContext toolContext) throws Exception {
        CancelOrderReq cancelOrderReq = RequestUtils.requestHandle(req, CancelOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(robinhoodChannel.cancelOrder(cancelOrderReq))));
    }

    public record RobinhoodCreateOrderReq(
        @ToolParam(description = "账户") String account,
        @ToolParam(description = "标的instrument url") String instrument,
        @ToolParam(description = "股票代码") String symbol,
        @ToolParam(description = "订单类型") String type,
        @ToolParam(description = "订单有效期") String timeInForce,
        @ToolParam(description = "触发类型") String trigger,
        @ToolParam(description = "价格") java.math.BigDecimal price,
        @ToolParam(description = "止损价") java.math.BigDecimal stopPrice,
        @ToolParam(description = "下单数量") java.math.BigDecimal quantity,
        @ToolParam(description = "买卖方向") String side,
        @ToolParam(description = "客户端订单ID") String clientId,
        @ToolParam(description = "是否盘前盘后交易") Boolean extendedHours,
        @ToolParam(description = "是否覆盖日内交易检查") Boolean overrideDayTradeChecks,
        @ToolParam(description = "是否覆盖DTBP检查") Boolean overrideDtbpChecks
    ){}

    @Tool(description = "创建订单")
    public ResultResponse<JSONObject> robinhoodCreateOrder(RobinhoodCreateOrderReq req, ToolContext toolContext) throws Exception {
        CreateOrderReq createOrderReq = RequestUtils.requestHandle(req, CreateOrderReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(robinhoodChannel.createOrder(createOrderReq))));
    }

    public record RobinhoodPasswordResetReq(
            @ToolParam(description = "用户名，必填，类型为 String，与电子邮件地址关联的用户名") String username,
            @ToolParam(description = "新密码，必填，类型为 String，要设置的新密码") String password
    ) { }

    @Tool(description = "重置密码")
    public ResultResponse<JSONObject> robinhoodPasswordReset(RobinhoodPasswordResetReq req, ToolContext toolContext) throws Exception {
        PasswordResetReq passwordResetReq = RequestUtils.requestHandle(req, PasswordResetReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(robinhoodChannel.passwordReset(passwordResetReq))));
    }

    public record RobinhoodPasswordResetRequestReq(
            @ToolParam(description = "邮箱，必填，类型为 String，注册时使用的邮箱地址（Address you registered with）") String email
    ) { }

    @Tool(description = "重置密码请求")
    public ResultResponse<JSONObject> robinhoodPasswordResetRequest(RobinhoodPasswordResetRequestReq req, ToolContext toolContext) throws Exception {
        PasswordResetRequestReq passwordResetRequestReq = RequestUtils.requestHandle(req, PasswordResetRequestReq.class, toolContext);
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(robinhoodChannel.passwordResetRequest(passwordResetRequestReq))));
    }

    @Tool(description = "获取标的列表，会返回一个分页列表，其中包含所有由 Robinhood 合作伙伴跟踪的金融工具，并按其列表日期排序")
    public ResultResponse<JSONObject> robinhoodGetContractList(ToolContext toolContext) throws Exception {
        RequestUtils.requestHandle(toolContext);
        ContractListReq contractListReq = new ContractListReq();
        return ResultResponse.success(JSON.parseObject(JSON.toJSONString(robinhoodChannel.getContractList(contractListReq))));
    }
}
