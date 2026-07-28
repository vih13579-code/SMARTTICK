package Controllers;

import DAOs.EmployeeDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteEmployeeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            EmployeeDAO employeeDAO = new EmployeeDAO();
            int result = employeeDAO.blockEmployee(employeeId);
            if (result > 0) {
                response.sendRedirect("ViewEmployeeServlet?success=deletesuccess");
            } else {
                response.sendRedirect("ViewEmployeeServlet?success=deletefailed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("ViewEmployeeServlet?success=deletefailed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
