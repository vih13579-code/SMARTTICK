<%@ page contentType="text/html" pageEncoding="UTF-8"%> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html> 
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Order Details</title>

        <!-- Bootstrap -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">

        <!-- Custom CSS -->
        <link rel="stylesheet" href="assets/css/orderDetail.css">

        <!-- CSS bổ sung để điều chỉnh thứ tự chồng -->
        <style>
            /* Bọc header với z-index thấp */
            .fixed-header {
                position: fixed;
                top: 0;
                left: 250px; /* Điều chỉnh để tránh che sidebar */
                width: calc(100% - 250px); /* Chiều rộng trừ đi sidebar */
                /*background-color: white;*/
                z-index: 1050;
                padding: 10px 20px;
                /*box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);*/
            }

            /* Bọc sidebar với z-index cao hơn để chồng lên header */
            .sidebar-container {
                position: relative;
                z-index: 2000;
            }

            .main-layout {
                display: flex;
            }

            .main-content {
                flex-grow: 1;
                margin-left: 250px; /* Inventoryảng cách để không bị chồng lên sidebar */
                margin-top: 120px;
                padding: 20px;
            }

            .order-layout {
                display: flex;
                gap: 20px;
            }

            .left-section, .right-section {
                flex: 1;
            }

            .order-info, .customer-info, .manage-order {
                background-color: #fff;
                padding: 15px;
                border-radius: 8px;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                margin-bottom: 20px;
            }

            .manage-order {
                margin-top: 20px;
            }

            .manage-order select {
                width: 100%;
                padding: 10px;
                margin-bottom: 20px;
                border-radius: 5px;
                border: 1px solid #ddd;
                font-size: 16px;
            }

            .manage-order button {
                width: 100%;
                padding: 12px;
                background-color: #28a745;
                border: none;
                border-radius: 5px;
                color: white;
                font-size: 16px;
            }

            .manage-order button:hover {
                background-color: #218838;
            }

            /* Điều chỉnh khoảng cách giữa các thành phần */
            .dropdown {
                margin-bottom: 20px; /* Add khoảng cách giữa dropdown và nút update */
            }

            .order-details {
                margin-top: 15px;
            }

            .order-details span {
                display: block;
                margin-bottom: 8px;
            }

            .status-1 {
                color: #ffeb3b;
            }

            .status-2 {
                color: #ff9800;
            }

            .status-3 {
                color: #2196f3;
            }

            .status-4 {
                color: #4caf50;
            }

            .status-5 {
                color: #f44336;
            }

            body.admin-ops-page.order-detail-admin-page .order-info,
            body.admin-ops-page.order-detail-admin-page .customer-info {
                background-color: #fff !important;
                color: #1f2937 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-info h3,
            body.admin-ops-page.order-detail-admin-page .customer-info h3 {
                color: #111827 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-info p,
            body.admin-ops-page.order-detail-admin-page .customer-info p,
            body.admin-ops-page.order-detail-admin-page .order-info strong,
            body.admin-ops-page.order-detail-admin-page .customer-info strong {
                color: #1f2937 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-info p > span:not([class^="status-"]),
            body.admin-ops-page.order-detail-admin-page .customer-info p > span {
                color: #4b5563 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-details {
                background-color: #fff !important;
                color: #1f2937 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-details > div {
                background-color: #f8fafc !important;
                color: #1f2937 !important;
                border-color: #d7dde7 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-details span {
                color: #374151 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-details i {
                color: #a97824 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-info .status-1 {
                color: #a16207 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-info .status-2 {
                color: #c2410c !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-info .status-3 {
                color: #1d4ed8 !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-info .status-4 {
                color: #15803d !important;
            }

            body.admin-ops-page.order-detail-admin-page .order-info .status-5 {
                color: #b91c1c !important;
            }

        </style>
    </head>
    <body class="admin-ops-page order-detail-admin-page">
        <div class="fixed-header"><jsp:include page="HeaderDashboard.jsp"></jsp:include>
                <p></p>
                <h2><i class="fa-solid fa-receipt"></i> Order Details</h2>
            </div>

            <div class="main-layout">
                <div class="sidebar-container">
                <jsp:include page="SidebarDashboard.jsp"></jsp:include>
                </div>

                <div class="main-content">
                    <div class="container">
                        <div class="order-layout">
                            <!-- Left: Order Information & Items -->
                            <div class="left-section">
                                <div class="order-info">
                                    <h3><i class="fa-solid fa-info-circle"></i> Order Information</h3>
                                    <p><strong>Order ID:</strong> <span>${data.orderID}</span></p>
                                <p><strong>Order Date:</strong> <span>${data.orderDate}</span></p>
                                <p><strong>Order Status:</strong> <span class="status-${data.status}">
                                        <c:if test="${data.status == 1}">Waiting For Acceptance</c:if>
                                        <c:if test="${data.status == 2}">Packaging</c:if>
                                        <c:if test="${data.status == 3}">Waiting For Delivery</c:if>
                                        <c:if test="${data.status == 4}">Delivered</c:if>
                                        <c:if test="${data.status == 5}">Cancel</c:if>
                                        </span></p>
                                    <p><strong>Total Amount:</strong> <span><fmt:formatNumber value="${data.totalAmount}" pattern="#,##0"/> VND</span></p>
                                <p><strong>Discount:</strong> <span><fmt:formatNumber value="${data.discount}" pattern="#,##0"/> VND</span></p>
                                <p><strong>Payment Method:</strong> <span>${empty data.paymentMethod ? 'COD' : data.paymentMethod}</span></p>
                                <p><strong>Payment Status:</strong> <span>${empty data.paymentStatus ? 'pending' : data.paymentStatus}</span></p>
                                <c:if test="${data.depositAmount > 0}">
                                    <p><strong>Deposit:</strong> <span><fmt:formatNumber value="${data.depositAmount}" pattern="#,##0"/> VND</span></p>
                                    <p><strong>Remaining Due:</strong> <span><fmt:formatNumber value="${data.amountDue}" pattern="#,##0"/> VND</span></p>
                                </c:if>
                            </div>

                            <h3><i class="fa-solid fa-box"></i> Order Items</h3>
                            <div class="order-details">
                                <c:forEach items="${dataDetail}" var="detail">
                                    <div>
                                        <span><i class="fa-solid fa-cube"></i> ${detail.productName}</span>
                                        <span><i class="fa-solid fa-cart-plus"></i> ${detail.quantity}</span>
                                        <span><i class="fa-solid fa-dollar-sign"></i> ${detail.price}</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <!-- Right: Customer Info & Manage Order -->
                        <div class="right-section">
                            <div class="customer-info">
                                <h3><i class="fa-solid fa-user"></i> Customer Information</h3>
                                <p><strong>Name:</strong> <span>${data.fullName}</span></p>
                                <p><strong>Phone:</strong> <span>${data.phone}</span></p>
                                <p><strong>Address:</strong> <span>${data.address}</span></p>
                            </div>

                            <!--                            <div class="manage-order">
                                                            <h3><i class="fa-solid fa-cogs"></i> Manage Order</h3>
                            <c:if test="${not empty errorMessage}">
                                <div class="alert alert-danger">
                                ${errorMessage}
                            </div>
                            </c:if>

                    
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Confirm Delete -->
        <div id="confirmationModal" class="modal">
            <div class="modal-content">
                <h3>Are you sure you want to delete this order?</h3>
                <button id="confirmBtn" class="btn btn-danger">Yes, Delete</button>
                <button id="cancelBtn" class="btn btn-secondary">Cancel</button>
            </div>
        </div>

        <!-- JavaScript -->
        <script>
            function confirmDelete() {
                document.getElementById("confirmationModal").style.display = "flex";
            }
            document.getElementById("confirmBtn").onclick = function () {
                document.getElementById("deleteForm").submit();
            };
            document.getElementById("cancelBtn").onclick = function () {
                document.getElementById("confirmationModal").style.display = "none";
            };

            function disableOptions() {
                const status = document.getElementById('orderStatus').value; // Lấy giá trị trạng thái đã chọn
                const options = document.getElementById('orderStatus').options;

                // Đảm bảo tất cả các tùy chọn đều được kích hoạt lại trước khi disable lại
                for (let i = 0; i < options.length; i++) {
                    options[i].disabled = false;
                }

                // Disable các trạng thái không hợp lệ
                if (status === '3') { // Waiting For Delivery
                    options[0].disabled = true; // Không thể chọn 'Waiting For Acceptance'
                    options[1].disabled = true; // Không thể chọn 'Packaging'
                    options[4].disabled = true; // Không thể chọn 'Cancel'
                } else if (status === '2') { // Packaging
                    options[0].disabled = true; // Không thể chọn 'Waiting For Acceptance'
                    options[4].disabled = true; // Không thể chọn 'Cancel'
                } else if (status === '4') { // Delivered
                    options[0].disabled = true; // Không thể chọn 'Waiting For Acceptance'
                    options[1].disabled = true; // Không thể chọn 'Packaging'
                    options[2].disabled = true; // Không thể chọn 'Waiting For Delivery'
                    options[4].disabled = true; // Không thể chọn 'Cancel'
                } else if (status === '5') { // Cancel
                    options[0].disabled = true; // Không thể chọn 'Waiting For Acceptance'
                    options[1].disabled = true; // Không thể chọn 'Packaging'
                    options[2].disabled = true; // Không thể chọn 'Waiting For Delivery'
                    options[3].disabled = true; // Không thể chọn 'Delivered'
                }
            }

// Gọi disableOptions() khi trang tải
            document.addEventListener('DOMContentLoaded', function () {
                disableOptions();
            });


        </script>
    </body>
</html>
