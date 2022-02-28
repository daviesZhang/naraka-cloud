package com.davies.naraka.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
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
