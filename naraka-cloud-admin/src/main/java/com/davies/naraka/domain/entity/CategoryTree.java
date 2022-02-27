package com.davies.naraka.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.davies.naraka.domain.enums.CategoryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author davies
 * @since 2022-02-08
 */
@Getter
@Setter
@TableName("t_category_tree")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CategoryTree implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 代表后代节点
     */
    private Integer descendant;

    /**
     * 代表祖先节点
     */
    private Integer ancestor;

    /**
     * 祖先距离后代的距离
     */
    private Integer distance;

    /**
     * 类型
     */
    private CategoryType categoryType;

    @TableField( fill = FieldFill.INSERT)
    private String createdBy;
    @TableField( fill = FieldFill.INSERT)
    private LocalDateTime createdTime;




}
