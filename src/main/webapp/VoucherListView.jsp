<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%-- 
    Document   : VoucherListView
    Created on : Mar 22, 2025, 5:19:32 PM
    Author     : CE181159-Nguyen Le Duy Minh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Voucher List</title>

        <!-- Bootstrap & FontAwesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

        <style>
            .fixed-header {
                position: fixed;
                top: 0;
                /*right: 0;*/
                left: 250px;
                width: calc(100% - 250px);
                background-color: white;
                z-index: 1000;
                padding: 10px 20px;
                /*box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);*/
            }

            .sidebar-container {
                position: fixed;
                top: 0;
                left: 0;
                width: 250px;
                height: 100%;
                background-color: white;
                z-index: 2000;
                padding-top: 0px;
            }

            .main-layout {
                display: flex;
            }

            .content {
                flex-grow: 1;
                margin-left: 250px;
                margin-top: 120px;
                padding: 20px;
            }

            .order-table th, .order-table td {
                padding: 14px 18px;
                font-size: 15px;
                vertical-align: middle;
            }

            .order-table tr:hover {
                background-color: #f9f9f9;
            }

            .btn {
                font-size: 14px;
                padding: 8px 14px;
            }

            .btn i {
                margin-right: 5px;
            }

            .badge {
                font-size: 0.85rem;
            }

            .action-buttons .btn {
                margin-bottom: 5px;
            }
        </style>
    </head>
    <body>

        <div class="sidebar-container">
            <jsp:include page="SidebarDashboard.jsp"/>
        </div>

        <div class="main-layout">
            <div class="fixed-header">
                <jsp:include page="HeaderDashboard.jsp"/>
            </div>

            <div class="content">
                <h3 font-weight="Bold">Voucher <small class="text-muted">(${totalVouchers} result(s))</small></h3>
                <!-- Success Alerts -->
                <c:if test="${param.success == 'createsuccess'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> Voucher created successfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.success == 'createfailed'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> Voucher created Unsuccessfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
  <c:if test="${param.success == 'deletesuccess'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> Voucher deleted successfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.success == 'deletefailed'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-triangle-exclamation me-2"></i> Voucher deleted unsuccessfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                    <c:if test="${param.success == 'updatesuccess'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> Voucher update successfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                   <c:if test="${param.success == 'updatefailed'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> Voucher update Unsuccessfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Add New -->
                <a href="CreateVoucherView.jsp" class="btn btn-success mb-3">
                    <i class="fa-solid fa-plus"></i> Add New Voucher
                </a>
                <form class="row g-2 mb-3" action="ViewVoucherListServlet" method="get">
                    <div class="col-md-3">
                        <input type="text" class="form-control" name="code" value="${searchCode}" placeholder="Code, description or date">
                    </div>
                    <div class="col-md-2">
                        <select class="form-select" name="status">
                            <option value="">All status</option>
                            <option value="1" ${selectedStatus == 1 ? 'selected' : ''}>Active</option>
                            <option value="0" ${selectedStatus == 0 ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select class="form-select" name="type">
                            <option value="">All types</option>
                            <option value="1" ${selectedType == 1 ? 'selected' : ''}>Percent</option>
                            <option value="0" ${selectedType == 0 ? 'selected' : ''}>Fixed</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <select class="form-select" name="sort">
                            <option value="id_desc" ${empty selectedSort || selectedSort == 'id_desc' ? 'selected' : ''}>Newest</option>
                            <option value="id_asc" ${selectedSort == 'id_asc' ? 'selected' : ''}>Oldest</option>
                            <option value="code_asc" ${selectedSort == 'code_asc' ? 'selected' : ''}>Code A-Z</option>
                            <option value="code_desc" ${selectedSort == 'code_desc' ? 'selected' : ''}>Code Z-A</option>
                            <option value="end_asc" ${selectedSort == 'end_asc' ? 'selected' : ''}>End date asc</option>
                            <option value="end_desc" ${selectedSort == 'end_desc' ? 'selected' : ''}>End date desc</option>
                            <option value="value_desc" ${selectedSort == 'value_desc' ? 'selected' : ''}>Highest value</option>
                        </select>
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-primary">
                            <i class="fa-solid fa-magnifying-glass"></i> Search
                        </button>
                    </div>
                    <div class="col-auto">
                        <a href="ViewVoucherListServlet" class="btn btn-outline-secondary">Reset</a>
                    </div>
                </form>

                <!-- Voucher Table -->
                <table class="table order-table table-bordered table-hover">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Code</th>
                            <th>Value</th>
                            <th>Max Discount</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Status</th>
                            <th>Type</th>        
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${Vouchers}" var="voucher">
                            <tr>
                                <td>#${voucher.voucherID}</td>
                                <td>${voucher.voucherCode}</td>
                                <td>${voucher.voucherValue}</td>
                                <td>${voucher.maxDiscountAmount}</td>
                                <td>${voucher.startDate}</td><!-- comment -->
                                <td>${voucher.endDate}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${voucher.status == 1}">
                                            <span class="badge bg-success">Active</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">Inactive</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${voucher.voucherType == 1}">
                                            <span class="badge bg-primary">Percent</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-success">Fixed</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="d-flex flex-wrap gap-2">
                                        <a href="ViewVoucherDetailServlet?voucherID=${voucher.voucherID}" class="btn btn-outline-primary">
                                            <i class="fa-solid fa-eye"></i> View
                                        </a>
                                        <a href="UpdateVoucherServlet?voucherID=${voucher.voucherID}" class="btn btn-outline-warning">
                                            <i class="fa-solid fa-pen-to-square"></i> Update
                                        </a>
                                        <button type="button" class="btn btn-outline-danger" data-bs-toggle="modal"
                                                data-bs-target="#confirmDeleteModal" data-id="${voucher.voucherID}">
                                            <i class="fa-solid fa-trash"></i> Delete
                                        </button>
                                    </div>
                                </td>

                            </tr>
                        </c:forEach>
                        <c:if test="${empty Vouchers}">
                            <tr>
                                <td colspan="9" class="text-center text-muted py-4">No vouchers found.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Voucher pagination">
                        <ul class="pagination justify-content-end">
                            <c:url var="prevUrl" value="ViewVoucherListServlet">
                                <c:param name="code" value="${searchCode}"/>
                                <c:param name="status" value="${selectedStatus}"/>
                                <c:param name="type" value="${selectedType}"/>
                                <c:param name="sort" value="${selectedSort}"/>
                                <c:param name="page" value="${currentPage - 1}"/>
                            </c:url>
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}"><a class="page-link" href="${prevUrl}">Previous</a></li>
                            <c:forEach begin="1" end="${totalPages}" var="p">
                                <c:url var="pageUrl" value="ViewVoucherListServlet">
                                    <c:param name="code" value="${searchCode}"/>
                                    <c:param name="status" value="${selectedStatus}"/>
                                    <c:param name="type" value="${selectedType}"/>
                                    <c:param name="sort" value="${selectedSort}"/>
                                    <c:param name="page" value="${p}"/>
                                </c:url>
                                <li class="page-item ${currentPage == p ? 'active' : ''}"><a class="page-link" href="${pageUrl}">${p}</a></li>
                            </c:forEach>
                            <c:url var="nextUrl" value="ViewVoucherListServlet">
                                <c:param name="code" value="${searchCode}"/>
                                <c:param name="status" value="${selectedStatus}"/>
                                <c:param name="type" value="${selectedType}"/>
                                <c:param name="sort" value="${selectedSort}"/>
                                <c:param name="page" value="${currentPage + 1}"/>
                            </c:url>
                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}"><a class="page-link" href="${nextUrl}">Next</a></li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>

        <!-- Delete Confirmation Modal -->
        <div class="modal fade" id="confirmDeleteModal" tabindex="-1" aria-labelledby="confirmDeleteModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <form method="post" action="DeleteVoucherServlet">
                    <input type="hidden" name="voucherID" id="deleteVoucherId">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="confirmDeleteModalLabel">Confirm Delete</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            Are you sure you want to delete this voucher?
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-danger">Yes, Delete</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- JS -->
        <script>
            const deleteModal = document.getElementById('confirmDeleteModal');
            deleteModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const voucherID = button.getAttribute('data-id');
                const input = deleteModal.querySelector('#deleteVoucherId');
                input.value = voucherID;
            });
        </script>

    </body>
</html>
