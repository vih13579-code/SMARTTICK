package DAOs;

import DB.DBContext;
import Models.CustomerVoucher;
import Models.Voucher;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerVoucherDAO {

    private static final Logger LOGGER =
            Logger.getLogger(CustomerVoucherDAO.class.getName());
    private static final String CUSTOMER_VOUCHER_SELECT =
            "SELECT cv.CustomerID, cv.ExpirationDate, cv.Quantity, "
            + "v.VoucherID, v.VoucherCode, v.VoucherType, v.VoucherValue, "
            + "v.MaxDiscountAmount, v.MinOrderValue, v.EndDate "
            + "FROM dbo.CustomerVoucher cv "
            + "JOIN dbo.Vouchers v ON cv.VoucherID = v.VoucherID "
            + "WHERE cv.CustomerID = ? AND cv.Quantity > 0 "
            + "AND v.EndDate >= ? "
            + "AND (cv.ExpirationDate IS NULL OR cv.ExpirationDate >= ?) ";

    private final DBContext dbContext = new DBContext();

    public List<CustomerVoucher> getVoucherOfCustomer(int customerId) {
        return getSavedVouchersOfCustomer(customerId);
    }

    public List<CustomerVoucher> getSavedVouchersOfCustomer(int customerId) {
        List<CustomerVoucher> vouchers = new ArrayList<>();
        String sql = CUSTOMER_VOUCHER_SELECT
                + "ORDER BY v.EndDate ASC, v.VoucherID DESC";
        LocalDateTime now = LocalDateTime.now();
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setTimestamp(2, Timestamp.valueOf(now));
            statement.setTimestamp(3, Timestamp.valueOf(now));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vouchers.add(mapCustomerVoucher(resultSet));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load saved vouchers for customer " + customerId, ex);
        }
        return vouchers;
    }

    public List<Voucher> getClaimableVouchersForCustomer(int customerId) {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT v.VoucherID, v.VoucherCode, v.VoucherType, v.VoucherValue, "
                + "v.MaxDiscountAmount, v.MinOrderValue, v.EndDate "
                + "FROM dbo.Vouchers v WHERE v.EndDate >= ? "
                + "AND NOT EXISTS (SELECT 1 FROM dbo.CustomerVoucher cv "
                + "WHERE cv.CustomerID = ? AND cv.VoucherID = v.VoucherID "
                + "AND cv.Quantity > 0) "
                + "ORDER BY v.EndDate ASC, v.VoucherID DESC";
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            statement.setInt(2, customerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vouchers.add(mapVoucher(resultSet));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Cannot load claimable vouchers for customer " + customerId, ex);
        }
        return vouchers;
    }

    public int saveVoucherForCustomer(int customerId, int voucherId) {
        String reactivateSql = "UPDATE cv SET Quantity = 1, ExpirationDate = NULL "
                + "FROM dbo.CustomerVoucher cv JOIN dbo.Vouchers v "
                + "ON cv.VoucherID = v.VoucherID "
                + "WHERE cv.CustomerID = ? AND cv.VoucherID = ? "
                + "AND cv.Quantity <= 0 AND v.EndDate >= ?";
        String insertSql = "INSERT INTO dbo.CustomerVoucher "
                + "(CustomerID, VoucherID, ExpirationDate, Quantity) "
                + "SELECT ?, v.VoucherID, NULL, 1 FROM dbo.Vouchers v "
                + "WHERE v.VoucherID = ? AND v.EndDate >= ? "
                + "AND NOT EXISTS (SELECT 1 FROM dbo.CustomerVoucher cv "
                + "WHERE cv.CustomerID = ? AND cv.VoucherID = v.VoucherID)";
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        try (Connection connection = dbContext.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(reactivateSql)) {
                statement.setInt(1, customerId);
                statement.setInt(2, voucherId);
                statement.setTimestamp(3, now);
                if (statement.executeUpdate() > 0) {
                    return 1;
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                statement.setInt(1, customerId);
                statement.setInt(2, voucherId);
                statement.setTimestamp(3, now);
                statement.setInt(4, customerId);
                return statement.executeUpdate();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING,
                    "Cannot save voucher " + voucherId + " for customer " + customerId, ex);
            return 0;
        }
    }

    public int syncAvailableVouchersForCustomer(int customerId) {
        String sql = "INSERT INTO dbo.CustomerVoucher "
                + "(CustomerID, VoucherID, ExpirationDate, Quantity) "
                + "SELECT c.CustomerID, v.VoucherID, NULL, 1 "
                + "FROM dbo.Customers c CROSS JOIN dbo.Vouchers v "
                + "WHERE c.CustomerID = ? AND c.IsBlock = 0 AND c.IsDeleted = 0 "
                + "AND v.EndDate >= ? "
                + "AND NOT EXISTS (SELECT 1 FROM dbo.CustomerVoucher cv "
                + "WHERE cv.CustomerID = c.CustomerID AND cv.VoucherID = v.VoucherID)";
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            return statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Cannot synchronize vouchers for customer " + customerId, ex);
            return 0;
        }
    }

    public int assignVoucherToCustomer(int customerId, int voucherId, int quantity,
            LocalDateTime expirationDate) {
        String updateSql = "UPDATE dbo.CustomerVoucher "
                + "SET Quantity = Quantity + ?, ExpirationDate = ? "
                + "WHERE CustomerID = ? AND VoucherID = ?";
        String insertSql = "INSERT INTO dbo.CustomerVoucher "
                + "(CustomerID, VoucherID, Quantity, ExpirationDate) VALUES (?, ?, ?, ?)";
        try (Connection connection = dbContext.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                int affected;
                try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
                    statement.setInt(1, quantity);
                    setTimestamp(statement, 2, expirationDate);
                    statement.setInt(3, customerId);
                    statement.setInt(4, voucherId);
                    affected = statement.executeUpdate();
                }
                if (affected == 0) {
                    try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                        statement.setInt(1, customerId);
                        statement.setInt(2, voucherId);
                        statement.setInt(3, quantity);
                        setTimestamp(statement, 4, expirationDate);
                        affected = statement.executeUpdate();
                    }
                }
                connection.commit();
                connection.setAutoCommit(originalAutoCommit);
                return affected;
            } catch (SQLException ex) {
                connection.rollback();
                connection.setAutoCommit(originalAutoCommit);
                throw ex;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Cannot assign voucher " + voucherId + " to customer " + customerId, ex);
            return 0;
        }
    }

    public boolean isVoucherAlreadyAssigned(int customerId, int voucherId) {
        return getVoucherQuantity(customerId, voucherId) > 0;
    }

    public int getVoucherQuantity(int customerId, int voucherId) {
        String sql = "SELECT Quantity FROM dbo.CustomerVoucher "
                + "WHERE CustomerID = ? AND VoucherID = ?";
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setInt(2, voucherId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("Quantity") : 0;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load customer voucher quantity", ex);
            return 0;
        }
    }

    public CustomerVoucher getVoucherById(int customerId, int voucherId) {
        String sql = CUSTOMER_VOUCHER_SELECT + "AND cv.VoucherID = ?";
        LocalDateTime now = LocalDateTime.now();
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setTimestamp(2, Timestamp.valueOf(now));
            statement.setTimestamp(3, Timestamp.valueOf(now));
            statement.setInt(4, voucherId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapCustomerVoucher(resultSet) : null;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load customer voucher", ex);
            return null;
        }
    }

    public void deleteVoucher(int customerId, int voucherId) {
        String sql = "DELETE FROM dbo.CustomerVoucher WHERE CustomerID = ? AND VoucherID = ?";
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.setInt(2, voucherId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot delete customer voucher", ex);
        }
    }

    private CustomerVoucher mapCustomerVoucher(ResultSet resultSet) throws SQLException {
        Timestamp expirationTimestamp = resultSet.getTimestamp("ExpirationDate");
        BigDecimal maxDiscount = resultSet.getBigDecimal("MaxDiscountAmount");
        if (resultSet.wasNull()) {
            maxDiscount = null;
        }
        Timestamp endTimestamp = resultSet.getTimestamp("EndDate");
        return new CustomerVoucher(
                resultSet.getInt("CustomerID"),
                expirationTimestamp == null ? null : expirationTimestamp.toLocalDateTime(),
                resultSet.getInt("Quantity"),
                resultSet.getInt("VoucherID"),
                resultSet.getString("VoucherCode"),
                resultSet.getString("VoucherType"),
                resultSet.getBigDecimal("VoucherValue"),
                maxDiscount,
                resultSet.getBigDecimal("MinOrderValue"),
                endTimestamp == null ? null : endTimestamp.toLocalDateTime());
    }

    private Voucher mapVoucher(ResultSet resultSet) throws SQLException {
        BigDecimal maxDiscount = resultSet.getBigDecimal("MaxDiscountAmount");
        if (resultSet.wasNull()) {
            maxDiscount = null;
        }
        Timestamp endTimestamp = resultSet.getTimestamp("EndDate");
        return new Voucher(
                resultSet.getInt("VoucherID"),
                resultSet.getString("VoucherCode"),
                resultSet.getString("VoucherType"),
                resultSet.getBigDecimal("VoucherValue"),
                maxDiscount,
                resultSet.getBigDecimal("MinOrderValue"),
                endTimestamp == null ? null : endTimestamp.toLocalDateTime());
    }

    private void setTimestamp(PreparedStatement statement, int index,
            LocalDateTime value) throws SQLException {
        if (value == null) {
            statement.setTimestamp(index, null);
        } else {
            statement.setTimestamp(index, Timestamp.valueOf(value));
        }
    }
}
