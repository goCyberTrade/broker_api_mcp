package com.ebang.openapi.req;

import com.ebang.openapi.utils.MapConvert;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取客户ipo申购明细的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IpoRecordReq extends BaseRequest{
//===========================================usmart

    /**
     * 申购编号，非必填，类型为 int64
     * 规则：与 serialNo 传其中一个即可，用于通过申购编号查询
     */
    @MapConvert(true)
    private Long applyId;

    /**
     * 流水号，非必填，类型为 int64
     * 规则：与 applyId 传其中一个即可，用于通过流水号查询
     */
    @MapConvert(true)
    private Long serialNo;

}