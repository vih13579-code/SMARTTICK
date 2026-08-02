package Controllers;

import DAOs.CustomerDAO;
import DAOs.CustomerVoucherDAO;
import DAOs.GoogleLogin;
import Models.Customer;
import Models.GoogleAccount;
import Models.GoogleTokenResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "GoogleLoginServlet", urlPatterns = {"/GoogleLogin"})
public class GoogleLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String source = (String) session.getAttribute("googleOAuthSource");

        try {
            String googleError = clean(request.getParameter("error"));
            if (!googleError.isEmpty()) {
                redirectFailure(request, response, session, source, "Google sign-in was cancelled or denied.");
                return;
            }

            String expectedState = clean((String) session.getAttribute("googleOAuthState"));
            String receivedState = clean(request.getParameter("state"));
            clearOAuthSession(session);
            if (expectedState.isEmpty() || receivedState.isEmpty() || !expectedState.equals(receivedState)) {
                redirectFailure(request, response, session, source, "Google sign-in session expired. Please try again.");
                return;
            }

            String code = clean(request.getParameter("code"));
            if (code.isEmpty()) {
                redirectFailure(request, response, session, source, "Missing Google authorization code.");
                return;
            }

            GoogleLogin googleLogin = new GoogleLogin();
            GoogleTokenResponse tokenResponse = googleLogin.getToken(code);
            if (tokenResponse == null || clean(tokenResponse.getAccessToken()).isEmpty()) {
                redirectFailure(request, response, session, source, "Could not get access token from Google.");
                return;
            }

            GoogleAccount googleAccount = googleLogin.getUserInfo(tokenResponse.getAccessToken());
            fillMissingProfileFromIdToken(googleAccount, tokenResponse.getIdToken());
            Customer customer = resolveCustomer(googleAccount);
            if (customer == null) {
                redirectFailure(request, response, session, source, "Could not find or create your SMARTTICK account.");
                return;
            }
            if (customer.getIsDeleted() == 1) {
                redirectFailure(request, response, session, source, "This account is no longer available.");
                return;
            }
            if (customer.getIsBlock() == 1) {
                redirectFailure(request, response, session, source, "This account is locked.");
                return;
            }

            session.invalidate();
            session = request.getSession(true);
            setAuthenticatedCustomer(session, customer);
            addRememberCookie(request, response, customer.getEmail());
            response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
        } catch (IllegalArgumentException ex) {
            getServletContext().log("Google OAuth validation failed", ex);
            redirectFailure(request, response, session, source, ex.getMessage());
        } catch (Exception ex) {
            getServletContext().log("Google OAuth callback failed", ex);
            redirectFailure(request, response, session, source, "Google sign-in failed. Please try again later.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    public String getServletInfo() {
        return "SMARTTICK Google OAuth callback";
    }

    private Customer resolveCustomer(GoogleAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Could not read Google profile.");
        }

        String email = clean(account.getEmail()).toLowerCase();
        String googleId = clean(account.getId());
        String fullName = clean(account.getName());
        String googleAvatar = normalizeGoogleAvatar(account.getPicture());

        if (email.isEmpty()) {
            throw new IllegalArgumentException("Google account did not return an email address.");
        }
        if (googleId.isEmpty()) {
            throw new IllegalArgumentException("Google account did not return a valid user ID.");
        }
        if (fullName.isEmpty()) {
            fullName = email;
        }

        CustomerDAO customerDAO = new CustomerDAO();

        Customer customerByGoogleId = customerDAO.getCustomerByGoogleId(googleId);
        if (customerByGoogleId != null) {
            if (customerByGoogleId.getIsDeleted() == 1 || customerByGoogleId.getIsBlock() == 1) {
                return customerByGoogleId;
            }
            customerByGoogleId.setFullName(fullName);
            customerByGoogleId.setEmail(email);
            customerByGoogleId.setGoogleId(googleId);
            customerByGoogleId.setAvatar(resolveAvatar(googleAvatar, customerByGoogleId.getAvatar()));
            customerDAO.updateGoogleCustomer(customerByGoogleId);
            Customer refreshedCustomer = customerDAO.getCustomerById(customerByGoogleId.getId());
            return refreshedCustomer != null ? refreshedCustomer : customerByGoogleId;
        }

        Customer customerByEmail = customerDAO.getCustomerByEmail(email);
        if (customerByEmail != null) {
            if (customerByEmail.getIsDeleted() == 1 || customerByEmail.getIsBlock() == 1) {
                return customerByEmail;
            }
            String existingGoogleId = clean(customerByEmail.getGoogleId());
            if (!existingGoogleId.isEmpty() && !existingGoogleId.equals(googleId)) {
                throw new IllegalArgumentException("This email is already linked to another Google account.");
            }
            String avatar = resolveAvatar(googleAvatar, customerByEmail.getAvatar());
            customerDAO.linkGoogleAccount(customerByEmail.getId(), googleId, fullName, avatar);
            Customer refreshedCustomer = customerDAO.getCustomerById(customerByEmail.getId());
            return refreshedCustomer != null ? refreshedCustomer : customerByEmail;
        }

        Customer newCustomer = new Customer(0, fullName, "", null, null, null, email, null, googleId, 0, 0, googleAvatar);
        if (customerDAO.addNewGoogleCustomer(newCustomer) != 1) {
            return null;
        }

        Customer createdCustomer = customerDAO.getGoogleCustomer(email, googleId);
        if (createdCustomer != null) {
            new CustomerVoucherDAO().syncAvailableVouchersForCustomer(createdCustomer.getId());
        }
        return createdCustomer;
    }

    private void setAuthenticatedCustomer(HttpSession session, Customer customer) {
        session.setAttribute("customer", customer);
        session.setAttribute("userId", customer.getId());
        session.setAttribute("fullName", customer.getFullName());
        session.setAttribute("email", customer.getEmail());
        session.setAttribute("role", "USER");
        session.setAttribute("avatar", customer.getAvatar());
    }

    private void addRememberCookie(HttpServletRequest request, HttpServletResponse response, String email) {
        Cookie cookie = new Cookie("smarttick_customer", email);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(20 * 60);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        response.addCookie(cookie);
    }

    private void redirectFailure(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, String source, String message) throws IOException {
        session.setAttribute("message", message);
        response.sendRedirect(request.getContextPath() + ("register".equals(source) ? "/register" : "/customerLogin"));
    }

    private void clearOAuthSession(HttpSession session) {
        session.removeAttribute("googleOAuthState");
        session.removeAttribute("googleOAuthSource");
    }

    private void fillMissingProfileFromIdToken(GoogleAccount account, String idToken) {
        if (account == null || clean(idToken).isEmpty()) {
            return;
        }
        JsonObject payload = decodeIdTokenPayload(idToken);
        if (payload == null) {
            return;
        }
        if (clean(account.getId()).isEmpty()) {
            account.setId(getJsonString(payload, "sub"));
        }
        if (clean(account.getEmail()).isEmpty()) {
            account.setEmail(getJsonString(payload, "email"));
        }
        if (clean(account.getName()).isEmpty()) {
            account.setName(getJsonString(payload, "name"));
        }
        if (clean(account.getPicture()).isEmpty()) {
            account.setPicture(getJsonString(payload, "picture"));
        }
    }

    private JsonObject decodeIdTokenPayload(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            byte[] payload = Base64.getUrlDecoder().decode(parts[1]);
            return new Gson().fromJson(new String(payload, StandardCharsets.UTF_8), JsonObject.class);
        } catch (IllegalArgumentException ex) {
            getServletContext().log("Could not decode Google ID token payload", ex);
            return null;
        }
    }

    private String getJsonString(JsonObject json, String property) {
        if (json == null || !json.has(property) || json.get(property).isJsonNull()) {
            return "";
        }
        return clean(json.get(property).getAsString());
    }

    private String normalizeGoogleAvatar(String avatar) {
        String value = clean(avatar);
        String lowerValue = value.toLowerCase();
        if (lowerValue.startsWith("http://") || lowerValue.startsWith("https://")) {
            return value;
        }
        return "";
    }

    private String resolveAvatar(String googleAvatar, String currentAvatar) {
        return clean(googleAvatar).isEmpty() ? clean(currentAvatar) : googleAvatar;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
