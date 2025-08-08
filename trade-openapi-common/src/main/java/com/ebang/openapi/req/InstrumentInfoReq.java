package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import com.tigerbrokers.stock.openapi.client.struct.enums.ActionType;
import com.tigerbrokers.stock.openapi.client.struct.enums.OrderType;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import com.tigerbrokers.stock.openapi.client.struct.enums.SegmentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 根据symbol等信息查询交易标的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InstrumentInfoReq extends BaseRequest{
//=========================================webull
    /**
     * 标的代码，为必填项，示例值如：SPX
     */
    @NotNull(groups = {ValidationUtil.WebullGroup.class})
    private String symbol;

    /**
     * 市场，为必填项，示例值如：HK ，可参考 Markets 相关定义
     */
    private String market;

    /**
     * 标的大类，为必填项，示例值如：EQUITY; OPTION
     */
    private String instrumentSuperType;

    /**
     * 标的二级分类，非必填项，但查询期权信息时必传
     * 示例值如：CALL_OPTION-看涨期权；PUT_OPTION-看跌期权
     */
    private String instrumentType;

    /**
     * 期权行权价，非必填项，但查询期权信息时必传，示例值如：3400
     */
    private String strikePrice;

    /**
     * 期权失效日期，格式 yyyy-MM-dd ，非必填项，但查询期权信息时必传
     * 示例值如：2024-12-20
     */
    private String initExpDate;

    /**
     * 标的id
     */
    private String instrumentId;
}
