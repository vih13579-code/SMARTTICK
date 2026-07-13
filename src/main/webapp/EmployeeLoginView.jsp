<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Admin Login | SMARTTICK</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=20260702-dashboard">
</head>
<body class="auth-page">
<main class="auth-card">
    <a class="brand-logo" href="${pageContext.request.contextPath}/" aria-label="SMARTTICK home">
        <img src="${pageContext.request.contextPath}/assets/imgs/Logo/smarttick-logo.png" alt="SMARTTICK">
        <span class="brand-logo-text">
            <strong>SMARTTICK</strong>
            <span>Time is luxury</span>
        </span>
    </a>

    <h1>Admin Login</h1>
    <p>Access your employee account with your assigned internal email.</p>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-danger"><c:out value="${sessionScope.message}"/></div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/EmployeeLogin">
        <label>Email
            <input class="form-control" type="email" name="email" required autocomplete="email">
        </label>
        <label>Password
            <input class="form-control" type="password" name="password" required autocomplete="current-password">
        </label>
        <button class="btn btn-primary" type="submit" style="width:100%">Log In</button>
    </form>

    <p>
        <a href="${pageContext.request.contextPath}/">Back to Home</a> ·
        <a href="${pageContext.request.contextPath}/customerLogin">Customer Login</a>
    </p>
</main>
</body>
</html>
