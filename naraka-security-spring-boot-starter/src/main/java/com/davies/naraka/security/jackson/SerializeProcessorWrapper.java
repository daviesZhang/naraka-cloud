package com.davies.naraka.security.jackson;


import com.davies.naraka.cloud.common.StringUtils;
import com.davies.naraka.cloud.common.enums.AuthorityProcessorType;

import java.util.function.Function;

/**
 * @author davies
 * @date 2022/1/27 12:34 PM
 */
public class SerializeProcessorWrapper {


    public static SerializeProcessor getSerializeProcessor(AuthorityProcessorType currentType, Function<String, String> previous) {
        return new SerializeProcessor(processor(currentType), previous);
    }


    private static Function<String, String> processor(AuthorityProcessorType processorType) {
        switch (processorType) {
            case DESENSITIZATION:
            default:
                return StringUtils::desensitization;
        }
    }
}
