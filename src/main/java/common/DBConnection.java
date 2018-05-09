package common;

import entity.ApplicationConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import server.LibraryServerApp;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static Log logger = LogFactory.getLog(DBConnection.class);
    private static Connection connect;
    private static ApplicationConfiguration configuration;

    public static Connection createConnection() {
        configuration = LibraryServerApp.getConfiguration();
        try {
            Class.forName(configuration.getSqlDriver());
        } catch (ClassNotFoundException e) {
            logger.error("Error in JDBC class Name");
        }
        try {
            connect = DriverManager
                    .getConnection(configuration.getJdbcURL()+"?user="+configuration.getUserName()+"&password="+configuration.getPassword()+"&useSSL=false");
        } catch (SQLException e) {
            logger.error("Database name or username or password is wrong");
        }
        return connect;
    }

    public static Connection getDBConnection() {
        return connect;
    }
}
