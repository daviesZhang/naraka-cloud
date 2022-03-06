package com.davies.naraka.admin.domain.dto.system;


import com.davies.naraka.admin.domain.enums.AuthorityProcessorType;
import com.davies.naraka.admin.domain.enums.ResourceType;
import com.davies.naraka.autoconfigure.annotation.QueryFilter;
import com.davies.naraka.autoconfigure.annotation.QuerySkip;
import com.davies.naraka.autoconfigure.domain.QueryField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

import static com.davies.naraka.autoconfigure.enums.QueryFilterType.*;

/**
 * @author davies
 * @date 2022/2/10 5:36 PM
 */
@Data
public class RoleAuthorityQueryDTO {
    @QueryFilter(types = {
            ORDER_ASC,
            ORDER_DESC,
            EQUALS,
            LIKE})
    private QueryField<String> resource;

    @QuerySkip
    @NotBlank
    private String code;

    @QuerySkip
    private Boolean assign;

    @QueryFilter(types = {
            CONTAINS,
            NOT_CONTAINS
    })
    private QueryField<List<Integer>> id;


    @QueryFilter(types = {EQUALS, ORDER_ASC,
            ORDER_DESC})
    private QueryField<AuthorityProcessorType> processor;


    @QueryFilter(types = {EQUALS, ORDER_ASC,
            ORDER_DESC})
    private QueryField<ResourceType> resourceType;


    @QueryFilter(types = {STARTS_WITH, LIKE})
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
