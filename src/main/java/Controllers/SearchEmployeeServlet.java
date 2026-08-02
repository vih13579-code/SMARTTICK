package Controllers;

import DAOs.EmployeeDAO;
import DAOs.RoleDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchEmployeeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter("query");
        query = query == null ? "" : query.trim();
        if (query.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ViewEmployeeServlet");
            return;
        }

        EmployeeDAO employeeDAO = new EmployeeDAO();
        request.setAttribute("listE", employeeDAO.searchEmployeesByName(query));
        request.setAttribute("listR", new RoleDAO().getAllRoles());
        request.setAttribute("searchQuery", query);
        request.getRequestDispatcher("EmployeeListView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
