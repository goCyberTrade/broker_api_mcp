package com.ebang.openapi.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.ebang.openapi.utils.ValidationUtil.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderFeesReq extends BaseRequest {

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
    @NotNull(groups = {FutuGroup.class})
    private String orderId;
}
