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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/popup.css">
    <title>Checkout Confirmation | SMARTTICK</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<main class="container checkout-page">
    <c:set var="subtotal" value="0"/>
    <c:set var="totalQuantity" value="0"/>
    <c:forEach items="${sessionScope.cartSelected}" var="p">
        <c:set var="subtotal" value="${subtotal + (p.getPrice() * p.getQuantity())}"/>
        <c:set var="totalQuantity" value="${totalQuantity + p.getQuantity()}"/>
    </c:forEach>
    <c:set var="discountValue" value="${empty sessionScope.discount ? 0 : sessionScope.discount}"/>
    <c:set var="finalTotal" value="${subtotal - discountValue < 0 ? 0 : subtotal - discountValue}"/>
    <c:set var="depositAmount" value="${totalQuantity >= 6 ? finalTotal * 30 / 100 : 0}"/>
    <c:set var="amountDue" value="${finalTotal - depositAmount}"/>

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

            <div class="totals">
                <div><span>Subtotal</span><b><fmt:formatNumber value="${subtotal}" type="currency"/></b></div>
                <div><span>Discount</span><b><fmt:formatNumber value="${discountValue}" type="currency"/></b></div>
                <c:if test="${depositAmount > 0}">
                    <div><span>Deposit 30%</span><b><fmt:formatNumber value="${depositAmount}" type="currency"/></b></div>
                    <div><span>Remaining</span><b><fmt:formatNumber value="${amountDue}" type="currency"/></b></div>
                </c:if>
                <div class="grand-total"><span>Total</span><b><fmt:formatNumber value="${finalTotal}" type="currency"/></b></div>
            </div>

            <form id="checkoutPaymentForm" class="payment-box" action="${pageContext.request.contextPath}/order" method="POST">
                <h3>Payment</h3>
                <label class="payment-option">
                    <input type="radio" name="paymentMethod" value="cod" ${depositAmount > 0 ? '' : 'checked'}>
                    <span>Cash on delivery</span>
                </label>
                <label class="payment-option">
                    <input type="radio" name="paymentMethod" value="vnpay_qr" ${depositAmount > 0 ? 'checked' : ''}>
                    <span>VNPAY QR</span>
                </label>
                <div id="vnpaySandboxInfo" class="vnpay-sandbox-info">
                    <img src="https://cdn.haitrieu.com/wp-content/uploads/2022/10/Logo-VNPAY-QR-1.png" alt="VNPAY QR" style="width: 100%; max-height: 48px; object-fit: contain;">
                    <div>
                        <strong><fmt:formatNumber value="${depositAmount > 0 ? depositAmount : finalTotal}" type="currency"/></strong>
                    </div>
                </div>
                <input type="hidden" name="buyProductAction" value="placeOrder">
                <div id="paymentError" class="alert alert-danger" hidden></div>
                <div class="payment-actions">
                    <button id="btnPlaceOrder" class="btn btn-primary" type="submit">Place Order</button>
                    <button id="btnVnpayPay" class="btn btn-primary" type="submit">
                        Thanh toán bằng VNPAY QR
                    </button>
                    <button class="btn btn-outline" type="button" data-bs-toggle="modal" data-bs-target="#cancelPaymentModal">Cancel Payment</button>
                </div>
            </form>
        </aside>
    </div>
</main>

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
                            <p><c:out value="${vou.getDescription()}"/></p>
                            <span>x${vou.getQuantity()}</span>
                        </div>
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/order?action=useVoucher&id=${vou.getVoucherID()}">Select</a>
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
    const paymentForm = document.getElementById('checkoutPaymentForm');
    const codButton = document.getElementById('btnPlaceOrder');
    const vnpayButton = document.getElementById('btnVnpayPay');
    const vnpayInfo = document.getElementById('vnpaySandboxInfo');
    const paymentError = document.getElementById('paymentError');
    const pendingOrderStorageKey = 'smarttickVnpayPendingOrderId';
    const navigationEntry = performance.getEntriesByType
        ? performance.getEntriesByType('navigation')[0] : null;
    if (!navigationEntry || navigationEntry.type !== 'back_forward') {
        sessionStorage.removeItem(pendingOrderStorageKey);
    }

    function selectedPaymentMethod() {
        const selected = paymentForm.querySelector('input[name="paymentMethod"]:checked');
        return selected ? selected.value : 'cod';
    }

    function updatePaymentControls() {
        const isVnpay = selectedPaymentMethod() === 'vnpay_qr';
        codButton.hidden = isVnpay;
        vnpayButton.hidden = !isVnpay;
        vnpayInfo.hidden = !isVnpay;
    }

    paymentForm.querySelectorAll('input[name="paymentMethod"]').forEach(function (input) {
        input.addEventListener('change', updatePaymentControls);
    });

    paymentForm.addEventListener('submit', async function (event) {
        if (selectedPaymentMethod() !== 'vnpay_qr') {
            return;
        }
        event.preventDefault();
        paymentError.hidden = true;
        vnpayButton.disabled = true;
        vnpayButton.textContent = 'Đang tạo giao dịch...';

        try {
            let orderId = sessionStorage.getItem(pendingOrderStorageKey);
            if (!orderId) {
                const orderData = new URLSearchParams(new FormData(paymentForm));
                orderData.set('buyProductAction', 'placeOrder');
                orderData.set('paymentMethod', 'vnpay_qr');
                orderData.set('vnpayCheckout', 'true');
                const orderResponse = await fetch(paymentForm.action, {
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: orderData.toString(),
                    credentials: 'same-origin'
                });
                const orderResult = await readJsonResponse(orderResponse);
                if (!orderResponse.ok || !orderResult.success) {
                    throw new Error(orderResult.message || 'Không thể tạo đơn hàng.');
                }
                orderId = String(orderResult.orderId);
                sessionStorage.setItem(pendingOrderStorageKey, orderId);
            }

            const createBody = new URLSearchParams();
            createBody.set('orderId', orderId);
            const paymentResponse = await fetch(contextPath + '/api/payments/vnpay/create', {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
                },
                body: createBody.toString(),
                credentials: 'same-origin'
            });
            const paymentResult = await readJsonResponse(paymentResponse);
            if (!paymentResponse.ok || !paymentResult.success || !paymentResult.paymentUrl) {
                if (paymentResult.code === 'ORDER_NOT_FOUND'
                        || paymentResult.code === 'ORDER_PAID') {
                    sessionStorage.removeItem(pendingOrderStorageKey);
                }
                throw new Error(paymentResult.message || 'Không thể tạo URL thanh toán VNPAY.');
            }
            window.location.assign(paymentResult.paymentUrl);
        } catch (error) {
            paymentError.textContent = error.message;
            paymentError.hidden = false;
            vnpayButton.disabled = false;
            vnpayButton.textContent = 'Thử lại thanh toán VNPAY';
        }
    });

    async function readJsonResponse(response) {
        const responseText = await response.text();
        try {
            return JSON.parse(responseText);
        } catch (error) {
            console.error("JSON parse failed. Response text:", responseText);
            if (response.redirected && response.url.indexOf('/customerLogin') !== -1) {
                throw new Error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
            }
            const snippet = responseText.substring(0, 150);
            throw new Error(
                'Máy chủ trả về dữ liệu không hợp lệ (HTTP ' + response.status + '). '
                + 'Nội dung phản hồi: ' + snippet
            );
        }
    }

    updatePaymentControls();

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
