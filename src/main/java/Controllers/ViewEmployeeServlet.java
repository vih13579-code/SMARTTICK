/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.EmployeeDAO;
import DAOs.RoleDAO;
import Models.Employee;
import Models.Role;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class ViewEmployeeServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EmployeeDAO empDAO = new EmployeeDAO();
        RoleDAO roleDAO = new RoleDAO();
        ArrayList<Employee> listE;
        ArrayList<Role> listR1;
        String empId = request.getParameter("id");
        listR1 = roleDAO.getAllRoles();
        if (empId != null) {
            Employee employee = empDAO.getEmployeeById(empId);
            try {
                request.setAttribute("employee", employee);
                request.setAttribute("listR1", listR1);
                request.getRequestDispatcher("EmployeeDetailView.jsp").forward(request, response);
                return;
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
        ArrayList<Role> listR2;
        listE = empDAO.getAllEmployees();
        listR2 = roleDAO.getAllRoles();
        try {
            request.setAttribute("listE", listE);
            request.setAttribute("listR", listR2);
            request.getRequestDispatcher("EmployeeListView.jsp").forward(request, response);
        } catch (NullPointerException e) {
            System.out.println(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
