<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/fonts/themify-icons/themify-icons.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=20260702-dashboard">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-ops.css?v=20260729-voucher-fix4">

<header class="dash-topbar">
    <div class="dash-breadcrumb">
        <i class="ti-panel"></i>
        <span>Dashboard</span>
        <span class="divider">/</span>
        <strong>SMARTTICK</strong>
    </div>

    <div class="dash-tools">
        <a class="icon-btn" href="${pageContext.request.contextPath}/NotificationServlet" title="Notifications" aria-label="Notifications">
            <i class="ti-bell"></i>
        </a>
        <a class="icon-btn" href="${pageContext.request.contextPath}/" title="View website" aria-label="View website">
            <i class="ti-new-window"></i>
        </a>

        <a class="dash-user" href="${pageContext.request.contextPath}/ViewEmployeeProfile">
            <c:choose>
                <c:when test="${not empty sessionScope.employee.avatar}">
                    <img class="dash-avatar" src="${pageContext.request.contextPath}/assets/imgs/EmployeeAvatar/${sessionScope.employee.avatar}" alt="" onerror="this.style.display='none';this.nextElementSibling.style.display='inline-flex';">
                    <span class="dash-avatar dash-avatar-fallback dash-avatar-hidden">ST</span>
                </c:when>
                <c:otherwise>
                    <span class="dash-avatar dash-avatar-fallback">ST</span>
                </c:otherwise>
            </c:choose>
            <span>Hi, <c:out value="${sessionScope.employee.fullname}" default="Administrator"/></span>
        </a>
    </div>
</header>



