/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.CustomerDAO;
import DAOs.CustomerVoucherDAO;
import Models.Customer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author TranVTH
 */

public class RegisterOTPServlet extends HttpServlet {

    // Static map lưu trạng thái lock theo email
    private static final Map<String, LockInfo> lockMap = new ConcurrentHashMap<>();

    // Class lưu số lần thử và thời gian lock
    private static class LockInfo {

        int attempts;
        long lockTime; // 0 nếu chưa lock

        LockInfo(int attempts, long lockTime) {
            this.attempts = attempts;
            this.lockTime = lockTime;
        }
    }

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
        request.getRequestDispatcher("VerifyOTPView.jsp").forward(request, response);
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
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        // Lấy thông tin email từ đối tượng customer đã đăng ký
        Customer customer = (Customer) session.getAttribute("registerCustomer");
        if (customer == null) {
            request.setAttribute("error", "Session expired! Please register again.");
            request.getRequestDispatcher("RegisterView.jsp").forward(request, response);
            return;
        }
        String email = customer.getEmail();
        String otpEntered = request.getParameter("otp") == null ? "" : request.getParameter("otp").trim();
        String otpStored = (String) session.getAttribute("otp");
        Long expiresAt = (Long) session.getAttribute("registerOtpExpiresAt");

        if (otpStored == null || expiresAt == null || System.currentTimeMillis() > expiresAt) {
            clearRegisterOtp(session);
            request.setAttribute("error", "The OTP has expired. Please register again to receive a new code.");
            request.getRequestDispatcher("OTPView.jsp").forward(request, response);
            return;
        }

        // Kiểm tra trạng thái lock của email từ lockMap
        LockInfo lockInfo = lockMap.get(email);
        if (lockInfo != null && lockInfo.attempts >= 5) {
            long elapsedTime = System.currentTimeMillis() - lockInfo.lockTime;
            if (elapsedTime < 30 * 60 * 1000) {
                int minutesRemaining = (int) ((30 * 60 * 1000 - elapsedTime) / 60000);
                request.setAttribute("error", "You entered the wrong OTP more than 5 times. Please try again after " + minutesRemaining + " minutes.");
                request.getRequestDispatcher("OTPView.jsp").forward(request, response);
                return;
            } else {
                // Reset lock nếu đã hết thời gian
                lockMap.remove(email);
                lockInfo = null;
            }
        }

        if (otpStored != null && otpStored.equals(otpEntered)) {
            // Nếu OTP đúng: xóa trạng thái lock (nếu có) và reset số lần thử
            lockMap.remove(email);

            // Đăng ký customer vào database
            CustomerDAO ctmDAO = new CustomerDAO();
            int result = ctmDAO.addNewCustomer(customer);

            if (result != 0) {
                clearRegisterOtp(session);
                session.setAttribute("successMessage", "Registration successful. Please log in.");

                CustomerVoucherDAO cv = new CustomerVoucherDAO();
                Customer cus = ctmDAO.getCustomerLogin(customer.getEmail(), customer.getPassword());
                if (cus != null) {
                    cv.assignVoucherToCustomer(cus.getId(), 1, 1, null);
                }
                response.sendRedirect(request.getContextPath() + "/customerLogin");
            } else {
                request.setAttribute("error", "Registration failed. Please try again.");
                request.getRequestDispatcher("OTPView.jsp").forward(request, response);
            }
        } else {
            // Nếu OTP sai, cập nhật số lần nhập sai vào lockMap
            if (lockInfo == null) {
                // Tạo mới LockInfo với 1 lần thử
                lockInfo = new LockInfo(1, 0);
                lockMap.put(email, lockInfo);
            } else {
                lockInfo.attempts++;
                if (lockInfo.attempts >= 5 && lockInfo.lockTime == 0) {
                    // Khi đạt 5 lần, thiết lập thời gian lock
                    lockInfo.lockTime = System.currentTimeMillis();
                }
            }
            int remaining = 5 - lockInfo.attempts;
            if (remaining < 0) {
                remaining = 0;
            }
            request.setAttribute("error", "Incorrect OTP code! You have " + remaining + " attempts left.");
            request.getRequestDispatcher("OTPView.jsp").forward(request, response);
        }
    }

    private void clearRegisterOtp(HttpSession session) {
        session.removeAttribute("otp");
        session.removeAttribute("registerOtpExpiresAt");
        session.removeAttribute("registerCustomer");
        session.removeAttribute("otpFallback");
        session.removeAttribute("otpFallbackWarning");
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
