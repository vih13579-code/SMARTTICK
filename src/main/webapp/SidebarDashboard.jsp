<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/fonts/themify-icons/themify-icons.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=20260702-dashboard">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-ops.css?v=20260725-ops6">

<c:set var="uri" value="${pageContext.request.requestURI}" />
<c:set var="isDashboard" value="${fn:contains(uri, '/admin/dashboard')}" />
<c:set var="isStatistic" value="${fn:contains(uri, 'Statistic') || fn:contains(uri, 'Revenue') || fn:contains(uri, 'Inventory')}" />
<c:set var="isStockImport" value="${(fn:contains(uri, 'Import') || fn:contains(uri, 'Warehouse')) && !isStatistic}" />
<c:set var="isOrder" value="${(fn:contains(uri, 'Order') || fn:contains(uri, 'order')) && !isStockImport && !isStatistic}" />
<c:set var="isProduct" value="${fn:contains(uri, 'Product') && !isStatistic}" />
<c:set var="isCatalog" value="${fn:contains(uri, 'catalog')}" />
<c:set var="isCustomer" value="${fn:contains(uri, 'Customer')}" />
<c:set var="isVoucher" value="${fn:contains(uri, 'Voucher')}" />
<c:set var="isSupplier" value="${fn:contains(uri, 'Supplier')}" />
<aside class="dash-side">
    <a class="brand-logo" href="${pageContext.request.contextPath}/admin/dashboard" aria-label="SMARTTICK dashboard">
        <img src="${pageContext.request.contextPath}/assets/imgs/Logo/smarttick-logo.png" alt="SMARTTICK">
        <span class="brand-logo-text">
            <strong>SMARTTICK</strong>
            <span>Time is luxury</span>
        </span>
    </a>

    <nav class="dash-nav" aria-label="Admin navigation">
        <span class="dash-nav-group">Dashboard</span>
        <a class="dash-nav-link ${isDashboard ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="ti-pie-chart"></i><span>Overview</span>
        </a>
        <a class="dash-nav-link ${isProduct ? 'active' : ''}" href="${pageContext.request.contextPath}/ProductListServlet">
            <i class="ti-package"></i><span>Products</span>
        </a>
        <a class="dash-nav-link ${isCatalog ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/catalog">
            <i class="ti-layout-grid2"></i><span>Category & brand</span>
        </a>

        <span class="dash-nav-group">Operations</span>
        <a class="dash-nav-link ${isCustomer ? 'active' : ''}" href="${pageContext.request.contextPath}/CustomerListServlet">
            <i class="ti-user"></i><span>Customers</span>
        </a>
        <a class="dash-nav-link ${isOrder ? 'active' : ''}" href="${pageContext.request.contextPath}/ViewOrderListServlet">
            <i class="ti-receipt"></i><span>Orders</span>
        </a>
        <a class="dash-nav-link ${isVoucher ? 'active' : ''}" href="${pageContext.request.contextPath}/ViewVoucherListServlet">
            <i class="ti-ticket"></i><span>Voucher</span>
        </a>
        <a class="dash-nav-link ${isSupplier ? 'active' : ''}" href="${pageContext.request.contextPath}/Supplier">
            <i class="ti-truck"></i><span>Suppliers</span>
        </a>
        <a class="dash-nav-link ${isStockImport ? 'active' : ''}" href="${pageContext.request.contextPath}/ImportOrder">
            <i class="ti-archive"></i><span>Stock Import</span>
        </a>

        <span class="dash-nav-group">Reports</span>
        <a class="dash-nav-link ${isStatistic ? 'active' : ''}" href="${pageContext.request.contextPath}/StatisticManagementServlet">
            <i class="ti-bar-chart"></i><span>Statistics</span>
        </a>
        <a class="dash-nav-link dash-logout" href="${pageContext.request.contextPath}/Logout">
            <i class="ti-power-off"></i><span>Log Out</span>
        </a>
    </nav>
</aside>



