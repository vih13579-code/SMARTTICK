package DB;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized SQL Server connection factory for SMARTTICK.
 * Values can be overridden with environment variables:
 * SMARTTICK_DB_URL, SMARTTICK_DB_USERNAME and SMARTTICK_DB_PASSWORD.
 */
public class DBContext {

    private static final Logger LOGGER = Logger.getLogger(DBContext.class.getName());
    private static final Properties PROPERTIES = loadProperties();

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                LOGGER.warning("db.properties was not found. Environment variables will be used.");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot read db.properties", ex);
        }
        return properties;
    }

    private static String value(String environmentName, String legacyEnvironmentName,
            String propertyName, String fallback) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.trim().isEmpty()) {
            return environmentValue.trim();
        }
        String legacyEnvironmentValue = System.getenv(legacyEnvironmentName);
        if (legacyEnvironmentValue != null && !legacyEnvironmentValue.trim().isEmpty()) {
            return legacyEnvironmentValue.trim();
        }
        return PROPERTIES.getProperty(propertyName, fallback).trim();
    }

    public Connection getConnection() {
        String driver = value("SMARTTICK_DB_DRIVER", "FWATCH_DB_DRIVER",
                "db.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = value("SMARTTICK_DB_URL", "FWATCH_DB_URL", "db.url",
                "jdbc:sqlserver://localhost:1433;databaseName=FWatch;encrypt=true;trustServerCertificate=true");
        String username = value("SMARTTICK_DB_USERNAME", "FWATCH_DB_USERNAME", "db.username", "sa");
        String password = value("SMARTTICK_DB_PASSWORD", "FWATCH_DB_PASSWORD", "db.password", "123456");

        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("SQL Server JDBC driver is missing from the application classpath.", ex);
        } catch (SQLException ex) {
            String message = "Cannot connect to SMARTTICK database with user '" + username
                    + "'. Check SQL Server, port 1433 and db.properties.";
            LOGGER.log(Level.SEVERE, message, ex);
            throw new IllegalStateException(message, ex);
        }
    }
}
