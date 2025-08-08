package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccountSummaryReq extends BaseRequest {

    /**
     * 账户id
     */
    /**
     * 账户id
     */
    @NotBlank(groups = {ValidationUtil.IBKRGroup.class})
    private String accountId;

    /**
     * tiger 渠道使用字段，是否返回按照品种（证券、期货）分类的数据，默认 False，为True时，返回一个dict，C表示期货， S表示股票
     */
    private boolean segment = false;

    /**
     * tiger 渠道使用字段，是否返回按照币种（美元、港币、人民币）分类的数据，默认为 False
     */
    private boolean marketValue = false;

    /**
     * 交易市场类型-华盛通参数
     *
     * @see com.huasheng.quant.open.gateway.sdk.constant.trade.ExchangeType
     * 盈利字段，交易类别(0-香港,5-美股,67-A股)
     */
    /**
     * 账户id
     */
    @NotBlank(groups = {ValidationUtil.USmartGroup.class})
    private String exchangeType;
}
