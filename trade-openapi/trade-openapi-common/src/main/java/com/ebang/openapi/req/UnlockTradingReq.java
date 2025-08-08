package com.ebang.openapi.req;

import com.ebang.openapi.utils.ValidationUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 解锁交易的请求类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnlockTradingReq extends BaseRequest{

//=================================HStong usmart
    /**
     * 交易密码需要使用AES加密（ECB/PKCSPadding），AES秘钥为"m+qS04/2CH1OweCnmXZ3TDZkCQS+hBzY"，需要进行Base64处理
     * 如密码为"123456"，加密步骤：Base64.Encode(AES.Encrypt(Base64.Decode("m+qS04/2CH1OweCnmXZ3TDZkCQS+hBzY"), "123456"))，加密后密码："W1U8iZIppSE+mBMtzy9vZQ=="
     */
//    @NotBlank(groups = {ValidationUtil.HStongGroup.class})
//    private String password;

//=====================================FUTU

    /**
     * true 解锁交易，false 锁定交易
     */
    private boolean unlock;
    
    /**
     * 交易密码的 32 位 MD5 加密（全小写），解锁交易必须要填密码，锁定交易不需要验证密码，可不填
     */
   // private String pwdMD5;
}
