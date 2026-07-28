package Controllers;

import DAOs.AddressDAO;
import DAOs.CustomerVoucherDAO;
import DAOs.OrderDAO;
import DAOs.ProductDAO;
import Models.Customer;
import Models.Order;
import Models.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CustomerDashboardServlet", urlPatterns = {"/customer/dashboard"})
public class CustomerDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin");
            return;
        }

        List<Order> orders = new OrderDAO().getAllOrderOfCustomer(customer.getId());
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.getAllProducts();
        List<Product> purchased = productDAO.getPurchasedProducts(customer.getId());

        request.setAttribute("orders", orders);
        request.setAttribute("orderCount", orders.size());
        request.setAttribute("recentOrders", orders.subList(0, Math.min(5, orders.size())));
        request.setAttribute("voucherCount", new CustomerVoucherDAO().getVoucherOfCustomer(customer.getId()).size());
        request.setAttribute("addressCount", new AddressDAO().getAddress(customer.getId()).size());
        request.setAttribute("purchasedProducts", purchased.subList(0, Math.min(4, purchased.size())));
        request.setAttribute("recommendedProducts", products.subList(0, Math.min(4, products.size())));
        request.getRequestDispatcher("/CustomerDashboardView.jsp").forward(request, response);
    }
}
