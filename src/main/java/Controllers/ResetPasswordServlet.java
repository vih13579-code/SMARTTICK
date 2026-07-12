/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.CustomerDAO;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ResetPasswordServlet extends HttpServlet {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!&*?]).{8,50}$");

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
        if (!isResetOtpVerified(request.getSession())) {
            response.sendRedirect(request.getContextPath() + "/SendMailServlet");
            return;
        }
        request.getRequestDispatcher("ResetPasswordView.jsp").forward(request, response);
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
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("resetEmail");

        if (!isResetOtpVerified(session) || email == null || email.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/SendMailServlet");
            return;
        }

        // Kiểm tra confirm password
        if (newPassword == null || confirmPassword == null || !newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match!");
            request.getRequestDispatcher("ResetPasswordView.jsp").forward(request, response);
            return;
        }

        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            request.setAttribute("error", "Password must be 8-50 characters and include uppercase, lowercase, number, and special character.");
            request.getRequestDispatcher("ResetPasswordView.jsp").forward(request, response);
            return;
        }

        CustomerDAO userDAO = new CustomerDAO();
        // Update Password mới trong database
        boolean success = userDAO.updatePassword(email, newPassword);

        if (success) {
            session.removeAttribute("otp");
            session.removeAttribute("resetEmail");
            session.removeAttribute("resetOtpExpiresAt");
            session.removeAttribute("resetOtpVerified");
            session.setAttribute("successMessage", "Password changed successfully!");
            response.sendRedirect(request.getContextPath() + "/customerLogin");
        } else {
            request.setAttribute("error", "An error occurred! Please try again.");
            request.getRequestDispatcher("ResetPasswordView.jsp").forward(request, response);
        }
    }

    private boolean isResetOtpVerified(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("resetOtpVerified"));
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
