package Utils;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory payment callback store used only by the classroom demo flow. */
public final class DemoPaymentStore {
    private static final long LIFETIME_MILLIS = 10 * 60 * 1000L;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Map<String, Payment> PAYMENTS = new ConcurrentHashMap<>();

    private DemoPaymentStore() { }

    public static Payment create(int customerId, long amount, String confirmationUrlPrefix) {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Payment payment = new Payment(token, customerId, amount,
                System.currentTimeMillis() + LIFETIME_MILLIS, confirmationUrlPrefix + token);
        PAYMENTS.put(token, payment);
        cleanupExpired();
        return payment;
    }

    public static Payment get(String token) {
        Payment payment = token == null ? null : PAYMENTS.get(token);
        if (payment != null && payment.isExpired()) {
            PAYMENTS.remove(token);
            return null;
        }
        return payment;
    }

    public static boolean markPaid(String token) {
        Payment payment = get(token);
        if (payment == null || payment.isConsumed()) {
            return false;
        }
        payment.markPaid();
        return true;
    }

    public static boolean isPaid(String token, int customerId) {
        Payment payment = get(token);
        return payment != null && payment.getCustomerId() == customerId
                && payment.isPaid() && !payment.isConsumed();
    }

    public static boolean consume(String token, int customerId) {
        Payment payment = get(token);
        if (payment == null || payment.getCustomerId() != customerId || !payment.isPaid()) {
            return false;
        }
        payment.consume();
        PAYMENTS.remove(token);
        return true;
    }

    public static void remove(String token) {
        if (token != null) {
            PAYMENTS.remove(token);
        }
    }

    private static void cleanupExpired() {
        for (Map.Entry<String, Payment> entry : PAYMENTS.entrySet()) {
            if (entry.getValue().isExpired()) {
                PAYMENTS.remove(entry.getKey());
            }
        }
    }

    public static final class Payment {
        private final String token;
        private final int customerId;
        private final long amount;
        private final long expiresAt;
        private final String confirmationUrl;
        private volatile boolean paid;
        private volatile boolean consumed;

        private Payment(String token, int customerId, long amount, long expiresAt,
                String confirmationUrl) {
            this.token = token;
            this.customerId = customerId;
            this.amount = amount;
            this.expiresAt = expiresAt;
            this.confirmationUrl = confirmationUrl;
        }

        public String getToken() { return token; }
        public int getCustomerId() { return customerId; }
        public long getAmount() { return amount; }
        public long getExpiresAt() { return expiresAt; }
        public String getConfirmationUrl() { return confirmationUrl; }
        public boolean isPaid() { return paid; }
        public boolean isConsumed() { return consumed; }
        public boolean isExpired() { return System.currentTimeMillis() > expiresAt; }
        private void markPaid() { paid = true; }
        private void consume() { consumed = true; }
    }
}
