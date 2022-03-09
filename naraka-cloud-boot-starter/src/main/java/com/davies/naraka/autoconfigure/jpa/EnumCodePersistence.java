package com.davies.naraka.autoconfigure.jpa;

import javax.persistence.Converter;

/**
 * autoRegisterUserTypes
 * @author davies
 */
@Converter
public interface EnumCodePersistence {

    /**
     * 获取枚举code
     * @return code
     */
    public int getCode();
}
