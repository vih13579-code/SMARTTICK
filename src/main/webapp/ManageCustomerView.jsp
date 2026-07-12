
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
                height: auto;
                background: #FFFFFF;
                color: black;
                padding-top: 20px;
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
                margin-left: 250px; /* Inventoryảng cách để không bị chồng lên sidebar */
                margin-top: 150px;
                padding: 20px;
            }
            /* Giữ header cố định */
            .header {
                position: fixed;
                top: 0;
                left: 260px; /* vì sidebar chiếm 250px */
                right: 10px;
                margin-top: 10px;
                z-index: 1000;
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
                border-radius: 5px;
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
                max-width: 500px;
                margin-bottom: 5px;
            }

            .table-navigate{
                display: flex;
                justify-content: space-between;
            }
            .btn-add {
                background-color: #003375;
                color: white;
                border: none;
                display: inline-block;
                padding: 5px 10px;
            }
            .btn-delete {
                background-color: red;
                color: white;
                border: none;
                display: inline-block;
                padding: 5px 10px;
            }
            .btn-edit {
                background-color: #007bff;
                color: white;
                border: none;
                display: inline-block;
                padding: 5px 10px;
            }
            .search-section {
                position: fixed;
                top: 85px;       /* Điều chỉnh theo chiều cao header nếu cần */
                left: 280px;     /* Điều chỉnh theo chiều rộng sidebar */
                right: 0;
                background: white;
                z-index: 999;
                padding: 10px;
            }

            .hi {
                position: fixed;
                top: 0;
                left: 267px;
                right: 0;
                background: white;
                z-index: 1000;
                display: flex;
                flex-direction: column;
                align-items: flex-start;
                padding: 18px;
                border: 5px;
            }

            .top-section {
                display: flex;
                gap: 10px;  /* khoảng cách giữa header và search */
                align-items: center;
                width: 100%;
            }

            .search-container {
                display: flex;
                align-items: center;
                width: 90%;
                max-width: 250px;  /* giới hạn kích thước tối đa */
                background: white;
                border-radius: 13px;
                overflow: hidden;
                border: 2px solid #7D69FF;
                margin-top: 65px;
                margin-bottom: 20px;
            }

            .search-input {
                flex: 1;
                border: none;
                outline: none;
                padding: 8px 12px; /* Giảm padding để nhỏ hơn */
                font-size: 14px; /* Giảm kích thước chữ */
                color: #555;
            }

            .search-button {
                border: none;
                padding: 8px 12px; /* Giảm padding của nút */
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                color: white;
                font-size: 14px;
            }

            .search-button:hover {
                background: #6454cc;
            }

            .h3{
                margin-left: 2px;
            }
            
            .alert{
                margin-top: 15px;
            }
        </style>
    </head>
    <body>
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            <div class="content">
                <div class="hi">
                <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                    <form action="CustomerListServlet" method="get" class="search-container">
                        <input type="text" name="txt" value="${param.txt}" placeholder="Search by name..." class="search-input">
                    <button type="submit" class="search-button">
                        🔍
                    </button>
                </form>
                <h3 class="h3" font-weight="Bold">CUSTOMER</h3>
            </div>

            <div class="table-container">
                <c:if test="${not empty message}">
                    <div class="alert alert-info" role="alert">
                        ${message}
                    </div>
                </c:if>
                <c:if test="${param.success == 'assigned'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> Assigned voucher successfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.success == 'failed'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fa-solid fa-circle-check me-2"></i> Assigned voucher unsuccessfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="customerTable">
                        <c:forEach items="${customers}" var="s">
                            <tr>
                                <td>${s.getId()}</td>
                                <td>${s.getFullName()}</td>
                                <td>${s.getEmail()}</td>
                                <td>${s.getPhoneNumber()}</td>
                                <td>
                                    <span class="badge ${s.getIsBlock() ==0 ? 'bg-success' : 'bg-danger' }">
                                        ${s.getStatus()}
                                    </span>
                                </td>

                                <td>
                                    <c:if test="${sessionScope.employee.getRoleId() == 3}">
                                        <a href="AssignVoucherServlet?Id=${s.getId()}" 
                                           class="btn btn-warning">
                                            Voucher
                                        </a>
                                    </c:if>
                                    <c:if test="${sessionScope.employee.getRoleId() == 2 }">
                                        <a href="CustomerListServlet?${s.getIsBlock() == 1 ? 'Activate' : 'Blocked'}=${s.getId()}" 
                                           class="btn ${s.getIsBlock() == 1 ? 'btn-success' : 'btn-danger'}" 
                                           onclick="return confirm('Are you sure?');">
                                            ${s.getIsBlock() == 0 ? 'Blocked' : 'Activate'}
                                        </a>
                                    </c:if>
                                    <a href="CustomerListServlet?id=${s.getId()}" class="btn btn-detail" style="background-color: #BDF3BD">Detail</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <script>
            function filterTable() {
                let input = document.getElementById("searchInput");
                let filter = input.value.toLowerCase();
                let table = document.getElementById("customerTable");
                let rows = table.getElementsByTagName("tr");

                for (let i = 0; i < rows.length; i++) {
                    let nameCell = rows[i].getElementsByTagName("td")[1];
                    if (nameCell) {
                        let nameText = nameCell.textContent || nameCell.innerText;
                        rows[i].style.display = nameText.toLowerCase().includes(filter) ? "" : "none";
                    }
                }
            }
        </script>

    </div>
</body>
</html>
