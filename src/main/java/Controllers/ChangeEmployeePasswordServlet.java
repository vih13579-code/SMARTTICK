package Controllers;

import DAOs.EmployeeDAO;
import Models.Employee;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ChangeEmployeePasswordServlet", urlPatterns = {"/ChangeEmployeePassword"})
public class ChangeEmployeePasswordServlet extends HttpServlet {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])"
            + "[A-Za-z\\d@$!%*?&]{8,50}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Employee loggedInEmployee = getLoggedInEmployee(session);
        if (loggedInEmployee == null) {
            response.sendRedirect(request.getContextPath() + "/EmployeeLogin");
            return;
        }

        Employee refreshedEmployee = new EmployeeDAO().getEmployeeById(
                String.valueOf(loggedInEmployee.getEmployeeId()));
        if (refreshedEmployee == null) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/EmployeeLogin");
            return;
        }

        session.setAttribute("employee", refreshedEmployee);
        request.getRequestDispatcher("ChangeEmployeePasswordView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Employee employee = getLoggedInEmployee(session);
        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/EmployeeLogin");
            return;
        }

        String currentPassword = request.getParameter("current");
        String newPassword = request.getParameter("new");
        String confirmPassword = request.getParameter("confirm");

        String validationError = validatePasswords(
                currentPassword, newPassword, confirmPassword);
        if (validationError != null) {
            redirectWithError(request, response, session, validationError);
            return;
        }

        EmployeeDAO employeeDAO = new EmployeeDAO();
        if (employeeDAO.checkPassword(employee.getEmployeeId(), currentPassword) != 1) {
            redirectWithError(request, response, session,
                    "The current password is incorrect.");
            return;
        }

        if (employeeDAO.changePassword(employee.getEmployeeId(), newPassword) != 1) {
            redirectWithError(request, response, session,
                    "The password could not be changed. Please try again.");
            return;
        }

        session.setAttribute("empromess", "Password changed successfully.");
        response.sendRedirect(request.getContextPath() + "/ViewEmployeeProfile");
    }

    private Employee getLoggedInEmployee(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object employee = session.getAttribute("employee");
        return employee instanceof Employee ? (Employee) employee : null;
    }

    private String validatePasswords(String currentPassword, String newPassword,
            String confirmPassword) {
        if (currentPassword == null || currentPassword.isEmpty()) {
            return "Please enter your current password.";
        }
        if (newPassword == null || !PASSWORD_PATTERN.matcher(newPassword).matches()) {
            return "The new password must contain 8–50 characters, including uppercase, "
                    + "lowercase, number, and special character.";
        }
        if (currentPassword.equals(newPassword)) {
            return "The new password must be different from the current password.";
        }
        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            return "The confirmation password does not match.";
        }
        return null;
    }

    private void redirectWithError(HttpServletRequest request,
            HttpServletResponse response, HttpSession session, String message)
            throws IOException {
        session.setAttribute("passwordError", message);
        response.sendRedirect(request.getContextPath() + "/ChangeEmployeePassword");
    }
}
