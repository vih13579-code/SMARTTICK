/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.CustomerVoucher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class CustomerVoucherDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    public List<CustomerVoucher> getVoucherOfCustomer(int customerID) {
        List<CustomerVoucher> list = new ArrayList<>();
        String sql = customerVoucherSelect()
                + " WHERE cv.CustomerID = ?"
                + " ORDER BY v.EndDate ASC, v.VoucherID DESC";
        try {
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, customerID);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                list.add(mapCustomerVoucher(rs));
            }
        } catch (SQLException e) {
            System.out.println("Get customer vouchers error: " + e.getMessage());
        }
        return list;
    }

    public int assignVoucherToCustomer(int customerID, int voucherID, int quantity, String expirationDate) {
        int count = 0;
        String sql = "INSERT INTO CustomerVoucher (CustomerID, VoucherID, Quantity, ExpirationDate) VALUES (?, ?, ?, ?)";
        try ( PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ps.setInt(2, voucherID);
            ps.setInt(3, quantity);
            if (expirationDate != null) {
                ps.setString(4, expirationDate);
            } else {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            }
            count = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Assign voucher error: " + e.getMessage());
        }
        return count;
    }

    public boolean isVoucherAlreadyAssigned(int customerID, int voucherID) {
        String sql = "SELECT * FROM CustomerVoucher WHERE CustomerID = ? AND VoucherID = ?";
        try ( PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, customerID);
            ps.setInt(2, voucherID);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Check assigned error: " + e.getMessage());
        }
        return false;
    }

    public CustomerVoucher getVoucherById(int customerID, int voucherID) {
        CustomerVoucher voucher = null;
        try {
            String sql = customerVoucherSelect()
                    + "WHERE cv.CustomerID = ? AND cv.VoucherID = ?";
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, customerID);
            pre.setInt(2, voucherID);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                voucher = mapCustomerVoucher(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voucher;
    }

    public void subtractQuantityOfVoucher(int customerID, int voucherID) {
        try {
            String sql = "UPDATE CustomerVoucher SET Quantity = Quantity - 1 WHERE CustomerID = ? AND VoucherID = ? AND Quantity > 0";
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
        public void deleteVoucherByVoucherID( int voucherID) {
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

    private String customerVoucherSelect() {
        return "SELECT cv.CustomerID, cv.ExpirationDate, cv.Quantity, "
                + "v.VoucherID, v.VoucherCode, v.VoucherValue, v.VoucherType, "
                + "v.StartDate, v.EndDate, v.UsedCount, v.MaxUsedCount, "
                + "v.MaxDiscountAmount, v.MinOrderValue, v.Status, v.Description "
                + "FROM CustomerVoucher cv "
                + "JOIN Vouchers v ON cv.VoucherID = v.VoucherID ";
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
                rs.getString("Description")
        );
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
