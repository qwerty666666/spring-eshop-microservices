package com.example.eshop.order.config;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import java.sql.Types;
import java.util.Set;

/**
 * Adds support Postgres Enum types
 */
public class PostgresDataTypeFactory extends PostgresqlDataTypeFactory {
    private static final Set<String> ENUM_TYPES = Set.of("order_statuses");

    @Override
    public boolean isEnumType(String sqlTypeName) {
        if (ENUM_TYPES.contains(sqlTypeName.toLowerCase())) {
            return true;
        }

        return super.isEnumType(sqlTypeName);
    }

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if (isEnumType(sqlTypeName)) {
            sqlType = Types.OTHER;
        }

        return super.createDataType(sqlType, sqlTypeName);
    }
}
