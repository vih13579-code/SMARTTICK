package DAOs;

import DB.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class StatisticDAO {

    private final DBContext db = new DBContext();
    private final Connection connector = db.getConnection();

    public Map<String, Long> getOverview(LocalDate startDate, LocalDate endDateExclusive) {
        Map<String, Long> data = new LinkedHashMap<>();
        data.put("totalEmployees", scalar("SELECT COUNT(*) FROM Employees WHERE RoleID <> 1"));
        data.put("totalCustomers", scalar("SELECT COUNT(*) FROM Customers WHERE IsDeleted = 0"));
        data.put("totalProducts", scalar("SELECT COUNT(*) FROM Products WHERE IsDeleted = 0"));
        data.put("totalOrders", scalar("SELECT COUNT(*) FROM Orders"));
        data.put("totalRevenue", scalar("SELECT COALESCE(SUM(TotalAmount), 0) FROM Orders"));
        data.put("selectedRevenue", scalarBetween("SELECT COALESCE(SUM(TotalAmount), 0) FROM Orders WHERE OrderedDate >= ? AND OrderedDate < ?", startDate, endDateExclusive));
        data.put("selectedOrders", scalarBetween("SELECT COUNT(*) FROM Orders WHERE OrderedDate >= ? AND OrderedDate < ?", startDate, endDateExclusive));
        data.put("activeVouchers", scalar("SELECT COUNT(*) FROM Vouchers WHERE Status = 1 AND EndDate >= GETDATE()"));
        data.put("expiredVouchers", scalar("SELECT COUNT(*) FROM Vouchers WHERE EndDate < GETDATE() OR Status = 0"));
        data.put("lowStockProducts", scalar("SELECT COUNT(*) FROM Products WHERE IsDeleted = 0 AND Stock > 0 AND Stock <= 5"));
        data.put("outOfStockProducts", scalar("SELECT COUNT(*) FROM Products WHERE IsDeleted = 0 AND Stock = 0"));
        data.put("totalStock", scalar("SELECT COALESCE(SUM(Stock), 0) FROM Products WHERE IsDeleted = 0"));
        return data;
    }

    public List<Map<String, Object>> getDailyRevenue(LocalDate startDate, LocalDate endDateExclusive) {
        return dailySeries(
                "SELECT CAST(OrderedDate AS DATE) AS StatisticDate, COALESCE(SUM(TotalAmount), 0) AS StatisticValue "
                + "FROM Orders WHERE OrderedDate >= ? AND OrderedDate < ? "
                + "GROUP BY CAST(OrderedDate AS DATE) ORDER BY StatisticDate",
                startDate, endDateExclusive);
    }

    public List<Map<String, Object>> getDailyOrders(LocalDate startDate, LocalDate endDateExclusive) {
        return dailySeries(
                "SELECT CAST(OrderedDate AS DATE) AS StatisticDate, COUNT(*) AS StatisticValue "
                + "FROM Orders WHERE OrderedDate >= ? AND OrderedDate < ? "
                + "GROUP BY CAST(OrderedDate AS DATE) ORDER BY StatisticDate",
                startDate, endDateExclusive);
    }

    public List<Map<String, Object>> getCustomerGrowth(LocalDate startDate, LocalDate endDateExclusive) {
        return dailySeries(
                "SELECT CAST(CreatedDate AS DATE) AS StatisticDate, COUNT(*) AS StatisticValue "
                + "FROM Customers WHERE CreatedDate >= ? AND CreatedDate < ? AND IsDeleted = 0 "
                + "GROUP BY CAST(CreatedDate AS DATE) ORDER BY StatisticDate",
                startDate, endDateExclusive);
    }

    public List<Map<String, Object>> getTopSellingProducts(LocalDate startDate, LocalDate endDateExclusive, int limit) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "SELECT TOP (?) p.ProductID, p.FullName, COALESCE(SUM(od.Quantity), 0) AS SoldQuantity, "
                + "COALESCE(SUM(od.Quantity * od.Price), 0) AS Revenue "
                + "FROM OrderDetails od "
                + "JOIN Orders o ON od.OrderID = o.OrderID "
                + "JOIN Products p ON od.ProductID = p.ProductID "
                + "WHERE o.OrderedDate >= ? AND o.OrderedDate < ? "
                + "GROUP BY p.ProductID, p.FullName "
                + "ORDER BY SoldQuantity DESC, Revenue DESC";
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, limit);
            setRange(ps, 2, startDate, endDateExclusive);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("productID", rs.getInt("ProductID"));
                row.put("fullName", rs.getString("FullName"));
                row.put("soldQuantity", rs.getLong("SoldQuantity"));
                row.put("revenue", rs.getLong("Revenue"));
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Top products statistic error: " + e.getMessage());
        }
        return rows;
    }

    public List<Map<String, Object>> getTopCustomers(LocalDate startDate, LocalDate endDateExclusive, int limit) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "SELECT TOP (?) c.CustomerID, c.FullName, COUNT(o.OrderID) AS OrderCount, "
                + "COALESCE(SUM(o.TotalAmount), 0) AS Revenue "
                + "FROM Orders o "
                + "JOIN Customers c ON o.CustomerID = c.CustomerID "
                + "WHERE o.OrderedDate >= ? AND o.OrderedDate < ? "
                + "GROUP BY c.CustomerID, c.FullName "
                + "ORDER BY Revenue DESC, OrderCount DESC";
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, limit);
            setRange(ps, 2, startDate, endDateExclusive);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("customerID", rs.getInt("CustomerID"));
                row.put("fullName", rs.getString("FullName"));
                row.put("orderCount", rs.getLong("OrderCount"));
                row.put("revenue", rs.getLong("Revenue"));
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Top customers statistic error: " + e.getMessage());
        }
        return rows;
    }

    private List<Map<String, Object>> dailySeries(String sql, LocalDate startDate, LocalDate endDateExclusive) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            setRange(ps, 1, startDate, endDateExclusive);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("label", rs.getString("StatisticDate"));
                row.put("value", rs.getLong("StatisticValue"));
                rows.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Daily statistic error: " + e.getMessage());
        }
        return rows;
    }

    private long scalar(String sql) {
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("Statistic query error: " + e.getMessage());
        }
        return 0;
    }

    private long scalarBetween(String sql, LocalDate startDate, LocalDate endDateExclusive) {
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            setRange(ps, 1, startDate, endDateExclusive);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("Statistic range query error: " + e.getMessage());
        }
        return 0;
    }

    private void setRange(PreparedStatement ps, int startIndex, LocalDate startDate, LocalDate endDateExclusive) throws SQLException {
        ps.setTimestamp(startIndex, Timestamp.valueOf(startDate.atStartOfDay()));
        ps.setTimestamp(startIndex + 1, Timestamp.valueOf(endDateExclusive.atStartOfDay()));
    }
}
