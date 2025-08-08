package com.ebang.openapi.req;

import com.ebang.openapi.utils.MapConvert;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改交易密码的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateOrResetTradePasswordReq extends BaseRequest{
//===========================================usmart

    /**
     * 交易密码，必填，必须是6位纯数字且经RSA加密（与X-Sign不同秘钥）
     */
    @NotBlank
    @MapConvert(true)
    private String password;

    /**
     * 旧交易密码，非必填，必须是6位纯数字且经RSA加密（与X-Sign不同秘钥），用于密码修改场景
     */
    @MapConvert(true)
    private String oldPassword;

    /**
     * 手机验证码，非必填，根据验证码重置交易密码时必填
     */
    @MapConvert(true)
    private String phoneCaptcha;


}
