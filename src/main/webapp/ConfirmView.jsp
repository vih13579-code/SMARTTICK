<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=voucher-20260729">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/popup.css?v=qr-payment-1">
    <title>Checkout Confirmation | SMARTTICK</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<main class="container checkout-page">
    <c:set var="subtotal" value="0"/>
    <c:forEach items="${sessionScope.cartSelected}" var="p">
        <c:set var="subtotal" value="${subtotal + (p.getPrice() * p.getQuantity())}"/>
    </c:forEach>
    <c:set var="discountValue" value="${empty sessionScope.discount ? 0 : sessionScope.discount}"/>
    <c:set var="finalTotal" value="${subtotal - discountValue < 0 ? 0 : subtotal - discountValue}"/>

    <div class="checkout-head">
        <div>
            <span class="eyebrow">Checkout</span>
            <h1>Confirmation</h1>
        </div>
        <a class="btn btn-outline" href="${pageContext.request.contextPath}/cart">Back to Cart</a>
    </div>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-danger"><c:out value="${sessionScope.message}"/></div>
    </c:if>

    <div class="checkout-grid">
        <section class="panel checkout-panel">
            <h2>Shipping Address</h2>
            <div class="summary-list">
                <div><strong>Email</strong><span><c:out value="${sessionScope.customer.email}"/></span></div>
                <div><strong>Full name</strong><span><c:out value="${sessionScope.order.fullName}"/></span></div>
                <div><strong>Phone</strong><span><c:out value="${sessionScope.order.phone}"/></span></div>
                <div><strong>Address</strong><span><c:out value="${sessionScope.order.address}"/></span></div>
            </div>
        </section>

        <aside class="panel checkout-summary">
            <h2>Order Summary</h2>
            <div class="checkout-items">
                <c:forEach items="${sessionScope.cartSelected}" var="p">
                    <div class="checkout-item">
                        <img src="${pageContext.request.contextPath}/product-images/${p.getImage()}"
                             onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'"
                             alt="SMARTTICK watch">
                        <div>
                            <strong><c:out value="${p.getFullName()}"/></strong>
                            <span>Qty ${p.getQuantity()}</span>
                            <b><fmt:formatNumber value="${p.getPrice() * p.getQuantity()}" type="currency"/></b>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <div class="voucher-row">
                <div>
                    <strong>Voucher</strong>
                    <span>
                        <c:choose>
                            <c:when test="${sessionScope.customerVoucherUsing != null}">
                                <c:out value="${sessionScope.customerVoucherUsing.getVoucherCode()}"/>
                            </c:when>
                            <c:otherwise>No voucher selected</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="voucher-actions">
                    <button type="button" class="link-button" onclick="openVoucherModal()">Select</button>
                    <c:if test="${sessionScope.customerVoucherUsing != null}">
                        <a href="${pageContext.request.contextPath}/order?action=cancelVoucher">Remove</a>
                    </c:if>
                </div>
            </div>
            <form class="voucher-code-form" action="${pageContext.request.contextPath}/order" method="get">
                <input type="hidden" name="action" value="useVoucher">
                <label for="checkoutVoucherCode">Voucher Code</label>
                <div>
                    <input id="checkoutVoucherCode" type="text" name="voucherCode"
                           minlength="3" maxlength="30" pattern="[A-Za-z0-9_-]{3,30}"
                           placeholder="SUMMER20" autocomplete="off" required
                           oninput="this.value=this.value.toUpperCase()">
                    <button class="btn btn-outline" type="submit">Apply</button>
                </div>
            </form>

            <div class="totals">
                <div><span>Subtotal</span><b><fmt:formatNumber value="${subtotal}" type="currency"/></b></div>
                <div><span>Discount</span><b><fmt:formatNumber value="${discountValue}" type="currency"/></b></div>
                <div class="grand-total"><span>Total</span><b><fmt:formatNumber value="${finalTotal}" type="currency"/></b></div>
            </div>

            <form id="placeOrderForm" class="payment-box" action="${pageContext.request.contextPath}/order" method="POST">
                <h3>QR Payment</h3>
                <p class="section-sub">Scan with a phone connected to the same Wi-Fi to confirm payment.</p>
                <input id="paymentToken" name="paymentToken" type="hidden" value="">
                <input type="hidden" name="buyProductAction" value="placeOrder">
                <div class="payment-actions">
                    <button id="startPaymentButton" class="btn btn-primary" type="button"
                            onclick="startQrPayment()">Pay by QR</button>
                    <button class="btn btn-outline" type="button" data-bs-toggle="modal" data-bs-target="#cancelPaymentModal">Cancel Payment</button>
                </div>
            </form>
        </aside>
    </div>
</main>

<div id="qrPaymentOverlay" class="qr-payment-overlay" style="display:none;">
    <section class="qr-payment-card" role="dialog" aria-modal="true" aria-labelledby="qrPaymentTitle">
        <button type="button" class="qr-payment-close" onclick="closeQrPayment()" aria-label="Close">&times;</button>
        <span class="eyebrow">SMARTTICK QR Payment</span>
        <h2 id="qrPaymentTitle">Scan to confirm payment</h2>
        <p>Use your phone camera to scan this QR code.</p>
        <img id="qrPaymentQr" alt="Payment QR code" width="300" height="300"
             onload="handleQrLoaded()" onerror="handleQrError()">
        <strong class="qr-payment-amount"><fmt:formatNumber value="${finalTotal}" type="currency"/></strong>
        <div id="qrPaymentStatus" class="qr-payment-status">Creating secure QR code...</div>
        <p class="qr-payment-help">
            Use a phone on the same Wi-Fi. The order is created automatically after scanning.
        </p>
        <details class="qr-payment-address-wrap">
            <summary>QR callback address</summary>
            <div id="qrPaymentAddress"></div>
        </details>
    </section>
