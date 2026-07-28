<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
<div class="fw-top">Free nationwide shipping for orders from 2,000,000 VND · Official warranty</div>
<header class="fw-header">
    <div class="container fw-nav">
        <a class="brand-logo" href="${pageContext.request.contextPath}/" aria-label="SMARTTICK home">
            <img src="${pageContext.request.contextPath}/assets/imgs/Logo/smarttick-logo.png" alt="SMARTTICK">
            <span class="brand-logo-text">
                <strong>SMARTTICK</strong>
                <span>Time is luxury</span>
            </span>
        </a>
        <nav class="fw-links">
            <a href="${pageContext.request.contextPath}/">Home</a>
            <a href="${pageContext.request.contextPath}/Men">Men's Watches</a>
            <a href="${pageContext.request.contextPath}/Women">Women's Watches</a>
            <a href="${pageContext.request.contextPath}/Sport">Sports Watches</a>
            <a href="${pageContext.request.contextPath}/Mechanical">Mechanical Watches</a>
            <a href="${pageContext.request.contextPath}/Watches">All Products</a>
        </nav>
        <div class="fw-actions">
            <c:choose>
                <c:when test="${not empty sessionScope.customer}">
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/customer/dashboard">Account</a>
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/NotificationServlet">Notifications</a>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/cart">Cart</a>
                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/Logout">Log Out</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/customerLogin">Log In</a>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/cart">Cart</a>
                </c:otherwise>
            </c:choose>
            <c:if test="${not empty sessionScope.employee}">
                <a class="btn btn-gold" href="${pageContext.request.contextPath}/admin/dashboard">Admin</a>
            </c:if>
        </div>
    </div>
</header>
