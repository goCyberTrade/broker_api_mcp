package com.ebang.openapi.req;

import com.ebang.openapi.utils.MapConvert;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取股票抵押比率列表的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MortgageListReq extends BaseRequest{
//===========================================usmart
    /**
     * 市场类型，非必填，类型为 int32
     * 可选值：0-港股, 5-美股, 67-A股, 100-全部
     * 用于指定要查询证券所属的市场范围
     */
    @MapConvert(true)
    private Integer exchangeType;

    /**
     * 证券代码，非必填，类型为 string
     * 用于精确查询某一证券的信息
     */
    @MapConvert(true)
    private String stockCode;

    /**
     * 状态，非必填，类型为 int32
     * 可选值：1-生效中, 0-已下架，默认值为 1
     * 用于筛选证券的状态
     */
    @MapConvert(true)
    private Integer status;



}
