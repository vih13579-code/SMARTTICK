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
public class UpdateVoucherServlet extends HttpServlet {

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
        String id = request.getParameter("voucherID");
        int voucherId = Integer.parseInt(id);
        VoucherDAO vDAO = new VoucherDAO();

        Voucher voucher = vDAO.getVoucher(voucherId);
        request.setAttribute("voucher", voucher);
        request.getRequestDispatcher("UpdateVoucherView.jsp").forward(request, response);

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
            // Lấy input
            int voucherID = Integer.parseInt(request.getParameter("voucherID"));
            String code = request.getParameter("voucherCode").trim();
            int type = Integer.parseInt(request.getParameter("voucherType"));
            int value = Integer.parseInt(request.getParameter("voucherValue"));
            int maxDiscount = Integer.parseInt(request.getParameter("maxDiscountAmount"));
            int minOrder = Integer.parseInt(request.getParameter("minOrderValue"));

            String rawStart = request.getParameter("startDate");
            String rawEnd = request.getParameter("endDate");

            int used = Integer.parseInt(request.getParameter("usedCount"));
            int maxUsed = Integer.parseInt(request.getParameter("maxUsedCount"));
            int status = Integer.parseInt(request.getParameter("status"));
            String desc = request.getParameter("description");

            // Parse ngày
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            DateTimeFormatter sqlFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime start = LocalDateTime.parse(rawStart, inputFormat);
            LocalDateTime end = LocalDateTime.parse(rawEnd, inputFormat);
            String startDate = start.format(sqlFormat);
            String endDate = end.format(sqlFormat);

            // ===== VALIDATE =====
            if (code.length() > 10) {
                request.setAttribute("error", "Voucher Code must not exceed 10 characters.");
            } else if (start.isAfter(end)) {
                request.setAttribute("error", "End date must be after start date.");
            } else if (value < 0 || maxDiscount < 0 || minOrder < 0 || used < 0 || maxUsed < 0) {
                request.setAttribute("error", "Numeric values must be non-negative.");
            }

            // Nếu có lỗi → quay lại form
            if (request.getAttribute("error") != null) {
                Voucher errorVoucher = new Voucher(voucherID, code, value, type,
                        rawStart, rawEnd, used, maxUsed, maxDiscount, minOrder, status, desc);
                request.setAttribute("voucher", errorVoucher);
                request.getRequestDispatcher("UpdateVoucherView.jsp").forward(request, response);
                return;
            }

            // Nếu hợp lệ → tiếp tục update
            Voucher updated = new Voucher(voucherID, code, value, type, startDate, endDate,
                    used, maxUsed, maxDiscount, minOrder, status, desc);

            VoucherDAO dao = new VoucherDAO();
            int count = dao.updateVoucher(updated);
            if (count > 0) {
                response.sendRedirect("ViewVoucherListServlet?success=updatesuccess");
            } else {
                response.sendRedirect("ViewVoucherListServlet?success=updatefailed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Something went wrong! Please check your inputs.");
            request.getRequestDispatcher("UpdateVoucherView.jsp").forward(request, response);
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
