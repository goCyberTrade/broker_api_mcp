package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenlanqing 2025/7/6 15:12
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ContractListReq extends BaseRequest{

    /**
     * 老虎证券：合约列表，1-查询单个、 2-期权/窝轮/牛熊证
     */
    private Integer queryContract;

    private String accountId;
    /**
     * 老虎证券：股票代码 如：00700 / AAPL，多个使用逗号分割
     */
    private String symbol;
    /**
     * 老虎证券：STK/OPT/FUT
     */
    private String secType;
    /**
     * 老虎证券：USD/HKD/CNH
     */
    private String currency;
    /**
     * 老虎证券：到期日 交易品种是期权时必传 yyyyMMdd
     */
    private String expiry;
    /**
     * 老虎证券：行权价 交易品种是期权时必传
     */
    private Double strike;
    /**
     * 老虎证券：CALL/PUT 交易品种是期权时必传
     */
    private String right;
    /**
     * 老虎证券：交易所 (美股 SMART 港股 SEHK 沪港通 SEHKNTL 深港通 SEHKSZSE)
     */
    private String exchange;
    /**
     * 语言支持: zh_CN,zh_TW,en_US, 默认: en_US
     */
    private String lang;
}
