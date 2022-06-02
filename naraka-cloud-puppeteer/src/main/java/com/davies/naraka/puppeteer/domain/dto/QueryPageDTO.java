package com.davies.naraka.puppeteer.domain.dto;


import com.davies.naraka.autoconfigure.QueryPage;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author davies
 * @date 2022/2/7 12:57 PM
 */

@Data
public class QueryPageDTO<T> implements QueryPage<T> {


    /**
     * 每页显示条数，默认 10
     */
    @NotNull
    private Integer size;

    /**
     * 当前页
     */
    @NotNull
    private Integer current;

    /**
     * 查询条件
     */

    private T query;


}
