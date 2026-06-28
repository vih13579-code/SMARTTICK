<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%-- 
    Document   : CreateVoucherView
    Created on : Mar 22, 2025, 7:45:24 PM
    Author     : CE181159-Nguyen Le Duy Minh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Voucher</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <style>
        .form-wrapper {
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            padding: 30px;
            margin-top: 30px;
        }

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
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar: Cột 1 -->
            <div class="col-2 sidebar-container p-0">
                <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            </div>

            <!-- Header: Trải dài từ cột 2 đến 6 -->
            <div class="col-10 p-0">
                <div class="fixed-header">
                    <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                </div>

                <!-- Nội dung chính: Form -->
                <div class="row justify-content-center mt-4">
                    <div class="col-8 col-md-6">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="fa-solid fa-triangle-exclamation"></i> ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>

                        <div class="form-wrapper">
                            <h2 class="mb-4">Create New Voucher</h2>
                            <form action="CreateVoucherServlet" method="post">
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Voucher Code</label>
                                        <input type="text" name="voucherCode" class="form-control" required maxlength="10">
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Type</label>
                                        <select name="voucherType" class="form-select">
                                            <option value="1">Percent (%)</option>
                                            <option value="0">Fixed Price</option>
                                        </select>
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Value</label>
                                        <input type="number" name="voucherValue" class="form-control" required>
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Max Discount</label>
                                        <input type="number" name="maxDiscountAmount" class="form-control">
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Min Order Value</label>
                                        <input type="number" name="minOrderValue" class="form-control" required>
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Max Used Count</label>
                                        <input type="number" name="maxUsedCount" class="form-control">
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Start Date</label>
                                        <input type="datetime-local" name="startDate" class="form-control" required>
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">End Date</label>
                                        <input type="datetime-local" name="endDate" class="form-control" required>
                                    </div>

                                    <div class="col-md-6 mb-3">
                                        <label class="form-label">Status</label>
                                        <select name="status" class="form-select">
                                            <option value="1">Active</option>
                                            <option value="0">Inactive</option>
                                        </select>
                                    </div>

                                    <div class="col-12 mb-3">
                                        <label class="form-label">Description</label>
                                        <textarea name="description" class="form-control"></textarea>
                                    </div>
                                </div>

                                <button type="submit" class="btn btn-success">
                                    <i class="fa-solid fa-plus"></i> Create
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div> <!-- End col-10 -->
        </div> <!-- End row -->
    </div> <!-- End container -->

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>
