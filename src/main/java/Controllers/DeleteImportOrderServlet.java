package Controllers;

import DAOs.ImportOrderDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DeleteImportOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int importId;
        try {
            importId = Integer.parseInt(request.getParameter("importId"));
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid import order ID");
            return;
        }

        HttpSession session = request.getSession();
        if (importId > 0 && new ImportOrderDAO().deleteImportOrder(importId)) {
            session.setAttribute("importSuccess", "Import order deleted successfully.");
        } else {
            session.setAttribute("importError",
                    "Could not delete the import order because some imported stock has already been used or the order no longer exists.");
        }
        response.sendRedirect(request.getContextPath() + "/ImportOrder");
    }
}
