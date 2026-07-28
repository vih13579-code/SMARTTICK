<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="account-fragment">
    <div class="account-fragment-head">
        <div>
            <span class="eyebrow">Security</span>
            <h2>Change Password</h2>
            <p class="section-sub">Protect your account with a strong SMARTTICK password.</p>
        </div>
    </div>

    <form class="account-form" action="changeCustomerPassword" method="POST" onsubmit="return validatePassword()">
        <label for="currentPassword">Current Password</label>
        <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>

        <label for="newPassword">New Password</label>
        <input type="password" class="form-control" id="newPassword" name="newPassword" required>
        <div id="passwordError" class="form-error"></div>

        <label for="confirmPassword">Confirm New Password</label>
        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
        <div id="confirmError" class="form-error"></div>

        <button type="submit" class="btn btn-primary">Change Password</button>
    </form>
</div>

<script>
    function validatePassword() {
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;
        const passwordError = document.getElementById("passwordError");
        const confirmError = document.getElementById("confirmError");
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

        passwordError.textContent = "";
        confirmError.textContent = "";

        if (!passwordRegex.test(newPassword)) {
            passwordError.textContent = "Password must be at least 8 characters, include one uppercase letter, one number, and one special character.";
            return false;
        }
        if (newPassword.length > 50) {
            passwordError.textContent = "Password is too long, password must be shorter than 50 characters.";
            return false;
        }
        if (newPassword !== confirmPassword) {
            confirmError.textContent = "Passwords do not match.";
            return false;
        }
        return true;
    }
</script>
