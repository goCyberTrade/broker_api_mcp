package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SignaturesOwnersReq extends BaseRequest {

    /**
     * 账户id
     */
    @NotBlank(groups = {ValidationUtil.IBKRGroup.class})
    private String accountId;

}
