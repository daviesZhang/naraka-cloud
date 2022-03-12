package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.cloud.common.AesEncryptorUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.DynamicParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.Properties;

/**
 * 字符串字段加解密 特殊类型
 *
 * @author davies
 */
@Slf4j
public class CryptoStringUserType implements UserType, DynamicParameterizedType {


    private final String key;

    public CryptoStringUserType(String key) {
        this.key = key;
    }


    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {

        Object o = rs.getObject(names[0]);
        if (o != null && !Strings.isNullOrEmpty((String) o)) {
            try {
                return AesEncryptorUtils.decrypt((String) o, key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return o;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {

        if (value != null && !Strings.isNullOrEmpty((String) value) && !Strings.isNullOrEmpty(key)) {
            try {
                String encryptValue = AesEncryptorUtils.encrypt((String) value, key);
                st.setString(index, encryptValue);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            st.setString(index, (String) value);
        }

    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        final ParameterType reader = (ParameterType) parameters.get(PARAMETER_TYPE);

    }
}
