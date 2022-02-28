package com.davies.naraka.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author davies
 * @date 2022/2/27 8:21 PM
 */
public class RequestRemoteUserSupplier implements CurrentUserNameSupplier {

    @Autowired
    private HttpServletRequest request;

    @Override
    public String get() {
        if (request==null){
            return null;
        }
        return request.getRemoteUser();
    }
}
