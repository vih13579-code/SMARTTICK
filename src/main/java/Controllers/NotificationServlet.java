package Controllers;

import DAOs.RatingRepliesDAO;
import Models.Customer;
import Models.CustomerNotification;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class NotificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin?expired=1");
            return;
        }

        List<CustomerNotification> notifications
                = new RatingRepliesDAO().getCustomerNotifications(customer.getId());
        int unreadCount = 0;
        for (CustomerNotification notification : notifications) {
            if (!notification.isRead()) {
                unreadCount++;
            }
        }

        if ("true".equalsIgnoreCase(request.getParameter("ajax"))) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(notifications));
            return;
        }

        request.setAttribute("notifications", notifications);
        request.setAttribute("notificationCount", notifications.size());
        request.setAttribute("unreadCount", unreadCount);
        request.getRequestDispatcher("/notification.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin?expired=1");
            return;
        }

        try {
            int replyId = Integer.parseInt(request.getParameter("repliesID"));
            boolean updated = new RatingRepliesDAO().markReplyAsRead(replyId, customer.getId());
            if (!updated) {
                session.setAttribute("notificationError",
                        "The notification was not found or does not belong to your account.");
            }
        } catch (NumberFormatException ex) {
            session.setAttribute("notificationError", "Invalid notification.");
        }

        response.sendRedirect(request.getContextPath() + "/NotificationServlet");
    }

    @Override
    public String getServletInfo() {
        return "Customer review reply notifications";
    }
}
