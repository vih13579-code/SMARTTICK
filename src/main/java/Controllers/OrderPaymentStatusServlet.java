package Controllers;

import DAOs.PaymentDAO;
import DAOs.PaymentDAO.PaymentStatusView;
import Models.Customer;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "OrderPaymentStatusServlet", urlPatterns = {"/api/payments/order/status"})
public class OrderPaymentStatusServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(OrderPaymentStatusServlet.class.getName());
    private static final Gson GSON = new Gson();
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required.");
            return;
        }

        String orderIdText = request.getParameter("orderId");
        if (orderIdText == null || !orderIdText.trim().matches("\\d{1,10}")) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid orderId.");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdText.trim());
            PaymentStatusView status = new PaymentDAO().getOrderPaymentStatus(orderId, customer.getId());
            if (status == null) {
                writeError(response, HttpServletResponse.SC_NOT_FOUND, "Order not found.");
                return;
            }

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", true);
            body.put("orderId", status.getOrderId());
            body.put("paymentStatus", normalizeStatus(status));
            body.put("providerStatus", status.getProviderStatus());
            body.put("transactionRef", status.getTransactionRef());
            body.put("transactionNo", status.getTransactionNo());
            body.put("paidAt", status.getPaidAt() == null
                    ? null : ISO_DATE.format(status.getPaidAt().toLocalDateTime()));
            response.getWriter().write(GSON.toJson(body));
        } catch (NumberFormatException ex) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid orderId.");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot read order payment status", ex);
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Cannot read payment status.");
        }
    }

    private String normalizeStatus(PaymentStatusView status) {
        if (status.getProviderStatus() != null && !status.getProviderStatus().trim().isEmpty()) {
            return status.getProviderStatus().toUpperCase();
        }
        if (status.getPaymentStatus() == null || status.getPaymentStatus().trim().isEmpty()) {
            return PaymentDAO.STATUS_PENDING;
        }
        return status.getPaymentStatus().toUpperCase();
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", message);
        response.getWriter().write(GSON.toJson(body));
    }
}
