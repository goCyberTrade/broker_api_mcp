package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询标的是否支持盘前盘后交易的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TradeQueryBeforeAndAfterSupportReq extends BaseRequest{

//====================================HSTONG
    /**
     * 交易类型	'K'-港股、'P'-美股、'v'-深股通、't'-沪股通
     */
    private String exchangeType;

    /**
     * 证券代码
     */
    private String stockCode;
}
