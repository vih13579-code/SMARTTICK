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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        try {
            PreparedStatement pre = connector.prepareStatement("SELECT * FROM CustomerVoucher cv\n"
                    + "  LEFT JOIN Vouchers v ON cv.VoucherID = v.VoucherID\n"
                    + "  WHERE CustomerID = ?");
            pre.setInt(1, customerID);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                // int customerID, String expirationDate, int quantity, int voucherID, String voucherCode, 
                //int voucherValue, int voucherType, String startDate, String endDate, int usedCount, 
                // int maxUsedCount, int maxDiscountAmount, int minOrderValue, int status, String description
                list.add(new CustomerVoucher(rs.getInt(1), rs.getString(3), rs.getInt(4), rs.getInt(5), rs.getString(6), rs.getInt(7), rs.getInt(8), rs.getString(9), rs.getString(10),
                        rs.getInt(11), rs.getInt(12), rs.getInt(13), rs.getInt(14), rs.getInt(15), rs.getString(16)));
            }
        } catch (SQLException e) {
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
            String sql = "SELECT * "
                    + "FROM CustomerVoucher cv "
                    + "LEFT JOIN Vouchers v ON cv.VoucherID = v.VoucherID "
                    + "WHERE cv.CustomerID = ? AND cv.VoucherID = ?";
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, customerID);
            pre.setInt(2, voucherID);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                voucher = new CustomerVoucher(rs.getInt(1), rs.getString(3), rs.getInt(4), rs.getInt(5), rs.getString(6), rs.getInt(7), rs.getInt(8), rs.getString(9), rs.getString(10),
                        rs.getInt(11), rs.getInt(12), rs.getInt(13), rs.getInt(14), rs.getInt(15), rs.getString(16));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return voucher;
    }

    public void subtractQuantityOfVoucher(int customerID, int voucherID) {
        try {
            String sql = "Update CustomerVoucher SET Quantity = Quantity - 1 WHERE CustomerID = ? AND VoucherID = ?";
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
