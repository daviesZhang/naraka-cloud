package com.davies.naraka.admin.domain.dto.system;

import com.davies.naraka.admin.annotation.QueryFilter;
import com.davies.naraka.cloud.common.domain.QueryField;
import com.davies.naraka.cloud.common.enums.QueryFilterType;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author davies
 * @date 2022/2/7 1:41 PM
 */
@Data
public class RoleQueryDTO {
    @QueryFilter(types = {
            QueryFilterType.EQUALS,
            QueryFilterType.LIKE})
    private QueryField<String> name;

    @QueryFilter(types = {
            QueryFilterType.LESSTHAN,
            QueryFilterType.EQUALS,
            QueryFilterType.LESSTHANEQUAL,
            QueryFilterType.GREATERTHANE,
            QueryFilterType.GREATERTHANEQUAL})
    private List<QueryField<LocalDateTime>> createdTime;

}
