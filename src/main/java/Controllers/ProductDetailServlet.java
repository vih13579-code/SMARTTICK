package Controllers;

import DAOs.OrderDetailDAO;
import DAOs.ProductDAO;
import DAOs.ProductRatingDAO;
import DAOs.RatingRepliesDAO;
import Models.Customer;
import Models.Product;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author LamVH
 */
public class ProductDetailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = parseInt(request.getParameter("id"));
        Product product = new ProductDAO().getProductByID(id);
        if (product == null || product.getDeleted() == 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ProductRatingDAO ratingDAO = new ProductRatingDAO();
        Customer customer = (Customer) request.getSession().getAttribute("customer");
        boolean canReview = customer != null && new OrderDetailDAO().getCustomerByProductID(customer.getId(), id);
        request.setAttribute("isOk", canReview);
        request.setAttribute("dataRating", ratingDAO.getAllProductRating(id));
        request.setAttribute("dataReplies", new RatingRepliesDAO().getAllRatingRepliesByProduct(id));
        request.setAttribute("star", ratingDAO.getStarAverage(id));
        request.setAttribute("product", product);
        request.setAttribute("attributes", product.getAttributeDetails());
        request.getRequestDispatcher("ProductDetailView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Customer customer = (Customer) request.getSession().getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin");
            return;
        }
        int productId = parseInt(request.getParameter("productId"));
        int star = parseInt(request.getParameter("star"));
        String comment = request.getParameter("comment");
        if (star < 1 || star > 5 || comment == null || comment.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ProductDetailServlet?id=" + productId + "&review=invalid");
            return;
        }
        new ProductRatingDAO().addProductRating(customer.getId(), productId, star, comment.trim());
        response.sendRedirect(request.getContextPath() + "/ProductDetailServlet?id=" + productId + "&review=created");
    }

    private int parseInt(String value) {
        try { return Integer.parseInt(value); } catch (Exception ex) { return -1; }
    }
}
