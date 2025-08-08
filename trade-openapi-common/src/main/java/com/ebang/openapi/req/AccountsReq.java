package com.ebang.openapi.req;

import lombok.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccountsReq extends BaseRequest {

    /**
     * 账户id
     */
    private String accountId;

}
