/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class VerifyOTPServlet extends HttpServlet {

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
        if (request.getSession().getAttribute("resetEmail") == null) {
            response.sendRedirect(request.getContextPath() + "/SendMailServlet");
            return;
        }
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
        String otpEntered = request.getParameter("otp") == null ? "" : request.getParameter("otp").trim();
        String otpStored = (String) session.getAttribute("otp");
        Long expiresAt = (Long) session.getAttribute("resetOtpExpiresAt");

        if (otpStored == null || expiresAt == null || System.currentTimeMillis() > expiresAt) {
            clearResetOtp(session);
            request.setAttribute("error", "The OTP has expired. Please resend the code.");
            request.getRequestDispatcher("VerifyOTPView.jsp").forward(request, response);
            return;
        }

        if (otpStored.equals(otpEntered)) {
            session.removeAttribute("otp");
            session.removeAttribute("resetOtpExpiresAt");
            session.setAttribute("resetOtpVerified", Boolean.TRUE);
            // Correct OTP -> Redirect to password reset page
            request.getRequestDispatcher("ResetPasswordView.jsp").forward(request, response);
        } else {
            // Incorrect OTP -> Return to OTP verification page
            request.setAttribute("error", "Incorrect OTP code!");
            request.getRequestDispatcher("VerifyOTPView.jsp").forward(request, response);
        }
    }

    private void clearResetOtp(HttpSession session) {
        session.removeAttribute("otp");
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetOtpExpiresAt");
        session.removeAttribute("resetOtpVerified");
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
