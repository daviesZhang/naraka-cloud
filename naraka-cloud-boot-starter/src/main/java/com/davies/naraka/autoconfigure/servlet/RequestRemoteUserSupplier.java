package com.davies.naraka.autoconfigure.servlet;

import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * 把header里面包含的username放到request RemoteUser方法返回里面
 *
 * @author davies
 * @date 2022/2/27 8:21 PM
 */
public class RequestRemoteUserSupplier implements CurrentUserNameSupplier {

    @Autowired
    private HttpServletRequest request;

    /**
     * 某些情况,RequestContextHolder 内的ThreadLocal 已经销毁或者还未put的时候调用此方法导致异常
     * 此类情况属于获取不到用户,返回null,不抛异常,调用方处理
     *
     * @return
     */
    @Override
    public String get() {
        if (request == null) {
            return null;
        }
        try {
            return request.getRemoteUser();
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
