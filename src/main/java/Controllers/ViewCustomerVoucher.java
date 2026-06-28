/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.CustomerVoucherDAO;
import Models.Customer;
import Models.CustomerVoucher;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
@WebServlet(name = "ViewCustomerVoucher", urlPatterns = {"/ViewCustomerVoucher"})
public class ViewCustomerVoucher extends HttpServlet {

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
        HttpSession session = request.getSession();
        Customer cus = (Customer) session.getAttribute("customer");
        if (cus == null) {
            response.sendRedirect("CustomerLoginView.jsp");
            return;
        }
        CustomerVoucherDAO c = new CustomerVoucherDAO();
        List<CustomerVoucher> list = c.getVoucherOfCustomer(cus.getId());
        for (CustomerVoucher customerVoucher : list) {
            if (customerVoucher.getExpirationDate() != null) {
                String expirationDateString = customerVoucher.getExpirationDate();
                String endDateString = customerVoucher.getEndDate();
                Timestamp expirationDate = Timestamp.valueOf(expirationDateString);
                Timestamp endDate = Timestamp.valueOf(endDateString);
                LocalDateTime currentDate = LocalDateTime.now();
                boolean isDeleted = false;
                if (expirationDate.toLocalDateTime().isBefore(currentDate)) {
                    System.out.println("Voucher Het Han");
                    c.deleteVoucher(cus.getId(), customerVoucher.getVoucherID());
                    isDeleted = true;
                }
                if (((customerVoucher.getUsedCount() == customerVoucher.getMaxUsedCount()) && isDeleted == false) && customerVoucher.getMaxUsedCount() != 0) {
                    System.out.println("Voucher Het Luot sd");
                    c.deleteVoucher(cus.getId(), customerVoucher.getVoucherID());
                    isDeleted = true;
                }

                if (endDate.toLocalDateTime().isBefore(currentDate) && isDeleted == false) {
                    System.out.println("Voucher Het End date");
                    c.deleteVoucher(cus.getId(), customerVoucher.getVoucherID());
                    isDeleted = true;
                }
            }
        }
        list = c.getVoucherOfCustomer(cus.getId());
        session.setAttribute("customerVoucher", list);
        request.getRequestDispatcher("CustomerVoucherView.jsp").forward(request, response);
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
