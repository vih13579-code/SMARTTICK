<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Statistic Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            body {
                background: #f6f7fb;
            }
            .content {
                flex-grow: 1;
                padding: 10px 15px;
                margin-left: 240px;
            }
            .stat-card {
                background: #fff;
                border: 1px solid #e5e7eb;
                border-radius: 8px;
                padding: 16px;
                min-height: 120px;
                box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
            }
            .stat-card .icon {
                width: 38px;
                height: 38px;
                border-radius: 8px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background: #eef2ff;
                color: #4338ca;
                font-size: 22px;
            }
            .stat-card .label {
                color: #64748b;
                font-size: 13px;
                margin-top: 12px;
            }
            .stat-card .value {
                font-size: 24px;
                font-weight: 700;
                color: #0f172a;
            }
            .panel {
                background: #fff;
                border: 1px solid #e5e7eb;
                border-radius: 8px;
                padding: 18px;
                box-shadow: 0 4px 12px rgba(15, 23, 42, 0.05);
            }
            .chart-box {
                height: 320px;
            }
            @media (max-width: 900px) {
                .content {
                    margin-left: 0;
                }
            }
        </style>
    </head>
    <body>
        <jsp:include page="SidebarDashboard.jsp"/>
        <div class="content">
            <jsp:include page="HeaderDashboard.jsp"/>
            <div class="container-fluid mt-4">
                <div class="d-flex flex-wrap justify-content-between align-items-center gap-3 mb-3">
                    <div>
                        <h2 class="mb-1">Management Statistics</h2>
                        <div class="text-muted">${startDate} to ${endDate}</div>
                    </div>
                    <form class="row g-2 align-items-center" action="StatisticManagementServlet" method="get">
                        <div class="col-auto">
                            <select name="period" class="form-select">
                                <option value="today" ${period == 'today' ? 'selected' : ''}>Today</option>
                                <option value="yesterday" ${period == 'yesterday' ? 'selected' : ''}>Yesterday</option>
                                <option value="week" ${period == 'week' ? 'selected' : ''}>This Week</option>
                                <option value="month" ${period == 'month' ? 'selected' : ''}>This Month</option>
                                <option value="year" ${period == 'year' ? 'selected' : ''}>This Year</option>
                                <option value="custom" ${period == 'custom' ? 'selected' : ''}>Custom</option>
                            </select>
                        </div>
                        <div class="col-auto">
                            <input type="date" class="form-control" name="startDate" value="${startDate}">
                        </div>
                        <div class="col-auto">
                            <input type="date" class="form-control" name="endDate" value="${endDate}">
                        </div>
                        <div class="col-auto">
                            <button class="btn btn-primary"><i class="bx bx-filter-alt"></i></button>
                        </div>
                    </form>
                </div>

                <div class="row g-3">
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-id-card"></i></span>
                            <div class="label">Employees</div>
                            <div class="value">${overview.totalEmployees}</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-user"></i></span>
                            <div class="label">Customers</div>
                            <div class="value">${overview.totalCustomers}</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-package"></i></span>
                            <div class="label">Products</div>
                            <div class="value">${overview.totalProducts}</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-receipt"></i></span>
                            <div class="label">Orders</div>
                            <div class="value">${overview.totalOrders}</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-money"></i></span>
                            <div class="label">Total Revenue</div>
                            <div class="value"><fmt:formatNumber value="${overview.totalRevenue}" pattern="#,##0"/> VND</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-line-chart"></i></span>
                            <div class="label">Selected Revenue</div>
                            <div class="value"><fmt:formatNumber value="${overview.selectedRevenue}" pattern="#,##0"/> VND</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-ticket"></i></span>
                            <div class="label">Active Vouchers</div>
                            <div class="value">${overview.activeVouchers}</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-time-five"></i></span>
                            <div class="label">Expired Vouchers</div>
                            <div class="value">${overview.expiredVouchers}</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-cube"></i></span>
                            <div class="label">Total Stock</div>
                            <div class="value">${overview.totalStock}</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-error"></i></span>
                            <div class="label">Low Stock</div>
                            <div class="value">${overview.lowStockProducts}</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-x-circle"></i></span>
                            <div class="label">Out Of Stock</div>
                            <div class="value">${overview.outOfStockProducts}</div>
                        </div>
                    </div>
                    <div class="col-xl-3 col-md-6">
                        <div class="stat-card">
                            <span class="icon"><i class="bx bx-calendar-check"></i></span>
                            <div class="label">Selected Orders</div>
                            <div class="value">${overview.selectedOrders}</div>
                        </div>
                    </div>
                </div>

                <div class="row g-3 mt-1">
                    <div class="col-lg-6">
                        <div class="panel">
                            <h5>Revenue Chart</h5>
                            <div class="chart-box"><canvas id="revenueChart"></canvas></div>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="panel">
                            <h5>Orders Chart</h5>
                            <div class="chart-box"><canvas id="ordersChart"></canvas></div>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="panel">
                            <h5>Customer Growth</h5>
                            <div class="chart-box"><canvas id="customerChart"></canvas></div>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="panel">
                            <h5>Top Selling Products</h5>
                            <div class="table-responsive">
                                <table class="table align-middle">
                                    <thead><tr><th>Product</th><th>Sold</th><th>Revenue</th></tr></thead>
                                    <tbody>
                                        <c:forEach items="${topProducts}" var="p">
                                            <tr>
                                                <td><c:out value="${p.fullName}"/></td>
                                                <td>${p.soldQuantity}</td>
                                                <td><fmt:formatNumber value="${p.revenue}" pattern="#,##0"/> VND</td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty topProducts}">
                                            <tr><td colspan="3" class="text-center text-muted">No product sales in this period.</td></tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-12">
                        <div class="panel">
                            <h5>Top Customers</h5>
                            <div class="table-responsive">
                                <table class="table align-middle">
                                    <thead><tr><th>Customer</th><th>Orders</th><th>Revenue</th></tr></thead>
                                    <tbody>
                                        <c:forEach items="${topCustomers}" var="cst">
                                            <tr>
                                                <td><c:out value="${cst.fullName}"/></td>
                                                <td>${cst.orderCount}</td>
                                                <td><fmt:formatNumber value="${cst.revenue}" pattern="#,##0"/> VND</td>
                                            </tr>
                                        </c:forEach>
                                        <c:if test="${empty topCustomers}">
                                            <tr><td colspan="3" class="text-center text-muted">No customers in this period.</td></tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script>
            const revenueData = ${empty revenueChartJson ? '[]' : revenueChartJson};
            const ordersData = ${empty ordersChartJson ? '[]' : ordersChartJson};
            const customerData = ${empty customerGrowthJson ? '[]' : customerGrowthJson};

            function buildChart(id, rows, label, color, fill) {
                const ctx = document.getElementById(id);
                if (!ctx) {
                    return;
                }
                new Chart(ctx, {
                    type: fill ? 'line' : 'bar',
                    data: {
                        labels: rows.map(row => row.label),
                        datasets: [{
                            label: label,
                            data: rows.map(row => row.value),
                            borderColor: color,
                            backgroundColor: fill ? color + '33' : color,
                            fill: fill,
                            tension: 0.25
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {display: false}
                        },
                        scales: {
                            y: {beginAtZero: true}
                        }
                    }
                });
            }

            buildChart('revenueChart', revenueData, 'Revenue', '#2563eb', true);
            buildChart('ordersChart', ordersData, 'Orders', '#16a34a', false);
            buildChart('customerChart', customerData, 'Customers', '#f59e0b', true);
        </script>
    </body>
</html>
