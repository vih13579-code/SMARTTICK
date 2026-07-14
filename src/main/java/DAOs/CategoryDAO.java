package DAOs;

import DB.DBContext;
import Models.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final DBContext db = new DBContext();

    private Connection connection() throws SQLException {
        Connection connection = db.getConnection();
        if (connection == null) throw new SQLException("Database connection unavailable");
        return connection;
    }

    public List<String> getAllCategoryNames() {
        List<String> names = new ArrayList<>();
        for (Category category : getAllCategories()) names.add(category.getName());
        return names;
    }

    public int getCategoryIdByName(String categoryName) {
        String sql = "SELECT CategoryID FROM Categories WHERE Name = ?";
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoryName);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException ex) {
            return -1;
        }
    }

    public List<Category> getAllCategory() { return getAllCategories(); }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = connection()) {
            boolean hasStatus = columnExists(connection, "Categories", "Status");
            boolean hasCreatedDate = columnExists(connection, "Categories", "CreatedDate");
            String sql = "SELECT c.CategoryID, c.Name, "
                       + (hasStatus ? "c.Status" : "CAST(1 AS BIT) AS Status") + ", "
                       + (hasCreatedDate ? "c.CreatedDate" : "CAST(NULL AS DATETIME) AS CreatedDate") + ", "
                       + "(SELECT COUNT(*) FROM Products p WHERE p.CategoryID = c.CategoryID AND p.IsDeleted = 0) AS ProductCount "
                       + "FROM Categories c ORDER BY c.Name";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    categories.add(new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("Name"),
                        rs.getBoolean("Status"),
                        rs.getTimestamp("CreatedDate"),
                        rs.getInt("ProductCount")
                    ));
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return categories;
    }

    public int createCategory(String name) {
        if (name == null || name.trim().isEmpty()) return 0;
        try (Connection connection = connection()) {
            String sql = columnExists(connection, "Categories", "Status")
                    ? "INSERT INTO Categories (Name, Status) VALUES (?, 1)"
                    : "INSERT INTO Categories (Name) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name.trim());
                return statement.executeUpdate();
            }
        } catch (SQLException ex) {
            return 0;
        }
    }

    public int updateCategory(int id, String name) {
        return executeName("UPDATE Categories SET Name = ? WHERE CategoryID = ?", name, id);
    }

    public int toggleCategoryStatus(int id, boolean status) {
        try (Connection connection = connection()) {
            if (!columnExists(connection, "Categories", "Status")) return 0;
            try (PreparedStatement statement = connection.prepareStatement("UPDATE Categories SET Status = ? WHERE CategoryID = ?")) {
                statement.setBoolean(1, status);
                statement.setInt(2, id);
                return statement.executeUpdate();
            }
        } catch (SQLException ex) {
            return 0;
        }
    }

    public int deleteCategory(int id) {
        if (isInUse(id)) return -1;
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM Categories WHERE CategoryID = ?")) {
            statement.setInt(1, id);
            return statement.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public boolean isInUse(int id) {
        try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Products WHERE CategoryID = ?")) {
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
