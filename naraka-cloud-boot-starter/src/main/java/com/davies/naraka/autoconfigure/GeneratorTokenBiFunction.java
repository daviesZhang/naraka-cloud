package com.davies.naraka.autoconfigure;

import java.util.Date;
import java.util.function.BiFunction;

/**
 * 根据用户名,签发人,返回一个token
 * @author davies
 * @date 2022/2/27 9:11 PM
 */
public interface GeneratorTokenBiFunction extends BiFunction<String, String, String>{
}
