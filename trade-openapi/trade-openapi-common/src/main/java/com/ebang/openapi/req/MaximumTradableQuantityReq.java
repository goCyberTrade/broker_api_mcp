package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import com.tigerbrokers.stock.openapi.client.struct.enums.ActionType;
import com.tigerbrokers.stock.openapi.client.struct.enums.OrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 获取获取最大可交易数量请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MaximumTradableQuantityReq extends BaseRequest{

//========================================Tiger

    /**
     * 账户，目前仅支持综合账户，为必填项
     */
    private String account;

    /**
     * 股票代码，为必填项
     */
    @NotBlank(groups = {ValidationUtil.LongportGroup.class,ValidationUtil.TigerGroup.class})
    private String symbol;

    /**
     * 到期日，交易品种是OPT/WAR/IOPT类型时必传，格式为yyyyMMdd，非必填项
     */
    private String expiry;

    /**
     * CALL/PUT，交易品种是OPT/WAR/IOPT类型时必传，非必填项
     */
    private String right;

    /**
     * 行权价，交易品种是OPT/WAR/IOPT类型时必传，非必填项
     */
    private String strike;

    /**
     * 分段类型，暂只支持SEC，非必填项，类型为SegmentType（需确保有对应的枚举或类定义 ）
     */
    private String segType;

    /**
     * 证券类型，STK:股票/FUT:期货/OPT:期权/WAR:窝轮/IOPT:牛熊证，期货暂不支持，非必填项，类型为SecType（需确保有对应的枚举或类定义 ）
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.SecType
     */
    private String secType;

    /**
     * 交易方向，BUY/SELL，为必填项
     * @see ActionType
     */
    @NotBlank(groups = {ValidationUtil.TigerGroup.class})
    private String action;

    /**
     * "MKT"="Market Order";"LMT"="Limit Order";"STP"="Stop Loss Order";"STP_LMT"="Stop Limit Order";"TRAIL"="Trailing Stop Order";"AM"="Auction Market Order";"AL"="Auction Limit Order";"TWAP"="Time Weighted Average Price";"VWAP"="Volume Weighted Average Price"
     * @see OrderType
     */
    @NotBlank(groups = {ValidationUtil.TigerGroup.class})
    private String orderType;

    /**
     * 限价，当orderType为LMT,STP_LMT时该参数必需，非必填项
     */
    private Double limitPrice;

    /**
     * 止损价，当orderType为STP,STP_LMT时该参数必需，非必填项
     */
    private Double stopPrice;

    /**
     * 机构用户专用，交易员密钥，非必填项
     */
    private String secretKey;

//===========================================HSTong
    /**
     * 交易类型，为必填项
     * 可能值：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通
     */
    @NotBlank(groups = {ValidationUtil.HStongGroup.class, ValidationUtil.USmartGroup.class})
    private String exchangeType;

    /**
     * 证券代码，为必填项，用于标识具体交易的证券
     */
    @NotBlank(groups = {ValidationUtil.HStongGroup.class, ValidationUtil.USmartGroup.class})
    private String stockCode;

    /**
     * 委托价格，为必填项，即交易时设定的价格
     */
    @NotBlank(groups = {ValidationUtil.HStongGroup.class})
    private String entrustPrice;

    /**
     * 委托类型，为必填项，不同交易类型对应不同可能值：
     * 港股：'0'-竞价限价、'1'-竞价、'2'-增强限价盘、'3'-限价盘、'4'-特别限价盘、'6'-暗盘、'7'-碎股
     * 美股：'3'-限价盘、'5'-市价盘、'8'-冰山市价、'9'-冰山限价、'10'-隐藏市价、'11'-隐藏限价
     * A股：'3'-限价盘
     */
    @NotBlank(groups = {ValidationUtil.HStongGroup.class})
    private String entrustType;

//=================================================futu

    /**
     * 账户ID
     */
    @NotNull(groups = {ValidationUtil.FutuGroup.class})
    private Long accId;

    /**
     * 订单类型，为必填项
     * 1 = 限价单；2 = 市价单；5 = 绝对限价订单；6 = 竞价订单；7 = 竞价限价订单；8 = 特别限价订单；9 = 特别限价且要求全部成交订单；10 = 止损市价单；11 = 止损限价单；12 = 触及市价单；13 = 触及限价单；14 = 跟踪止损市价单；15 = 跟踪止损限价单；16 = 时间加权市价算法单；17 = 时间加权限价算法单；18 = 成交量加权市价算法单；19 = 成交量加权限价算法单
     */
    @NotBlank(groups = {ValidationUtil.FutuGroup.class})
    private String futuOrderType;

    /**
     * 代码，为必填项
     * 港股必须是5位数字，A股必须是6位数字，美股没限制
     * 用于唯一标识交易的证券
     */
    @NotBlank(groups = {ValidationUtil.FutuGroup.class})
    private String code;

    /**
     * 价格，为必填项
     * 证券账户精确到小数点后3位，期货账户精确到小数点后9位，超出部分会被舍弃
     * 如果是竞价、市价单，也请填入一个当前价格，以便服务器计算
     */
    @NotNull(groups = {ValidationUtil.FutuGroup.class})
    private Double price;

    /**
     * 订单号，为选填项
     * 新下订单不需要，如果是修改订单就需要把原订单号带上
     * 因为改单的最大买卖数量会包含原订单数量
     */
   // private long orderID;

    /**
     * 是否调整价格，为选填项
     * 如果价格不合法，是否调整到合法价位，true调整，false不调整
     * 对港、A股有意义，因为港股有价位，A股2位精度，美股可不传
     */
    private boolean adjustPrice;

    /**
     * 调整方向和调整幅度百分比限制，为选填项
     * 正数代表向上调整，负数代表向下调整，具体值代表调整幅度限制
     * 如：0.015代表向上调整且幅度不超过1.5%；-0.01代表向下调整且幅度不超过1%
     */
    private double adjustSideAndLimit;

    /**
     * 证券所属市场，为选填项
     * 参见 TrdSecMarket 的枚举定义，用于指定证券所属的市场
     */
    @NotBlank(groups = {ValidationUtil.FutuGroup.class})
    private int secMarket;

    /**
     * 服务器订单id，为选填项
     * 可以用来代替orderID，和orderID二选一
     */
    private String orderIDEx;

//================================================longport
    /**
     * 股票代码，为必填项
     * 使用 ticker.region 格式，例如：AAPL.US
     * 用于唯一标识要交易的股票
     */
 //   private String symbol;

    /**
     * 订单类型，为必填项
     * "LO"="限价单";"ELO"="增强限价单";"MO"="市价单";"AO"="竞价市价单";"ALO"="竞价限价单";"ODD"="碎股单挂单";"LIT"="触价限价单";"MIT"="触价市价单";"TSLPAMT"="跟踪止损限价单(跟踪金额)";"TSLPPCT"="跟踪止损限价单(跟踪涨跌幅)";"SLO"="特殊限价单，不支持改单"
     */
    @NotBlank(groups = {ValidationUtil.LongportGroup.class})
    private String longportOrderType;

    /**
     * 预估下单价格，为选填项
     * 例如：388.5
     * 用于预先设定下单的价格参考
     */
  //  private BigDecimal price;

    /**
     * 买卖方向，为必填项
     * 可选值：
     * Buy - 买入
     * Sell - 卖出（卖出只支持美股卖空查询 ）
     * 用于指定股票交易的方向
     */
    @NotBlank(groups = {ValidationUtil.LongportGroup.class})
    private String side;

    /**
     * 结算货币，为选填项
     * 用于指定交易结算时使用的货币
     */
    private String currency;

    /**
     * 订单 ID，为选填项
     * 获取改单预估最大购买数量时必填
     * 用于标识具体的订单，改单场景下需要
     */
    private String orderId;

//============================================usmart
    /**
     * 委托价格，非必填（竞价单可不填），但不能为 0，类型为数字
     * 用于设置股票交易的委托价格
     */
  //  private Integer entrustPrice;

    /**
     * 委托属性，必填，类型为字符串
     * 可选值：'0'-美股限价单, 'd'-竞价单, 'e'-增强限价单, 'g'-竞价限价单, 'u'-碎股单
     * 用于指定委托的具体属性类型
     */
    @NotBlank(groups = {ValidationUtil.USmartGroup.class})
    private String entrustProp;

    /**
     * 交易类别，必填，类型为 int32
     * 可选值：0-香港,5-美股,6-沪港通,7-深港通
     * 用于标识股票交易所属的市场类别
     */
  //  private Integer exchangeType;

    /**
     * 证券代码，必填，类型为字符串
     * 用于唯一标识要交易的证券
     */
  //  private String stockCode;
}
