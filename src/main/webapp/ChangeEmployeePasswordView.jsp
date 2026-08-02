<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Change Password | SMARTTICK</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    </head>
    <body class="admin-ops-page">
        <jsp:include page="SidebarDashboard.jsp"/>

        <div class="content">
            <jsp:include page="HeaderDashboard.jsp"/>

            <main class="admin-page-stack">
                <div class="admin-page-title">
                    <div>
                        <h1>Change Password</h1>
                        <p>Use a strong password that is not shared with another account.</p>
                    </div>
                </div>

                <c:if test="${not empty sessionScope.passwordError}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${sessionScope.passwordError}"/>
                    </div>
                    <c:remove var="passwordError" scope="session"/>
                </c:if>

                <section class="change_password_container" aria-labelledby="changePasswordFormTitle">
                    <h2 id="changePasswordFormTitle" class="h4 mb-2">Update your password</h2>
                    <p class="text-secondary mb-4">
                        The new password must contain 8–50 characters, including uppercase,
                        lowercase, number, and special character.
                    </p>

                    <form action="${pageContext.request.contextPath}/ChangeEmployeePassword"
                          method="post" id="changePasswordForm" novalidate>
                        <div class="mb-3">
                            <label for="currentPassword" class="form-label">Current Password</label>
                            <input type="password" class="form-control" id="currentPassword"
                                   name="current" maxlength="50"
                                   autocomplete="current-password" required>
                            <div class="invalid-feedback">Please enter your current password.</div>
                        </div>

                        <div class="mb-3">
                            <label for="newPassword" class="form-label">New Password</label>
                            <input type="password" class="form-control" id="newPassword"
                                   name="new" minlength="8" maxlength="50"
                                   autocomplete="new-password" required>
                            <div id="passwordError" class="error-message"></div>
                        </div>

                        <div class="mb-4">
                            <label for="confirmPassword" class="form-label">Confirm New Password</label>
                            <input type="password" class="form-control" id="confirmPassword"
                                   name="confirm" minlength="8" maxlength="50"
                                   autocomplete="new-password" required>
                            <div id="confirmError" class="error-message"></div>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">Change Password</button>
                        </div>
                    </form>
                </section>
            </main>
        </div>

        <script>
            (function () {
                "use strict";

                const form = document.getElementById("changePasswordForm");
                const currentPassword = document.getElementById("currentPassword");
                const newPassword = document.getElementById("newPassword");
                const confirmPassword = document.getElementById("confirmPassword");
                const passwordError = document.getElementById("passwordError");
                const confirmError = document.getElementById("confirmError");
                const passwordPattern =
                        /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,50}$/;

                function clearErrors() {
                    passwordError.textContent = "";
                    confirmError.textContent = "";
                    currentPassword.classList.remove("is-invalid");
                    newPassword.classList.remove("is-invalid");
                    confirmPassword.classList.remove("is-invalid");
                }

                form.addEventListener("submit", function (event) {
                    clearErrors();
                    let valid = true;

                    if (!currentPassword.value) {
                        currentPassword.classList.add("is-invalid");
                        valid = false;
                    }

                    if (!passwordPattern.test(newPassword.value)) {
                        newPassword.classList.add("is-invalid");
                        passwordError.textContent =
                                "Use 8–50 characters with uppercase, lowercase, number, and special character.";
                        valid = false;
                    } else if (newPassword.value === currentPassword.value) {
                        newPassword.classList.add("is-invalid");
                        passwordError.textContent =
                                "The new password must be different from the current password.";
                        valid = false;
                    }

                    if (!confirmPassword.value || newPassword.value !== confirmPassword.value) {
                        confirmPassword.classList.add("is-invalid");
                        confirmError.textContent = "The confirmation password does not match.";
                        valid = false;
                    }

                    if (!valid) {
                        event.preventDefault();
                    }
                });

                [currentPassword, newPassword, confirmPassword].forEach(function (input) {
                    input.addEventListener("input", clearErrors);
                });
            }());
        </script>
    </body>
</html>
