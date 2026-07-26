package Controllers;

import Configs.VnpayConfig;
import DAOs.PaymentDAO;
import DAOs.PaymentDAO.PaymentValidationException;
import Models.Customer;
import Models.Payment;
import Utils.VnpayUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "VnpayCreatePaymentServlet", urlPatterns = {"/api/payments/vnpay/create"})
public class VnpayCreatePaymentServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(VnpayCreatePaymentServlet.class.getName());
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNPAY_DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Gson GSON = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "AUTH_REQUIRED",
                    "Please sign in before paying.");
            return;
        }

        Integer orderId = readOrderId(request);
        if (orderId == null || orderId <= 0) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "INVALID_ORDER_ID",
                    "orderId must be a positive integer.");
            return;
        }

        try {
            VnpayConfig config = VnpayConfig.getInstance(getServletContext());
            config.validateSandboxConfiguration();

            LocalDateTime requestedCreateDate = LocalDateTime.now(VIETNAM_ZONE);
            LocalDateTime requestedExpireDate = requestedCreateDate.plusMinutes(config.getExpireMinutes());
            String transactionRef = createTransactionRef(orderId, requestedCreateDate);

            PaymentDAO paymentDAO = new PaymentDAO();
            Payment payment = paymentDAO.createOrReusePendingPayment(
                    orderId,
                    customer.getId(),
                    transactionRef,
                    Timestamp.valueOf(requestedCreateDate),
                    Timestamp.valueOf(requestedExpireDate));

            LocalDateTime createDate = payment.getCreatedAt().toLocalDateTime();
            LocalDateTime expireDate = payment.getExpiresAt().toLocalDateTime();
            long vnpayAmount = Math.multiplyExact(payment.getAmount(), 100L);

            Map<String, String> fields = new LinkedHashMap<>();
            fields.put("vnp_Version", config.getVersion());
            fields.put("vnp_Command", config.getCommand());
            fields.put("vnp_TmnCode", config.getTmnCode());
            fields.put("vnp_Amount", String.valueOf(vnpayAmount));
            fields.put("vnp_CurrCode", config.getCurrCode());
            fields.put("vnp_TxnRef", payment.getTransactionRef());
            fields.put("vnp_OrderInfo", "Thanh toan don hang SMARTTICK " + orderId);
            fields.put("vnp_OrderType", "other");
            fields.put("vnp_Locale", "vn");
            fields.put("vnp_ReturnUrl", config.getReturnUrl());
            fields.put("vnp_IpAddr", VnpayUtil.getClientIp(request));
            fields.put("vnp_CreateDate", VNPAY_DATE.format(createDate));
            fields.put("vnp_ExpireDate", VNPAY_DATE.format(expireDate));
            if (!config.getBankCode().isEmpty()) {
                fields.put("vnp_BankCode", config.getBankCode());
            }

            String paymentUrl = VnpayUtil.buildPaymentUrl(
                    config.getPaymentUrl(), fields, config.getHashSecret());
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("success", true);
            body.put("orderId", orderId);
            body.put("transactionRef", payment.getTransactionRef());
            body.put("paymentUrl", paymentUrl);
            response.getWriter().write(GSON.toJson(body));
            LOGGER.log(Level.INFO, "Created VNPAY Sandbox URL for order {0}, transaction {1}",
                    new Object[]{orderId, payment.getTransactionRef()});
        } catch (PaymentValidationException ex) {
            int status = "ORDER_NOT_FOUND".equals(ex.getCode())
                    ? HttpServletResponse.SC_NOT_FOUND : HttpServletResponse.SC_CONFLICT;
            writeError(response, status, ex.getCode(), ex.getMessage());
        } catch (ArithmeticException ex) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "INVALID_AMOUNT",
                    "Order amount is outside the supported range.");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Cannot create pending VNPAY payment for order " + orderId, ex);
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DATABASE_ERROR",
                    "Cannot create payment at this time.");
        } catch (IllegalStateException ex) {
            LOGGER.log(Level.SEVERE, "VNPAY Sandbox configuration is invalid: {0}", ex.getMessage());
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "CONFIGURATION_ERROR",
                    ex.getMessage());
        }
    }

    private Integer readOrderId(HttpServletRequest request) throws IOException {
        String rawOrderId = request.getParameter("orderId");
        String contentType = request.getContentType();
        if ((rawOrderId == null || rawOrderId.trim().isEmpty())
                && contentType != null && contentType.toLowerCase().contains("application/json")) {
            JsonObject body = JsonParser.parseReader(request.getReader()).getAsJsonObject();
            if (body.has("orderId") && !body.get("orderId").isJsonNull()) {
                rawOrderId = body.get("orderId").getAsString();
            }
        }
        if (rawOrderId == null || !rawOrderId.trim().matches("\\d{1,10}")) {
            return null;
        }
        try {
            return Integer.valueOf(rawOrderId.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String createTransactionRef(int orderId, LocalDateTime createdAt) {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "ST" + orderId + VNPAY_DATE.format(createdAt) + random;
    }

    private void writeError(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("code", code);
        body.put("message", message);
        response.getWriter().write(GSON.toJson(body));
    }
}
