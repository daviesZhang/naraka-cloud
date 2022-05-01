package com.davies.naraka.admin.controller;

import com.davies.naraka.admin.domain.UserInfo;
import com.davies.naraka.admin.domain.dto.system.CurrentUserDTO;
import com.davies.naraka.admin.domain.dto.system.LoginDTO;
import com.davies.naraka.admin.domain.entity.User;
import com.davies.naraka.admin.service.IUserService;
import com.davies.naraka.admin.service.exception.UserNotFoundException;
import com.davies.naraka.autoconfigure.ClassUtils;
import com.davies.naraka.autoconfigure.GeneratorTokenBiFunction;
import com.davies.naraka.autoconfigure.security.HasUser;
import com.davies.naraka.autoconfigure.security.SecurityHelper;
import com.davies.naraka.cloud.common.StringUtils;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;


/**
 * @author davies
 * @date 2022/1/22 11:01 AM
 */
@Slf4j
@RestController
public class TokenController {


    private final GeneratorTokenBiFunction generatorToken;

    private RedissonClient redissonClient;


    private final IUserService userService;

    private final PasswordEncoder passwordEncoder;

    public TokenController(
            RedissonClient redissonClient,
            GeneratorTokenBiFunction generatorToken,
            IUserService userService,
            PasswordEncoder passwordEncoder) {
        this.generatorToken = generatorToken;
        this.redissonClient = redissonClient;
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
        UserInfo userInfo = (UserInfo) redissonClient.getBucket(SecurityHelper.userCacheKey(request.getRemoteUser()));
        return ClassUtils.copyObject(userInfo, new CurrentUserDTO());
    }




    /**
     * 刷新缓存和token的有效时间
     *
     * @param request
     * @return
     */
    @HasUser
    @PostMapping("/refreshToken")
    public ResponseEntity<Void> refresh(HttpServletRequest request) {

        Optional<User> optionalUser = userService.findUserByUsername(request.getRemoteUser());
        User user = optionalUser.orElseThrow(UserNotFoundException::new);
        userService.getUserInfoAndCache(user);
        String jwt = this.generatorToken.apply(user.getUsername(), null);
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
        if (passwordEncoder.matches(password, user.getPassword())) {
            userService.getUserInfoAndCache(user);
            String jwt = this.generatorToken.apply(user.getUsername(), null);
            log.info("[{}] login success", user.getUsername());
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, jwt)
                    .build();
        }
        throw new RuntimeException();
    }
}
