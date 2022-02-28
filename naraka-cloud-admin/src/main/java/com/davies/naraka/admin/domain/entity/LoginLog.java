package com.davies.naraka.admin.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 管理员登陆日子 
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Getter
@Setter
@TableName("t_login_log")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 客户端host
     */
    private String clientHost;

    /**
     * 登陆时间
     */
    private LocalDateTime loginTime;

    /**
     * 是否登陆成功
     */
    private Boolean success;

    /**
     * 客户端mac地址
     */
    private String clientMac;

    /**
     * 设备
     */
    private String device;

    /**
     * 来源
     */
    private String referer;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;


}
