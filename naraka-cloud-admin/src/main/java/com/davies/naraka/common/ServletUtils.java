package com.davies.naraka.common;

import javax.servlet.http.HttpServletRequest;

/**
 * @author davies
 * @date 2022/2/21 5:14 PM
 */
public class ServletUtils {


    /**
     * 获取真实IP
     *
     * @param request
     * @return
     */
    public static String realIp(HttpServletRequest request){
        //todo 如果经过代理,判断其他约定header ip是否有值
        return request.getRemoteAddr();
    }
}
