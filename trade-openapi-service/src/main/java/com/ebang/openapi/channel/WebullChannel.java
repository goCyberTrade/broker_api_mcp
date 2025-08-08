package com.ebang.openapi.channel;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ebang.openapi.constant.Constants;
import com.ebang.openapi.req.*;
import com.ebang.openapi.util.DataUtils;
import com.futu.openapi.pb.TrdCommon;
import com.futu.openapi.pb.TrdGetHistoryOrderList;
import com.futu.openapi.pb.TrdModifyOrder;
import com.google.protobuf.util.JsonFormat;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.request.TigerHttpRequest;
import com.tigerbrokers.stock.openapi.client.https.response.TigerHttpResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.MethodName;
import com.tigerbrokers.stock.openapi.client.util.builder.TradeParamBuilder;
import com.webull.openapi.common.Region;
import com.webull.openapi.http.HttpApiConfig;
import com.webull.openapi.trade.api.TradeApiService;
import com.webull.openapi.trade.api.http.TradeHttpApiService;
import com.webull.openapi.trade.api.request.StockOrder;
import com.webull.openapi.trade.api.response.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 微牛证券
 *
 * @author chenlanqing
 */
@Component("webull")
public class WebullChannel extends BaseChannel {

    //key:apiKey
    final Map<String, TradeApiService> tradeApiServiceMap = new ConcurrentHashMap<>();

    private TradeApiService getTradeApiService() {
        //如果已初始化过客户端，无需再次初始化 HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String apiKey = getApiKey();
        if (tradeApiServiceMap.containsKey(apiKey)) {
            return tradeApiServiceMap.get(apiKey);
        }
        // 获取渠道配置
        Map<String, String> routeConfig = getRouteConfig();

        HttpApiConfig apiConfig = HttpApiConfig.builder()
                .appKey(routeConfig.get("appKey"))
                .appSecret(routeConfig.get("appSecret"))
                .regionId(Region.hk.name())
                .build();
        TradeApiService apiService = new TradeHttpApiService(apiConfig);
        tradeApiServiceMap.put(apiKey, apiService);
        return apiService;
    }

//    private InstrumentInfo getInstrumentInfo(String symbol, String category) {
//
//    }


    @Override
    public Object getAllPosition(PositionListQueryReq req) throws Exception {
        TradeApiService apiService = getTradeApiService();
        return apiService.getAccountPositions(req.getAccountId(), req.getPageSize(), req.getLastInstrumentId());
    }

    @Override
    public Object getAccountSummary(AccountSummaryReq req) {
        TradeApiService apiService = getTradeApiService();
        AccountDetail accountDetail = apiService.getAccountDetail(req.getAccountId());
        return accountDetail;
    }

    //获取账户列表
    @Override
    public Object accounts(AccountsReq req) {

        TradeApiService apiService = getTradeApiService();
        List<Account> accounts = apiService.getAccountList(null);
        return accounts;
    }

    @Override
    public Object balances(BalanceReq req) {

        TradeApiService apiService = getTradeApiService();
        AccountBalance accountBalance = apiService.getAccountBalance(req.getAccountId(), req.getCurrency());
        return accountBalance;
    }

    @Override
    public Object getOrderInfo(OrderInfoQueryReq req) throws Exception {
        TradeApiService apiService = getTradeApiService();
        SimpleOrder orderDetails = apiService.getOrderDetails(req.getAccountId(), req.getClientOrderId());
        return orderDetails;
    }

