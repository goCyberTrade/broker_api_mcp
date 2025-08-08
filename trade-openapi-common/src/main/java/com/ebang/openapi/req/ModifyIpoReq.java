package com.ebang.openapi.req;

import com.ebang.openapi.utils.MapConvert;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * ipo改单/撤单的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ModifyIpoReq extends BaseRequest{
//===========================================usmart

    /**
     * 操作类型，必填，类型为 int32
     * 可选值：0-改单，1-撤单
     * 标识当前操作是改单还是撤单
     */
    @NotNull
    @MapConvert(true)
    private Integer actionType;

    /**
     * 认购记录 ID，必填，类型为 int64
     * 关联要操作的新股认购记录
     */
    @NotNull
    @MapConvert(true)
    private Long applyId;

    /**
     * 认购数量，必填，类型为 number
     * 改单或撤单涉及的认购数量
     */
    @NotNull
    @MapConvert(true)
    private BigDecimal applyQuantity;

    /**
     * 认购现金，非必填，类型为 number
     * 规则：改融资认购单时必填
     * 改单场景下（融资认购改单）需填写的现金金额
     */
    @MapConvert(true)
    private BigDecimal cash;


}
