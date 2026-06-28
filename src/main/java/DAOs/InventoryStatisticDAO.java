package DAOs;

import DB.DBContext;
import Models.InventoryStatistic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class InventoryStatisticDAO {
    public ArrayList<InventoryStatistic> getInventoryByCategory(String categoryName) {
        ArrayList<InventoryStatistic> list = new ArrayList<>();
        String sql = "SELECT p.Model, p.Stock FROM Products p JOIN Categories c ON p.CategoryID=c.CategoryID "
                + "WHERE c.Name=? AND p.IsDeleted=0 ORDER BY p.Stock, p.Model";
        try (Connection connection = new DBContext().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoryName);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) list.add(new InventoryStatistic(rs.getString(1), rs.getInt(2)));
            }
        } catch (SQLException ex) { System.err.println("Inventory category query failed: " + ex.getMessage()); }
        return list;
    }

    public ArrayList<InventoryStatistic> getAllInventory() { return queryInventory(null); }
    public ArrayList<InventoryStatistic> searchInventory(String keyword) { return queryInventory(keyword); }

    private ArrayList<InventoryStatistic> queryInventory(String keyword) {
        ArrayList<InventoryStatistic> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT c.Name,b.Name,p.Model,p.FullName,p.Stock,s.Name,i.ImportDate,iod.ImportPrice "
                + "FROM Products p JOIN Categories c ON p.CategoryID=c.CategoryID JOIN Brands b ON p.BrandID=b.BrandID "
                + "LEFT JOIN ImportStockDetails iod ON p.ProductID=iod.ProductID "
                + "LEFT JOIN ImportStocks i ON iod.ImportID=i.ImportID LEFT JOIN Suppliers s ON i.SupplierID=s.SupplierID ");
        if (keyword != null && !keyword.trim().isEmpty()) sql.append("WHERE p.FullName LIKE ? OR p.Model LIKE ? OR c.Name LIKE ? OR b.Name LIKE ? OR s.Name LIKE ? ");
        sql.append("ORDER BY c.Name,b.Name,p.FullName,i.ImportDate DESC");
        try (Connection connection = new DBContext().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String value = "%" + keyword.trim() + "%";
                for (int i=1;i<=5;i++) statement.setString(i,value);
            }
            try (ResultSet rs=statement.executeQuery()) {
                while (rs.next()) list.add(new InventoryStatistic(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5),rs.getString(6),rs.getDate(7),rs.getLong(8)));
            }
        } catch (SQLException ex) { System.err.println("Inventory query failed: " + ex.getMessage()); }
        return list;
    }
}
