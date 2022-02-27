package com.davies.naraka.security;

import java.util.Date;
import java.util.function.BiFunction;

/**
 * @author davies
 * @date 2022/2/27 9:11 PM
 */
public interface GeneratorTokenBiFunction extends BiFunction<String, Date, String>{
}
