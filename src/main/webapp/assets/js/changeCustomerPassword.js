/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/ClientSide/javascript.js to edit this template
 */


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

    if (newPassword.length > 50){
        passwordError.textContent = "Password is too long, password must be shorter than 50 characters.";
        return false;
    }

    if (newPassword !== confirmPassword) {
        confirmError.textContent = "Passwords do not match.";
        return false;
    }

    return true;
}

              