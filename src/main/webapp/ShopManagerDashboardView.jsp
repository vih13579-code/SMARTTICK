<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Admin Dashboard | SMARTTICK</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=20260702-dashboard">
</head>
<body class="dashboard-page admin-ops-page">
<div class="dashboard-shell">
    <jsp:include page="SidebarDashboard.jsp"/>
    <main class="dash-main">
        <jsp:include page="HeaderDashboard.jsp"/>
        <c:set var="totalProducts" value="${empty stats.totalProducts ? 0 : stats.totalProducts}"/>
        <c:set var="inStockProducts" value="${empty stats.inStockProducts ? 0 : stats.inStockProducts}"/>
        <c:set var="lowStockProducts" value="${empty stats.lowStockProducts ? 0 : stats.lowStockProducts}"/>
        <c:set var="hiddenProducts" value="${empty stats.hiddenProducts ? 0 : stats.hiddenProducts}"/>
        <c:set var="totalCustomers" value="${empty stats.totalCustomers ? 0 : stats.totalCustomers}"/>
        <c:set var="totalOrders" value="${empty stats.totalOrders ? 0 : stats.totalOrders}"/>
        <c:set var="totalInventory" value="${empty stats.totalInventory ? 0 : stats.totalInventory}"/>
        <c:set var="totalRevenue" value="${empty stats.totalRevenue ? 0 : stats.totalRevenue}"/>

        <section class="dash-hero">
            <div class="dash-title">
                <h1>Overview</h1>
                <p>SMARTTICK store operations overview.</p>
            </div>
            <div class="dash-actions">
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/ProductListServlet">Products</a>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/CreateProductServlet">Add Watch</a>
            </div>
        </section>

        <div class="dash-overview-layout">
            <div>
                <section class="stats" aria-label="Dashboard summary">
                    <article class="stat">
                        <small>Products</small>
                        <strong>${totalProducts}</strong>
                        <span>${inStockProducts} on sale</span>
                    </article>
                    <article class="stat">
                        <small>Orders</small>
                        <strong>${totalOrders}</strong>
                        <span>${fn:length(recentOrders)} tracked orders</span>
                    </article>
                    <article class="stat">
                        <small>Customers</small>
                        <strong>${totalCustomers}</strong>
                        <span>${fn:length(newCustomers)} new customers</span>
                    </article>
                    <article class="stat">
                        <small>Revenue</small>
                        <strong><fmt:formatNumber value="${totalRevenue}" pattern="#,##0"/> VND</strong>
                        <span>Completed Orders</span>
                    </article>
                </section>

                <section class="dash-card">
                    <div class="dash-card-head">
                        <div>
                            <div class="dash-chart-tabs">
                                <span>Sales</span>
                                <span>Inventory</span>
                                <span>Orders</span>
                            </div>
                            <p class="dash-card-sub">Operational signals from current data.</p>
                        </div>
                        <a class="icon-btn" href="${pageContext.request.contextPath}/StatisticManagementServlet" title="Statistics" aria-label="Statistics">
                            <i class="ti-bar-chart"></i>
                        </a>
                    </div>

                    <div class="dash-activity-chart" aria-hidden="true">
                        <svg viewBox="0 0 900 260" preserveAspectRatio="none" role="img">
                            <defs>
                                <linearGradient id="dashArea" x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="0%" stop-color="#bf5cff" stop-opacity=".32"/>
                                    <stop offset="100%" stop-color="#bf5cff" stop-opacity="0"/>
                                </linearGradient>
                            </defs>
                            <path d="M20,188 C90,165 115,82 160,128 S245,164 300,118 S390,92 440,72 S520,54 570,96 S655,132 710,90 S805,70 880,82"
                                  fill="none" stroke="#bf5cff" stroke-width="4" stroke-linecap="round"/>
                            <path d="M20,188 C90,165 115,82 160,128 S245,164 300,118 S390,92 440,72 S520,54 570,96 S655,132 710,90 S805,70 880,82 L880,250 L20,250 Z"
                                  fill="url(#dashArea)"/>
                            <g fill="#303030" stroke="#f5f5f2" stroke-width="4">
                                <circle cx="160" cy="128" r="6"/><circle cx="300" cy="118" r="6"/><circle cx="440" cy="72" r="6"/>
                                <circle cx="570" cy="96" r="6"/><circle cx="710" cy="90" r="6"/>
                            </g>
                        </svg>
                        <div class="dash-chart-labels">
                            <span>Jan</span><span>Feb</span><span>Mar</span><span>Apr</span><span>May</span><span>Jun</span>
                        </div>
                    </div>
                </section>

                <div class="mini-panels">
                    <section class="dash-card mini-panel">
                        <div class="dash-card-head">
                            <div>
                                <h2>Inventory</h2>
                                <p class="dash-card-sub">Total Inventory: ${totalInventory}</p>
                            </div>
                        </div>
                        <div class="bar-row" aria-hidden="true">
                            <div class="bar-item"><span class="bar gold" style="height:92px"></span><span>Total</span></div>
                            <div class="bar-item"><span class="bar green" style="height:126px"></span><span>In Stock</span></div>
                            <div class="bar-item"><span class="bar blue" style="height:64px"></span><span>Low Stock</span></div>
                            <div class="bar-item"><span class="bar" style="height:42px"></span><span>Hidden</span></div>
                        </div>
                    </section>

                    <section class="dash-card mini-panel">
                        <div class="dash-card-head">
                            <div>
                                <h2>Stock Status</h2>
                                <p class="dash-card-sub">Track products that need attention.</p>
                            </div>
                        </div>
                        <div class="rail-list">
                            <div class="rail-item">
                                <span class="rail-icon"><i class="ti-alert"></i></span>
                                <div><strong>${lowStockProducts} low-stock products</strong><span>Restocking needed</span></div>
                            </div>
                            <div class="rail-item">
                                <span class="rail-icon"><i class="ti-eye"></i></span>
                                <div><strong>${hiddenProducts} hidden products</strong><span>Not visible on the storefront</span></div>
                            </div>
                            <div class="rail-item">
                                <span class="rail-icon"><i class="ti-check-box"></i></span>
                                <div><strong>${inStockProducts} products in stock</strong><span>Ready for sale</span></div>
                            </div>
                        </div>
                    </section>
                </div>

                <section class="panel">
                    <div class="dash-card-head">
                        <div>
                            <h2>Latest Products</h2>
                            <p class="dash-card-sub">Recently updated watch models.</p>
                        </div>
                        <a class="dash-card-link" href="${pageContext.request.contextPath}/ProductListServlet">View All</a>
                    </div>
                    <div class="table-scroll">
                        <table class="table">
                            <thead>
                            <tr><th>Name</th><th>Category</th><th>Brand</th><th>Price</th><th>Inventory</th></tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${newProducts}" var="p">
                                <tr>
                                    <td><c:out value="${p.name}"/></td>
                                    <td><c:out value="${p.category}"/></td>
                                    <td><c:out value="${p.brand}"/></td>
                                    <td><fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND</td>
                                    <td>${p.stock}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty newProducts}">
                                <tr><td colspan="5">No products yet.</td></tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </section>

                <section class="panel">
                    <div class="dash-card-head">
                        <div>
                            <h2>Recent Orders</h2>
                            <p class="dash-card-sub">Latest order processing status.</p>
                        </div>
                        <a class="dash-card-link" href="${pageContext.request.contextPath}/ViewOrderListServlet">Manage Orders</a>
                    </div>
                    <div class="table-scroll">
                        <table class="table">
                            <thead>
                            <tr><th>Order ID</th><th>Customers</th><th>Order Date</th><th>Total</th><th>Status</th></tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${recentOrders}" var="o" begin="0" end="7">
                                <tr>
                                    <td>#${o.orderID}</td>
                                    <td><c:out value="${o.fullName}"/></td>
                                    <td><c:out value="${o.orderDate}"/></td>
                                    <td><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0"/> VND</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${o.status == 1}"><span class="status-pill status-pending">Pending Confirmation</span></c:when>
                                            <c:when test="${o.status == 2}"><span class="status-pill status-confirmed">Confirmed</span></c:when>
                                            <c:when test="${o.status == 3}"><span class="status-pill status-shipping">Shipping</span></c:when>
                                            <c:when test="${o.status == 4}"><span class="status-pill status-done">Completed</span></c:when>
                                            <c:when test="${o.status == 5}"><span class="status-pill status-cancel">Canceled</span></c:when>
                                            <c:otherwise><span class="status-pill">Unknown</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty recentOrders}">
                                <tr><td colspan="5">No orders yet.</td></tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>

            <aside class="side-rail">
                <section class="rail-card">
                    <h3>Notifications</h3>
                    <div class="rail-list">
                        <div class="rail-item">
                            <span class="rail-icon"><i class="ti-package"></i></span>
                            <div><strong>${lowStockProducts} low-stock models</strong><span>Prioritize restock checks</span></div>
                        </div>
                        <div class="rail-item">
                            <span class="rail-icon"><i class="ti-shopping-cart"></i></span>
                            <div><strong>${totalOrders} orders</strong><span>Currently in the system</span></div>
                        </div>
                        <div class="rail-item">
                            <span class="rail-icon"><i class="ti-receipt"></i></span>
                            <div><strong><fmt:formatNumber value="${totalRevenue}" pattern="#,##0"/> VND</strong><span>Completed revenue</span></div>
                        </div>
                    </div>
                </section>

                <section class="rail-card">
                    <h3>New Customers</h3>
                    <div class="rail-list">
                        <c:forEach items="${newCustomers}" var="c">
                            <div class="rail-item">
                                <span class="rail-icon"><i class="ti-user"></i></span>
                                <div><strong><c:out value="${c.name}"/></strong><span><c:out value="${c.email}"/></span></div>
                            </div>
                        </c:forEach>
                        <c:if test="${empty newCustomers}">
                            <div class="rail-item">
                                <span class="rail-icon"><i class="ti-info-alt"></i></span>
                                <div><strong>No Data Yet</strong><span>New customers will appear here</span></div>
                            </div>
                        </c:if>
                    </div>
                </section>
            </aside>
        </div>
    </main>
</div>
</body>
</html>

