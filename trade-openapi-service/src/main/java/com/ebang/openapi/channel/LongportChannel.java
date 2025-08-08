package com.ebang.openapi.channel;

import com.ebang.openapi.config.LongportConfig;
import com.ebang.openapi.context.RequestContext;
import com.ebang.openapi.enums.ProductTypeEnums;
import com.ebang.openapi.req.*;
import com.ebang.openapi.util.DateUtils;
import com.ebang.openapi.util.EnumUtils;
import com.longport.Config;
import com.longport.ConfigBuilder;
import com.longport.Market;
import com.longport.trade.*;
import com.longport.trade.CashFlow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 长桥证券
 *
 * @author chenlanqing
 */
@Slf4j
@RequiredArgsConstructor
@Component("longport")
public class LongportChannel extends BaseChannel {

    private final Config config;
    private final LongportConfig longportConfig;

    private final static Map<String, TradeContext> contextMap = new HashMap<>();
    private TradeContext getTradeContext() throws Exception {

        if (!contextMap.containsKey(RequestContext.getApiKey())) {
            // 获取配置
            Map<String, String> routeConfig = getRouteConfig();
            Config configBuild = new ConfigBuilder(
                    routeConfig.get("appKey"),
                    routeConfig.get("appSecret"),
                    routeConfig.get("accessToken")
            ).build();
            TradeContext tradeContext = TradeContext.create(configBuild).get(longportConfig.getTimeout(), TimeUnit.SECONDS);
            contextMap.put(RequestContext.getApiKey(), tradeContext);
        }
        return contextMap.get(RequestContext.getApiKey());

    }

    @Override
    public Object getAllPosition(PositionListQueryReq req) throws Exception {
        // 基金持仓： https://open.longportapp.com/zh-CN/docs/trade/asset/fund
        // 股票持仓： https://open.longportapp.com/zh-CN/docs/trade/asset/stock
        var symbol = req.getSymbol();
        var symbolArray = new String[]{};
        if (StringUtils.isNotBlank(symbol)) {
            symbolArray = symbol.split(",");
        }
        var context = TradeContext.create(config).get(longportConfig.getTimeout(), TimeUnit.SECONDS);
        if (ProductTypeEnums.FUNDS.getProductType().equals(req.getProductType())) {
            // 基金查询
            var fundPositionsOptions = new GetFundPositionsOptions();
            fundPositionsOptions.setSymbols(symbolArray);
            var fundsResponse = context.getFundPositions(fundPositionsOptions).get(longportConfig.getTimeout(), TimeUnit.SECONDS);
            return fundsResponse.getChannels();
        }

        var getStockPositionsOptions = new GetStockPositionsOptions();
        getStockPositionsOptions.setSymbols(symbolArray);
        var stockResponse = context.getStockPositions(getStockPositionsOptions).get(longportConfig.getTimeout(), TimeUnit.SECONDS);
        return stockResponse.getChannels();
    }

    @Override
    public Object availableFunds(AvailableFundsReq availableFundsReq) throws Exception {
        var context = getTradeContext();
        CompletableFuture<AccountBalance[]> response;
        if (StringUtils.isEmpty(availableFundsReq.getCurrency())) {
            response = context.getAccountBalance();
        } else {
            response = context.getAccountBalance(availableFundsReq.getCurrency());
        }

        return response.get();
    }

    @Override
    public Object getOrderInfo(OrderInfoQueryReq req) throws Exception {
        // 获取配置
        var context = getTradeContext();
        var response = context.getOrderDetail(req.getOrderId());
        return response.get();
    }

