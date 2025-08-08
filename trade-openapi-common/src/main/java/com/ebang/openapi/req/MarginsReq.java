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
public class MarginsReq extends BaseRequest {

    /**
     * 账户id
     */
    @NotBlank(groups = {ValidationUtil.IBKRGroup.class})
    private String accountId;
    /**
     * 盈利字段，交易类别(0-香港,5-美股,67-A股)
     */
    @NotBlank(groups = {ValidationUtil.USmartGroup.class})
    private int exchangeType;
}
