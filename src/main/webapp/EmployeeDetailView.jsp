<%@page import="Models.Employee"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <title>View Employee Details</title>
        <style>
            body {
                background-color: #f8f9fa;
            }

            .content {
                flex-grow: 1;
                padding: 12px;
                margin-left: 250px;
            }

            .profile-container {
                max-width: 700px;
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
            .form-container {
                width: 60%;
            }
            .avatar-container {
                text-align: center;
                width: 35%;
            }
            .avatar-preview {
                width: 180px;
                height: 180px;
                border-radius: 50%;
                object-fit: cover;
                border: 3px solid #ddd;
            }
            .btn-save {
                background-color: #007bff;
                color: white;
            }
            .btn-change {
                background-color: #dc3545;
                color: white;
            }

            .value{
                width: 150px;

            }

        </style>
    </head>

    <body>
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            <div class="content">
            <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-10" style="padding: 10px;">
                            <form action="" method="">
                                <div class="profile-container">
                                    <div class="form-container">
                                        <div class="mb-1 d-flex">
                                            <label class="value fw-bold">Employee ID:</label>
                                            <p>${employee.employeeId}</p>
                                    </div>

                                    <div class="mb-1 d-flex">
                                        <label class="value fw-bold">Role Name:</label>
                                        <c:forEach items="${listR1}" var="r">
                                            <c:if test="${r.roleId eq employee.roleId}">
                                                <p>${r.roleName}</p>
                                            </c:if>
                                        </c:forEach>
                                    </div>

                                    <div class="mb-1 d-flex">
                                        <label class="value fw-bold">Full Name:</label>
                                        <p>${employee.fullname}</p>
                                    </div>

                                    <div class="mb-1 d-flex">
                                        <label class="value fw-bold">Birthday:</label>
                                        <p><fmt:formatDate value="${employee.birthday}" pattern="dd/MM/yyyy" /></p>
                                    </div>

                                    <div class="mb-1 d-flex">
                                        <label class="value fw-bold">Phone Number:</label>
                                        <p>${employee.phoneNumber}</p>
                                    </div>

                                    <div class="mb-1 d-flex">
                                        <label class="value fw-bold">Email:</label>
                                        <p>${employee.email}</p>
                                    </div>

                                    <div class="mb-2 d-flex">
                                        <label class="value fw-bold">Gender:</label>
                                        <p>${employee.gender}</p>
                                    </div>

                                    <div class="mb-1 d-flex">
                                        <label class="value fw-bold">Created Date:</label>
                                        <p><fmt:formatDate value="${employee.createdDate}" pattern="dd/MM/yyyy" /></p>
                                    </div>

                                    <div class="mb-1 d-flex">
                                        <label class="value fw-bold">Status:</label>
                                        <c:choose>
                                            <c:when test="${employee.status == 1}">
                                                <p><span style="background-color: #28a745;
                                                         color: white;
                                                         padding: 5px 12px;
                                                         border-radius: 15px;
                                                         font-size: 12px;
                                                         display: inline-flex;
                                                         align-items: center;
                                                         gap: 5px;
                                                         text-align: center;">
                                                        <i class='bx bx-check-circle' style="font-size: 14px;"></i> Available
                                                    </span></p>
                                                </c:when>
                                                <c:otherwise>
                                                <p><span style="background-color: #dc3545;
                                                         color: white;
                                                         padding: 5px 12px;
                                                         border-radius: 15px;
                                                         font-size: 12px;
                                                         display: inline-flex;
                                                         align-items: center;
                                                         gap: 5px;
                                                         text-align: center;
                                                         margin-left: 10px;">
                                                        <i class='bx bx-x-circle' style="font-size: 14px;"></i> Disable
                                                    </span></p>
                                                </c:otherwise>
                                            </c:choose>
                                    </div>
                                    <div class="d-flex gap-1" style="justify-content: left;">
                                         <a href="ViewEmployeeServlet" style="background-color: #007bff; color: white; padding: 8px 20px; border: none; border-radius: 10px; display: inline-flex; align-items: center; gap: 5px; cursor: pointer; text-decoration: none;">
                                            <i class='bx bx-arrow-back'></i> Cancel
                                        </a>
                                        <a href="UpdateEmployeeServlet?id=${employee.employeeId}" style="background-color: orange; color: white; text-decoration: none; padding: 5px 15px; border-radius: 5px; display: inline-block; cursor: pointer;">
                                            <i class='bx bx-edit'></i> Update
                                        </a>
                                    </div>
                                </div>
                                <div class="avatar-container">
                                    <label class="form-label">Avatar</label>
                                    <div class="mb-3">
                                        <c:choose>
                                            <c:when test="${not empty employee.avatar}">
                                                <img id="avatarPreview" class="avatar-preview" src="assets/imgs/Employee/${employee.avatar}" alt="Avatar">
                                            </c:when>
                                            <c:otherwise>
                                                <img id="avatarPreview" class="avatar-preview" src="assets/imgs/EmployeeAvatar/defauft_avatar.jpg" alt="Avatar">
                                            </c:otherwise>
                                        </c:choose>

                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        </div>
    </body>
</html>