package Controllers;

import DAOs.EmployeeDAO;
import DAOs.RoleDAO;
import Models.Employee;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewEmployeeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        RoleDAO roleDAO = new RoleDAO();
        String idValue = request.getParameter("id");

        if (idValue != null && !idValue.trim().isEmpty()) {
            Integer employeeId = parsePositiveInt(idValue);
            Employee employee = employeeId == null
                    ? null : employeeDAO.getEmployeeById(String.valueOf(employeeId));
            if (employee == null || employee.getRoleId() == 1) {
                response.sendRedirect(request.getContextPath()
                        + "/ViewEmployeeServlet?error=notfound");
                return;
            }

            request.setAttribute("employee", employee);
            request.setAttribute("listR1", roleDAO.getAllRoles());
            request.getRequestDispatcher("EmployeeDetailView.jsp").forward(request, response);
            return;
        }

        request.setAttribute("listE", employeeDAO.getAllEmployees());
        request.setAttribute("listR", roleDAO.getAllRoles());
        request.getRequestDispatcher("EmployeeListView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private Integer parsePositiveInt(String value) {
        try {
            int id = Integer.parseInt(value);
            return id > 0 ? id : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
