package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BalanceReq extends BaseRequest {

    /**
     * 账户id
     */
    @NotBlank(groups = {ValidationUtil.IBKRGroup.class,ValidationUtil.WebullGroup.class})
    private String accountId;
    //币种，默认HKD
    @NotBlank(groups = {ValidationUtil.WebullGroup.class})
    private String currency;
    //盈利特有字段，交易类别(0-香港,5-美股,67-A股)
    @NotBlank(groups = {ValidationUtil.USmartGroup.class})
    private int exchangeType;
}
