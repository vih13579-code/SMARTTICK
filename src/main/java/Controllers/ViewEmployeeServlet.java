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
        String query = request.getParameter("query");
        String sort = request.getParameter("sort");
        Integer roleId = parseInteger(request.getParameter("roleId"));
        Integer status = parseInteger(request.getParameter("status"));
        int pageSize = 10;
        int page = Math.max(1, parseInteger(request.getParameter("page"), 1));
        int totalEmployees = empDAO.countEmployees(query, roleId, status);
        int totalPages = Math.max(1, (int) Math.ceil(totalEmployees / (double) pageSize));
        if (page > totalPages) {
            page = totalPages;
        }

        ArrayList<Employee> listE = empDAO.searchEmployees(query, roleId, status, sort, page, pageSize);
        ArrayList<Role> listR2 = roleDAO.getAllRoles();
        try {
            request.setAttribute("listE", listE);
            request.setAttribute("listR", listR2);
            request.setAttribute("searchQuery", query);
            request.setAttribute("selectedRoleId", roleId);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("selectedSort", sort);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalEmployees", totalEmployees);
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

    private Integer parseInteger(String value) {
        return parseInteger(value, null);
    }

    private Integer parseInteger(String value, Integer fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

}
