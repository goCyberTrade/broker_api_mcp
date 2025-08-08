package com.ebang.openapi.channel;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ebang.openapi.config.FutuBase;
import com.ebang.openapi.config.TradeConfig;
import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import com.ebang.openapi.req.*;
import com.ebang.openapi.util.DataUtils;
import com.ebang.openapi.util.MD5Util;
import com.futu.openapi.pb.*;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 富途牛牛证券
 *
 * @author chenlanqing
 */
@Slf4j
@Component("futu")
public class FutuChannel extends BaseChannel {

    @Autowired
    private FutuBase futuBase;
    @Autowired
    private TradeConfig tradeConfig;
    /**
     * 获取环境
     */
    private TrdCommon.TrdEnv getTrdEnv() {
        Map<String, String> routeConfig = getRouteConfig();
        String trdEnv = routeConfig.get("trdEnv");
        if (StringUtils.isEmpty(trdEnv)) {
            return TrdCommon.TrdEnv.forNumber(tradeConfig.getFutuDefaultEnv());
        }
        return TrdCommon.TrdEnv.forNumber(Integer.valueOf(routeConfig.get("trdEnv")));
    }

    private TrdCommon.TrdFilterConditions getTrdFilterConditions(TrdFilterConditions item) {

        if (item == null) {
            return null;
        }
        TrdCommon.TrdFilterConditions.Builder builder = TrdCommon.TrdFilterConditions.newBuilder();
        if (item.getCodeList() != null && !item.getCodeList().isEmpty()) {
            builder.addAllCodeList(item.getCodeList());
        }
        if (item.getIdList() != null && !item.getIdList().isEmpty()) {
            builder.addAllIdList(item.getIdList());
        }
        if (StringUtils.isNotEmpty(item.getBeginTime())) {
            builder.setBeginTime(item.getBeginTime());
        }
        else {
            builder.setBeginTime("2000-01-01 00:00:00");
        }
        if (StringUtils.isNotEmpty(item.getEndTime())) {
            builder.setEndTime(item.getEndTime());
        }
        else {
            builder.setEndTime("9999-12-31 23:59:59");
        }
        if (item.getOrderIDExList() != null && !item.getOrderIDExList().isEmpty()) {
            builder.addAllOrderIDExList(item.getOrderIDExList());
        }
        if (item.getFilterMarket() != null) {
            builder.setFilterMarket(item.getFilterMarket());
        }
        return builder.build();
    }

    @Override
    public Object getOrderInfo(OrderInfoQueryReq req) throws Exception {

        OrderListQueryReq listReq = new OrderListQueryReq();
        TrdFilterConditions trdFilterConditions = new TrdFilterConditions();
        trdFilterConditions.setOrderIDExList(List.of(req.getOrderId()));
        listReq.setTrdFilterConditions(trdFilterConditions);
        listReq.setAccId(req.getAccId());
        listReq.setTrdMarket(req.getTrdMarket());
        return getOrderList(listReq);
    }

    @Override
    public Object getOrderList(OrderListQueryReq req) throws Exception {

        List<TrdCommon.OrderStatus> statusList = new ArrayList<>();
        if (req.getStatusList() != null) {
            req.getStatusList().forEach(item -> statusList.add(TrdCommon.OrderStatus.forNumber(item)));

        }
        TrdGetHistoryOrderList.Response getHisOrderListRsp = futuBase.getHistoryOrderListSync(req.getAccId(), TrdCommon.TrdMarket.forNumber(req.getTrdMarket()),
                getTrdEnv(), getTrdFilterConditions(req.getTrdFilterConditions()),
                statusList);

        //校验返回
        return handleResponse(getHisOrderListRsp);
    }

