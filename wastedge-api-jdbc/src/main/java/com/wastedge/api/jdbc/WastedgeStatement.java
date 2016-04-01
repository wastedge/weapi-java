package com.wastedge.api.jdbc;

import com.wastedge.api.ApiQuery;
import com.wastedge.api.jdbc.weql.WeqlQueryParser;
import org.apache.commons.lang3.Validate;
import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WastedgeStatement implements Statement {
    private final WastedgeConnection connection;
    private ResultSet resultSet;
    private boolean closed;
    private int maxRows;

    WastedgeStatement(WastedgeConnection connection) {
        Validate.notNull(connection, "connection");

        this.connection = connection;
    }

    @Override
    public ResultSet executeQuery(String query) throws SQLException {
        return executeQuery(query, null);
    }

    ResultSet executeQuery(String query, List parameters) throws SQLException {
        Validate.notNull(query, "query");

        try {
            ApiQuery apiQuery = new WeqlQueryParser().parse(connection.getApi(), query, parameters);

            if (maxRows > 0) {
                apiQuery.setCount(maxRows);
            }

            resultSet = new WastedgeResultSet(apiQuery.executeReader(), apiQuery.getEntity());

            return resultSet;
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public int executeUpdate(String query) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void close() throws SQLException {
        closed = true;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int fieldSize) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int getMaxRows() throws SQLException {
        return maxRows;
    }

    @Override
    public void setMaxRows(int maxRows) throws SQLException {
        this.maxRows = maxRows;
    }

    @Override
    public void setEscapeProcessing(boolean escapeProcessing) throws SQLException {
        if (escapeProcessing) {
            throw new SQLFeatureNotSupportedException();
        }
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int timeout) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void cancel() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        // Ignore.
    }

    @Override
    public void setCursorName(String cursorName) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean execute(String s) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        if (resultSet == null) {
            throw new SQLException("Result set not available");
        }

        // The documentation states that this can only be called once.

        ResultSet resultSet = this.resultSet;
        this.resultSet = null;
        return resultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        if (direction != ResultSet.FETCH_FORWARD) {
            throw new SQLFeatureNotSupportedException();
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int size) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public void addBatch(String batch) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void clearBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public WastedgeConnection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int executeUpdate(String query, int autoGeneratedKeys) throws SQLException {
        return executeUpdate(query);
    }

    @Override
    public int executeUpdate(String query, int[] columnIndexes) throws SQLException {
        return executeUpdate(query);
    }

    @Override
    public int executeUpdate(String query, String[] columnNames) throws SQLException {
        return executeUpdate(query);
    }

    @Override
    public boolean execute(String query, int autoGeneratedKeys) throws SQLException {
        return execute(query);
    }

    @Override
    public boolean execute(String query, int[] columnIndexes) throws SQLException {
        return execute(query);
    }

    @Override
    public boolean execute(String query, String[] columnNames) throws SQLException {
        return execute(query);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        if (poolable) {
            throw new SQLFeatureNotSupportedException();
        }
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        // Ignore.
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return true;
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
