package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil.*;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderListQueryReq extends BaseRequest {


    // IB相关参数
    // 账户ID
    private String accountId;

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
    // 公共过滤对象
    @NotNull(groups = {FutuGroup.class})
    private TrdFilterConditions trdFilterConditions;
    /**
     * 订单状态
     * @see com.futu.openapi.pb.TrdCommon.OrderStatus
     */
    private List<Integer> statusList = new ArrayList<>();

    // 长桥参数
    // 标的代码
    private String symbol;
    /**
     * 订单状态
     * @see com.longport.trade.OrderStatus
     */
    private List<String> status;
    /**
     * 买卖方向
     * @see com.longport.trade.OrderSide
     */
    private String side;
    /**
     * 市场
     * @see com.longport.Market
     */
    private String market;
    // 订单编号
    private String orderId;
    // 历史订单开始时间(毫秒时间戳)
    private Long startAt;
    // 历史订单结束时间(毫秒时间戳)
    private Long endAt;

    // 老虎参数
    // 账号
    @NotEmpty(groups = {TigerGroup.class})
    private String account;
    /**
     * 账户分段
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.SegmentType
     */
    private String segType;
    /**
     * 证券类型
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.SecType
     */
    private String secType;
    /**
     * 老虎证券-市场
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.Market
     */
//    private String market;
    // 标的代码
//    private String symbol;
    // 过期日
    private String expiry;
    // 行权价格
    private BigDecimal strike;
    /**
     * 期权方向
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.Right
     */
    private String right;
    // 订单开始时间
    private String startDate;
    // 订单结束时间
    private String endDate;
    // 限制条数（默认100，最大300）
    private Integer limit;
    /**
     * 账户分段
     * @see com.tigerbrokers.stock.openapi.client.struct.enums.OrderStatus
     */
    private List<Integer> states;

    // 交易员秘钥(机构用户专用)
    private String secretKey;

    // 微牛参数
    // 账户ID
//    private String accountId;
    // 华盛通参数
    /**
     * 交易市场类型
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.ExchangeType
     */
    private List<String> exchangeType;
    // 起始日期(yyyyMMdd)
    private String startDateYmd;
    // 截止日期
    private String endDateYmd;

    // 盈立参数
    // 证券代码
    private String stockCode;

}
