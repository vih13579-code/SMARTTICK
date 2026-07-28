<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Forgot Password | SMARTTICK</title>
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

    <h1>Forgot Password</h1>
    <p>Enter your registered email to receive a password reset OTP.</p>

    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger"><c:out value="${requestScope.error}"/></div>
    </c:if>
    <c:if test="${not empty requestScope.message}">
        <div class="alert alert-success"><c:out value="${requestScope.message}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/SendMailServlet" method="post">
        <label>Email
            <input class="form-control" type="email" id="email" name="email" required autocomplete="email">
        </label>
        <button type="submit" class="btn btn-primary" style="width:100%">Send OTP</button>
    </form>

    <p><a href="${pageContext.request.contextPath}/customerLogin">Back to Log In</a></p>
</main>
</body>
</html>
