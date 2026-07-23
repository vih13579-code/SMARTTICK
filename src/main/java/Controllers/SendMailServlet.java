/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.CustomerDAO;
import Models.Customer;
import Models.Email;
import Models.EmailUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.regex.Pattern;

public class SendMailServlet extends HttpServlet {
    public static final long RESET_OTP_VALID_MILLIS = 10 * 60 * 1000L;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final SecureRandom RANDOM = new SecureRandom();

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
        request.getRequestDispatcher("ForgotPasswordView.jsp").forward(request, response);
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
        try {
            String emailAddress = request.getParameter("email") == null ? "" : request.getParameter("email").trim();

            if (!EMAIL_PATTERN.matcher(emailAddress).matches()) {
                request.setAttribute("error", "Please enter a valid email!");
                request.getRequestDispatcher("ForgotPasswordView.jsp").forward(request, response);
                return;
            }

            // Check if the email exists
            CustomerDAO userDAO = new CustomerDAO();
            Customer user = userDAO.getCustomerByEmail(emailAddress);
            
            if (user == null) {
                request.setAttribute("error", "Email does not exist in the system!");
                request.getRequestDispatcher("ForgotPasswordView.jsp").forward(request, response);
                return;
            }
            if (user.getIsDeleted() == 1 || user.getIsBlock() == 1) {
                request.setAttribute("error", "This account cannot reset password.");
                request.getRequestDispatcher("ForgotPasswordView.jsp").forward(request, response);
                return;
            }
            
            // Check if the account is created using Google
            if (user.getGoogleId() != null && !user.getGoogleId().isEmpty()) {
                request.setAttribute("error", "This account was created using Google and cannot reset password!");
                request.getRequestDispatcher("ForgotPasswordView.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            clearResetOtp(session);

            // Generate OTP code
            String otp = generateOTP();

            // Send OTP via email
            Email email = new Email();
            email.setTo(emailAddress);
            email.setSubject("SMARTTICK - Password Reset OTP");
            String emailContent = "<p>Dear Customer,</p>"
                    + "<p>We received a request to reset your password. Please use the OTP below to proceed:</p>"
                    + "<h2 style='color:#b68a3a;letter-spacing:6px;'>" + otp + "</h2>"
                    + "<p>This code is valid for 10 minutes. Do not share it with anyone.</p>"
                    + "<p>If you did not request this, please ignore this email.</p>"
                    + "<p>Best regards,<br>SMARTTICK</p>";
            email.setContent(emailContent);

            try {
                EmailUtils.send(email);
            } catch (Exception e) {
                getServletContext().log("Cannot send reset password OTP email", e);
                request.setAttribute("error", "Failed to send email! Please try again.");
                request.getRequestDispatcher("ForgotPasswordView.jsp").forward(request, response);
                return;
            }

            session.setAttribute("otp", otp);
            session.setAttribute("resetEmail", emailAddress);
            session.setAttribute("resetOtpExpiresAt", System.currentTimeMillis() + RESET_OTP_VALID_MILLIS);
            session.setAttribute("resetOtpVerified", Boolean.FALSE);

            // Redirect to OTP input page
            request.setAttribute("message", "OTP has been sent to your email.");
            request.getRequestDispatcher("VerifyOTPView.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred, please try again!");
            request.getRequestDispatcher("ForgotPasswordView.jsp").forward(request, response);
        }
    }

    /**
     * Generate a random 6-digit OTP code
     */
    private String generateOTP() {
        int otp = 100000 + RANDOM.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    private void clearResetOtp(HttpSession session) {
        session.removeAttribute("otp");
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetOtpExpiresAt");
        session.removeAttribute("resetOtpVerified");
    }

}
