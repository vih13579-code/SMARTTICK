<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=20260618-ui4">

<header class="dash-header">
    <div class="dash-user">
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
    </div>
</header>
