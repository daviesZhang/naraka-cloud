package com.davies.naraka.controller;

import com.alibaba.nacos.common.utils.MD5Utils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.davies.naraka.annotation.HasUser;
import com.davies.naraka.common.ClassUtils;
import com.davies.naraka.common.ListMapCollector;
import com.davies.naraka.common.StringUtils;
import com.davies.naraka.domain.dto.system.CurrentUserDTO;
import com.davies.naraka.domain.dto.system.LoginDTO;
import com.davies.naraka.domain.entity.Authority;
import com.davies.naraka.domain.entity.User;
import com.davies.naraka.domain.enums.AuthorityProcessorType;
import com.davies.naraka.domain.enums.ResourceType;
import com.davies.naraka.security.GeneratorTokenBiFunction;
import com.davies.naraka.security.SecurityHelper;
import com.davies.naraka.security.properties.SecurityProperties;
import com.davies.naraka.service.IUserService;
import com.davies.naraka.service.exception.UserNotFoundException;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


/**
 * @author davies
 * @date 2022/1/22 11:01 AM
 */
@Slf4j
@RestController
public class TokenController {


    private final BiFunction<String, Date, String> generatorToken;

    private RedissonClient redissonClient;

    /**
     * 比token有效时间多两分钟,防止认证通过后,因程序运行时间导致缓存无效
     */
    private final long USER_CACHE_LIVE;


    private final IUserService userService;

    private final PasswordEncoder passwordEncoder;

    public TokenController(
            RedissonClient redissonClient,
            SecurityProperties securityProperties,
            GeneratorTokenBiFunction generatorToken,
            IUserService userService,
            PasswordEncoder passwordEncoder) {
        this.generatorToken = generatorToken;
        this.redissonClient = redissonClient;
        this.USER_CACHE_LIVE = securityProperties.getExpiresAt() + 2;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 从缓存中返回当前登录用户的信息
     *
     * @return CurrentUserDTO
     */
    @HasUser
    @GetMapping("/me")
    public CurrentUserDTO me(HttpServletRequest request) {
        return (CurrentUserDTO) redissonClient.getBucket(SecurityHelper.userCacheKey(request.getRemoteUser())).get();
    }

    private CurrentUserDTO refreshCache(User userInfo) {
        List<Authority> authorities = userService.getUserAuthorityList(userInfo.getUsername());
        CurrentUserDTO currentUser = new CurrentUserDTO();
        ClassUtils.copyObject(userInfo, currentUser);
        currentUser.setAuthority(getAuthorityMap(authorities));
        RMap<String, Map<String, Set<AuthorityProcessorType>>> authorityMap =
                redissonClient.getMap(SecurityHelper.userAuthorityCacheKey(userInfo.getUsername()));

        authorityMap.clear();
        authorityMap.expire(USER_CACHE_LIVE, TimeUnit.MINUTES);
        authorityMap.putAll(authorityListToMap(authorities).get(ResourceType.URL));
        redissonClient.getBucket(SecurityHelper.userCacheKey(userInfo.getUsername()))
                .set(currentUser, USER_CACHE_LIVE, TimeUnit.MINUTES);
        return currentUser;
    }

    /**
     * 刷新缓存和token的有效时间
     * @param request
     * @return
     */
    @HasUser
    @PostMapping("/refreshToken")
    public ResponseEntity<Void> refresh(HttpServletRequest request) {
        //String username
        Optional<User> optionalUser = userService.findUserByUsername(request.getRemoteUser());
        User user = optionalUser.orElseThrow(UserNotFoundException::new);
        refreshCache(user);
        String jwt = this.generatorToken.apply(user.getUsername(), new Date());
        log.info("[{}]  refresh token", user.getUsername());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwt)
                .build();
    }


    @PostMapping("/token")
    public ResponseEntity<Void> token(@RequestBody @Validated LoginDTO loginDTO) {
        String principal = loginDTO.getPrincipal();
        String password = loginDTO.getPassword();
        Optional<User> optionalUser;
        if (StringUtils.EMAIL_PATTERN.matcher(principal).matches()) {
            optionalUser = userService.findUserByEmail(principal);
        } else if (StringUtils.PHONE_PATTERN.matcher(principal).matches()) {
            optionalUser = userService.findUserByPhone(principal);
        } else {
            optionalUser = userService.findUserByUsername(principal);
        }
        User user = optionalUser.orElseThrow(UserNotFoundException::new);
        if (passwordEncoder.matches(password,user.getPassword())) {
            refreshCache(user);
            String jwt = this.generatorToken.apply(user.getUsername(), new Date());
            log.info("[{}] login success", user.getUsername());

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .build();
        }
        throw new RuntimeException();
    }


    /**
     * 返回拥有的url作为key,value为需要过滤的字段列表,用逗号隔开
     *
     * @param authorityList
     * @return
     */
    private Map<String, String> getAuthorityMap(List<Authority> authorityList) {
        Map<String, String> filterMap = authorityList.stream()
                .filter(authority -> authority.getResourceType() == ResourceType.URL
                        && authority.getProcessor() == AuthorityProcessorType.FILTER
                        && !Strings.isNullOrEmpty(authority.getProcessorValue()))
                .collect(Collectors.groupingBy(Authority::getResource,
                        Collectors.mapping(Authority::getProcessorValue,
                                Collectors.joining(StringPool.COMMA))));
        return authorityList.stream().filter(authority -> authority.getResourceType() == ResourceType.URL)
                .map(Authority::getResource)
                .distinct()
                .collect(Collectors.toMap(s -> s, s -> filterMap.getOrDefault(s, StringPool.EMPTY)));

    }


    /**
     * @param authorityList
     * @return ResourceType=> Resource=> ProcessorValue(按逗号拆分后) => AuthorityProcessorType
     */
    private Map<ResourceType, Map<String, Map<String, Set<AuthorityProcessorType>>>> authorityListToMap(List<Authority> authorityList) {
        return authorityList.stream().collect(Collectors
                .groupingBy(Authority::getResourceType, Collectors.mapping((Function<Authority, Authority>) input -> input,
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
                )));
    }


    private void accumulator(Map<String, Set<AuthorityProcessorType>> stringSetMap,
                             Collection<Authority> authorities) {
        if (authorities.isEmpty()) {
            return;
        }
        authorities.forEach(authority -> {
            stringSetMap.compute(authority.getProcessorValue(), (s, authorityProcessorTypes) -> {
                if (authorityProcessorTypes == null) {
                    return Sets.newHashSet(authority.getProcessor());
                } else {
                    authorityProcessorTypes.add(authority.getProcessor());
                    return authorityProcessorTypes;
                }
            });
        });
    }
}
