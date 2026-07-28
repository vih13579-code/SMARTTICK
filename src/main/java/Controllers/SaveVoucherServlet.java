package Controllers;

import DAOs.CustomerVoucherDAO;
import Models.Customer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SaveVoucherServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin?expired=1");
            return;
        }

        int voucherID;
        try {
            voucherID = Integer.parseInt(request.getParameter("voucherID"));
        } catch (NumberFormatException ex) {
            session.setAttribute("voucherMessage", "Invalid voucher.");
            response.sendRedirect(request.getContextPath() + "/ViewCustomerVoucher");
            return;
        }

        int count = new CustomerVoucherDAO().saveVoucherForCustomer(customer.getId(), voucherID);
        session.setAttribute("voucherMessage", count > 0
                ? "Voucher saved successfully."
                : "This voucher is no longer available or already saved.");
        response.sendRedirect(request.getContextPath() + "/ViewCustomerVoucher");
    }
}
