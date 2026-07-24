package Controllers;

import DAOs.CartDAO;
import DAOs.ProductDAO;
import Models.Cart;
import Models.Customer;
import Models.Product;
import Models.ProductVariant;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AddToCartServlet", urlPatterns = {"/AddToCart"})
public class AddToCartServlet extends HttpServlet {
    private static final int MAX_CART_QUANTITY = 100;

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
        Integer variantId = parseOptionalInt(request.getParameter("variantId"));
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
        ProductVariant variant = productDao.getProductVariant(id, variantId);
        int availableStock = variant == null ? (product == null ? 0 : product.getStock()) : variant.getStock();
        if (product == null || product.getDeleted() != 0 || availableStock <= 0 || (variantId != null && variant == null)) {
            session.setAttribute("message", "This product is not available.");
            response.sendRedirect("ProductDetailServlet?id=" + id);
            return;
        }

        CartDAO cartDao = new CartDAO();
        Cart cartCheck = cartDao.getProductOfCart(cus.getId(), id, variantId);
        if (cartCheck != null) {
            updateExistingCartItem(response, session, cus, cartDao, cartCheck, id, variantId, quantity, availableStock);
        } else {
            addNewCartItem(response, session, cus, cartDao, id, variantId, quantity, availableStock);
        }
    }

    private int parseQuantity(String quantityParam) {
        if (quantityParam == null || quantityParam.trim().isEmpty()) {
            return 1;
        }
        return Integer.parseInt(quantityParam);
    }

    private Integer parseOptionalInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void updateExistingCartItem(HttpServletResponse response, HttpSession session,
            Customer cus, CartDAO cartDao, Cart cartCheck, int id, Integer variantId, int quantity, int availableStock)
            throws IOException {
        int totalQuantity = cartCheck.getQuantity() + quantity;

        if (totalQuantity > MAX_CART_QUANTITY) {
            session.setAttribute("message", "Please contact SMARTTICK for orders over " + MAX_CART_QUANTITY + " units.");
            response.sendRedirect("ProductDetailServlet?id=" + id);
        } else if (availableStock >= totalQuantity) {
            cartDao.updateProductQuantity(cartCheck.getProductID(), variantId, totalQuantity, cus.getId());
            response.sendRedirect("cart");
        } else {
            session.setAttribute("message", "Sorry, the product quantity in stock is not enough.");
            response.sendRedirect("ProductDetailServlet?id=" + id);
        }
    }

    private void addNewCartItem(HttpServletResponse response, HttpSession session, Customer cus,
            CartDAO cartDao, int id, Integer variantId, int quantity, int availableStock) throws IOException {
        if (quantity > MAX_CART_QUANTITY) {
            session.setAttribute("message", "Please contact SMARTTICK for orders over " + MAX_CART_QUANTITY + " units.");
            response.sendRedirect("ProductDetailServlet?id=" + id);
        } else if (availableStock < quantity) {
            session.setAttribute("message", "Sorry, the product quantity in stock is not enough.");
            response.sendRedirect("ProductDetailServlet?id=" + id);
        } else {
            Cart cart = new Cart(id, quantity);
            cart.setVariantId(variantId);
            cartDao.addToCart(cus.getId(), cart);
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
