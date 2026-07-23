/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.Cart;
import Models.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    public List<Cart> getCartOfAccountID(int accountID) {
        List<Cart> list = new ArrayList<>();
        try {
            PreparedStatement pre = connector.prepareStatement("SELECT c.ProductID, c.Quantity, p.[Image], p.FullName, p.Price, p.CategoryID, p.Stock\n"
                    + "FROM Carts c\n"
                    + "LEFT JOIN Products p ON c.ProductID = p.ProductID\n"
                    + "WHERE c.CustomerID = ? AND p.IsDeleted = 0 ORDER BY CustomerID DESC");
            pre.setInt(1, accountID);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                list.add(new Cart(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                        rs.getLong(5), rs.getInt(6), rs.getInt(7)));
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
            PreparedStatement pre = connector.prepareStatement("INSERT INTO Carts VALUES (?, ?, ?)");
            pre.setInt(1, customerID);
            pre.setInt(2, c.getProductID());
            pre.setInt(3, c.getQuantity());
            pre.executeUpdate();
        } catch (SQLException e) {

        }
    }

    public Cart getProductOfCart(int customerID, int productID) {
        Cart c = null;
        try {
            PreparedStatement pre = connector.prepareStatement("Select ProductID, Quantity from Carts where CustomerID = ? AND ProductID = ?");
            pre.setInt(1, customerID);
            pre.setInt(2, productID);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                c = new Cart(rs.getInt(1), rs.getInt(2));
            }
        } catch (SQLException e) {

        }
        return c;
    }

    public void updateProductQuantity(int productID, int quantity, int id) {
        String sql = "UPDATE Carts SET Quantity = ? WHERE ProductID = ? AND CustomerID = ?";
        try {
            PreparedStatement preparedStatement = connector.prepareStatement(sql);
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, productID);
            preparedStatement.setInt(3, id);
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
            PreparedStatement preparedStatement = connector.prepareStatement("Delete from Carts CustomerID = ?");
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
