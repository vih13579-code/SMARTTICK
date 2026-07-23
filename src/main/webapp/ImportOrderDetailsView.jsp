
<%@page import="Models.ImportOrder"%>
<%@page import="Models.ImportOrderDetail"%>
<%@page import="java.util.List"%>
<%@page import="DAOs.SupplierDAO"%>
<%@page import="Models.Supplier"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>F Shop</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <style>
            body {
                display: flex;
            }

            .sidebar {
                width: 250px;
                height: 97vh;
                background: #FFFFFF;
                color: black;
                padding-top: 20px;
                box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.3);
                transform: translateZ(0);
                position: relative;
                z-index: 10;
                border-radius: 10px;
                margin-top: 10px;
            }

            .sidebar a {
                color: #7A7D90;
                text-decoration: none;
                padding: 10px;
                display: block;
            }

            .sidebar a:hover {
                background: #7D69FF;
                color: white;
                width: 90%;
                font-weight: bold;

                border-top-right-radius: 10px;
                border-bottom-right-radius: 10px;
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;

            }

            .content {
                flex-grow: 1;
                padding: 12px;
                margin-left: 250px;
            }

            .header {
                display: flex;
                justify-content: right;
                align-items: center;
                padding: 10px;
                background: #FFFFFF;
                box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.3);
                border-radius: 10px;
                height: 85px;
            }

            .icon {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                object-fit: cover;
            }

            .logo-side-bar {
                margin-left: 5%;
                margin-bottom: 3%;
            }
            /* ========================================================= */

            .table-container {
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0px 5px 15px rgba(0, 0, 0, 0.1);
            }

            table {
                border-radius: 10px;
                overflow: hidden;
            }

            thead {
                background: #7D69FF;
                color: white;
            }

            tbody tr:hover {
                background: #f2f2f2;
                transition: 0.3s;
            }

            .search-box {
                max-width: 300px;
                margin-bottom: 10px;
            }

            .table-navigate{
                display: flex;
                justify-content: space-between;
            }
        </style>
    </head>
    <body>
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            <div class="content">
            <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                <!--
                <div class="table-navigate">
                    <div class="table-navigate"></div>
                </div>-->

                <div class="table-container" style="margin-top: 20px">
                    <div>
                        <h3></h3>
                    </div>
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Import ID</th>
                                <th>Employee ID</th>
                                <th>Employee Name</th>
                                <th>Date</th>
                                <th>Supplier</th>
                                <th>Amount</th>
                            </tr>
                        </thead>
                        <tbody id="supplierTable">
                        <c:set var="i" value="${importOrder}"/>
                        <c:set var="e" value="${employee}"/>
                        <tr>
                            <td>${i.getIoid()}</td>
                            <td>${i.getEmployeeId()}</td>
                            <td style="word-wrap: break-word; white-space: normal; max-width: 200px;">${e.getFullname()}</td>
                            <td>${i.getImportDate()}</td>
                            <td  style="word-wrap: break-word; white-space: normal; max-width: 200px;">${i.getSupplier().getName()}</td>
                            <td>${i.getPriceFormatted()}</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="table-container" style="margin-top: 20px">
                <div>
                    <h3>Details</h3>
                </div>
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Product ID</th>
                            <th>Product Model</th>
                            <th>Product Name</th>
                            <th>Quantity</th>
                            <th>Import Price</th>
                        </tr>
                    </thead>
                    <tbody id="supplierTable">
                        <c:forEach items="${importOrder.getImportOrderDetails()}" var="d">
                            <tr>
                                <td>${d.getProduct().getProductId()}</td>
                                <td  style="word-wrap: break-word; white-space: normal; max-width: 200px;">${d.getProduct().getModel()}</td>
                                <td  style="word-wrap: break-word; white-space: normal; max-width: 200px;">${d.getProduct().getFullName()}</td>
                                <td>${d.getQuantity()}</td>
                                <td>${d.getPriceFormatted()}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        function filterByDate() {
            let startDate = document.getElementById("startDate").value;
            let endDate = document.getElementById("endDate").value;
            let table = document.getElementById("supplierTable");
            let rows = table.getElementsByTagName("tr");

            // Chuyển đổi định dạng ngày về timestamp để so sánh
            let startTimestamp = new Date(startDate).getTime();
            let endTimestamp = new Date(endDate).getTime();

            for (let i = 0; i < rows.length; i++) {
                let dateCell = rows[i].getElementsByTagName("td")[1]; // Lấy cột ngày
                if (dateCell) {
                    let rowDate = new Date(dateCell.textContent.trim()).getTime();

                    if ((!startDate || rowDate >= startTimestamp) &&
                            (!endDate || rowDate <= endTimestamp)) {
                        rows[i].style.display = "";
                    } else {
                        rows[i].style.display = "none";
                    }
                }
            }
        }
    </script>

</body>
</html>
