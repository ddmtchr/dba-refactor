package com.ddmtchr.dbarefactor.config;


import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

public class XADataSourceWrapper implements DataSource {
    private final XADataSource xaDataSource;

    public XADataSourceWrapper(XADataSource xaDataSource) {
        this.xaDataSource = xaDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        XAConnection xaConnection = xaDataSource.getXAConnection();
        return xaConnection.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        XAConnection xaConnection = xaDataSource.getXAConnection(username, password);
        return xaConnection.getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger("XADataSourceWrapper");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(xaDataSource)) {
            return iface.cast(xaDataSource);
        }
        throw new SQLException("Cannot unwrap to " + iface.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(xaDataSource);
    }
}
