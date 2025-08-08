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
public class ModifyOrderReq extends BaseRequest {


    // IB相关参数
    // 订单编号
    @NotNull(groups = {IBKRGroup.class, FutuGroup.class, LongportGroup.class})
    private String orderId;
    // 账户ID
    @NotNull(groups = {IBKRGroup.class})
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
    // 订单号，forAll 为 true 时，传0
//    private String orderId;

// 下面的字段仅针对单个订单，且 modifyOrderOp 为 ModifyOrderOp_Normal 有效

    // 数量，期权单位是"张"（精确到小数点后 0 位，超出部分会被舍弃）
    private BigDecimal qty;

    // 价格，（证券账户精确到小数点后 3 位，期货账户精确到小数点后 9 位，超出部分会被舍弃）
//    private BigDecimal price;

    // 是否调整价格，如果价格不合法，是否调整到合法价位，true 调整，false 不调整。如果价格不合法又不允许调整，则会返回错误
    private Boolean adjustPrice;

    // 调整方向和调整幅度百分比限制，正数代表向上调整，负数代表向下调整，具体值代表调整幅度限制，如：0.015代表向上调整且幅度不超过1.5%；-0.01代表向下调整且幅度不超过1%
    private BigDecimal adjustSideAndLimit;

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

    // 表示服务器订单id，可以用来代替orderID，和orderID二选一
    private String orderIDEx;


    // 长桥参数
    // 订单编号
//    private String orderId;
    // 改单数量
//    private BigDecimal quantity;
    //    private String orderType;
    // 改单价格，LO/ELO/ALO/ODD/LIT 类型订单必填
//    private BigDecimal price;
    // 触发价格 LIT/MIT订单必填
    private BigDecimal triggerPrice;
    // 指定价差 TSLPAMT/TSLPPCT订单必填
    private BigDecimal limitOffset;
    // 跟踪金额 TSLPAMT 订单必填
    private BigDecimal trailingAmount;
    // 跟踪涨跌幅，单位为百分比，例如2.5表示2.5%。TSLPPCT订单必填
    private BigDecimal trailingPercent;
    // 备注
    private String remark;

    // 老虎参数
    // 用户授权账户 必填
    @NotEmpty(groups = {TigerGroup.class})
    private String account;
    // 订单编号
    @NotNull(groups = {TigerGroup.class})
    private Long id;
    // 改单数量(港股、沪港通、窝轮、牛熊证有最小数量限制) 必填
    private Long totalQuantity;
    // 改单数量的偏移量，默认0。碎股单结合totalQuantity代表真实数量，如 totalQuantity=111、scale=2 → 真实 1.11，选填
    private Integer totalQuantityScale;

    // 限价，orderType 为 LMT、STP_LMT 时必需
    private BigDecimal limitPrice;

    // 股票订单止损触发价，orderType 为 STP、STP_LMT 时必需；为 TRAIL 时是跟踪额；与 trailingPercent 互斥（后者优先）
//    private BigDecimal auxPrice;

    // 跟踪止损单 - 止损百分比，orderType 为 TRAIL 时与 auxPrice 互斥（优先用该字段）
//    private BigDecimal trailingPercent;
    // 机构用户专用 - 交易员密钥，选填
    private String secretKey;

    // 微牛参数
    // 账户ID
//    private String accountId;
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
    // 委托数量
    private BigDecimal entrustAmount;
    // 委托价格，<如果为条件单，该值可为空>
    private BigDecimal entrustPrice;
    // 原始委托编号
    private String entrustId;
    /**
     * 委托类型
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.EntrustType
     */
    private String entrustType;
    /**
     * 盘前盘后交易，0:否 1:是 3:只支持盘中 5:港股支持盘中及暗盘 7:美股支持盘中及盘前盘后；<如果为普通订单，默认值为 0，可能值：0、1>；<如果为条件单，默认值为 3，可能值：3、5、7>
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.SessionType
     */
    private String sessionType;

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

    // 委托数量
//    private BigDecimal entrustAmount;
    // 委托ID
//    private String entrustId;
    // 价格(竞价单价格传0)
//    private BigDecimal entrustPrice;
    // 交易密码（RSA公钥加密）
    private String password;
    // 是否强制委托标识，超过9倍24档下单时forceEntrustFlag=true可强制下单，但有可能是废单
    private Boolean forceEntrustFlag;
}
