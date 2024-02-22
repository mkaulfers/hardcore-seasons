package com.xtwistedx.models;

public class MySQLConfig {
    String host;
    int port;
    String database;
    String username;
    String password;

    public MySQLConfig(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }
}
