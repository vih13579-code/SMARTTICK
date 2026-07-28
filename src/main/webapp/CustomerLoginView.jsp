<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Customer Login | SMARTTICK</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=20260702-google-auth">
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

    <h1>Customer Login</h1>
    <p>Access your account with your registered email.</p>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-danger"><c:out value="${sessionScope.message}"/></div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty requestScope.pageMessage}">
        <div class="alert alert-danger"><c:out value="${requestScope.pageMessage}"/></div>
    </c:if>
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success"><c:out value="${sessionScope.successMessage}"/></div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/customerLogin">
        <label>Email
            <input class="form-control" type="email" name="email" required autocomplete="email" value="<c:out value='${rememberedEmail}'/>">
        </label>
        <label>Password
            <input class="form-control" type="password" name="password" required autocomplete="current-password">
        </label>
        <button class="btn btn-primary" type="submit" style="width:100%">Log In</button>
    </form>

    <c:if test="${googleConfigured}">
    <div class="oauth-divider"><span>or</span></div>
    <a class="google-btn" href="${pageContext.request.contextPath}/google-auth?source=login">
        <span class="google-icon" aria-hidden="true">
            <svg viewBox="0 0 24 24" focusable="false">
                <path fill="#EA4335" d="M12 10.2v3.9h5.4c-.2 1.3-1.6 3.9-5.4 3.9-3.2 0-5.9-2.7-5.9-6s2.7-6 5.9-6c1.8 0 3 .8 3.7 1.5l2.5-2.4C16.6 3.6 14.5 2.7 12 2.7 6.9 2.7 2.8 6.8 2.8 12S6.9 21.3 12 21.3c6.9 0 8.6-4.8 8.6-7.3 0-.5 0-.9-.1-1.3H12z"/>
                <path fill="#34A853" d="M2.8 12c0 1.7.6 3.2 1.6 4.4l3-2.4c-.4-.6-.6-1.2-.6-2s.2-1.4.6-2L4.4 7.6C3.4 8.8 2.8 10.3 2.8 12z"/>
                <path fill="#FBBC05" d="M12 21.3c2.5 0 4.6-.8 6.2-2.3l-3-2.4c-.8.6-1.8 1-3.2 1-2.5 0-4.6-1.7-5.3-4l-3 2.3c1.6 3.2 4.8 5.4 8.3 5.4z"/>
                <path fill="#4285F4" d="M18.2 19c1.8-1.7 2.4-4.2 2.4-6.3 0-.6-.1-1.1-.2-1.6H12v3.9h4.8c-.2 1.1-.8 2.8-2.4 4l3 2.4z"/>
            </svg>
        </span>
        <span>Continue with Google</span>
    </a>
    </c:if>

    <p>
        <a href="${pageContext.request.contextPath}/">Back to Home</a> ·
        <a href="${pageContext.request.contextPath}/register">Register</a> ·
        <a href="${pageContext.request.contextPath}/EmployeeLogin">Admin Login</a>
    </p>
</main>
</body>
</html>
