package Controllers;

import Models.Customer;
import Utils.QrPaymentStore;
import Utils.QrPaymentStore.Payment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "QrPaymentServlet", urlPatterns = {"/qr-payment/*"})
public class QrPaymentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if ("/start".equals(request.getPathInfo())) {
            startPayment(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/qr".equals(path)) {
            renderQr(request, response);
        } else if ("/confirm".equals(path)) {
            confirmPayment(request, response);
        } else if ("/status".equals(path)) {
            paymentStatus(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void startPayment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null || session.getAttribute("order") == null
                || session.getAttribute("cartSelected") == null) {
            json(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "{\"ok\":false,\"message\":\"Please sign in and confirm the order first.\"}");
            return;
        }

        long totalAmount = number(session.getAttribute("totalAmount"));
        long discount = number(session.getAttribute("discount"));
        long amount = Math.max(0L, totalAmount - discount);
        String oldToken = (String) session.getAttribute("qrPaymentToken");
        QrPaymentStore.remove(oldToken);

        String baseUrl = resolvePublicBaseUrl(request);
        Payment payment = QrPaymentStore.create(customer.getId(), amount,
                baseUrl + "/qr-payment/confirm?token=");
        session.setAttribute("qrPaymentToken", payment.getToken());

        String body = "{\"ok\":true,\"token\":\"" + payment.getToken()
                + "\",\"qrUrl\":\"" + request.getContextPath()
                + "/qr-payment/qr?token=" + payment.getToken()
                + "\",\"confirmationUrl\":\"" + escapeJson(payment.getConfirmationUrl())
                + "\",\"expiresInSeconds\":600}";
        json(response, HttpServletResponse.SC_OK, body);
    }

    private void renderQr(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Payment payment = QrPaymentStore.get(request.getParameter("token"));
        if (payment == null) {
            response.sendError(HttpServletResponse.SC_GONE, "Payment QR expired.");
            return;
        }
        try {
            BitMatrix matrix = new QRCodeWriter().encode(payment.getConfirmationUrl(),
                    BarcodeFormat.QR_CODE, 320, 320);
            response.setContentType("image/png");
            response.setHeader("Cache-Control", "no-store");
            MatrixToImageWriter.writeToStream(matrix, "PNG", response.getOutputStream());
        } catch (WriterException ex) {
            throw new IOException("Cannot create payment QR.", ex);
        }
    }

    private void confirmPayment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getParameter("token");
        Payment payment = QrPaymentStore.get(token);
        boolean confirmed = payment != null && QrPaymentStore.markPaid(token);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(phoneResultPage(confirmed, payment));
    }

    private void paymentStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        String token = request.getParameter("token");
        String sessionToken = session == null ? null : (String) session.getAttribute("qrPaymentToken");
        if (customer == null || token == null || !token.equals(sessionToken)) {
            // Return a normal JSON response so an expired/stale polling request never
            // sends the browser to the application's HTML 403 error page.
            json(response, HttpServletResponse.SC_OK, "{\"status\":\"INVALID\"}");
            return;
        }
        Payment payment = QrPaymentStore.get(token);
        String status = payment == null ? "EXPIRED" : (payment.isPaid() ? "PAID" : "PENDING");
        json(response, HttpServletResponse.SC_OK, "{\"status\":\"" + status + "\"}");
    }

    private String resolvePublicBaseUrl(HttpServletRequest request) {
        String configured = System.getenv("SMARTTICK_QR_PAYMENT_BASE_URL");
        if (configured != null && !configured.trim().isEmpty()) {
            return trimSlash(configured.trim());
        }
        String host = findLanAddress();
        if (host == null) {
            host = request.getServerName();
        }
        int port = request.getServerPort();
        String portPart = port == 80 ? "" : ":" + port;
        return request.getScheme() + "://" + host + portPart + request.getContextPath();
    }

    private String findLanAddress() {
        String fallback = null;
        try {
            for (NetworkInterface network : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                String name = (network.getDisplayName() + " " + network.getName()).toLowerCase(Locale.ROOT);
                if (!network.isUp() || network.isLoopback() || network.isVirtual()
                        || name.contains("virtual") || name.contains("vmware")
                        || name.contains("docker") || name.contains("wsl")) {
                    continue;
                }
                Enumeration<InetAddress> addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && address.isSiteLocalAddress()) {
                        String hostAddress = address.getHostAddress();
                        if (name.contains("wi-fi") || name.contains("wifi") || name.contains("wlan")) {
                            return hostAddress;
                        }
                        if (fallback == null) {
                            fallback = hostAddress;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // The configured environment variable or request host remains available as fallback.
        }
        return fallback;
    }

    private String phoneResultPage(boolean confirmed, Payment payment) {
        String title = confirmed ? "Payment confirmed" : "QR expired";
        String detail = confirmed
                ? "SMARTTICK received the payment confirmation. You may return to the computer."
                : "This payment link is invalid, expired, or already used.";
        String amount = payment == null ? "" : NumberFormat.getNumberInstance(new Locale("vi", "VN"))
                .format(payment.getAmount()) + " VND";
        return "<!doctype html><html><head><meta charset='UTF-8'><meta name='viewport' "
                + "content='width=device-width,initial-scale=1'><title>SMARTTICK QR Payment</title>"
                + "<style>body{margin:0;background:#07111f;color:#fff;font-family:Arial;display:grid;"
                + "place-items:center;min-height:100vh}.card{width:min(88%,420px);background:#101d2f;"
                + "padding:32px;border-radius:20px;text-align:center;box-shadow:0 20px 60px #0008}"
                + ".icon{font-size:64px;color:" + (confirmed ? "#39d98a" : "#ff6b6b") + "}"
                + "h1{font-size:26px}p{color:#bed0e5;line-height:1.6}.amount{font-size:22px;"
                + "font-weight:bold;color:#f4c86b}</style></head><body><main class='card'>"
                + "<div class='icon'>" + (confirmed ? "&#10003;" : "&#10007;") + "</div><h1>"
                + title + "</h1><div class='amount'>" + amount + "</div><p>" + detail
                + "</p><small>SMARTTICK QR PAYMENT</small></main></body></html>";
    }

    private long number(Object value) {
        return value instanceof Number ? ((Number) value).longValue() : 0L;
    }

    private String trimSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void json(HttpServletResponse response, int status, String body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write(body);
    }
}
