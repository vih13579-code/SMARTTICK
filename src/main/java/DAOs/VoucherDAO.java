package DAOs;

import DB.DBContext;
import Models.Voucher;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoucherDAO {

    private static final Logger LOGGER = Logger.getLogger(VoucherDAO.class.getName());
    private static final String SELECT_COLUMNS =
            "VoucherID, VoucherCode, VoucherType, VoucherValue, "
            + "MaxDiscountAmount, MinOrderValue, EndDate ";

    private final DBContext dbContext;

    public VoucherDAO() {
        this(new DBContext());
    }

    VoucherDAO(DBContext dbContext) {
        this.dbContext = dbContext;
    }

    public List<Voucher> getAllVoucher() {
        String sql = "SELECT " + SELECT_COLUMNS
                + "FROM dbo.Vouchers ORDER BY VoucherID DESC";
        return queryVouchers(sql, null);
    }

    public List<Voucher> searchVouchers(String searchQuery) {
        String sql = "SELECT " + SELECT_COLUMNS
                + "FROM dbo.Vouchers WHERE UPPER(VoucherCode) LIKE ? "
                + "ORDER BY VoucherID DESC";
        return queryVouchers(sql, "%" + normalizeCode(searchQuery) + "%");
    }

    public List<Voucher> getAllVoucherActivate() {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS
                + "FROM dbo.Vouchers WHERE EndDate >= ? ORDER BY EndDate ASC, VoucherID DESC";
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vouchers.add(mapVoucher(resultSet));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load active vouchers", ex);
        }
        return vouchers;
    }

    public Voucher getVoucher(int voucherId) {
        String sql = "SELECT " + SELECT_COLUMNS
                + "FROM dbo.Vouchers WHERE VoucherID = ?";
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, voucherId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapVoucher(resultSet) : null;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load voucher ID " + voucherId, ex);
            return null;
        }
    }

    public Voucher getVoucherByCode(String voucherCode) {
        String sql = "SELECT " + SELECT_COLUMNS
                + "FROM dbo.Vouchers WHERE UPPER(VoucherCode) = UPPER(?)";
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizeCode(voucherCode));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapVoucher(resultSet) : null;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load voucher by code", ex);
            return null;
        }
    }

    public boolean existsByCode(String voucherCode) throws SQLException {
        return existsByCode(voucherCode, null);
    }

    public boolean existsByCodeExcludingId(String voucherCode, int voucherId)
            throws SQLException {
        return existsByCode(voucherCode, voucherId);
    }

    public WriteResult insertVoucher(Voucher voucher) throws SQLException {
        String sql = "INSERT INTO dbo.Vouchers "
                + "(VoucherCode, VoucherType, VoucherValue, MaxDiscountAmount, "
                + "MinOrderValue, EndDate) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setWriteParameters(statement, voucher);
            if (statement.executeUpdate() == 0) {
                return WriteResult.FAILED;
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    voucher.setVoucherId(keys.getInt(1));
                }
            }
            return WriteResult.SUCCESS;
        } catch (SQLException ex) {
            if (isDuplicateKey(ex)) {
                return WriteResult.DUPLICATE_CODE;
            }
            throw ex;
        }
    }

    public WriteResult updateVoucher(Voucher voucher) throws SQLException {
        String sql = "UPDATE dbo.Vouchers SET VoucherCode = ?, VoucherType = ?, "
                + "VoucherValue = ?, MaxDiscountAmount = ?, MinOrderValue = ?, EndDate = ? "
                + "WHERE VoucherID = ?";
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            setWriteParameters(statement, voucher);
            statement.setInt(7, voucher.getVoucherId());
            return statement.executeUpdate() > 0 ? WriteResult.SUCCESS : WriteResult.NOT_FOUND;
        } catch (SQLException ex) {
            if (isDuplicateKey(ex)) {
                return WriteResult.DUPLICATE_CODE;
            }
            throw ex;
        }
    }

    public int deleteVoucher(int voucherId) {
        String deleteCustomerVoucher =
                "DELETE FROM dbo.CustomerVoucher WHERE VoucherID = ?";
        String deleteVoucher = "DELETE FROM dbo.Vouchers WHERE VoucherID = ?";
        try (Connection connection = dbContext.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement =
                        connection.prepareStatement(deleteCustomerVoucher)) {
                    statement.setInt(1, voucherId);
                    statement.executeUpdate();
                }
                int affected;
                try (PreparedStatement statement = connection.prepareStatement(deleteVoucher)) {
                    statement.setInt(1, voucherId);
                    affected = statement.executeUpdate();
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
            LOGGER.log(Level.SEVERE, "Cannot delete voucher ID " + voucherId, ex);
            return 0;
        }
    }

    private List<Voucher> queryVouchers(String sql, String parameter) {
        List<Voucher> vouchers = new ArrayList<>();
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parameter != null) {
                statement.setString(1, parameter);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vouchers.add(mapVoucher(resultSet));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot query vouchers", ex);
        }
        return vouchers;
    }

    private boolean existsByCode(String voucherCode, Integer excludedId)
            throws SQLException {
        String sql = "SELECT 1 FROM dbo.Vouchers "
                + "WHERE UPPER(VoucherCode) = UPPER(?)"
                + (excludedId == null ? "" : " AND VoucherID <> ?");
        try (Connection connection = dbContext.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizeCode(voucherCode));
            if (excludedId != null) {
                statement.setInt(2, excludedId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void setWriteParameters(PreparedStatement statement, Voucher voucher)
            throws SQLException {
        statement.setString(1, normalizeCode(voucher.getVoucherCode()));
        statement.setString(2, voucher.getType());
        statement.setBigDecimal(3, voucher.getValue());
        if (voucher.getMaxDiscount() == null) {
            statement.setNull(4, Types.DECIMAL);
        } else {
            statement.setBigDecimal(4, voucher.getMaxDiscount());
        }
        statement.setBigDecimal(5, voucher.getMinOrderValue());
        statement.setTimestamp(6, Timestamp.valueOf(voucher.getEndDate()));
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

    private boolean isDuplicateKey(SQLException exception) {
        SQLException current = exception;
        while (current != null) {
            if (current.getErrorCode() == 2601 || current.getErrorCode() == 2627) {
                return true;
            }
            String message = current.getMessage();
            if ("23000".equals(current.getSQLState()) && message != null
                    && (message.contains("UQ_Vouchers_Code")
                    || message.contains("UX_Vouchers_Code_CI"))) {
                return true;
            }
            current = current.getNextException();
        }
        return false;
    }

    private String normalizeCode(String voucherCode) {
        return voucherCode == null ? "" : voucherCode.trim().toUpperCase(Locale.ROOT);
    }

    public enum WriteResult {
        SUCCESS,
        DUPLICATE_CODE,
        NOT_FOUND,
        FAILED
    }
}
