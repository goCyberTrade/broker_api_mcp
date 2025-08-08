package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 获取资金流水的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CashFlowSummaryReq extends BaseRequest{
//===========================================longport
    /**
     * 开始时间，例如：2025-05-10 00:00:00，为必填项
     */
    @NotBlank(groups = {ValidationUtil.LongportGroup.class})
    private String startTime;
    /**
     * 结束时间，例如：2025-05-10 00:00:00，为必填项
     */
    @NotBlank(groups = {ValidationUtil.LongportGroup.class})
    private String endTime;
    /**
     * 资金类型，可选值：1 - 现金、2 - 股票、3 - 基金，非必填项
     */
    private Integer businessType;
    /**
     * 标的代码，例如：AAPL.US，非必填项
     */
    private String symbol;


//==================================HSTONG  查询当日资金流水
    /**
     * 交易类型，为必填项
     * 可能值：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通
     * 用于标识要查询数据所属的交易市场类型
     */
    @NotBlank(groups = {ValidationUtil.HStongGroup.class})
    private String exchangeType;

//==================================HSTONG  查询历史资金流水

    /**
     * 起始日期，为必填项
     * 格式要求为：yyyyMMdd
     * 用于限定查询数据的起始时间范围
     */
    private Integer startDate;

    /**
     * 结束日期，为必填项
     * 格式要求为：yyyyMMdd
     * 用于限定查询数据的结束时间范围
     */
    private Integer endDate;

//===================================futu
    /**
     * 账户ID
     */
    @NotNull(groups = {ValidationUtil.FutuGroup.class})
    private Long accId;

    /**
     * 市场,为必填项
     */
    @NotBlank(groups = {ValidationUtil.FutuGroup.class})
    private Integer market;

    /**
     * 清算日期，为必填项
     * 格式要求为 "yyyy-MM-dd"，如 "2017-05-20"
     * 用于指定交易清算的具体日期
     */
    @NotBlank(groups = {ValidationUtil.FutuGroup.class})
    private String clearingDate;

    /**
     * 现金流方向，为选填项
     * 参见 TrdCashFlowDirection 的枚举定义，用于指定现金流的方向
     */
    private Integer cashFlowDirection;

}
