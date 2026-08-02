package Controllers;

import DAOs.AddressDAO;
import DAOs.CustomerDAO;
import DAOs.CustomerVoucherDAO;
import DAOs.OrderDAO;
import Models.Customer;
import Models.Order;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ViewProfileServlet", urlPatterns = {"/viewCustomerProfile"})
public class ViewCustomerProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer signedInCustomer = session == null
                ? null : (Customer) session.getAttribute("customer");
        if (signedInCustomer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin?expired=1");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.getCustomerById(signedInCustomer.getId());
        if (customer == null || customer.getIsDeleted() == 1) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/customerLogin?expired=1");
            return;
        }
        session.setAttribute("customer", customer);

        boolean hasLocalPassword = customerDAO.hasLocalPassword(customer.getId());
        session.setAttribute("hasLocalPassword", hasLocalPassword);
        request.setAttribute("hasLocalPassword", hasLocalPassword);

        List<Order> orders = new OrderDAO().getAllOrderOfCustomer(customer.getId());
        if (orders == null) {
            orders = Collections.emptyList();
        }
        request.setAttribute("orderCount", orders.size());
        request.setAttribute("recentOrders",
                orders.subList(0, Math.min(3, orders.size())));
        request.setAttribute("voucherCount",
                new CustomerVoucherDAO().getVoucherOfCustomer(customer.getId()).size());
        request.setAttribute("addressCount",
                new AddressDAO().getAddress(customer.getId()).size());

        request.setAttribute("profilePage", "CustomerProfileView.jsp");
        request.getRequestDispatcher("/ProfileManagementView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
