package com.davies.naraka.system.domain.dto;

import com.davies.naraka.autoconfigure.annotation.QueryConfig;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import lombok.Data;

/**
 * @author davies
 * @date 2022/6/5 22:33
 */
@Data
public class RoleQueryDTO {


    @QueryConfig(filterType = QueryFilterType.STARTS_WITH)
    private String code;


    private QueryField<String> name;
}
