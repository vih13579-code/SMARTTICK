
<%@page import="java.util.List"%>
<%@page import="Models.Product"%>
<%@page import="Models.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome for icons -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet">
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

            /* Hiệu ứng hover khi di chuột */
            .clickable-row {
                cursor: pointer;
                /*transition: background-color 0.2s ease-in-out, color 0.2s ease-in-out;*/
            }

            /* Hiệu ứng hover khi di chuột vào cả hàng */
            .clickable-row:hover td {
                background-color: #f8f9fa; /* Màu nền xám nhạt khi hover */
                color: red;
            }

            .clickable-product-row {
                cursor: pointer;
            }

            .clickable-product-row:hover td {
                background-color: #f8f9fa;
                color: red;
            }

        </style>
    </head>
    <body class="admin-ops-page">
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            <div class="content">
            <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                <div class="container mt-4">
                <%--<c:set value="${supplier}" var="sup"></c:set>--%>
                <c:set value="${sessionScope.supplier}" var="sup"></c:set>
                <c:set value="${importOrder}" var="importOrder"></c:set>
                <c:set value="0" var="sum"></c:set>
                    <!-- Start List Selected Suppliers -->
                    <div class="table-container style"margin-top: 20px"">
                        <div class="table-navigate">
                            <h3>Selected Supplier</h3>
                            <button id="openModalBtn" class="btn btn-detail" style="background-color: #BDF3BD; height: 100%">Select Supplier</button>
                        </div>
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Tax ID</th>
                                    <th>Company Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Address</th>
                                </tr>
                            </thead>
                            <tbody id="supplierTable">
                                <tr>
                                    <td>${sup.getTaxId()}</td>
                                <td>${sup.getName()}</td>
                                <td>${sup.getEmail()}</td>
                                <td>${sup.getPhoneNumber()}</td>
                                <td>${sup.getAddress()}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <!-- End List Selected Suppliers -->

                <!-- Start List Selected Suppliers -->
                <div class="table-container" style="margin-top: 20px">
                    <div class="table-navigate">
                        <h3>Selected Products</h3>
                        <button id="openProductModalBtn" class="btn btn-detail" style="background-color: #BDF3BD; height: 100%">Select Product</button>
                    </div>

                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Product ID</th>
                                <th>Product Name</th>
                                <th>Model</th>
                                <th>Import Quantity</th>
                                <th>Import Price</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody id="productTable">
                            <%--<c:forEach items="${selectedProducts}" var="d">--%>
                            <c:forEach items="${sessionScope.selectedProducts}" var="d">
                                <tr>
                                    <td>${d.getProduct().getProductId()}</td>
                                    <td>${d.getProduct().getModel()}</td>
                                    <td>${d.getProduct().getFullName()}</td>
                                    <td>${d.getQuantity()}</td>
                                    <td>${d.getPriceFormatted()}</td>
                                    <td>
                                        <button class="btn btn-warning edit-product"
                                                data-id="${d.getProduct().getProductId()}"
                                                data-name="${d.getProduct().getFullName()}"
                                                data-model="${d.getProduct().getModel()}"
                                                data-quantity="${d.getQuantity()}"
                                                data-price="${d.getImportPrice()}">
                                            Edit
                                        </button>
                                    </td>
                                </tr>
                                <c:set var="sum" value="${sum + d.getQuantity() * d.getImportPrice()}" scope="page"></c:set>
                            </c:forEach>
                            <tr>
                                <td colspan="3"></td>
                                <td class="text-end fw-bold" style="text-align: left">Total:</td>
                                <td class="text-start fw-bold" id="totalAmount">${sum} VND</td>
                            </tr>
                        </tbody>
                    </table>
                    <button type="button" class="btn btn-success" onclick="cancelImportOrder()">Cancel</button>
                    <button type="button" class="btn btn-success" onclick="redirectToImport()">Import</button>
                </div>
            </div>
            <!-- End List Selected Suppliers -->

            <!--          Start Modal Select Supplier            -->
            <div class="modal fade" id="createImportOrder" tabindex="-1" aria-labelledby="createImportOrderLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="createImportOrderLabel">Select Supplier</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <!-- Input Search -->
                            <input type="text" id="searchSupplierInput" class="form-control mb-2" placeholder="Search supplier...">

                            <!-- Bảng cuộn -->
                            <div style="max-height: 400px; overflow-y: auto;">
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
                                                <td  style="word-wrap: break-word; white-space: normal; max-width: 200px;">${s.getEmail()}</td>
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
                        </div>
                        <div class="modal-footer">
                            <form id="importOrderForm" method="POST" action="ImportStock">
                                <input type="hidden" name="supplierId" id="selectedSupplierId">
                                <button type="submit" class="btn btn-success" id="confirmSelection" disabled>Confirm</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <!--          End Modal Select Supplier            -->

            <!-- Start Modal import price and quantity -->
            <div class="modal fade" id="productInputModal" tabindex="-1" aria-labelledby="productInputLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="productInputLabel">Enter Quantity & Price</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <form id="productInputForm">
                                <input type="hidden" id="selectedSupplierId">

                                <div class="mb-3">
                                    <label for="inputQuantity" class="form-label">Quantity</label>
                                    <input type="number" class="form-control" id="inputQuantity" min="1" onclick="resetHighlight()" required>
                                </div>

                                <div class="mb-3">
                                    <label for="inputPrice" class="form-label">Price</label>
                                    <input type="number" class="form-control" id="inputPrice" min="1" onclick="resetHighlight()" required>
                                </div>

                                <button type="button" class="btn btn-success" id="confirmProduct">Confirm</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <!-- End Modal import price and quantity -->

            <!-- ================================================================================================================================================================================================================================= -->

            <!--          Start Modal Select Product            -->
            <div class="modal fade" id="selectProductModal" tabindex="-1" aria-labelledby="selectProductLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="selectProductLabel">Select Product</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <!-- Input Search -->
                            <input type="text" id="searchProductInput" class="form-control mb-2" placeholder="Search product...">

                            <!-- Bảng cuộn -->
                            <div style="max-height: 400px; overflow-y: auto;">
                                <table class="table table-striped" id="productListTable">
                                    <thead>
                                        <tr>
                                            <th>Product ID</th>
                                            <th>Name</th>
                                            <th>Model</th>
                                            <th>Import Quantity</th>
                                            <th>Import Price</th>
                                            <th>Select</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%--<c:forEach items="${products}" var="p">--%>
                                        <c:if test="${sessionScope.products == null}">a</c:if>
                                        <c:forEach items="${sessionScope.products}" var="p">
                                            <tr>
                                                <td>${p.getProductId()}</td>
                                                <td>${p.getFullName()}</td>
                                                <td>${p.getModel()}</td>
                                                <td>
                                                    <input type="number" class="form-control product-quantity" data-id="${p.getProductId()}" min="1" placeholder="Enter quantity">
                                                </td>
                                                <td>
                                                    <input type="number" class="form-control product-price" data-id="${p.getProductId()}" min="1000" step="0.01" placeholder="Enter price">
                                                </td>
                                                <td>
                                                    <button class="btn btn-primary select-product" data-id="${p.getProductId()}">Select</button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <form id="productOrderForm" method="POST" action="ImportStock">
                                <input type="hidden" name="productId" id="selectedProductId" value="${p.getProductId()}">
                                <input type="hidden" name="importQuantity" id="selectedProductQuantity">
                                <input type="hidden" name="importPrice" id="selectedProductPrice">
                                <button type="submit" class="btn btn-success" id="confirmProductSelection" disabled>Confirm</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <!--          End Modal Select Product            -->

            <!-- Start Edit Modal -->
            <div class="modal fade" id="editProductModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Edit Imported Product</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <form id="editProductForm" method="POST" action="ImportStock">
                                <input type="hidden" id="editProductId" name="productEditedId">

                                <div class="mb-3">
                                    <label class="form-label">Product Name:</label>
                                    <input type="text" class="form-control" id="editProductName" readonly>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Model:</label>
                                    <input type="text" class="form-control" id="editProductModel" readonly>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Import Quantity:</label>
                                    <input type="number" class="form-control" id="editProductQuantity" name="quantity" min="1">
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Import Price:</label>
                                    <input type="number" class="form-control" id="editProductPrice" name="price" min="1000" step="1">
                                </div>
                            </form>
                        </div>
                        <div class="modal-footer">
                            <button name="action" value="delete" type="submit" class="btn btn-danger" form="editProductForm" id="deleteProduct">Delete</button>
                            <button name="action" value="save" type="submit" class="btn btn-success" form="editProductForm">Save</button>
                        </div>
                    </div>
                </div>
            </div>
            <!-- End Edit Modal -->
        </div>

        <!-- Toast Notifications -->
        <div class="toast-container position-fixed top-0 end-0 p-3">
            <div id="errorToast" class="toast text-bg-danger border-0" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <strong class="me-auto">Notification</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                    <%= session.getAttribute("error")%>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                let totalAmountElement = document.getElementById("totalAmount");
                let amount = parseFloat(totalAmountElement.innerText.replace(/[^\d.-]/g, '')); // Lấy số từ chuỗi

                if (!isNaN(amount)) {
                    totalAmountElement.innerText = amount.toLocaleString('vi-VN', {style: 'currency', currency: 'VND'});
                }
            });
        </script>

        <script>
            function searchSupplier() {
                let input = document.getElementById("searchSupplierInput");
                let filter = input.value.toLowerCase();
                let table = document.getElementById("supplierListTable");
                let rows = table.getElementsByTagName("tr");

                for (let i = 1; i < rows.length; i++) {
                    let nameCell = rows[i].getElementsByTagName("td")[1];
                    if (nameCell) {
                        let nameText = nameCell.textContent || nameCell.innerText;
                        rows[i].style.display = nameText.toLowerCase().includes(filter) ? "" : "none";
                    }
                }
            }
        </script>

        <script>
            function searchProduct() {
                let input = document.getElementById("searchProductInput").value.toLowerCase();
                let table = document.getElementById("productList");
                let rows = table.getElementsByTagName("tr");

                for (let i = 0; i < rows.length; i++) { // Lặp qua tất cả hàng
                    let nameCell = rows[i].getElementsByTagName("td")[0]; // Giả sử cột "Product Name" là cột đầu tiên (td[0])
                    if (nameCell) {
                        let nameText = nameCell.textContent || nameCell.innerText;
                        rows[i].style.display = nameText.toLowerCase().includes(input) ? "" : "none";
                    }
                }
            }
        </script>
        <!-- Search -->
        <script>
            document.getElementById("openModalBtn").addEventListener("click", function () {
                var myModal = new bootstrap.Modal(document.getElementById("createImportOrder"));
                myModal.show();
            });
        </script>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                // Lấy tham chiếu đến nút "Select Product"
                let openProductModalBtn = document.getElementById("openProductModalBtn");

                // Khi nút được nhấn, hiển thị modal product
                openProductModalBtn.addEventListener("click", function () {
                    let productModal = new bootstrap.Modal(document.getElementById("selectProductModal"));
                    productModal.show();
                });
            });

        </script>
        <!-- Modal -->
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

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const confirmProductBtn = document.getElementById("confirmProductSelection");
                const productIdInput = document.getElementById("selectedProductId");

                const productQuantityInput = document.getElementById("selectedProductQuantity");
                const productPriceInput = document.getElementById("selectedProductPrice");

                // Xử lý sự kiện khi nhấn nút Select trong danh sách products
                document.querySelectorAll(".select-product").forEach(button => {
                    button.addEventListener("click", function () {
                        const productId = this.dataset.id;
                        const selectedRow = this.closest("tr");

                        const quantityInput = selectedRow.querySelector(".product-quantity");
                        const priceInput = selectedRow.querySelector(".product-price");

                        const productQuantity = parseInt(quantityInput.value);
                        const productPrice = parseFloat(priceInput.value);

                        if (isNaN(productQuantity) || productQuantity < 1) {
                            alert("Please enter a valid quantity (min: 1).");
                            quantityInput.value = "";
                            quantityInput.focus();

                            const selectedRow = this.closest("tr");
                            // Bỏ highlight cho dòng được chọn
                            selectedRow.classList.remove("table-success");
                            // Bật nút Confirm
                            confirmProductBtn.disabled = true;

                            return;
                        }
                        if (isNaN(productPrice) || productPrice < 1000) {
                            alert("Please enter a valid price (min: 1000).");
                            priceInput.value = "";
                            priceInput.focus();

                            const selectedRow = this.closest("tr");
                            // Bỏ highlight cho dòng được chọn
                            selectedRow.classList.remove("table-success");
                            // Bật nút Confirm
                            confirmProductBtn.disabled = true;

                            return;
                        }

                        // Save dữ liệu vào input ẩn để gửi đến Servlet
                        productQuantityInput.value = productQuantity;
                        productPriceInput.value = productPrice;

                        // Save ID vào input ẩn
                        productIdInput.value = productId;

                        // Loại bỏ highlight khỏi tất cả các dòng
                        document.querySelectorAll("#productListTable tbody tr").forEach(row => {
                            row.classList.remove("table-success");
                        });

                        // Add highlight cho dòng được chọn
                        selectedRow.classList.add("table-success");

                        // Bật nút Confirm
                        confirmProductBtn.disabled = false;
                    });
                });
            });
        </script>

        <script>
            function resetHighlight() {
                // Delete highlight của tất cả các dòng đang có class "table-success"
                document.querySelectorAll("tr.table-success").forEach(row => row.classList.remove("table-success"));
            }

