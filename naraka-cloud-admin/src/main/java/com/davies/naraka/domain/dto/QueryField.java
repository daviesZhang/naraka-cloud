package com.davies.naraka.domain.dto;

import com.davies.naraka.domain.enums.QueryFilterType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QueryUserDTO 查询例子
 * {"username": {
 * "type": "LIKE",
 * "filter": "e"},
 * "status": {
 * "type": "CONTAINS",
 * "filter":[1,0]},
 * "passwordExpireTime": {
 * "type": "LESSTHANEQUAL",
 * "filter":"2055-09-01 11:11:11"},
 * "createdTime": [
 * { "type": "GREATERTHANEQUAL",
 * "filter":"2021-09-01 11:11:11"},{
 * "type": "LESSTHANEQUAL",
 * "filter":"2022-01-28 11:11:11"
 * }]
 * }
 *
 * @author davies
 * @date 2022/1/30 10:06 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryField<T> {

    private QueryFilterType type;

    private T filter;



}
