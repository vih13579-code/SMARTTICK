package Controllers;
/**
 * @author LamVH
 */

import DAOs.AttributeDAO;
import DAOs.BrandDAO;
import DAOs.CategoryDAO;
import DAOs.ProductDAO;
import Models.Attribute;
import Models.AttributeDetail;
import Models.Product;
import Utils.ProductImageStorage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5L * 1024 * 1024, maxRequestSize = 22L * 1024 * 1024)
public class CreateProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        prepareForm(request, request.getParameter("name"));
        request.getRequestDispatcher("CreateProductView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String categoryName = trim(request.getParameter("categoryName"));
        String brandName = trim(request.getParameter("brandName"));
        String model = trim(request.getParameter("model"));
        String fullName = trim(request.getParameter("fullName"));
        String description = trim(request.getParameter("description"));

        long price;
        int stock;
        int isDeleted;
        try {
            price = Long.parseLong(request.getParameter("price"));
            stock = Integer.parseInt(request.getParameter("stock"));
            isDeleted = Integer.parseInt(request.getParameter("isDeleted"));
        } catch (NumberFormatException ex) {
            showError(request, response, categoryName, "Price, stock or product status is invalid.");
            return;
        }

        if (categoryName.isEmpty() || brandName.isEmpty() || model.isEmpty() || fullName.isEmpty() || description.isEmpty()) {
            showError(request, response, categoryName, "Please complete all required fields.");
            return;
        }
        if (price <= 0 || stock < 0) {
            showError(request, response, categoryName, "Price must be greater than 0 and stock cannot be negative.");
            return;
        }

        ProductDAO productDAO = new ProductDAO();
        if (productDAO.isModelExists(model) || productDAO.isFullnameExists(fullName)) {
            showError(request, response, categoryName, "Product name and model must be unique.");
            return;
        }

        List<String> uploaded = new ArrayList<>();
        try {
            String mainImage = ProductImageStorage.save(request.getPart("txtP1"), true);
            String image1 = ProductImageStorage.save(request.getPart("txtP2"), false);
            String image2 = ProductImageStorage.save(request.getPart("txtP3"), false);
            String image3 = ProductImageStorage.save(request.getPart("txtP4"), false);
            uploaded.add(mainImage);
            if (image1 != null) uploaded.add(image1);
            if (image2 != null) uploaded.add(image2);
            if (image3 != null) uploaded.add(image3);

            Product product = new Product(categoryName, brandName, model, fullName, description,
                    isDeleted, price, mainImage, image1, image2, image3);
            product.setStock(stock);
            product.setQuantity(0);
            int productId = productDAO.createProduct(product);
            if (productId <= 0) throw new IOException("Database rejected the product. Verify category and brand data.");

            String[] attributeIds = request.getParameterValues("attributeId");
            if (attributeIds != null) {
                for (String attributeId : attributeIds) {
                    int id = Integer.parseInt(attributeId);
                    String information = trim(request.getParameter("attributeInfor_" + id));
                    productDAO.addAttributeDetail(new AttributeDetail(id, productId, information, null));
                }
            }
            request.getSession().setAttribute("success", "Watch created successfully.");
            response.sendRedirect(request.getContextPath() + "/ProductListServlet");
        } catch (Exception ex) {
            for (String fileName : uploaded) ProductImageStorage.deleteQuietly(fileName);
            showError(request, response, categoryName, ex.getMessage() == null ? "Image upload or product creation failed." : ex.getMessage());
        }
    }

    private void prepareForm(HttpServletRequest request, String selectedCategory) {
        CategoryDAO categoryDAO = new CategoryDAO();
        BrandDAO brandDAO = new BrandDAO();
        request.setAttribute("categories", categoryDAO.getAllCategoryNames());
        request.setAttribute("brands", brandDAO.getAllBrandName());
        request.setAttribute("categoryName", selectedCategory);
        if (selectedCategory != null && !selectedCategory.trim().isEmpty()) {
            int categoryId = categoryDAO.getCategoryIdByName(selectedCategory);
            List<Attribute> attributes = new AttributeDAO().getAttributesByCategoryID(categoryId);
            request.setAttribute("attributes", attributes);
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String categoryName, String message)
            throws ServletException, IOException {
        request.setAttribute("errorMsg", message);
        prepareForm(request, categoryName);
        request.getRequestDispatcher("CreateProductView.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
