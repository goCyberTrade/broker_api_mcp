package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenlanqing 2025/7/6 15:12
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HistoryTransactionReq extends BaseRequest {

    /**
     * 华盛通证券：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通
     * <p>
     * 盈立证券：交易类别(0-香港,5-美股, 67-A股，100-查询所有交易类别)
     */
    private String exchangeType;

    /**
     * IB: 账号 id，多个使用逗号分割
     */
    private String accountId;
    /**
     * IB: 合约 id，多个使用逗号分割
     */
    private String contractId;
    /**
     * 币种
     */
    private String currency;
    /**
     * 指定历史交易数据的天数
     */
    private Integer days;

    /**
     * 长桥证券：股票代码，使用 ticker.region 格式，例如：AAPL.US
     * <p>
     * 老虎证券：股票代码。order_id 和 symbol其中一个必传。
     * <p>
     * 盈立证券：股票代码
     */
    private String symbol;

    /**
     * 长桥证券：开始时间，yyyy-MM-dd HH:mm:ss。开始时间为空时，默认为结束时间或当前时间前九十天
     * <p>
     * 老虎证券：起始时间（yyyy-MM-dd HH-mm-ss）
     * <p>
     * 华盛通证券：起始日期	格式为：yyyyMMdd
     * <p>
     * 富途证券：起始时间，严格按照 yyyy-MM-dd HH:mm:ss
     */
    private String startDate;

    /**
     * 长桥证券：结束时间，yyyy-MM-dd HH:mm:ss。结束时间为空时，默认为开始时间后九十天或当前时间。
     * <p>
     * 老虎证券：截止时间（yyyy-MM-dd HH-mm-ss格式需要转换为毫秒的时间戳）
     * <p>
     * 华盛通证券：结束日期	格式为：yyyyMMdd
     * <p>
     * 富途证券：起始时间，严格按照 yyyy-MM-dd HH:mm:ss
     */
    private String endDate;

    /**
     * 老虎证券：下单成功后返回的全局订单ID，非本地订单ID。 order_id 和 symbol其中一个必传。 使用orderId后，symbol参数不生效
     */
    private String orderId;

    /**
     * 老虎证券：STK:股票/FUT:期货/OPT:期权/WAR:窝轮/IOPT:牛熊证, 未指定查全部。指定symbol查询时必传
     */
    private String secType;
    /**
     * 老虎证券：sect_type为OPT/WAR/IOPT类型时必传，到期日
     */
    private String expiry;
    /**
     * 老虎证券：sect_type为OPT/WAR/IOPT类型时必传，CALL/PUT
     */
    private String right;

    /**
     * 华盛通证券：每页返回数量
     * <p>
     * 盈立证券：每页结果数，默认值10
     */
    private Integer limit;

    /**
     * 华盛通证券：初始值为0 开始，分页时传上一页最后一条数据的queryParamStr
     */
    private String queryParamStr;

    /**
     * 盈立证券：当前页 1开始，默认值1
     */
    private Integer pageNum;

    /**
     * 富途证券：1-香港市场、2-美国市场、3-A股市场（仅模拟交易）、5-期货市场（环球期货）、113-香港基金市场、123-美国基金市场
     */
    private Integer market;
    /**
     * 富途证券：成交记录 id，多个使用逗号分割
     */
    private String idList;
}
