package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 交易条件过滤类
 */
@Data
public class TrdFilterConditions {

    /**
     * 代码过滤，只返回包含这些代码的数据，没传不过滤
     */
    private List<String> codeList;
    /**
     * ID 主键过滤，只返回包含这些 ID 的数据，没传不过滤，订单是 orderID、成交是 fillID、持仓是 positionID
     */
    private List<Long> idList;
    /**
     * 开始时间，严格按 YYYY-MM-DD HH:MM:SS 或 YYYY-MM-DD HH:MM:SS.MS 格式传，对持仓无效，拉历史数据必须填
     */
    private String beginTime;
    /**
     * 结束时间，严格按 YYYY-MM-DD HH:MM:SS 或 YYYY-MM-DD HH:MM:SS.MS 格式传，对持仓无效，拉历史数据必须填
     */
    private String endTime;
    /**
     * 服务器订单ID列表，可以用来替代orderID列表，二选一
     */
    private List<String> orderIDExList;
    /**
     * 指定交易市场
     * @see com.futu.openapi.pb.TrdCommon.TrdMarket
     */
    private Integer filterMarket;



}