    @Override
    public Object modifyOrder(ModifyOrderReq req) throws Exception {

        Map<String, String> routeConfig = getRouteConfig();

        // 查询订单信息
        OrderInfoQueryReq orderInfoQueryReq = new OrderInfoQueryReq();
        orderInfoQueryReq.setOrderId(req.getOrderId());
        Object orderInfo = getOrderInfo(orderInfoQueryReq);
        if (orderInfo == null) {
            throw new RuntimeException("查询不到订单");
        }
        JSONObject jsonObject = (JSONObject) orderInfo;
        TrdGetHistoryOrderList.Response trdGetHistoryOrderList = jsonObject.toJavaObject(TrdGetHistoryOrderList.Response.class);
        if (trdGetHistoryOrderList.getErrCode() != 0 || CollectionUtils.isEmpty(trdGetHistoryOrderList.getS2C().getOrderListList())) {
            throw new RuntimeException("订单不存在");
        }
        TrdCommon.Order order = trdGetHistoryOrderList.getS2C().getOrderListList().get(0);

        TrdCommon.TrdHeader trdHeader = futuBase.makeTrdHeader(getTrdEnv(), req.getAccId(), TrdCommon.TrdMarket.forNumber(req.getTrdMarket()));
        TrdModifyOrder.C2S.Builder c2s = TrdModifyOrder.C2S.newBuilder()
                .setHeader(trdHeader)
                .setOrderID(Long.valueOf(req.getOrderId()))
                .setModifyOrderOp(TrdCommon.ModifyOrderOp.ModifyOrderOp_Normal_VALUE)
                .setForAll(false)
                .setQty(NumberUtils.toDouble(req.getQty(), order.getQty()))
                .setPrice(NumberUtils.toDouble(req.getPrice(), order.getPrice()))
                .setAdjustPrice(BooleanUtils.toBooleanDefaultIfNull(req.getAdjustPrice(), false))
                .setAdjustSideAndLimit(NumberUtils.toDouble(req.getAdjustSideAndLimit(), 0.0))
                .setAuxPrice(NumberUtils.toDouble(req.getAuxPrice(), order.getAuxPrice()))
                .setTrailType(DataUtils.toInt(req.getTrailType(), order.getTrailType()))
                .setTrailValue(NumberUtils.toDouble(req.getTrailValue(), order.getTrailValue()))
                .setTrailSpread(NumberUtils.toDouble(req.getTrailSpread(), order.getTrailSpread()));
        TrdModifyOrder.Response modifyOrder = futuBase.modifyOrderSync(c2s);
        //校验返回
        return handleResponse(modifyOrder);
    }

    public Object cancelOrder(CancelOrderReq req) throws Exception {

        Map<String, String> routeConfig = getRouteConfig();

        TrdCommon.TrdHeader trdHeader = futuBase.makeTrdHeader(getTrdEnv(), req.getAccId(), TrdCommon.TrdMarket.forNumber(req.getTrdMarket()));
        TrdModifyOrder.C2S.Builder c2s = TrdModifyOrder.C2S.newBuilder()
                .setHeader(trdHeader)
                .setModifyOrderOp(TrdCommon.ModifyOrderOp.ModifyOrderOp_Cancel_VALUE)
                .setOrderID(Long.valueOf(req.getOrderId()))
                .setForAll(BooleanUtils.toBooleanDefaultIfNull(req.getForAll(), false));
        if (StringUtils.isNotEmpty(req.getOrderId())) {
            c2s.setOrderID(Long.valueOf(req.getOrderId()));
        }
        if (StringUtils.isNotEmpty(req.getOrderIDEx())) {
            c2s.setOrderIDEx(req.getOrderIDEx());
        }
        TrdModifyOrder.Response cancelOrder = futuBase.modifyOrderSync(c2s);
        //校验返回
        return handleResponse(cancelOrder);
    }

