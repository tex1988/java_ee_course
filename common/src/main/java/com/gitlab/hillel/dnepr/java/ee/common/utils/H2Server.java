package com.gitlab.hillel.dnepr.java.ee.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class H2Server implements AutoCloseable {
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASSWORD = "";
    private static final Map<String, JdbcConnectionPool> JDBC_CONNECTION_POOLS = new HashMap<>();
    private final int port;
    private Server embeddedH2Server;

    public H2Server(int port) {
        this.port = port;
        if (port < 1024 || port > 65535) {
            throw new IllegalArgumentException("Connection port must be 1 - 65535");
        }
    }

    public H2Server() {
        this(NetUtils.getFreePorts(1024, 65535, 1).get(0));
    }

    public void start() throws SQLException {
        if (embeddedH2Server == null) {
            embeddedH2Server = Server.createTcpServer(
                    "-tcp",
                    "-tcpAllowOthers",
                    "-webAllowOthers",
                    "-ifNotExists",
                    "-tcpPort",
                    String.valueOf(this.port));
            try {
                Class.forName("org.h2.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }
        }
        if (embeddedH2Server.isRunning(true)) {
            LOGGER.info("H2 server is already started. Port: {}", this.port);
        } else {
            embeddedH2Server.start();
            if (embeddedH2Server.isRunning(true)) {
                LOGGER.info("H2 server is successfully started. Port: {}", this.port);
            } else {
                throw new SQLException("Could not start H2 server.");
            }
        }
    }

    public void stop() {
        //Server.shutdownTcpServer("tcp://localhost:" + this.port, StringUtils.EMPTY, true, true);
        if (embeddedH2Server == null) {
            throw new IllegalStateException("H2 server is not started");
        }
        if (embeddedH2Server.isRunning(true)) {
            embeddedH2Server.stop();
        }
    }

    public Connection getConnection(String dbName, String user, String password) throws SQLException {
        if (embeddedH2Server == null || !embeddedH2Server.isRunning(true)) {
            start();
        }
        final String connectionString = String.format(
                "jdbc:h2:tcp://%s:%s/%s",
                NetUtils.getHostName(),
                this.port,
                dbName
        );
        final String lowerCaseConnectionString = connectionString.toLowerCase();
        if (!JDBC_CONNECTION_POOLS.containsKey(lowerCaseConnectionString)
                || JDBC_CONNECTION_POOLS.get(lowerCaseConnectionString).getActiveConnections() == 0
                || JDBC_CONNECTION_POOLS.get(lowerCaseConnectionString).getConnection().isClosed()) {
            JDBC_CONNECTION_POOLS.put(lowerCaseConnectionString, JdbcConnectionPool.create(connectionString, user, password));
        }
        return JDBC_CONNECTION_POOLS.get(lowerCaseConnectionString).getConnection();
    }

    public Connection getConnection(String dbName) throws SQLException {
        return getConnection(dbName, DEFAULT_USER, DEFAULT_PASSWORD);
    }

    @Override
    public void close() {
        this.stop();
    }
}
