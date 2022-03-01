package com.davies.naraka.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * 把header里面包含的username放到request RemoteUser方法返回里面
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
