/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.OrderDAO;
import java.util.List;
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

public class DeleteOrderServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String monthStr = request.getParameter("delete");
        int month = 0;
        try {
            month = Integer.parseInt(monthStr);
        } catch (NumberFormatException e) {

            month = 6;
        }

        OrderDAO orderDAO = new OrderDAO();
        List<Order> ordersToDelete = orderDAO.getOrdersToDelete(month);
        int count = ordersToDelete.size();
       
        request.setAttribute("ordersToDelete", ordersToDelete);
        request.setAttribute("deleteMonth", month);

        request.getRequestDispatcher("PreviewDeleteOrder.jsp").forward(request, response);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    response.setContentType("text/plain");
    try (PrintWriter out = response.getWriter()) {
        OrderDAO oDAO = new OrderDAO();
        String month = request.getParameter("delete");

        int count = oDAO.deleteOrder(Integer.parseInt(month));
        if (count > 0) {
            out.print("success");
        } else {
            out.print("fail");
        }
    } catch (NumberFormatException e) {
        response.getWriter().print("fail");
    }
}


    private void sendCancellationEmail(Customer customer, String orderID, List<OrderDetail> orderItems) {
        try {
            Email email = new Email();
            email.setTo(customer.getEmail()); // Customer email
            email.setSubject("Order Cancellation Notification - Order #" + orderID);

            StringBuilder sb = new StringBuilder();
            sb.append("Dear ").append(customer.getFullName()).append(",<br><br>");
            sb.append("We regret to inform you that your order <b>#").append(orderID)
                    .append("</b> has been canceled. Below are the details:<br>");
            sb.append("<b>Reason:</b> Order cancellation requested or inventory issue.<br><br>");
            sb.append("<b>Order Details:</b><br>");
            sb.append("<table border='1' cellspacing='0' cellpadding='5'>");
            sb.append("<tr><th>Product</th><th>Quantity</th><th>Price</th><th>Total</th></tr>");

            double totalRefund = 0;
            for (OrderDetail item : orderItems) {
                double itemTotal = item.getQuantity() * item.getPrice();
                totalRefund += itemTotal;
                sb.append("<tr>")
                        .append("<td>").append(item.getProductName()).append("</td>")
                        .append("<td>").append(item.getQuantity()).append("</td>")
                        .append("<td>").append(String.format("%.02f", (double) item.getPrice())).append(" VND</td>")
                        .append("<td>").append(String.format("%.02f", itemTotal)).append(" VND</td>")
                        .append("</tr>");
            }

            sb.append("</table><br>");
            sb.append("<b>Total Refund Amount:</b> ").append(String.format("%.02f", totalRefund)).append(" VND<br>");
            sb.append("If you have already made a payment, the refund will be processed shortly.<br><br>");
            sb.append("For any further assistance, please contact our support team.<br><br>");
            sb.append("Best Regards,<br>");
            sb.append("<b>SMARTTICK</b>");

            email.setContent(sb.toString());

            // Send the email
            EmailUtils.send(email);

        } catch (Exception e) {
            e.printStackTrace();
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
