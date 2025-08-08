package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenlanqing 2025/7/6 15:12
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InstrumentPositionReq extends BaseRequest{

    private String accountId;

    /**
     * 合约 ID
     */
    private String contactId;
}
