package Controllers;

import DAOs.OrderDAO;
import DAOs.ProductDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDAO productDAO = new ProductDAO();
        try {
            request.setAttribute("stats", productDAO.getDashboardStats());
            request.setAttribute("newProducts", productDAO.getNewProducts());
            request.setAttribute("newCustomers", productDAO.getNewCustomers());
            request.setAttribute("recentOrders", new OrderDAO().getOrderList());
            request.getRequestDispatcher("/ShopManagerDashboardView.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("Cannot load SMARTTICK dashboard", ex);
        }
    }
}
