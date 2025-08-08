package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取保证金比例的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MarginRatioReq extends BaseRequest{
//===========================================longport
    /**
     * YES	股票代码，使用 ticker.region 格式，例如：AAPL.US
     */
    @NotBlank
    private String symbol;

}
