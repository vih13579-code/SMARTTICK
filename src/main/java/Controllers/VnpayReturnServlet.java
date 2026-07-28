package Controllers;

import Configs.VnpayConfig;
import DAOs.PaymentDAO;
import Models.Payment;
import Utils.VnpayUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "VnpayReturnServlet", urlPatterns = {"/api/payments/vnpay/return"})
public class VnpayReturnServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(VnpayReturnServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean signatureValid = false;
        Payment payment = null;
        String responseCode = request.getParameter("vnp_ResponseCode");
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        String transactionRef = request.getParameter("vnp_TxnRef");

        try {
            VnpayConfig config = VnpayConfig.getInstance(getServletContext());
            config.validateSandboxConfiguration();
            Map<String, String> fields = VnpayUtil.extractParameters(request);
            signatureValid = VnpayUtil.verifySecureHash(
                    config.getHashSecret(), fields, request.getParameter("vnp_SecureHash"))
                    && config.getTmnCode().equals(request.getParameter("vnp_TmnCode"));
            if (signatureValid && transactionRef != null && transactionRef.length() <= 100) {
                payment = new PaymentDAO().findByTransactionRef(transactionRef);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot read payment returned by VNPAY", ex);
        } catch (IllegalStateException ex) {
            LOGGER.log(Level.SEVERE, "VNPAY Sandbox configuration is invalid: {0}", ex.getMessage());
        }

        request.setAttribute("signatureValid", signatureValid);
        request.setAttribute("responseCode", responseCode);
        request.setAttribute("transactionStatus", transactionStatus);
        request.setAttribute("transactionRef", transactionRef);
        request.setAttribute("orderId", payment == null ? null : payment.getOrderId());
        request.getRequestDispatcher("/VnpayResultView.jsp").forward(request, response);
    }
}
