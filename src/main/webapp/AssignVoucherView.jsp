<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Assign Voucher | SMARTTICK</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>
<body class="admin-ops-page voucher-admin-page">
    <jsp:include page="SidebarDashboard.jsp"/>

    <main class="content">
        <jsp:include page="HeaderDashboard.jsp"/>

        <div class="admin-page-title">
            <div>
                <h1>Assign Voucher</h1>
                <p>Assign a saved promotion to <c:out value="${customer.fullName}"/>.</p>
            </div>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/CustomerListServlet">
                <i class="fa-solid fa-arrow-left"></i> Back
            </a>
        </div>

        <c:if test="${not empty error}">
            <div class="alert alert-danger"><c:out value="${error}"/></div>
        </c:if>

        <section class="voucher-form-wrapper">
            <form action="${pageContext.request.contextPath}/AssignVoucherServlet" method="post">
                <input type="hidden" name="customerID" value="${customer.id}">
                <div class="voucher-form-grid">
                    <div class="voucher-field wide">
                        <label class="form-label" for="voucherID">Voucher</label>
                        <select class="form-select" id="voucherID" name="voucherID" required>
                            <c:forEach items="${vouchers}" var="voucher">
                                <option value="${voucher.voucherId}">
                                    <c:out value="${voucher.voucherCode}"/> — <c:out value="${voucher.type}"/>
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${empty vouchers}">
                            <div class="field-error">No unexpired voucher is available.</div>
                        </c:if>
                    </div>
                    <div class="voucher-field">
                        <label class="form-label" for="quantity">Quantity</label>
                        <input class="form-control" id="quantity" type="number" name="quantity"
                               value="1" min="1" max="2" step="1" required>
                    </div>
                    <div class="voucher-field">
                        <label class="form-label" for="expirationDate">Customer Expiration Date</label>
                        <input class="form-control" id="expirationDate" type="datetime-local"
                               name="expirationDate">
                        <small class="field-hint">Optional; cannot exceed the voucher End Date.</small>
                    </div>
                </div>
                <div class="voucher-form-actions">
                    <button class="btn btn-primary" type="submit" ${empty vouchers ? 'disabled' : ''}>
                        <i class="fa-solid fa-ticket"></i> Assign Voucher
                    </button>
                </div>
            </form>
        </section>
    </main>
</body>
</html>
