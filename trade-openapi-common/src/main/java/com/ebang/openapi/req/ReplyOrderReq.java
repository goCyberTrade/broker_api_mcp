package com.ebang.openapi.req;

import com.ebang.openapi.utils.SnowflakeIdGenerator;
import com.longport.trade.OrderSide;
import com.longport.trade.OrderType;
import com.longport.trade.OutsideRTH;
import com.longport.trade.TimeInForceType;
import com.tigerbrokers.stock.openapi.client.struct.TagValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReplyOrderReq extends BaseRequest {


    // IB相关参数
    // 订单抑制唯一标识
    private String id;
}
