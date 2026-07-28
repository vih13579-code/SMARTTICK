/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import java.sql.Connection;
import Models.Voucher;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    public List<Voucher> getAllVoucher() {
        List<Voucher> V = new ArrayList<>();
        try {

            PreparedStatement pr = connector.prepareStatement("SELECT * FROM Vouchers");
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                Voucher voucher = new Voucher(
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
                        rs.getString("Description")
                );
                V.add(voucher);
            };
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
        return V;
    }

    public List<Voucher> searchVouchers(String keyword) {
        List<Voucher> vouchers = new ArrayList<>();
        String sql = "SELECT * FROM Vouchers WHERE VoucherCode LIKE ? OR Description LIKE ? ORDER BY VoucherID DESC";
        try ( PreparedStatement pr = connector.prepareStatement(sql)) {
            String searchValue = "%" + keyword + "%";
            pr.setString(1, searchValue);
            pr.setString(2, searchValue);
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                Voucher voucher = new Voucher(
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
                        rs.getString("Description")
                );
                vouchers.add(voucher);
            }
        } catch (SQLException e) {
            System.out.println("Error searching vouchers: " + e.getMessage());
        }
        return vouchers;
    }

        public List<Voucher> getAllVoucherActivate() {
        List<Voucher> V = new ArrayList<>();
        try {

            PreparedStatement pr = connector.prepareStatement(
                    "SELECT * FROM Vouchers "
                    + "WHERE Status = 1 "
                    + "AND EndDate >= GETDATE() "
                    + "AND (MaxUsedCount = 0 OR UsedCount < MaxUsedCount) "
                    + "ORDER BY EndDate ASC, VoucherID DESC");
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                Voucher voucher = new Voucher(
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
                        rs.getString("Description")
                );
                V.add(voucher);
            };
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
        return V;
    }

    public Voucher getVoucher(int VoucherID) {
        Voucher voucher = new Voucher();
        try {
            PreparedStatement pr = connector.prepareStatement("SELECT * FROM Vouchers WHERE VoucherID =?");
            pr.setInt(1, VoucherID);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                voucher.setVoucherID(rs.getInt("VoucherID"));
                voucher.setVoucherCode(rs.getString("VoucherCode"));
                voucher.setVoucherType(rs.getInt("VoucherType"));
                voucher.setVoucherValue(rs.getInt("VoucherValue"));
                voucher.setStartDate(rs.getString("StartDate"));
                voucher.setEndDate(rs.getString("EndDate"));
                voucher.setUsedCount(rs.getInt("UsedCount"));
                voucher.setMaxUsedCount(rs.getInt("MaxUsedCount")); // correct
                voucher.setMaxDiscountAmount(rs.getInt("MaxDiscountAmount"));
                voucher.setMinOrderValue(rs.getInt("MinOrderValue"));
                voucher.setStatus(rs.getInt("Status"));
                voucher.setDescription(rs.getString("Description"));
            }
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
        return voucher;
    }

    public int updateVoucher(Voucher updated) {
        String sql = "UPDATE Vouchers SET "
                + "VoucherCode = ?, "
                + "VoucherValue = ?, "
                + "VoucherType = ?, "
                + "StartDate = ?, "
                + "EndDate = ?, "
                + "UsedCount = ?, "
                + "MaxUsedCount = ?, "
                + "MaxDiscountAmount = ?, "
                + "MinOrderValue = ?, "
                + "Status = ?, "
                + "Description = ? "
                + "WHERE VoucherID = ?";

        boolean previousAutoCommit;
        try {
            previousAutoCommit = connector.getAutoCommit();
            connector.setAutoCommit(false);
            int count;
            try (PreparedStatement pr = connector.prepareStatement(sql)) {
            pr.setString(1, updated.getVoucherCode());
            pr.setInt(2, updated.getVoucherValue());
            pr.setInt(3, updated.getVoucherType());
            pr.setString(4, updated.getStartDate());
            pr.setString(5, updated.getEndDate());
            pr.setInt(6, updated.getUsedCount());
            pr.setInt(7, updated.getMaxUsedCount());
            pr.setInt(8, updated.getMaxDiscountAmount());
            pr.setInt(9, updated.getMinOrderValue());
            pr.setInt(10, updated.getStatus());
            pr.setString(11, updated.getDescription());
            pr.setInt(12, updated.getVoucherID());

            count = pr.executeUpdate();
            }

            if (count > 0 && updated.getStatus() == 1) {
                assignVoucherToAllEligibleCustomers(connector, updated.getVoucherID());
            }
            connector.commit();
            connector.setAutoCommit(previousAutoCommit);
            return count;
        } catch (SQLException e) {
            rollbackQuietly();
            System.out.println("Error updating voucher: " + e.getMessage());
            return 0;
        }
    }

    public int deleteVoucher(int voucherID) {
        boolean previousAutoCommit = true;
        try {
            previousAutoCommit = connector.getAutoCommit();
            connector.setAutoCommit(false);

            try (PreparedStatement customerVoucherStatement = connector.prepareStatement(
                    "DELETE FROM CustomerVoucher WHERE VoucherID = ?")) {
                customerVoucherStatement.setInt(1, voucherID);
                customerVoucherStatement.executeUpdate();
            }

            int count;
            try (PreparedStatement voucherStatement = connector.prepareStatement(
                    "DELETE FROM Vouchers WHERE VoucherID = ?")) {
                voucherStatement.setInt(1, voucherID);
                count = voucherStatement.executeUpdate();
            }

            if (count == 0) {
                connector.rollback();
                return 0;
            }
            connector.commit();
            return count;
        } catch (SQLException e) {
            try {
                connector.rollback();
            } catch (SQLException rollbackError) {
                System.out.println("Voucher delete rollback error: " + rollbackError.getMessage());
            }
            System.out.println("Delete error: " + e.getMessage());
            return 0;
        } finally {
            try {
                connector.setAutoCommit(previousAutoCommit);
            } catch (SQLException e) {
                System.out.println("Cannot restore voucher connection: " + e.getMessage());
            }
        }
    }


    public boolean checkVoucherCodeExists(String code) {
        String sql = "SELECT 1 FROM Vouchers WHERE VoucherCode = ?";
        try ( PreparedStatement pr = connector.prepareStatement(sql)) {
            pr.setString(1, code);
            ResultSet rs = pr.executeQuery();
            return rs.next(); // Nếu có dòng nào → code đã tồn tại
        } catch (SQLException e) {
            System.out.println("Error checking code: " + e.getMessage());
        }
        return false;
    }

    public int insertVoucher(Voucher v) {
        String sql = "INSERT INTO Vouchers (VoucherCode, VoucherValue, VoucherType, StartDate, EndDate, "
                + "UsedCount, MaxUsedCount, MaxDiscountAmount, MinOrderValue, Status, Description) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        boolean previousAutoCommit;
        try {
            previousAutoCommit = connector.getAutoCommit();
            connector.setAutoCommit(false);
            int count;
            int voucherId;
            try (PreparedStatement pr = connector.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pr.setString(1, v.getVoucherCode());
                pr.setInt(2, v.getVoucherValue());
                pr.setInt(3, v.getVoucherType());
                pr.setString(4, v.getStartDate());
                pr.setString(5, v.getEndDate());
                pr.setInt(6, 0);
                pr.setInt(7, v.getMaxUsedCount());
                pr.setInt(8, v.getMaxDiscountAmount());
                pr.setInt(9, v.getMinOrderValue());
                pr.setInt(10, v.getStatus());
                pr.setString(11, v.getDescription());

                count = pr.executeUpdate();
                try (ResultSet keys = pr.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("The created voucher ID could not be read.");
                    }
                    voucherId = keys.getInt(1);
                }
            }

            if (v.getStatus() == 1) {
                assignVoucherToAllEligibleCustomers(connector, voucherId);
            }
            connector.commit();
            connector.setAutoCommit(previousAutoCommit);
            return count;
        } catch (SQLException e) {
            rollbackQuietly();
            System.out.println("Error inserting voucher: " + e.getMessage());
            return 0;
        }
    }

    private int assignVoucherToAllEligibleCustomers(Connection connection, int voucherId)
            throws SQLException {
        String sql = "INSERT INTO CustomerVoucher (CustomerID, VoucherID, ExpirationDate, Quantity) "
                + "SELECT c.CustomerID, v.VoucherID, NULL, 1 "
                + "FROM Customers c CROSS JOIN Vouchers v "
                + "WHERE v.VoucherID = ? "
                + "AND v.Status = 1 "
                + "AND v.EndDate >= GETDATE() "
                + "AND c.IsBlock = 0 "
                + "AND c.IsDeleted = 0 "
                + "AND NOT EXISTS ("
                + "SELECT 1 FROM CustomerVoucher cv "
                + "WHERE cv.CustomerID = c.CustomerID AND cv.VoucherID = v.VoucherID)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, voucherId);
            return ps.executeUpdate();
        }
    }

    private void rollbackQuietly() {
        try {
            connector.rollback();
            connector.setAutoCommit(true);
        } catch (SQLException rollbackError) {
            System.out.println("Voucher transaction rollback error: " + rollbackError.getMessage());
        }
    }
}
