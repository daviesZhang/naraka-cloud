package com.davies.naraka.system.controller;

import com.davies.naraka.autoconfigure.GeneratorTokenBiFunction;
import com.davies.naraka.cloud.common.exception.NarakaException;
import com.davies.naraka.system.domain.dto.TokenRequestDTO;
import com.davies.naraka.system.domain.entity.SysUser;
import com.davies.naraka.system.repository.SysUserRepository;
import com.davies.naraka.system.service.security.SecurityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author davies
 * @date 2022/5/31 14:31
 */
@RestController
@Validated
@RequestMapping("/token")
public class TokenController {


    private final PasswordEncoder passwordEncoder;

    private final GeneratorTokenBiFunction generatorToken;
    private final SysUserRepository userRepository;


    private final SecurityService securityService;

    public TokenController(PasswordEncoder passwordEncoder, GeneratorTokenBiFunction generatorToken, SysUserRepository userRepository, SecurityService securityService) {
        this.passwordEncoder = passwordEncoder;
        this.generatorToken = generatorToken;
        this.userRepository = userRepository;
        this.securityService = securityService;
    }

    @PostMapping()
    public String token(@RequestBody @Validated TokenRequestDTO tokenRequest) {
        String username = tokenRequest.getUsername();
        SysUser sysUser = userRepository
                .findByUsername(tokenRequest.getUsername())
                .orElseThrow(() -> new NarakaException("用户不存在或密码错误~", 401));
        boolean success = passwordEncoder.matches(tokenRequest.getPassword(), sysUser.getPassword());
        if (success) {
            this.securityService.login(username);
            return generatorToken.apply(username, null);
        }
        throw new NarakaException("用户不存在或密码错误~", 401);
    }


    @GetMapping("/refresh")
    public String refresh(HttpServletRequest request) {
        String username = request.getRemoteUser();
        this.securityService.login(username);
        return generatorToken.apply(username, null);
    }
}
