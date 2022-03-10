package com.davies.naraka.puppeteer;

import com.davies.naraka.autoconfigure.ProcessorFunction;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author davies
 */
@Component
public class CustomProcessorFunction implements ProcessorFunction {
    @Override
    public Map<String, Set<String>> apply(String s) {
        return Maps.newHashMap();
    }

}
