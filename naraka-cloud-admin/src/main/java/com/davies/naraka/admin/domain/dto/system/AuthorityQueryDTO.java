package com.davies.naraka.admin.domain.dto.system;


import com.davies.naraka.cloud.common.annotation.QueryFilter;
import com.davies.naraka.cloud.common.enums.AuthorityProcessorType;
import com.davies.naraka.admin.domain.enums.ResourceType;
import com.davies.naraka.cloud.common.domain.QueryField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.davies.naraka.cloud.common.enums.QueryFilterType.*;

/**
 * @author davies
 * @date 2022/2/10 5:36 PM
 */
@Data
public class AuthorityQueryDTO {
    @QueryFilter(types = {
            ORDER_ASC,
            ORDER_DESC,
            EQUALS,
            LIKE})
    private QueryField<String> resource;


    @QueryFilter(types = {CONTAINS, ORDER_ASC,
            ORDER_DESC})
    private QueryField<List<AuthorityProcessorType>> processor;


    @QueryFilter(types = {CONTAINS, ORDER_ASC,
            ORDER_DESC})
    private QueryField<List<ResourceType>> resourceType;





    @QueryFilter(types = {STARTS_WITH,LIKE})
    private QueryField<String> processorValue;


    @QueryFilter(types = {
            ORDER_ASC,
            ORDER_DESC,
            LESSTHAN,
            EQUALS,
            LESSTHANEQUAL,
            GREATERTHANE,
            GREATERTHANEQUAL})
    private List<QueryField<LocalDateTime>> createdTime;


}
