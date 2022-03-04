package com.example.eshop.warehouse.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.internal.AbstractJsonSqlTypeDescriptor;
import org.hibernate.HibernateException;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.postgresql.util.PGobject;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * JPA Type that converts any Object to Postgres Json.
 * <p>
 * The generated Json will contain {@code @type) field with class
 * FQN, which will be used in deserialization process to determine
 * result class.
 * <p>
 * The implementation is inspired by {@link JsonBinaryType}.
 * But we can't use {@link JsonBinaryType} directly because
 * we try to support every Object serialization / deserialization,
 * and therefore we use {@link DefaultTyping#EVERYTHING} for this.
 * But {@link JsonBinaryType} uses {@link ObjectMapper#readTree(String)}
 * to convert Object to db type. And {@link ObjectMapper#readTree(String)}
 * throws exception if it used with {@link DefaultTyping#EVERYTHING}.
 * (It seems to be a bug in jackson {@link DefaultTypeResolverBuilder#useForType(JavaType)}
 * for {@link DefaultTyping#EVERYTHING} case. There was an issue
 * {@see https://github.com/FasterXML/jackson-databind/issues/88}
 * for the same problem that is fixed, but newly added {@link DefaultTyping#EVERYTHING}
 * seems to be implementing wrongly)
 */
public class JsonType extends AbstractSingleColumnStandardBasicType<Object> {
    public JsonType() {
        super(
                JsonSqlTypeDescriptor.INSTANCE,
                new JsonJavaTypeDescriptor(new ObjectMapper()
                        .activateDefaultTypingAsProperty(
                                new LaissezFaireSubTypeValidator(),
                                DefaultTyping.EVERYTHING,
                                "@type"
                        )
                )
        );
    }

    @Override
    public String getName() {
        return "jsonb";
    }

    private static class JsonSqlTypeDescriptor extends AbstractJsonSqlTypeDescriptor {
        public static final JsonSqlTypeDescriptor INSTANCE = new JsonSqlTypeDescriptor();

        @Override
        public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
            return new BasicBinder<>(javaTypeDescriptor, this) {
                @Override
                protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                    st.setObject(index, javaTypeDescriptor.unwrap(value, Object.class, options), getSqlType());
                }

                @Override
                protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                    st.setObject(name, javaTypeDescriptor.unwrap(value, Object.class, options), getSqlType());
                }
            };
        }
    }

    private static class JsonJavaTypeDescriptor extends AbstractTypeDescriptor<Object> {
        protected ObjectMapper objectMapper;

        public JsonJavaTypeDescriptor(ObjectMapper objectMapper) {
            super(Object.class);
            this.objectMapper = objectMapper;
        }

        @Override
        public <X> X unwrap(Object value, Class<X> type, WrapperOptions options) {
            if (value == null) {
                return null;
            }

            try {
                PGobject jsonObject = new PGobject();
                jsonObject.setType("json");
                jsonObject.setValue(toString(value));

                return (X) jsonObject;
            } catch (SQLException e) {
                throw new HibernateException(e);
            }
        }

        @Override
        public <X> Object wrap(X value, WrapperOptions options) {
            if (value == null) {
                return null;
            }

            if (!(value instanceof PGobject pgObj) || !pgObj.getType().equals("json")) {
                throw new HibernateException("Expected PGobject json but " + value + " provided");
            }

            return fromString(pgObj.getValue());
        }

        @Override
        public String toString(Object value) {
            try {
                return objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new HibernateException(
                        new IllegalArgumentException("Object " + value + " can't be converted to JSON", e)
                );
            }
        }

        @Override
        public Object fromString(String string) {
            try {
                return objectMapper.readValue(string, Object.class);
            } catch (JsonProcessingException e) {
                throw new HibernateException(
                        new IllegalArgumentException("String " + string + " can't be converted from JSON", e)
                );
            }
        }
    }
}
