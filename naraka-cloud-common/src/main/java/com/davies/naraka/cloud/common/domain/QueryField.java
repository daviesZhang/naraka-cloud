package com.davies.naraka.cloud.common.domain;


import com.davies.naraka.cloud.common.enums.QueryFilterType;

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

public class QueryField<T> {

    private QueryFilterType type;

    private T filter;


    public QueryFilterType getType() {
        return type;
    }

    public void setType(QueryFilterType type) {
        this.type = type;
    }

    public T getFilter() {
        return filter;
    }

    public void setFilter(T filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "QueryField{" +
                "type=" + type +
                ", filter=" + filter +
                '}';
    }
}
