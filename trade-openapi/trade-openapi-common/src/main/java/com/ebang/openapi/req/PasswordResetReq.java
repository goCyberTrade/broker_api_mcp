package com.ebang.openapi.req;

import com.ebang.openapi.utils.MapConvert;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 重置密码的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordResetReq extends BaseRequest{
//===========================================Robinhood

    /**
     * 用户名，必填，类型为 String
     * 与电子邮件地址关联的用户名
     */
    @NotBlank
    private String username;

    /**
     * 新密码，必填，类型为 String
     * 要设置的新密码
     */
    @NotBlank
    private String password;


}