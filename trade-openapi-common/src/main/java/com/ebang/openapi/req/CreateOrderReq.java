package com.ebang.openapi.req;

import com.ebang.openapi.utils.SnowflakeIdGenerator;
import com.ebang.openapi.utils.ValidationUtil.*;
import com.longport.trade.OrderSide;
import com.longport.trade.OrderType;
import com.longport.trade.OutsideRTH;
import com.longport.trade.TimeInForceType;
import com.tigerbrokers.stock.openapi.client.struct.TagValue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class CreateOrderReq extends BaseRequest {


    // IB相关参数
    // 合约编号
    @NotNull(groups = {IBKRGroup.class})
    private Integer conid;
    // 账户ID
    @NotEmpty(groups = {IBKRGroup.class})
    private String accountId;
    /**
     * 订单类型
     * @see com.ebang.openapi.enums.ibkr.OrderTypeEnums
     */
    @NotNull(groups = {IBKRGroup.class, FutuGroup.class, LongportGroup.class, TigerGroup.class})
    private String orderType;
    /**
     * 买卖方向
     * @see com.ebang.openapi.enums.ibkr.OrderSideEnums
     */
    @NotNull(groups = {IBKRGroup.class, LongportGroup.class})
    private String side;
    /**
     * 有效期限
     * @see com.ebang.openapi.enums.ibkr.TimeInForceEnums
     */
    @NotNull(groups = {IBKRGroup.class})
    private String tif;
    // 数量
    @NotNull(groups = {IBKRGroup.class})
    private BigDecimal quantity;
    // 价格
    private BigDecimal price;
    // 账户ID（接受订单账户）
    private String acctId;
    // 合约交易所相关
    private String conidex;
    // 证券类型
    @NotNull(groups = {TigerGroup.class})
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

    // 富途相关参数
    // 账户ID
    @NotNull(groups = {FutuGroup.class})
    private Long accId;
    /**
     * 交易市场
     * @see com.futu.openapi.pb.TrdCommon.TrdMarket
     */
    @NotNull(groups = {FutuGroup.class})
    private Integer trdMarket;
    /**
     * 交易方向
     * @see com.futu.openapi.pb.TrdCommon.TrdSide
     */
    @NotNull(groups = {FutuGroup.class})
    private Integer trdSide;
    /**
     * 订单类型
     * @see com.futu.openapi.pb.TrdCommon.OrderType
     */
//    private Integer orderType;
    // 代码，港股必须是5位数字，A股必须是6位数字，美股没限制
    @NotNull(groups = {FutuGroup.class})
    private String code;
    // 数量，期权单位是"张"（精确到小数点后0位，超出部分会被舍弃。期权期货单位是"张"）
    @NotNull(groups = {FutuGroup.class})
    private BigDecimal qty;
    // 价格，（证券账户精确到小数点后3位，期货账户精确到小数点后9位，超出部分会被舍弃）
//    private BigDecimal price;
    // 是否调整价格，如果价格不合法，是否调整到合法价位，true调整，false不调整。如果价格不合法又不允许调整，则会返回错误
    private Boolean adjustPrice;
    // 调整方向和调整幅度百分比限制，正数代表向上调整，负数代表向下调整，具体值代表调整幅度限制，如：0.015代表向上调整且幅度不超过1.5%；-0.01代表向下调整且幅度不超过1%
    private BigDecimal adjustSideAndLimit;
    /**
     * 证券所属市场
     * @see com.futu.openapi.pb.TrdCommon.TrdSecMarket
     */
    private Integer secMarket;
    // 用户备注字符串，最多只能传64字节。可用于标识订单唯一信息等，下单填上，订单结构就会带上。
    private String remark;
    /**
     * 订单有效期限
     * @see com.futu.openapi.pb.TrdCommon.TimeInForce
     */
    @NotEmpty(groups = {FutuGroup.class, LongportGroup.class})
    private String timeInForce;
    // 是否允许盘前盘后成交。仅适用于美股限价单。默认false
    private Boolean fillOutsideRTH;
    // 触发价格
//    private BigDecimal auxPrice;
    /**
     * 跟踪类型
     * @see com.futu.openapi.pb.TrdCommon.TrailType
     */
    private Integer trailType;
    // 跟踪金额/百分比
    private BigDecimal trailValue;
    // 指定价差
    private BigDecimal trailSpread;
    /**
     * 美股订单时段
     * @see com.futu.openapi.pb.Common.Session
     */
    private Integer session;


    // 长桥参数
    // 股票代码，使用ticker.regin格式，例如:AAPL.US
    @NotEmpty(groups = {LongportGroup.class, TigerGroup.class})
    private String symbol;
    /**
     * 订单类型
     * @see com.longport.trade.OrderType
     */
//    private String orderType;
    // 下单价格，LO/ELO/ALO/ODD/LIT 类型订单必填
    private BigDecimal submittedPrice;
    // 下单数量
    @NotEmpty(groups = {LongportGroup.class})
    private BigDecimal submittedQuantity;
    // 触发价格 LIT/MIT订单必填
    private BigDecimal triggerPrice;
    // 指定价差 TSLPAMT/TSLPPCT订单必填
    private BigDecimal limitOffset;
    // 跟踪金额 TSLPAMT 订单必填
    private BigDecimal trailingAmount;
    // 跟踪涨跌幅，单位为百分比，例如2.5表示2.5%。TSLPPCT订单必填
    private BigDecimal trailingPercent;
    // 长期单过期时间 格式为YYYY-MM-DD，timeInForce为GTD时必填
    private LocalDate expireDate;
    /**
     * 买卖方向
     * @see com.longport.trade.OrderSide
     */
//    private String side;
    /**
     * 是否允许盘前盘后，美股必填
     * @see com.longport.trade.OutsideRTH
     */
    private String outsideRTHStr;
    /**
     * 订单有效期类型
     * @see com.longport.trade.TimeInForceType
     */
//    private String timeInForce;
    // 备注
//    private String remark;

    // 老虎参数
    // 用户授权账户 必填
    @NotEmpty(groups = {TigerGroup.class})
    private String account;

    // 订单编号，作用是防止重复下单。可通过订单号接口获取，传0则服务端自动生成，传0无法防重复下单，需谨慎 选填
    // 看demo中没有传应该也是自动生成
//    private String orderId;
    
    // 股票代码 如：AAPL；（sec_typ为窝轮牛熊证时,在app窝轮/牛熊证列表中名称下面的5位数字） 必填
//    private String symbol;

    /**
     * 合约类型 必填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.SecType
     */
//    private String secType;

    /**
     * 交易方向 必填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.ActionType
     */
    @NotNull(groups = {TigerGroup.class})
    private String action;

    /**
     * 订单类型 必填
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.OrderType
     */
//    private String orderType;

    // 下单数量(港股、沪港通、窝轮、牛熊证有最小数量限制) 必填
    @NotNull(groups = {TigerGroup.class})
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
//    private BigDecimal trailingPercent;

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
//    private String timeInForce;

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

    // 微牛参数
    // 账户ID
//    private String accountId;
    // 第三方订单ID，此字段入参长度最长为40
    /**
     * 买卖方向
     * @see com.webull.openapi.common.dict.OrderSide
     */
//    private String side;
    /**
     * 订单有效期
     * @see com.webull.openapi.common.dict.OrderTIF
     */
//    private String tif;

    // 是否允许盘前盘后交易。 市价单只能是false
    private Boolean extendedHoursTrading;

    // 资产标的id，调用者通过调用Get Instruments获取
    private String instrumentId;
    /**
     * 订单类型
     * @see com.webull.openapi.common.dict.OrderType
     */
//    private String orderType;

    // 限价，order_type为 LIMIT、STOP_LOSS_LIMIT、ENHANCED_LIMIT、AT_AUCTION_LIMIT(竞价限价盘)必传
//    private BigDecimal limitPrice;

    // 下单的标的数量，整数，支持的最大值为1000000股
//    private BigDecimal qty;

    // 止损价，order_type为 STOP_LOSS(止损单)、STOP_LOSS_LIMIT(止损限价)时，需要传
    private BigDecimal stopPrice;

    /**
     * 跟踪止损单的价差类型，跟踪止损单要传
     * @see com.webull.openapi.common.dict.TrailingType
     */
//    private String trailingType;

    // 跟踪止损单的价差数值，跟踪止损单要传
    private BigDecimal trailingStopStep;

    // 华盛通参数
    /**
     * 交易类型 'K'-港股、'P'-美股、'v'-深股通、't'-沪股通
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.ExchangeType
     */
    private String exchangeType;
    // 证券代码
    private String stockCode;
    // 委托数量
    private BigDecimal entrustAmount;
    // 委托价格，<如果为条件单，该值可为空>
    private BigDecimal entrustPrice;
    /**
     * 买卖方向，'1'-买入、'2'-卖出（在空仓情况下传入'2'则为卖空，也可使用 '3'-空头平仓、'4'-空头开仓作为入参）
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.EntrustBs
     */
    private String entrustBs;
    /**
     * 委托类型
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.EntrustType
     */
    private String entrustType;
    /**
     * 美股直连交易所
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.EntrustEx
     */
//    private String exchange;
    /**
     * 盘前盘后交易，0:否 1:是 3:只支持盘中 5:港股支持盘中及暗盘 7:美股支持盘中及盘前盘后；<如果为普通订单，默认值为 0，可能值：0、1>；<如果为条件单，默认值为 3，可能值：3、5、7>
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.SessionType
     */
    private String sessionType;

    // 冰山单披露数量，如为冰山单，该值必填，且该值必须大于0，小于等于委托数量
    private BigDecimal iceBergDisplaySize;

    // 有效天数，<如果为条件单，该值必传，最多支持100个自然日>
    private String validDays;

    // 触发条件值，<如果为条件单，该值必传，可能值：价格、价差、百分比数字>
    private BigDecimal condValue;
    /**
     * 跟踪类型，1百分比、2价差；<如果为条件跟踪单，该值必传>
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.TradeCondTrackType
     */
    private String condTrackType;

    // 盈立证券
    // 流水号，最长19位，确保唯一推荐雪花算法生成（不传时后台自动生成）
    private Long serialNo = SnowflakeIdGenerator.getId();

    // 委托数量
//    private BigDecimal entrustAmount;

    // 价格(竞价单价格传0)
//    private BigDecimal entrustPrice;

    // 委托属性('0'-美股限价单/暗盘委托limit order,'d'-竞价单,'e'-增强限价单,'g'-竞价限价单) 港股: ('0'-暗盘委托/限价单,'d'-竞价单,'e'-增强限价单,'g'-竞价限价单,'w'-市价单)美股: ('0'-限价单,'w'-市价单)A股: ('0'-限价单)
    private String entrustProp;

    // 委托类别(0-买，1-卖)
//    private Integer entrustType;

    // 交易类别(0-香港,5-美股,6-沪港通,7-深港通)
//    private Integer exchangeType;

    // 股票代码
//    private String stockCode;

    // 交易密码（RSA公钥加密）
    private String password;

    // 股票名称
    private String stockName;

    // 是否强制委托标识，超过9倍24档下单时forceEntrustFlag=true可强制下单，但有可能是废单
    private Boolean forceEntrustFlag;

    // 交易阶段标志（0/不传-正常订单交易（默认），1-盘前，2-盘后交易，3-暗盘交易，12-盘前盘后）
//    private Integer sessionType;

    // 订单类型：GTC/GTD/DAY(默认DAY当日有效，暂不支持)
//    private String timeInForce;

    // 有效期（GTD传递订单,格式：yyyy-MM-dd，最多90天，暂不支持）
    private String validDate;

    // 交易所 默认SMART（SMART,AMEX,ARCA,BATS,BEX,BYX,CBOE,CHX,DRCTEDGE,EDGEA,EDGX,IBKRTS,IEX,ISE,ISLAND,LTSE,MEMX,NYSE,NYSENAT,PEARL,PHLX,PSX)
//    private String exchange;

    // Robinhood参数
    // 用于下单的账户URL
//    private String account;

    // 所交易证券的工具URL
    private String instrument;

    // 所交易证券的标的代码 例如:MSFT
//    private String symbol;

    // 订单类型：market（市价单）或 limit（限价单）
    private String type;

    // 订单有效期：gfd（当日有效至收盘）、gtc（取消前有效）、ioc（即时成交否则取消）、opg（开盘价成交）
//    private String timeInForce;

    // 触发类型：immediate（立即）或 stop（止损）
    private String trigger;

    // 买入时愿意支付的价格或卖出时愿意接受的价格
//    private BigDecimal price;

    // 当 trigger 为 stop 时的触发价格（达到该价格时，止损订单将转换为相应类型的订单）
//    private BigDecimal stopPrice;

    // 希望买入或卖出的股数
//    private BigDecimal quantity;

    // 交易方向：buy（买入）或 sell（卖出）
//    private String side;

    // 仅 OAuth 应用可用
    private String clientId;

    // 订单是否在交易所闭市时执行
    private Boolean extendedHours;

    // 是否覆盖日内交易检查
    private Boolean overrideDayTradeChecks;

    // 是否覆盖 DTBP（日内交易购买力）检查
    private Boolean overrideDtbpChecks;
}