// Lắng nghe sự kiện focus trên tất cả input trong bảng
            document.addEventListener("focusin", function (event) {
                if (event.target.classList.contains("product-quantity") || event.target.classList.contains("product-price")) {
                    let row = event.target.closest("tr"); // Tìm thẻ <tr> chứa ô input
                    if (row) {
                        row.classList.remove("table-success"); // Delete highlight của dòng đó
                    }
                }
            });
        </script>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const editModal = new bootstrap.Modal(document.getElementById("editProductModal"));
                const editForm = document.getElementById("editProductForm");

                document.querySelectorAll(".edit-product").forEach(button => {
                    button.addEventListener("click", function () {
                        // Lấy dữ liệu từ nút Edit
                        const productId = this.dataset.id;
                        const productName = this.dataset.name;
                        const productModel = this.dataset.model;
                        const quantity = this.dataset.quantity;
                        const price = this.dataset.price;

                        // Điền dữ liệu vào form
                        document.getElementById("editProductId").value = productId;
                        document.getElementById("editProductName").value = productName;
                        document.getElementById("editProductModel").value = productModel;
                        document.getElementById("editProductQuantity").value = quantity;
                        document.getElementById("editProductPrice").value = price;

                        // Showing modal
                        editModal.show();
                    });
                });

                // Delete products
                //                document.getElementById("deleteProduct").addEventListener("click", function () {
                //                    if (confirm("Are you sure you want to delete this product?")) {
                //                        editForm.action = "ImportStock";
                //                        editForm.submit();
                //                    }
                //                });

            });
        </script>
        <!-- tuong tac -->
        <%
            String error = (String) session.getAttribute("error");
            if (error != null) {
                session.removeAttribute("error");
            }
        %>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                let errorMessage = "<%= error%>";
                if (errorMessage && errorMessage !== "null") {
                    let toastElement = document.getElementById("errorToast");
                    let toast = new bootstrap.Toast(toastElement);
                    toast.show();
                }
            });
        </script>
        <!-- error -->
        <script>
            //            function redirectToImport() {
            //                const urlParams = new URLSearchParams(window.location.search);
            ////                const id = urlParams.get('id'); // Lấy id từ URL
            //                window.location.href = `ImportStock`;
            ////                if (id) {
            ////                    window.location.href = `ImportStock?importStockId=\${id}`;
            ////                } else {
            ////                    alert("Không tìm thấy ID trong URL!");
            ////                }
            //            }

            function redirectToImport() {
                const form = document.createElement("form");
                form.method = "POST";
                form.action = "ImportStock";

                document.body.appendChild(form);
                form.submit();
            }

        </script>
        <script>
            function cancelImportOrder() {
                window.location.href = 'ImportStock?status=cancel';
            }
        </script>
        <!-- submit final -->

        <!-- giao dien danh sach -->
        <script>
            document.getElementById("searchProductInput").addEventListener("keyup", function () {
                let filter = this.value.toLowerCase();
                let rows = document.querySelectorAll("#productListTable tbody tr");

                rows.forEach(row => {
                    let name = row.cells[1].textContent.toLowerCase();
                    let id = row.cells[0].textContent.toLowerCase();
                    if (name.includes(filter) || id.includes(filter)) {
                        row.style.display = "";
                    } else {
                        row.style.display = "none";
                    }
                });
            });
        </script>

        <script>
            document.getElementById("searchSupplierInput").addEventListener("keyup", function () {
                let filter = this.value.toLowerCase();
                let rows = document.querySelectorAll("#supplierListTable tbody tr");

                rows.forEach(row => {
                    let taxId = row.cells[0].textContent.toLowerCase();
                    let companyName = row.cells[1].textContent.toLowerCase();
                    let email = row.cells[2].textContent.toLowerCase();

                    if (taxId.includes(filter) || companyName.includes(filter) || email.includes(filter)) {
                        row.style.display = "";
                    } else {
                        row.style.display = "none";
                    }
                });
            });
        </script>
    </body>
</html>

