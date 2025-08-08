package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenlanqing 2025/7/6 15:11
 * @version 1.0.0
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class PortfolioAllocationReq extends BaseRequest{
    private String accountId;
}
