<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<fmt:setLocale value="en_US"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Voucher Details | SMARTTICK</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>
<body class="admin-ops-page voucher-admin-page">
    <jsp:include page="SidebarDashboard.jsp"/>

    <main class="content">
        <jsp:include page="HeaderDashboard.jsp"/>

        <div class="admin-page-title">
            <div>
                <h1>Voucher Details</h1>
                <p>Review the complete promotion conditions.</p>
            </div>
            <div class="voucher-heading-actions">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/UpdateVoucherServlet?voucherID=${voucher.voucherId}">
                    <i class="fa-solid fa-pen-to-square"></i> Update
                </a>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ViewVoucherListServlet">
                    <i class="fa-solid fa-arrow-left"></i> Back
                </a>
            </div>
        </div>

        <section class="panel voucher-detail-card">
            <dl class="voucher-detail-grid">
                <div><dt>Voucher ID</dt><dd>#${voucher.voucherId}</dd></div>
                <div><dt>Voucher Code</dt><dd><c:out value="${voucher.voucherCode}"/></dd></div>
                <div><dt>Type</dt><dd><c:out value="${voucher.type}"/></dd></div>
                <div><dt>Value</dt><dd><fmt:formatNumber value="${voucher.value}" minFractionDigits="0" maxFractionDigits="2"/></dd></div>
                <div>
                    <dt>Max Discount</dt>
                    <dd>
                        <c:choose>
                            <c:when test="${voucher.type eq 'PERCENT'}">
                                <fmt:formatNumber value="${voucher.maxDiscount}" minFractionDigits="0" maxFractionDigits="2"/>
                            </c:when>
                            <c:otherwise>Not used for FIXED vouchers</c:otherwise>
                        </c:choose>
                    </dd>
                </div>
                <div><dt>Min Order Value</dt><dd><fmt:formatNumber value="${voucher.minOrderValue}" minFractionDigits="0" maxFractionDigits="2"/></dd></div>
                <div class="wide"><dt>End Date</dt><dd><c:out value="${fn:replace(voucher.endDate, 'T', ' ')}"/></dd></div>
            </dl>
        </section>
    </main>
</body>
</html>
