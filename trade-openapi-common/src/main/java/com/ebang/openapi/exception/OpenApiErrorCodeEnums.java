package com.ebang.openapi.exception;

import lombok.Getter;

/**
 * @author chenlanqing 2025/7/4 13:24
 * @version 1.0.0
 */
@Getter
public enum OpenApiErrorCodeEnums {

    /*******************************common******************************/
    UNAUTHORIZED(401L, "认证失败！请确认API Key是否正确"),
    SYSTEM_ERROR(500L, "系统异常"),

    UN_SUPPORT_CHANNEL(1404L, "不支持的渠道"),

    NO_PLATFORM_ACCT(1405L, "平台账户未配置"),

    INVALID_CHANNEL_CONFIG(1406L, "渠道账户未配置不合法"),
    INVALID_CALL_ROUTE(1407L, "渠道调用结果异常"),
    PARAM_ERROR(1400L, "参数错误"),

    /*******************************robin hood******************************/
    UN_SUPPORT_MFA_ACCOUNT(1500L, "robinhood 不支持mfa账户认证"),

    /*******************************ibkr******************************/
    ACCOUNT_NOT_EXISTS(1600L, "账户不存在"),
    ORDER_ID_NOT_EXISTS(1601L, "订单不存在"),

    /*******************************tiger******************************/

    REQUIRED_SYMBOL(1700L, "Symbol 参数不能为空"),
    ;
    private final long errorCode;
    private final String message;

    OpenApiErrorCodeEnums(long errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
