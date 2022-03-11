package com.davies.naraka.autoconfigure;


import com.davies.naraka.autoconfigure.domain.QueryField;

import javax.validation.valueextraction.ExtractedValue;
import javax.validation.valueextraction.ValueExtractor;

/**
 * @author davies
 * @date 2022/3/11 8:31 PM
 */
public class QueryFieldValueExtractor implements ValueExtractor<QueryField<@ExtractedValue ?>> {


    @Override
    public void extractValues(QueryField<?> queryPage, ValueReceiver valueReceiver) {
        valueReceiver.value("filter", queryPage.getFilter());
    }
}
