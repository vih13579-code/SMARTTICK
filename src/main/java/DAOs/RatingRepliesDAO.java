/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.RatingReplies;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) responsible for managing rating replies.
 * Provides methods to retrieve, insert, update, delete,
 * and manage reply records in the database.
 *
 * @author TrucBQCE181355
 */
public class RatingRepliesDAO {

    /** Database context object. */
    DBContext db = new DBContext();

    /** Database connection object. */
    Connection connector = db.getConnection();

    /**
     * Retrieves all replies associated with a specific product.
     *
     * @param productId product ID
     * @return list of rating replies
     */
    public List<RatingReplies> getAllRatingRepliesByProduct(int productId) {

        // Create list to store replies
        List<RatingReplies> list = new ArrayList<>();

        // SQL query to retrieve replies by product ID
        String query = "SELECT rr.* FROM RatingReplies rr JOIN ProductRatings pr ON rr.RateID = pr.RateID WHERE pr.ProductID = ?";

        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, productId);
            ResultSet rs = pre.executeQuery();

            // Convert ResultSet into RatingReplies objects
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

    /**
     * Retrieves all replies for a specific rating.
     *
     * @param rateId rating ID
     * @return list of rating replies
     */
    public List<RatingReplies> getAllRatingRepliesByRateID(int rateId) {

        // Create list to store replies
        List<RatingReplies> list = new ArrayList<>();

        // SQL query to retrieve replies by rating ID
        String query = "SELECT * FROM RatingReplies WHERE RateID = ?";

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

    /**
     * Inserts a new reply into the database.
     *
     * @param employeeId employee ID
     * @param rateId rating ID
     * @param answer reply content
     * @return number of affected rows
     */
    public int addRatingReply(int employeeId, int rateId, String answer) {

        int count = 0;

        // SQL query for inserting a reply
        String query = "INSERT INTO RatingReplies (EmployeeID, RateID, Answer, IsRead) VALUES (?, ?, ?, 0)";

        try {
            PreparedStatement pre = connector.prepareStatement(query);
            pre.setInt(1, employeeId);
            pre.setInt(2, rateId);
            pre.setString(3, answer);

            count = pre.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Retrieves all replies belonging to a customer's ratings.
     *
     * @param customerID customer ID
     * @return list of replies
     */
    public List<RatingReplies> getCustomerReplies(int customerID) {

        // Create list to store replies
        List<RatingReplies> list = new ArrayList<>();

        String query = "SELECT rr.* FROM RatingReplies rr "
                + "JOIN ProductRatings pr ON rr.RateID = pr.RateID "
                + "WHERE pr.CustomerID = ? ORDER BY pr.CreatedDate DESC";

        try (PreparedStatement stmt = connector.prepareStatement(query)) {

            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new RatingReplies(
                        rs.getInt("ReplyID"),
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

    /**
     * Retrieves a reply by its ID.
     *
     * @param replyID reply ID
     * @return RatingReplies object if found; otherwise null
     */
    public RatingReplies getReplyByRepyID(int replyID) {

        RatingReplies r = null;

        String query = "SELECT * FROM RatingReplies WHERE ReplyID = ?";

        try (PreparedStatement stmt = connector.prepareStatement(query)) {

            stmt.setInt(1, replyID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                r = new RatingReplies(
                        rs.getInt("ReplyID"),
                        rs.getInt("EmployeeID"),
                        rs.getInt("RateID"),
                        rs.getString("Answer"),
                        rs.getBoolean("IsRead"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return r;
    }

    /**
     * Updates an existing reply.
     *
     * @param reply reply object
     * @param Answer updated reply content
     * @return number of affected rows
     */
    public int UpdateReply(RatingReplies reply, String Answer) {

        String query = "UPDATE RatingReplies SET Answer = ? WHERE ReplyID = ?";

        try (PreparedStatement stmt = connector.prepareStatement(query)) {

            stmt.setString(1, Answer);
            stmt.setInt(2, reply.getReplyID());

            return stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Marks a reply as read.
     *
     * @param ReplyID reply ID
     * @return true if successful; otherwise false
     */
    public boolean markReplyAsRead(int ReplyID) {

        String query = "UPDATE RatingReplies SET IsRead = 1 WHERE ReplyID = ?";

        try (PreparedStatement stmt = connector.prepareStatement(query)) {

            stmt.setInt(1, ReplyID);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Deletes a reply from the database.
     *
     * @param ReplyID reply ID
     * @return true if successful; otherwise false
     */
    public boolean DeleteRatingReply(int ReplyID) {

        String query = "DELETE FROM RatingReplies WHERE ReplyID = ?";

        try (PreparedStatement stmt = connector.prepareStatement(query)) {

            stmt.setInt(1, ReplyID);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Tests DAO methods.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        // Create DAO object
        RatingRepliesDAO r = new RatingRepliesDAO();

        // Display test result
        System.out.println("RATING REPLY_______________________");
        System.out.println(r.getReplyByRepyID(1).getAnswer());
    }
}