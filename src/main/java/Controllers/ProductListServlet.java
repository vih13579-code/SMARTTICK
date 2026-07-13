package Controllers;

import DAOs.AttributeDAO;
import DAOs.CategoryDAO;
import DAOs.ProductDAO;
import Models.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author LamVH
 */
public class ProductListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDAO dao = new ProductDAO();
        String deleteId = request.getParameter("delete");
        String restoreId = request.getParameter("restore");
        String detailId = request.getParameter("id");

        if (deleteId != null) {
            Product product = dao.getProductByID(parseInt(deleteId));
            int changed = product == null ? 0 : dao.deleteProduct(product.getProductId());
            request.getSession().setAttribute(changed > 0 ? "success" : "error",
                    changed > 0 ? "Watch hidden successfully." : "Watch not found.");
            response.sendRedirect(request.getContextPath() + "/ProductListServlet");
            return;
        }
        if (restoreId != null) {
            Product product = dao.getProductByID(parseInt(restoreId));
            int changed = product == null ? 0 : dao.restoreProduct(product.getProductId());
            request.getSession().setAttribute(changed > 0 ? "success" : "error",
                    changed > 0 ? "Watch restored successfully." : "Watch not found.");
            response.sendRedirect(request.getContextPath() + "/ProductListServlet");
            return;
        }
        if (detailId != null) {
            Product product = dao.getProductByID(parseInt(detailId));
            if (product == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            product.setAttributeDetails(new AttributeDAO().getAttributesByProductID(product.getProductId()));
            request.setAttribute("product", product);
            request.getRequestDispatcher("ProductDetailManagerView.jsp").forward(request, response);
            return;
        }

        ArrayList<Product> products;
        String categoryId = request.getParameter("categoryId");
        String keyword = request.getParameter("txt");
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            products = new ArrayList<>(dao.getProductsByCategory(parseInt(categoryId)));
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            products = new ArrayList<>(dao.searchProductByName(keyword));
        } else if ("true".equals(request.getParameter("new_import"))) {
            products = new ArrayList<>(dao.getNewImportedProducts());
        } else {
            products = dao.getProductList();
        }

        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");
        Comparator<Product> comparator;
        if ("Price".equals(sortBy)) comparator = Comparator.comparingLong(Product::getPrice);
        else if ("FullName".equals(sortBy)) comparator = Comparator.comparing(Product::getFullName, String.CASE_INSENSITIVE_ORDER);
        else if ("Stock".equals(sortBy)) comparator = Comparator.comparingInt(Product::getStock);
        else comparator = Comparator.comparingInt(Product::getDeleted).thenComparing(Product::getProductId).reversed();
        if ("desc".equalsIgnoreCase(order)) comparator = comparator.reversed();
        products.sort(comparator);

        request.setAttribute("categories", new CategoryDAO().getAllCategories());
        request.setAttribute("products", products);
        request.getRequestDispatcher("ManageProductView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private int parseInt(String value) {
        try { return Integer.parseInt(value); } catch (Exception ex) { return -1; }
    }
}
