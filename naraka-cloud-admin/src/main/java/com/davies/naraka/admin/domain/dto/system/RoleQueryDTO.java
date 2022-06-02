package com.davies.naraka.admin.domain.dto.system;

import com.davies.naraka.autoconfigure.annotation.QueryFilter;
import com.davies.naraka.autoconfigure.domain.QueryField;
import com.davies.naraka.autoconfigure.enums.QueryFilterType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.davies.naraka.autoconfigure.enums.QueryFilterType.*;

/**
 * @author davies
 * @date 2022/2/7 1:41 PM
 */
@Data
public class RoleQueryDTO {
    @QueryFilter(types = {
            EQ,
            QueryFilterType.LIKE})
    private QueryField<String> name;

    @QueryFilter(types = {LT, EQ, LE, GT, GE})
    private List<QueryField<LocalDateTime>> createdTime;

}
