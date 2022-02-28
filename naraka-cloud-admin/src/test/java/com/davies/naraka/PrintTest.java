package com.davies.naraka;

import org.junit.jupiter.api.Test;

/**
 * @author davies
 * @date 2022/2/28 1:18 PM
 */
public class PrintTest {



    @Test
    public void regx(){

        System.out.println("/admin/test".replaceAll("/admin/(?<segment>.*)", "/${segment}"));
    }
}