</div>

<div class="popup" id="orderPopup" style="display:none;">
    <div class="popup-content">
        <img src="https://cdn-icons-png.flaticon.com/512/845/845646.png" alt="success" style="width:82px;margin-bottom:15px;">
        <h3>Order Successful</h3>
        <p>Your order is waiting for shop acceptance.</p>
        <div class="popup-actions">
            <a class="btn btn-primary" href="odetailforcus?id=${sessionScope.newOrder}">OK</a>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/Watches">Back to shop</a>
        </div>
    </div>
</div>

<div class="modal fade" id="voucherModal" tabindex="-1" aria-labelledby="voucherModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="voucherModalLabel">Select Voucher</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <c:if test="${empty sessionScope.customerVoucher}">
                    <p>No vouchers available.</p>
                </c:if>
                <c:forEach var="vou" items="${sessionScope.customerVoucher}">
                    <div class="voucher-card">
                        <div>
                            <strong><c:out value="${vou.getVoucherCode()}"/></strong>
                            <p>
                                <c:choose>
                                    <c:when test="${vou.type eq 'PERCENT'}">
                                        <fmt:formatNumber value="${vou.value}" pattern="0.##"/>% off, up to
                                        <fmt:formatNumber value="${vou.maxDiscount}" type="currency"/>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${vou.value}" type="currency"/> off
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <span>x${vou.getQuantity()}</span>
                        </div>
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/order?action=useVoucher&id=${vou.voucherId}">Select</a>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="cancelPaymentModal" tabindex="-1" aria-labelledby="cancelPaymentLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="cancelPaymentLabel">Confirm Payment Cancellation</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to cancel this payment?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline" data-bs-dismiss="modal">No</button>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/cart">Yes, Cancel</a>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
<script>
    const contextPath = '${pageContext.request.contextPath}';
    let qrPaymentTimer = null;
    let qrPaymentLoaded = false;

    async function startQrPayment() {
        const button = document.getElementById('startPaymentButton');
        const overlay = document.getElementById('qrPaymentOverlay');
        const status = document.getElementById('qrPaymentStatus');
        button.disabled = true;
        overlay.style.display = 'flex';
        status.className = 'qr-payment-status';
        status.textContent = 'Creating secure QR code...';
        qrPaymentLoaded = false;
        try {
            const response = await fetch(contextPath + '/qr-payment/start', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'same-origin'
            });
            const data = await response.json();
            if (!response.ok || !data.ok) {
                throw new Error(data.message || 'Cannot start QR payment.');
            }
            document.getElementById('paymentToken').value = data.token;
            document.getElementById('qrPaymentQr').src = data.qrUrl + '&t=' + Date.now();
            document.getElementById('qrPaymentAddress').textContent = data.confirmationUrl;
            status.textContent = 'Waiting for QR scan...';
            beginPaymentPolling(data.token);
        } catch (error) {
            status.className = 'qr-payment-status failed';
            status.textContent = error.message;
            button.disabled = false;
        }
    }

    function handleQrLoaded() {
        qrPaymentLoaded = true;
    }

    function handleQrError() {
        if (qrPaymentLoaded) {
            return;
        }
        clearInterval(qrPaymentTimer);
        const status = document.getElementById('qrPaymentStatus');
        status.className = 'qr-payment-status failed';
        status.textContent = 'Cannot load the QR image. Publish the project again, then retry.';
        document.getElementById('startPaymentButton').disabled = false;
    }

    function beginPaymentPolling(token) {
        clearInterval(qrPaymentTimer);
        qrPaymentTimer = setInterval(async function () {
            try {
                const response = await fetch(
                    contextPath + '/qr-payment/status?token=' + encodeURIComponent(token),
                    {cache: 'no-store', credentials: 'same-origin'}
                );
                const data = await response.json();
                if (data.status === 'PAID') {
                    clearInterval(qrPaymentTimer);
                    const status = document.getElementById('qrPaymentStatus');
                    status.className = 'qr-payment-status paid';
                    status.textContent = 'Payment confirmed! Creating your order...';
                    setTimeout(function () {
                        document.getElementById('placeOrderForm').submit();
                    }, 700);
                } else if (data.status === 'EXPIRED' || data.status === 'INVALID') {
                    clearInterval(qrPaymentTimer);
                    const status = document.getElementById('qrPaymentStatus');
                    status.className = 'qr-payment-status failed';
                    status.textContent = 'QR expired. Close and try again.';
                    document.getElementById('startPaymentButton').disabled = false;
                }
            } catch (error) {
                // Keep polling through short local-network interruptions.
            }
        }, 1500);
    }

    function closeQrPayment() {
        clearInterval(qrPaymentTimer);
        document.getElementById('qrPaymentOverlay').style.display = 'none';
        document.getElementById('startPaymentButton').disabled = false;
    }

    function openVoucherModal() {
        new bootstrap.Modal(document.getElementById('voucherModal')).show();
    }
    function showPopup() {
        document.getElementById('orderPopup').style.display = 'flex';
    }
    <%
        String message = (String) session.getAttribute("orderStatus");
        if (message != null && message.equals("success")) {
            out.print("showPopup();");
            session.removeAttribute("orderStatus");
        }
    %>
</script>
</body>
<c:if test="${not empty sessionScope.message}">
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.newOrder}">
    <c:remove var="newOrder" scope="session"/>
</c:if>
</html>
