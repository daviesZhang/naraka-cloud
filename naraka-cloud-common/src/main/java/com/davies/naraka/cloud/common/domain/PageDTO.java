package com.davies.naraka.cloud.common.domain;


import java.util.List;

/**
 * @author davies
 * @date 2022/2/7 1:13 PM
 */

public class PageDTO<T> {

    private Long current;

    private Long total;

    private Long size;

    private List<T> items;


    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "PageDTO{" +
                "current=" + current +
                ", total=" + total +
                ", size=" + size +
                ", items=" + items +
                '}';
    }
}
