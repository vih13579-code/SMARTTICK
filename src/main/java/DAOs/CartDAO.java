/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.Cart;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();
    private Boolean hasCartVariantColumn;
    private Boolean hasProductVariantsTable;

    private boolean databaseObjectExists(String sql) {
        try (PreparedStatement pre = connector.prepareStatement(sql);
                ResultSet rs = pre.executeQuery()) {
            return rs.next() && rs.getObject(1) != null;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean hasCartVariantColumn() {
        if (hasCartVariantColumn == null) {
            hasCartVariantColumn = databaseObjectExists("SELECT COL_LENGTH('dbo.Carts', 'ProductVariantID')");
        }
        return hasCartVariantColumn;
    }

    private boolean hasProductVariantsTable() {
        if (hasProductVariantsTable == null) {
            hasProductVariantsTable = databaseObjectExists("SELECT OBJECT_ID('dbo.ProductVariants', 'U')");
        }
        return hasProductVariantsTable;
    }

    public List<Cart> getCartOfAccountID(int accountID) {
        List<Cart> list = new ArrayList<>();
        try {
            String sql;
            if (hasCartVariantColumn() && hasProductVariantsTable()) {
                sql = "SELECT c.ProductID, c.ProductVariantID, c.Quantity, "
                        + "COALESCE(v.[Image], p.[Image]) AS Image, p.FullName, p.Price, p.CategoryID, "
                        + "v.ColorName, v.ColorHex "
                        + "FROM Carts c "
                        + "LEFT JOIN Products p ON c.ProductID = p.ProductID "
                        + "LEFT JOIN ProductVariants v ON c.ProductVariantID = v.VariantID AND v.ProductID = p.ProductID "
                        + "WHERE c.CustomerID = ? AND p.IsDeleted = 0 ORDER BY c.CustomerID DESC";
            } else if (hasCartVariantColumn()) {
                sql = "SELECT c.ProductID, c.ProductVariantID, c.Quantity, p.[Image] AS Image, "
                        + "p.FullName, p.Price, p.CategoryID, NULL AS ColorName, NULL AS ColorHex "
                        + "FROM Carts c "
                        + "LEFT JOIN Products p ON c.ProductID = p.ProductID "
                        + "WHERE c.CustomerID = ? AND p.IsDeleted = 0 ORDER BY c.CustomerID DESC";
            } else {
                sql = "SELECT c.ProductID, c.Quantity, p.[Image] AS Image, p.FullName, p.Price, p.CategoryID "
                        + "FROM Carts c "
                        + "LEFT JOIN Products p ON c.ProductID = p.ProductID "
                        + "WHERE c.CustomerID = ? AND p.IsDeleted = 0 ORDER BY c.CustomerID DESC";
            }
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, accountID);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                if (hasCartVariantColumn()) {
                    Integer variantId = rs.getObject("ProductVariantID") == null ? null : rs.getInt("ProductVariantID");
                    list.add(new Cart(rs.getInt("ProductID"), variantId, rs.getInt("Quantity"), rs.getString("Image"),
                            rs.getString("FullName"), rs.getLong("Price"), rs.getInt("CategoryID"),
                            rs.getString("ColorName"), rs.getString("ColorHex")));
                } else {
                    list.add(new Cart(rs.getInt("ProductID"), rs.getInt("Quantity"), rs.getString("Image"),
                            rs.getString("FullName"), rs.getLong("Price"), rs.getInt("CategoryID")));
                }
            }
        } catch (SQLException e) {
            System.out.println(e + "");
        }
        return list;
    }

    public int getNumberOfProduct(int accountID) {
        int num = 0;
        try {
            PreparedStatement pre = connector.prepareStatement("SELECT * FROM Carts c\n"
                    + "  JOIN Products p On c.ProductID = p.ProductID\n"
                    + "  WHERE CustomerID = ? AND p.IsDeleted = 0");
            pre.setInt(1, accountID);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                num++;
            }
        } catch (SQLException e) {
            System.out.println(e + "");
        }
        return num;
    }

    public void addToCart(int customerID, Cart c) {
        try {
            String sql = hasCartVariantColumn()
                    ? "INSERT INTO Carts (CustomerID, ProductID, Quantity, ProductVariantID) VALUES (?, ?, ?, ?)"
                    : "INSERT INTO Carts (CustomerID, ProductID, Quantity) VALUES (?, ?, ?)";
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, customerID);
            pre.setInt(2, c.getProductID());
            pre.setInt(3, c.getQuantity());
            if (hasCartVariantColumn()) {
                if (c.getVariantId() == null || c.getVariantId() <= 0) {
                    pre.setNull(4, java.sql.Types.INTEGER);
                } else {
                    pre.setInt(4, c.getVariantId());
                }
            }
            pre.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + "");
        }
    }

    public Cart getProductOfCart(int customerID, int productID) {
        return getProductOfCart(customerID, productID, null);
    }

    public Cart getProductOfCart(int customerID, int productID, Integer variantId) {
        Cart c = null;
        try {
            String sql;
            if (hasCartVariantColumn()) {
                sql = variantId == null || variantId <= 0
                        ? "Select ProductID, ProductVariantID, Quantity from Carts where CustomerID = ? AND ProductID = ? AND ProductVariantID IS NULL"
                        : "Select ProductID, ProductVariantID, Quantity from Carts where CustomerID = ? AND ProductID = ? AND ProductVariantID = ?";
            } else {
                sql = "Select ProductID, Quantity from Carts where CustomerID = ? AND ProductID = ?";
            }
            PreparedStatement pre = connector.prepareStatement(sql);
            pre.setInt(1, customerID);
            pre.setInt(2, productID);
            if (hasCartVariantColumn() && variantId != null && variantId > 0) {
                pre.setInt(3, variantId);
            }
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                c = new Cart(rs.getInt("ProductID"), rs.getInt("Quantity"));
                if (hasCartVariantColumn() && rs.getObject("ProductVariantID") != null) {
                    c.setVariantId(rs.getInt("ProductVariantID"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e + "");
        }
        return c;
    }

    public void updateProductQuantity(int productID, int quantity, int id) {
        updateProductQuantity(productID, null, quantity, id);
    }

    public void updateProductQuantity(int productID, Integer variantId, int quantity, int id) {
        boolean hasVariantColumn = hasCartVariantColumn();
        String sql;
        if (!hasVariantColumn) {
            sql = "UPDATE Carts SET Quantity = ? WHERE ProductID = ? AND CustomerID = ?";
        } else if (variantId == null || variantId <= 0) {
            sql = "UPDATE Carts SET Quantity = ?, ProductVariantID = NULL WHERE ProductID = ? AND CustomerID = ?";
        } else {
            sql = "UPDATE Carts SET Quantity = ?, ProductVariantID = ? WHERE ProductID = ? AND CustomerID = ?";
        }
        try {
            PreparedStatement preparedStatement = connector.prepareStatement(sql);
            preparedStatement.setInt(1, quantity);
            if (!hasVariantColumn || variantId == null || variantId <= 0) {
                preparedStatement.setInt(2, productID);
                preparedStatement.setInt(3, id);
            } else {
                preparedStatement.setInt(2, variantId);
                preparedStatement.setInt(3, productID);
                preparedStatement.setInt(4, id);
            }
            preparedStatement.executeUpdate();
            System.out.println("Update Ok");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteProductOnCart(int productID, int id) {
        try {
            PreparedStatement preparedStatement = connector.prepareStatement("Delete from Carts where ProductID = ? and CustomerID = ?");
            preparedStatement.setInt(1, productID);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCartOfCustomer(int id) {
        try {
            PreparedStatement preparedStatement = connector.prepareStatement("Delete from Carts WHERE CustomerID = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CartDAO c = new CartDAO();
        System.out.println(c.getProductOfCart(1, 2).getQuantity());
    }
}
