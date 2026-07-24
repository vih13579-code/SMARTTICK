
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Change Employee Password Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <style>
            body {
                background-color: #f8f9fa;
            }
            .head {
                margin-top: 10px;
                display: flex;
                justify-content: right;
                align-items: center;
                padding: 10px;
                background: #FFFFFF;
                box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.3);
                border-radius: 10px;
                height: 85px;
            }

            .icon_head {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                object-fit: cover;
            }
            .content{
                max-width: 1600px;
                height: auto;
                margin: 20px auto;
                background: white;
                padding: 40px;
                border-radius: 10px;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                display: flex;
                flex-wrap: wrap;
                justify-content: space-around;
                align-items: center;
            }
            .change_password_container{
                width: 60%;
                min-width: 600px;
            }
            .error-message {
                color: red;
                font-size: 0.9em;
                margin-top: 5px;
                margin-bottom: 10px;
            }
            /* Popup styles */
            .popup {
                display: block;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                justify-content: center;
                align-items: center;
            }
            .popup-content {
                background-color: white;
                padding: 30px;
                border-radius: 8px;
                text-align: center;
                width: 300px;
                margin: 150px auto;
            }
            .popup button {
                background-color: #007bff;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
            }
            .popup button:hover {
                background-color: #0056b3;
            }
        </style>
    </head>
    <body class="admin-ops-page">
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-2">
                    <jsp:include page="SidebarDashboard.jsp"></jsp:include>
                    </div>
                    <div class="col-md-10" style="padding: 10px;">
                    <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                        <div class="content">
                            <h2 style="width: 100%;">Change Password</h2>

                            <div class="change_password_container">
                                <form action="ChangeEmployeePassword" method="POST" onsubmit="return validatePassword()">

                                    <div class="mb-4">
                                        <label for="currentPassword" class="form-label">Current Password</label>
                                        <input type="password" class="form-control" id="currentPassword" name="current" required>
                                    </div>
                                    <div class="mb-4">
                                        <label for="newPassword" class="form-label">New Password</label>
                                        <input type="password" class="form-control" id="newPassword" name="new" required>
                                        <div id="passwordError" class="error-message mb-8"></div>
                                    </div>
                                    <div class="mb-4">
                                        <label for="confirmPassword" class="form-label">Confirm New Password</label>
                                        <input type="password" class="form-control" id="confirmPassword" name="confirm" required>
                                        <div id="confirmError" class="error-message mb-8"></div>
                                    </div>
                                    <div class="d-grid">
                                        <button type="submit" class="btn btn-primary">Change Password</button>
                                    </div>
                                </form>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        <c:if test="${sessionScope.empromess != null}">
            <!-- Popup -->
            <div class="popup" id="Popup">
                <div class="popup-content">
                    <h3>${sessionScope.empromess}</h3>
                    <button onclick="closePopup()">Close</button>
                </div>
            </div>
            <%
                session.removeAttribute("empromess");
            %>
        </c:if>
        <script>
            function confirmLogout() {
                if (confirm("Are you sure you want to log out?")) {
                    // Chuyá»ƒn hÆ°á»›ng tá»›i trang logout hoáº·c gá»i API logout
                    window.location.href = "${pageContext.request.contextPath}/Logout";
                }
            }
            function closePopup() {
                document.getElementById("Popup").style.display = "none";
            }

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
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

