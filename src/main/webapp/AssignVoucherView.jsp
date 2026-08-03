<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Assign Voucher | SMARTTICK</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/flatpickr.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/themes/dark.css">
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

        <c:if test="${param.success eq 'assigned'}">
            <div class="alert alert-success">Voucher assigned successfully.</div>
        </c:if>
        <c:if test="${param.success eq 'unassigned'}">
            <div class="alert alert-success">Voucher unassigned successfully.</div>
        </c:if>
        <c:if test="${param.success eq 'failed' or param.success eq 'unassignfailed'}">
            <div class="alert alert-danger">The voucher assignment could not be updated.</div>
        </c:if>
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

        <section class="voucher-assigned-wrapper">
            <div class="voucher-assigned-heading">
                <div>
                    <h2>Assigned Vouchers</h2>
                    <p>These vouchers are currently attached to this customer account.</p>
                </div>
            </div>
            <div class="table-container voucher-table-wrap">
                <table class="table voucher-table">
                    <thead>
                        <tr>
                            <th>Code</th>
                            <th>Type</th>
                            <th>Quantity</th>
                            <th>Customer Expiration</th>
                            <th>Voucher End Date</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${assignedVouchers}" var="assignedVoucher">
                            <tr>
                                <td><strong><c:out value="${assignedVoucher.voucherCode}"/></strong></td>
                                <td><c:out value="${assignedVoucher.type}"/></td>
                                <td>${assignedVoucher.quantity}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty assignedVoucher.expirationDate}">Voucher End Date</c:when>
                                        <c:otherwise>
                                            <c:out value="${fn:replace(assignedVoucher.expirationDate, 'T', ' ')}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${fn:replace(assignedVoucher.endDate, 'T', ' ')}"/></td>
                                <td>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/UnassignVoucherServlet"
                                          onsubmit="return confirm('Unassign this voucher from the customer?');">
                                        <input type="hidden" name="customerID" value="${customer.id}">
                                        <input type="hidden" name="voucherID" value="${assignedVoucher.voucherId}">
                                        <button class="btn btn-danger btn-sm" type="submit">
                                            <i class="fa-solid fa-link-slash"></i> Unassign
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty assignedVouchers}">
                            <tr>
                                <td class="empty-state" colspan="6">No voucher is assigned to this customer.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/flatpickr.min.js"></script>
    <script>
        flatpickr("#expirationDate", {
            enableTime: true,
            dateFormat: "Y-m-d\\TH:i",
            altInput: true,
            altFormat: "m/d/Y h:i K",
            disableMobile: true
        });
    </script>
</body>
</html>
