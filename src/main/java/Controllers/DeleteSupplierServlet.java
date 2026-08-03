package Controllers;

import DAOs.SupplierDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Thuongnvce181966
 */
public class DeleteSupplierServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parsePositiveInt(request.getParameter("id"));
        if (id == null) {
            request.getSession().setAttribute("supplierError", "Invalid supplier ID.");
            response.sendRedirect(request.getContextPath() + "/Supplier");
            return;
        }

        SupplierDAO supplierDAO = new SupplierDAO();
        if (supplierDAO.deleteSupplier(id) == 0) {
            request.getSession().setAttribute("supplierError",
                    "Supplier could not be deleted. It may already have been removed.");
        } else {
            request.getSession().setAttribute("supplierMessage", "Supplier deleted successfully.");
        }
        response.sendRedirect(request.getContextPath() + "/Supplier");
    }

    private Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
