package Configs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.servlet.ServletContext;

public final class VnpayConfig {

    private static final String CONFIG_PATH = "/WEB-INF/classes/vnpay.properties";
    private static volatile VnpayConfig instance;

    private final String paymentUrl;
    private final String tmnCode;
    private final String hashSecret;
    private final String returnUrl;
    private final String ipnUrl;
    private final String version;
    private final String command;
    private final String currCode;
    private final String bankCode;
    private final int expireMinutes;

    private VnpayConfig(Properties properties) {
        paymentUrl = value(properties, "vnpay.url");
        tmnCode = value(properties, "vnpay.tmnCode");
        hashSecret = value(properties, "vnpay.hashSecret");
        returnUrl = value(properties, "vnpay.returnUrl");
        ipnUrl = value(properties, "vnpay.ipnUrl");
        version = value(properties, "vnpay.version");
        command = value(properties, "vnpay.command");
        currCode = value(properties, "vnpay.currCode");
        bankCode = value(properties, "vnpay.bankCode");
        expireMinutes = parseExpireMinutes(value(properties, "vnpay.expireMinutes"));
    }

    public static VnpayConfig getInstance(ServletContext context) {
        VnpayConfig local = instance;
        if (local == null) {
            synchronized (VnpayConfig.class) {
                local = instance;
                if (local == null) {
                    instance = local = load(context);
                }
            }
        }
        return local;
    }

    private static VnpayConfig load(ServletContext context) {
        Properties properties = new Properties();
        try (InputStream input = context.getResourceAsStream(CONFIG_PATH)) {
            if (input == null) {
                throw new IllegalStateException("Missing VNPAY configuration at " + CONFIG_PATH);
            }
            properties.load(input);
            return new VnpayConfig(properties);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot read VNPAY configuration at " + CONFIG_PATH, ex);
        }
    }

    private static String value(Properties properties, String key) {
        return properties.getProperty(key, "").trim();
    }

    private static int parseExpireMinutes(String value) {
        if (value.isEmpty()) {
            return 15;
        }
        try {
            int minutes = Integer.parseInt(value);
            if (minutes < 5 || minutes > 60) {
                throw new IllegalArgumentException("vnpay.expireMinutes must be between 5 and 60");
            }
            return minutes;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("vnpay.expireMinutes must be a number", ex);
        }
    }

    public void validateSandboxConfiguration() {
        require(paymentUrl, "vnpay.url");
        require(tmnCode, "vnpay.tmnCode");
        require(hashSecret, "vnpay.hashSecret");
        require(returnUrl, "vnpay.returnUrl");
        require(ipnUrl, "vnpay.ipnUrl");
        require(version, "vnpay.version");
        require(command, "vnpay.command");
        require(currCode, "vnpay.currCode");

        try {
            URI uri = new URI(paymentUrl);
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || !"sandbox.vnpayment.vn".equalsIgnoreCase(uri.getHost())) {
                throw new IllegalStateException(
                        "vnpay.url must point to https://sandbox.vnpayment.vn; production URLs are blocked");
            }
            validateCallbackUrl(returnUrl, "vnpay.returnUrl");
            validateCallbackUrl(ipnUrl, "vnpay.ipnUrl");
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Invalid URL in vnpay.properties", ex);
        }
    }

    private static void validateCallbackUrl(String value, String key) throws URISyntaxException {
        URI uri = new URI(value);
        if (uri.getScheme() == null || uri.getHost() == null
                || (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme()))) {
            throw new IllegalStateException(key + " must be an absolute HTTP(S) URL");
        }
    }

    private static void require(String value, String key) {
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(key + " is not configured");
        }
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public String getTmnCode() {
        return tmnCode;
    }

    public String getHashSecret() {
        return hashSecret;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public String getIpnUrl() {
        return ipnUrl;
    }

    public String getVersion() {
        return version;
    }

    public String getCommand() {
        return command;
    }

    public String getCurrCode() {
        return currCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public int getExpireMinutes() {
        return expireMinutes;
    }
}
