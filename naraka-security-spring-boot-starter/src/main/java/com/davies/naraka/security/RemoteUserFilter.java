package com.davies.naraka.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author davies
 * @date 2022/2/27 11:55 AM
 */

@ConditionalOnProperty(name = "naraka.security.remoteUser",havingValue = "true")
@Component
public class RemoteUserFilter extends OncePerRequestFilter {



    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(new RemoteUserHttpServletRequest(httpServletRequest), httpServletResponse);
    }


}
