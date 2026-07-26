
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
            }

            .table-navigate{
                display: flex;
                justify-content: space-between;
            }
        </style>
    </head>
    <body class="admin-ops-page">
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            <div class="content">
            <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                <div class="table-container" style="margin-top: 20px">
                    <div class="table-navigate">
                        <h3>Suppliers</h3>
                        <!--
                        <input type="text" id="searchInput" class="form-control search-box" placeholder="Find by name ..." onkeyup="searchTable()">-->
                        <button class="btn btn-detail" data-bs-toggle="modal" data-bs-target="#createSupplierModal" style="background-color: #BDF3BD; height: 100%;">Create</button>
                    </div>

                    <div class="table-navigate">
                        <div class="table-navigate">
                            <input type="text" id="searchInput" class="form-control search-box" placeholder="Find by name ..." value="${searchValue}">
                        <button class="btn btn-primary me-2" onclick="searchSupplier()">Search</button>
                        <button type="button" class="btn btn-secondary" onclick="resetSearch()">Reset</button>
                    </div>
                </div>

                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Tax ID</th>
                            <th>Name</th>
                            <th>Phone Number</th>
                            <th>Email</th>
                            <th>Address</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody id="supplierTable">
                        <c:forEach items="${suppliers}" var="s">
                            <tr>
                                <td>${s.getTaxId()}</td>
                                <td  style="word-wrap: break-word; white-space: normal; max-width: 200px;">${s.getName()}</td>
                                <td>${s.getPhoneNumber()}</td>
                                <td>${s.getEmail()}</td>
                                <td>${s.getShortedAddress()}</td>
                                <td>
                                    <span class="badge ${s.getActivate() == 1 ? 'bg-success' : 'bg-danger'}">${s.getStatus()}</span>
                                </td>
                                <td>
                                    <a href="Supplier?id=${s.getSupplierId()}" class="btn btn-detail" style="background-color: #BDF3BD">Detail</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div> 

            <!--          Start Modal Create Supplier            -->
            <div class="modal fade" id="createSupplierModal" tabindex="-1" aria-labelledby="createSupplierModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="createSupplierModalLabel">New Supplier</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="importSupplierForm" method="POST" action="CreateSupplier">
                                <div class="mb-3">
                                    <label for="taxId" class="form-label">Tax ID</label>
                                    <input name="taxNumber" type="text" class="form-control" id="taxId" pattern="[0-9]{10}" required minlength="10" maxlength="10" title="Tax ID must contain only numbers and be 10 digits">
                                </div>
                                <div class="mb-3">
                                    <label for="companyName" class="form-label">Company Name</label>
                                    <input name="name" type="text" class="form-control" id="companyName" required maxlength="255" title="The maximum length of the name is 255 characters.">
                                </div>
                                <div class="mb-3">
                                    <label for="email" class="form-label">Email</label>
                                    <input name="email" type="email" class="form-control" id="email" required>
                                </div>
                                <div class="mb-3">
                                    <label for="phoneNumber" class="form-label" require>Phone Number</label>
                                    <input name="phone" type="tel" class="form-control" id="phoneNumber" pattern="[0-9]{10}" required minlength="10" maxlength="10" title="Phone number must contain only numbers and be 10 digits">
                                </div>
                                <div class="mb-3">
                                    <label for="address" class="form-label">Address</label>
                                    <input name="address" type="text" class="form-control" id="address" required pattern="^[^,]+,\s[^,]+,\s[^,]+$" title="Address must have 3 parts separated by commas (e.g., 123 Tech Street, District 1, Ho Chi Minh City)">
                                </div>
                                <div class="mb-3">
                                    <label for="status" class="form-label">Status</label>
                                    <select name="status" id="status" class="form-select">
                                        <option value="1">Active</option>
                                        <option value="0">Inactive</option>
                                    </select>
                                </div>
                                <button type="submit" class="btn btn-success">Save</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <!--          End Modal Create Supplier            -->

            <!-- Start Error Modal -->
            <div class="modal fade" id="errorModal" tabindex="-1" aria-labelledby="errorModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="errorModalLabel">Error</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <p id="errorMessage"></p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            </div>
            <!-- End Error Modal -->

        </div>

        <!--        <script>
                    function searchTable() {
                        let input = document.getElementById("searchInput");
                        let filter = input.value.toLowerCase();
                        let table = document.getElementById("supplierTable");
                        let rows = table.getElementsByTagName("tr");
        
                        for (let i = 1; i < rows.length; i++) {
                            let nameCell = rows[i].getElementsByTagName("td")[1];
                            if (nameCell) {
                                let nameText = nameCell.textContent || nameCell.innerText;
                                rows[i].style.display = nameText.toLowerCase().includes(filter) ? "" : "none";
                            }
                        }
                    }
                </script>-->

        <script>
            document.getElementById("searchInput").addEventListener("keypress", function (event) {
                if (event.key === "Enter") {
                    event.preventDefault();
                    let searchValue = this.value.trim();
                    if (searchValue !== "") {
                        window.location.href = "SearchSupplier?name=" + encodeURIComponent(searchValue);
                    }
                }
            });
            function searchSupplier() {
                let searchValue = document.getElementById("searchInput").value.trim();
                if (searchValue !== "") {
                    window.location.href = "SearchSupplier?name=" + encodeURIComponent(searchValue);
                }
            }
        </script>


        <script>
            function resetSearch() {
                document.getElementById('searchInput').value = '';
                window.location.href = 'Supplier'; // Load lại trang mà không có tham số lọc
            }
        </script>

        <%
            String errorMessage = (String) session.getAttribute("error");
            if (errorMessage != null) {
        %>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                document.getElementById("errorMessage").innerText = "<%= errorMessage%>";
                var errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
                errorModal.show();
            });
        </script>
        <%
                session.removeAttribute("error");
            }
        %>

    </body>
</html>
