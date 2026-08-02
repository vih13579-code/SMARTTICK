<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="account-fragment security-fragment">
    <div class="account-fragment-head">
        <div>
            <span class="eyebrow">Security</span>
            <h2>
                <c:choose>
                    <c:when test="${hasLocalPassword}">Change password</c:when>
                    <c:otherwise>Create a new password</c:otherwise>
                </c:choose>
            </h2>
            <p class="section-sub">
                <c:choose>
                    <c:when test="${hasLocalPassword}">
                        Keep your account secure with a password only you know.
                    </c:when>
                    <c:otherwise>
                        Your Google account is already verified. Create a password to also sign in
                        with your email address.
                    </c:otherwise>
                </c:choose>
            </p>
        </div>
    </div>

    <c:if test="${not empty passwordError}">
        <div class="alert alert-danger"><c:out value="${passwordError}"/></div>
    </c:if>

    <form class="account-form password-form"
          action="${pageContext.request.contextPath}/changeCustomerPassword"
          method="post" onsubmit="return validateCustomerPassword()">
        <c:if test="${hasLocalPassword}">
            <label for="currentPassword">Current password</label>
            <input type="password" class="form-control" id="currentPassword"
                   name="currentPassword" autocomplete="current-password" required>
        </c:if>

        <label for="newPassword">New password</label>
        <input type="password" class="form-control" id="newPassword" name="newPassword"
               minlength="8" maxlength="50" autocomplete="new-password" required
               aria-describedby="passwordHelp passwordError">
        <small id="passwordHelp" class="form-hint">
            8-50 characters with uppercase, lowercase, number, and one of @$!%*?&.
        </small>
        <div id="passwordError" class="form-error" aria-live="polite"></div>

        <label for="confirmPassword">Confirm new password</label>
        <input type="password" class="form-control" id="confirmPassword"
               name="confirmPassword" maxlength="50" autocomplete="new-password" required
               aria-describedby="confirmError">
        <div id="confirmError" class="form-error" aria-live="polite"></div>

        <button type="submit" class="btn btn-primary">
            <c:choose>
                <c:when test="${hasLocalPassword}">Change password</c:when>
                <c:otherwise>Create password</c:otherwise>
            </c:choose>
        </button>
    </form>
</div>

<script>
    function validateCustomerPassword() {
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;
        const passwordError = document.getElementById("passwordError");
        const confirmError = document.getElementById("confirmError");
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,50}$/;

        passwordError.textContent = "";
        confirmError.textContent = "";

        if (!passwordRegex.test(newPassword)) {
            passwordError.textContent =
                    "Use 8-50 characters with uppercase, lowercase, number, and one of @$!%*?&.";
            return false;
        }
        if (newPassword !== confirmPassword) {
            confirmError.textContent = "Passwords do not match.";
            return false;
        }
        return true;
    }
</script>
