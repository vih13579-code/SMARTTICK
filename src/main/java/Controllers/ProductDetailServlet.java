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
import javax.servlet.http.HttpSession;

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
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        boolean canReview = customer != null && new OrderDetailDAO().getCustomerByProductID(customer.getId(), id);
        Object message = session.getAttribute("message");
        if (message != null) {
            request.setAttribute("message", message);
            session.removeAttribute("message");
        }
        Object successMessage = session.getAttribute("successMessage");
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }
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
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin");
            return;
        }
        int productId = parseInt(request.getParameter("productId"));
        Product product = new ProductDAO().getProductByID(productId);
        if (product == null || product.getDeleted() == 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int star = parseInt(request.getParameter("star"));
        String comment = request.getParameter("comment");
        if (star < 1 || star > 5 || comment == null || comment.trim().isEmpty()) {
            session.setAttribute("message", "Please choose a rating and write your review before submitting.");
            response.sendRedirect(request.getContextPath() + "/ProductDetailServlet?id=" + productId);
            return;
        }
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        int orderId = orderDetailDAO.getReviewableOrderId(customer.getId(), productId);
        if (orderId <= 0) {
            session.setAttribute("message", "You can review this product after a delivered order, once per product.");
            response.sendRedirect(request.getContextPath() + "/ProductDetailServlet?id=" + productId);
            return;
        }
        int created = new ProductRatingDAO().addProductRating(customer.getId(), productId, orderId, star, comment.trim());
        if (created > 0) {
            session.setAttribute("successMessage", "Thank you. Your review has been submitted.");
        } else {
            session.setAttribute("message", "Could not submit your review. Please try again.");
        }
        response.sendRedirect(request.getContextPath() + "/ProductDetailServlet?id=" + productId);
    }

    private int parseInt(String value) {
        try { return Integer.parseInt(value); } catch (Exception ex) { return -1; }
    }
}
