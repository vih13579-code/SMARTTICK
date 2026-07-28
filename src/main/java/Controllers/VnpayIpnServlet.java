package Controllers;

import Configs.VnpayConfig;
import DAOs.PaymentDAO;
import DAOs.PaymentDAO.IpnResult;
import Utils.VnpayUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "VnpayIpnServlet", urlPatterns = {"/api/payments/vnpay/ipn"})
public class VnpayIpnServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(VnpayIpnServlet.class.getName());
    private static final DateTimeFormatter VNPAY_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Gson GSON = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processIpn(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processIpn(request, response);
    }

    private void processIpn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            VnpayConfig config = VnpayConfig.getInstance(getServletContext());
            config.validateSandboxConfiguration();
            Map<String, String> fields = VnpayUtil.extractParameters(request);
            boolean signatureValid = VnpayUtil.verifySecureHash(
                    config.getHashSecret(), fields, request.getParameter("vnp_SecureHash"))
                    && config.getTmnCode().equals(request.getParameter("vnp_TmnCode"));
            if (!signatureValid) {
                writeIpnResponse(response, "97", "Invalid Checksum");
                return;
            }

            String transactionRef = request.getParameter("vnp_TxnRef");
            String amountText = request.getParameter("vnp_Amount");
            String responseCode = request.getParameter("vnp_ResponseCode");
            String transactionStatus = request.getParameter("vnp_TransactionStatus");
            if (transactionRef == null || transactionRef.trim().isEmpty()
                    || transactionRef.length() > 100 || amountText == null
                    || responseCode == null || transactionStatus == null) {
                writeIpnResponse(response, "99", "Invalid request");
                return;
            }

            long signedAmount = Long.parseLong(amountText);
            if (signedAmount <= 0 || signedAmount % 100L != 0) {
                writeIpnResponse(response, "04", "Invalid amount");
                return;
            }

            PaymentDAO paymentDAO = new PaymentDAO();
            IpnResult result = paymentDAO.processIpn(
                    transactionRef,
                    signedAmount / 100L,
                    responseCode,
                    transactionStatus,
                    request.getParameter("vnp_TransactionNo"),
                    request.getParameter("vnp_BankCode"),
                    request.getParameter("vnp_BankTranNo"),
                    parsePayDate(request.getParameter("vnp_PayDate")),
                    GSON.toJson(fields));
            writeIpnResponse(response, result.getResponseCode(), result.getMessage());
            LOGGER.log(Level.INFO, "Processed VNPAY IPN for transaction {0}: {1}",
                    new Object[]{transactionRef, result.getResponseCode()});
        } catch (NumberFormatException ex) {
            writeIpnResponse(response, "04", "Invalid amount");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot process VNPAY IPN", ex);
            writeIpnResponse(response, "99", "Unknown error");
        } catch (IllegalStateException ex) {
            LOGGER.log(Level.SEVERE, "VNPAY Sandbox configuration is invalid: {0}", ex.getMessage());
            writeIpnResponse(response, "99", "Unknown error");
        }
    }

    private Timestamp parsePayDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Timestamp.valueOf(LocalDateTime.parse(value.trim(), VNPAY_DATE));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private void writeIpnResponse(HttpServletResponse response, String code, String message)
            throws IOException {
        String body = "{\"RspCode\":\"" + code + "\",\"Message\":\"" + message + "\"}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }
}
