<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%-- 
    Document   : UpdateVoucherView
    Created on : Mar 22, 2025, 6:37:14 PM
    Author     : CE181159-Nguyen Le Duy Minh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Update Voucher</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

        <style>
            .sidebar-container {
                background-color: white;
                height: 100vh;
                border-right: 1px solid #eee;
            }

            .fixed-header {
                padding: 15px 30px;
                background-color: white;
                border-bottom: 1px solid #eee;
            }

            .form-wrapper {
                background: #fff;
                border-radius: 12px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                padding: 30px;
                margin-top: 30px;
            }

            .form-label {
                font-weight: 500;
            }

            .btn i {
                margin-right: 6px;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid">
            <div class="row">
                <!-- Sidebar -->
                <div class="col-2 sidebar-container p-0">
                    <jsp:include page="SidebarDashboard.jsp" />
                </div>

                <!-- Header + Content -->
                <div class="col-10 p-0">
                    <!-- Header -->
                    <div class="fixed-header">
                        <jsp:include page="HeaderDashboard.jsp" />
                    </div>

                    <!-- Main Content -->
                    <div class="row justify-content-center mt-4">
                        <div class="col-8 col-md-6">
                            <h2 class="mb-4 text-center">Update Voucher</h2>

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                    <i class="fa-solid fa-triangle-exclamation me-2"></i> ${error}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                </div>
                            </c:if>

                            <div class="form-wrapper">
                                <form action="UpdateVoucherServlet" method="post">
                                    <input type="hidden" name="voucherID" value="${voucher.voucherID}" />

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Voucher Code</label>
                                            <input type="text" name="voucherCode" class="form-control" value="${voucher.voucherCode}" required maxlength="10">
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Voucher Type</label>
                                            <select class="form-select" name="voucherType" required>
                                                <option value="1" ${voucher.voucherType == 1 ? 'selected' : ''}>Percent (%)</option>
                                                <option value="0" ${voucher.voucherType == 0 ? 'selected' : ''}>Fixed Price</option>
                                            </select>
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Voucher Value</label>
                                            <input type="number" name="voucherValue" class="form-control" value="${voucher.voucherValue}" required>
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Max Discount Amount</label>
                                            <input type="number" name="maxDiscountAmount" class="form-control" value="${voucher.maxDiscountAmount}">
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Min Order Value</label>
                                            <input type="number" name="minOrderValue" class="form-control" value="${voucher.minOrderValue}" required>
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Used Count</label>
                                            <input type="number" name="usedCount" class="form-control" value="${voucher.usedCount}">
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Start Date</label>
                                            <input type="datetime-local" name="startDate" class="form-control" value="${voucher.startDate}" required>
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">End Date</label>
                                            <input type="datetime-local" name="endDate" class="form-control" value="${voucher.endDate}" required>
                                        </div>



                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Max Used Count</label>
                                            <input type="number" name="maxUsedCount" class="form-control" value="${voucher.maxUsedCount}">
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Status</label>
                                            <select class="form-select" name="status">
                                                <option value="1" ${voucher.status == 1 ? 'selected' : ''}>Active</option>
                                                <option value="0" ${voucher.status == 0 ? 'selected' : ''}>Inactive</option>
                                            </select>
                                        </div>

                                        <div class="col-12 mb-3">
                                            <label class="form-label">Description</label>
                                            <textarea class="form-control" name="description" rows="3">${voucher.description}</textarea>
                                        </div>
                                    </div>

                                    <div class="text-center">
                                        <button type="submit" class="btn btn-success me-2">
                                            <i class="fa-solid fa-check"></i> Update Voucher
                                        </button>
                                        <%-- 
                                        <a href="ViewVoucherListServlet" class="btn btn-secondary">
                                            <i class="fa-solid fa-arrow-left"></i> Cancel
                                        </a>
                                        --%>
                                    </div>
                                </form>
                            </div> <!-- form-wrapper -->
                        </div>
                    </div> <!-- row -->
                </div> <!-- col-10 -->
            </div> <!-- row -->
        </div> <!-- container-fluid -->
    </body>
</html>
