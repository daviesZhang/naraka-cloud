package com.davies.naraka.autoconfigure.jpa;

import com.google.common.base.Strings;
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
import java.util.function.Supplier;

/**
 * 实现了EnumCodePersistence 接口的Enum 类型转换,入库为自定义int node
 *
 * @author davies
 */
public class EnumCodeUserType implements UserType, DynamicParameterizedType {

    private Class enumClass;

    public EnumCodeUserType(Class enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        //parameters
        final ParameterType reader = (ParameterType) parameters.get(PARAMETER_TYPE);
        this.enumClass = reader.getReturnedClass();
    }

    /**
     * @return
     */
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.INTEGER};
    }

    @Override
    public Class returnedClass() {
        return this.enumClass;
    }


    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        Object value = rs.getObject(names[0]);
        if (value == null) {
            return null;
        }
        if (Supplier.class.isAssignableFrom(this.enumClass)) {
            Supplier[] objects = (Supplier[]) this.enumClass.getEnumConstants();
            for (Supplier o : objects) {
                if (Objects.equals(value, o.get())) {
                    return o;
                }
            }
        }
        throw new IllegalArgumentException(Strings.lenientFormat("%s must impl EnumCodePersistence", this.enumClass.getName()));
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        Supplier code = (Supplier) value;
        st.setObject(index, code.get());
    }


    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x == null ? 0 : x.hashCode();
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


}
