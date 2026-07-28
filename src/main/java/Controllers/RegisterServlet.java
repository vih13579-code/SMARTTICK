package Controllers;

import DAOs.CustomerDAO;
import DAOs.CustomerVoucherDAO;
import DAOs.Iconstant;
import Models.Customer;
import Models.Email;
import Models.EmailUtils;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    public static final long REGISTER_OTP_VALID_MILLIS = 10 * 60 * 1000L;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%!&*?]).{8,50}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("googleConfigured", Iconstant.isConfigured());
        clearGoogleConfigurationMessage(request.getSession(false));
        request.getRequestDispatcher("/RegisterView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String fullName = value(request.getParameter("fullname"));
        String email = value(request.getParameter("email")).toLowerCase(Locale.ROOT);
        String password = request.getParameter("password") == null ? "" : request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword") == null ? "" : request.getParameter("confirmPassword");

        if (fullName.isEmpty()) {
            forwardRegisterError(request, response, "Please enter your full name.");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            forwardRegisterError(request, response, "Invalid email.");
            return;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            forwardRegisterError(request, response,
                    "Password must be 8-50 characters and include uppercase, lowercase, number, and special character.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            forwardRegisterError(request, response, "Password confirmation does not match.");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        if (customerDAO.checkEmailExisted(email) == 1) {
            forwardRegisterError(request, response, "This email is already registered.");
            return;
        }

        Customer pendingCustomer = new Customer(0, fullName, password, "", "", "", email, "", 0, 0, "");
        String otp = generateOTP();
        try {
            sendOTPEmail(email, otp, fullName);
        } catch (Exception ex) {
            HttpSession session = request.getSession();
            clearRegisterOtp(session);
            getServletContext().log("Cannot send registration OTP email", ex);
            if (completeRegistration(customerDAO, pendingCustomer)) {
                session.setAttribute("successMessage",
                        "Registration successful. Email verification was skipped because SMTP is unavailable.");
                response.sendRedirect(request.getContextPath() + "/customerLogin");
            } else {
                forwardRegisterError(request, response, "Registration failed. Please try again.");
            }
            return;
        }

        HttpSession session = request.getSession();
        clearRegisterOtp(session);
        session.setAttribute("otp", otp);
        session.setAttribute("registerOtpExpiresAt", System.currentTimeMillis() + REGISTER_OTP_VALID_MILLIS);
        session.setAttribute("registerCustomer", pendingCustomer);
        response.sendRedirect(request.getContextPath() + "/OTPView.jsp");
    }

    private String value(String input) {
        return input == null ? "" : input.trim();
    }

    private void forwardRegisterError(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        request.setAttribute("error", error);
        request.setAttribute("googleConfigured", Iconstant.isConfigured());
        request.getRequestDispatcher("/RegisterView.jsp").forward(request, response);
    }

    private void clearRegisterOtp(HttpSession session) {
        session.removeAttribute("otp");
        session.removeAttribute("registerOtpExpiresAt");
        session.removeAttribute("registerCustomer");
        session.removeAttribute("otpFallback");
        session.removeAttribute("otpFallbackWarning");
    }

    private void clearGoogleConfigurationMessage(HttpSession session) {
        if (session == null) {
            return;
        }
        Object message = session.getAttribute("message");
        if (message instanceof String && ((String) message).startsWith("Google login is not configured yet.")) {
            session.removeAttribute("message");
        }
    }

    private boolean completeRegistration(CustomerDAO customerDAO, Customer customer) {
        if (customerDAO.addNewCustomer(customer) == 0) {
            return false;
        }

        Customer registeredCustomer = customerDAO.getCustomerLogin(customer.getEmail(), customer.getPassword());
        if (registeredCustomer != null) {
            CustomerVoucherDAO voucherDAO = new CustomerVoucherDAO();
            voucherDAO.syncAvailableVouchersForCustomer(registeredCustomer.getId());
        }
        return true;
    }

    private String generateOTP() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }

    private void sendOTPEmail(String recipientEmail, String otp, String fullName) throws Exception {
        Email email = new Email();
        email.setTo(recipientEmail);
        email.setSubject("SMARTTICK - Registration Verification OTP");
        email.setContent(
                "<div style='font-family:Arial,sans-serif;color:#111827;line-height:1.6'>"
                + "<h2 style='margin:0 0 12px'>Hello " + escapeHtml(fullName) + ",</h2>"
                + "<p>Thank you for registering a SMARTTICK account. Your email verification OTP is:</p>"
                + "<div style='font-size:32px;font-weight:800;letter-spacing:6px;color:#b68a3a;margin:18px 0'>"
                + otp + "</div>"
                + "<p>This code is valid for 10 minutes. Do not share it with anyone.</p>"
                + "<p>If you did not make this request, please ignore this email.</p>"
                + "<p>SMARTTICK Team</p>"
                + "</div>");
        EmailUtils.send(email);
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
