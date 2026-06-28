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
        return searchVouchers(null, null, null, "id_desc", 1, Integer.MAX_VALUE);
    }

    public List<Voucher> searchVoucherByCode(String code) {
        return searchVouchers(code, null, null, "id_desc", 1, Integer.MAX_VALUE);
    }

    public List<Voucher> searchVouchers(String keyword, Integer status, Integer type, String sort, int page, int pageSize) {
        List<Voucher> V = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Vouchers WHERE 1 = 1");
        appendVoucherFilters(sql, params, keyword, status, type);
        sql.append(voucherSortClause(sort));
        if (pageSize > 0 && pageSize < Integer.MAX_VALUE) {
            sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add(Math.max(0, (page - 1) * pageSize));
            params.add(pageSize);
        }

        try ( PreparedStatement pr = connector.prepareStatement(sql.toString())) {
            setParameters(pr, params);
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                V.add(mapVoucher(rs));
            }
        } catch (SQLException e) {
            System.out.println("Search voucher error: " + e.getMessage());
        }
        return V;
    }

    public int countVouchers(String keyword, Integer status, Integer type) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Vouchers WHERE 1 = 1");
        appendVoucherFilters(sql, params, keyword, status, type);
        try ( PreparedStatement pr = connector.prepareStatement(sql.toString())) {
            setParameters(pr, params);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Count voucher error: " + e.getMessage());
        }
        return 0;
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

    private void appendVoucherFilters(StringBuilder sql, List<Object> params, String keyword, Integer status, Integer type) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String search = "%" + keyword.trim() + "%";
            sql.append(" AND (VoucherCode LIKE ? OR Description LIKE ?")
                    .append(" OR CONVERT(VARCHAR(10), StartDate, 120) LIKE ?")
                    .append(" OR CONVERT(VARCHAR(10), EndDate, 120) LIKE ?)");
            params.add(search);
            params.add(search);
            params.add(search);
            params.add(search);
        }
        if (status != null && (status == 0 || status == 1)) {
            sql.append(" AND Status = ?");
            params.add(status);
        }
        if (type != null && (type == 0 || type == 1)) {
            sql.append(" AND VoucherType = ?");
            params.add(type);
        }
    }

    private String voucherSortClause(String sort) {
        if ("code_asc".equals(sort)) {
            return " ORDER BY VoucherCode ASC";
        }
        if ("code_desc".equals(sort)) {
            return " ORDER BY VoucherCode DESC";
        }
        if ("end_asc".equals(sort)) {
            return " ORDER BY EndDate ASC";
        }
        if ("end_desc".equals(sort)) {
            return " ORDER BY EndDate DESC";
        }
        if ("value_desc".equals(sort)) {
            return " ORDER BY VoucherValue DESC";
        }
        if ("id_asc".equals(sort)) {
            return " ORDER BY VoucherID ASC";
        }
        return " ORDER BY VoucherID DESC";
    }

    private void setParameters(PreparedStatement pr, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            pr.setObject(i + 1, params.get(i));
        }
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
                rs.getString("Description")
        );
    }
}
