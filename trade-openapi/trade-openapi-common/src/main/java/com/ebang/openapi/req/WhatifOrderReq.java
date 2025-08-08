package com.ebang.openapi.req;

import com.ebang.openapi.utils.SnowflakeIdGenerator;
import com.longport.trade.OrderSide;
import com.longport.trade.OrderType;
import com.longport.trade.OutsideRTH;
import com.longport.trade.TimeInForceType;
import com.tigerbrokers.stock.openapi.client.struct.TagValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class WhatifOrderReq extends BaseRequest {


    // IB相关参数
    // 合约编号
    private Integer conid;
    // 账户ID
    private String accountId;
    /**
     * 订单类型
     * @see com.ebang.openapi.enums.ibkr.OrderTypeEnums
     */
    private String orderType;
    /**
     * 买卖方向
     * @see com.ebang.openapi.enums.ibkr.OrderSideEnums
     */
    private String side;
    /**
     * 有效期限
     * @see com.ebang.openapi.enums.ibkr.TimeInForceEnums
     */
    private String tif;
    // 数量
    private BigDecimal quantity;
    // 价格
    private BigDecimal price;
    // 账户ID（接受订单账户）
    private String acctId;
    // 合约交易所相关
    private String conidex;
    // 证券类型
    private String secType;
    // 客户端订单ID
    private String clientOrderId;
    // 父订单ID
    private String parentId;
    // 上市交易所
    private String listingExchange;
    // 是否单组
    private Boolean isSingleGroup;
    // 是否盘前盘后交易
    private Boolean outsideRTH;
    // 辅助价格
    private BigDecimal auxPrice;
    // 交易代码
    private String ticker;
    // 跟踪金额
    private BigDecimal trailingAmt;
    // 跟踪类型
    private String trailingType;
    // 渠道内部标识
    private String referrer;
    // 现金数量
    private BigDecimal cashQty;
    // 是否使用自适应价格管理算法
    private Boolean useAdaptive;
    // 是否货币转换
    private Boolean isCcyConv;
    // 策略
    private String strategy;
    // 策略参数
    private Map<String, Object> strategyParameters;

    // 老虎参数
    // 用户授权账户 必填
    private String account;

    // 订单编号，作用是防止重复下单。可通过订单号接口获取，传0则服务端自动生成，传0无法防重复下单，需谨慎 选填
    // 看demo中没有传应该也是自动生成
//    private String orderId;

    // 股票代码 如：AAPL；（sec_typ为窝轮牛熊证时,在app窝轮/牛熊证列表中名称下面的5位数字） 必填
    private String symbol;

    /**
     * 合约类型 必填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.SecType
     */
//    private String secType;

    /**
     * 交易方向 必填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.ActionType
     */
    private String action;

    /**
     * 订单类型 必填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.OrderType
     */
//    private String orderType;

    // 下单数量(港股、沪港通、窝轮、牛熊证有最小数量限制) 必填
    private Long totalQuantity;

    // 下单数量的偏移量，默认0。碎股单结合totalQuantity代表真实数量，如 totalQuantity=111、scale=2 → 真实 1.11，选填
    private Integer totalQuantityScale;

    // 订单金额(基金等金额订单场景用)
    private BigDecimal cashAmount;

    // 限价，orderType 为 LMT、STP_LMT 时必需
    private BigDecimal limitPrice;

    // 股票订单止损触发价，orderType 为 STP、STP_LMT 时必需；为 TRAIL 时是跟踪额；与 trailingPercent 互斥（后者优先）
//    private BigDecimal auxPrice;

    // 跟踪止损单 - 止损百分比，orderType 为 TRAIL 时与 auxPrice 互斥（优先用该字段）
    private BigDecimal trailingPercent;

    // 是否允许盘前盘后交易(美股专属)，true 允许、false 不允许，默认允许；市价/止损/跟踪止损单忽略该参数
//    private Boolean outsideRth;

    /**
     * 美股订单时段，选填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.TradingSessionType
     */
    private String tradingSessionType;

    // 价格微调幅度，默认0不调整；正数向上、负数向下，自动调价格到合法位（如 0.001 代表幅度≤0.1%），选填
    private BigDecimal adjustLimit;

    /**
     * 市场，选填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.Market
     */
    private String market;
    /**
     * 货币，选填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.Currency
     */
    private String currency;
    /**
     * 订单有效期，可选 DAY（当日）、GTC（取消前，最长180天）、GTD（指定时间前），默认 DAY，选填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.TimeInForce
     */
    private String timeInForce;

    // 订单有效截止时间（13位时间戳，精确到秒），timeInForce 为 GTD 时必填，否则不填
    private Long expireTime;

    // 交易所 (美股 SMART 港股 SEHK 沪港通 SEHKNTL 深港通 SEHKSZSE)，选填
    private String exchange;

    // 过期日(期权、窝轮、牛熊证专属)，选填
    private String expiry;

    // 底层价格(期权、窝轮、牛熊证专属)，选填
    private String strike;

    // 期权方向 PUT/CALL(期权、窝轮、牛熊证专属)，选填
    private String right;

    // 1手单位(期权、窝轮、牛熊证专属)，选填
    private BigDecimal multiplier;

    // 本地标的 窝轮牛熊证必填，填app列表中名称下5位数字，选填
    private String localSymbol;

    // 机构用户专用 - 交易员密钥，选填
    private String secretKey;

    // 下单备注，下单后不可改，查询订单返回该信息，选填
    private String userMark;
    // 附加订单参数
    // 附加订单（Attached Order ）是指能通过附加的子订单对主订单起到止盈或止损效果的订单，可以附加的子订单类型有限价单（可用于止盈）、止损限价单/止损单（可用于止损）。通过增加以下参数可以实现附加订单
    /**
     * 附加订单类型，下附加单时必填（orderType 需为 LMT）：PROFIT-止盈、LOSS-止损、BRACKETS-括号单（含止盈+止损），必填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.AttachType
     */
    private String attachType;

    // 止盈单编号，可通过订单号接口获取，传0则服务端生成
    // 看demo中没有传应该也是自动生成
//    private Integer profitTakerOrderId;

    // 止盈单价格，下止盈单时必填
    private Double profitTakerPrice;

    // 止盈单有效期，同 timeInForce，仅支持 DAY、GTC，下止盈单时必填
    private String profitTakerTif;

    // 止盈单是否允许盘前盘后，同 outsideRth 逻辑
    private Boolean profitTakerRth;

    // 止损单编号，可通过订单号接口获取，传0则服务端生成
    // 看demo中没有传应该也是自动生成
//    private Integer stopLossOrderId;

    // 止损单触发价，下止损单时必填，必填
    private BigDecimal stopLossPrice;

    // 止损单执行限价（仅综合账号场景用），止损单的限价没有填写时，为附加止损市价单
    private BigDecimal stopLossLimitPrice;

    // 止损单有效期，同 timeInForce，仅支持 DAY、GTC，下止损单时必填
    private String stopLossTif;

    // 跟踪止损单 - 止损百分比，下跟踪止损单时与 stopLossTrailingAmount 二选一（填了都优先用该字段），选填
    private BigDecimal stopLossTrailingPercent;

    // 跟踪止损单 - 止损额，下跟踪止损单时与 stopLossTrailingPercent 二选一，选填
    private BigDecimal stopLossTrailingAmount;

    // TWAP/VWAP订单参数
    // TWAP/VWAP订单,只支持美股股票标的，只能在盘中下单，不支持预挂单
    // 订单类型 orderType
    // 资金账号 account
    // 股票代码 symbol
    // 证券类型只支持STK secType
    // 订单数量 totalQuantity
    // 算法参数
    // start_time long 策略开始时间
    // end_time long 策略结束时间
    // participation_rate String 最大参与率(成交量为日军成交量的最大比例)
    private List<TagValue> algoParams;
}
