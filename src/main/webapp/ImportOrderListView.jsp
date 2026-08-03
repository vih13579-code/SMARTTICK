
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

            .table-success {
                /*font-weight: bold;*/
            }

        </style>
    </head>
    <body class="admin-ops-page">
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            <div class="content">
            <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                <div class="table-container" style="margin-top: 20px">
                    <div class="admin-page-title">
                        <div>
                            <h1>Import Stock History</h1>
                            <p>Filter completed imports and open their product details.</p>
                        </div>
                        <button class="btn btn-primary" type="button" onclick="window.location.href = 'ImportStock'">
                            <i class="ti-plus" aria-hidden="true"></i> Create
                        </button>
                    </div>
                    <c:if test="${not empty sessionScope.importSuccess}">
                        <div class="alert alert-success"><c:out value="${sessionScope.importSuccess}"/></div>
                        <c:remove var="importSuccess" scope="session"/>
                    </c:if>
                    <c:if test="${not empty sessionScope.importError}">
                        <div class="alert alert-danger"><c:out value="${sessionScope.importError}"/></div>
                        <c:remove var="importError" scope="session"/>
                    </c:if>
                    <!--
                    <input type="text" id="searchInput" class="form-control search-box" placeholder="Find by name ..." value="${searchValue}">-->
                <div class="table-navigate" style="margin-top: 20px">
                    <div class="table-navigate">
                        <form method="GET" action="ImportOrder" class="admin-filter-form">
                            <label for="startDate" class="me-2">From:</label>
                            <input name="fromDate" type="date" id="startDate" class="form-control me-3" value="<c:out value='${param.fromDate}'/>">

                            <label for="endDate" class="me-2">To:</label>
                            <input name="toDate" type="date" id="endDate" class="form-control me-3" value="<c:out value='${param.toDate}'/>">

                            <!--<button type="submit" onclick="filterByDate()" class="btn btn-primary">Filter</button>-->
                            <button type="submit" class="btn btn-primary">Filter</button>
                            <button type="button" class="btn btn-secondary" onclick="resetFilter()">Reset</button>
                        </form>
                    </div>
                </div>

                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Import ID</th>
                            <th>Date</th>
                            <th>Amount</th>
                            <th>Supplier</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="supplierTable">
                        <c:forEach items="${importOrders}" var="i">
                            <tr>
                                <td>${i.getIoid()}</td>
                                <td>${i.getImportDate()}</td>
                                <td>${i.getPriceFormatted()}</td>
                                <td  style="word-wrap: break-word; white-space: normal; max-width: 200px;">${i.getSupplier().getName()}</td>
                                <td>
                                    <div class="table-actions">
                                        <a href="${pageContext.request.contextPath}/ImportOrder?id=${i.ioid}">
                                            <i class="ti-eye" aria-hidden="true"></i> Details
                                        </a>
                                        <a href="${pageContext.request.contextPath}/UpdateImportOrder?id=${i.ioid}">
                                            <i class="ti-pencil" aria-hidden="true"></i> Update
                                        </a>
                                        <form method="post" action="${pageContext.request.contextPath}/DeleteImportOrder"
                                              onsubmit="return confirm('Delete import order #${i.ioid}? Stock quantities will be adjusted.');">
                                            <input type="hidden" name="importId" value="${i.ioid}">
                                            <button type="submit" class="btn btn-danger">
                                                <i class="fa-solid fa-trash" aria-hidden="true"></i> Delete
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <!--          Start Modal Select Supplier            -->
            <div class="modal fade" id="createImportOrder" tabindex="-1" aria-labelledby="createImportOrderLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="createImportOrderLabel">Select Supplier</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <table class="table table-striped" id="supplierListTable">
                                <thead>
                                    <tr>
                                        <th>Tax ID</th>
                                        <th>Company Name</th>
                                        <th>Email</th>
                                        <th>Phone</th>
                                        <th>Address</th>
                                        <th>Select</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${suppliers}" var="s">
                                        <tr>
                                            <td>${s.getTaxId()}</td>
                                            <td  style="word-wrap: break-word; white-space: normal; max-width: 200px;">${s.getName()}</td>
                                            <td>${s.getEmail()}</td>
                                            <td>${s.getPhoneNumber()}</td>
                                            <td>${s.getAddress()}</td>
                                            <td>
                                                <button class="btn btn-primary select-supplier" data-id="${s.getSupplierId()}">Select</button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="modal-footer">
                            <form id="importOrderForm" method="POST" action="CreateImportOrder">
                                <input type="hidden" name="supplierId" id="selectedSupplierId">
                                <button type="submit" class="btn btn-success" id="confirmSelection" disabled>Confirm</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <!--          End Modal Select Supplier            -->
        </div>
    </div>

    <!--        <script>
                function filterByDate() {
                    let startDate = document.getElementById("startDate").value;
                    let endDate = document.getElementById("endDate").value;
                    let table = document.getElementById("supplierTable");
                    let rows = table.getElementsByTagName("tr");
        
                    // Chuyển đổi định dạng ngày về timestamp để so sánh
                    let startTimestamp = new Date(startDate).getTime();
                    let endTimestamp = new Date(endDate).getTime();
                    
                    console.log(startTimestamp);
                    console.log(endTimestamp);
        
                    for (let i = 0; i < rows.length; i++) {
                        let dateCell = rows[i].getElementsByTagName("td")[1]; // Lấy cột ngày
                        console.log(dateCell);
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
            </script>-->

    <script>
        const openModalBtn = document.getElementById("openModalBtn");
        if (openModalBtn) {
            openModalBtn.addEventListener("click", function () {
                var myModal = new bootstrap.Modal(document.getElementById("createImportOrder"));
                myModal.show();
            });
        }
    </script>

    <script>
        function resetFilter() {
            document.getElementById('startDate').value = '';
            document.getElementById('endDate').value = '';
            window.location.href = 'ImportOrder'; // Load lại trang mà không có tham số lọc
        }
    </script>

    <script>
        // Hàm lấy giá trị tham số từ URL
        function getQueryParam(param) {
            const urlParams = new URLSearchParams(window.location.search);
            return urlParams.get(param);
        }

        // Gán giá trị fromDate và toDate nếu có trong URL
        document.getElementById("startDate").value = getQueryParam("fromDate") || "";
        document.getElementById("endDate").value = getQueryParam("toDate") || "";
    </script>

    <!--    <script>
            document.getElementById("searchInput").addEventListener("keypress", function (event) {
                if (event.key === "Enter") {
                    event.preventDefault();
                    let searchValue = this.value.trim();
                    if (searchValue !== "") {
                        window.location.href = "ImportOrder?name=" + encodeURIComponent(searchValue);
                    }
                }
            });
        </script>-->

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const confirmBtn = document.getElementById("confirmSelection");
            const supplierIdInput = document.getElementById("selectedSupplierId");

            // Xử lý sự kiện khi nhấn nút Select
            document.querySelectorAll(".select-supplier").forEach(button => {
                button.addEventListener("click", function () {
                    const supplierId = this.dataset.id;
                    const selectedRow = this.closest("tr");

                    // Save ID vào input ẩn
                    supplierIdInput.value = supplierId;

                    // Loại bỏ highlight khỏi tất cả các dòng
                    document.querySelectorAll("#supplierListTable tbody tr").forEach(row => {
                        row.classList.remove("table-success");
                    });

                    // Add highlight cho dòng được chọn
                    selectedRow.classList.add("table-success");

                    // Bật nút Confirm
                    confirmBtn.disabled = false;
                });
            });
        });
    </script>

</body>
</html>
