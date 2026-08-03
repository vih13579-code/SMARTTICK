package Controllers;

import DAOs.CustomerDAO;
import DAOs.CustomerVoucherDAO;
import DAOs.VoucherDAO;
import Models.Customer;
import Models.CustomerVoucher;
import Models.Voucher;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AssignVoucherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer customerId = parsePositiveInt(request.getParameter("Id"));
        if (customerId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!loadForm(request, customerId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.getRequestDispatcher("/AssignVoucherView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer customerId = parsePositiveInt(request.getParameter("customerID"));
        Integer voucherId = parsePositiveInt(request.getParameter("voucherID"));
        Integer quantity = parsePositiveInt(request.getParameter("quantity"));

        if (customerId == null || voucherId == null || quantity == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            if (quantity > 2) {
                throw new IllegalArgumentException("Quantity must be between 1 and 2.");
            }
            LocalDateTime now = LocalDateTime.now();
            Voucher voucher = new VoucherDAO().getVoucher(voucherId);
            if (voucher == null) {
                throw new IllegalArgumentException("Voucher not found.");
            }
            if (voucher.getEndDate() == null || !voucher.getEndDate().isAfter(now)) {
                throw new IllegalArgumentException("Voucher has expired.");
            }

            LocalDateTime expirationDate =
                    parseExpirationDate(request.getParameter("expirationDate"));
            if (expirationDate != null) {
                if (!expirationDate.isAfter(now)) {
                    throw new IllegalArgumentException(
                            "Expiration Date must be after the current time.");
                }
                if (expirationDate.isAfter(voucher.getEndDate())) {
                    throw new IllegalArgumentException(
                            "Expiration Date cannot be after the voucher End Date.");
                }
            }

            CustomerVoucherDAO customerVoucherDAO = new CustomerVoucherDAO();
            int currentQuantity =
                    customerVoucherDAO.getVoucherQuantity(customerId, voucherId);
            if (currentQuantity + quantity > 2) {
                throw new IllegalArgumentException(
                        "A customer can hold at most 2 copies of the same voucher.");
            }

            int affected = customerVoucherDAO.assignVoucherToCustomer(
                    customerId, voucherId, quantity, expirationDate);
            response.sendRedirect(request.getContextPath() + "/AssignVoucherServlet?Id="
                    + customerId + "&success=" + (affected > 0 ? "assigned" : "failed"));
        } catch (DateTimeParseException ex) {
            forwardError(request, response, customerId,
                    "Expiration Date has an invalid format.");
        } catch (IllegalArgumentException ex) {
            forwardError(request, response, customerId, ex.getMessage());
        }
    }

    private boolean loadForm(HttpServletRequest request, int customerId) {
        Customer customer = new CustomerDAO().getCustomerById(customerId);
        if (customer == null) {
            return false;
        }
        CustomerVoucherDAO customerVoucherDAO = new CustomerVoucherDAO();
        List<Voucher> vouchers = new VoucherDAO().getAllVoucherActivate();
        List<CustomerVoucher> assignedVouchers =
                customerVoucherDAO.getAssignedVouchersForCustomer(customerId);
        request.setAttribute("customer", customer);
        request.setAttribute("vouchers", vouchers);
        request.setAttribute("assignedVouchers", assignedVouchers);
        return true;
    }

    private void forwardError(HttpServletRequest request, HttpServletResponse response,
            int customerId, String message) throws ServletException, IOException {
        request.setAttribute("error", message);
        if (!loadForm(request, customerId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.getRequestDispatcher("/AssignVoucherView.jsp").forward(request, response);
    }

    private LocalDateTime parseExpirationDate(String rawDate) {
        if (rawDate == null || rawDate.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(rawDate.trim());
    }

    private Integer parsePositiveInt(String value) {
        if (value == null || !value.trim().matches("[1-9][0-9]*")) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
