package us.mkaulfers.hardcoreseasons.models;

public class MySQLConfig {
    public String host;
    public int port;
    public String database;
    public String username;
    public String password;
    public int updateInterval;

    public MySQLConfig(String host, int port, String database, String username, String password, int updateInterval) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.updateInterval = updateInterval;
    }
}
