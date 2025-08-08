package com.ebang.openapi.entity;

import lombok.Data;

/**
 * @Author: zyz
 * @Date: 2025/7/9 9:06
 * @Description:
 **/
@Data
public class USmartSymbol {

    private String symbol;
    private String nameChs;
    private String nameCht;
    private String NameEn;
    // 类型 0:未知 1:股票 2:基金 3:期货 4:债券 5:衍生证券 6:指数 7:外汇 8:其他 9:板块
    private Integer type1;
    private Integer lotSize;

}