    @Override
    public Object getOrderList(OrderListQueryReq req) throws Exception {

        // 获取配置
        var context = getTradeContext();
        // 当日订单
        GetTodayOrdersOptions getTodayOrdersOptions = new GetTodayOrdersOptions();
        getTodayOrdersOptions.setSymbol(req.getSymbol());
        getTodayOrdersOptions.setStatus(EnumUtils.safeConvertToEnumArrayIgnoreCase(OrderStatus.class, req.getStatus()));
        getTodayOrdersOptions.setSide(EnumUtils.safeValueOfIgnoreCase(OrderSide.class, req.getSide()));
        getTodayOrdersOptions.setMarket(EnumUtils.safeValueOfIgnoreCase(Market.class, req.getMarket()));
        getTodayOrdersOptions.setOrderId(req.getOrderId());
        var dayResponse = context.getTodayOrders(getTodayOrdersOptions);
        List<Order> dayOrderList = new ArrayList<>(Arrays.asList(dayResponse.get()));
        // 历史订单
        GetHistoryOrdersOptions getHistoryOrdersOptions = new GetHistoryOrdersOptions();
        getHistoryOrdersOptions.setSymbol(req.getSymbol());
        getHistoryOrdersOptions.setStatus(EnumUtils.safeConvertToEnumArrayIgnoreCase(OrderStatus.class, req.getStatus()));
        getHistoryOrdersOptions.setSide(EnumUtils.safeValueOfIgnoreCase(OrderSide.class, req.getSide()));
        getHistoryOrdersOptions.setMarket(EnumUtils.safeValueOfIgnoreCase(Market.class, req.getMarket()));
        getHistoryOrdersOptions.setStartAt(DateUtils.timestampToLocalOffsetDateTime(req.getStartAt()));
        getHistoryOrdersOptions.setEndAt(DateUtils.timestampToLocalOffsetDateTime(req.getEndAt()));
        var hisResponse = context.getHistoryOrders(getHistoryOrdersOptions);
        List<Order> hisOrderList = new ArrayList<>(Arrays.asList(hisResponse.get()));
        List<String> orderIds = hisOrderList.stream().map(Order::getOrderId).collect(Collectors.toList());
        dayOrderList.forEach(item -> {
            if (!orderIds.contains(item.getOrderId())) {
                hisOrderList.add(item);
            }
        });
        return hisOrderList;


    }
    @Override
    public Object modifyOrder(ModifyOrderReq req) throws Exception {

        // 获取配置
        TradeContext context = getTradeContext();

        // 查询订单信息
        OrderInfoQueryReq orderInfoQueryReq = new OrderInfoQueryReq();
        orderInfoQueryReq.setOrderId(req.getOrderId());
        Object orderInfoObj = getOrderInfo(orderInfoQueryReq);
        if (orderInfoObj == null) {
            throw new RuntimeException("查询不到订单");
        }

        // 修改订单
        ReplaceOrderOptions param = new ReplaceOrderOptions(
                req.getOrderId(),
                req.getQuantity() == null ? ((OrderDetail)orderInfoObj).getQuantity() : req.getQuantity()
        );
        param.setPrice(req.getPrice());
        param.setTriggerPrice(req.getTriggerPrice());
        param.setLimitOffset(req.getLimitOffset());
        param.setTrailingAmount(req.getTrailingAmount());
        param.setTrailingPercent(req.getTrailingPercent());
        param.setRemark(req.getRemark());

        var response = context.replaceOrder(param);
        return response.get();
    }
    public Object cancelOrder(CancelOrderReq req) throws Exception {

        // 获取配置
        TradeContext context = getTradeContext();
        var response = context.cancelOrder(req.getOrderId());
        return response.get();
    }
    @Override
    public Object createOrder(CreateOrderReq req) throws Exception {

        // 获取配置
        TradeContext context = getTradeContext();
        // 提交订单
        SubmitOrderOptions param = new SubmitOrderOptions(
                req.getSymbol(),
                EnumUtils.safeValueOfIgnoreCase(OrderType.class, req.getOrderType()),
                EnumUtils.safeValueOfIgnoreCase(OrderSide.class, req.getSide()),
                req.getSubmittedQuantity(),
                EnumUtils.safeValueOfIgnoreCase(TimeInForceType.class, req.getTimeInForce())
        );
        param.setSubmittedPrice(req.getSubmittedPrice());
        param.setTriggerPrice(req.getTriggerPrice());
        param.setLimitOffset(req.getLimitOffset());
        param.setTrailingAmount(req.getTrailingAmount());
        param.setTrailingPercent(req.getTrailingPercent());
        param.setExpireDate(req.getExpireDate());
        param.setOutsideRth(EnumUtils.safeValueOfIgnoreCase(OutsideRTH.class, req.getOutsideRTHStr()));
        param.setRemark(req.getRemark());

        var response = context.submitOrder(param);
        return response.get();
    }

