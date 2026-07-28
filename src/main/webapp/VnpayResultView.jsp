<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
    <title>Kết quả thanh toán VNPAY | SMARTTICK</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<main class="container payment-result-page"
      data-signature-valid="${signatureValid}"
      data-order-id="${orderId}">
    <section class="payment-result">
        <div id="resultIcon" class="payment-result-icon checking">...</div>
        <span class="eyebrow">VNPAY Sandbox</span>
        <h1 id="resultTitle">Đang xác nhận giao dịch</h1>
        <p id="resultMessage">SMARTTICK đang đọc trạng thái đã được IPN xác nhận từ hệ thống.</p>
        <dl class="payment-result-meta">
            <div><dt>Mã đơn hàng</dt><dd><c:out value="${orderId != null ? orderId : 'Không xác định'}"/></dd></div>
            <div><dt>Mã tham chiếu</dt><dd><c:out value="${transactionRef}"/></dd></div>
            <div><dt>Mã giao dịch</dt><dd id="transactionNo">Đang cập nhật</dd></div>
        </dl>
        <div class="payment-result-actions">
            <c:if test="${orderId != null}">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/odetailforcus?id=${orderId}">
                    Xem đơn hàng
                </a>
            </c:if>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/Watches">Tiếp tục mua sắm</a>
        </div>
    </section>
</main>
<jsp:include page="footer.jsp"/>
<script>
    (function () {
        const page = document.querySelector('.payment-result-page');
        const signatureValid = page.dataset.signatureValid === 'true';
        const orderId = page.dataset.orderId;
        const title = document.getElementById('resultTitle');
        const message = document.getElementById('resultMessage');
        const icon = document.getElementById('resultIcon');
        const transactionNo = document.getElementById('transactionNo');
        let attempts = 0;

        function render(status, data) {
            icon.className = 'payment-result-icon ' + status.toLowerCase();
            if (status === 'PAID') {
                icon.textContent = '✓';
                title.textContent = 'Thanh toán thành công';
                message.textContent = 'IPN hợp lệ đã được xử lý và đơn hàng đã chuyển sang trạng thái PAID.';
            } else if (status === 'CANCELLED') {
                icon.textContent = '×';
                title.textContent = 'Giao dịch đã bị hủy';
                message.textContent = 'Bạn đã hủy giao dịch trên VNPAY Sandbox.';
            } else if (status === 'EXPIRED') {
                icon.textContent = '!';
                title.textContent = 'Giao dịch đã hết hạn';
                message.textContent = 'Phiên thanh toán Sandbox đã hết thời gian hiệu lực.';
            } else if (status === 'FAILED') {
                icon.textContent = '×';
                title.textContent = 'Thanh toán thất bại';
                message.textContent = 'VNPAY đã từ chối hoặc không thể hoàn tất giao dịch thử nghiệm.';
            } else {
                icon.textContent = '...';
                title.textContent = 'Giao dịch đang được xác nhận';
                message.textContent = 'Return URL đã quay về, SMARTTICK đang chờ IPN hợp lệ từ VNPAY.';
            }
            transactionNo.textContent = data && data.transactionNo ? data.transactionNo : 'Chưa có';
            if (status === 'PAID' || status === 'FAILED'
                    || status === 'CANCELLED' || status === 'EXPIRED') {
                sessionStorage.removeItem('smarttickVnpayPendingOrderId');
            }
        }

        async function loadStatus() {
            try {
                const response = await fetch(
                    '${pageContext.request.contextPath}/api/payments/order/status?orderId='
                    + encodeURIComponent(orderId),
                    {credentials: 'same-origin'}
                );
                const data = await response.json();
                if (!response.ok || !data.success) {
                    throw new Error(data.message || 'Không thể đọc trạng thái thanh toán.');
                }
                const status = (data.paymentStatus || 'PENDING').toUpperCase();
                render(status, data);
                if (status === 'PENDING' && attempts < 10) {
                    attempts++;
                    window.setTimeout(loadStatus, 2000);
                }
            } catch (error) {
                icon.textContent = '!';
                icon.className = 'payment-result-icon failed';
                title.textContent = 'Không thể kiểm tra giao dịch';
                message.textContent = error.message;
            }
        }

        if (!signatureValid || !orderId) {
            icon.textContent = '!';
            icon.className = 'payment-result-icon failed';
            title.textContent = 'Phản hồi không hợp lệ';
            message.textContent = 'Chữ ký VNPAY không hợp lệ hoặc không tìm thấy giao dịch tương ứng.';
            transactionNo.textContent = 'Không có';
            return;
        }
        loadStatus();
    }());
</script>
</body>
</html>
