
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Update Employee Profile Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <style>
            body {
                background-color: #f8f9fa;
            }

            .profile-container {
                max-width: 1600px;
                height: auto;
                margin: 20px auto;
                background: white;
                color: #1f2937 !important;
                padding: 40px;
                border-radius: 10px;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                display: flex;
                flex-wrap: wrap;
                justify-content: space-between;
                align-items: center;
            }
            .form-container {
                width: 60%;
            }

            .profile-container label,
            .profile-container p,
            .avatar-container label {
                color: #1f2937 !important;
            }

            .profile-container .fw-bold {
                color: #111827 !important;
            }

            .profile-container .form-control,
            .profile-container .form-select {
                background-color: #333333;
                border-color: #4b5563;
                color: #ffffff;
            }

            .profile-container .form-control:focus,
            .profile-container .form-select:focus {
                background-color: #333333;
                border-color: #f4b400;
                color: #ffffff;
                box-shadow: 0 0 0 0.2rem rgba(244, 180, 0, 0.18);
            }

            .profile-container .form-select option {
                background-color: #333333;
                color: #ffffff;
            }

            .profile-container .smart-file-input {
                background-color: #ffffff;
                color: #1f2937;
            }
            .avatar-container {
                text-align: center;
                width: 35%;
            }
            .avatar-preview {
                width: 180px;
                height: 180px;
                border-radius: 50%;
                border: 3px solid #ddd;
            }
            .value{
                width: 150px;

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
                        <form action="${pageContext.request.contextPath}/UpdateEmployeeProfile" method="post" enctype="multipart/form-data" onsubmit="return validateForm()">

                            <div class="profile-container">

                                <div class="form-container">

                                    <div class="mb-3 d-flex">
                                        <label class="value">Email:</label>
                                        <p>${sessionScope.employee.getEmail()}</p>
                                </div>

                                <div class="mb-3 d-flex">
                                    <label class="value">Role:</label>
                                    <c:if test="${sessionScope.employee.getRoleId() == 1}">
                                        <p class="fw-bold">Admin</p>
                                    </c:if>
                                    <c:if test="${sessionScope.employee.getRoleId() == 2}">
                                        <p class="fw-bold">Shop Manager</p>
                                    </c:if>
                                    <c:if test="${sessionScope.employee.getRoleId() == 3}">
                                        <p class="fw-bold">Order Manager</p>
                                    </c:if>
                                    <c:if test="${sessionScope.employee.getRoleId() == 4}">
                                        <p class="fw-bold">Warehouse Manager</p>
                                    </c:if>
                                </div>

                                <div class="mb-3 d-flex">
                                    <label class="form-label value">Full Name</label>
                                    <input type="text" class="form-control" name="fullName" value="${sessionScope.employee.getFullname()}" required>
                                </div>

                                <div class="mb-3 d-flex">
                                    <label class="form-label value" value>Gender</label>
                                    <select class="form-select" name="gender" required>
                                        <option value="Male" ${sessionScope.employee.getGender() == 'Male' ? 'selected' : ''}>Male</option>
                                        <option value="Female" ${sessionScope.employee.getGender() == 'Female' ? 'selected' : ''}>Female</option>
                                        <option value="Other" ${sessionScope.employee.getGender() == 'Other' ? 'selected' : ''}>Other</option>
                                    </select>
                                </div>

                                <div class="mb-3 d-flex">
                                    <label class="form-label value">Phone</label>
                                    <input type="text" class="form-control" name="phone" value="${sessionScope.employee.getPhoneNumber()}" required>
                                </div>

                                <div class="mb-3 d-flex">
                                    <label class="form-label value">Date Of Birth</label>
                                    <input type="date" class="form-control" name="dob" id="dob" value="${sessionScope.employee.getBirthday().toString()}" required>

                                </div>
                                <small id="dob-error" class="text-danger" style="display:none;">Invalid date of birth</small>
                            </div>

                            <div class="avatar-container">
                                <label class="form-label">Avatar</label>
                                <div class="mb-5">
                                    <c:choose>
                                        <c:when test="${!sessionScope.employee.getAvatar().equals('')}">
                                            <img id="avatarPreview" class="avatar-preview" src="assets/imgs/EmployeeAvatar/${sessionScope.employee.getAvatar()}" alt="Avatar">
                                        </c:when>
                                        <c:otherwise>
                                            <img id="avatarPreview" class="avatar-preview" src="assets/imgs/EmployeeAvatar/defauft_avatar.jpg" alt="Avatar">
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <input type="file" class="form-control smart-file-input" name="avatar" accept="image/*" onchange="previewImage(event)">
                            </div>
                            <div class="form-container">
                                <div class="d-flex gap-3" style="justify-content: start;">
                                    <button type="submit" class="btn btn-primary px-4 py-2">Save</button>
                                </div>
                            </div>

                        </div>
                    </form>
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
            document.getElementById("dob").addEventListener("change", function () {
                let dobInput = document.getElementById("dob");
                let dobError = document.getElementById("dob-error");
                let selectedDate = new Date(dobInput.value);
                let today = new Date();
                today.setHours(0, 0, 0, 0);

                if (selectedDate > today) {
                    dobError.style.display = "block";
                    dobInput.value = "";
                    event.preventDefault();
                } else {
                    dobError.style.display = "none";
                }
            });
            function confirmLogout() {
                if (confirm("Are you sure you want to log out?")) {
                    window.location.href = "${pageContext.request.contextPath}/Logout";
                }
            }
            function closePopup() {
                document.getElementById("Popup").style.display = "none";
            }


            function validateForm() {
                var phone = document.getElementsByName('phone')[0].value;
                var phonePattern = /^0[1-9][0-9]{8}$/;
                if (!phonePattern.test(phone)) {
                    alert('Phone number must be exactly 10 digits and start with 0. Ex: 0946771397');
                    return false;
                }
                return true;
            }

            function previewImage(event) {
                var reader = new FileReader();
                reader.onload = function () {
                    var output = document.getElementById('avatarPreview');
                    output.src = reader.result;
                };
                reader.readAsDataURL(event.target.files[0]);
            }




        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/file-input.js"></script>
    </body>
</html>


