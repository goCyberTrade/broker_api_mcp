package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenlanqing 2025/7/10 15:22
 * @version 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TodayTransactionReq extends BaseRequest{

    /**
     * 长桥证券：股票代码，使用 ticker.region 格式，例如：AAPL.US
     */
    private String symbol;
    /**
     * 长桥证券：订单 ID，用于指定订单 ID 查询，例如：701276261045858304
     */
    private String orderId;

    /**
     * 华盛通证券：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通
     */
    private String exchangeType;

    /**
     * 华盛通证券：每页返回数量
     */
    private Integer limit;

    /**
     * 华盛通证券：初始值为0 开始，分页时传上一页最后一条数据的queryParamStr
     */
    private String queryParamStr;

    /**
     * 富途证券：1-香港市场、2-美国市场、3-A股市场（仅模拟交易）、5-期货市场（环球期货）、113-香港基金市场、123-美国基金市场
     */
    private Integer market;
    /**
     * 富途证券：成交记录 id，多个使用逗号分割
     */
    private String idList;
}
