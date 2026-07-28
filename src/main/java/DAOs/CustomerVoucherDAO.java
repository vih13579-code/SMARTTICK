/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.CustomerVoucher;
import Models.Voucher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerVoucherDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    private static final String AVAILABLE_VOUCHER_SELECT
            = "SELECT cv.CustomerID, cv.ExpirationDate, cv.Quantity, "
            + "v.VoucherID, v.VoucherCode, v.VoucherValue, v.VoucherType, "
            + "v.StartDate, v.EndDate, v.UsedCount, v.MaxUsedCount, "
            + "v.MaxDiscountAmount, v.MinOrderValue, v.Status, v.Description "
            + "FROM CustomerVoucher cv "
            + "JOIN Vouchers v ON cv.VoucherID = v.VoucherID "
            + "WHERE cv.CustomerID = ? "
            + "AND cv.Quantity > 0 "
            + "AND v.Status = 1 "
            + "AND v.StartDate <= GETDATE() "
            + "AND v.EndDate >= GETDATE() "
            + "AND (cv.ExpirationDate IS NULL OR cv.ExpirationDate >= GETDATE()) "
            + "AND (v.MaxUsedCount = 0 OR v.UsedCount < v.MaxUsedCount) ";

    public List<CustomerVoucher> getVoucherOfCustomer(int customerID) {
        List<CustomerVoucher> list = new ArrayList<>();
        syncAvailableVouchersForCustomer(customerID);
        String sql = AVAILABLE_VOUCHER_SELECT + "ORDER BY v.EndDate ASC, v.VoucherID DESC";
        try (PreparedStatement pre = connector.prepareStatement(sql)) {
            pre.setInt(1, customerID);
            try (ResultSet rs = pre.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCustomerVoucher(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Load customer vouchers error: " + e.getMessage());
        }
        return list;
    }

    public List<CustomerVoucher> getSavedVouchersOfCustomer(int customerID) {
        List<CustomerVoucher> list = new ArrayList<>();
        String sql = AVAILABLE_VOUCHER_SELECT + "ORDER BY v.EndDate ASC, v.VoucherID DESC";
        try (PreparedStatement pre = connector.prepareStatement(sql)) {
            pre.setInt(1, customerID);
            try (ResultSet rs = pre.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCustomerVoucher(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Load saved customer vouchers error: " + e.getMessage());
        }
        return list;
    }

    public List<Voucher> getClaimableVouchersForCustomer(int customerID) {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT v.* "
                + "FROM Vouchers v "
                + "WHERE v.Status = 1 "
                + "AND v.StartDate <= GETDATE() "
                + "AND v.EndDate >= GETDATE() "
                + "AND (v.MaxUsedCount = 0 OR v.UsedCount < v.MaxUsedCount) "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM CustomerVoucher cv "
                + "WHERE cv.CustomerID = ? AND cv.VoucherID = v.VoucherID AND cv.Quantity > 0) "
                + "ORDER BY v.EndDate ASC, v.VoucherID DESC";
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVoucher(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Load claimable vouchers error: " + e.getMessage());
        }
        return list;
    }

    public int saveVoucherForCustomer(int customerID, int voucherID) {
        String sql = "INSERT INTO CustomerVoucher (CustomerID, VoucherID, ExpirationDate, Quantity) "
                + "SELECT ?, v.VoucherID, NULL, 1 "
                + "FROM Vouchers v "
                + "JOIN Customers c ON c.CustomerID = ? "
                + "WHERE v.VoucherID = ? "
                + "AND c.IsBlock = 0 "
                + "AND c.IsDeleted = 0 "
                + "AND v.Status = 1 "
                + "AND v.StartDate <= GETDATE() "
                + "AND v.EndDate >= GETDATE() "
                + "AND (v.MaxUsedCount = 0 OR v.UsedCount < v.MaxUsedCount) "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM CustomerVoucher cv "
                + "WHERE cv.CustomerID = ? AND cv.VoucherID = v.VoucherID)";
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ps.setInt(2, customerID);
            ps.setInt(3, voucherID);
            ps.setInt(4, customerID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Save customer voucher error: " + e.getMessage());
            return 0;
        }
    }

    public int syncAvailableVouchersForCustomer(int customerID) {
        String sql = "INSERT INTO CustomerVoucher (CustomerID, VoucherID, ExpirationDate, Quantity) "
                + "SELECT c.CustomerID, v.VoucherID, NULL, 1 "
                + "FROM Customers c CROSS JOIN Vouchers v "
                + "WHERE c.CustomerID = ? "
                + "AND c.IsBlock = 0 "
                + "AND c.IsDeleted = 0 "
                + "AND v.Status = 1 "
                + "AND v.EndDate >= GETDATE() "
                + "AND (v.MaxUsedCount = 0 OR v.UsedCount < v.MaxUsedCount) "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM CustomerVoucher cv "
                + "WHERE cv.CustomerID = ? AND cv.VoucherID = v.VoucherID)";
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ps.setInt(2, customerID);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Sync customer vouchers error: " + e.getMessage());
            return 0;
        }
    }

    public int assignVoucherToCustomer(int customerID, int voucherID, int quantity, String expirationDate) {
        boolean previousAutoCommit;
        try {
            previousAutoCommit = connector.getAutoCommit();
            connector.setAutoCommit(false);
            String updateSql = "UPDATE CustomerVoucher "
                    + "SET Quantity = Quantity + ?, ExpirationDate = ? "
                    + "WHERE CustomerID = ? AND VoucherID = ?";
            int count;
            try (PreparedStatement ps = connector.prepareStatement(updateSql)) {
                ps.setInt(1, quantity);
                setNullableDateTime(ps, 2, expirationDate);
                ps.setInt(3, customerID);
                ps.setInt(4, voucherID);
                count = ps.executeUpdate();
            }

            if (count == 0) {
                String insertSql = "INSERT INTO CustomerVoucher "
                        + "(CustomerID, VoucherID, Quantity, ExpirationDate) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = connector.prepareStatement(insertSql)) {
                    ps.setInt(1, customerID);
                    ps.setInt(2, voucherID);
                    ps.setInt(3, quantity);
                    setNullableDateTime(ps, 4, expirationDate);
                    count = ps.executeUpdate();
                }
            }
            connector.commit();
            connector.setAutoCommit(previousAutoCommit);
            return count;
        } catch (SQLException e) {
            try {
                connector.rollback();
                connector.setAutoCommit(true);
            } catch (SQLException rollbackError) {
                System.out.println("Assign voucher rollback error: " + rollbackError.getMessage());
            }
            System.out.println("Assign voucher error: " + e.getMessage());
            return 0;
        }
    }

    public boolean isVoucherAlreadyAssigned(int customerID, int voucherID) {
        String sql = "SELECT 1 FROM CustomerVoucher "
                + "WHERE CustomerID = ? AND VoucherID = ? AND Quantity > 0";
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ps.setInt(2, voucherID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Check assigned error: " + e.getMessage());
        }
        return false;
    }

    public int getVoucherQuantity(int customerID, int voucherID) {
        String sql = "SELECT Quantity FROM CustomerVoucher WHERE CustomerID = ? AND VoucherID = ?";
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ps.setInt(2, voucherID);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("Quantity") : 0;
            }
        } catch (SQLException e) {
            System.out.println("Load voucher quantity error: " + e.getMessage());
            return 0;
        }
    }

    public CustomerVoucher getVoucherById(int customerID, int voucherID) {
        syncAvailableVouchersForCustomer(customerID);
        String sql = AVAILABLE_VOUCHER_SELECT + "AND cv.VoucherID = ?";
        try (PreparedStatement pre = connector.prepareStatement(sql)) {
            pre.setInt(1, customerID);
            pre.setInt(2, voucherID);
            try (ResultSet rs = pre.executeQuery()) {
                return rs.next() ? mapCustomerVoucher(rs) : null;
            }
        } catch (SQLException e) {
            System.out.println("Load voucher error: " + e.getMessage());
        }
        return null;
    }

    public void subtractQuantityOfVoucher(int customerID, int voucherID) {
        try {
            String sql = "UPDATE CustomerVoucher SET Quantity = Quantity - 1 "
                    + "WHERE CustomerID = ? AND VoucherID = ? AND Quantity > 0";
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, customerID);
            pre.setInt(2, voucherID);
            pre.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVoucher(int customerID, int voucherID) {
        try {
            String sql = "Delete from CustomerVoucher Where CustomerID = ? AND VoucherID = ?";
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, customerID);
            pre.setInt(2, voucherID);
            pre.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void deleteVoucherByVoucherID(int voucherID) {
        try {
            String sql = "Delete from CustomerVoucher Where  VoucherID = ?";
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, voucherID);
            pre.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public void increaseVoucher(int voucherID) {
        try {
            String sql = "Update Vouchers SET UsedCount = UsedCount + 1 WHERE VoucherID = ?";
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, voucherID);
            pre.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int deleteCustomerVoucher(int voucherID) {
        int count = 0;
        String sql = "DELETE FROM CustomerVoucher WHERE VoucherID = ?;";
        try ( PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, voucherID);
            count = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
        return count;
    }

    private CustomerVoucher mapCustomerVoucher(ResultSet rs) throws SQLException {
        return new CustomerVoucher(
                rs.getInt("CustomerID"),
                rs.getString("ExpirationDate"),
                rs.getInt("Quantity"),
                rs.getInt("VoucherID"),
                rs.getString("VoucherCode"),
                rs.getInt("VoucherValue"),
                rs.getInt("VoucherType"),
                rs.getString("StartDate"),
                rs.getString("EndDate"),
                rs.getInt("UsedCount"),
                rs.getInt("MaxUsedCount"),
                rs.getInt("MaxDiscountAmount"),
                rs.getInt("MinOrderValue"),
                rs.getInt("Status"),
                rs.getString("Description"));
    }

    private Voucher mapVoucher(ResultSet rs) throws SQLException {
        return new Voucher(
                rs.getInt("VoucherID"),
                rs.getString("VoucherCode"),
                rs.getInt("VoucherValue"),
                rs.getInt("VoucherType"),
                rs.getString("StartDate"),
                rs.getString("EndDate"),
                rs.getInt("UsedCount"),
                rs.getInt("MaxUsedCount"),
                rs.getInt("MaxDiscountAmount"),
                rs.getInt("MinOrderValue"),
                rs.getInt("Status"),
                rs.getString("Description"));
    }

    private void setNullableDateTime(PreparedStatement ps, int parameterIndex, String value)
            throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(parameterIndex, java.sql.Types.TIMESTAMP);
        } else {
            ps.setString(parameterIndex, value);
        }
    }

    public static void main(String[] args) {
        CustomerVoucherDAO cv = new CustomerVoucherDAO();
        List<CustomerVoucher> list = cv.getVoucherOfCustomer(13);
        for (CustomerVoucher customerVoucher : list) {
            System.out.println(customerVoucher.getVoucherCode() + " " + customerVoucher.getExpirationDate());
        }
       // cv.assignVoucherToCustomer(13, 1, 1, null);

    }

//    public static void main(String[] args) {
//        CustomerVoucherDAO c = new CustomerVoucherDAO();
//        List<CustomerVoucher> list = c.getVoucherOfCustomer(1);
//        for (CustomerVoucher customerVoucher : list) {
//            System.out.println(customerVoucher.getVoucherCode() + " " + customerVoucher.getMaxDiscountAmount());
//        }
//    }
}
