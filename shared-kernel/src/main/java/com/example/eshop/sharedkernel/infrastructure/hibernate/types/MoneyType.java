package com.example.eshop.sharedkernel.infrastructure.hibernate.types;

import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.CurrencyType;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class MoneyType implements CompositeUserType {
    @Override
    public String[] getPropertyNames() {
        return new String[] { "money_amount", "money_currency" };
    }

    @Override
    public Type[] getPropertyTypes() {
        return new Type[] { BigIntegerType.INSTANCE, CurrencyType.INSTANCE };
    }

    @Override
    public Object getPropertyValue(Object component, int property) throws HibernateException {
        Money money = (Money) component;

        return switch (property) {
            case 0 -> money.getAmount();
            case 1 -> money.getCurrency();
            default -> throw new IllegalArgumentException(property + " is an invalid property index for class type "
                    + component.getClass().getName());
        };
    }

    @Override
    public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
        throw new UnsupportedOperationException("A Money is immutable");
    }

    @Override
    public Class returnedClass() {
        return Money.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        var amount = rs.getBigDecimal(names[0]);

        if (rs.wasNull()) {
            return null;
        }

        var currency = rs.getString(names[1]);

        return Money.of(amount.doubleValue(), currency);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.NUMERIC);
            st.setNull(index + 1, Types.VARCHAR);
        } else {
            var money = (Money) value;

            st.setBigDecimal(index, money.getAmount());
            st.setString(index + 1, money.getCurrency().getCurrencyCode());
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
    public Serializable disassemble(Object value, SharedSessionContractImplementor session) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return original;
    }
}
