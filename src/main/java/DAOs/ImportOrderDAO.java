/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.ImportOrder;
import Models.ImportOrderDetail;
import Models.Product;
import Models.Supplier;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ImportOrderDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    public ArrayList<ImportOrder> getAllImportOrders() {
        ArrayList<ImportOrder> list = new ArrayList<>();

        String query = "SELECT * FROM ImportStocks I JOIN Suppliers S ON I.SupplierID = S.SupplierID";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Supplier s = new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("TaxID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getTimestamp("CreatedDate").toLocalDateTime(),
                        rs.getTimestamp("LastModify").toLocalDateTime(),
                        rs.getInt("IsDeleted"),
                        rs.getInt("IsActivate")
                );

                ImportOrder io = new ImportOrder(
                        rs.getInt("ImportID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("SupplierID"),
                        rs.getDate("ImportDate"),
                        rs.getLong("TotalCost"),
                        rs.getInt("Completed")
                );

                io.setSupplier(s);

                list.add(io);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return list;
    }

    public ImportOrder getImportOrderByID(int id) {
        ImportOrder io = null;

        String query = "SELECT * FROM ImportStocks I JOIN Suppliers S ON I.SupplierID = S.SupplierID WHERE ImportID = ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Supplier s = new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("TaxID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getTimestamp("CreatedDate").toLocalDateTime(),
                        rs.getTimestamp("LastModify").toLocalDateTime(),
                        rs.getInt("IsDeleted"),
                        rs.getInt("IsActivate")
                );

                io = new ImportOrder(
                        rs.getInt("ImportID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("SupplierID"),
                        rs.getDate("ImportDate"),
                        rs.getLong("TotalCost"),
                        rs.getInt("Completed")
                );

                io.setSupplier(s);
            }

            return io;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return io;
    }
    
    public ArrayList<ImportOrder> getImportOrderBySupplierName(String name) {
        ArrayList<ImportOrder> list = new ArrayList<>();

        String query = "SELECT * FROM ImportStocks I JOIN Suppliers S ON I.SupplierID = S.SupplierID WHERE S.Name LIKE ";
        String search = "'%" + name + "%'";
        query += search;
        
        System.out.println(query);

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Supplier s = new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("TaxID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getTimestamp("CreatedDate").toLocalDateTime(),
                        rs.getTimestamp("LastModify").toLocalDateTime(),
                        rs.getInt("IsDeleted"),
                        rs.getInt("IsActivate")
                );

                ImportOrder io = new ImportOrder(
                        rs.getInt("ImportID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("SupplierID"),
                        rs.getDate("ImportDate"),
                        rs.getLong("TotalCost"),
                        rs.getInt("Completed")
                );

                io.setSupplier(s);

                list.add(io);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return list;
    }

    public ImportOrder getImportOrderDetailsByID(int id) {
        ImportOrder io = getImportOrderByID(id);

        String query = "SELECT P.Model, P.FullName, D.ImportID, D.ImportQuantity, D.ImportPrice, P.ProductID FROM ImportStockDetails D JOIN Products P ON D.ProductID = P.ProductID WHERE D.ImportID = ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            ArrayList<ImportOrderDetail> l = new ArrayList<>();
            Product p;
            while (rs.next()) {
                p = new Product();
                p.setProductId(rs.getInt("ProductID"));
                p.setModel(rs.getString("Model"));
                p.setFullName(rs.getString("FullName"));
                l.add(new ImportOrderDetail(
                        rs.getInt("ImportID"),
                        p,
                        rs.getInt("ImportQuantity"),
                        rs.getLong("ImportPrice")
                ));
            }

            io.setImportOrderDetails(l);
            return io;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return io;
    }

    public int createImportOrder(ImportOrder io) {
        String query = "INSERT INTO ImportStocks (EmployeeID, SupplierID, ImportDate, TotalCost, Completed) VALUES (?, ?, GETDATE(), ?, 1)";
        try {
            PreparedStatement ps = connector.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, io.getEmployeeId());
            ps.setInt(2, io.getSupplierId());
            ps.setLong(3, io.getTotalCost());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int generatedId = rs.getInt(1); // Lấy ID vừa được tạo
                    return generatedId;
                }
                rs.close();
            }
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return -1;
    }

    public int updateImportOrderSupplier(int supplierId) {
        String query = "UPDATE ImportStocks SET SupplierID = ?";
        try {
            PreparedStatement ps = connector.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, supplierId);

            return ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e);
        }
        return -1;
    }

    public int importStock(List<Map<String, String>> l) {

        String query = "UPDATE Products SET Stock = Stock + CASE";
        String quantity = "";
        String ids = "(";

        for (Map<String, String> map : l) {
            quantity += " WHEN ProductID = " + map.get("id") + " THEN " + map.get("stock");

            if (l.size() == 1) {
                ids += map.get("id") + ")";
            } else {
                ids += ", " + map.get("id");
            }
        }

        if (l.size() != 1) {
            ids += ")";
            query += quantity + " END WHERE ProductID IN " + "(" + ids.substring(3);
        } else {
            query += quantity + " END WHERE ProductID IN " + ids;
        }

        System.out.println(query);

        return 1;
    }

    private int completedImportOrder(int importId, long total) {
        String query = "UPDATE ImportStocks SET Completed = 1, ImportDate = GETDATE(), TotalCost = ? WHERE ImportID = ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setLong(1, total);
            ps.setInt(2, importId);

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return -1;
    }

    public int importStock(int importId) {
        String query = "UPDATE P SET P.Stock = P.Stock + D.ImportQuantity FROM Products P INNER JOIN ImportStockDetails D ON P.ProductID = D.ProductID WHERE D.ImportID = ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setInt(1, importId);

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return -1;
    }

    public ArrayList<ImportOrder> filterHistoryByDate(String from, String to) {
        ArrayList<ImportOrder> list = new ArrayList<>();

        String query = "SELECT * FROM ImportStocks I JOIN Suppliers S ON I.SupplierID = S.SupplierID WHERE ImportDate BETWEEN ? AND ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setString(1, from + " 00:00:00");
            ps.setString(2, to + " 23:59:59");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Supplier s = new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("TaxID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getTimestamp("CreatedDate").toLocalDateTime(),
                        rs.getTimestamp("LastModify").toLocalDateTime(),
                        rs.getInt("IsDeleted"),
                        rs.getInt("IsActivate")
                );

                ImportOrder io = new ImportOrder(
                        rs.getInt("ImportID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("SupplierID"),
                        rs.getDate("ImportDate"),
                        rs.getLong("TotalCost"),
                        rs.getInt("Completed")
                );

                io.setSupplier(s);

                list.add(io);
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return list;
    }

    // =========================================================================
    // 1. Statistics tổng số đơn nhập theo ngày/tháng/năm
    public Map<String, Integer> getImportOrdersCountByDate() {

        String sql = "SELECT \n"
                + "    CAST(I.ImportDate AS DATE) AS ImportDate, \n"
                + "    SUM(D.ImportQuantity) AS TotalQuantity\n"
                + "FROM ImportStocks I\n"
                + "JOIN ImportStockDetails D ON I.ImportID = D.ImportID\n"
                + "WHERE CAST(I.ImportDate AS DATE) IN (\n"
                + "    SELECT DISTINCT TOP 5 CAST(ImportDate AS DATE)\n"
                + "    FROM ImportStocks\n"
                + "    ORDER BY CAST(ImportDate AS DATE) DESC\n"
                + ")\n"
                + "GROUP BY CAST(I.ImportDate AS DATE)\n"
                + "ORDER BY ImportDate DESC";

        Map<String, Integer> result = new LinkedHashMap<>();
        try {
            PreparedStatement ps = connector.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("ImportDate"),
                        rs.getInt("TotalQuantity"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    public Map<String, Integer> getImportOrdersCountByMonth() {

        String sql = "SELECT FORMAT(I.ImportDate, 'yyyy-MM') AS ImportMonth, SUM(D.ImportQuantity) AS TotalQuantity\n"
                + "FROM ImportStocks I\n"
                + "JOIN ImportStockDetails D ON I.ImportID = D.ImportID\n"
                + "GROUP BY FORMAT(I.ImportDate, 'yyyy-MM')\n"
                + "ORDER BY ImportMonth DESC";

        Map<String, Integer> result = new LinkedHashMap<>();
        try {
            PreparedStatement ps = connector.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("ImportMonth"),
                        rs.getInt("TotalQuantity"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    // 3. Statistics tổng số đơn nhập theo nhà cung cấp
    public Map<String, Integer> getOrdersBySupplier() {
        String sql = "SELECT TOP 5 s.SupplierID, s.Name AS SupplierName, COUNT(io.ImportID) AS OrderCount\n"
                + "FROM ImportStocks io\n"
                + "JOIN Suppliers s ON io.SupplierID = s.SupplierID\n"
                + "GROUP BY s.SupplierID, s.Name\n"
                + "ORDER BY OrderCount DESC";

        Map<String, Integer> result = new LinkedHashMap<>();
        try {
            PreparedStatement ps = connector.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("SupplierName"), rs.getInt("OrderCount"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

    // 4. Statistics top products nhập nhiều nhất
    public Map<String, Integer> getTopImportedProducts() throws SQLException {
        String sql = "SELECT TOP 5 D.ProductID, P.Model, SUM(D.ImportQuantity) AS TotalQuantity\n"
                + "FROM ImportStockDetails D\n"
                + "JOIN Products P ON D.ProductID = P.ProductID\n"
                + "GROUP BY D.ProductID, P.Model\n"
                + "ORDER BY TotalQuantity DESC";

        Map<String, Integer> result = new LinkedHashMap<>();
        try {
            PreparedStatement ps = connector.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("Model"), rs.getInt("TotalQuantity"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return result;
    }

}
