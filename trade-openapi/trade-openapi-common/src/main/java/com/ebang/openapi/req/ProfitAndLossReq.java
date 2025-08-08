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
public class ProfitAndLossReq extends BaseRequest {

    /**
     * 账户id
     */
    @NotBlank(groups = {ValidationUtil.IBKRGroup.class})
    private String accountId;

}
