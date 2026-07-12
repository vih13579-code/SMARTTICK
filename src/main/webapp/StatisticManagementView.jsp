<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Statistic Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>    
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-zoom@1.0.1/dist/chartjs-plugin-zoom.min.js"></script>
        <style>
            .content {
                flex-grow: 1;
                padding: 10px 15px;
                margin-left: 240px;
            }

            .fixed-header {
                position: fixed;
                width: 82%;
                margin-top: -10px;
                margin-bottom: 500px;
            }

            .stat-card {
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                padding: 15px;
                border-radius: 12px;
                box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.2);
                text-align: center;
                font-size: 16px;
                font-weight: bold;
                width: 200px;
                transition: transform 0.3s;
                text-decoration: none;
                color: white;
                height: 110px;
            }
            .stat-card:hover {
                transform: scale(1.08);
            }
            .inventory-card {
                background-color: #60CDB6;
            }
            .revenue-card {
                background-color: #7455B2;
            }

            .chart-container {
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 15px;
                background-color: #fff;
                box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.15);
                margin: 10px auto;
                width: 80%; 
            }

            canvas {
                max-width: 100%;
                height: 100%;  /* Đảm bảo biểu đồ chiếm chiều cao toàn bộ container */
            }


            /* Nút */

            .nav-buttons {
                display: flex;
                justify-content: center;
                gap: 15px;
                margin-top: 10px;
            }
            .nav-buttons button {
                border: none;
                background: transparent;
                font-size: 24px;
                cursor: pointer;
                color: #4F8CF7;
            }
            .nav-buttons button:hover {
                color: #8C52FF;
            }

            /* Bảng có viền và header cố định khi cuộn */
            .rounded-table {
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
            }
            .table-container {
                max-height: 250px;
                overflow-y: auto;
                border-radius: 10px;
                border: 1px solid #ddd;
            }
            .table-container thead {
                position: sticky;
                top: 0;
                background-color: #f8f9fa;
                z-index: 10;
            }
        </style>
    </head>
    <body>
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>  
            <div class="content">
                <jsp:include page="HeaderDashboard.jsp"></jsp:include>
            <div class="container mt-4">
                <div class="row mt-4 g-4 justify-content-center">
                    <div class="row justify-content-center text-center">
                        <div class="col-md-3">
                            <a href="InventoryStatisticServlet" class="stat-card inventory-card">
                                <i class='bx bx-box bx-lg'></i>
                                <span>Inventory</span>
                            </a>
                        </div>
                        <div class="col-md-3">
                            <a href="RevenueStatisticServlet" class="stat-card revenue-card">
                                <i class='bx bx-dollar-circle bx-lg'></i>
                                <span>Revenue</span>
                            </a>
                        </div>
                    </div>
                    <!-- Top Products & Top Customers -->
                    <div class="row mt-4">
                        <div class="col-md-6">
                            <h4>Top Products</h4>
                            <div class="table-container rounded-table">
                                <table class="table table-striped">
                                    <thead class="table-primary">
                                        <tr>
                                            <th>Product</th>
                                            <th>Sales</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    <c:if test="${not empty listP}">
                                        <c:forEach items="${listP}" var="p">
                                            <tr>
                                                <td>${p.productName}</td>
                                                <td>${p.soldQuantity}</td>
                                            </tr>
                                        </c:forEach>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <h4>Top Customers</h4>
                        <div class="table-container rounded-table">
                            <table class="table table-striped">
                                <thead class="table-success">
                                    <tr>
                                        <th>Customer</th>
                                        <th>Email</th>
                                        <th>Order</th>
                                    </tr>
                                </thead>
                                <tbody>                         
                                    <c:if test="${not empty listC}">
                                        <c:forEach items="${listC}" var="c">
                                            <tr>
                                                <td>${c.customerName}</td>
                                                <td>${c.customerEmail}</td>
                                                <td>${c.successfulOrders}</td>
                                            </tr>
                                        </c:forEach> 
                                    </c:if>                             
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script>
           
        </script>
        </div>
    </body>
</html>
