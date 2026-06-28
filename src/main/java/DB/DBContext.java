package DB;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Skeleton DB connection class.
 * Copy db.properties.example thành db.properties rồi sửa thông tin kết nối.
 */
public class DBContext {
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = DBContext.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new SQLException("Missing src/main/resources/db.properties. Copy from db.properties.example first.");
            }
            props.load(in);
        } catch (IOException ex) {
            throw new SQLException("Cannot read db.properties", ex);
        }

        String server = props.getProperty("serverName", "localhost");
        String port = props.getProperty("portNumber", "1433");
        String database = props.getProperty("databaseName", "Smarttick");
        String user = props.getProperty("user", "sa");
        String password = props.getProperty("password", "");
        String encrypt = props.getProperty("encrypt", "false");
        String trust = props.getProperty("trustServerCertificate", "true");

        String url = "jdbc:sqlserver://" + server + ":" + port
                + ";databaseName=" + database
                + ";encrypt=" + encrypt
                + ";trustServerCertificate=" + trust;
        return DriverManager.getConnection(url, user, password);
    }
}
