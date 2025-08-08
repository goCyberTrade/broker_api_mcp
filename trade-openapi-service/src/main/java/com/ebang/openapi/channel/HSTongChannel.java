package com.ebang.openapi.channel;

import com.ebang.openapi.config.HSQuantOpenTradeAndHqApi;
import com.ebang.openapi.exception.OpenApiErrorCodeEnums;
import com.ebang.openapi.exception.OpenApiException;
import com.ebang.openapi.req.*;
import com.ebang.openapi.util.DataUtils;
import com.huasheng.quant.open.gateway.api.HSQuantOpenApiHandle;
import com.huasheng.quant.open.gateway.sdk.constant.RateType;
import com.huasheng.quant.open.gateway.sdk.constant.trade.ExchangeType;
import com.huasheng.quant.open.gateway.sdk.domain.ModelResult;
import com.huasheng.quant.open.gateway.sdk.vo.CommonStringVo;
import com.huasheng.quant.open.gateway.sdk.vo.trade.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 华盛通证券
 *
 * @author chenlanqing
 */
@Component("hstong")
@Slf4j
public class HSTongChannel extends BaseChannel {

    @Autowired
    private HSQuantOpenTradeAndHqApi hSQuantOpenTradeAndHqApi;

    private final static int QUERY_COUNT = 20;

    @Override
    public Object getAllPosition(PositionListQueryReq req) throws Exception {
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        ExchangeTypeParam exchangeTypeParam = new ExchangeTypeParam();
        exchangeTypeParam.setExchangeType(req.getExchangeType());
        ModelResult<HoldsVo> holdsVoModelResult = quantOpenApiHandle.queryHoldsList(exchangeTypeParam);
        return handleResponse(holdsVoModelResult);
    }

    @Override
    public Object getAccountSummary(AccountSummaryReq req) {
        // 获取处理类
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        ExchangeTypeParam exchangeTypeParam = new ExchangeTypeParam();
        exchangeTypeParam.setExchangeType(req.getExchangeType());
        ModelResult<MarginFundInfo> result = quantOpenApiHandle.queryMarginFundInfo(exchangeTypeParam);
        return handleResponse(result);
    }

