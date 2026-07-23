<%@page import="Models.Supplier"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Supplier Detail</title>
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
                border-radius: 10px;
            }
            .content {
                flex-grow: 1;
                padding: 12px;
                display: flex;
                flex-direction: column;
                gap: 20px;
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
            .table-container {
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0px 5px 15px rgba(0, 0, 0, 0.1);
                width: 100%; /* Kéo dài bảng ra hết chiều ngang trống */
            }
            .table th {
                background: #7D69FF;
                color: white;
                width: 20%;
                text-align: left;
            }
            .btn-action {
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 5px;
                text-decoration: none;
                margin-top: 20px;
            }
            .btn-action:hover {
                background-color: #5a4edc;
            }
        </style>
    </head>
    <body>
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            <div class="content">
            <jsp:include page="HeaderDashboard.jsp"></jsp:include>

                <div class="table-container">
                    <h3>Supplier Details</h3>
                    <table class="table table-bordered">
                    <c:choose>
                        <c:when test="${supplier != null}">
                            <tr>
                                <th>Tax ID</th>
                                <td>${supplier.getTaxId()}</td>
                            </tr>
                            <tr>
                                <th>Name</th>
                                <td style="word-wrap: break-word; white-space: normal; max-width: 200px;">${supplier.getName()}</td>
                            </tr>
                            <tr>
                                <th>Phone Number</th>
                                <td>${supplier.getPhoneNumber()}</td>
                            </tr>
                            <tr>
                                <th>Email</th>
                                <td>${supplier.getEmail()}</td>
                            </tr>
                            <tr>
                                <th>Address</th>
                                <td>${supplier.getAddress()}</td>
                            </tr>
                            <tr>
                                <th>Status</th>
                                <td>
                                    <span class="badge ${supplier.getActivate() == 1 ? 'bg-success' : 'bg-danger'}">
                                        ${supplier.getStatus()}
                                    </span>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="2" class="text-danger text-center">Supplier not found!</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </table>
                <button class="btn btn-primary btn-action" data-bs-toggle="modal" data-bs-target="#updateSupplierModal" style=" height: auto">Update</button>
                <button class="btn btn-danger btn-action" id="deleteBtn" data-bs-toggle="modal" data-bs-target="#deleteSupplierModal" style="height: auto">Delete</button>

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

                <!--          Start Modal Update Supplier            -->
                <c:set var="s" value="${supplier}"/>
                <div class="modal fade" id="updateSupplierModal" tabindex="-1" aria-labelledby="updateSupplierModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="updateSupplierModalLabel">Edit Supplier</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <form id="updateSupplierForm" method="POST" action="UpdateSupplier?id=${s.getSupplierId()}">
                                    <div class="mb-3">
                                        <label for="taxId" class="form-label">Tax ID</label>
                                        <input value="${s.getTaxId()}" name="taxNumber" type="tel" class="form-control" id="taxId" pattern="[0-9]{10}" required minlength="10" maxlength="10" title="Tax ID must contain only numbers and be 10 digits">
                                    </div>
                                    <div class="mb-3">
                                        <label for="companyName" class="form-label">Company Name</label>
                                        <input value="${s.getName()}" name="name" type="text" class="form-control" id="companyName" required maxlength="255">
                                    </div>
                                    <div class="mb-3">
                                        <label for="email" class="form-label">Email</label>
                                        <input value="${s.getEmail()}" name="email" type="email" class="form-control" id="email" required>
                                    </div>
                                    <div class="mb-3">
                                        <label for="phoneNumber" class="form-label">Phone Number</label>
                                        <input value="${s.getPhoneNumber()}" name="phone" type="tel" class="form-control" id="phoneNumber" pattern="[0-9]{10}" required minlength="10" maxlength="10" title="Phone number must contain only numbers and be 10 digits">
                                    </div>
                                    <div class="mb-3">
                                        <label for="address" class="form-label">Address</label>
                                        <input value="${s.getAddress()}" name="address" type="text" class="form-control" id="address" required pattern="^[^,]+,\s[^,]+,\s[^,]+$" title="Address must have 3 parts separated by commas (e.g., 123 Tech Street, District 1, Ho Chi Minh City)">
                                    </div>
                                    <div class="mb-3">
                                        <label for="status" class="form-label">Status</label>
                                        <select name="status" id="status" class="form-select">
                                            <option value="1" ${s.getActivate() == 1 ? 'selected' : ''}>Active</option>
                                            <option value="0" ${s.getActivate() == 0 ? 'selected' : ''}>Inactive</option>
                                        </select>
                                    </div>
                                    <button type="submit" class="btn btn-success">Save</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <!--          End Modal Update Supplier            -->
            </div>
        </div>
        <%
            String errorMessage = (String) session.getAttribute("error");
            if (errorMessage != null) {
        %>
        <script>
            document.getElementById("errorMessage").innerText = "<%= errorMessage%>";
            var errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
            errorModal.show();
        </script>
        <%
                session.removeAttribute("error");
            }
        %>
        <script>
            document.getElementById("deleteBtn").addEventListener("click", function (event) {
                var confirmDelete = confirm("Are you sure you want to deleted this supplier ?");
                if (!confirmDelete) {
                    event.preventDefault();
                } else {
                    window.location.href = "DeleteSupplier?id=${s.getSupplierId()}";
                }
            });
        </script>
    </body>
</html>