    @Override
    public Object getOrderList(OrderListQueryReq req) throws Exception {

        TradeApiService apiService = getTradeApiService();

        // 查询所有当日订单
        List<Order> rtnList = new ArrayList<>();
        boolean hasNext = true;
        String lastClientOrderId = null;
        do {
            Orders<Order> dayOrders = apiService.getDayOrders(req.getAccountId(), 100, lastClientOrderId);
            hasNext = dayOrders.getHasNext();
            if (!dayOrders.getOrders().isEmpty()) {
                lastClientOrderId = dayOrders.getOrders().get(dayOrders.getOrders().size() - 1).getClientOrderId();
                rtnList.addAll(dayOrders.getOrders());
            }

        } while (hasNext);

        List<String> orderIds = rtnList.stream().map(Order::getOrderId).collect(Collectors.toList());

        List<SimpleOrder> openOrders = new ArrayList<>();
        // 查询所有待成交订单
        lastClientOrderId = null;
        do {
            Orders<SimpleOrder> dayOrders = apiService.getOpenedOrders(req.getAccountId(), 100, lastClientOrderId);
            hasNext = dayOrders.getHasNext();
            if (!dayOrders.getOrders().isEmpty()) {
                lastClientOrderId = dayOrders.getOrders().get(dayOrders.getOrders().size() - 1).getClientOrderId();
                // 过滤当日订单中的待成交
                for (SimpleOrder item : dayOrders.getOrders()) {
                    if (orderIds.contains(item.getOrderId())) {
                        openOrders.add(item);
                    }
                }
            }

        } while (hasNext);

        // 组合订单
        if (!openOrders.isEmpty()) {
            rtnList.addAll(0, openOrders);
        }

        return rtnList;
    }
    @Override
    public Object modifyOrder(ModifyOrderReq req) throws Exception {

        OrderInfoQueryReq orderInfoQueryReq = new OrderInfoQueryReq();
        orderInfoQueryReq.setAccountId(req.getAccountId());
        orderInfoQueryReq.setClientOrderId(req.getClientOrderId());
        Object orderInfo = getOrderInfo(orderInfoQueryReq);
        if (orderInfo == null) {
            throw new RuntimeException("查询不到订单");
        }
        SimpleOrder order = (SimpleOrder)orderInfo;

        TradeApiService apiService = getTradeApiService();

        StockOrder stockOrder = new StockOrder();
        stockOrder.setClientOrderId(req.getClientOrderId());
        stockOrder.setSide(order.getSide());
        stockOrder.setTif(order.getTif());
        stockOrder.setExtendedHoursTrading(order.getExtendedHoursTrading());
        stockOrder.setInstrumentId(order.getInstrumentId());
        stockOrder.setOrderType(order.getOrderType());
        stockOrder.setLimitPrice(DataUtils.decimalToString(req.getLimitPrice()));
        stockOrder.setQty(DataUtils.decimalToString(req.getQty()));
        stockOrder.setStopPrice(DataUtils.decimalToString(req.getStopPrice()));
        stockOrder.setTrailingType(req.getTrailingType());
        stockOrder.setTrailingStopStep(DataUtils.decimalToString(req.getTrailingStopStep()));

        OrderResponse rtnRes = apiService.replaceOrder(req.getAccountId(), stockOrder);
        return rtnRes;
    }
    public Object cancelOrder(CancelOrderReq req) throws Exception {

        TradeApiService apiService = getTradeApiService();
        OrderResponse rtnRes = apiService.cancelOrder(req.getAccountId(), req.getClientOrderId());
        return rtnRes;
    }
    @Override
    public Object createOrder(CreateOrderReq req) throws Exception {

        TradeApiService apiService = getTradeApiService();

        StockOrder stockOrder = new StockOrder();
        stockOrder.setClientOrderId(req.getClientOrderId());
        stockOrder.setSide(req.getSide());
        stockOrder.setTif(req.getTif());
        stockOrder.setExtendedHoursTrading(req.getExtendedHoursTrading());
        stockOrder.setInstrumentId(req.getInstrumentId());
        stockOrder.setOrderType(req.getOrderType());
        stockOrder.setLimitPrice(DataUtils.decimalToString(req.getLimitPrice()));
        stockOrder.setQty(DataUtils.decimalToString(req.getQty()));
        stockOrder.setStopPrice(DataUtils.decimalToString(req.getStopPrice()));
        stockOrder.setTrailingType(req.getTrailingType());
        stockOrder.setTrailingStopStep(DataUtils.decimalToString(req.getTrailingStopStep()));

        OrderResponse rtnRes = apiService.placeOrder(req.getAccountId(), stockOrder);
        return rtnRes;
    }


    /**
     * 查询交易标的
     */
    @Override
    public Object getSymbol(GetSymbolReq req) {
        TradeApiService apiService = getTradeApiService();
        return apiService.getTradeInstrument(req.getInstrumentId());
    }

    /**
     * 查询标的基础信息
     */
    @Override
    public Object getInstrumentInfo(InstrumentInfoReq req) {
        TradeApiService apiService = getTradeApiService();
        return apiService.getSecurityInfo(req.getSymbol(), req.getMarket()
                    , req.getInstrumentSuperType(), req.getInstrumentType(), req.getStrikePrice(), req.getInitExpDate());
    }
}
