/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SupplierDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    public ArrayList<Supplier> getAllSuppliers() {
        ArrayList<Supplier> list = new ArrayList<>();

        String query = "SELECT * FROM Suppliers WHERE IsDeleted = 0 ORDER BY IsActivate DESC";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("TaxID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getTimestamp("CreatedDate").toLocalDateTime(),
                        rs.getTimestamp("LastModify").toLocalDateTime(),
                        rs.getInt("IsDeleted"),
                        rs.getInt("IsActivate")
                ));
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return list;
    }

    public ArrayList<Supplier> getAllActivatedSuppliers() {
        ArrayList<Supplier> list = new ArrayList<>();

        String query = "SELECT * FROM Suppliers WHERE IsDeleted = 0 AND IsActivate = 1";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("TaxID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getTimestamp("CreatedDate").toLocalDateTime(),
                        rs.getTimestamp("LastModify").toLocalDateTime(),
                        rs.getInt("IsDeleted"),
                        rs.getInt("IsActivate")
                ));
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return list;
    }

    public Supplier getSupplierByID(int supplierId) {
        Supplier s = null;

        String query = "SELECT * FROM Suppliers WHERE SupplierId = ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                s = new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("TaxID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getTimestamp("CreatedDate").toLocalDateTime(),
                        rs.getTimestamp("LastModify").toLocalDateTime(),
                        rs.getInt("IsDeleted"),
                        rs.getInt("IsActivate")
                );
            }
            return s;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return s;
    }

    public Supplier getSupplierByTaxID(String supplierTaxId) {
        Supplier s = null;

        String query = "SELECT * FROM Suppliers WHERE TaxID = ?";

        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setString(1, supplierTaxId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                s = new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("TaxID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("Address"),
                        rs.getTimestamp("CreatedDate").toLocalDateTime(),
                        rs.getTimestamp("LastModify").toLocalDateTime(),
                        rs.getInt("IsDeleted"),
                        rs.getInt("IsActivate")
                );
            }
            return s;
        } catch (SQLException e) {
            System.out.println(e);
        }

        return s;
    }

    public int createSupplier(Supplier s) {

        String query = "INSERT INTO Suppliers (TaxID, [Name], Email, PhoneNumber, Address, CreatedDate, LastModify, IsDeleted, IsActivate) VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE(), 0, ?)";
        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setString(1, s.getTaxId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhoneNumber());
            ps.setString(5, s.getAddress());
            ps.setInt(6, s.getActivate());

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public int updateSupplier(Supplier s) {

        String query = "UPDATE Suppliers SET TaxID = ?, [Name] = ?, Email = ?, PhoneNumber = ?, Address = ?, LastModify = GETDATE(), IsDeleted = ?, IsActivate = ? WHERE SupplierID = ?";
        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setString(1, s.getTaxId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhoneNumber());
            ps.setString(5, s.getAddress());
            ps.setInt(6, 0);
            ps.setInt(7, s.getActivate());
            ps.setInt(8, s.getSupplierId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public int deleteSupplier(int supplierId) {

        String query = "UPDATE Suppliers SET IsDeleted = 1 WHERE SupplierID = ?";
        try {
            PreparedStatement ps = connector.prepareStatement(query);
            ps.setInt(1, supplierId);

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }

        return 0;
    }

    public List<Supplier> searchSupplierByName(String name) {
        List<Supplier> list = new ArrayList<>();
        String query = "SELECT * FROM Suppliers WHERE Name LIKE ?";

        try ( PreparedStatement ps = connector.prepareStatement(query)) {
            ps.setString(1, "%" + name + "%");

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Supplier(
                            rs.getInt("SupplierID"),
                            rs.getString("TaxID"),
                            rs.getString("Name"),
                            rs.getString("Email"),
                            rs.getString("PhoneNumber"),
                            rs.getString("Address"),
                            rs.getTimestamp("CreatedDate").toLocalDateTime(),
                            rs.getTimestamp("LastModify").toLocalDateTime(),
                            rs.getInt("IsDeleted"),
                            rs.getInt("IsActivate")
                    ));
                }
                return list;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
}
