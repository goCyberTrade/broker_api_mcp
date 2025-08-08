package com.ebang.openapi.util;


import com.ebang.openapi.config.USmartConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(RSAUtil.class);

    /**
     * 初始化publicKey和privateKey对象
     */
//    @PostConstruct
//    public void init() throws Exception {
//        System.out.println(".............................");
//        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY.getBytes()));
//        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(PRIVATE_KEY.getBytes()));
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        publicKey = keyFactory.generatePublic(publicKeySpec);
//        privateKey = keyFactory.generatePrivate(privateKeySpec);
//    }


    /**
     * 加密
     * encryptstr->URLSAFE_BASE64(RSA(str))
     *
     * @paramstr
     * @returnifnoexceptionreturnURLSAFE_BASE64(RSA(str)),elsereturnnull
     */
    public static String encrypt(String str,String publicKeyStr) {
        try {
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Cipher encoder = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            encoder.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] rsa = encoder.doFinal(str.getBytes());
            String base64 = Base64.getUrlEncoder().encodeToString(rsa);
            return base64;
        } catch (Exception e) {
            LOGGER.error("encryptstr:{}failed,exceptionis", str, e);
        }
        return null;
    }


    /**
     * *交易签名
     * *
     * *@paramdata待签名数据
     * *@return交易签名
     */
    public static String sign(String data, String privateKeyStr) throws Exception {

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr.getBytes()));
        KeyFactory keyFactory1 = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory1.generatePrivate(privateKeySpec);


        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(key);
        signature.update(data.getBytes());
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(signature.sign()));
    }

}