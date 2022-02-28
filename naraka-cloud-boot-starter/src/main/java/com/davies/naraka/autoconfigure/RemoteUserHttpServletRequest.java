package com.davies.naraka.autoconfigure;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author davies
 * @date 2022/2/27 11:55 AM
 */
public class RemoteUserHttpServletRequest extends HttpServletRequestWrapper {

    private final String usernameHeader;

    public RemoteUserHttpServletRequest(HttpServletRequest request,String usernameHeader) {
        super(request);
        this.usernameHeader = usernameHeader;
    }

    @Override
    public String getRemoteUser() {
        String username = getHeader(this.usernameHeader);
        if(Strings.isNullOrEmpty(username)){
            return null;
        }
        return username;
    }
}
