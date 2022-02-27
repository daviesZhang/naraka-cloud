package com.davies.naraka.common;

import com.google.common.base.Charsets;
import org.springframework.security.crypto.codec.Hex;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author davies
 * @date 2022/1/31 11:18 AM
 */
public class AesEncryptorUtils {




    public static String encrypt(String password, String strKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] keyBytes = Arrays.copyOf(strKey.getBytes(Charsets.UTF_8), 16);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cleartext = password.getBytes(Charsets.UTF_8);
        byte[] ciphertextBytes = cipher.doFinal(cleartext);
        return new String(Hex.encode(ciphertextBytes));

    }

    /**
     *
     * @param password
     * @param strKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String decrypt(String password, String strKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] keyBytes = Arrays.copyOf(strKey.getBytes(Charsets.UTF_8), 16);
        SecretKey key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] ciphertextBytes = cipher.doFinal(Hex.decode(password));
        return new String(ciphertextBytes);
    }

}
