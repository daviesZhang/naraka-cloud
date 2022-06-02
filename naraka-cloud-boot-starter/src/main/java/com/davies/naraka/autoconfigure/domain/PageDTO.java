package com.davies.naraka.autoconfigure.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author davies
 * @date 2022/2/7 1:13 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {

    private Integer current;

    private Integer total;

    private Integer size;

    private List<T> items;

    private Map<String, Object> statistics = new HashMap<>();


    public PageDTO(Integer current, Integer total, Integer size, List<T> items) {
        this.current = current;
        this.total = total;
        this.size = size;
        this.items = items;
    }

    public void put(String key, Object value) {
        this.statistics.put(key, value);
    }
}
