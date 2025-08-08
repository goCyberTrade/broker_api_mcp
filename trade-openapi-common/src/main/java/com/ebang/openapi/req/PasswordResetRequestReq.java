package com.ebang.openapi.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 重置密码请求的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordResetRequestReq extends BaseRequest{
//===========================================usmart

    /**
     * 邮箱，必填，类型为 String
     * 描述为：注册时使用的邮箱地址（Address you registered with）
     */
    @NotBlank
    private String email;


}