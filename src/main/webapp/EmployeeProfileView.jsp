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
                background-color: #f8f9fa;
            }
            .profile-container {
                max-width: 800px;
                margin: 20px auto;
                background: white;
                padding: 40px;
                border-radius: 10px;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            }
            .avatar-preview {
                width: 180px;
                height: 180px;
                border-radius: 50%;
                object-fit: cover;
                border: 3px solid #ddd;
                display: block;
                margin: 0 auto;
            }
            .info-label {
                font-weight: bold;
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
                window.location.href = '/UpdateEmployeeProfile';
            }

            function changePassword() {
                window.location.href = '/ChangeEmployeePassword';
            }

        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

