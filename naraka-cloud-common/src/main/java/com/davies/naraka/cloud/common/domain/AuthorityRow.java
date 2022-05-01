package com.davies.naraka.cloud.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 对行数据鉴权的对象
 *
 * @author davies
 * @date 2022/3/20 21:00
 */
@Data
public class AuthorityRow implements Serializable {


    /**
     * sql | properties
     */
    private String type;

    /**
     *
     */
    private List<String> method;


    /**
     * 这里要防止sql注入
     */
    private String content;

}
