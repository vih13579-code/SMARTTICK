
<%@page import="java.util.Map"%>
<%@page import="Models.Product"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Import Statistic</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            body {
                display: flex;
                background-color: #f8f9fa;
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
            /*==================================================================*/
            .container {
                max-width: 1200px;
                background: #fff;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            }
            h1 {
                color: #007bff;
                margin-bottom: 20px;
            }
            h2 {
                font-size: 20px;
                margin-top: 20px;
                color: #343a40;
                border-bottom: 2px solid #007bff;
                padding-bottom: 5px;
            }
            .chart-container {
                width: 100%;
                height: 300px;
                padding: 10px;
                background: #ffffff;
                border-radius: 8px;
                box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
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

        </style>
    </head>
    <body class="admin-ops-page">
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            <div class="content">
                <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                <!--<div class="container mt-5">-->
                <div class="container" style="margin-top: 20px">
                    <h1 class="text-center" style="text-align: center;">Import Statistic</h1>
                    <div class="row">
                        <div class="col-md-6">
                            <h2 style="text-align: center;">Daily Stock Import</h2>
                            <div class="chart-container">
                                <canvas id="orderByDayChart"></canvas>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <h2 style="text-align: center;">Monthly Stock Import</h2>
                            <div class="chart-container">
                                <canvas id="orderByMonthChart"></canvas>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <h2 style="text-align: center;">Stock Import by Supplier</h2>
                            <div class="chart-container">
                                <canvas id="supplierChart"></canvas>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <h2 style="text-align: center;">Stock Import by Product</h2>
                            <div class="chart-container">
                                <canvas id="productChart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <script>
                // Láº¥y dá»¯ liá»‡u JSON tá»« server vÃ  parse thÃ nh object JavaScript
                const orderCountByDay = JSON.parse('${orderCountByDayJson}');
                const orderCountByMonth = JSON.parse('${orderCountByMonthJson}');
                const ordersBySupplier = JSON.parse('${ordersBySupplierJson}');
                const topImportedProducts = JSON.parse('${topImportedProductsJson}');

                // HÃ m váº½ biá»ƒu Ä‘á»“
                function renderChart(chartId, labels, data, label, type = 'bar') {
                    const ctx = document.getElementById(chartId);
                    if (!ctx)
                        return;
                    new Chart(ctx, {
                        type: type,
                        data: {
                            labels: labels,
                            datasets: [{
                                    label: label,
                                    data: data,
                                    backgroundColor: type === 'bar' ? 'rgba(54, 162, 235, 0.5)' : ['#ff6384', '#36a2eb', '#ffcd56', '#4bc0c0', '#9966ff'],
                                    borderColor: type === 'bar' ? 'rgba(54, 162, 235, 1)' : [],
                                    borderWidth: 1
                                }]
                        },
                        options: {
                            responsive: true,
                            scales: type === 'bar' ? {y: {beginAtZero: true}} : {}
                        }
                    });
                }

                // Cháº¡y khi DOM Ä‘Ã£ táº£i xong
                document.addEventListener("DOMContentLoaded", function () {
                    renderChart("orderByDayChart", Object.keys(orderCountByDay), Object.values(orderCountByDay), "Import Orders by Day");
                    renderChart("orderByMonthChart", Object.keys(orderCountByMonth), Object.values(orderCountByMonth), "Import Orders by Month");
                    renderChart("supplierChart", Object.keys(ordersBySupplier), Object.values(ordersBySupplier), "Suppliers", 'pie');
                    renderChart("productChart", Object.keys(topImportedProducts), Object.values(topImportedProducts), "Top Imported Products", 'bar');
                });
        </script>
    </body>
</html>

