package Controllers;

import DAOs.CustomerDAO;
import DAOs.Iconstant;
import Models.Customer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CustomerLoginServlet", urlPatterns = {"/customerLogin"})
public class CustomerLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("customer") != null) {
            response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
            return;
        }

        request.setAttribute("rememberedEmail", getCookieValue(request, "smarttick_customer"));
        if (request.getParameter("expired") != null) {
            request.setAttribute("pageMessage", "Your session expired. Please sign in again.");
        }
        request.setAttribute("googleConfigured", Iconstant.isConfigured());
        clearGoogleConfigurationMessage(session);
        request.getRequestDispatcher("CustomerLoginView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        CustomerDAO dao = new CustomerDAO();

        if (email == null || password == null || email.trim().isEmpty() || password.isEmpty()) {
            session.setAttribute("message", "Email and password are required.");
            response.sendRedirect(request.getContextPath() + "/customerLogin");
            return;
        }

        String loginEmail = email.trim();
        Customer customer = dao.getCustomerLogin(loginEmail, password);
        if (customer == null) {
            session.setAttribute("message", "Incorrect email or password.");
            response.sendRedirect(request.getContextPath() + "/customerLogin");
            return;
        }
        if (customer.getIsDeleted() == 1) {
            session.setAttribute("message", "This account no longer exists.");
            response.sendRedirect(request.getContextPath() + "/customerLogin");
            return;
        }
        if (customer.getIsBlock() == 1) {
            session.setAttribute("message", "This account is locked.");
            response.sendRedirect(request.getContextPath() + "/customerLogin");
            return;
        }

        session.invalidate();
        session = request.getSession(true);
        session.setAttribute("customer", customer);
        session.setAttribute("userId", customer.getId());
        session.setAttribute("fullName", customer.getFullName());
        session.setAttribute("email", customer.getEmail());
        session.setAttribute("role", "USER");
        session.setAttribute("avatar", customer.getAvatar());

        Cookie cookie = new Cookie("smarttick_customer", loginEmail);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(20 * 60);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        response.addCookie(cookie);
        response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "";
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return "";
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
}