    @Override
    public Object createOrder(CreateOrderReq req) throws Exception {

        Map<String, String> routeConfig = getRouteConfig();

        TrdCommon.TrdHeader trdHeader = futuBase.makeTrdHeader(getTrdEnv(), req.getAccId(), TrdCommon.TrdMarket.forNumber(req.getTrdMarket()));
        TrdPlaceOrder.C2S.Builder c2s = TrdPlaceOrder.C2S.newBuilder()
                .setHeader(trdHeader)
                .setTrdSide(req.getTrdSide())
                .setOrderType(Integer.valueOf(req.getOrderType()))
                .setCode(req.getCode())
                .setQty(NumberUtils.toDouble(req.getQty(), 0.0))
                .setPrice(NumberUtils.toDouble(req.getPrice(), 0.0))
                .setAdjustPrice(BooleanUtils.toBooleanDefaultIfNull(req.getAdjustPrice(), false))
                .setAdjustSideAndLimit(NumberUtils.toDouble(req.getAdjustSideAndLimit(), 0.0))
                .setSecMarket(DataUtils.toInt(req.getSecMarket(), 0))
                .setTimeInForce(NumberUtils.toInt(req.getTimeInForce(), 0))
                .setFillOutsideRTH(BooleanUtils.toBooleanDefaultIfNull(req.getFillOutsideRTH(), false))
                .setAuxPrice(NumberUtils.toDouble(req.getAuxPrice(), 0.0))
                .setTrailType(DataUtils.toInt(req.getTrailType(), 0))
                .setTrailValue(NumberUtils.toDouble(req.getTrailValue(), 0.0))
                .setTrailSpread(NumberUtils.toDouble(req.getTrailSpread(), 0.0))
                .setSession(DataUtils.toInt(req.getSession(), 0));
        if (StringUtils.isNotEmpty(req.getRemark())) {
            c2s.setRemark(req.getRemark());
        }
        TrdPlaceOrder.Response trdPlaceOrder = futuBase.placeOrderSync(c2s);
        //校验返回
        return handleResponse(trdPlaceOrder);
    }

    @Override
    public Object orderFees(OrderFeesReq req) throws Exception {

        Map<String, String> routeConfig = getRouteConfig();

        TrdCommon.TrdHeader trdHeader = futuBase.makeTrdHeader(getTrdEnv(), req.getAccId(), TrdCommon.TrdMarket.forNumber(req.getTrdMarket()));
        TrdGetOrderFee.C2S.Builder c2s = TrdGetOrderFee.C2S.newBuilder()
                .setHeader(trdHeader)
                .addOrderIdExList(req.getOrderId());
        TrdGetOrderFee.Response orderFee = futuBase.getOrderFee(c2s);
        //校验返回
        return handleResponse(orderFee);
    }

    @Override
    public Object availableFunds(AvailableFundsReq req) throws Exception {

        if (StringUtils.isEmpty(req.getAccountId())) {
            return null;
        }

        TrdGetFunds.Response response = futuBase.getFundsSync(Long.valueOf(req.getAccountId()), TrdCommon.TrdMarket.forNumber(req.getTrdMarket()), getTrdEnv(), false, TrdCommon.Currency.Currency_HKD);
        //校验返回
        return handleResponse(response);
    }

    @Override
    public Object accounts(AccountsReq req) throws Exception {

        TrdGetAccList.Response response = futuBase.getAccListSync();
        //校验返回
        return handleResponse(response);
    }

    /**
     * 解锁交易
     */
    @Override
    public Object unlockTrading(UnlockTradingReq req) throws Exception {
        Map<String, String> routeConfig = getRouteConfig();
        TrdUnlockTrade.Response response = futuBase.unlockTradeSync(routeConfig.get("tradePassword"), req.isUnlock());
        return handleResponseExt(response, 0, "");

    }

    /**
     * 获取最大可交易数量
     */
    @Override
    public Object getMaximumTradableQuantity(MaximumTradableQuantityReq req) throws Exception {
        TrdGetMaxTrdQtys.Response response = futuBase.getMaxTrdQtys(req.getAccId(), TrdCommon.TrdMarket.forNumber(req.getSecMarket()),
                getTrdEnv(), req);
        return handleResponseExt(response, 0, "maxTrdQtys");
    }

