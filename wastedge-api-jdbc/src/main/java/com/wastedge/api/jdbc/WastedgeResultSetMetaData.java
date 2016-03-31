package com.wastedge.api.jdbc;

import com.wastedge.api.EntityPhysicalField;
import com.wastedge.api.EntitySchema;
import com.wastedge.api.EntityTypedField;
import org.apache.commons.lang3.Validate;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.List;

public class WastedgeResultSetMetaData implements ResultSetMetaData {
    private final EntitySchema entity;
    private final List<EntityTypedField> fields;

    WastedgeResultSetMetaData(EntitySchema entity, List<EntityTypedField> fields) {
        Validate.notNull(entity, "entity");
        Validate.notNull(fields, "fields");
        
        this.entity = entity;
        this.fields = fields;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return fields.size();
    }

    @Override
    public boolean isAutoIncrement(int columnIndex) throws SQLException {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int columnIndex) throws SQLException {
        return false;
    }

    @Override
    public boolean isSearchable(int columnIndex) throws SQLException {
        return true;
    }

    @Override
    public boolean isCurrency(int columnIndex) throws SQLException {
        return false;
    }

    private EntityTypedField getField(int columnIndex) {
        if (columnIndex < 1 || columnIndex > fields.size()) {
            throw new IllegalArgumentException("columnIndex");
        }
        return fields.get(columnIndex - 1);
    }

    @Override
    public int isNullable(int columnIndex) throws SQLException {
        EntityTypedField field = getField(columnIndex);
        if (field instanceof EntityPhysicalField) {
            return ((EntityPhysicalField)field).isMandatory() ? columnNoNulls : columnNullable;
        }
        return columnNullable;
    }

    @Override
    public boolean isSigned(int columnIndex) throws SQLException {
        return getField(columnIndex).getDataType().isNumeric();
    }

    @Override
    public int getColumnDisplaySize(int columnIndex) throws SQLException {
        return Short.MAX_VALUE;
    }

    @Override
    public String getColumnLabel(int columnIndex) throws SQLException {
        return getField(columnIndex).getComments();
    }

    @Override
    public String getColumnName(int columnIndex) throws SQLException {
        return getField(columnIndex).getName();
    }

    @Override
    public String getSchemaName(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public int getPrecision(int columnIndex) throws SQLException {
        return Short.MAX_VALUE;
    }

    @Override
    public int getScale(int columnIndex) throws SQLException {
        return getField(columnIndex).getDecimals();
    }

    @Override
    public String getTableName(int columnIndex) throws SQLException {
        return entity.getName();
    }

    @Override
    public String getCatalogName(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public int getColumnType(int columnIndex) throws SQLException {
        switch (getField(columnIndex).getDataType()) {
            case BYTES:
                return Types.VARBINARY;
            case STRING:
                return Types.VARCHAR;
            case DATE:
            case DATE_TIME:
            case DATE_TIME_TZ:
                return Types.DATE;
            case DECIMAL:
                return Types.DOUBLE;
            case LONG:
                return Types.BIGINT;
            case INT:
                return Types.INTEGER;
            case BOOL:
                return Types.BOOLEAN;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public String getColumnTypeName(int columnIndex) throws SQLException {
        return getField(columnIndex).getDataType().toString();
    }

    @Override
    public boolean isReadOnly(int columnIndex) throws SQLException {
        return true;
    }

    @Override
    public boolean isWritable(int columnIndex) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int columnIndex) throws SQLException {
        return false;
    }

    @Override
    public String getColumnClassName(int columnIndex) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
