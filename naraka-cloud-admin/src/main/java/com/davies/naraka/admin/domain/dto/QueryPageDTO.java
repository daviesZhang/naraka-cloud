package com.davies.naraka.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author davies
 * @date 2022/2/7 12:57 PM
 */
@Data
public class QueryPageDTO<T> {


    /**
     * 每页显示条数，默认 10
     */
    @NotNull
    private Long size;

    /**
     * 当前页
     */
    @NotNull
    private Long current;

    /**
     * 查询条件
     */
    @NotNull
    private T query;
}
