package Controllers;

import DAOs.CustomerDAO;
import Models.Customer;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/changeCustomerPassword"})
public class ChangeCustomerPasswordServlet extends HttpServlet {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])"
            + "[A-Za-z\\d@$!%*?&]{8,50}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Customer customer = getAuthenticatedCustomer(request, response);
        if (customer == null) {
            return;
        }
        renderForm(request, response, new CustomerDAO().hasLocalPassword(customer.getId()));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Customer customer = getAuthenticatedCustomer(request, response);
        if (customer == null) {
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        boolean hasLocalPassword = customerDAO.hasLocalPassword(customer.getId());
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            showError(request, response, hasLocalPassword, "Please enter a new password.");
            return;
        }
        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            showError(request, response, hasLocalPassword,
                    "Password must be 8-50 characters and include uppercase, lowercase, "
                    + "a number, and one of @$!%*?&.");
            return;
        }
        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            showError(request, response, hasLocalPassword,
                    "New password and confirmation do not match.");
            return;
        }

        if (hasLocalPassword) {
            if (currentPassword == null || currentPassword.isEmpty()) {
                showError(request, response, true, "Please enter your current password.");
                return;
            }
            if (!customerDAO.confirmPassword(customer.getId(), currentPassword)) {
                showError(request, response, true, "Your current password is incorrect.");
                return;
            }
            if (currentPassword.equals(newPassword)) {
                showError(request, response, true,
                        "New password must be different from your current password.");
                return;
            }
        }

        if (customerDAO.changeCustomerPassword(customer.getId(), newPassword) <= 0) {
            showError(request, response, hasLocalPassword,
                    "The password could not be saved. Please try again.");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("hasLocalPassword", Boolean.TRUE);
        session.setAttribute("message", hasLocalPassword
                ? "Password changed successfully."
                : "SMARTTICK password created. You can now sign in with email and password.");
        getServletContext().log("Customer password updated for customer ID " + customer.getId());
        response.sendRedirect(request.getContextPath() + "/changeCustomerPassword");
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin?expired=1");
        }
        return customer;
    }

    private void showError(HttpServletRequest request, HttpServletResponse response,
            boolean hasLocalPassword, String message) throws ServletException, IOException {
        request.setAttribute("passwordError", message);
        renderForm(request, response, hasLocalPassword);
    }

    private void renderForm(HttpServletRequest request, HttpServletResponse response,
            boolean hasLocalPassword) throws ServletException, IOException {
        request.getSession().setAttribute("hasLocalPassword", hasLocalPassword);
        request.setAttribute("hasLocalPassword", hasLocalPassword);
        request.setAttribute("profilePage", "ChangeCustomerPasswordView.jsp");
        request.getRequestDispatcher("/ProfileManagementView.jsp").forward(request, response);
    }
}
