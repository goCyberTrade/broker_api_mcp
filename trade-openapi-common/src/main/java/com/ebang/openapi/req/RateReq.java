package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询汇率的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RateReq extends BaseRequest{

//====================================HSTONG
    /**
     * 0:换汇汇率, 1:即期汇率
     * 如果不传，默认查询换汇汇率
     */
    private Integer rateType;


}