    @Override
    public Object getOrderInfo(OrderInfoQueryReq req) throws Exception {

        // 获取处理类
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        List<String> exchangeTypes = Arrays.stream(ExchangeType.values())
                .map(ExchangeType::getCode)
                .collect(Collectors.toList());
        // 查询参数
        RealEntrustPageQueryParam param = new RealEntrustPageQueryParam();
        param.setEntrustId(List.of(req.getEntrustId()));

        // 接口只支持单一市场类型，所以遍历所有
        for (String exchangeType : exchangeTypes) {
            param.setExchangeType(exchangeType);
            ModelResult<OrderVo> orderVoModelResult = quantOpenApiHandle.queryRealEntrustList(param);
            if (!orderVoModelResult.isSuccess()) {
                break;
            }
            List<Order> data = orderVoModelResult.getModel().getData();
            if (data != null && !data.isEmpty()) {
                return data.get(0);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public Object getOrderList(OrderListQueryReq req) throws Exception {

        // 获取处理类
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());

        List<String> exchangeTypes = req.getExchangeType();
        // 必传参数，没有时新增所有
        if (exchangeTypes == null || exchangeTypes.isEmpty()) {
            exchangeTypes = Arrays.stream(ExchangeType.values())
                    .map(ExchangeType::getCode)
                    .collect(Collectors.toList());
        }
        // 查询参数
        HistoryPageQueryParam param = new HistoryPageQueryParam();
        param.setStartDate(req.getStartDateYmd());
        param.setEndDate(req.getEndDateYmd());
        param.setQueryCount(QUERY_COUNT);

        List<Order> rtnList = new ArrayList<>();
        // 接口只支持单一市场类型，所以遍历所有
        for (String exchangeType : exchangeTypes) {
            param.setExchangeType(exchangeType);

            boolean hasNext = true;
            String queryParamStr = "0";
            // 分页查询补足数据
            do {
                param.setQueryParamStr(queryParamStr);
                ModelResult<OrderVo> orderVoModelResult = quantOpenApiHandle.queryHistoryEntrustList(param);
                if (!orderVoModelResult.isSuccess()) {
                    break;
                }
                List<Order> data = orderVoModelResult.getModel().getData();
                if (data != null && !data.isEmpty()) {
                    rtnList.addAll(data);
                }
                hasNext = data.size() == QUERY_COUNT;
                if (hasNext) {
                    queryParamStr = data.get(QUERY_COUNT - 1).getQueryParamStr();
                }

            } while (hasNext);
        }

        return rtnList;
    }

    @Override
    public Object modifyOrder(ModifyOrderReq req) throws Exception {

        OrderInfoQueryReq orderInfoQueryReq = new OrderInfoQueryReq();
        orderInfoQueryReq.setEntrustId(req.getEntrustId());
        Object orderInfo = getOrderInfo(orderInfoQueryReq);
        if (orderInfo == null) {
            throw new RuntimeException("查询不到订单");
        }
        Order order = (Order) orderInfo;
        // 获取处理类
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        ChangeEntrustParam entrustParam = new ChangeEntrustParam();

        entrustParam.setExchangeType(order.getExchangeType());
        entrustParam.setStockCode(order.getStockCode());
        entrustParam.setEntrustAmount(DataUtils.decimalToStringDef(req.getEntrustAmount(), order.getEntrustAmount()));
        entrustParam.setEntrustPrice(DataUtils.decimalToStringDef(req.getEntrustPrice(), order.getEntrustAmount()));
        entrustParam.setEntrustId(req.getEntrustId());
        entrustParam.setEntrustType(StringUtils.defaultIfEmpty(req.getEntrustType(), order.getEntrustType()));
        entrustParam.setSessionType(req.getSessionType());
        entrustParam.setValidDays(req.getValidDays());
        entrustParam.setCondValue(DataUtils.decimalToString(req.getCondValue()));
        entrustParam.setCondTrackType(req.getCondTrackType());
        ModelResult<CommonStringVo> result = quantOpenApiHandle.changeEntrust(entrustParam);
        return handleResponse(result);
    }

    @Override
    public Object cancelOrder(CancelOrderReq req) throws Exception {

        OrderInfoQueryReq orderInfoQueryReq = new OrderInfoQueryReq();
        orderInfoQueryReq.setEntrustId(req.getEntrustId());
        Object orderInfo = getOrderInfo(orderInfoQueryReq);
        if (orderInfo == null) {
            throw new RuntimeException("查询不到订单");
        }
        Order order = (Order) orderInfo;
        // 获取处理类
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        CancelEntrustParam entrustParam = new CancelEntrustParam();

        entrustParam.setExchangeType(order.getExchangeType());
        entrustParam.setStockCode(order.getStockCode());
        entrustParam.setEntrustAmount(order.getEntrustAmount());
        entrustParam.setEntrustPrice(order.getEntrustAmount());
        entrustParam.setEntrustId(req.getEntrustId());
        entrustParam.setEntrustType(order.getEntrustType());
        ModelResult<CommonStringVo> result = quantOpenApiHandle.cancelEntrust(entrustParam);
        return handleResponse(result);
    }

    @Override
    public Object createOrder(CreateOrderReq req) throws Exception {
        // 获取处理类
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        EntrustParam entrustParam = new EntrustParam();

        entrustParam.setExchangeType(req.getExchangeType());
        entrustParam.setStockCode(req.getStockCode());
        entrustParam.setEntrustAmount(DataUtils.decimalToString(req.getEntrustAmount()));
        entrustParam.setEntrustPrice(DataUtils.decimalToString(req.getEntrustPrice()));
        entrustParam.setEntrustBs(req.getEntrustBs());
        entrustParam.setEntrustType(req.getEntrustType());
        entrustParam.setExchange(req.getExchange());
        entrustParam.setSessionType(req.getSessionType());
        entrustParam.setIceBergDisplaySize(DataUtils.decimalToString(req.getIceBergDisplaySize()));
        entrustParam.setValidDays(req.getValidDays());
        entrustParam.setCondValue(DataUtils.decimalToString(req.getCondValue()));
        entrustParam.setCondTrackType(req.getCondTrackType());
        ModelResult<CommonStringVo> result = quantOpenApiHandle.entrust(entrustParam);
        return handleResponse(result);
    }

    /**
     * 解锁交易
     */
    @Override
    public Object unlockTrading(UnlockTradingReq req) {
        Map<String, String> routeConfig = getRouteConfig();
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        TradeLoginParam tradeLoginParam = new TradeLoginParam();
        tradeLoginParam.setPassword(routeConfig.get("password"));
        tradeLoginParam.setKeyBase64("m+qS04/2CH1OweCnmXZ3TDZkCQS+hBzY");
        return quantOpenApiHandle.tradeLogin(tradeLoginParam);
    }

    /**
     * 获取最大可交易数量
     */
    @Override
    public Object getMaximumTradableQuantity(MaximumTradableQuantityReq req) {
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        MaxAvailableAssetParam maxAvailableAssetParam = new MaxAvailableAssetParam();
        maxAvailableAssetParam.setEntrustType(req.getEntrustType());
        maxAvailableAssetParam.setEntrustPrice(req.getEntrustPrice());
        maxAvailableAssetParam.setStockCode(req.getStockCode());
        maxAvailableAssetParam.setExchangeType(req.getExchangeType());
        ModelResult<MaxAvailableAssetVo> maxAvailableAssetVoModelResult = quantOpenApiHandle.queryMaxAvailableAsset(maxAvailableAssetParam);
        return handleResponse(maxAvailableAssetVoModelResult);
    }

    /**
     * 查询股票融资融券信息
     */
    @Override
    public Object getMarginTradingData(MarginTradingDataReq req) {
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        DataTypeParam dataTypeParam = new DataTypeParam();
        dataTypeParam.setDataType(req.getDataType());
        dataTypeParam.setStockCode(req.getStockCode());
        ModelResult<MarginFullInfoVo> marginFullInfoVoModelResult = quantOpenApiHandle.queryMarginFullInfo(dataTypeParam);
        return handleResponse(marginFullInfoVoModelResult);
    }

    /**
     * 查询标的是否支持盘前盘后交易
     */
    @Override
    public Object tradeQueryBeforeAndAfterSupport(TradeQueryBeforeAndAfterSupportReq req) {
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        StockCodeParam stockCodeParam = new StockCodeParam();
        stockCodeParam.setStockCode(req.getStockCode());
        stockCodeParam.setExchangeType(req.getExchangeType());
        ModelResult<CommonStringVo> commonStringVoModelResult = quantOpenApiHandle.queryBeforeAndAfterSupport(stockCodeParam);
        return handleResponse(commonStringVoModelResult);
    }

    /**
     * 获取资金流水
     */
    @Override
    public Object getCashFlowSummary(CashFlowSummaryReq req) throws Exception {
        List<FundJour> all = new ArrayList<>();
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        String queryParamStr = "0";
        if (req.getStartDate() != null && req.getEndDate() != null) {
            HistoryPageIntQueryParam historyPageIntQueryParam = new HistoryPageIntQueryParam();
            historyPageIntQueryParam.setStartDate(req.getStartDate());
            historyPageIntQueryParam.setEndDate(req.getEndDate());
            historyPageIntQueryParam.setExchangeType(req.getExchangeType());
            while (true) {
                historyPageIntQueryParam.setQueryParamStr(queryParamStr);
                historyPageIntQueryParam.setQueryCount(100);
                ModelResult<FundJourVo> fundJourVoModelResult = quantOpenApiHandle.queryHistoryFundJourList(historyPageIntQueryParam);
                if (fundJourVoModelResult == null) {
                    break;
                }
                FundJourVo model = fundJourVoModelResult.getModel();
                if (model == null) {
                    break;
                }
                List<FundJour> data = model.getData();
                if (CollectionUtils.isEmpty(data)) {
                    break;
                }
                all.addAll(data);
                if (all.size() >= 10000) {
                    break;
                }
                queryParamStr = data.get(data.size() - 1).getQueryParamStr();
            }
            return all;
        } else {
            RealPageQueryParam realPageQueryParam = new RealPageQueryParam();
            realPageQueryParam.setExchangeType(req.getExchangeType());
            while (true) {
                realPageQueryParam.setQueryParamStr(queryParamStr);
                realPageQueryParam.setQueryCount(100);
                ModelResult<FundJourVo> fundJourVoModelResult = quantOpenApiHandle.queryRealFundJourList(realPageQueryParam);
                if (fundJourVoModelResult == null) {
                    break;
                }
                FundJourVo model = fundJourVoModelResult.getModel();
                if (model == null) {
                    break;
                }
                List<FundJour> data = model.getData();
                if (CollectionUtils.isEmpty(data)) {
                    break;
                }
                all.addAll(data);
                if (all.size() >= 10000) {
                    break;
                }
                queryParamStr = data.get(data.size() - 1).getQueryParamStr();
            }
            // 这里也可以实现类似的自动分页逻辑，如有需要可补充
            return all;
        }
    }

    /**
     * 查询汇率
     */
    @Override
    public Object getRate(RateReq req) {
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        RateType exchange = RateType.EXCHANGE;
        if (req.getRateType() != null && req.getRateType() == RateType.IMMEDIATE.getCode()) {
            exchange = RateType.IMMEDIATE;
        }
        return quantOpenApiHandle.queryRateList(exchange);
    }

    @Override
    public Object getHistoryTransaction(HistoryTransactionReq req) throws Exception {
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        HistoryPageQueryParam exchangeTypeParam = new HistoryPageQueryParam();
        exchangeTypeParam.setExchangeType(req.getExchangeType());
        exchangeTypeParam.setStartDate(req.getStartDate());
        exchangeTypeParam.setEndDate(req.getEndDate());
        exchangeTypeParam.setQueryParamStr(req.getQueryParamStr());
        exchangeTypeParam.setQueryCount(req.getLimit());
        ModelResult<OrderVo> holdsVoModelResult = quantOpenApiHandle.queryHistoryDeliverList(exchangeTypeParam);
        return handleResponse(holdsVoModelResult);
    }

    @Override
    public Object getTodayTransaction(TodayTransactionReq req) throws Exception {
        HSQuantOpenApiHandle quantOpenApiHandle = hSQuantOpenTradeAndHqApi.getQuantOpenApiHandle(getRouteConfig());
        RealPageQueryParam exchangeTypeParam = new RealPageQueryParam();
        exchangeTypeParam.setExchangeType(req.getExchangeType());
        exchangeTypeParam.setQueryParamStr(req.getQueryParamStr());
        exchangeTypeParam.setQueryCount(req.getLimit());
        ModelResult<OrderVo> holdsVoModelResult = quantOpenApiHandle.queryRealDeliverList(exchangeTypeParam);
        return handleResponse(holdsVoModelResult);
    }

    private Object handleResponse(ModelResult<?> modelResult) {
        if (Objects.isNull(modelResult)) {
            return null;
        }
        if (modelResult.isSuccess()) {
            return modelResult.getModel();
        }
        log.error("api response error, error code:{}  msg:{}", modelResult.getErrorCode(), modelResult.getErrorMsg());
        throw new OpenApiException(OpenApiErrorCodeEnums.INVALID_CALL_ROUTE);
    }
}
