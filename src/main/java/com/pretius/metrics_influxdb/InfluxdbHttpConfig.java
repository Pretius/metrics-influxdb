package com.pretius.metrics_influxdb;

import java.util.concurrent.TimeUnit;

public class InfluxdbHttpConfig {
    private String host;
    private int port;
    private String path = "";
    private String database;
    private String userName;
    private String password;
    private TimeUnit timePrecision = TimeUnit.MILLISECONDS;
    private InfluxdbVersion version = InfluxdbVersion.VERSION_0_9;



    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TimeUnit getTimePrecision() {
        return timePrecision;
    }

    /**
     * @param timePrecision The precision of the epoch time that is sent to the server,
     *                      should be TimeUnit.MILLISECONDS unless you are using a custom Clock
     *                      that does not return milliseconds epoch time for getTime().
     *                      Default is TimeUnit.MILLISECONDS
     */
    public void setTimePrecision(TimeUnit timePrecision) {
        this.timePrecision = timePrecision;
    }

    public InfluxdbVersion getVersion() {
        return version;
    }

    public void setVersion(InfluxdbVersion version) {
        this.version = version;
    }
}
