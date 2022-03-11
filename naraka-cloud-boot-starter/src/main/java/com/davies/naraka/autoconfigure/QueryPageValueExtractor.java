package com.davies.naraka.autoconfigure;


import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.ValueExtractor;

/**
 * @author davies
 * @date 2022/3/11 8:31 PM
 */
public class QueryPageValueExtractor implements ValueExtractor<QueryPage<@ExtractedValue ?>> {


    @Override
    public void extractValues(QueryPage<?> queryPage, ValueReceiver valueReceiver) {
        valueReceiver.value("query", queryPage.getQuery());
    }
}
