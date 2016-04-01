package com.wastedge.api.jdbc;

import org.apache.commons.lang3.Validate;
import org.joda.time.LocalDateTime;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressWarnings("deprecation")
public class WastedgePreparedStatement extends WastedgeStatement implements PreparedStatement {
    private final String query;
    private final List parameters = new ArrayList();

    WastedgePreparedStatement(WastedgeConnection connection, String query) {
        super(connection);

        Validate.notNull(query, "query");

        this.query = query;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return executeQuery(query);
    }

    @Override
    public int executeUpdate() throws SQLException {
        return executeUpdate(query);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        setObject(parameterIndex, null);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setByte(int parameterIndex, byte value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setShort(int parameterIndex, short value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setInt(int parameterIndex, int value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setLong(int parameterIndex, long value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setFloat(int parameterIndex, float value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setDouble(int parameterIndex, double value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setString(int parameterIndex, String value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setDate(int parameterIndex, Date value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setTime(int parameterIndex, Time value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream value, int i1) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream value, int i1) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream value, int i1) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void clearParameters() throws SQLException {
        parameters.clear();
    }

    @Override
    public void setObject(int parameterIndex, Object value, int i1) throws SQLException {
        setObject(parameterIndex, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setObject(int parameterIndex, Object value) throws SQLException {
        while (parameters.size() <= parameterIndex) {
            parameters.add(null);
        }

        if (value instanceof Date) {
            value = new LocalDateTime(((Date)value).getTime());
        } else if (value instanceof Timestamp) {
            value = new LocalDateTime(((Timestamp)value).getTime());
        }

        parameters.set(parameterIndex, value);
    }

    @Override
    public boolean execute() throws SQLException {
        return execute(query);
    }

    @Override
    public void addBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader value, int length) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setRef(int parameterIndex, Ref value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setBlob(int parameterIndex, Blob value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Clob value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setArray(int parameterIndex, Array value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setDate(int parameterIndex, Date value, Calendar calendar) throws SQLException {
        setObject(parameterIndex, calendar != null ? calendar : value);
    }

    @Override
    public void setTime(int parameterIndex, Time value, Calendar calendar) throws SQLException {
        setObject(parameterIndex, calendar != null ? calendar : value);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp value, Calendar calendar) throws SQLException {
        setObject(parameterIndex, calendar != null ? calendar : value);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        setNull(parameterIndex, sqlType);
    }

    @Override
    public void setURL(int parameterIndex, URL value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setRowId(int parameterIndex, RowId value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader value, long length) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream value, long length) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setNClob(int parameterIndex, Reader value, long length) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setObject(int parameterIndex, Object value, int sqlTargetType, int scaleOrLength) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream value, long length) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream value, long length) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setClob(int parameterIndex, Reader value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream value) throws SQLException {
        setObject(parameterIndex, value);
    }

    @Override
    public void setNClob(int parameterIndex, Reader value) throws SQLException {
        setObject(parameterIndex, value);
    }
}
