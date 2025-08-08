package com.ebang.openapi.req;

import com.ebang.openapi.utils.MapConvert;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 新股认购的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplyIpoReq extends BaseRequest{
//===========================================usmart

    /**
     * 认购数量，必填，类型为 number
     * 用于指定新股认购的数量
     */
    @NotNull
    @MapConvert(true)
    private BigDecimal applyQuantity;

    /**
     * 认购类型，必填，类型为 int32
     * 可选值：1-现金，2-融资
     * 用于标识新股认购的资金方式
     */
    @NotNull
    @MapConvert(true)
    private Integer applyType;

    /**
     * IPO 交易系统唯一编号，必填，类型为 int64
     * 用于关联具体的新股 IPO 交易
     */
    @NotNull
    @MapConvert(true)
    private Long ipold;

    /**
     * 流水号，必填，类型为 int64
     * 最长19位，需确保唯一，推荐使用雪花算法生成
     * 用于标识本次认购操作的唯一性
     */
    @NotNull
    @MapConvert(true)
    private Long serialNo;

    /**
     * 认购现金，非必填，类型为 number
     * 规则：融资认购时必填
     * 用于指定融资认购方式下的现金部分金额
     */
    @MapConvert(true)
    private BigDecimal cash;



}
