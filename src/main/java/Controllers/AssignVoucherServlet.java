/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.CustomerDAO;
import DAOs.CustomerVoucherDAO;
import DAOs.VoucherDAO;
import Models.Customer;
import Models.Voucher;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.util.List;

public class AssignVoucherServlet extends HttpServlet {

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
        try {
            int customerId = Integer.parseInt(request.getParameter("Id"));

            CustomerDAO cDAO = new CustomerDAO();
            VoucherDAO vDAO = new VoucherDAO();

            Customer customer = cDAO.getCustomerById(customerId);
            List<Voucher> vouchers = vDAO.getAllVoucherActivate();

            request.setAttribute("customer", customer);
            request.setAttribute("vouchers", vouchers);
        } catch (Exception e) {
            e.printStackTrace();  // Ghi log để dễ debug
            request.setAttribute("error", "Unable to load customer data or voucher.");
        }

        // Dù có lỗi hay không, vẫn forward về JSP
        request.getRequestDispatcher("AssignVoucherView.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        try {
//            int customerID = Integer.parseInt(request.getParameter("customerID"));
//            int voucherID = Integer.parseInt(request.getParameter("voucherID"));
//            int quantity = Integer.parseInt(request.getParameter("quantity"));
//            String rawExpire = request.getParameter("expirationDate");
//
//            String expirationDate = null;
//            LocalDateTime now = LocalDateTime.now();
//
//            if (quantity <= 0) {
//                throw new Exception("Quantity must be greater than 0.");
//            }
//
//            if (rawExpire != null && !rawExpire.isEmpty()) {
//                LocalDateTime dt = LocalDateTime.parse(rawExpire, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
//                if (dt.isBefore(now)) {
//                    throw new Exception("Expiration date must be after the current time.");
//                }
//                expirationDate = dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            }
//
//            CustomerVoucherDAO cvDAO = new CustomerVoucherDAO();
//            if (cvDAO.isVoucherAlreadyAssigned(customerID, voucherID)) {
//                throw new Exception("The customer has been issued this voucher.");
//            }
//            VoucherDAO vDAO = new VoucherDAO();
//            Voucher voucher = vDAO.getVoucher(voucherID); 
//
//            // Nếu voucher không tồn tại, báo lỗi
//            if (voucher == null) {
//                throw new Exception("Voucher not found.");
//            }
//            // Convert ExpirationDate string thành LocalDateTime để so sánh
//            LocalDateTime expirationDateTime = LocalDateTime.parse(expirationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            String sd = voucher.getStartDate();
//            String ed =  voucher.getEndDate();
//            LocalDateTime startDate = LocalDateTime.parse(voucher.getStartDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
//            LocalDateTime endDate = LocalDateTime.parse(voucher.getEndDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
//
//            // Kiểm tra ngày hết hạn có nằm trong khoảng ngày của voucher không
//            if (expirationDateTime.isBefore(startDate) || expirationDateTime.isAfter(endDate)) {
//                throw new Exception("Expiration date must be between the voucher's start and end date.");
//            }
//
//            int count = cvDAO.assignVoucherToCustomer(customerID, voucherID, quantity, expirationDate);
//            if (count > 0) {
//                response.sendRedirect("CustomerListServlet?success=assigned");
//            } else {
//                response.sendRedirect("CustomerListServlet?success=failed");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            request.setAttribute("error", e.getMessage());
//
//            CustomerDAO cDAO = new CustomerDAO();
//            VoucherDAO vDAO = new VoucherDAO();
//
//            int id = Integer.parseInt(request.getParameter("customerID"));
//            request.setAttribute("customer", cDAO.getCustomerById(id));
//            request.setAttribute("vouchers", vDAO.getAllVoucher());
//
//            request.getRequestDispatcher("AssignVoucherView.jsp").forward(request, response);
//        }
//    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int customerID = Integer.parseInt(request.getParameter("customerID"));
            int voucherID = Integer.parseInt(request.getParameter("voucherID"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            String rawExpire = request.getParameter("expirationDate");

            LocalDateTime now = LocalDateTime.now();

            if (quantity <= 0) {
                throw new Exception("Quantity must be greater than 0.");
            } else if (quantity > 2) {
                throw new Exception("Quantity must be smaller than 3.");
            }

            VoucherDAO vDAO = new VoucherDAO();
            Voucher voucher = vDAO.getVoucher(voucherID);

            if (voucher == null || voucher.getVoucherID() <= 0) {
                throw new Exception("Voucher not found.");
            }

            if (voucher.getStatus() != 1) {
                throw new Exception("Voucher is inactive.");
            }

            if (voucher.getMaxUsedCount() > 0) {
                int remainingUses = voucher.getMaxUsedCount() - voucher.getUsedCount();
                if (quantity > remainingUses) {
                    throw new Exception("Voucher does not have enough remaining uses.");
                }
            }

            LocalDateTime startDate = Timestamp.valueOf(voucher.getStartDate()).toLocalDateTime();
            LocalDateTime endDate = Timestamp.valueOf(voucher.getEndDate()).toLocalDateTime();
            if (endDate.isBefore(now)) {
                throw new Exception("Voucher has expired.");
            }

            String expirationDate = null;
            if (rawExpire != null && !rawExpire.trim().isEmpty()) {
                LocalDateTime expirationDateTime = LocalDateTime.parse(
                        rawExpire, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                if (expirationDateTime.isBefore(now)) {
                    throw new Exception("Expiration date must be after the current time.");
                }
                if (expirationDateTime.isBefore(startDate) || expirationDateTime.isAfter(endDate)) {
                    throw new Exception("Expiration date must be between the voucher's start and end date.");
                }
                expirationDate = expirationDateTime.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }

            CustomerVoucherDAO cvDAO = new CustomerVoucherDAO();
            int currentQuantity = cvDAO.getVoucherQuantity(customerID, voucherID);
            if (currentQuantity + quantity > 2) {
                throw new Exception("A customer can hold at most 2 copies of the same voucher.");
            }

            int count = cvDAO.assignVoucherToCustomer(customerID, voucherID, quantity, expirationDate);
            if (count > 0) {
                response.sendRedirect("CustomerListServlet?success=assigned");
            } else {
                response.sendRedirect("CustomerListServlet?success=failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());

            // Reload lại thông tin customer và voucher
            CustomerDAO cDAO = new CustomerDAO();
            VoucherDAO vDAO = new VoucherDAO();

            int id = Integer.parseInt(request.getParameter("customerID"));
            request.setAttribute("customer", cDAO.getCustomerById(id));
            request.setAttribute("vouchers", vDAO.getAllVoucherActivate());

            request.getRequestDispatcher("AssignVoucherView.jsp").forward(request, response);
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
