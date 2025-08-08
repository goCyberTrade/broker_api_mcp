package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenlanqing 2025/7/6 15:12
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountPerformanceReq extends BaseRequest{

    /**
     * 账号 id，多个使用逗号分割
     */
    private String accountId;
    /**
     * 老虎证券：起始日期， 格式 yyyy-MM-dd, 如 '2022-01-01'。如不传则使用end_date往前30天的日期
     */
    private String startDate;
    /**
     * 老虎证券：截止日期， 格式 yyyy-MM-dd, 如 '2022-02-01'。如不传则使用当前日期
     */
    private String endDate;
    /**
     * 老虎证券：账户划分类型, 可选值有: SegmentType.SEC 代表证券; SegmentType.FUT 代表期货， 可以从 tigeropen.common.consts.SegmentType 下导入
     */
    private String segType;
    /**
     * 老虎证券：币种，包括 ALL/USD/HKD/CNH 等, 可以从 tigeropen.common.consts.Currency 下导入
     */
    private String currency;

    /**
     * 盈立证券：交易类别(0-香港,5-美股, 67-A股，100-查询所有交易类别)
     */
    private String exchangeType;
}
