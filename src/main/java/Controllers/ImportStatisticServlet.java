/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.ImportOrderDAO;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Thuongnvce181966
 */

public class ImportStatisticServlet extends HttpServlet {

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
//        processRequest(request, response);
        ImportOrderDAO statsDAO = new ImportOrderDAO();

        try {
            String orderCountByDayJson = new Gson().toJson(statsDAO.getImportOrdersCountByDate());
            request.setAttribute("orderCountByDayJson", orderCountByDayJson);

            String orderCountByMonthJson = new Gson().toJson(statsDAO.getImportOrdersCountByMonth());
            request.setAttribute("orderCountByMonthJson", orderCountByMonthJson);

            String ordersBySupplierJson = new Gson().toJson(statsDAO.getOrdersBySupplier());
            request.setAttribute("ordersBySupplierJson", ordersBySupplierJson);

            String topImportedProductsJson = new Gson().toJson(statsDAO.getTopImportedProducts());
            request.setAttribute("topImportedProductsJson", topImportedProductsJson);

            request.getRequestDispatcher("ImportStatisticView.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to load statistics data");
        }
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
        processRequest(request, response);
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
