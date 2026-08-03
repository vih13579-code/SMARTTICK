<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Supplier Details | SMARTTICK</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-ops.css?v=20260729-admin-fix">
        <style>
            .supplier-details {
                display: grid;
                grid-template-columns: repeat(2, minmax(0, 1fr));
                gap: 12px;
            }
            .supplier-detail {
                padding: 16px;
                border: 1px solid var(--dash-line);
                border-radius: 12px;
                background: var(--dash-surface-2);
            }
            .supplier-detail.address {
                grid-column: 1 / -1;
            }
            .supplier-detail small {
                display: block;
                margin-bottom: 5px;
                color: var(--dash-muted);
            }
            .supplier-detail strong {
                color: var(--dash-text);
                overflow-wrap: anywhere;
            }
            .supplier-actions {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                margin-top: 20px;
            }
            @media (max-width: 680px) {
                .supplier-details {
                    grid-template-columns: 1fr;
                }
                .supplier-detail.address {
                    grid-column: auto;
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
                        <h1>Supplier Details</h1>
                        <p>Review and update supplier contact information.</p>
                    </div>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/Supplier">Back to Suppliers</a>
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

                <c:choose>
                    <c:when test="${empty supplier}">
                        <section class="panel empty-state">Supplier was not found.</section>
                    </c:when>
                    <c:otherwise>
                        <section class="panel">
                            <div class="supplier-details">
                                <div class="supplier-detail">
                                    <small>Tax ID</small>
                                    <strong><c:out value="${supplier.taxId}"/></strong>
                                </div>
                                <div class="supplier-detail">
                                    <small>Company Name</small>
                                    <strong><c:out value="${supplier.name}"/></strong>
                                </div>
                                <div class="supplier-detail">
                                    <small>Phone</small>
                                    <strong><c:out value="${supplier.phoneNumber}"/></strong>
                                </div>
                                <div class="supplier-detail">
                                    <small>Email</small>
                                    <strong><c:out value="${supplier.email}"/></strong>
                                </div>
                                <div class="supplier-detail address">
                                    <small>Address</small>
                                    <strong><c:out value="${supplier.address}"/></strong>
                                </div>
                                <div class="supplier-detail">
                                    <small>Status</small>
                                    <span class="badge ${supplier.activate == 1 ? 'bg-success' : 'bg-danger'}">
                                        ${supplier.activate == 1 ? 'Active' : 'Inactive'}
                                    </span>
                                </div>
                            </div>

                            <div class="supplier-actions">
                                <button class="btn btn-primary" type="button" data-bs-toggle="modal"
                                        data-bs-target="#updateSupplierModal">Update Supplier</button>
                                <form method="post" action="${pageContext.request.contextPath}/DeleteSupplier"
                                      onsubmit="return confirm('Delete this supplier? Existing import history will be preserved.');">
                                    <input type="hidden" name="id" value="${supplier.supplierId}">
                                    <button class="btn btn-danger" type="submit">
                                        <i class="fa-solid fa-trash" aria-hidden="true"></i> Delete Supplier
                                    </button>
                                </form>
                            </div>
                        </section>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>

        <c:if test="${not empty supplier}">
            <div class="modal fade" id="updateSupplierModal" tabindex="-1"
                 aria-labelledby="updateSupplierModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h2 class="modal-title fs-5" id="updateSupplierModalLabel">Update Supplier</h2>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/UpdateSupplier">
                            <input type="hidden" name="id" value="${supplier.supplierId}">
                            <div class="modal-body">
                                <div class="form-two">
                                    <label for="taxId">Tax ID
                                        <input id="taxId" name="taxNumber" type="text" class="form-control"
                                               value="<c:out value='${supplier.taxId}'/>"
                                               pattern="[A-Za-z0-9-]{3,20}" minlength="3" maxlength="20"
                                               title="Use 3 to 20 letters, numbers, or hyphens." required>
                                    </label>
                                    <label for="companyName">Company Name
                                        <input id="companyName" name="name" type="text" class="form-control"
                                               value="<c:out value='${supplier.name}'/>"
                                               minlength="2" maxlength="255" required>
                                    </label>
                                    <label for="supplierEmail">Email
                                        <input id="supplierEmail" name="email" type="email" class="form-control"
                                               value="<c:out value='${supplier.email}'/>" maxlength="254" required>
                                    </label>
                                    <label for="phoneNumber">Phone
                                        <input id="phoneNumber" name="phone" type="tel" class="form-control"
                                               value="<c:out value='${supplier.phoneNumber}'/>"
                                               pattern="0[0-9]{9}" maxlength="10"
                                               title="Use a 10-digit phone number starting with 0." required>
                                    </label>
                                    <label class="field-wide" for="supplierAddress">Address
                                        <input id="supplierAddress" name="address" type="text" class="form-control"
                                               value="<c:out value='${supplier.address}'/>"
                                               minlength="3" maxlength="255" required>
                                    </label>
                                    <label for="supplierStatus">Status
                                        <select id="supplierStatus" name="status" class="form-select" required>
                                            <option value="1" ${supplier.activate == 1 ? 'selected' : ''}>Active</option>
                                            <option value="0" ${supplier.activate == 0 ? 'selected' : ''}>Inactive</option>
                                        </select>
                                    </label>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                <button type="submit" class="btn btn-primary">Save Changes</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </c:if>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
