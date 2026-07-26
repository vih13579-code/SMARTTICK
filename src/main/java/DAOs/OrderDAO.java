/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.Cart;
import Models.Customer;
import Models.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAO {
    private static final int BULK_DEPOSIT_QUANTITY = 6;
    private static final int BULK_DEPOSIT_PERCENT = 30;

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    public List<Order> getOrderList() {
        List<Order> list = new ArrayList<>();
        String url = "select * from Orders where Orders.Status != 6 Order BY Orders.Status  ASC";
        try {

            PreparedStatement pre = connector.prepareStatement(url);
            ResultSet rs = pre.executeQuery();

            while (rs.next()) {
                Order o = new Order(rs.getInt("OrderID"),
                        rs.getInt("CustomerID"),
                        rs.getString("FullName"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getInt("TotalAmount"),
                        rs.getString("OrderedDate"),
                        rs.getInt("Status"));
                list.add(o);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    public Order getOrderByID(String orderID) {
        Order o = new Order();
        String query = "select * from Orders where Orders.OrderID = ?";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setString(1, orderID);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                o.setOrderID(rs.getInt("OrderID"));
                o.setAccountID(rs.getInt("CustomerID"));
                o.setFullName(rs.getString("FullName"));
                o.setPhone(rs.getString("PhoneNumber"));
                o.setAddress(rs.getString("Address"));
                o.setTotalAmount(rs.getInt("TotalAmount"));
                o.setOrderDate(rs.getString("OrderedDate"));
                o.setStatus(rs.getInt("Status"));
                o.setDiscount(rs.getInt("Discount"));
                readPaymentFields(rs, o);
            }
        } catch (Exception e) {
        }
        return o;
    }

    public int getNewestOrderID() {
        int id = 0;
        try {
            PreparedStatement pre = connector.prepareStatement("Select top 1* from Orders\n"
                    + "order by OrderID desc");
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return id;
    }

    // public void createNewOrder(Order o) {
    // try {
    // String data = "";
    // data = "'" + o.getAccountID() + "',";
    // data += "'" + o.getFullName() + "',";
    // data += "'" + o.getPhone() + "',";
    // data += "N'" + o.getAddress() + "',";
    // data += o.getTotalAmount() + "";
    //
    // PreparedStatement pre = connector.prepareStatement("Insert into [Orders]
    // (CustomerID, FullName, PhoneNumber, [Address], TotalAmount, [Status],
    // OrderedDate)"
    // + " values (" + data + ", 1, GETDATE())");
    // pre.executeUpdate();
    // } catch (SQLException e) {
    // System.out.println(e);
    // }
    // }
    public void createOrder(Order o) {
        try {
            String data = "";
            data = "'" + o.getAccountID() + "',";
            data += "'" + o.getFullName() + "',";
            data += "'" + o.getPhone() + "',";
            data += "N'" + o.getAddress() + "',";
            data += o.getTotalAmount() + "";

            PreparedStatement pre = connector.prepareStatement(
                    "Insert into [Orders] (CustomerID, FullName, PhoneNumber, [Address], TotalAmount, [Status], OrderedDate, Discount)"
                            + " values (" + data + ", 1, DATEADD(HOUR, 7, GETUTCDATE()), ?)");
            pre.setInt(1, o.getDiscount());
            pre.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void addOrderDetail(int orderID, int productID, int quantity, long price) {
        try {
            PreparedStatement pre = connector.prepareStatement("Insert into [OrderDetails] values"
                    + "(?, ?, ?, ?)");
            pre.setInt(1, orderID);
            pre.setInt(2, productID);
            pre.setInt(3, quantity);
            pre.setLong(4, price);
            pre.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void subtractQuantityAfterBuy(int productID, int quantity) {
        try {
            PreparedStatement pr = connector
                    .prepareStatement("Update Products set Stock = Stock - ? where ProductID=?");
            pr.setInt(1, quantity);
            pr.setInt(2, productID);
            pr.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void plusQuantityAfterCancel(int productID, int quantity) {
        try {
            PreparedStatement pr = connector
                    .prepareStatement("Update Products set Stock = Stock + ? where ProductID=?");
            pr.setInt(1, quantity);
            pr.setInt(2, productID);
            pr.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void addQuantityAfterCancel(int productID, int quantity) {
        try {
            PreparedStatement pr = connector
                    .prepareStatement("Update Products set Stock = Stock + ? where ProductID=?");
            pr.setInt(1, quantity);
            pr.setInt(2, productID);
            pr.executeUpdate();
        } catch (Exception e) {
        }
    }

    public int updateOrder(int orderID, int status) {
        int count = 0;
        try {
            connector.setAutoCommit(false);
            int currentStatus = getOrderStatusForUpdate(connector, orderID);
            if (!isValidStatusTransition(currentStatus, status)) {
                connector.rollback();
                return 0;
            }
            if (status == 4) {
                deductStockForDeliveredOrder(connector, orderID);
            }
            String sql = status == 4
                    ? "UPDATE Orders SET Status = ?, DeliveredDate = DATEADD(HOUR, 7, GETUTCDATE()) WHERE OrderID = ?"
                    : "UPDATE Orders SET Status = ? WHERE OrderID = ?";
            try (PreparedStatement pre = connector.prepareStatement(sql)) {
                pre.setInt(1, status);
                pre.setInt(2, orderID);
                count = pre.executeUpdate();
            }
            connector.commit();
        } catch (Exception e) {
            try {
                connector.rollback();
            } catch (SQLException ignored) {
            }
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                connector.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
        return count;
    }

    /**
     * Force update order status without validating transition rules.
     * Used for administrative overrides.
     */
    public int forceUpdateOrder(int orderID, int status) {
        int count = 0;
        try {
            connector.setAutoCommit(false);
            int currentStatus = getOrderStatusForUpdate(connector, orderID);
            if (status == 4 && currentStatus != 4) {
                deductStockForDeliveredOrder(connector, orderID);
            }
            String sql = status == 4
                    ? "UPDATE Orders SET Status = ?, DeliveredDate = DATEADD(HOUR, 7, GETUTCDATE()) WHERE OrderID = ?"
                    : "UPDATE Orders SET Status = ? WHERE OrderID = ?";
            try (PreparedStatement pre = connector.prepareStatement(sql)) {
                pre.setInt(1, status);
                pre.setInt(2, orderID);
                count = pre.executeUpdate();
            }
            connector.commit();
        } catch (Exception e) {
            try {
                connector.rollback();
            } catch (SQLException ignored) {
            }
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                connector.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
        return count;
    }

    public int placeOrderTransaction(Order order, List<Cart> selectedItems, int customerId, String source,
            Integer voucherId) throws SQLException {
        if (order == null || selectedItems == null || selectedItems.isEmpty()) {
            throw new SQLException("No products selected.");
        }

        boolean oldAutoCommit = connector.getAutoCommit();
        connector.setAutoCommit(false);
        try {
            Map<Integer, Long> prices = new HashMap<>();
            long subtotal = 0;

            String productSql = "SELECT ProductID, FullName, Price, Stock, IsDeleted, Image, CategoryID "
                    + "FROM Products WITH (UPDLOCK, ROWLOCK) WHERE ProductID = ?";
            try (PreparedStatement ps = connector.prepareStatement(productSql)) {
                for (Cart item : selectedItems) {
                    if (item == null || item.getQuantity() <= 0) {
                        throw new SQLException("Invalid quantity.");
                    }
                    ps.setInt(1, item.getProductID());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next() || rs.getInt("IsDeleted") != 0) {
                            throw new SQLException("Product is not available.");
                        }
                        int stock = rs.getInt("Stock");
                        if (stock < item.getQuantity()) {
                            throw new SQLException("Not enough stock for " + rs.getString("FullName") + ".");
                        }
                        long price = rs.getLong("Price");
                        prices.put(item.getProductID(), price);
                        item.setPrice((int) price);
                        item.setFullName(rs.getString("FullName"));
                        item.setImage(rs.getString("Image"));
                        item.setCategory(rs.getInt("CategoryID"));
                        subtotal += price * item.getQuantity();
                    }
                }
            }

            int discount = calculateValidDiscount(connector, customerId, voucherId, subtotal);
            long finalTotal = Math.max(0, subtotal - discount);
            int totalQuantity = 0;
            for (Cart item : selectedItems) {
                totalQuantity += item.getQuantity();
            }
            long depositAmount = totalQuantity >= BULK_DEPOSIT_QUANTITY
                    ? Math.round(finalTotal * (BULK_DEPOSIT_PERCENT / 100.0))
                    : 0;
            long amountDue = Math.max(0, finalTotal - depositAmount);
            order.setDepositAmount(depositAmount);
            order.setAmountDue(amountDue);
            if (order.getPaymentMethod() == null || order.getPaymentMethod().trim().isEmpty()) {
                order.setPaymentMethod("cod");
            }
            if (order.getPaymentStatus() == null || order.getPaymentStatus().trim().isEmpty()) {
                order.setPaymentStatus(depositAmount > 0 ? "deposit_pending" : "pending");
            }
            order.setPaymentReference(null);

            boolean hasPaymentColumns = columnExists(connector, "Orders", "PaymentMethod")
                    && columnExists(connector, "Orders", "PaymentStatus")
                    && columnExists(connector, "Orders", "DepositAmount")
                    && columnExists(connector, "Orders", "AmountDue")
                    && columnExists(connector, "Orders", "PaymentReference");
            String orderSql = hasPaymentColumns
                    ? "INSERT INTO [Orders] (CustomerID, FullName, PhoneNumber, [Address], TotalAmount, [Status], OrderedDate, Discount, PaymentMethod, PaymentStatus, DepositAmount, AmountDue, PaymentReference) "
                            + "VALUES (?, ?, ?, ?, ?, 1, DATEADD(HOUR, 7, GETUTCDATE()), ?, ?, ?, ?, ?, ?)"
                    : "INSERT INTO [Orders] (CustomerID, FullName, PhoneNumber, [Address], TotalAmount, [Status], OrderedDate, Discount) "
                            + "VALUES (?, ?, ?, ?, ?, 1, DATEADD(HOUR, 7, GETUTCDATE()), ?)";
            int orderId;
            try (PreparedStatement ps = connector.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setString(2, order.getFullName());
                ps.setString(3, order.getPhone());
                ps.setString(4, order.getAddress());
                ps.setLong(5, finalTotal);
                ps.setInt(6, discount);
                if (hasPaymentColumns) {
                    ps.setString(7, order.getPaymentMethod());
                    ps.setString(8, order.getPaymentStatus());
                    ps.setLong(9, depositAmount);
                    ps.setLong(10, amountDue);
                    ps.setString(11, order.getPaymentReference());
                }
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Cannot create order.");
                }
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Cannot read created order ID.");
                    }
                    orderId = keys.getInt(1);
                }
            }

            try (PreparedStatement detailPs = connector.prepareStatement(
                    "INSERT INTO [OrderDetails] (OrderID, ProductID, Quantity, Price) VALUES (?, ?, ?, ?)");
                    PreparedStatement cartPs = connector
                            .prepareStatement("DELETE FROM Carts WHERE ProductID = ? AND CustomerID = ?")) {
                for (Cart item : selectedItems) {
                    long price = prices.get(item.getProductID());
                    detailPs.setInt(1, orderId);
                    detailPs.setInt(2, item.getProductID());
                    detailPs.setInt(3, item.getQuantity());
                    detailPs.setLong(4, price);
                    detailPs.addBatch();

                    if (!"buyNow".equalsIgnoreCase(source)) {
                        cartPs.setInt(1, item.getProductID());
                        cartPs.setInt(2, customerId);
                        cartPs.addBatch();
                    }
                }
                detailPs.executeBatch();
                if (!"buyNow".equalsIgnoreCase(source)) {
                    cartPs.executeBatch();
                }
            }

            if (voucherId != null) {
                consumeVoucher(connector, customerId, voucherId);
            }

            connector.commit();
            order.setDiscount(discount);
            order.setTotalAmount(finalTotal);
            order.setAccountID(customerId);
            order.setOrderID(orderId);
            return orderId;
        } catch (SQLException ex) {
            connector.rollback();
            throw ex;
        } finally {
            connector.setAutoCommit(oldAutoCommit);
        }
    }

    public int cancelCustomerOrder(int orderId, int customerId) {
        int count = 0;
        try {
            connector.setAutoCommit(false);
            int status;
            try (PreparedStatement ps = connector.prepareStatement(
                    "SELECT Status FROM Orders WITH (UPDLOCK, ROWLOCK) WHERE OrderID = ? AND CustomerID = ?")) {
                ps.setInt(1, orderId);
                ps.setInt(2, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        connector.rollback();
                        return 0;
                    }
                    status = rs.getInt("Status");
                }
            }
            if (status != 1) {
                connector.rollback();
                return 0;
            }
            try (PreparedStatement ps = connector.prepareStatement(
                    "UPDATE Orders SET Status = 5 WHERE OrderID = ? AND CustomerID = ? AND Status = 1")) {
                ps.setInt(1, orderId);
                ps.setInt(2, customerId);
                count = ps.executeUpdate();
            }
            connector.commit();
        } catch (SQLException ex) {
            try {
                connector.rollback();
            } catch (SQLException ignored) {
            }
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                connector.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
        return count;
    }

    private int getOrderStatusForUpdate(Connection connection, int orderId) throws SQLException {
        try (PreparedStatement ps = connection
                .prepareStatement("SELECT Status FROM Orders WITH (UPDLOCK, ROWLOCK) WHERE OrderID = ?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("Status") : 0;
            }
        }
    }

    // Public wrapper to read the current order status without locking.
    public int getOrderStatus(int orderId) {
        String sql = "SELECT Status FROM Orders WHERE OrderID = ?";
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Status");
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return 0;
    }

    private boolean isValidStatusTransition(int currentStatus, int nextStatus) {
        if (currentStatus < 1 || currentStatus > 5 || nextStatus < 1 || nextStatus > 5) {
            return false;
        }
        if (currentStatus == 4 || currentStatus == 5) {
            return false;
        }
        if (nextStatus == 5) {
            return currentStatus == 1 || currentStatus == 2;
        }
        return nextStatus == currentStatus + 1;
    }

    private void restoreStockForOrder(Connection connection, int orderId) throws SQLException {
        String sql = "UPDATE p SET p.Stock = p.Stock + od.Quantity "
                + "FROM Products p JOIN OrderDetails od ON p.ProductID = od.ProductID WHERE od.OrderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private void readPaymentFields(ResultSet rs, Order order) {
        try {
            order.setPaymentMethod(rs.getString("PaymentMethod"));
        } catch (SQLException ignored) {
        }
        try {
            order.setPaymentStatus(rs.getString("PaymentStatus"));
        } catch (SQLException ignored) {
        }
        try {
            order.setDepositAmount(rs.getLong("DepositAmount"));
        } catch (SQLException ignored) {
        }
        try {
            order.setAmountDue(rs.getLong("AmountDue"));
        } catch (SQLException ignored) {
        }
        try {
            order.setPaymentReference(rs.getString("PaymentReference"));
        } catch (SQLException ignored) {
        }
    }

    private void deductStockForDeliveredOrder(Connection connection, int orderId) throws SQLException {
        String sql = "SELECT od.ProductID, od.Quantity, p.Stock FROM OrderDetails od "
                + "JOIN Products p ON p.ProductID = od.ProductID "
                + "WHERE od.OrderID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("ProductID");
                    int quantity = rs.getInt("Quantity");
                    int stock = rs.getInt("Stock");
                    if (stock < quantity) {
                        throw new SQLException(
                                "Not enough stock to complete delivery for product ID " + productId + ".");
                    }
                    try (PreparedStatement deduct = connection.prepareStatement(
                            "UPDATE Products SET Stock = Stock - ? WHERE ProductID = ? AND IsDeleted = 0")) {
                        deduct.setInt(1, quantity);
                        deduct.setInt(2, productId);
                        int affected = deduct.executeUpdate();
                        if (affected == 0) {
                            throw new SQLException("Unable to deduct stock for product ID " + productId + ".");
                        }
                    }
                }
            }
        }
    }

    private int calculateValidDiscount(Connection connection, int customerId, Integer voucherId, long subtotal)
            throws SQLException {
        if (voucherId == null) {
            return 0;
        }
        String sql = "SELECT cv.Quantity, cv.ExpirationDate, v.VoucherValue, v.VoucherType, v.StartDate, v.EndDate, "
                + "v.UsedCount, v.MaxUsedCount, v.MaxDiscountAmount, v.MinOrderValue, v.Status "
                + "FROM CustomerVoucher cv WITH (UPDLOCK, ROWLOCK) "
                + "JOIN Vouchers v WITH (UPDLOCK, ROWLOCK) ON cv.VoucherID = v.VoucherID "
                + "WHERE cv.CustomerID = ? AND cv.VoucherID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Voucher is not available.");
                }
                Timestamp now = new Timestamp(System.currentTimeMillis());
                Timestamp expirationDate = rs.getTimestamp("ExpirationDate");
                Timestamp startDate = rs.getTimestamp("StartDate");
                Timestamp endDate = rs.getTimestamp("EndDate");
                int quantity = rs.getInt("Quantity");
                int maxUsed = rs.getInt("MaxUsedCount");
                int used = rs.getInt("UsedCount");
                int minOrder = rs.getInt("MinOrderValue");
                if (rs.getInt("Status") != 1 || quantity <= 0
                        || (startDate != null && startDate.after(now))
                        || (endDate != null && endDate.before(now))
                        || (expirationDate != null && expirationDate.before(now))
                        || (maxUsed > 0 && used >= maxUsed)) {
                    throw new SQLException("Voucher is expired or inactive.");
                }
                if (subtotal < minOrder) {
                    throw new SQLException("Order does not meet voucher minimum value.");
                }
                int value = rs.getInt("VoucherValue");
                int type = rs.getInt("VoucherType");
                int maxDiscount = rs.getInt("MaxDiscountAmount");
                long discount = type == 1 ? Math.round(subtotal * (value / 100.0)) : value;
                if (type == 1 && maxDiscount > 0 && discount > maxDiscount) {
                    discount = maxDiscount;
                }
                return (int) Math.min(discount, subtotal);
            }
        }
    }

    private void consumeVoucher(Connection connection, int customerId, int voucherId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE CustomerVoucher SET Quantity = Quantity - 1 WHERE CustomerID = ? AND VoucherID = ? AND Quantity > 1")) {
            ps.setInt(1, customerId);
            ps.setInt(2, voucherId);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                try (PreparedStatement delete = connection.prepareStatement(
                        "DELETE FROM CustomerVoucher WHERE CustomerID = ? AND VoucherID = ? AND Quantity = 1")) {
                    delete.setInt(1, customerId);
                    delete.setInt(2, voucherId);
                    delete.executeUpdate();
                }
            }
        }
        try (PreparedStatement ps = connection
                .prepareStatement("UPDATE Vouchers SET UsedCount = UsedCount + 1 WHERE VoucherID = ?")) {
            ps.setInt(1, voucherId);
            ps.executeUpdate();
        }
    }

    public int deleteOrder(int month) {
        int count = 0;
        String query = "DECLARE @CurrentUTC DATETIME = GETUTCDATE();\n"
                + "DECLARE @ThresholdUTC DATETIME = DATEADD(MONTH, -?, @CurrentUTC);\n"
                + "\n"
                + "\n"
                + "DELETE FROM OrderDetails \n"
                + "WHERE OrderID IN (\n"
                + "    SELECT OrderID FROM Orders \n"
                + "    WHERE [Status] = '5' AND OrderedDate < @ThresholdUTC\n"
                + ");\n"
                + "\n"
                + "\n"
                + "DELETE FROM Orders \n"
                + "WHERE [Status] = '5' AND OrderedDate < @ThresholdUTC;";
        try {
            PreparedStatement pre = connector.prepareStatement(query);

            pre.setInt(1, month);
            count = pre.executeUpdate();

        } catch (Exception e) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return count;
    }

    public List<Order> getAllOrderOfCustomer(int customerID) {
        List<Order> list = new ArrayList<>();
        try {
            PreparedStatement pre = connector.prepareStatement("SELECT * FROM Orders WHERE CustomerID = ?\n"
                    + "Order by OrderedDate DESC");
            pre.setInt(1, customerID);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                list.add(new Order(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getInt(8), rs.getInt(9)));
            }
        } catch (SQLException e) {
            System.out.println(e + "");
        }
        return list;
    }

    public int checkHaveOrders(int id) {
        String query = "SELECT * FROM Orders WHERE "
                + "CustomerID = ? AND Status != 4 AND Status != 5";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, id);

            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                return 1;
            }
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    public List<Order> searchOrders(String searchQuery) {
        List<Order> list = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE "
                + "FullName LIKE ? OR "
                + "PhoneNumber LIKE ?  Order BY Status ASC";
        try {
            PreparedStatement pre = connector.prepareStatement(query);

            pre.setString(1, "%" + searchQuery + "%");
            pre.setString(2, "%" + searchQuery + "%");

            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                Order o = new Order(
                        rs.getInt("OrderID"),
                        rs.getInt("CustomerID"),
                        rs.getString("FullName"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getInt("TotalAmount"),
                        rs.getString("OrderedDate"),
                        rs.getInt("Status"));
                list.add(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Customer getCustomerByOrderId(int id) {
        String sql = "SELECT c.CustomerID, c.FullName, c.PhoneNumber, c.Email, "
                + "c.IsBlock, c.IsDeleted FROM customers c "
                + "JOIN orders o ON c.CustomerID = o.CustomerID WHERE o.OrderID = ?";
        try (PreparedStatement ps = connector.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("CustomerID"),
                            rs.getString("FullName"),
                            null,
                            null,
                            null,
                            rs.getString("PhoneNumber"),
                            rs.getString("Email"),
                            null,
                            rs.getInt("IsBlock"),
                            rs.getInt("IsDeleted"),
                            null);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null; // Không tìm thấy khách hàng
    }

    public List<Order> getOrdersToDelete(int month) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders "
                + "WHERE [Status] = '5' "
                + "AND OrderedDate < DATEADD(MONTH, -?, GETUTCDATE())";
        try {
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, month);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                Order o = new Order(
                        rs.getInt("OrderID"),
                        rs.getInt("CustomerID"),
                        rs.getString("FullName"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getInt("TotalAmount"),
                        rs.getString("OrderedDate"),
                        rs.getInt("Status"));
                list.add(o);
            }
        } catch (SQLException e) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return list;
    }

    public static void main(String[] args) {
        OrderDAO o = new OrderDAO();
        // o.addOrderDetail(1, 1, 3, 34000000);
        // List<Order> list = o.getAllOrderOfCustomer(1);
        // for (Order order : list) {
        // System.out.println(order.getAddress());
        // }
        Order od = new Order(13, "Bui Minh Nhut", "034931105", "fjds, fds fds, sdfhds, dshfdd", 20000000, 30000);

    }
}
