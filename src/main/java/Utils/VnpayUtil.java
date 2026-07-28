package Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

public final class VnpayUtil {

    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String SECURE_HASH = "vnp_SecureHash";
    private static final String SECURE_HASH_TYPE = "vnp_SecureHashType";

    private VnpayUtil() {
    }

    public static String createSecureHash(String secret, Map<String, String> fields) {
        return hmacSha512(secret, buildHashData(fields));
    }

    public static boolean verifySecureHash(String secret, Map<String, String> fields, String receivedHash) {
        if (receivedHash == null || receivedHash.trim().isEmpty()) {
            return false;
        }
        String expectedHash = createSecureHash(secret, fields);
        return constantTimeEquals(expectedHash, receivedHash.trim());
    }

    public static String buildPaymentUrl(String baseUrl, Map<String, String> fields, String secret) {
        TreeMap<String, String> sorted = filteredAndSorted(fields);
        String hashData = buildEncodedPairs(sorted);
        String secureHash = hmacSha512(secret, hashData);
        return baseUrl + "?" + hashData + "&vnp_SecureHash=" + secureHash;
    }

    public static String buildHashData(Map<String, String> fields) {
        return buildEncodedPairs(filteredAndSorted(fields));
    }

    private static TreeMap<String, String> filteredAndSorted(Map<String, String> fields) {
        TreeMap<String, String> sorted = new TreeMap<>();
        if (fields == null) {
            return sorted;
        }
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null || value.isEmpty()
                    || SECURE_HASH.equals(key) || SECURE_HASH_TYPE.equals(key)) {
                continue;
            }
            sorted.put(key, value);
        }
        return sorted;
    }

    private static String buildEncodedPairs(Map<String, String> sortedFields) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedFields.entrySet()) {
            if (result.length() > 0) {
                result.append('&');
            }
            result.append(urlEncode(entry.getKey()))
                    .append('=')
                    .append(urlEncode(entry.getValue()));
        }
        return result.toString();
    }

    public static String hmacSha512(String secret, String data) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec key = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA512);
            mac.init(key);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(bytes.length * 2);
            for (byte item : bytes) {
                result.append(String.format("%02x", item & 0xff));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new IllegalStateException("Cannot create VNPAY HMAC SHA512 signature", ex);
        }
    }

    public static Map<String, String> extractParameters(HttpServletRequest request) {
        TreeMap<String, String> fields = new TreeMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] values = entry.getValue();
            if (values != null && values.length > 0 && values[0] != null) {
                fields.put(entry.getKey(), values[0]);
            }
        }
        return Collections.unmodifiableMap(fields);
    }

    public static String getClientIp(HttpServletRequest request) {
        String forwarded = firstNonBlank(
                request.getHeader("X-Forwarded-For"),
                request.getHeader("X-Real-IP"),
                request.getRemoteAddr());
        if (forwarded != null && forwarded.contains(",")) {
            forwarded = forwarded.substring(0, forwarded.indexOf(',')).trim();
        }
        if ("0:0:0:0:0:0:0:1".equals(forwarded) || "::1".equals(forwarded)) {
            return "127.0.0.1";
        }
        return forwarded == null || forwarded.isEmpty() ? "127.0.0.1" : forwarded;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()
                    && !"unknown".equalsIgnoreCase(value.trim())) {
                return value.trim();
            }
        }
        return null;
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        byte[] left = expected.toLowerCase().getBytes(StandardCharsets.US_ASCII);
        byte[] right = actual.toLowerCase().getBytes(StandardCharsets.US_ASCII);
        int difference = left.length ^ right.length;
        int length = Math.max(left.length, right.length);
        for (int i = 0; i < length; i++) {
            byte leftByte = i < left.length ? left[i] : 0;
            byte rightByte = i < right.length ? right[i] : 0;
            difference |= leftByte ^ rightByte;
        }
        return difference == 0;
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.US_ASCII.name());
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("US-ASCII is not available", ex);
        }
    }
}
