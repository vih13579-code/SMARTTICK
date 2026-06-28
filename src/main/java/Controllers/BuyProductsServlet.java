package Controllers;

import DAOs.CustomerVoucherDAO;
import Models.Customer;
import Models.CustomerVoucher;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "buyProductsServlet", urlPatterns = {"/order"})
/**
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class BuyProductsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect("CustomerLoginView.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("ConfirmView.jsp");
            return;
        }

        if (action.equalsIgnoreCase("cancelVoucher")) {
            session.removeAttribute("customerVoucherUsing");
            session.removeAttribute("discount");
            response.sendRedirect("ConfirmView.jsp");
            return;
        }

        if (action.equalsIgnoreCase("useVoucher")) {
            applyVoucher(request, response, session, customer);
            return;
        }

        response.sendRedirect("ConfirmView.jsp");
    }

    private void applyVoucher(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Customer customer) throws IOException {
        int voucherId;
        try {
            voucherId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            session.setAttribute("message", "Voucher is not available.");
            response.sendRedirect("ConfirmView.jsp");
            return;
        }

        CustomerVoucher customerVoucherUsing =
                new CustomerVoucherDAO().getVoucherById(customer.getId(), voucherId);
        if (customerVoucherUsing == null || customerVoucherUsing.getQuantity() <= 0) {
            session.setAttribute("message", "Voucher is not available.");
            response.sendRedirect("ConfirmView.jsp");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = parseDateTime(customerVoucherUsing.getStartDate());
        LocalDateTime endDate = parseDateTime(customerVoucherUsing.getEndDate());
        LocalDateTime expirationDate = parseDateTime(customerVoucherUsing.getExpirationDate());
        if (customerVoucherUsing.getStatus() != 1
                || (startDate != null && now.isBefore(startDate))
                || (endDate != null && now.isAfter(endDate))
                || (expirationDate != null && now.isAfter(expirationDate))) {
            session.setAttribute("message", "Voucher has expired or is not active.");
            response.sendRedirect("ConfirmView.jsp");
            return;
        }

        if (customerVoucherUsing.getMaxUsedCount() > 0
                && customerVoucherUsing.getUsedCount() >= customerVoucherUsing.getMaxUsedCount()) {
            session.setAttribute("message", "Voucher usage limit has been reached.");
            response.sendRedirect("ConfirmView.jsp");
            return;
        }

        Object totalObject = session.getAttribute("totalAmount");
        long totalAmount = totalObject instanceof Number ? ((Number) totalObject).longValue() : 0L;
        if (totalAmount < customerVoucherUsing.getMinOrderValue()) {
            session.setAttribute("message", "Your order is not equal or greater than "
                    + customerVoucherUsing.getMinOrderValue() + " VND. You cannot use this voucher.");
            response.sendRedirect("ConfirmView.jsp");
            return;
        }

        int discount = 0;
        if (customerVoucherUsing.getVoucherType() == 1) {
            discount = (int) Math.round(totalAmount * (customerVoucherUsing.getVoucherValue() / 100.0));
            if (customerVoucherUsing.getMaxDiscountAmount() > 0
                    && discount > customerVoucherUsing.getMaxDiscountAmount()) {
                discount = customerVoucherUsing.getMaxDiscountAmount();
            }
        } else if (customerVoucherUsing.getVoucherType() == 0) {
            discount = customerVoucherUsing.getVoucherValue();
        }
        if (discount > totalAmount) {
            discount = (int) totalAmount;
        }

        session.setAttribute("discount", discount);
        session.setAttribute("customerVoucherUsing", customerVoucherUsing);
        response.sendRedirect("ConfirmView.jsp");
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String normalized = value.trim().replace('T', ' ');
        if (normalized.length() > 19) {
            normalized = normalized.substring(0, 19);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            if (normalized.length() == 10) {
                normalized += " 00:00:00";
            } else if (normalized.length() == 16) {
                normalized += ":00";
            }
            return LocalDateTime.parse(normalized, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
