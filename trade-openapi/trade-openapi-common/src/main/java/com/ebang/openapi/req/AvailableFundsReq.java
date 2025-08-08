package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import com.tigerbrokers.stock.openapi.client.struct.enums.SegmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AvailableFundsReq extends BaseRequest {

    /**
     * 账户id
     */
    @NotBlank(groups = {ValidationUtil.IBKRGroup.class})
    private String accountId;
    /**
     * 交易市场
     * @see com.futu.openapi.pb.TrdCommon.TrdMarket
     */
    @NotNull(groups = {ValidationUtil.FutuGroup.class})
    private Integer trdMarket;

    /**
     * tiger 渠道使用字段，转出segment, FUT或SEC
     */
    private String fromSegment = SegmentType.SEC.name();

    /**
     * 币种，USD/HKD/CNH
     */
    @NotBlank(groups = {ValidationUtil.TigerGroup.class})
    private String currency;

}
