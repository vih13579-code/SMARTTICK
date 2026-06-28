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
            String propertyName, String legacyPropertyName, String fallback) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.trim().isEmpty()) {
            return environmentValue.trim();
        }
        String legacyEnvironmentValue = System.getenv(legacyEnvironmentName);
        if (legacyEnvironmentValue != null && !legacyEnvironmentValue.trim().isEmpty()) {
            return legacyEnvironmentValue.trim();
        }
        String propertyValue = PROPERTIES.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue.trim();
        }
        String legacyPropertyValue = PROPERTIES.getProperty(legacyPropertyName);
        if (legacyPropertyValue != null && !legacyPropertyValue.trim().isEmpty()) {
            return legacyPropertyValue.trim();
        }
        return fallback;
    }

    public Connection getConnection() {
        String driver = value("SMARTTICK_DB_DRIVER", "FWATCH_DB_DRIVER",
                "db.driver", "driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String url = value("SMARTTICK_DB_URL", "FWATCH_DB_URL", "db.url", "url", null);
        if (url == null) {
            String server = PROPERTIES.getProperty("serverName", "localhost").trim();
            String port = PROPERTIES.getProperty("portNumber", "1433").trim();
            String database = PROPERTIES.getProperty("databaseName", "Smarttick").trim();
            String encrypt = PROPERTIES.getProperty("encrypt", "false").trim();
            String trust = PROPERTIES.getProperty("trustServerCertificate", "true").trim();
            url = "jdbc:sqlserver://" + server + ":" + port
                    + ";databaseName=" + database
                    + ";encrypt=" + encrypt
                    + ";trustServerCertificate=" + trust;
        }
        String username = value("SMARTTICK_DB_USERNAME", "FWATCH_DB_USERNAME",
                "db.username", "user", "sa");
        String password = value("SMARTTICK_DB_PASSWORD", "FWATCH_DB_PASSWORD",
                "db.password", "password", "");

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
