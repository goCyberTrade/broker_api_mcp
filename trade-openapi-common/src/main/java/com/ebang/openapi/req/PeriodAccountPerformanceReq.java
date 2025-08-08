package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenlanqing 2025/7/6 15:12
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PeriodAccountPerformanceReq extends BaseRequest{

    /**
     * 账号 id，多个使用逗号分割
     */
    private String accountId;

    /**
     * IB: 周期：
     * Enum: "1D" "7D" "MTD" "1M" "3M" "6M" "12M" "YTD"
     * Specify the period for which the account should be analyzed. Available period lengths:
     * <li>1D - The last 24 hours.
     * <li>7D - The last 7 full days.
     * <li>MTD - Performance since the 1st of the month.
     * <li>1M - A full calendar month from the last full trade day.
     * <li>3M - 3 full calendar months from the last full trade day.
     * <li>6M - 6 full calendar months from the last full trade day.
     * <li>12M - 12 full calendar month from the last full trade day.
     * <li>YTD - Performance since January 1st.
     */
    private String period;
}
