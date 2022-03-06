package com.davies.naraka.autoconfigure.enums;

/**
 * @author davies
 * @date 2022/1/30 10:18 AM
 */
public enum QueryFilterType {
    /**
     * 以开头 xxxx%
     */
    STARTS_WITH,
    /**
     * 以结束 %xxx
     */
    ENDS_WITH,
    /**
     * 包含
     */
    CONTAINS,
    /**
     * 不包含
     */
    NOT_CONTAINS,
    /**
     * 模糊
     */
    LIKE,
    /**
     * 小于
     */
    LESSTHAN,
    /**
     * 小于等于
     */
    LESSTHANEQUAL,
    /**
     * 等于
     */
    EQUALS,
    /**
     * 不等于
     */
    NOT_EQUALS,
    /**
     * 大于等于
     */
    GREATERTHANEQUAL,
    /**
     * 大于
     */
    GREATERTHANE,
    /**
     * DESC排序
     */
    ORDER_DESC,
    /**
     * ASC排序
     */
    ORDER_ASC;



}
