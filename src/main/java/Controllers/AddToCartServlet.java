package Controllers;

import DAOs.CartDAO;
import DAOs.ProductDAO;
import Models.Cart;
import Models.Customer;
import Models.Product;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AddToCartServlet", urlPatterns = {"/AddToCart"})
public class AddToCartServlet extends HttpServlet {

    private void addProductToCart(HttpServletRequest request, HttpServletResponse response, boolean allowIdParam)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer cus = (Customer) session.getAttribute("customer");
        if (cus == null) {
            response.sendRedirect("customerLogin");
            return;
        }

        String productIdParam = request.getParameter("productID");
        if (allowIdParam && (productIdParam == null || productIdParam.trim().isEmpty())) {
            productIdParam = request.getParameter("id");
        }

        int id;
        int quantity;
        try {
            id = Integer.parseInt(productIdParam);
            quantity = parseQuantity(request.getParameter("quantity"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (quantity <= 0) {
            session.setAttribute("message", "Invalid quantity.");
            response.sendRedirect("ProductDetailServlet?id=" + id);
            return;
        }

        ProductDAO productDao = new ProductDAO();
        Product product = productDao.getProductByID(id);
        if (product == null || product.getDeleted() != 0 || product.getStock() <= 0) {
            session.setAttribute("message", "This product is not available.");
            response.sendRedirect("ProductDetailServlet?id=" + id);
            return;
        }

        CartDAO cartDao = new CartDAO();
        Cart cartCheck = cartDao.getProductOfCart(cus.getId(), id);
        if (cartCheck != null) {
            updateExistingCartItem(request, response, session, cus, product, cartDao, cartCheck, id, quantity);
        } else {
            addNewCartItem(response, session, cus, product, cartDao, id, quantity);
        }
    }

    private int parseQuantity(String quantityParam) {
        if (quantityParam == null || quantityParam.trim().isEmpty()) {
            return 1;
        }
        return Integer.parseInt(quantityParam);
    }

    private void updateExistingCartItem(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            Customer cus, Product product, CartDAO cartDao, Cart cartCheck, int id, int quantity) throws IOException {
        int totalQuantity = cartCheck.getQuantity() + quantity;

        if (product.getStock() >= totalQuantity) {
            cartDao.updateProductQuantity(cartCheck.getProductID(), totalQuantity, cus.getId());
            response.sendRedirect("cart");
        } else {
            session.setAttribute("message", "Cannot add more. Stock: " + product.getStock()
                    + "; already in cart: " + cartCheck.getQuantity() + ".");
            response.sendRedirect("ProductDetailServlet?id=" + id);
        }
    }

    private void addNewCartItem(HttpServletResponse response, HttpSession session, Customer cus, Product product,
            CartDAO cartDao, int id, int quantity) throws IOException {
        if (product.getStock() < quantity) {
            session.setAttribute("message", "Cannot add " + quantity + " item(s). Current stock: "
                    + product.getStock() + ".");
            response.sendRedirect("ProductDetailServlet?id=" + id);
        } else {
            cartDao.addToCart(cus.getId(), new Cart(id, quantity));
            response.sendRedirect("cart");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        addProductToCart(request, response, true);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        addProductToCart(request, response, false);
    }

    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }
}
