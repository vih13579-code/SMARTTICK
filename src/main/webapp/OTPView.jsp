<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>OTP Verification | SMARTTICK</title>
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

    <h1>Verify Email</h1>
    <p>Enter the 6-digit OTP sent to your registered email. The code is valid for 10 minutes.</p>

    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger"><c:out value="${requestScope.error}"/></div>
    </c:if>
    <c:if test="${not empty requestScope.message}">
        <div class="alert alert-success"><c:out value="${requestScope.message}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/RegisterOTPServlet" method="post">
        <label>OTP Code
            <input class="form-control" type="text" name="otp" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" required autocomplete="one-time-code">
        </label>
        <button type="submit" class="btn btn-primary" style="width:100%">Verify</button>
    </form>

    <p><a href="${pageContext.request.contextPath}/register">Register Again</a></p>
</main>
</body>
</html>
