<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Suppliers | SMARTTICK</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-ops.css?v=20260729-admin-fix">
        <style>
            .supplier-toolbar {
                display: flex;
                align-items: center;
                justify-content: space-between;
                flex-wrap: wrap;
                gap: 12px;
                margin-bottom: 18px;
            }
            .supplier-search {
                display: grid;
                grid-template-columns: minmax(220px, 360px) auto auto;
                gap: 8px;
                width: min(560px, 100%);
            }
            @media (max-width: 620px) {
                .supplier-search {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body class="admin-ops-page">
        <div class="dashboard-shell">
            <jsp:include page="SidebarDashboard.jsp"/>
            <main class="dash-main">
                <jsp:include page="HeaderDashboard.jsp"/>

                <div class="admin-page-title">
                    <div>
                        <h1>Suppliers</h1>
                        <p>Manage supplier contacts and availability for stock imports.</p>
                    </div>
                    <button class="btn btn-primary" type="button" data-bs-toggle="modal"
                            data-bs-target="#createSupplierModal">
                        <i class="ti-plus" aria-hidden="true"></i> Create Supplier
                    </button>
                </div>

                <c:if test="${not empty sessionScope.supplierMessage}">
                    <div class="alert alert-success" role="alert">
                        <c:out value="${sessionScope.supplierMessage}"/>
                    </div>
                    <c:remove var="supplierMessage" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.supplierError}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${sessionScope.supplierError}"/>
                    </div>
                    <c:remove var="supplierError" scope="session"/>
                </c:if>

                <section class="table-container">
                    <div class="supplier-toolbar">
                        <form class="supplier-search" method="get"
                              action="${pageContext.request.contextPath}/SearchSupplier">
                            <input class="form-control" type="search" name="name"
                                   value="<c:out value='${searchValue}'/>"
                                   placeholder="Search name, Tax ID, or email">
                            <button class="btn btn-primary" type="submit">
                                <i class="ti-search" aria-hidden="true"></i> Search
                            </button>
                            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/Supplier">
                                <i class="ti-reload" aria-hidden="true"></i> Reset
                            </a>
                        </form>
                        <span class="text-muted">${suppliers.size()} supplier(s)</span>
                    </div>

                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Tax ID</th>
                                    <th>Company Name</th>
                                    <th>Phone</th>
                                    <th>Email</th>
                                    <th>Address</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${suppliers}" var="supplier">
                                    <tr>
                                        <td><c:out value="${supplier.taxId}"/></td>
                                        <td><c:out value="${supplier.name}"/></td>
                                        <td><c:out value="${supplier.phoneNumber}"/></td>
                                        <td><c:out value="${supplier.email}"/></td>
                                        <td><c:out value="${supplier.shortedAddress}"/></td>
                                        <td>
                                            <span class="badge ${supplier.activate == 1 ? 'bg-success' : 'bg-danger'}">
                                                ${supplier.activate == 1 ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="table-actions">
                                                <a href="${pageContext.request.contextPath}/Supplier?id=${supplier.supplierId}">
                                                    <i class="ti-eye" aria-hidden="true"></i> Details
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty suppliers}">
                                    <tr>
                                        <td colspan="7" class="empty-state">No suppliers found.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>
        </div>

        <div class="modal fade" id="createSupplierModal" tabindex="-1"
             aria-labelledby="createSupplierModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 class="modal-title fs-5" id="createSupplierModalLabel">Create Supplier</h2>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/CreateSupplier">
                        <div class="modal-body">
                            <div class="form-two">
                                <label for="taxId">Tax ID
                                    <input id="taxId" name="taxNumber" type="text" class="form-control"
                                           pattern="[A-Za-z0-9-]{3,20}" minlength="3" maxlength="20"
                                           title="Use 3 to 20 letters, numbers, or hyphens." required>
                                </label>
                                <label for="companyName">Company Name
                                    <input id="companyName" name="name" type="text" class="form-control"
                                           minlength="2" maxlength="255" required>
                                </label>
                                <label for="supplierEmail">Email
                                    <input id="supplierEmail" name="email" type="email" class="form-control"
                                           maxlength="254" required>
                                </label>
                                <label for="phoneNumber">Phone
                                    <input id="phoneNumber" name="phone" type="tel" class="form-control"
                                           pattern="0[0-9]{9}" maxlength="10"
                                           title="Use a 10-digit phone number starting with 0." required>
                                </label>
                                <label class="field-wide" for="supplierAddress">Address
                                    <input id="supplierAddress" name="address" type="text" class="form-control"
                                           minlength="3" maxlength="255" required>
                                </label>
                                <label for="supplierStatus">Status
                                    <select id="supplierStatus" name="status" class="form-select" required>
                                        <option value="1">Active</option>
                                        <option value="0">Inactive</option>
                                    </select>
                                </label>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">Create Supplier</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
