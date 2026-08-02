/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.CustomerNotification;
import Models.RatingReplies;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RatingRepliesDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    public List<RatingReplies> getAllRatingRepliesByProduct(int productId) {
        List<RatingReplies> list = new ArrayList<>();
        String query = "SELECT rr.* FROM RatingReplies rr JOIN ProductRatings pr ON rr.RateID = pr.RateID "
                + "WHERE pr.ProductID = ? AND pr.IsDeleted = 0";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, productId);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                RatingReplies rr = new RatingReplies(
                        rs.getInt("ReplyID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("RateID"),
                        rs.getString("Answer"),
                        rs.getBoolean("IsRead")
                );
                list.add(rr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
 public List<RatingReplies> getAllRatingRepliesByRateID(int rateId) {
        List<RatingReplies> list = new ArrayList<>();
        String query = "select * from RatingReplies where RateID =?";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, rateId);
            ResultSet rs = pre.executeQuery();
            while (rs.next()) {
                RatingReplies rr = new RatingReplies(
                        rs.getInt("ReplyID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("RateID"),
                        rs.getString("Answer"),
                        rs.getBoolean("IsRead")
                );
                list.add(rr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public int addRatingReply(int employeeId, int rateId, String answer) {
        int count = 0;
        String query = "INSERT INTO RatingReplies (EmployeeID, RateID, Answer, IsRead) VALUES (?, ?, ?, 0)";
        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, employeeId);
            pre.setInt(2, rateId);
            pre.setString(3, answer);
       count =  pre.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<RatingReplies> getCustomerReplies(int customerID) {
        List<RatingReplies> list = new ArrayList<>();
//        String query = "SELECT r.* FROM RatingReplies r JOIN ProductRatings pr ON r.rateID = pr.rateID WHERE pr.customerID = ? AND r.isRead = 0";
        String query = "SELECT rr.* FROM RatingReplies rr \n"
                + "JOIN ProductRatings pr ON rr.RateID = pr.RateID\n"
                + "WHERE pr.CustomerID =? ORDER BY pr.CreatedDate DESC";
        try (
                 PreparedStatement stmt = connector.prepareStatement(query)) {
            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new RatingReplies(rs.getInt("ReplyID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("RateID"),
                        rs.getString("Answer"),
                        rs.getBoolean("IsRead")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<CustomerNotification> getCustomerNotifications(int customerId) {
        List<CustomerNotification> notifications = new ArrayList<>();
        String sql = "SELECT rr.ReplyID, rr.Answer, rr.IsRead, "
                + "pr.ProductID, pr.Comment, pr.CreatedDate, p.FullName "
                + "FROM RatingReplies rr "
                + "INNER JOIN ProductRatings pr ON rr.RateID = pr.RateID "
                + "LEFT JOIN Products p ON pr.ProductID = p.ProductID "
                + "WHERE pr.CustomerID = ? "
                + "ORDER BY rr.IsRead ASC, pr.CreatedDate DESC, rr.ReplyID DESC";

        try (PreparedStatement statement = connector.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new CustomerNotification(
                            rs.getInt("ReplyID"),
                            rs.getInt("ProductID"),
                            rs.getString("FullName"),
                            rs.getString("Comment"),
                            rs.getString("Answer"),
                            rs.getTimestamp("CreatedDate"),
                            rs.getBoolean("IsRead")
                    ));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return notifications;
    }
  public RatingReplies getReplyByRepyID(int replyID) {
        List<RatingReplies> list = new ArrayList<>();
        RatingReplies r = null ;
//        String query = "SELECT r.* FROM RatingReplies r JOIN ProductRatings pr ON r.rateID = pr.rateID WHERE pr.customerID = ? AND r.isRead = 0";
        String query = "SELECT rr.* FROM RatingReplies rr WHERE rr.ReplyID =?";
        try (
                 PreparedStatement stmt = connector.prepareStatement(query)) {
            stmt.setInt(1, replyID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
         r=   new RatingReplies(rs.getInt("ReplyID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("RateID"),
                        rs.getString("Answer"),
                        rs.getBoolean("IsRead"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r ;
    }
    public int UpdateReply(RatingReplies reply, String Answer){
        if (reply == null) {
            return 0;
        }
    String query = "UPDATE RatingReplies SET Answer = ? WHERE ReplyID = ?";

        try (
                 PreparedStatement stmt = connector.prepareStatement(query)) {

            stmt.setInt(2, reply.getReplyID());
            stmt.setString(1, Answer);
            return stmt.executeUpdate() ;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public boolean markReplyAsRead(int ReplyID) {
        String query = "UPDATE RatingReplies SET IsRead = 1 WHERE ReplyID = ?";

        try (
                 PreparedStatement stmt = connector.prepareStatement(query)) {

            stmt.setInt(1, ReplyID);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean markReplyAsRead(int replyId, int customerId) {
        String sql = "UPDATE rr SET rr.IsRead = 1 "
                + "FROM RatingReplies rr "
                + "INNER JOIN ProductRatings pr ON rr.RateID = pr.RateID "
                + "WHERE rr.ReplyID = ? AND pr.CustomerID = ?";

        try (PreparedStatement statement = connector.prepareStatement(sql)) {
            statement.setInt(1, replyId);
            statement.setInt(2, customerId);
            return statement.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
  
     public boolean DeleteRatingReply(int ReplyID) {
        String query = "DELETE FROM RatingReplies WHERE ReplyID = ?";

        try (
                 PreparedStatement stmt = connector.prepareStatement(query)) {

            stmt.setInt(1, ReplyID);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
     public static void main(String[] args) {
        RatingRepliesDAO r = new RatingRepliesDAO();
         System.out.println("RATING REPLY_______________________");
         System.out.println(r.getReplyByRepyID(1).getAnswer());
    }
}
