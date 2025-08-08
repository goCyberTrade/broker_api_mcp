package com.ebang.openapi.req;

import com.ebang.openapi.utils.SnowflakeIdGenerator;
import com.ebang.openapi.utils.ValidationUtil.*;
import com.tigerbrokers.stock.openapi.client.struct.TagValue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class CancelOrderReq extends BaseRequest {


    // IB相关参数
    // 订单编号
    @NotNull(groups = {IBKRGroup.class, FutuGroup.class})
    private String orderId;
    // 账户ID
    @NotNull(groups = {IBKRGroup.class})
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
    // 订单号，forAll 为 true 时，传0
//    private String orderId;
    // 是否对此业务账户的全部订单操作，true：对全部订单，false：对单个订单，不传默认为对单个订单。批量操作仅支持全部撤单，不支持全部生效、全部失效、全部删除。
    private Boolean forAll;
    // 表示服务器订单id，可以用来代替orderID，和orderID二选一
    private String orderIDEx;


    // 长桥参数
    // 订单编号
//    private String orderId;

    // 老虎参数
    // 用户授权账户 必填
    @NotEmpty(groups = {TigerGroup.class})
    private String account;
    // 订单编号
    private Long id;
    @NotNull(groups = {TigerGroup.class})
    // 机构用户专用 - 交易员密钥，选填
    private String secretKey;

    // 微牛参数
    // 账户ID
//    private String accountId;
    // 客户端订单ID
    private String clientOrderId;

    // 华盛通参数
    // 原始委托编号
    private String entrustId;

    // 盈立证券
    // 委托编号
//    private String entrustId;
    // 交易密码（RSA公钥加密）
    private String password;
    // 碎股委托编号(碎股撤单使用)
    private String oddId;
}
