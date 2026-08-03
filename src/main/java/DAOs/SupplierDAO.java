package DAOs;

import DB.DBContext;
import Models.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thuongnvce181966
 */
public class SupplierDAO {

    private static final Logger LOGGER = Logger.getLogger(SupplierDAO.class.getName());
    private final DBContext db = new DBContext();

    public ArrayList<Supplier> getAllSuppliers() {
        ArrayList<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers "
                + "WHERE IsDeleted = 0 "
                + "ORDER BY IsActivate DESC, Name ASC";
        try ( Connection connection = db.getConnection();  PreparedStatement statement = connection.prepareStatement(sql);  ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                suppliers.add(mapSupplier(result));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load suppliers.", ex);
        }
        return suppliers;
    }

    public ArrayList<Supplier> getAllActivatedSuppliers() {
        ArrayList<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers "
                + "WHERE IsDeleted = 0 AND IsActivate = 1 "
                + "ORDER BY Name ASC";
        try ( Connection connection = db.getConnection();  PreparedStatement statement = connection.prepareStatement(sql);  ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                suppliers.add(mapSupplier(result));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load active suppliers.", ex);
        }
        return suppliers;
    }

    public Supplier getSupplierByID(int supplierId) {
        String sql = "SELECT * FROM Suppliers "
                + "WHERE SupplierID = ? AND IsDeleted = 0";
        try ( Connection connection = db.getConnection();  PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, supplierId);
            try ( ResultSet result = statement.executeQuery()) {
                return result.next() ? mapSupplier(result) : null;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load supplier " + supplierId + ".", ex);
            return null;
        }
    }

    public Supplier getSupplierByTaxID(String supplierTaxId) {
        String sql = "SELECT * FROM Suppliers "
                + "WHERE TaxID = ? AND IsDeleted = 0";
        try ( Connection connection = db.getConnection();  PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplierTaxId);
            try ( ResultSet result = statement.executeQuery()) {
                return result.next() ? mapSupplier(result) : null;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load supplier by tax ID.", ex);
            return null;
        }
    }

    public boolean taxIdExists(String taxId, int excludedSupplierId) {
        String sql = "SELECT 1 FROM Suppliers "
                + "WHERE TaxID = ? AND SupplierID <> ?";
        try ( Connection connection = db.getConnection();  PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, taxId);
            statement.setInt(2, excludedSupplierId);
            try ( ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot validate supplier tax ID.", ex);
            return true;
        }
    }

    public int createSupplier(Supplier supplier) {
        String sql = "INSERT INTO Suppliers "
                + "(TaxID, [Name], Email, PhoneNumber, Address, CreatedDate, "
                + "LastModify, IsDeleted, IsActivate) "
                + "VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE(), 0, ?)";
        try ( Connection connection = db.getConnection();  PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplier.getTaxId());
            statement.setString(2, supplier.getName());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getPhoneNumber());
            statement.setString(5, supplier.getAddress());
            statement.setInt(6, supplier.getActivate());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Cannot create supplier.", ex);
            return 0;
        }
    }

    public int updateSupplier(Supplier supplier) {
        String sql = "UPDATE Suppliers SET TaxID = ?, [Name] = ?, Email = ?, "
                + "PhoneNumber = ?, Address = ?, LastModify = GETDATE(), "
                + "IsActivate = ? "
                + "WHERE SupplierID = ? AND IsDeleted = 0";
        try ( Connection connection = db.getConnection();  PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, supplier.getTaxId());
            statement.setString(2, supplier.getName());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getPhoneNumber());
            statement.setString(5, supplier.getAddress());
            statement.setInt(6, supplier.getActivate());
            statement.setInt(7, supplier.getSupplierId());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Cannot update supplier " + supplier.getSupplierId() + ".", ex);
            return 0;
        }
    }

    public int deleteSupplier(int supplierId) {
        String sql = "UPDATE Suppliers SET IsDeleted = 1, IsActivate = 0, "
                + "LastModify = GETDATE() "
                + "WHERE SupplierID = ? AND IsDeleted = 0";
        try ( Connection connection = db.getConnection();  PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, supplierId);
            return statement.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Cannot delete supplier " + supplierId + ".", ex);
            return 0;
        }
    }

    public List<Supplier> searchSupplierByName(String keyword) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers "
                + "WHERE IsDeleted = 0 "
                + "AND ([Name] LIKE ? OR TaxID LIKE ? OR Email LIKE ?) "
                + "ORDER BY IsActivate DESC, Name ASC";
        String searchTerm = "%" + keyword + "%";
        try ( Connection connection = db.getConnection();  PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, searchTerm);
            statement.setString(2, searchTerm);
            statement.setString(3, searchTerm);
            try ( ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    suppliers.add(mapSupplier(result));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot search suppliers.", ex);
        }
        return suppliers;
    }

    private Supplier mapSupplier(ResultSet result) throws SQLException {
        return new Supplier(
                result.getInt("SupplierID"),
                result.getString("TaxID"),
                result.getString("Name"),
                result.getString("Email"),
                result.getString("PhoneNumber"),
                result.getString("Address"),
                toLocalDateTime(result.getTimestamp("CreatedDate")),
                toLocalDateTime(result.getTimestamp("LastModify")),
                result.getInt("IsDeleted"),
                result.getInt("IsActivate")
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
