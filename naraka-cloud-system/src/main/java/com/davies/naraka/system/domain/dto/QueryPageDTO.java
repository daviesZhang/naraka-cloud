package com.davies.naraka.system.domain.dto;

import com.davies.naraka.autoconfigure.QueryPage;
import lombok.Data;

/**
 * @author davies
 * @date 2022/5/31 09:07
 */
@Data
public class QueryPageDTO<T> implements QueryPage<T> {


    private Integer size;
    private Integer current;


    private T query;


    public Integer getCurrent() {
        return current - 1;
    }

    public Integer getSourceCurrent() {
        return current;
    }
}
