package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil.*;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class OrderInfoQueryReq extends BaseRequest {


    // IB相关参数
    // 订单编号
    @NotEmpty(groups = {IBKRGroup.class, FutuGroup.class, LongportGroup.class})
    private String orderId;

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
    // 订单编号
//    private String orderId;
    // 长桥参数
    // 订单编号

    // 老虎参数
    // 账号
    @NotEmpty(groups = TigerGroup.class)
    private String account;
    // 订单编号
    @NotNull(groups = {TigerGroup.class})
    private Long id;
    // 交易员秘钥(机构用户专用)
    private String secretKey;

    // 微牛参数
    // 账户ID
    private String accountId;
    // 客户端订单ID
    private String clientOrderId;

    // 华盛通参数
    // 订单委托编号
    private String entrustId;

    // 盈立参数
    // 订单委托编号
//    private String entrustId;
    // 订单流水号
    private String serialNo;

    // Robbinhood
    // 订单编号
//    private String orderId;
}
