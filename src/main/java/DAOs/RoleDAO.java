/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAOs;

import DB.DBContext;
import Models.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class RoleDAO {
    DBContext db = new DBContext();
    Connection connector = db.getConnection();
    
    public ArrayList<Role> getAllRoles(){
        ArrayList<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM Roles WHERE RoleID <> 1";
        try {
            PreparedStatement pr =connector.prepareStatement(sql);
            ResultSet rs = pr.executeQuery();
            while(rs.next()){
                list.add(new Role(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }
    public static void main(String[] args) {
        RoleDAO roleDAO = new RoleDAO();
        ArrayList<Role> list = roleDAO.getAllRoles();
        for (Role role : list) {
            System.out.println(role.getRoleName());
        }
    }
}
