package Controllers;

import DAOs.CustomerDAO;
import DAOs.OrderDAO;
import Models.Customer;
import Models.Email;
import Models.EmailUtils;
import Models.Order;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CustomerListServlet extends HttpServlet {

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
        HttpSession session = request.getSession();
        // Lấy thông báo từ session (nếu có) và remove luôn để không lặp lại
        String message = (String) session.getAttribute("message");
        if (message != null) {
            request.setAttribute("message", message);
            session.removeAttribute("message");
        }

        CustomerDAO pr = new CustomerDAO();
        ArrayList<Customer> customers;

        String detailID = request.getParameter("id");
        String deleteID = request.getParameter("Blocked");
        String restoreID = request.getParameter("Activate");
        String keyword = request.getParameter("txt");

        if (deleteID != null) {
            int id = Integer.parseInt(deleteID);
            // Retrieve customer details before blocking
            Customer customer = pr.getCustomerById(id);
            if (customer != null) {
                pr.blockCustomer(id);
                sendStatusChangeNotificationEmail(customer, true); // true -> Blocked
                // Set thông báo vào session
                session.setAttribute("message", "Customer " + customer.getFullName() + " has been blocked");
            }
            response.sendRedirect("CustomerListServlet");
            return;
        }
        if (restoreID != null) {
            int id = Integer.parseInt(restoreID);
            Customer customer = pr.getCustomerById(id);
            if (customer != null) {
                pr.restoreCustomer(id);
                sendStatusChangeNotificationEmail(customer, false); // false -> Activated
                session.setAttribute("message", "Customer " + customer.getFullName() + " has been activated");
            }
            response.sendRedirect("CustomerListServlet");
            return;
        }
        if (detailID != null) {
            int id = Integer.parseInt(detailID);
            Customer customer = pr.getCustomerById(id);
            // Giả sử bạn có OrderDAO để lấy lịch sử mua hàng
            OrderDAO orderDAO = new OrderDAO();
            List<Order> orders = orderDAO.getAllOrderOfCustomer(id);
            request.setAttribute("customer", customer);
            request.setAttribute("purchaseHistory", orders);

            request.getRequestDispatcher("CustomerDetailView.jsp").forward(request, response);
            return;
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            customers = (ArrayList<Customer>) pr.searchCustomerByName(keyword);
        } else {
            customers = pr.getCustomerList();
        }

        try {
            request.setAttribute("customers", customers);
            request.getRequestDispatcher("ManageCustomerView.jsp").forward(request, response);
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    private void sendStatusChangeNotificationEmail(Customer customer, boolean isBlocked) {
        try {
            Email email = new Email();
            email.setTo(customer.getEmail()); // Customer email

            // Subject and message customization based on action
            if (isBlocked) {
                email.setSubject("Account Suspension Notice");

                StringBuilder sb = new StringBuilder();
                sb.append("Dear ").append(customer.getFullName()).append(",<br><br>");
                sb.append("We regret to inform you that your account has been <b>blocked</b> due to a violation of our platform policies.<br>");
                sb.append("This action was taken to ensure the security and integrity of our system.<br><br>");
                sb.append("<b>Reason for Blocking:</b> Suspicious or inappropriate activity detected.<br>");
                sb.append("<b>Account ID:</b> ").append(customer.getId()).append("<br>");
                sb.append("<b>Email:</b> ").append(customer.getEmail()).append("<br><br>");
                sb.append("If you believe this action was taken in error, please contact our support team.<br>");
                sb.append("<b>Support Email:</b> support@smarttick.vn<br><br>");
                sb.append("Thank you for your understanding.<br>");
                sb.append("Best Regards,<br>");
                sb.append("<b>SMARTTICK Team</b>");

                email.setContent(sb.toString());
            } else {
                email.setSubject("Account Reinstated Successfully");

                StringBuilder sb = new StringBuilder();
                sb.append("Dear ").append(customer.getFullName()).append(",<br><br>");
                sb.append("Good news! Your account has been <b>reactivated</b> and you can now access our platform as usual.<br><br>");
                sb.append("We appreciate your cooperation and look forward to serving you.<br>");
                sb.append("<b>Account ID:</b> ").append(customer.getId()).append("<br>");
                sb.append("<b>Email:</b> ").append(customer.getEmail()).append("<br><br>");
                sb.append("If you need further assistance, please contact our support team.<br>");
                sb.append("<b>Support Email:</b> support@smarttick.vn<br><br>");
                sb.append("Thank you for being a valued member of <b>SMARTTICK</b>.<br>");
                sb.append("Best Regards,<br>");
                sb.append("<b>SMARTTICK Team</b>");

                email.setContent(sb.toString());
            }

            // Send the email
            EmailUtils.send(email);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        processRequest(request, response);
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
