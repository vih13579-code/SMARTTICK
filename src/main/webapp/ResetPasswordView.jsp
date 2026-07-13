<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Reset Password | SMARTTICK</title>
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

    <h1>Reset Password</h1>
    <p>Create a new password for your account.</p>

    <c:if test="${not empty requestScope.error}">
        <div class="alert alert-danger"><c:out value="${requestScope.error}"/></div>
    </c:if>

    <form id="resetPassword" action="${pageContext.request.contextPath}/ResetPasswordServlet" method="post">
        <label>New Password
            <input class="form-control" type="password" name="newPassword" id="newPassword" required autocomplete="new-password">
        </label>
        <p id="passwordRules" class="error-message"></p>
        <label>Confirm Password
            <input class="form-control" type="password" name="confirmPassword" id="confirmPassword" required autocomplete="new-password">
        </label>
        <p id="passwordError" class="error-message"></p>
        <button type="submit" class="btn btn-primary" style="width:100%">Update Password</button>
    </form>
</main>

<script>
    document.getElementById("resetPassword").addEventListener("submit", function (event) {
        const password = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;
        const passwordRules = document.getElementById("passwordRules");
        const passwordError = document.getElementById("passwordError");
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%!&*?]).{8,50}$/;
        let isValid = true;

        if (!passwordRegex.test(password)) {
            passwordRules.textContent = "Password must be 8-50 characters and include uppercase, lowercase, number, and special character.";
            isValid = false;
        } else {
            passwordRules.textContent = "";
        }

        if (password !== confirmPassword) {
            passwordError.textContent = "Password confirmation does not match.";
            isValid = false;
        } else {
            passwordError.textContent = "";
        }

        if (!isValid) {
            event.preventDefault();
        }
    });
</script>
</body>
</html>
