package Controllers;
/**
 * @author LamVH
 */

import DAOs.AttributeDAO;
import DAOs.BrandDAO;
import DAOs.CategoryDAO;
import DAOs.ProductDAO;
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
public class UpdateProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("id"));
            Product product = new ProductDAO().getProductByID(productId);
            if (product == null) {
                request.getSession().setAttribute("error", "Watch not found.");
                response.sendRedirect(request.getContextPath() + "/ProductListServlet");
                return;
            }
            prepareForm(request, product);
            request.getRequestDispatcher("UpdateProductView.jsp").forward(request, response);
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/ProductListServlet");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ProductDAO productDAO = new ProductDAO();
        int id;
        long price;
        int stock;
        int isDeleted;
        try {
            id = Integer.parseInt(request.getParameter("id"));
            price = Long.parseLong(request.getParameter("price"));
            stock = Integer.parseInt(request.getParameter("stock"));
            isDeleted = Integer.parseInt(request.getParameter("isDeleted"));
        } catch (NumberFormatException ex) {
            request.getSession().setAttribute("error", "Invalid product data.");
            response.sendRedirect(request.getContextPath() + "/ProductListServlet");
            return;
        }

        Product current = productDAO.getProductByID(id);
        if (current == null) {
            response.sendRedirect(request.getContextPath() + "/ProductListServlet");
            return;
        }

        String fullName = trim(request.getParameter("fullName"));
        String model = trim(request.getParameter("model"));
        String description = trim(request.getParameter("description"));
        String categoryName = trim(request.getParameter("categoryName"));
        String brandName = trim(request.getParameter("brandName"));

        if (fullName.isEmpty() || model.isEmpty() || description.isEmpty() || categoryName.isEmpty() || brandName.isEmpty()
                || price <= 0 || stock < 0) {
            current.setFullName(fullName);
            current.setModel(model);
            current.setDescription(description);
            current.setCategoryName(categoryName);
            current.setBrandName(brandName);
            current.setPrice(price);
            current.setStock(stock);
            showError(request, response, current, "Required fields are missing, price must be positive and stock cannot be negative.");
            return;
        }
        if (productDAO.checkDuplicateProduct(fullName, model, id)) {
            showError(request, response, current, "Another product already uses this name or model.");
            return;
        }

        List<String> newlyUploaded = new ArrayList<>();
        try {
            String mainImage = replacement(request.getPart("txtPPic"), current.getImage(), newlyUploaded);
            String image1 = replacement(request.getPart("txtPPic1"), current.getImage1(), newlyUploaded);
            String image2 = replacement(request.getPart("txtPPic2"), current.getImage2(), newlyUploaded);
            String image3 = replacement(request.getPart("txtPPic3"), current.getImage3(), newlyUploaded);

            Product updated = new Product();
            updated.setProductId(id);
            updated.setFullName(fullName);
            updated.setModel(model);
            updated.setDescription(description);
            updated.setPrice(price);
            updated.setStock(stock);
            updated.setDeleted(isDeleted);
            updated.setCategoryName(categoryName);
            updated.setBrandName(brandName);
            updated.setImage(mainImage);
            updated.setImage1(image1);
            updated.setImage2(image2);
            updated.setImage3(image3);

            String[] attributeIds = request.getParameterValues("attributeId");
            if (attributeIds != null) {
                List<AttributeDetail> details = new ArrayList<>();
                for (String value : attributeIds) {
                    int attributeId = Integer.parseInt(value);
                    details.add(new AttributeDetail(attributeId, id,
                            trim(request.getParameter("attributeInfor_" + attributeId)), null));
                }
                updated.setAttributeDetails(details);
            }

            if (productDAO.updateProduct(updated) <= 0) throw new IOException("Database update failed.");

            deleteReplaced(current.getImage(), mainImage);
            deleteReplaced(current.getImage1(), image1);
            deleteReplaced(current.getImage2(), image2);
            deleteReplaced(current.getImage3(), image3);
            request.getSession().setAttribute("success", "Watch updated successfully.");
            response.sendRedirect(request.getContextPath() + "/ProductListServlet");
        } catch (Exception ex) {
            for (String fileName : newlyUploaded) ProductImageStorage.deleteQuietly(fileName);
            showError(request, response, current, ex.getMessage() == null ? "Update failed." : ex.getMessage());
        }
    }

    private String replacement(Part part, String current, List<String> newlyUploaded) throws IOException {
        if (part == null || part.getSize() == 0) return current;
        String fileName = ProductImageStorage.save(part, false);
        if (fileName != null) newlyUploaded.add(fileName);
        return fileName == null ? current : fileName;
    }

    private void deleteReplaced(String oldName, String newName) {
        if (oldName != null && newName != null && !oldName.equals(newName)) ProductImageStorage.deleteQuietly(oldName);
    }

    private void prepareForm(HttpServletRequest request, Product product) {
        request.setAttribute("product", product);
        request.setAttribute("categories", new CategoryDAO().getAllCategoryNames());
        request.setAttribute("brands", new BrandDAO().getAllBrandName());
        request.setAttribute("attributes", new AttributeDAO().getAttributesByCategoryID(
                new CategoryDAO().getCategoryIdByName(product.getCategoryName())));
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, Product product, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        prepareForm(request, product);
        request.getRequestDispatcher("UpdateProductView.jsp").forward(request, response);
    }

    private String trim(String value) { return value == null ? "" : value.trim(); }
}
