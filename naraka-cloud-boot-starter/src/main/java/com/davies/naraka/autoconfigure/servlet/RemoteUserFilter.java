package com.davies.naraka.autoconfigure.servlet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 把header里面包含的username放到request RemoteUser方法返回里面
 * @author davies
 * @date 2022/2/27 11:55 AM
 */


public class RemoteUserFilter extends OncePerRequestFilter {

    @Value("${naraka.username.header}")
    private String headerName;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(new RemoteUserHttpServletRequest(httpServletRequest, headerName), httpServletResponse);
    }


}
