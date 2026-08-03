package Controllers;

import DAOs.CustomerVoucherDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UnassignVoucherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer customerId = parsePositiveInt(request.getParameter("customerID"));
        Integer voucherId = parsePositiveInt(request.getParameter("voucherID"));
        if (customerId == null || voucherId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int affected = new CustomerVoucherDAO().unassignVoucher(customerId, voucherId);
        response.sendRedirect(request.getContextPath() + "/AssignVoucherServlet?Id="
                + customerId + "&success="
                + (affected > 0 ? "unassigned" : "unassignfailed"));
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
