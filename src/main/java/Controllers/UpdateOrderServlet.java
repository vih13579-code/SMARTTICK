/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.OrderDAO;
import DAOs.OrderDetailDAO;
import Models.Customer;
import Models.Email;
import Models.EmailUtils;
import Models.Order;
import Models.OrderDetail;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class UpdateOrderServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderID = request.getParameter("orderID");
        OrderDAO oDAO = new OrderDAO();
        Order o = oDAO.getOrderByID(orderID);

        OrderDetailDAO odDAO = new OrderDetailDAO();
        List<OrderDetail> list = odDAO.getOrderDetail(orderID);
        if (o.getFullName() != "") {
            request.setAttribute("dataDetail", list);
            request.setAttribute("data", o);
            request.getRequestDispatcher("UpdateOrderView.jsp").forward(request, response);

        } else {
            response.sendRedirect(request.getContextPath() + "/ViewOrderListServlet");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String status = request.getParameter("update");
        String orderID = request.getParameter("orderID");
        try {

            OrderDAO oDAO = new OrderDAO();

            if (status != null && orderID != null) {
                int orderIdInt = Integer.parseInt(orderID);
                int orderStatus = Integer.parseInt(status);
                boolean force = "true".equalsIgnoreCase(request.getParameter("force"));
                int count = force ? oDAO.forceUpdateOrder(orderIdInt, orderStatus)
                        : oDAO.updateOrder(orderIdInt, orderStatus);

                if (count <= 0) {
                    // Update failed (invalid transition or DB issue). Include current and requested
                    // statuses to help admin diagnose the problem.
                    int currentStatus = oDAO.getOrderStatus(orderIdInt);
                    response.sendRedirect(request.getContextPath() + "/UpdateOrderServlet?orderID=" + orderID
                            + "&error=invalid_transition&current=" + currentStatus + "&requested=" + orderStatus);
                    return;
                }

                // Retrieve customer details only after a successful update
                Customer customer = oDAO.getCustomerByOrderId(orderIdInt);
                if (customer == null || customer.getEmail() == null || customer.getEmail().isEmpty()) {
                    System.out.println("ERROR: Customer email is missing!");
                } else {
                    // Fetch order details
                    OrderDetailDAO odDAO = new OrderDetailDAO();
                    List<OrderDetail> orderItems = odDAO.getOrderDetail(orderID);
                    if (orderItems == null || orderItems.isEmpty()) {
                        System.out.println("ERROR: Order items are empty!");
                    } else {
                        // Send order confirmation email
                        sendOrderConfirmationEmail(customer, orderID, orderStatus, orderItems);
                    }
                }

                // Redirect to order view with success
                response.sendRedirect(
                        request.getContextPath() + "/UpdateOrderServlet?orderID=" + orderID + "&success=created");
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/UpdateOrderServlet?orderID=" + orderID + "&error=invalid_request");
            }
        } catch (NumberFormatException e) {
            System.out.println(e);
        }

    }

    private void sendOrderConfirmationEmail(Customer customer, String orderID, int orderStatus,
            List<OrderDetail> orderItems) {
        try {
            Email email = new Email();
            email.setTo(customer.getEmail()); // Customer email
            email.setSubject(" SMARTTICK - Order Update Notification - Order #" + orderID);

            StringBuilder sb = new StringBuilder();
            sb.append("Dear ").append(customer.getFullName()).append(",<br><br>");
            sb.append("Your order <b>#").append(orderID).append("</b> has been updated to: <b>")
                    .append(getOrderStatusText(orderStatus)).append("</b>.<br><br>");
            sb.append("<b>Order Details:</b><br>");
            sb.append("<table border='1' cellspacing='0' cellpadding='5'>");
            sb.append("<tr><th>Product</th><th>Quantity</th><th>Price</th><th>Total</th></tr>");

            double totalAmount = 0;
            for (OrderDetail item : orderItems) {
                double itemTotal = item.getQuantity() * item.getPrice();
                totalAmount += itemTotal;
                sb.append("<tr>")
                        .append("<td>").append(item.getProductName()).append("</td>")
                        .append("<td>").append(item.getQuantity()).append("</td>")
                        .append("<td>").append(String.format("%.02f", (double) item.getPrice())).append(" VND</td>")
                        .append("<td>").append(String.format("%.02f", itemTotal)).append(" VND</td>")
                        .append("</tr>");
            }

            sb.append("</table><br>");
            sb.append("<b>Total Amount:</b> ").append(String.format("%.02f", totalAmount)).append(" VND<br>");
            sb.append("<br>Thank you for choosing <b>SMARTTICK</b>!<br>");
            sb.append("If you have any questions, feel free to contact us.<br><br>");
            sb.append("Best Regards,<br>");
            sb.append("<b>SMARTTICK</b>");

            email.setContent(sb.toString());

            // Send the email
            EmailUtils.send(email);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getOrderStatusText(int status) {
        switch (status) {
            case 1:
                return "Waiting For Acceptance";
            case 2:
                return "Packaging";
            case 3:
                return "Waiting For Delivery";
            case 4:
                return "Delivered";
            case 5:
                return "Cancel";
            default:
                return "Unknown Status";
        }
    }

    /**
     * Returns the servlet description.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }// </editor-fold>

}
