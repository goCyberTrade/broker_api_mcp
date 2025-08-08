package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 根据symbol等信息查询交易标的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetSymbolReq extends BaseRequest{
//=========================================webull

    /**
     * 标的id
     */
    @NotNull(groups = {ValidationUtil.WebullGroup.class})
    private String instrumentId;
}
