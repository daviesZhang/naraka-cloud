package com.davies.naraka.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.davies.naraka.domain.bo.UserAndRoleBO;
import com.davies.naraka.domain.entity.Authority;
import com.davies.naraka.domain.entity.Role;
import com.davies.naraka.domain.entity.User;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
public interface IUserService extends IService<User> {


    User createUser(User user);


    /**
     * 根据用户名获取用户所拥有的Authority 列表
     *
     * @param username
     * @return
     */
    List<Authority> getUserAuthorityList(@NotNull String username);


    /**
     * 根据用户名获取用户所拥有的Role 列表
     *
     * @param username
     * @return
     */
    List<Role> getUserRoleList(@NotNull String username);

    /**
     * 灵活组装条件查询单个用户
     *
     * @param queryWrapper 查询条件
     * @return Optional<User>
     */
    Optional<User> findUser(@NotNull Wrapper<User> queryWrapper);


    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return Optional<User>
     */
    Optional<User> findUserByUsername(@NotNull String username);

    /**
     * 根据ID查找用户
     *
     * @param id 主键ID
     * @return Optional<User>
     */
    Optional<User> findUserById(int id);

    /**
     * 根据电话查找用户
     *
     * @param phone 电话
     * @return Optional<User>
     */
    Optional<User> findUserByPhone(@NotNull String phone);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return Optional<User>
     */
    Optional<User> findUserByEmail(@NotNull String email);


    /**
     * 用户表 left join 角色表 后的结果
     * @param page
     * @param wrapper
     * @return
     */
    Page<UserAndRoleBO> userAndRolePage(Page<UserAndRoleBO> page, Wrapper<User> wrapper);
}
