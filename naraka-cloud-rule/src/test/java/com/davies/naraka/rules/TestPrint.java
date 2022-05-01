package com.davies.naraka.rules;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Base64;

/**
 * @author davies
 * @date 2022/4/18 15:25
 */
@Slf4j
public class TestPrint {


    @Test
    public void testPrint(){
        String intet = String.valueOf(BigDecimal.valueOf(70).compareTo(BigDecimal.valueOf(80)));
        System.out.println(intet);

    }

    @Test
    public void println(){
        // 客户ID
        final String customerKey = "facd24d7dfb34b56b7abf19556401927";
        // 客户密钥
        final String customerSecret = "118d1fb5dae746009d9c908f86403bf9";
        // 拼接客户 ID 和客户密钥并使用 base64 编码
        String plainCredentials = customerKey + ":" + customerSecret;
        String base64Credentials = new String(Base64.getEncoder().encode(plainCredentials.getBytes()));
        log.info(base64Credentials);
    }
}
