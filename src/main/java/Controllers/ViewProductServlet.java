package Controllers;
/**
 * @author LamVH
 */

import DAOs.BrandDAO;
import DAOs.CategoryDAO;
import DAOs.ProductDAO;
import Models.Category;
import Models.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ViewProductServlet", urlPatterns = {
    "/ViewProduct", "/Watches", "/Men", "/Women", "/Sport", "/Mechanical", "/Quartz"
})
public class ViewProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ProductDAO productDAO = new ProductDAO();
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategories();
        String servletPath = request.getServletPath();
        String routedCategory = categoryForRoute(servletPath, categories);

        String keyword = trim(request.getParameter("q"));
        String category = routedCategory != null ? routedCategory : trimToNull(request.getParameter("category"));
        String brand = trimToNull(request.getParameter("brand"));
        Long minPrice = parseLong(request.getParameter("minPrice"));
        Long maxPrice = parseLong(request.getParameter("maxPrice"));
        String sort = trimToNull(request.getParameter("sort"));

        ArrayList<Product> products = productDAO.searchCatalog(keyword, category, brand, minPrice, maxPrice, sort);
        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("brands", new BrandDAO().getAllBrand());
        request.setAttribute("selectedCategory", category);
        request.setAttribute("selectedBrand", brand);
        request.setAttribute("keyword", keyword);
        request.setAttribute("minPrice", minPrice);
        request.setAttribute("maxPrice", maxPrice);
        request.setAttribute("sort", sort);

        boolean home = "/".equals(servletPath) || servletPath == null || servletPath.isEmpty();
        if (home) {
            request.setAttribute("featuredProducts", products.subList(0, Math.min(8, products.size())));
            request.getRequestDispatcher("HomeView.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("ProductListView.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private String categoryForRoute(String route, List<Category> categories) {
        if ("/Men".equals(route)) return firstExistingCategory(categories, "Men's Watches", "Men Watches", "Men", "\u0110\u1ed3ng h\u1ed3 nam");
        if ("/Women".equals(route)) return firstExistingCategory(categories, "Women's Watches", "Women Watches", "Women", "\u0110\u1ed3ng h\u1ed3 n\u1eef");
        if ("/Sport".equals(route)) return firstExistingCategory(categories, "Sports Watches", "Sports", "Sport Watches", "\u0110\u1ed3ng h\u1ed3 th\u1ec3 thao");
        if ("/Mechanical".equals(route)) return firstExistingCategory(categories, "Mechanical Watches", "Mechanical", "\u0110\u1ed3ng h\u1ed3 c\u01a1");
        if ("/Quartz".equals(route)) return firstExistingCategory(categories, "Quartz Watches", "Quartz", "\u0110\u1ed3ng h\u1ed3 quartz", "\u0110\u1ed3ng h\u1ed3 pin");
        return null;
    }

    private String firstExistingCategory(List<Category> categories, String... aliases) {
        if (categories != null) {
            for (String alias : aliases) {
                for (Category category : categories) {
                    if (category.getName() != null && category.getName().equalsIgnoreCase(alias)) {
                        return category.getName();
                    }
                }
            }
        }
        return aliases.length == 0 ? null : aliases[0];
    }

    private Long parseLong(String value) {
        try {
            return value == null || value.trim().isEmpty() ? null : Long.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String trim(String value) { return value == null ? "" : value.trim(); }
    private String trimToNull(String value) {
        String trimmed = trim(value);
        return trimmed.isEmpty() ? null : trimmed;
    }
}
