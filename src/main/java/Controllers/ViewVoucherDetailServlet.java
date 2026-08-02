package Controllers;

import DAOs.VoucherDAO;
import Models.Voucher;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewVoucherDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rawId = request.getParameter("voucherID");
        if (rawId == null || !rawId.trim().matches("[1-9][0-9]*")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int voucherId;
        try {
            voucherId = Integer.parseInt(rawId.trim());
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Voucher voucher = new VoucherDAO().getVoucher(voucherId);
        if (voucher == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("voucher", voucher);
        request.getRequestDispatcher("/VoucherDetailView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}
