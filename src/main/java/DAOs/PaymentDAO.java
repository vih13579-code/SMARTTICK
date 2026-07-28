package DAOs;

import DB.DBContext;
import Models.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PaymentDAO {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PAID = "PAID";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_EXPIRED = "EXPIRED";
    private static final String PROVIDER_VNPAY = "VNPAY";

    private final DBContext db = new DBContext();

    public Payment createOrReusePendingPayment(int orderId, int customerId, String transactionRef,
            Timestamp createdAt, Timestamp expiresAt) throws SQLException, PaymentValidationException {
        try (Connection connection = db.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long payableAmount;
                String orderPaymentStatus;
                String orderSql = "SELECT TotalAmount, DepositAmount, PaymentStatus "
                        + "FROM Orders WITH (UPDLOCK, HOLDLOCK) "
                        + "WHERE OrderID = ? AND CustomerID = ?";
                try (PreparedStatement statement = connection.prepareStatement(orderSql)) {
                    statement.setInt(1, orderId);
                    statement.setInt(2, customerId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new PaymentValidationException("ORDER_NOT_FOUND", "Order does not exist.");
                        }
                        orderPaymentStatus = resultSet.getString("PaymentStatus");
                        long totalAmount = resultSet.getLong("TotalAmount");
                        long depositAmount = resultSet.getLong("DepositAmount");
                        payableAmount = depositAmount > 0 ? depositAmount : totalAmount;
                    }
                }

                if (STATUS_PAID.equalsIgnoreCase(orderPaymentStatus)) {
                    throw new PaymentValidationException("ORDER_PAID", "This order has already been paid.");
                }
                if (payableAmount <= 0) {
                    throw new PaymentValidationException("INVALID_AMOUNT", "Order amount must be greater than zero.");
                }

                expireOldPendingPayments(connection, orderId, createdAt);
                Payment existing = findReusablePendingPayment(connection, orderId, payableAmount, createdAt);
                if (existing != null) {
                    connection.commit();
                    return existing;
                }

                String insertSql = "INSERT INTO Payments "
                        + "(OrderID, Provider, TransactionRef, Amount, Status, ExpiresAt, CreatedAt, UpdatedAt) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                    statement.setInt(1, orderId);
                    statement.setString(2, PROVIDER_VNPAY);
                    statement.setString(3, transactionRef);
                    statement.setLong(4, payableAmount);
                    statement.setString(5, STATUS_PENDING);
                    statement.setTimestamp(6, expiresAt);
                    statement.setTimestamp(7, createdAt);
                    statement.setTimestamp(8, createdAt);
                    statement.executeUpdate();
                }

                String updateOrderSql = "UPDATE Orders SET PaymentMethod = ?, PaymentStatus = ?, "
                        + "PaymentReference = ? WHERE OrderID = ? AND CustomerID = ?";
                try (PreparedStatement statement = connection.prepareStatement(updateOrderSql)) {
                    statement.setString(1, "VNPAY_QR");
                    statement.setString(2, STATUS_PENDING);
                    statement.setString(3, transactionRef);
                    statement.setInt(4, orderId);
                    statement.setInt(5, customerId);
                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Cannot attach VNPAY payment to order.");
                    }
                }

                connection.commit();
                Payment payment = new Payment();
                payment.setOrderId(orderId);
                payment.setProvider(PROVIDER_VNPAY);
                payment.setTransactionRef(transactionRef);
                payment.setAmount(payableAmount);
                payment.setStatus(STATUS_PENDING);
                payment.setCreatedAt(createdAt);
                payment.setExpiresAt(expiresAt);
                return payment;
            } catch (SQLException | PaymentValidationException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void expireOldPendingPayments(Connection connection, int orderId, Timestamp now) throws SQLException {
        String sql = "UPDATE Payments SET Status = ?, UpdatedAt = ? "
                + "WHERE OrderID = ? AND Status = ? "
                + "AND (ExpiresAt <= ? OR TransactionRef LIKE '%[^A-Za-z0-9]%')";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, STATUS_EXPIRED);
            statement.setTimestamp(2, now);
            statement.setInt(3, orderId);
            statement.setString(4, STATUS_PENDING);
            statement.setTimestamp(5, now);
            statement.executeUpdate();
        }
    }

    private Payment findReusablePendingPayment(Connection connection, int orderId, long amount, Timestamp now)
            throws SQLException {
        String sql = "SELECT TOP 1 * FROM Payments "
                + "WHERE OrderID = ? AND Provider = ? AND Status = ? AND Amount = ? AND ExpiresAt > ? "
                + "ORDER BY ID DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            statement.setString(2, PROVIDER_VNPAY);
            statement.setString(3, STATUS_PENDING);
            statement.setLong(4, amount);
            statement.setTimestamp(5, now);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapPayment(resultSet) : null;
            }
        }
    }

    public Payment findByTransactionRef(String transactionRef) throws SQLException {
        String sql = "SELECT * FROM Payments WHERE TransactionRef = ?";
        try (Connection connection = db.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transactionRef);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapPayment(resultSet) : null;
            }
        }
    }

    public PaymentStatusView getOrderPaymentStatus(int orderId, int customerId) throws SQLException {
        String sql = "SELECT o.OrderID, o.PaymentStatus, o.PaidAt, "
                + "p.TransactionRef, p.VnpayTransactionNo, p.Status AS ProviderStatus "
                + "FROM Orders o LEFT JOIN Payments p ON p.TransactionRef = o.PaymentReference "
                + "WHERE o.OrderID = ? AND o.CustomerID = ?";
        try (Connection connection = db.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            statement.setInt(2, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                PaymentStatusView view = new PaymentStatusView();
                view.setOrderId(resultSet.getInt("OrderID"));
                view.setPaymentStatus(resultSet.getString("PaymentStatus"));
                view.setPaidAt(resultSet.getTimestamp("PaidAt"));
                view.setTransactionRef(resultSet.getString("TransactionRef"));
                view.setTransactionNo(resultSet.getString("VnpayTransactionNo"));
                view.setProviderStatus(resultSet.getString("ProviderStatus"));
                return view;
            }
        }
    }

    public IpnResult processIpn(String transactionRef, long callbackAmount, String responseCode,
            String transactionStatus, String transactionNo, String bankCode, String bankTransactionNo,
            Timestamp payDate, String rawResponse) throws SQLException {
        try (Connection connection = db.getConnection()) {
            connection.setAutoCommit(false);
            try {
                PaymentAndOrder locked = lockPaymentAndOrder(connection, transactionRef);
                if (locked == null) {
                    connection.rollback();
                    return new IpnResult("01", "Order not found");
                }
                if (!STATUS_PENDING.equalsIgnoreCase(locked.paymentStatus)
                        || STATUS_PAID.equalsIgnoreCase(locked.orderPaymentStatus)) {
                    connection.rollback();
                    return new IpnResult("02", "Order already confirmed");
                }
                if (callbackAmount != locked.paymentAmount
                        || callbackAmount != locked.orderPayableAmount) {
                    connection.rollback();
                    return new IpnResult("04", "Invalid amount");
                }

                boolean success = "00".equals(responseCode) && "00".equals(transactionStatus);
                String finalStatus = success ? STATUS_PAID : mapFailureStatus(responseCode);
                String updatePaymentSql = "UPDATE Payments SET VnpayTransactionNo = ?, Status = ?, "
                        + "ResponseCode = ?, TransactionStatus = ?, BankCode = ?, BankTransactionNo = ?, "
                        + "PayDate = ?, RawResponse = ?, UpdatedAt = GETDATE() "
                        + "WHERE TransactionRef = ? AND Status = ?";
                try (PreparedStatement statement = connection.prepareStatement(updatePaymentSql)) {
                    statement.setString(1, transactionNo);
                    statement.setString(2, finalStatus);
                    statement.setString(3, responseCode);
                    statement.setString(4, transactionStatus);
                    statement.setString(5, bankCode);
                    statement.setString(6, bankTransactionNo);
                    statement.setTimestamp(7, payDate);
                    statement.setString(8, rawResponse);
                    statement.setString(9, transactionRef);
                    statement.setString(10, STATUS_PENDING);
                    if (statement.executeUpdate() != 1) {
                        connection.rollback();
                        return new IpnResult("02", "Order already confirmed");
                    }
                }

                String updateOrderSql;
                if (success) {
                    updateOrderSql = "UPDATE Orders SET PaymentStatus = ?, PaymentMethod = ?, "
                            + "PaymentReference = ?, PaidAt = COALESCE(?, GETDATE()) "
                            + "WHERE OrderID = ? AND PaymentStatus <> ?";
                } else {
                    updateOrderSql = "UPDATE Orders SET PaymentStatus = ?, PaymentMethod = ?, "
                            + "PaymentReference = ? WHERE OrderID = ? AND PaymentReference = ?";
                }
                try (PreparedStatement statement = connection.prepareStatement(updateOrderSql)) {
                    statement.setString(1, finalStatus);
                    statement.setString(2, "VNPAY_QR");
                    statement.setString(3, transactionRef);
                    if (success) {
                        statement.setTimestamp(4, payDate);
                        statement.setInt(5, locked.orderId);
                        statement.setString(6, STATUS_PAID);
                    } else {
                        statement.setInt(4, locked.orderId);
                        statement.setString(5, transactionRef);
                    }
                    statement.executeUpdate();
                }

                connection.commit();
                return new IpnResult("00", "Confirm Success");
            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private PaymentAndOrder lockPaymentAndOrder(Connection connection, String transactionRef)
            throws SQLException {
        String sql = "SELECT p.OrderID, p.Amount, p.Status AS PaymentStatus, "
                + "o.TotalAmount, o.DepositAmount, o.PaymentStatus AS OrderPaymentStatus "
                + "FROM Payments p WITH (UPDLOCK, ROWLOCK) "
                + "JOIN Orders o WITH (UPDLOCK, ROWLOCK) ON o.OrderID = p.OrderID "
                + "WHERE p.TransactionRef = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, transactionRef);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                PaymentAndOrder result = new PaymentAndOrder();
                result.orderId = resultSet.getInt("OrderID");
                result.paymentAmount = resultSet.getLong("Amount");
                result.paymentStatus = resultSet.getString("PaymentStatus");
                result.orderPaymentStatus = resultSet.getString("OrderPaymentStatus");
                long totalAmount = resultSet.getLong("TotalAmount");
                long depositAmount = resultSet.getLong("DepositAmount");
                result.orderPayableAmount = depositAmount > 0 ? depositAmount : totalAmount;
                return result;
            }
        }
    }

    private String mapFailureStatus(String responseCode) {
        if ("24".equals(responseCode)) {
            return STATUS_CANCELLED;
        }
        if ("11".equals(responseCode)) {
            return STATUS_EXPIRED;
        }
        return STATUS_FAILED;
    }

    private Payment mapPayment(ResultSet resultSet) throws SQLException {
        Payment payment = new Payment();
        payment.setId(resultSet.getLong("ID"));
        payment.setOrderId(resultSet.getInt("OrderID"));
        payment.setProvider(resultSet.getString("Provider"));
        payment.setTransactionRef(resultSet.getString("TransactionRef"));
        payment.setVnpayTransactionNo(resultSet.getString("VnpayTransactionNo"));
        payment.setAmount(resultSet.getLong("Amount"));
        payment.setStatus(resultSet.getString("Status"));
        payment.setResponseCode(resultSet.getString("ResponseCode"));
        payment.setTransactionStatus(resultSet.getString("TransactionStatus"));
        payment.setBankCode(resultSet.getString("BankCode"));
        payment.setBankTransactionNo(resultSet.getString("BankTransactionNo"));
        payment.setPayDate(resultSet.getTimestamp("PayDate"));
        payment.setExpiresAt(resultSet.getTimestamp("ExpiresAt"));
        payment.setCreatedAt(resultSet.getTimestamp("CreatedAt"));
        payment.setUpdatedAt(resultSet.getTimestamp("UpdatedAt"));
        return payment;
    }

    private static class PaymentAndOrder {

        private int orderId;
        private long paymentAmount;
        private long orderPayableAmount;
        private String paymentStatus;
        private String orderPaymentStatus;
    }

    public static class PaymentStatusView {

        private int orderId;
        private String paymentStatus;
        private String transactionRef;
        private String transactionNo;
        private String providerStatus;
        private Timestamp paidAt;

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getTransactionRef() {
            return transactionRef;
        }

        public void setTransactionRef(String transactionRef) {
            this.transactionRef = transactionRef;
        }

        public String getTransactionNo() {
            return transactionNo;
        }

        public void setTransactionNo(String transactionNo) {
            this.transactionNo = transactionNo;
        }

        public String getProviderStatus() {
            return providerStatus;
        }

        public void setProviderStatus(String providerStatus) {
            this.providerStatus = providerStatus;
        }

        public Timestamp getPaidAt() {
            return paidAt;
        }

        public void setPaidAt(Timestamp paidAt) {
            this.paidAt = paidAt;
        }
    }

    public static class IpnResult {

        private final String responseCode;
        private final String message;

        public IpnResult(String responseCode, String message) {
            this.responseCode = responseCode;
            this.message = message;
        }

        public String getResponseCode() {
            return responseCode;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class PaymentValidationException extends Exception {

        private final String code;

        public PaymentValidationException(String code, String message) {
            super(message);
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
