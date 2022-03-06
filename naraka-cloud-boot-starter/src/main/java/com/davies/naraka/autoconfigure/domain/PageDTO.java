package com.davies.naraka.autoconfigure.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author davies
 * @date 2022/2/7 1:13 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {

    private Long current;

    private Long total;

    private Long size;

    private List<T> items;


}
