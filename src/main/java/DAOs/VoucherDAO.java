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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
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

    public List<Voucher> searchVoucherByCode(String code) {
        List<Voucher> V = new ArrayList<>();
        String sql = "SELECT * FROM Vouchers WHERE VoucherCode LIKE ?";
        try ( PreparedStatement pr = connector.prepareStatement(sql)) {
            pr.setString(1, "%" + code + "%");
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
            }
        } catch (SQLException e) {
            System.out.println("Search voucher error: " + e.getMessage());
        }
        return V;
    }

    public List<Voucher> getAllVoucherActivate() {
        List<Voucher> V = new ArrayList<>();
        try {

            PreparedStatement pr = connector.prepareStatement("SELECT * FROM Vouchers WHERE Status =1");
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
        int count = 0;
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

        try ( PreparedStatement pr = connector.prepareStatement(sql)) {
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
        } catch (SQLException e) {
            System.out.println("Error updating voucher: " + e.getMessage());
        }
        return count;
    }

    public int deleteVoucher(int voucherID) {
        int count = 0;
        String sql = "DELETE FROM Vouchers WHERE VoucherID = ?;";
        try ( PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, voucherID);
            count = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete error: " + e.getMessage());
        }
        return count;
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
        int count = 0;
        try ( PreparedStatement pr = connector.prepareStatement(sql)) {
            pr.setString(1, v.getVoucherCode());
            pr.setInt(2, v.getVoucherValue());
            pr.setInt(3, v.getVoucherType());
            pr.setString(4, v.getStartDate());
            pr.setString(5, v.getEndDate());
            pr.setInt(6, 0); // used count luôn là 0 khi tạo
            pr.setInt(7, v.getMaxUsedCount());
            pr.setInt(8, v.getMaxDiscountAmount());
            pr.setInt(9, v.getMinOrderValue());
            pr.setInt(10, v.getStatus());
            pr.setString(11, v.getDescription());

            count = pr.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting voucher: " + e.getMessage());
        }
        return count;
    }
}
