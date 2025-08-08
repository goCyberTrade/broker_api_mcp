package com.ebang.openapi.req;

import com.ebang.openapi.utils.MapConvert;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取客户ipo申购列表-分页查询的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IpoRecordListReq extends BaseRequest{
//===========================================usmart

    /**
     * 认购开始时间，非必填，类型为 string
     * 格式要求：yyyy-MM-dd HH:mm:ss ，用于筛选认购开始时间大于等于该值的记录
     */
    @MapConvert(true)
    private String applyTimeMin;

    /**
     * 认购结束时间，非必填，类型为 string
     * 格式要求：yyyy-MM-dd HH:mm:ss ，用于筛选认购结束时间小于等于该值的记录
     */
    @MapConvert(true)
    private String applyTimeMax;

}