package Controllers;

import DAOs.BrandDAO;
import DAOs.CategoryDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "CatalogManagementServlet", urlPatterns = {"/admin/catalog"})
public class CatalogManagementServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("categories", new CategoryDAO().getAllCategories());
        request.setAttribute("brands", new BrandDAO().getAllBrand());
        request.getRequestDispatcher("/CatalogManagementView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String type = request.getParameter("type");
        String name = request.getParameter("name");
        int id = parseInt(request.getParameter("id"));
        int result = 0;

        if ("category".equals(type)) {
            CategoryDAO dao = new CategoryDAO();
            if ("create".equals(action)) result = dao.createCategory(name);
            if ("update".equals(action)) result = dao.updateCategory(id, name);
            if ("delete".equals(action)) result = dao.deleteCategory(id);
            if ("toggle".equals(action)) {
                boolean newStatus = "1".equals(request.getParameter("status"));
                result = dao.toggleCategoryStatus(id, newStatus);
            }
        } else if ("brand".equals(type)) {
            BrandDAO dao = new BrandDAO();
            if ("create".equals(action)) result = dao.createBrand(name);
            if ("update".equals(action)) result = dao.updateBrand(id, name);
            if ("delete".equals(action)) result = dao.deleteBrand(id);
            if ("toggle".equals(action)) {
                boolean newStatus = "1".equals(request.getParameter("status"));
                result = dao.toggleBrandStatus(id, newStatus);
            }
        }

        if (result == -1) request.getSession().setAttribute("error", "Cannot delete an item currently used by products.");
        else if (result > 0) request.getSession().setAttribute("success", "Updated successfully.");
        else request.getSession().setAttribute("error", "Action failed or the name already exists.");
        response.sendRedirect(request.getContextPath() + "/admin/catalog" + ("brand".equals(type) ? "?tab=brand" : ""));
    }

    private int parseInt(String value) {
        try { return Integer.parseInt(value); } catch (Exception ex) { return 0; }
    }
}
