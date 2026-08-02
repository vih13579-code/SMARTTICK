package Controllers;

import DAOs.CartDAO;
import DAOs.CustomerDAO;
import DAOs.OrderDAO;
import Models.Customer;
import Models.Email;
import Models.EmailUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "RequestToDeleteAccountServlet",
        urlPatterns = {"/requestToDeleteAccount"})
public class RequestToDeleteAccountServlet extends HttpServlet {

    private static final long OTP_VALIDITY_MILLIS = 5 * 60 * 1000L;
    private static final long CHALLENGE_VALIDITY_MILLIS = 10 * 60 * 1000L;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null) {
            writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "fail", "Your session has expired.", null, null);
            return;
        }
        if (hasPendingOrders(customer.getId())) {
            writeJson(response, HttpServletResponse.SC_CONFLICT,
                    "fail", "You have an active order. Finish or cancel it before deleting your account.",
                    null, null);
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        boolean hasLocalPassword = customerDAO.hasLocalPassword(customer.getId());
        String challenge = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        session.setAttribute("deleteAccountChallenge", challenge);
        session.setAttribute("deleteAccountChallengeExpiresAt",
                now + CHALLENGE_VALIDITY_MILLIS);
        session.setAttribute("deleteAccountCustomerId", customer.getId());

        if (hasLocalPassword) {
            clearOtp(session);
            writeJson(response, HttpServletResponse.SC_OK,
                    "success", null, "password", challenge);
            return;
        }

        String otp = generateOtp();
        try {
            sendOtpEmail(customer.getEmail(), otp, customer.getFullName());
            session.setAttribute("deleteAccountOtp", otp);
            session.setAttribute("deleteAccountOtpExpiresAt", now + OTP_VALIDITY_MILLIS);
            writeJson(response, HttpServletResponse.SC_OK,
                    "success", null, "otp", challenge);
        } catch (Exception ex) {
            clearDeletionVerification(session);
            getServletContext().log("Could not send account deletion OTP to customer "
                    + customer.getId(), ex);
            writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "fail", "The verification email could not be sent. Please try again later.",
                    null, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin?expired=1");
            return;
        }

        if (!isValidChallenge(request, session, customer.getId())) {
            failAndRedirect(request, response,
                    "The deletion confirmation expired. Please start again.");
            return;
        }
        if (hasPendingOrders(customer.getId())) {
            clearDeletionVerification(session);
            failAndRedirect(request, response,
                    "You have an active order. Finish or cancel it before deleting your account.");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        boolean hasLocalPassword = customerDAO.hasLocalPassword(customer.getId());
        if (hasLocalPassword) {
            String password = request.getParameter("confirmPassword");
            if (!customerDAO.confirmPassword(customer.getId(), password)) {
                clearDeletionVerification(session);
                failAndRedirect(request, response, "The confirmation password is incorrect.");
                return;
            }
        } else if (!isValidOtp(request.getParameter("OTP"), session)) {
            clearDeletionVerification(session);
            failAndRedirect(request, response,
                    "The verification code is incorrect or has expired.");
            return;
        }

        if (customerDAO.requestToDeleteAccount(customer.getId()) <= 0) {
            clearDeletionVerification(session);
            failAndRedirect(request, response,
                    "Your account could not be deleted. Please try again.");
            return;
        }

        new CartDAO().deleteCartOfCustomer(customer.getId());
        clearDeletionVerification(session);
        getServletContext().log("Customer account deletion completed for customer ID "
                + customer.getId());
        response.sendRedirect(request.getContextPath() + "/Logout");
    }

    private boolean hasPendingOrders(int customerId) {
        return new OrderDAO().checkHaveOrders(customerId) != 0;
    }

    private boolean isValidChallenge(HttpServletRequest request, HttpSession session,
            int customerId) {
        String submitted = clean(request.getParameter("challenge"));
        String expected = clean((String) session.getAttribute("deleteAccountChallenge"));
        Object expiryValue = session.getAttribute("deleteAccountChallengeExpiresAt");
        Object customerValue = session.getAttribute("deleteAccountCustomerId");
        if (!(expiryValue instanceof Long) || !(customerValue instanceof Integer)) {
            return false;
        }
        return !submitted.isEmpty()
                && submitted.equals(expected)
                && customerId == (Integer) customerValue
                && System.currentTimeMillis() <= (Long) expiryValue;
    }

    private boolean isValidOtp(String submittedOtp, HttpSession session) {
        String expectedOtp = clean((String) session.getAttribute("deleteAccountOtp"));
        Object expiryValue = session.getAttribute("deleteAccountOtpExpiresAt");
        return expiryValue instanceof Long
                && !expectedOtp.isEmpty()
                && expectedOtp.equals(clean(submittedOtp))
                && System.currentTimeMillis() <= (Long) expiryValue;
    }

    private String generateOtp() {
        return String.valueOf(100000 + secureRandom.nextInt(900000));
    }

    private void sendOtpEmail(String recipientEmail, String otp, String fullName)
            throws Exception {
        Email email = new Email();
        email.setTo(recipientEmail);
        email.setSubject("Confirm your SMARTTICK account deletion");
        email.setContent("<p>Dear " + escapeHtml(clean(fullName)) + ",</p>"
                + "<p>Use this verification code to confirm your account deletion request:</p>"
                + "<h2>" + otp + "</h2>"
                + "<p>This code expires in 5 minutes. If you did not request account deletion, "
                + "do not share this code and keep your account signed in.</p>"
                + "<p>SMARTTICK Team</p>");
        EmailUtils.send(email);
    }

    private void clearOtp(HttpSession session) {
        session.removeAttribute("deleteAccountOtp");
        session.removeAttribute("deleteAccountOtpExpiresAt");
    }

    private void clearDeletionVerification(HttpSession session) {
        clearOtp(session);
        session.removeAttribute("deleteAccountChallenge");
        session.removeAttribute("deleteAccountChallengeExpiresAt");
        session.removeAttribute("deleteAccountCustomerId");
    }

    private void failAndRedirect(HttpServletRequest request, HttpServletResponse response,
            String message) throws IOException {
        request.getSession().setAttribute("messageFail", message);
        response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
    }

    private void writeJson(HttpServletResponse response, int statusCode, String status,
            String message, String verification, String challenge) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("status", status);
        if (message != null) {
            json.addProperty("message", message);
        }
        if (verification != null) {
            json.addProperty("verification", verification);
        }
        if (challenge != null) {
            json.addProperty("challenge", challenge);
        }
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new Gson().toJson(json));
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
