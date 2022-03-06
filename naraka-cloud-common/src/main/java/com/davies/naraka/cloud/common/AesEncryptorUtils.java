package com.davies.naraka.cloud.common;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 数据AES加解密工具类
 * @author davies
 * @date 2022/1/31 11:18 AM
 */
public class AesEncryptorUtils {




    public static String encrypt(String value, String strKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] keyBytes = Arrays.copyOf(strKey.getBytes(Charsets.UTF_8), 16);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cleartext = value.getBytes(Charsets.UTF_8);
        byte[] ciphertextBytes = cipher.doFinal(cleartext);

        return BaseEncoding.base16().lowerCase().encode(ciphertextBytes);

    }


    /**
     * @param value
     * @param strKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String decrypt(String value, String strKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] keyBytes = Arrays.copyOf(strKey.getBytes(Charsets.UTF_8), 16);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] ciphertextBytes = cipher.doFinal(BaseEncoding.base16().lowerCase().decode(value));
        return new String(ciphertextBytes);
    }

}
