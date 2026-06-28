/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.VoucherDAO;
import Models.Voucher;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class ViewVoucherListServlet extends HttpServlet {

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
        
       
            
            VoucherDAO vDAO = new VoucherDAO();
            String searchCode = request.getParameter("code");
            Integer status = parseInteger(request.getParameter("status"));
            Integer type = parseInteger(request.getParameter("type"));
            String sort = request.getParameter("sort");
            int pageSize = 10;
            int page = Math.max(1, parseInteger(request.getParameter("page"), 1));
            int totalVouchers = vDAO.countVouchers(searchCode, status, type);
            int totalPages = Math.max(1, (int) Math.ceil(totalVouchers / (double) pageSize));
            if (page > totalPages) {
                page = totalPages;
            }
            List<Voucher> ListVoucher = vDAO.searchVouchers(searchCode, status, type, sort, page, pageSize);

            request.setAttribute("Vouchers", ListVoucher);
            request.setAttribute("searchCode", searchCode);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("selectedType", type);
            request.setAttribute("selectedSort", sort);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalVouchers", totalVouchers);
            request.getRequestDispatcher("VoucherListView.jsp").forward(request, response);
            
     

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
