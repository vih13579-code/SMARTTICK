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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class CreateVoucherServlet extends HttpServlet {

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
        try {
            request.setCharacterEncoding("UTF-8");

            String code = request.getParameter("voucherCode");
            if (code.length() > 10) {
                request.setAttribute("error", "Voucher code must not exceed 10 characters.");
                request.getRequestDispatcher("CreateVoucherView.jsp").forward(request, response);
                return;
            }

            int type = Integer.parseInt(request.getParameter("voucherType"));
            int value = Integer.parseInt(request.getParameter("voucherValue"));
            int maxDiscount = Integer.parseInt(request.getParameter("maxDiscountAmount"));
            int minOrder = Integer.parseInt(request.getParameter("minOrderValue"));

            String rawStart = request.getParameter("startDate");
            String rawEnd = request.getParameter("endDate");

            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            DateTimeFormatter sqlFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDateTime start = LocalDateTime.parse(rawStart, inputFormat);
            LocalDateTime end = LocalDateTime.parse(rawEnd, inputFormat);

            if (end.isBefore(start)) {
                request.setAttribute("error", "End Date must be after Start Date.");
                request.getRequestDispatcher("CreateVoucherView.jsp").forward(request, response);
                return;
            }
            if(type == 1 && value > 100){
            request.setAttribute("error", "If the voucher type is percent, you cannot set a value greater than 100.");
                request.getRequestDispatcher("CreateVoucherView.jsp").forward(request, response);
                return;
            }

            String startDate = start.format(sqlFormat);
            String endDate = end.format(sqlFormat);

            int maxUsed = Integer.parseInt(request.getParameter("maxUsedCount"));
            int status = Integer.parseInt(request.getParameter("status"));
            String desc = request.getParameter("description");

            VoucherDAO dao = new VoucherDAO();

            if (dao.checkVoucherCodeExists(code)) {
                request.setAttribute("error", "Voucher code '" + code + "' already exists!");
                request.getRequestDispatcher("CreateVoucherView.jsp").forward(request, response);
                return;
            }

            Voucher newVoucher = new Voucher(0, code, value, type, startDate, endDate,
                    0, maxUsed, maxDiscount, minOrder, status, desc);

            int count = dao.insertVoucher(newVoucher);
            if(count>0){
            response.sendRedirect("ViewVoucherListServlet?success=createsuccess");
            }else{
            response.sendRedirect("ViewVoucherListServlet?success=createfailed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while creating voucher.");
            request.getRequestDispatcher("CreateVoucherView.jsp").forward(request, response);
        }
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
