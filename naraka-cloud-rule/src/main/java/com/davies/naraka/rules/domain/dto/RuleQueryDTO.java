package com.davies.naraka.rules.domain.dto;

import com.davies.naraka.autoconfigure.annotation.QueryFilter;
import com.davies.naraka.autoconfigure.domain.QueryField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.davies.naraka.autoconfigure.enums.QueryFilterType.*;

/**
 * @author davies
 * @date 2022/3/19 19:30
 */
@Data
public class RuleQueryDTO {


    private Long id;


    private String project;


    @QueryFilter(types = {LIKE, EQ})
    private QueryField<String> name;


    private String createdBy;

    @QueryFilter(types = {ASC, DESC, LE, GE})
    private List<QueryField<LocalDateTime>> createdTime;

    private String updatedBy;

    @QueryFilter(types = {ASC, DESC})
    private QueryField<LocalDateTime> updatedTime;
}
