<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Statistic Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
        <style>
            .content {
                flex-grow: 1;
                padding: 10px 15px;
                margin-left: 240px;
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
                background-color: #60CDB6;
            }
            .stat-card:hover {
                transform: scale(1.08);
            }
        </style>
    </head>
    <body>
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
        <div class="content">
            <jsp:include page="HeaderDashboard.jsp"></jsp:include>
            <div class="container mt-4">
                <div class="row mt-4 g-4 justify-content-center text-center">
                    <div class="col-md-3">
                        <a href="InventoryStatisticServlet" class="stat-card">
                            <i class='bx bx-box bx-lg'></i>
                            <span>Inventory</span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
