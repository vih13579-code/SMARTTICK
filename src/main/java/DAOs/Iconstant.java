package DAOs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Google OAuth endpoints and environment-based client configuration. */
public final class Iconstant {
    private static final Logger LOGGER = Logger.getLogger(Iconstant.class.getName());
    private static final Properties PROPERTIES = loadProperties();

    private Iconstant() { }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = Iconstant.class.getClassLoader().getResourceAsStream("google-oauth.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                LOGGER.warning("google-oauth.properties was not found. Environment or system properties will be used.");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot read google-oauth.properties", ex);
        }
        return properties;
    }

    public static final String GOOGLE_CLIENT_ID = value(
            "SMARTTICK_GOOGLE_CLIENT_ID", "FWATCH_GOOGLE_CLIENT_ID",
            "smarttick.google.clientId", "fwatch.google.clientId", "your_google_client_id");
    public static final String GOOGLE_CLIENT_SECRET = value(
            "SMARTTICK_GOOGLE_CLIENT_SECRET", "FWATCH_GOOGLE_CLIENT_SECRET",
            "smarttick.google.clientSecret", "fwatch.google.clientSecret", "your_google_client_secret");
    public static final String GOOGLE_REDIRECT_URI = value(
            "SMARTTICK_GOOGLE_REDIRECT_URI", "FWATCH_GOOGLE_REDIRECT_URI",
            "smarttick.google.redirectUri", "fwatch.google.redirectUri",
            "http://localhost:8080/SMARTTICK/GoogleLogin");
    public static final String GOOGLE_AUTH_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String GOOGLE_SCOPE = "openid email profile";

    public static final String GOOGLE_GRANT_TYPE = "authorization_code";
    public static final String GOOGLE_LINK_GET_TOKEN = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_LINK_GET_USER_INFO =
            "https://openidconnect.googleapis.com/v1/userinfo";

    public static boolean isConfigured() {
        return !isPlaceholder(GOOGLE_CLIENT_ID) && !isPlaceholder(GOOGLE_CLIENT_SECRET)
                && GOOGLE_REDIRECT_URI != null && !GOOGLE_REDIRECT_URI.trim().isEmpty();
    }

    private static boolean isPlaceholder(String value) {
        return value == null || value.trim().isEmpty() || value.startsWith("your_google_");
    }

    private static String value(String environmentName, String legacyEnvironmentName,
            String systemProperty, String legacySystemProperty, String fallback) {
        String environmentValue = System.getenv(environmentName);
        if (environmentValue != null && !environmentValue.trim().isEmpty()) {
            return environmentValue.trim();
        }
        String legacyEnvironmentValue = System.getenv(legacyEnvironmentName);
        if (legacyEnvironmentValue != null && !legacyEnvironmentValue.trim().isEmpty()) {
            return legacyEnvironmentValue.trim();
        }
        String propertyValue = System.getProperty(systemProperty);
        if (propertyValue != null && !propertyValue.trim().isEmpty()) {
            return propertyValue.trim();
        }
        String legacyPropertyValue = System.getProperty(legacySystemProperty);
        if (legacyPropertyValue != null && !legacyPropertyValue.trim().isEmpty()) {
            return legacyPropertyValue.trim();
        }
        String fileValue = PROPERTIES.getProperty(systemProperty);
        if (fileValue != null && !fileValue.trim().isEmpty()) {
            return fileValue.trim();
        }
        String fileLegacyValue = PROPERTIES.getProperty(legacySystemProperty);
        if (fileLegacyValue != null && !fileLegacyValue.trim().isEmpty()) {
            return fileLegacyValue.trim();
        }
        String fileEnvValue = PROPERTIES.getProperty(environmentName);
        if (fileEnvValue != null && !fileEnvValue.trim().isEmpty()) {
            return fileEnvValue.trim();
        }
        String fileLegacyEnvValue = PROPERTIES.getProperty(legacyEnvironmentName);
        if (fileLegacyEnvValue != null && !fileLegacyEnvValue.trim().isEmpty()) {
            return fileLegacyEnvValue.trim();
        }
        return fallback;
    }
}
