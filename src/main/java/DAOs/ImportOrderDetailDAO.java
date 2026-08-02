/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.ImportOrderDetail;
import Models.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ImportOrderDetailDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    public int createImportOrderDetails(ArrayList<ImportOrderDetail> detailList) {
        String query = "INSERT INTO ImportStockDetails (ImportID, ProductID, ImportQuantity, ImportPrice) VALUES";
        ArrayList<String> values = new ArrayList<>();

        for (ImportOrderDetail d : detailList) {
            String value = "(";
            value += d.getIoid() + ",";
            value += d.getProduct().getProductId() + ",";
            value += d.getQuantity() + ",";
            value += d.getImportPrice() + ")";
        }

        for (String v : values) {
            query += " " + v + ",";
        }

        String finalQuery = query.substring(0, query.length() - 1);

        try {
            PreparedStatement ps = connector.prepareStatement(finalQuery);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return 1;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public int createImportOrderDetail(ImportOrderDetail detail) {
        String query = "INSERT INTO ImportStockDetails (ImportID, ProductID, ImportQuantity, ImportPrice) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = connector.prepareStatement(query);

            ps.setInt(1, detail.getIoid());
            ps.setInt(2, detail.getProduct().getProductId());
            ps.setInt(3, detail.getQuantity());
            ps.setLong(4, detail.getImportPrice());

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public ArrayList<ImportOrderDetail> getDetailsById(int detailId) {
        String query = "SELECT * FROM ImportStockDetails WHERE ImportID = ?";
        ArrayList<ImportOrderDetail> list = null;
        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setInt(1, detailId);

            ResultSet rs = ps.executeQuery();
            list = new ArrayList<>();
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("ProductID"));
                list.add(new ImportOrderDetail(rs.getInt("ImportID"), p, rs.getInt("ImportQuantity"), rs.getLong("ImportPrice")));
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return list;
    }

    public int updateDetailById(ImportOrderDetail d) {
        String query = "UPDATE ImportStockDetails SET ImportQuantity = ?, ImportPrice = ? WHERE ImportID = ? AND ProductID = ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setInt(1, d.getQuantity());
            ps.setLong(2, d.getImportPrice());
            ps.setInt(3, d.getIoid());
            ps.setInt(4, d.getProduct().getProductId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public int deleteDetailById(int productId, int importId) {
        String query = "DELETE FROM ImportStockDetails WHERE ProductID = ? AND ImportID = ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setInt(1, productId);
            ps.setInt(2, importId);

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public long calculateTotalPrice(int importId) {
        String query = "SELECT COALESCE(SUM(CAST(ImportQuantity AS BIGINT) * ImportPrice), 0) AS TotalPrice "
                + "FROM ImportStockDetails WHERE ImportID = ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setInt(1, importId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getLong("TotalPrice");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public ArrayList<ImportOrderDetail> getImportOrdersToday() {
        ArrayList<ImportOrderDetail> list = new ArrayList<>();

        String query = "SELECT *\n"
                + "FROM ImportStockDetails IOD\n"
                + "JOIN ImportStocks IO ON IOD.ImportID = IO.ImportID\n"
                + "JOIN Products P ON IOD.ProductID = P.ProductID\n"
                + "WHERE CAST(IO.ImportDate AS DATE) = CAST(GETDATE() AS DATE)\n"
                + "ORDER BY P.ProductID ASC";

        try {
            PreparedStatement ps = connector.prepareStatement(query);

            ResultSet rs = ps.executeQuery();
            list = new ArrayList<>();
            while (rs.next()) {
                Product p = new Product();
                p.setProductId(rs.getInt("ProductID"));
                p.setModel(rs.getString("Model"));
                list.add(new ImportOrderDetail(rs.getInt("ImportID"), p, rs.getInt("ImportQuantity"), rs.getLong("ImportPrice")));
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return list;
    }

}
