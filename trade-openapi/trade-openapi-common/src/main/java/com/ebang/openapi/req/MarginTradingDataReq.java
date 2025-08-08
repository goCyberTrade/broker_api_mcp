package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询股票融资融券信息的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MarginTradingDataReq extends BaseRequest{

//====================================HSTONG
    /**
     * 股票类型，为必填项
     * 值需参考对应的数据字典中股票类型的定义，用于区分不同种类的股票
     */
    @NotBlank(groups = {ValidationUtil.HStongGroup.class})
    private String dataType;

    /**
     * 股票代码，为必填项
     * 用于唯一标识具体的股票，不同市场、不同股票有各自对应的代码
     */
    @NotBlank(groups = {ValidationUtil.HStongGroup.class})
    private String stockCode;

//========================================futu

    /**
     * 账户ID
     */
    @NotNull(groups = {ValidationUtil.FutuGroup.class})
    private Long accId;
    /**
     * 市场
     */
    @NotNull(groups = {ValidationUtil.FutuGroup.class})
    private Integer market;
    /**
     * 代码
     */
    @NotBlank(groups = {ValidationUtil.FutuGroup.class})
    private String code;

}
