package com.davies.naraka.security;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author davies
 * @date 2022/2/27 11:55 AM
 */
public class RemoteUserHttpServletRequest extends HttpServletRequestWrapper {

    public RemoteUserHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getRemoteUser() {
        String authorization = getHeader(HttpHeaders.AUTHORIZATION);
        if(Strings.isNullOrEmpty(authorization)){
            return null;
        }
        return authorization;
    }
}
