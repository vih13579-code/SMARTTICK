package Controllers;

import DAOs.EmployeeDAO;
import Models.Employee;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DisableEmployeeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Employee actor = session == null
                ? null : (Employee) session.getAttribute("employee");
        if (actor == null) {
            response.sendRedirect(request.getContextPath() + "/EmployeeLogin");
            return;
        }
        if (actor.getRoleId() != 1) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            if (employeeId <= 0 || employeeId == actor.getEmployeeId()) {
                redirectResult(request, response, false);
                return;
            }

            EmployeeDAO employeeDAO = new EmployeeDAO();
            Employee target = employeeDAO.getEmployeeById(String.valueOf(employeeId));
            if (target == null || target.getRoleId() == 1) {
                redirectResult(request, response, false);
                return;
            }
            redirectResult(request, response,
                    employeeDAO.blockEmployee(employeeId) == 1);
        } catch (NumberFormatException ex) {
            redirectResult(request, response, false);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private void redirectResult(HttpServletRequest request,
            HttpServletResponse response, boolean success) throws IOException {
        response.sendRedirect(request.getContextPath()
                + "/ViewEmployeeServlet?success="
                + (success ? "disablesuccess" : "disablefailed"));
    }
}
