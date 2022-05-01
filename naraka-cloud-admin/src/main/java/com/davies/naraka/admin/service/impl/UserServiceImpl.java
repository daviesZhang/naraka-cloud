package com.davies.naraka.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.davies.naraka.admin.common.ListMapCollector;
import com.davies.naraka.admin.domain.UserInfo;
import com.davies.naraka.admin.domain.bo.UserAndRoleBO;
import com.davies.naraka.admin.domain.entity.*;
import com.davies.naraka.admin.domain.enums.AuthorityProcessorType;
import com.davies.naraka.admin.domain.enums.ResourceType;
import com.davies.naraka.admin.mapper.UserMapper;
import com.davies.naraka.admin.service.*;
import com.davies.naraka.admin.service.exception.UserEmailExistsException;
import com.davies.naraka.admin.service.exception.UserNameExistsException;
import com.davies.naraka.admin.service.exception.UserPhoneExistsException;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.jackson.SerializeBeanPropertyFactory;
import com.davies.naraka.autoconfigure.properties.SecurityProperties;
import com.davies.naraka.autoconfigure.security.SecurityHelper;
import com.davies.naraka.cloud.common.StringConstants;
import com.davies.naraka.cloud.common.domain.AuthorityRow;
import com.davies.naraka.cloud.common.exception.NarakaException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.davies.naraka.admin.domain.enums.AuthorityProcessorType.SKIP_ROW;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author davies
 * @since 2022-01-24
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final IUserRoleService userRoleService;
    private final IAuthorityRoleService authorityRoleService;

    private final IRoleService roleService;

    private final IAuthorityService authorityService;

    private final RedissonClient redissonClient;
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    /**
     * 比token有效时间多两分钟,防止认证通过后,因程序运行时间导致缓存无效
     */
    private final long USER_CACHE_LIVE;

    public UserServiceImpl(IUserRoleService userRoleService,
                           IAuthorityRoleService authorityRoleService,
                           IRoleService roleService,
                           IAuthorityService authorityService,
                           SecurityProperties securityProperties,
                           RedissonClient redissonClient) {
        this.userRoleService = userRoleService;
        this.authorityRoleService = authorityRoleService;
        this.roleService = roleService;
        this.authorityService = authorityService;
        this.redissonClient = redissonClient;
        this.USER_CACHE_LIVE = securityProperties.getExpiresAt() + 2;
    }


    @Override
    public User createUser(User user) {
        findUserByUsername(user.getUsername())
                .map(User::getUsername)
                .ifPresent(username -> {
                    throw new UserNameExistsException(String.format("createUser username:[%s] username exists", username));
                });

        findUserByPhone(user.getPhone())
                .map(User::getUsername)
                .ifPresent(username -> {
                    throw new UserPhoneExistsException(String.format("createUser username:[%s] phone exists", username));
                });

        findUserByEmail(user.getEmail())
                .map(User::getUsername)
                .ifPresent(username -> {
                    throw new UserEmailExistsException(String.format("createUser username:[%s] email exists", username));
                });
        save(user);
        log.info("create user [{}] success", user.getUsername());
        return user;
    }

    @Override
    public List<Authority> getUserAuthorityList(@NotNull String username, ResourceType resourceType) {
        UserRole code = userRoleService.getOne(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUsername, username));
        return getUserAuthorityList(username, resourceType, code);
    }


    private List<Authority> getUserAuthorityList(@NotNull String username, ResourceType resourceType, UserRole userRole) {

        if (userRole == null) {
            return Collections.emptyList();
        }
        List<Integer> ids = authorityRoleService.list(new LambdaQueryWrapper<AuthorityRole>().eq(AuthorityRole::getCode, userRole.getCode()))
                .stream().map(AuthorityRole::getAuthority).collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Authority> lambdaQueryWrapper = new LambdaQueryWrapper<Authority>().in(Authority::getId, ids);
        if (resourceType != null) {
            lambdaQueryWrapper.eq(Authority::getResourceType, resourceType);
        }
        return authorityService.list(lambdaQueryWrapper);
    }

    @Override
    public List<Role> getUserRoleList(@NotNull String username) {
        List<String> code = userRoleService.list(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUsername, username))
                .stream().map(UserRole::getCode).collect(Collectors.toList());
        if (code.isEmpty()) {
            return Collections.emptyList();
        }
        return roleService.list(new LambdaQueryWrapper<Role>().in(Role::getCode, code));
    }

    @Override
    public Optional<User> findUser(@NotNull Wrapper<User> queryWrapper) {
        return Optional.ofNullable(getOne(queryWrapper));
    }

    @Override
    public Optional<User> findUserByUsername(@NotNull String username) {
        return this.findUser(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public Optional<User> findUserById(int id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<User> findUserByPhone(@NotNull String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getPhone, phone);
        return this.findUser(queryWrapper);
    }

    @Override
    public Optional<User> findUserByEmail(@NotNull String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getEmail, email);
        return this.findUser(queryWrapper);
    }

    @Override
    public Page<UserAndRoleBO> userAndRolePage(Page<UserAndRoleBO> page, Wrapper<User> wrapper) {
        return baseMapper.selectUserAndRolePage(page, wrapper);
    }


    /**
     * @param user
     * @return
     */
    @Override
    public UserInfo getUserInfoAndCache(User user) {
        UserInfo userInfo = new UserInfo();
        ClassUtils.copyObject(user, userInfo);
        String username = user.getUsername();
        UserRole code = userRoleService.getOne(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUsername, username));
        if (code != null) {
            List<Authority> authorities = getUserAuthorityList(username, ResourceType.API, code);
            if (!authorities.isEmpty()) {
                userInfo.setApi(getApiMap(authorities));
                userInfo.setAuthoritySerialize(authoritySerializeToMap(authorities));
                userInfo.setAuthorityRows(getAuthorityRows(authorities));
            }
        }
        // 如读频繁,可考虑再加一层本地缓存
        String userCacheKey = SecurityHelper.userCacheKey(username);
        redissonClient.getBucket(userCacheKey).set(userInfo, USER_CACHE_LIVE, TimeUnit.MINUTES);
        cacheMap(userInfo.getApi(), SecurityHelper.userAuthorityApiCacheKey(username));
        cacheMap(userInfo.getAuthoritySerialize(), SecurityHelper.userAuthoritySerializeCacheKey(username));
        cacheMap(userInfo.getAuthorityRows(), SecurityHelper.userAuthorityRowCacheKey(username));
        return userInfo;
    }

    private <T> void cacheMap(Map<String, T> objectMap, String cacheKey) {
        RMap<String, Object> rowCache = redissonClient.getMap(cacheKey);
        rowCache.clear();
        rowCache.expire(USER_CACHE_LIVE, TimeUnit.MINUTES);
        if (null == objectMap) {
            rowCache.putAll(Collections.emptyMap());
        } else {
            rowCache.putAll(objectMap);
        }
    }


    /**
     * {}
     *
     * @param authorityList
     * @return
     */
    public Map<String, Set<AuthorityRow>> getAuthorityRows(List<Authority> authorityList) {
        return authorityList.stream().filter(authority -> authority.getProcessor() == SKIP_ROW).collect(
                Collectors.groupingBy(Authority::getResource,
                        Collectors.mapping(authority -> authority,
                                Collectors.mapping(authority -> {
                                    try {
                                        String value = authority.getProcessorValue();
                                        if (Strings.isNullOrEmpty(value)) {
                                            return new AuthorityRow();
                                        }
                                        return OBJECT_MAPPER.readValue(authority.getProcessorValue(), AuthorityRow.class);
                                    } catch (JsonProcessingException e) {
                                        throw new NarakaException(e);
                                    }
                                }, Collectors.toSet())
                        ))
        );

    }

    /**
     * 返回拥有的url作为key,value为需要过滤的字段列表,用逗号隔开
     *
     * @param authorityList
     * @return
     */
    private Map<String, String> getApiMap(List<Authority> authorityList) {
        Map<String, String> filterMap = authorityList.stream()
                .filter(authority -> authority.getProcessor() == AuthorityProcessorType.FILTER
                        && !Strings.isNullOrEmpty(authority.getProcessorValue()))
                .collect(Collectors.groupingBy(Authority::getResource,
                        Collectors.mapping(Authority::getProcessorValue,
                                Collectors.joining(StringPool.COMMA))));
        return authorityList.stream()
                .map(Authority::getResource)
                .distinct()
                .collect(Collectors.toMap(s -> s, s -> filterMap.getOrDefault(s, StringPool.EMPTY)));

    }


    private boolean serializeAuthority(Authority authority) {
        AuthorityProcessorType type = authority.getProcessor();
        return type == AuthorityProcessorType.DESENSITIZATION ||
                type == AuthorityProcessorType.FILTER;
    }

    /**
     * @param authorityList
     * @return Resource=> ProcessorValue(按逗号拆分后) => AuthorityProcessorType
     */
    private Map<String, Map<String, Set<String>>> authoritySerializeToMap(List<Authority> authorityList) {
        return authorityList.stream().filter(this::serializeAuthority).collect(
                Collectors.groupingBy(Authority::getResource, Collectors.mapping(authority -> {
                            String value = authority.getProcessorValue();
                            if (Strings.isNullOrEmpty(value)) {
                                return new ArrayList<>();
                            }
                            return Arrays.stream(value.split(StringPool.COMMA)).map(v -> {
                                Authority newAuthority = ClassUtils.copyObject(authority, new Authority());
                                newAuthority.setProcessorValue(v);
                                return newAuthority;
                            }).collect(Collectors.toList());
                        }, new ListMapCollector<>(this::accumulator)
                ))
        );
    }


    private void accumulator(Map<String, Set<String>> stringSetMap,
                             Collection<Authority> authorities) {
        if (authorities.isEmpty()) {
            return;
        }
        authorities.forEach(authority -> {
            stringSetMap.compute(authority.getProcessorValue(), (s, authorityProcessorTypes) -> {
                if (authorityProcessorTypes == null) {

                    return Sets.newHashSet(processorTypeToString(authority.getProcessor()));
                } else {
                    authorityProcessorTypes.add(processorTypeToString(authority.getProcessor()));
                    return authorityProcessorTypes;
                }
            });
        });
    }


    private String processorTypeToString(AuthorityProcessorType processorType) {
        String name;
        switch (processorType) {
            case SKIP_ROW:
                name = processorType.name();
                break;
            case FILTER:
            case DESENSITIZATION:
            default:
                name = SerializeBeanPropertyFactory.SERIALIZE_PREFIX + StringConstants.UNDERSCORE + processorType.name();
                break;
        }
        return CaseFormat.UPPER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL).convert(name);

    }
}