    /**
     * 查询股票融资融券信息
     */
    @Override
    public Object getMarginTradingData(MarginTradingDataReq req) throws Exception {
        TrdGetMaxTrdQtys.Response response = futuBase.getMarginRatio(req.getAccId(), TrdCommon.TrdMarket.forNumber(req.getMarket()),
                getTrdEnv(), req);
        return handleResponseExt(response, 1, "marginRatioInfoList");
    }

    /**
     * 获取资金流水
     */
    @Override
    public Object getCashFlowSummary(CashFlowSummaryReq req) throws Exception {
        TrdFlowSummary.Response response = futuBase.getFlowSummary(req.getAccId(), TrdCommon.TrdMarket.forNumber(req.getMarket()),
                getTrdEnv(), req);
        return handleResponseExt(response, 0, "flowSummaryInfoList");
    }

    @Override
    public Object getAllPosition(PositionListQueryReq req) throws Exception {
        Map<String, String> routeConfig = getRouteConfig();
        TrdGetPositionList.Response response = futuBase.getPositionList(Long.parseLong(routeConfig.get("trdAcc")),
                TrdCommon.TrdMarket.forNumber(req.getTrdMarket()),
                getTrdEnv(),
                req
        );
        //校验返回
        return handleResponse(response);
    }

    @Override
    public Object getHistoryTransaction(HistoryTransactionReq request) throws Exception {
        Map<String, String> routeConfig = getRouteConfig();

        TrdCommon.TrdFilterConditions conditions = futuBase.buildCondition(
                request.getSymbol(),
                request.getIdList(),
                request.getMarket(),
                request.getStartDate(),
                request.getEndDate()
        );

        TrdGetHistoryOrderFillList.Response response = futuBase.getHistoryOrderFillListSync(Long.parseLong(routeConfig.get("trdAcc")),
                TrdCommon.TrdMarket.forNumber(request.getMarket()),
                getTrdEnv(),
                conditions
        );
        //校验返回
        return handleResponse(response);
    }

    @Override
    public Object getTodayTransaction(TodayTransactionReq request) throws Exception {
        Map<String, String> routeConfig = getRouteConfig();

        TrdCommon.TrdFilterConditions conditions = futuBase.buildCondition(
                request.getSymbol(),
                request.getIdList(),
                request.getMarket(),
                null,
                null
        );

        TrdGetOrderFillList.Response response = futuBase.getOrderFillListSync(Long.parseLong(routeConfig.get("trdAcc")),
                TrdCommon.TrdMarket.forNumber(request.getMarket()),
                getTrdEnv(),
                true,
                conditions
        );
        //校验返回
        return handleResponse(response);
    }

    @SneakyThrows
    private JSONObject handleResponse(MessageOrBuilder builder) {
        JSONObject responseResult = JSON.parseObject(JsonFormat.printer().print(builder));
        if (responseResult.getIntValue("retType") != Common.RetType.RetType_Succeed_VALUE) {
            log.error("api response error msg:{}", responseResult.getString("retMsg"));
            throw new OpenApiException(OpenApiErrorCodeEnums.INVALID_CALL_ROUTE);
        }

       if(responseResult.containsKey("s2c")){
           return responseResult.getJSONObject("s2c");
       }
        return responseResult;
    }

    /**
     * type == 0 对象，== 1 列表
     */
    @SneakyThrows
    private Object handleResponseExt(MessageOrBuilder builder, int type, String str) {
        JSONObject responseResult = JSON.parseObject(JsonFormat.printer().print(builder));
        if (responseResult != null) {
            if (responseResult.getIntValue("retType") != Common.RetType.RetType_Succeed_VALUE) {
                log.error("api response error msg:{}", responseResult.getString("retMsg"));
                throw new OpenApiException(OpenApiErrorCodeEnums.INVALID_CALL_ROUTE);
            }
            JSONObject s2c = responseResult.getJSONObject("s2c");
            if (s2c != null && StringUtils.isNotBlank(str)) {
                if (type == 0) {
                    return s2c.getJSONObject(str);
                } else {
                    return s2c.getJSONArray(str);
                }
            }
        }
        return null;
    }
}
