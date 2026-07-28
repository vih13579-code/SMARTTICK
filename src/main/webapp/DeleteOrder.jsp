<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="assets/css/orderDetail.css">
        <style>
            /* Modal style */
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                justify-content: center;
                align-items: center;
            }
            .modal-content {
                background-color: #fff;
                padding: 20px;
                border-radius: 10px;
                text-align: center;
                width: 400px;
                max-height: 80vh;
                overflow-y: auto;
            }
            .btn-danger, .btn-secondary {
                margin: 10px;
                padding: 10px 20px;
                font-size: 16px;
            }
            /* Form container */
            .form-container {
                max-width: 600px;
                margin: 100px auto;
                padding: 20px;
                background-color: #f9f9f9;
                border-radius: 8px;
                box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
            }
            .dropdown select {
                padding: 10px;
                font-size: 16px;
                width: 100%;
                border-radius: 5px;
                border: 1px solid #ccc;
            }
            .btn-success {
                width: 100%;
                padding: 12px;
                font-size: 18px;
                border-radius: 5px;
            }
            .btn-success:hover {
                background-color: #28a745;
                border-color: #218838;
            }
            /* Heading style */
            h2 {
                color: #e74c3c;
                text-align: center;
                font-size: 1.5rem;
                font-weight: bold;
                margin-top: 30px;
            }
            .alert-warning {
                text-align: center;
                font-size: 1.1rem;
                background-color: #f8d7da;
                color: #721c24;
                padding: 15px;
                border-radius: 5px;
                margin-top: 20px;
            }
            .fixed-header {
                position: fixed;
                top: 0;
                left: 250px; /* Äiá»u chá»‰nh Ä‘á»ƒ trÃ¡nh che sidebar */
                width: calc(100% - 250px); /* Chiá»u rá»™ng trá»« Ä‘i sidebar */
                /*background-color: white;*/
                z-index: 1050;
                padding: 10px 20px;

            }

        </style>
        <title>Delete Order Page</title>
    </head>
    <body class="admin-ops-page">

        <jsp:include page="SidebarDashboard.jsp" />
        <div class="fixed-header"><jsp:include page="HeaderDashboard.jsp"></jsp:include></div>

        <!-- Form container -->
        <div class="form-container">
            <h2>Warning: This action will permanently delete orders</h2>
            <h3 class="alert-warning">
                Please select the timeframe for which orders should be deleted (1, 3, 6, or 12 months ago).
            </h3>
            <form id="previewForm">
                <div class="dropdown mb-3">
                    <label for="delete" class="form-label">Select Timeframe for Deletion:</label>
                    <select name="delete" id="delete">
                        <option value="1">1 Month Ago</option>
                        <option value="3">3 Months Ago</option>
                        <option value="6">6 Months Ago</option>
                        <option value="12">12 Months Ago</option>
                    </select>
                </div>
                <button type="button" class="btn btn-success" onclick="previewOrders()">
                    <i class="fa-solid fa-eye"></i> Preview Orders to be Deleted
                </button>
            </form>
        </div>

        <!-- Preview Modal -->
        <div id="previewModal" class="modal">
            <div class="modal-content">
                <h3>Orders to be Deleted</h3>
                <div id="orderList">
                    <!-- Danh sÃ¡ch orders sáº½ Ä‘Æ°á»£c load táº¡i Ä‘Ã¢y -->
                </div>
                <button id="confirmDeleteBtn" class="btn btn-danger">Yes, Delete</button>
                <button id="cancelPreviewBtn" class="btn btn-secondary">Cancel</button>
            </div>
        </div>

        <!-- Success Modal -->
        <div id="successModal" class="modal">
            <div class="modal-content">
                <h3>Deletion Successful!</h3>
                <p>You will be redirected to the main page shortly.</p>
            </div>
        </div>
        <script>
            // HÃ m hiá»ƒn thá»‹ modal preview sau khi láº¥y danh sÃ¡ch orders cáº§n xÃ³a
            function previewOrders() {
                const timeframe = document.getElementById("delete").value;
                // Gá»i AJAX Ä‘á»ƒ láº¥y danh sÃ¡ch orders (giáº£ sá»­ servlet PreviewDeleteOrderServlet tráº£ vá» HTML)
                fetch("DeleteOrderServlet?delete=" + timeframe)
                        .then(response => response.text())
                        .then(data => {
                            document.getElementById("orderList").innerHTML = data;
                            document.getElementById("previewModal").style.display = "flex";
                        })
                        .catch(error => console.error("Error fetching preview orders:", error));
            }

            // Khi ngÆ°á»i dÃ¹ng nháº¥n "Yes, Delete", gá»­i yÃªu cáº§u xÃ³a orders
            document.getElementById("confirmDeleteBtn").onclick = function () {
                const timeframe = document.getElementById("delete").value;
                // Gá»­i yÃªu cáº§u POST tá»›i DeleteOrderServlet
                fetch("DeleteOrderServlet", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: "delete=" + encodeURIComponent(timeframe)
                })
                        .then(response => response.text())
                        .then(data => {
                            document.getElementById("previewModal").style.display = "none";

                            if (data.trim() === "success") {
                                document.getElementById("successModal").style.display = "flex";
                                setTimeout(() => {
                                    window.location.href = "ViewOrderListServlet";
                                }, 2000);
                            } else {
                                alert("No orders were deleted. Please check the timeframe or try again.");
                            }
                        })

            };

            // Khi ngÆ°á»i dÃ¹ng nháº¥n Cancel trong modal preview
            document.getElementById("cancelPreviewBtn").onclick = function () {
                document.getElementById("previewModal").style.display = "none";
            };
        </script>

    </body>
</html>

