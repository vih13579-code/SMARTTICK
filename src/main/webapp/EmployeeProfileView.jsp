<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Employee Profile Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <style>
            body {
                background-color: #242424 !important;
            }
            .profile-container {
                max-width: 800px;
                margin: 20px auto;
                background: #2f2f2f !important;
                color: #f8fafc !important;
                padding: 40px;
                border-radius: 10px;
                border: 1px solid rgba(255, 255, 255, 0.08);
                box-shadow: 0 18px 45px rgba(0, 0, 0, 0.25);
            }
            .profile-container h3 {
                color: #ffffff !important;
                font-weight: 800;
            }
            .profile-container p {
                color: #e5e7eb !important;
                font-weight: 500;
            }
            .profile-container .text-muted {
                color: #cbd5e1 !important;
            }
            .avatar-preview {
                width: 180px;
                height: 180px;
                border-radius: 50%;
                object-fit: cover;
                border: 3px solid rgba(255, 255, 255, 0.22);
                background: #1f1f1f;
                display: block;
                margin: 0 auto;
            }
            .info-label {
                font-weight: bold;
                color: #ffffff !important;
            }
            /* Popup styles */
            .popup {
                max-width: 800px;
                margin: 16px auto 0;
            }
            .popup-content {
                background-color: rgba(57, 208, 111, 0.14);
                border: 1px solid rgba(57, 208, 111, 0.32);
                color: #d7ffe3;
                padding: 14px 18px;
                border-radius: 8px;
                text-align: left;
            }
            .popup button {
                background-color: #0d111b;
                color: white;
                padding: 8px 16px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                margin-top: 8px;
            }
            .popup button:hover {
                background-color: #0d111b;
            }
        </style>
    </head>
    <body class="admin-ops-page">
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-2">
                    <jsp:include page="SidebarDashboard.jsp"></jsp:include>
                    </div>
                    <div class="col-md-10" style="padding-top: 10px;">
                    <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                        <div class="container">
                            <div class="profile-container text-center">
                            <c:choose>
                                <c:when test="${!sessionScope.employee.getAvatar().equals('')}">
                                    <img class="avatar-preview" src="assets/imgs/EmployeeAvatar/${sessionScope.employee.getAvatar()}" alt="Avatar">
                                </c:when>
                                <c:otherwise>
                                    <img class="avatar-preview" src="assets/imgs/EmployeeAvatar/defauft_avatar.jpg" alt="Avatar">
                                </c:otherwise>
                            </c:choose>
                            <h3 class="mt-3">${sessionScope.employee.getFullname()}</h3>
                            <p class="text-muted">${sessionScope.employee.getEmail()}</p>
                            <p><span class="info-label">Role:</span>
                                <c:choose>
                                    <c:when test="${sessionScope.employee.getRoleId() == 1}">Admin</c:when>
                                    <c:when test="${sessionScope.employee.getRoleId() == 2}">Shop Manager</c:when>
                                    <c:when test="${sessionScope.employee.getRoleId() == 3}">Order Manager</c:when>
                                    <c:when test="${sessionScope.employee.getRoleId() == 4}">Warehouse Manager</c:when>
                                </c:choose>
                            </p>
                            <p><span class="info-label">Gender:</span> ${sessionScope.employee.getGender()}</p>
                            <p><span class="info-label">Phone:</span> ${sessionScope.employee.getPhoneNumber()}</p>
                            <p><span class="info-label">Date of Birth:</span> ${sessionScope.employee.getBirthday().toString()}</p>
                            <div class="form-container">
                            <div class="d-flex gap-3" style="justify-content: space-between;">
                                <button type="submit" class="btn btn-primary px-4 py-2" onclick="updateProfile()">Update Profile</button>
                                <button type="button" class="btn btn-warning px-4 py-2" onclick="changePassword()">Change Password</button>
                            </div>
                        </div>
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
            <c:remove scope="session" var="empromess"/>

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


            function validateForm() {
                var phone = document.getElementsByName('phone')[0].value;
                var phonePattern = /^\d{10}$/;
                if (!phonePattern.test(phone)) {
                    alert('Phone number must be exactly 10 digits.');
                    return false;
                }
                return true;
            }
            function updateProfile(){
                window.location.href = '${pageContext.request.contextPath}/UpdateEmployeeProfile';
            }

            function changePassword() {
                window.location.href = '${pageContext.request.contextPath}/ChangeEmployeePassword';
            }

        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

