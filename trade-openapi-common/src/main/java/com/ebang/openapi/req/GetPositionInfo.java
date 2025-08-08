package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetPositionInfo extends BaseRequest {

    // IB相关参数
    // 合约编号
    @NotNull(groups = {IBKRGroup.class})
    private Integer conId;
}
