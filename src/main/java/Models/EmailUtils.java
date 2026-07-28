package Models;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/** SMTP sender configured through environment variables or email.properties. */
public final class EmailUtils {
    private static final Properties CONFIG = loadConfiguration();

    private EmailUtils() { }

    public static void send(final Email email) throws Exception {
        final String username = value("SMARTTICK_SMTP_USERNAME", "FWATCH_SMTP_USERNAME", "smtp.username", "");
        final String password = value("SMARTTICK_SMTP_PASSWORD", "FWATCH_SMTP_PASSWORD", "smtp.password", "");
        final String from = value("SMARTTICK_SMTP_FROM", "FWATCH_SMTP_FROM", "smtp.from", username);
        final String fromName = value("SMARTTICK_SMTP_FROM_NAME", "FWATCH_SMTP_FROM_NAME", "smtp.fromName", "SMARTTICK");
        if (isPlaceholder(username) || isPlaceholder(password) || from.trim().isEmpty()) {
            throw new IllegalStateException(
                    "SMTP is not configured. Set SMARTTICK_SMTP_USERNAME/SMARTTICK_SMTP_PASSWORD "
                    + "or update email.properties.");
        }

        String host = value("SMARTTICK_SMTP_HOST", "FWATCH_SMTP_HOST", "smtp.host", "smtp.gmail.com");
        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", host);
        mailProperties.put("mail.smtp.port", value("SMARTTICK_SMTP_PORT", "FWATCH_SMTP_PORT", "smtp.port", "587"));
        mailProperties.put("mail.smtp.auth", value("SMARTTICK_SMTP_AUTH", "FWATCH_SMTP_AUTH", "smtp.auth", "true"));
        mailProperties.put("mail.smtp.starttls.enable",
                value("SMARTTICK_SMTP_STARTTLS", "FWATCH_SMTP_STARTTLS", "smtp.starttls.enable", "true"));
        mailProperties.put("mail.smtp.ssl.trust",
                value("SMARTTICK_SMTP_SSL_TRUST", "FWATCH_SMTP_SSL_TRUST", "smtp.ssl.trust", host));
        mailProperties.put("mail.smtp.connectiontimeout",
                value("SMARTTICK_SMTP_CONNECTION_TIMEOUT", "FWATCH_SMTP_CONNECTION_TIMEOUT", "smtp.connectiontimeout", "10000"));
        mailProperties.put("mail.smtp.timeout",
                value("SMARTTICK_SMTP_TIMEOUT", "FWATCH_SMTP_TIMEOUT", "smtp.timeout", "10000"));
        mailProperties.put("mail.smtp.writetimeout",
                value("SMARTTICK_SMTP_WRITE_TIMEOUT", "FWATCH_SMTP_WRITE_TIMEOUT", "smtp.writetimeout", "10000"));

        Session session = Session.getInstance(mailProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from, fromName, "UTF-8"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getTo()));
        message.setSubject(email.getSubject(), "UTF-8");
        message.setContent(email.getContent(), "text/html; charset=utf-8");
        Transport.send(message);
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = EmailUtils.class.getClassLoader().getResourceAsStream("email.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ignored) {
            // Environment variables can still provide the complete configuration.
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
        return CONFIG.getProperty(propertyName, fallback).trim();
    }

    private static boolean isPlaceholder(String value) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        String normalized = value.trim().toLowerCase();
        return normalized.startsWith("your_") || normalized.contains("example.com");
    }
}
