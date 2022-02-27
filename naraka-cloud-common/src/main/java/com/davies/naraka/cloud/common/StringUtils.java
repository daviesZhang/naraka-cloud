package com.davies.naraka.cloud.common;

import com.google.common.base.Strings;

import java.util.regex.Pattern;

/**
 * @author davies
 * @date 2022/2/27 8:05 PM
 */
public class StringUtils {

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@]*@[^.@]+\\.[^.@]+$");

    public static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");

    private static final int DESENSITIZATION_MIN_SIZE = 3;

    /**
     * 数据脱敏
     * 判断11位电话号码 是否包含符合邮箱格式 其他字符串隐藏中间字符
     *
     * @param value
     * @return
     */
    public static String desensitization(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return value;
        }
        if (StringUtils.EMAIL_PATTERN.matcher(value).matches()) {
            return Strings.repeat(StringConstants.ASTERISK, value.indexOf(StringConstants.AT)) + value.substring(value.indexOf(StringConstants.AT));
        } else if (StringUtils.PHONE_PATTERN.matcher(value).matches()) {
            return value.substring(0, 3) + Strings.repeat(StringConstants.ASTERISK, 5) + value.substring(value.length() - 3);
        } else if (value.length() <= DESENSITIZATION_MIN_SIZE) {
            return Strings.repeat(StringConstants.ASTERISK, value.length());
        }
        return value.charAt(0) + Strings.repeat(StringConstants.ASTERISK, value.length() - 2) + value.substring(value.length() - 1);
    }

}
