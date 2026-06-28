<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ConfirmView - SMARTTICK Skeleton</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body>
    <main class="container">
        <h1>ConfirmView</h1>
        <c:if test="${not empty sessionScope.message}">
            <p class="alert">${sessionScope.message}</p>
            <c:remove var="message" scope="session"/>
        </c:if>

        <section>
            <h2>Apply Voucher</h2>
            <p>Total amount:
                <strong><fmt:formatNumber value="${sessionScope.totalAmount}" pattern="#,##0"/> VND</strong>
            </p>
            <c:choose>
                <c:when test="${not empty sessionScope.customerVoucherUsing}">
                    <p>Selected voucher: <strong>${sessionScope.customerVoucherUsing.voucherCode}</strong></p>
                    <p>Discount:
                        <strong><fmt:formatNumber value="${sessionScope.discount}" pattern="#,##0"/> VND</strong>
                    </p>
                    <p><a href="${pageContext.request.contextPath}/order?action=cancelVoucher">Cancel voucher</a></p>
                </c:when>
                <c:otherwise>
                    <c:if test="${empty sessionScope.customerVoucher}">
                        <p>No vouchers available.</p>
                    </c:if>
                    <c:forEach var="voucher" items="${sessionScope.customerVoucher}">
                        <p>
                            <strong>${voucher.voucherCode}</strong>
                            -
                            <c:choose>
                                <c:when test="${voucher.voucherType == 1}">
                                    ${voucher.voucherValue}% off
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${voucher.voucherValue}" pattern="#,##0"/> VND off
                                </c:otherwise>
                            </c:choose>
                            <a href="${pageContext.request.contextPath}/order?action=useVoucher&id=${voucher.voucherID}">Apply</a>
                        </p>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </section>
        <p><a href="${pageContext.request.contextPath}/">Về trang chủ</a></p>
    </main>
</body>
</html>
