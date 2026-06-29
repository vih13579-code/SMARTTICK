/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.Product;
import Models.ProductRating;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TrucBQCE181355
 */
public class ProductRatingDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    public List<ProductRating> getAllProductRating(int productID) {
        List<ProductRating> list = new ArrayList<>();
        String query = "SELECT P.* ,C.FullName FROM ProductRatings AS P\n"
                + "JOIN Customers AS C ON C.CustomerID = P.CustomerID \n"
                + "WHERE ProductID = ?  ORDER BY P.CreatedDate DESC";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, productID);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                ProductRating p = new ProductRating(
                        rs.getInt("RateID"),
                        rs.getInt("CustomerID"),
                        rs.getInt("ProductID"),
                        rs.getInt("OrderID"),
                        rs.getDate("CreatedDate"),
                        rs.getInt("Star"),
                        rs.getString("Comment"),
                        rs.getBoolean("isDeleted"),
                        rs.getBoolean("isRead"),
                        rs.getString("FullName")
                );
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public float getStarAverage(int productId) {
        float star = 0;
        String query = "SELECT COALESCE(ROUND(SUM(Star) * 1.0 / COUNT(Star), 0), 0) AS avs\n"
                + "FROM ProductRatings as p  \n"
                + "WHERE p.ProductID = ?";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, productId);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                star = rs.getFloat("avs");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return star;
    }

    public ProductRating getStarAVG(int productId) {
        int star = 0;
        ProductRating p = new ProductRating();
        String query = "SELECT COALESCE(ROUND(SUM(Star) * 1.0 / COUNT(Star), 0), 0) AS avs\n"
                + "FROM ProductRatings as p  \n"
                + "WHERE p.ProductID = ?";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, productId);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                star = rs.getInt("avs");
                p.setStar(star);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return p;
    }

    public ProductRating getProductRating(int rateID) {
        ProductRating pro = new ProductRating();
        String query = "select * from ProductRatings WHERE RateID =?";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, rateID);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {

                pro.setRateID(rs.getInt("RateID"));
                pro.setCustomerID(rs.getInt("CustomerID"));
                pro.setProductID(rs.getInt("ProductID"));
                pro.setOrderID(rs.getInt("OrderID"));
                pro.setCreatedDate(rs.getDate("CreatedDate"));
                pro.setStar(rs.getInt("Star"));
                pro.setComment(rs.getString("Comment"));
                pro.setIsDeleted(rs.getBoolean("isDeleted"));
                pro.setIsRead(rs.getBoolean("isRead"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(pro.getProductID());
        return pro;
    }

    public List<ProductRating> getNewFeedback() {
        List<ProductRating> list = new ArrayList<>();
        String query = "SELECT P.* ,C.FullName FROM ProductRatings AS P JOIN Customers AS C ON C.CustomerID = P.CustomerID  ORDER BY P.IsRead ASC";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                ProductRating p = new ProductRating(
                        rs.getInt("RateID"),
                        rs.getInt("CustomerID"),
                        rs.getInt("ProductID"),
                        rs.getInt("OrderID"),
                        rs.getDate("CreatedDate"),
                        rs.getInt("Star"),
                        rs.getString("Comment"),
                        rs.getBoolean("isDeleted"),
                        rs.getBoolean("isRead"),
                        rs.getString("FullName")
                );
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatusComment(int rateID, int status) {
        boolean isOk = false;
        String query = "Update ProductRatings SET IsDeleted = ? WHERE RateID =?";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, status);
            pre.setInt(2, rateID);
            pre.executeUpdate();
            isOk = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOk;
    }

    public void updateisReadComment(int rateID) {
        String query = "Update ProductRatings SET IsRead = 1  WHERE RateID =?";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, rateID);
            pre.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int addProductRating(int customerId, int productId, int star, String comment) {
        int count = 0;
        String query = "INSERT INTO ProductRatings (CustomerID, ProductID, CreatedDate, Star, Comment, isDeleted, isRead) VALUES (?, ?, GETDATE(), ?, ?, 0, 0)";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, customerId);
            pre.setInt(2, productId);
            pre.setInt(3, star);
            pre.setString(4, comment);
           count = pre.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public Product getProductID(int rateID) {
        Product p = new Product();
        String query = "select FullName , ProductID from Products where ProductID =(select ProductID from ProductRatings WHERE RateID =?)";

        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, rateID);
            ResultSet rs = pre.executeQuery();
            if (rs.next()) {
                p.setFullName(rs.getString("FullName"));
                p.setProductId(rs.getInt("ProductID"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return p;
    }
       public boolean markReplyAsUnRead(int ReplyID) {
        String query = "UPDATE ProductRatings SET IsRead= 0 WHERE RateID = ?";

        try (
                 PreparedStatement stmt = connector.prepareStatement(query)) {

            stmt.setInt(1, ReplyID);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}
