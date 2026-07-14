/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.EmployeeDAO;
import DAOs.ImportOrderDAO;
import DAOs.SupplierDAO;
import Models.Employee;
import Models.ImportOrder;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 *
 * @author Thuongnvce181966
 */

public class ViewImportOrderServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        ImportOrderDAO importOrderDAO = new ImportOrderDAO();
        SupplierDAO supplierDAO = new SupplierDAO();
        String id = request.getParameter("id");

        if (id != null && !id.trim().isEmpty()) {
            try {
                int importId = Integer.parseInt(id.trim());
                ImportOrder existingOrder = importOrderDAO.getImportOrderByID(importId);
                if (existingOrder == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                ImportOrder importOrder = importOrderDAO.getImportOrderDetailsByID(importId);
                Employee employee = new EmployeeDAO().getEmployeeById(String.valueOf(importOrder.getEmployeeId()));
                request.setAttribute("importOrder", importOrder);
                request.setAttribute("employee", employee);
                request.getRequestDispatcher("ImportOrderDetailsView.jsp").forward(request, response);
                return;
            } catch (NumberFormatException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String supplierName = request.getParameter("name");

        ArrayList<ImportOrder> importOrders;
        if (fromDate != null && !fromDate.trim().isEmpty()
                && toDate != null && !toDate.trim().isEmpty()) {
            importOrders = importOrderDAO.filterHistoryByDate(fromDate.trim(), toDate.trim());
        } else if (supplierName != null && !supplierName.trim().isEmpty()) {
            importOrders = importOrderDAO.getImportOrderBySupplierName(supplierName.trim());
            request.setAttribute("searchValue", supplierName.trim());
        } else {
            importOrders = importOrderDAO.getAllImportOrders();
        }

        request.setAttribute("importOrders", importOrders);
        request.setAttribute("suppliers", supplierDAO.getAllActivatedSuppliers());
        request.getRequestDispatcher("ImportOrderListView.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns the servlet description.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }// </editor-fold>

}
