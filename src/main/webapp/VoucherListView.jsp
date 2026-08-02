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
    <title>Vouchers | SMARTTICK</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
</head>
<body class="admin-ops-page voucher-admin-page">
    <jsp:include page="SidebarDashboard.jsp"/>

    <main class="content">
        <jsp:include page="HeaderDashboard.jsp"/>

        <div class="admin-page-title">
            <div>
                <h1>Vouchers</h1>
                <p>Create, search, and manage promotion conditions.</p>
            </div>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/CreateVoucherServlet">
                <i class="fa-solid fa-plus"></i> Create Voucher
            </a>
        </div>

        <c:if test="${param.success eq 'createsuccess'}">
            <div class="alert alert-success">Voucher created successfully.</div>
        </c:if>
        <c:if test="${param.success eq 'updatesuccess'}">
            <div class="alert alert-success">Voucher updated successfully.</div>
        </c:if>
        <c:if test="${param.success eq 'deletesuccess'}">
            <div class="alert alert-success">Voucher deleted successfully.</div>
        </c:if>
        <c:if test="${param.success eq 'createfailed' or param.success eq 'updatefailed' or param.success eq 'deletefailed'}">
            <div class="alert alert-danger">The voucher operation could not be completed.</div>
        </c:if>

        <div class="voucher-toolbar">
            <form class="voucher-search" action="${pageContext.request.contextPath}/SearchVoucherServlet" method="get">
                <input class="form-control" type="search" name="query"
                       value="<c:out value='${searchQuery}'/>" maxlength="30"
                       placeholder="Search by voucher code" aria-label="Search voucher">
                <button class="btn btn-primary" type="submit">
                    <i class="fa-solid fa-magnifying-glass"></i> Search
                </button>
                <c:if test="${not empty searchQuery}">
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ViewVoucherListServlet">Clear</a>
                </c:if>
            </form>
        </div>

        <section class="table-container voucher-table-wrap">
            <table class="table voucher-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Voucher Code</th>
                        <th>Type</th>
                        <th>Value</th>
                        <th>Max Discount</th>
                        <th>Min Order Value</th>
                        <th>End Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${Vouchers}" var="voucher">
                        <tr>
                            <td>#${voucher.voucherId}</td>
                            <td><strong><c:out value="${voucher.voucherCode}"/></strong></td>
                            <td><span class="voucher-type-badge ${voucher.type eq 'PERCENT' ? 'percent' : 'fixed'}">
                                <c:out value="${voucher.type}"/>
                            </span></td>
                            <td><fmt:formatNumber value="${voucher.value}" minFractionDigits="0" maxFractionDigits="2"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${voucher.type eq 'PERCENT'}">
                                        <fmt:formatNumber value="${voucher.maxDiscount}" minFractionDigits="0" maxFractionDigits="2"/>
                                    </c:when>
                                    <c:otherwise>—</c:otherwise>
                                </c:choose>
                            </td>
                            <td><fmt:formatNumber value="${voucher.minOrderValue}" minFractionDigits="0" maxFractionDigits="2"/></td>
                            <td><c:out value="${fn:replace(voucher.endDate, 'T', ' ')}"/></td>
                            <td>
                                <div class="table-actions">
                                    <a href="${pageContext.request.contextPath}/ViewVoucherDetailServlet?voucherID=${voucher.voucherId}">
                                        <i class="fa-solid fa-eye"></i> View
                                    </a>
                                    <a href="${pageContext.request.contextPath}/UpdateVoucherServlet?voucherID=${voucher.voucherId}">
                                        <i class="fa-solid fa-pen-to-square"></i> Update
                                    </a>
                                    <button class="btn btn-danger" type="button" data-bs-toggle="modal"
                                            data-bs-target="#confirmDeleteModal" data-id="${voucher.voucherId}">
                                        <i class="fa-solid fa-trash"></i> Delete
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty Vouchers}">
                        <tr><td class="empty-state" colspan="8">No vouchers found.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </section>
    </main>

    <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <form method="post" action="${pageContext.request.contextPath}/DeleteVoucherServlet">
                <input type="hidden" name="voucherID" id="deleteVoucherId">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 class="modal-title fs-5">Delete Voucher</h2>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">Are you sure you want to delete this voucher?</div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById("confirmDeleteModal").addEventListener("show.bs.modal", function (event) {
            document.getElementById("deleteVoucherId").value =
                    event.relatedTarget.getAttribute("data-id");
        });
    </script>
</body>
</html>
