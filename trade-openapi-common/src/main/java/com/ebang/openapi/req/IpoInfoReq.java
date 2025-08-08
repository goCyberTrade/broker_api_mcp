package com.ebang.openapi.req;

import com.ebang.openapi.utils.MapConvert;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取新股详细信息的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IpoInfoReq extends BaseRequest{
//===========================================usmart

    /**
     * 市场类型，非必填，类型为 int32
     * 取值：0-HK（港股）,5-US（美股）
     * 规则：如果 ipold 不传，该字段必传，用于指定股票所属市场
     */
    @MapConvert(true)
    private Integer exchangeType;

    /**
     * 新股 ID，非必填，类型为 int64
     * 规则：与 (stockCode & exchangeType) 不能同时为空；当有值时，优先按此查询，stockCode & exchangeType 条件不生效
     * 用于通过新股 ID 精确查询
     */
    @MapConvert(true)
    private Long ipold;

    /**
     * 股票代码，非必填，类型为 string
     * 规则：如果 ipold 不传，该字段必传，用于指定要查询的股票代码
     */
    @MapConvert(true)
    private String stockCode;

}