    /**
     * 获取资金流水
     */
    @Override
    public Object getCashFlowSummary(CashFlowSummaryReq req) throws Exception {
        var context = getTradeContext();
        OffsetDateTime offsetDateTimeStart = DateUtils.dateStrToOffsetDateTime(req.getStartTime(), ZoneOffset.UTC);
        OffsetDateTime offsetDateTimeEnd = DateUtils.dateStrToOffsetDateTime(req.getEndTime(), ZoneOffset.UTC);
        List<CashFlow> allCashFlows = new ArrayList<>();
        int page = 1;
        int pageSize = 1000;
        int maxRecords = 10000;
        while (allCashFlows.size() < maxRecords) {
            GetCashFlowOptions getCashFlowOptions = new GetCashFlowOptions(offsetDateTimeStart, offsetDateTimeEnd);
            getCashFlowOptions.setSymbol(req.getSymbol());
            getCashFlowOptions.setPage(page);
            getCashFlowOptions.setSize(pageSize);
            if (req.getBusinessType() != null) {
                BalanceType balanceType = null;
                // 1 - 现金、2 - 股票、3 - 基金
                switch (req.getBusinessType()) {
                    case 1:
                        balanceType = BalanceType.Cash;
                        break;
                    case 2:
                        balanceType = BalanceType.Stock;
                        break;
                    case 3:
                        balanceType = BalanceType.Fund;
                        break;
                    default:
                        break;
                }
                if (balanceType != null) {
                    getCashFlowOptions.setBusinessType(balanceType);
                }
            }
            var response = context.getCashFlow(getCashFlowOptions);
            CashFlow[] cashFlows = response.get();
            if (cashFlows == null || cashFlows.length == 0) {
                break; // 没有更多数据了
            }
            // 计算本次需要添加的数据量，确保不超过maxRecords
         //   int remainingCapacity = maxRecords - allCashFlows.size();
           // int dataToAdd = Math.min(cashFlows.length, remainingCapacity);
            // 只添加需要的数据量
            for (int i = 0; i < cashFlows.length; i++) {
                allCashFlows.add(cashFlows[i]);
            }
            // 如果已经达到最大记录数，或者数据不足一页，则停止查询
            if (allCashFlows.size() >= maxRecords || cashFlows.length < pageSize) {
                break;
            }
            page++;
        }
        return allCashFlows;
    }

    /**
     * 预估最大购买数量
     */
    @Override
    public Object getMaximumTradableQuantity(MaximumTradableQuantityReq req) throws Exception {
        var context = getTradeContext();
        EstimateMaxPurchaseQuantityOptions estimateMaxPurchaseQuantityOptions =
                new EstimateMaxPurchaseQuantityOptions(req.getSymbol(), OrderType.valueOf(req.getLongportOrderType()), OrderSide.valueOf(req.getSide()));
        estimateMaxPurchaseQuantityOptions.setPrice(req.getPrice() != null? BigDecimal.valueOf(req.getPrice()):null);
        estimateMaxPurchaseQuantityOptions.setCurrency(req.getCurrency());
        estimateMaxPurchaseQuantityOptions.setOrderId(req.getOrderId());
        var response = context.getEstimateMaxPurchaseQuantity(estimateMaxPurchaseQuantityOptions);
        return response.get();
    }

    /**
     * 获取保证金比例
     */
    @Override
    public Object marginRatio(MarginRatioReq req) throws Exception {
        var context = getTradeContext();
        var response = context.getMarginRatio(req.getSymbol());
        return response.get();
    }

    @Override
    public Object getHistoryTransaction(HistoryTransactionReq req) throws Exception {
        var context = TradeContext.create(config).get(longportConfig.getTimeout(), TimeUnit.SECONDS);
        GetHistoryExecutionsOptions historyExecutionsOptions = new GetHistoryExecutionsOptions();
        historyExecutionsOptions.setSymbol(req.getSymbol());
        historyExecutionsOptions.setStartAt(DateUtils.dateStrToOffsetDateTime(req.getStartDate(), ZoneOffset.UTC));
        historyExecutionsOptions.setEndAt(DateUtils.dateStrToOffsetDateTime(req.getEndDate(), ZoneOffset.UTC));
        CompletableFuture<Execution[]> response = context.getHistoryExecutions(historyExecutionsOptions);
        return response.get(longportConfig.getTimeout(), TimeUnit.SECONDS);
    }

    @Override
    public Object getTodayTransaction(TodayTransactionReq req) throws Exception {
        var context = TradeContext.create(config).get(longportConfig.getTimeout(), TimeUnit.SECONDS);
        GetTodayExecutionsOptions todayTransactionReq = new GetTodayExecutionsOptions();
        todayTransactionReq.setSymbol(req.getSymbol());
        todayTransactionReq.setOrderId(req.getOrderId());
        CompletableFuture<Execution[]> response = context.getTodayExecutions(todayTransactionReq);
        return response.get(longportConfig.getTimeout(), TimeUnit.SECONDS);
    }
}
