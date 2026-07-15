/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.Address;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TranVTH
 */

public class AddressDAO {

    DBContext db = new DBContext();
    Connection connector = db.getConnection();

    /**
     *
     * @param customerID
     * @return
     */
    public Address getDefaultAddress(int customerID) {
        try {
            PreparedStatement pr = connector.prepareStatement("SELECT AddressID, AddressDetails FROM Addresses Where CustomerID = ? AND IsDefault = 1");
            pr.setInt(1, customerID);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                return new Address(rs.getInt(1), rs.getString(2));
            }
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
        return null;
    }

    public Address getAddressByID(int id) {
        try {
            PreparedStatement pr = connector.prepareStatement("SELECT * FROM Addresses Where AddressID = ?");
            pr.setInt(1, id);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                return new Address(rs.getInt(1), rs.getInt(2), rs.getInt(4), rs.getString(3));
            }
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
        return null;
    }

    public List<Address> getAddress(int customerID) {
        List<Address> list = new ArrayList<>();
        try {
            PreparedStatement pr = connector.prepareStatement("SELECT * FROM Addresses Where CustomerID = ? ORDER BY IsDefault DESC");
            pr.setInt(1, customerID);
            ResultSet rs = pr.executeQuery();
            while (rs.next()) {
                list.add(new Address(rs.getInt(1), rs.getInt(1), rs.getInt(4), rs.getString(3)));
            }
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
        return list;
    }

    public void updateAddress(Address a) {
        try {
            PreparedStatement pr = connector.prepareStatement("UPDATE Addresses SET AddressDetails = '" + a.getAddressDetails() + "', IsDefault = ? WHERE AddressID = ?");
            pr.setInt(1, a.getIsDefault());
            pr.setInt(2, a.getAddressID());
            pr.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
    }

    public int addAddress(Address a) {
        int id = 0;
        try {
            PreparedStatement pr = connector.prepareStatement("INSERT INTO Addresses VALUES (?, '" + a.getAddressDetails() + "', ?)");
            pr.setInt(1, a.getCustomerID());
            pr.setInt(2, a.getIsDefault());
            pr.executeUpdate();
            pr = connector.prepareStatement("SELECT TOP 1* FROM Addresses Order by AddressID DESC");
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
        return id;
    }

    public void disableDefaultAddress(int addressID, int customerID) {
        try {
            PreparedStatement pr = connector.prepareStatement("UPDATE Addresses SET IsDefault = 0 WHERE AddressID != ? AND CustomerID = ?");
            pr.setInt(1, addressID);
            pr.setInt(2, customerID);
            pr.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
    }

    public void deleteAddress(int addressID) {
        try {
            PreparedStatement pr = connector.prepareStatement("DELETE FROM Addresses WHERE AddressID = ?");
            pr.setInt(1, addressID);
            pr.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
    }

    public void setAsDefault(int addressID) {
        try {
            PreparedStatement pr = connector.prepareStatement("UPDATE Addresses SET IsDefault = 1 WHERE AddressID = ?");
            pr.setInt(1, addressID);
            pr.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e + " ");
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        AddressDAO a = new AddressDAO();
        a.addAddress(new Address(1, 0, "AP Tra coi, My Huong Commune, My Tu District, Soc Trang Province"));
    }

}
