package entity;

import io.dropwizard.Configuration;

public class ApplicationConfiguration extends Configuration {
    private static ApplicationConfiguration CONFIGURATION = null;
    private String jdbcURL;
    private String userName;
    private String password;
    private String sqlDriver;
    private String inputFolderPath;

    public ApplicationConfiguration(){ CONFIGURATION = this; }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
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

    public String getSqlDriver() {
        return sqlDriver;
    }

    public void setSqlDriver(String sqlDriver) {
        this.sqlDriver = sqlDriver;
    }

    public String getInputFolderPath() {
        return inputFolderPath;
    }

    public void setInputFolderPath(String inputFolderPath) {
        this.inputFolderPath = inputFolderPath;
    }
}
