package DAOs;

import DB.DBContext;
import Models.Brand;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO {
    private final DBContext db = new DBContext();

    private Connection connection() throws SQLException {
        Connection connection = db.getConnection();
        if (connection == null) throw new SQLException("Database connection unavailable");
        return connection;
    }

    public List<String> getAllBrandName() {
        List<String> names = new ArrayList<>();
        for (Brand brand : getAllBrand()) names.add(brand.getName());
        return names;
    }

    public int getBrandIdByName(String brandName) {
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement("SELECT BrandID FROM Brands WHERE Name = ?")) {
            statement.setString(1, brandName);
            try (ResultSet rs = statement.executeQuery()) { return rs.next() ? rs.getInt(1) : -1; }
        } catch (SQLException ex) { return -1; }
    }

    public List<Brand> getAllBrand() {
        List<Brand> brands = new ArrayList<>();
        try (Connection connection = connection()) {
            boolean hasStatus = columnExists(connection, "Brands", "Status");
            boolean hasCreatedDate = columnExists(connection, "Brands", "CreatedDate");
            String sql = "SELECT b.BrandID, b.Name, "
                       + (hasStatus ? "b.Status" : "CAST(1 AS BIT) AS Status") + ", "
                       + (hasCreatedDate ? "b.CreatedDate" : "CAST(NULL AS DATETIME) AS CreatedDate") + ", "
                       + "(SELECT COUNT(*) FROM Products p WHERE p.BrandID = b.BrandID AND p.IsDeleted = 0) AS ProductCount "
                       + "FROM Brands b ORDER BY b.Name";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    brands.add(new Brand(
                        rs.getInt("BrandID"),
                        rs.getString("Name"),
                        rs.getBoolean("Status"),
                        rs.getTimestamp("CreatedDate"),
                        rs.getInt("ProductCount")
                    ));
                }
            }
        } catch (SQLException ex) { System.err.println(ex.getMessage()); }
        return brands;
    }

    public int createBrand(Brand brand) { return createBrand(brand == null ? null : brand.getName()); }

    public int createBrand(String name) {
        if (name == null || name.trim().isEmpty()) return 0;
        try (Connection connection = connection()) {
            String sql = columnExists(connection, "Brands", "Status")
                    ? "INSERT INTO Brands (Name, Status) VALUES (?, 1)"
                    : "INSERT INTO Brands (Name) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name.trim());
                return statement.executeUpdate();
            }
        } catch (SQLException ex) { return 0; }
    }

    public int updateBrand(int id, String name) { return executeName("UPDATE Brands SET Name = ? WHERE BrandID = ?", name, id); }

    public int toggleBrandStatus(int id, boolean status) {
        try (Connection connection = connection()) {
            if (!columnExists(connection, "Brands", "Status")) return 0;
            try (PreparedStatement statement = connection.prepareStatement("UPDATE Brands SET Status = ? WHERE BrandID = ?")) {
                statement.setBoolean(1, status);
                statement.setInt(2, id);
                return statement.executeUpdate();
            }
        } catch (SQLException ex) {
            return 0;
        }
    }

    public int deleteBrand(int id) {
        if (isInUse(id)) return -1;
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM Brands WHERE BrandID = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate();
        } catch (SQLException ex) { return 0; }
    }

    public boolean isInUse(int id) {
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Products WHERE BrandID = ?")) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException ex) { return true; }
    }

    private int executeName(String sql, String name, int id) {
        if (name == null || name.trim().isEmpty()) return 0;
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.trim());
            if (id > 0) statement.setInt(2, id);
            return statement.executeUpdate();
        } catch (SQLException ex) { return 0; }
    }

    private boolean columnExists(Connection connection, String table, String column) throws SQLException {
        String sql = "SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS "
                   + "WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, table);
            statement.setString(2, column);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }
}
