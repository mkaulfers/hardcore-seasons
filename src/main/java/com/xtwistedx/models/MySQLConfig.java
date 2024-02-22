package com.xtwistedx.models;

public class MySQLConfig {
    String host;
    int port;
    String database;
    String username;
    String password;
    int updateInterval;

    public MySQLConfig(String host, int port, String database, String username, String password, int updateInterval) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.updateInterval = updateInterval;
    }
}
