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
    LT,
    /**
     * 小于等于
     */
    LE,
    /**
     * 等于
     */
    EQ,
    /**
     * 不等于
     */
    NOT_EQ,
    /**
     * 大于等于
     */
    GE,
    /**
     * 大于
     */
    GT,
    /**
     * DESC排序
     */
    DESC,
    /**
     * ASC排序
     */
    ASC


}
