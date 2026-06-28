package DAOs;

import DB.DBContext;
import Models.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class CustomerDAO {

    public Customer getCustomerById(int id) {
        String sql = "SELECT CustomerID, FullName, Password, Birthday, Gender, PhoneNumber, "
                + "Email, CreatedDate, GoogleID, IsBlock, IsDeleted, Avatar "
                + "FROM Customers WHERE CustomerID = ?";
        try (Connection connection = new DBContext().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("CustomerID"),
                            rs.getString("FullName"),
                            rs.getString("Password"),
                            rs.getString("Birthday"),
                            rs.getString("Gender"),
                            rs.getString("PhoneNumber"),
                            rs.getString("Email"),
                            rs.getString("CreatedDate"),
                            rs.getString("GoogleID"),
                            rs.getInt("IsBlock"),
                            rs.getInt("IsDeleted"),
                            rs.getString("Avatar")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
}
