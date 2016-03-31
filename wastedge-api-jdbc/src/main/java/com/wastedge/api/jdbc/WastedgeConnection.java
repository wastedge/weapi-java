package com.wastedge.api.jdbc;

import com.wastedge.api.Api;
import com.wastedge.api.ApiCredentials;
import com.wastedge.api.ApiException;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

@SuppressWarnings("unused")
public class WastedgeConnection implements Connection {
    private Api api;
    private boolean autoCommit;
    private boolean closed;

    public WastedgeConnection(Api api) {
        Validate.notNull(api, "api");

        this.api = api;
    }

    public WastedgeConnection(String connectionString) throws IOException {
        Validate.notNull(connectionString, "connectionString");

        if (!connectionString.startsWith("jdbc:")) {
            throw new ApiException("Unsupported connection string");
        }

        api = new Api(ApiCredentials.parse(connectionString.substring(5)));
    }

    public Api getApi() {
        return api;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new WastedgeStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String query) throws SQLException {
        Validate.notNull(query, "query");

        return new WastedgePreparedStatement(this, query);
    }

    @Override
    public CallableStatement prepareCall(String query) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public String nativeSQL(String query) throws SQLException {
        return query;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return autoCommit;
    }

    @Override
    public void commit() throws SQLException {
        // Ignore.
    }

    @Override
    public void rollback() throws SQLException {
        // Ignore.
    }

    @Override
    public void close() throws SQLException {
        closed = true;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        if (!readOnly) {
            throw new SQLFeatureNotSupportedException();
        }
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return true;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        if (catalog != null) {
            throw new SQLFeatureNotSupportedException();
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setTransactionIsolation(int transactionIsolation) throws SQLException {
        if (transactionIsolation != TRANSACTION_NONE) {
            throw new SQLFeatureNotSupportedException();
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return TRANSACTION_NONE;
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
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String query, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareStatement(query);
    }

    @Override
    public CallableStatement prepareCall(String query, int resultSetType, int resultSetConcurrency) throws SQLException {
        return prepareCall(query);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setHoldability(int i) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Savepoint setSavepoint(String savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String query, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return prepareStatement(query);
    }

    @Override
    public CallableStatement prepareCall(String query, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return prepareCall(query);
    }

    @Override
    public PreparedStatement prepareStatement(String query, int autoGeneratedKeys) throws SQLException {
        return prepareStatement(query);
    }

    @Override
    public PreparedStatement prepareStatement(String query, int[] columnIndexes) throws SQLException {
        return prepareStatement(query);
    }

    @Override
    public PreparedStatement prepareStatement(String query, String[] columnNames) throws SQLException {
        return prepareStatement(query);
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return true;
    }

    @Override
    public void setClientInfo(String s, String s1) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String s) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String s, Object[] objects) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String s, Object[] objects) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        if (schema != null) {
            throw new SQLFeatureNotSupportedException();
        }
    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException();
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
