<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Employee Profile | SMARTTICK</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-ops.css?v=20260729-admin-fix">
        <style>
            .employee-profile-card {
                display: grid;
                grid-template-columns: 190px minmax(0, 1fr);
                gap: 28px;
                align-items: center;
            }
            .avatar-preview {
                width: 170px;
                height: 170px;
                border: 3px solid #d8ad5a;
                border-radius: 50%;
                background: #424242;
                object-fit: cover;
            }
            .employee-profile-card h2 {
                margin: 0 0 5px;
            }
            .profile-email {
                margin-bottom: 20px;
                color: var(--dash-muted);
            }
            .profile-details {
                display: grid;
                grid-template-columns: repeat(2, minmax(0, 1fr));
                gap: 12px 24px;
                margin-bottom: 22px;
            }
            .profile-detail {
                padding: 12px 14px;
                border: 1px solid var(--dash-line);
                border-radius: 10px;
                background: var(--dash-surface-2);
            }
            .profile-detail small {
                display: block;
                margin-bottom: 4px;
                color: var(--dash-muted);
            }
            .profile-actions {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
            }
            @media (max-width: 700px) {
                .employee-profile-card,
                .profile-details {
                    grid-template-columns: 1fr;
                }
                .avatar-preview {
                    margin: 0 auto;
                }
            }
        </style>
    </head>
    <body class="admin-ops-page">
        <div class="dashboard-shell">
            <jsp:include page="SidebarDashboard.jsp"/>
            <main class="dash-main">
                <jsp:include page="HeaderDashboard.jsp"/>

                <div class="admin-page-title">
                    <div>
                        <h1>My Profile</h1>
                        <p>View your employee account information.</p>
                    </div>
                </div>

                <c:if test="${not empty sessionScope.empromess}">
                    <div class="alert alert-success" role="alert">
                        <c:out value="${sessionScope.empromess}"/>
                    </div>
                    <c:remove scope="session" var="empromess"/>
                </c:if>

                <c:set var="employee" value="${sessionScope.employee}"/>
                <c:choose>
                    <c:when test="${empty employee}">
                        <div class="panel empty-state">Employee information is unavailable.</div>
                    </c:when>
                    <c:otherwise>
                        <section class="profile-container employee-profile-card">
                            <div>
                                <c:choose>
                                    <c:when test="${not empty employee.avatar}">
                                        <img class="avatar-preview"
                                             src="${pageContext.request.contextPath}/assets/imgs/EmployeeAvatar/${employee.avatar}"
                                             onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/imgs/EmployeeAvatar/defauft_avatar.jpg';"
                                             alt="Employee avatar">
                                    </c:when>
                                    <c:otherwise>
                                        <img class="avatar-preview"
                                             src="${pageContext.request.contextPath}/assets/imgs/EmployeeAvatar/defauft_avatar.jpg"
                                             alt="Default employee avatar">
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div>
                                <h2><c:out value="${employee.fullname}"/></h2>
                                <div class="profile-email"><c:out value="${employee.email}"/></div>
                                <div class="profile-details">
                                    <div class="profile-detail">
                                        <small>Role</small>
                                        <strong>
                                            <c:choose>
                                                <c:when test="${employee.roleId == 1}">Admin</c:when>
                                                <c:when test="${employee.roleId == 2}">Shop Manager</c:when>
                                                <c:when test="${employee.roleId == 3}">Order Manager</c:when>
                                                <c:when test="${employee.roleId == 4}">Warehouse Manager</c:when>
                                                <c:otherwise>Employee</c:otherwise>
                                            </c:choose>
                                        </strong>
                                    </div>
                                    <div class="profile-detail">
                                        <small>Gender</small>
                                        <strong><c:out value="${employee.gender}" default="Not provided"/></strong>
                                    </div>
                                    <div class="profile-detail">
                                        <small>Phone</small>
                                        <strong><c:out value="${employee.phoneNumber}" default="Not provided"/></strong>
                                    </div>
                                    <div class="profile-detail">
                                        <small>Date of Birth</small>
                                        <strong><c:out value="${employee.birthday}" default="Not provided"/></strong>
                                    </div>
                                </div>
                                <div class="profile-actions">
                                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/UpdateEmployeeProfile">Update Profile</a>
                                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ChangeEmployeePassword">Change Password</a>
                                </div>
                            </div>
                        </section>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>
    </body>
</html>
