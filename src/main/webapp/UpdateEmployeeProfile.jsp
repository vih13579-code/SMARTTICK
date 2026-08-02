<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Update Employee Profile | SMARTTICK</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-ops.css?v=20260729-admin-fix">
        <style>
            .profile-edit-grid {
                display: grid;
                grid-template-columns: minmax(0, 1fr) 260px;
                gap: 28px;
                align-items: start;
            }
            .profile-fields {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 18px;
            }
            .profile-fields .field-wide {
                grid-column: 1 / -1;
            }
            .profile-fields label,
            .avatar-container > label {
                display: block;
                margin-bottom: 7px;
                font-weight: 700;
            }
            .profile-readonly {
                min-height: 48px;
                padding: 12px 14px;
                border: 1px solid rgba(255, 255, 255, .1);
                border-radius: 10px;
                background: #292929;
                color: #c9c7bd;
            }
            .avatar-container {
                text-align: center;
            }
            .avatar-preview {
                display: block;
                width: 164px;
                height: 164px;
                margin: 0 auto 18px;
                border: 3px solid #d8ad5a;
                border-radius: 50%;
                background: #424242;
                object-fit: cover;
            }
            .profile-actions {
                display: flex;
                gap: 10px;
                margin-top: 22px;
            }
            @media (max-width: 760px) {
                .profile-edit-grid,
                .profile-fields {
                    grid-template-columns: 1fr;
                }
                .profile-fields .field-wide {
                    grid-column: auto;
                }
                .avatar-container {
                    grid-row: 1;
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
                        <h1>Update Profile</h1>
                        <p>Keep your employee information and profile image up to date.</p>
                    </div>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${errorMessage}"/>
                    </div>
                </c:if>

                <c:set var="employee" value="${sessionScope.employee}"/>
                <c:choose>
                    <c:when test="${empty employee}">
                        <div class="panel empty-state">Employee information is unavailable.</div>
                    </c:when>
                    <c:otherwise>
                        <form class="profile-container" action="${pageContext.request.contextPath}/UpdateEmployeeProfile"
                              method="post" enctype="multipart/form-data" id="employeeProfileForm">
                            <div class="profile-edit-grid">
                                <div class="profile-fields">
                                    <div>
                                        <label>Email</label>
                                        <div class="profile-readonly"><c:out value="${employee.email}"/></div>
                                    </div>
                                    <div>
                                        <label>Role</label>
                                        <div class="profile-readonly">
                                            <c:choose>
                                                <c:when test="${employee.roleId == 1}">Admin</c:when>
                                                <c:when test="${employee.roleId == 2}">Shop Manager</c:when>
                                                <c:when test="${employee.roleId == 3}">Order Manager</c:when>
                                                <c:when test="${employee.roleId == 4}">Warehouse Manager</c:when>
                                                <c:otherwise>Employee</c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="field-wide">
                                        <label for="fullName">Full Name</label>
                                        <input id="fullName" type="text" class="form-control" name="fullName"
                                               value="<c:out value='${employee.fullname}'/>" minlength="2" maxlength="100" required>
                                    </div>
                                    <div>
                                        <label for="gender">Gender</label>
                                        <select id="gender" class="form-select" name="gender" required>
                                            <option value="Male" ${employee.gender == 'Male' ? 'selected' : ''}>Male</option>
                                            <option value="Female" ${employee.gender == 'Female' ? 'selected' : ''}>Female</option>
                                            <option value="Other" ${employee.gender == 'Other' ? 'selected' : ''}>Other</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label for="phone">Phone</label>
                                        <input id="phone" type="tel" class="form-control" name="phone"
                                               value="<c:out value='${employee.phoneNumber}'/>"
                                               pattern="0[1-9][0-9]{8}" maxlength="10"
                                               title="Use a 10-digit phone number starting with 0." required>
                                    </div>
                                    <div class="field-wide">
                                        <label for="dob">Date of Birth</label>
                                        <input id="dob" type="date" class="form-control" name="dob"
                                               value="${employee.birthday}" required>
                                    </div>
                                </div>

                                <div class="avatar-container">
                                    <label for="avatar">Avatar</label>
                                    <c:choose>
                                        <c:when test="${not empty employee.avatar}">
                                            <img id="avatarPreview" class="avatar-preview"
                                                 src="${pageContext.request.contextPath}/assets/imgs/EmployeeAvatar/${employee.avatar}"
                                                 onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/imgs/EmployeeAvatar/defauft_avatar.jpg';"
                                                 alt="Employee avatar">
                                        </c:when>
                                        <c:otherwise>
                                            <img id="avatarPreview" class="avatar-preview"
                                                 src="${pageContext.request.contextPath}/assets/imgs/EmployeeAvatar/defauft_avatar.jpg"
                                                 alt="Default employee avatar">
                                        </c:otherwise>
                                    </c:choose>
                                    <input id="avatar" type="file" class="form-control smart-file-input" name="avatar"
                                           accept="image/jpeg,image/png,image/webp">
                                    <small class="text-muted d-block mt-2">JPG, PNG, or WEBP; maximum 5 MB.</small>
                                </div>
                            </div>

                            <div class="profile-actions">
                                <button type="submit" class="btn btn-primary">Save Changes</button>
                                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ViewEmployeeProfile">Cancel</a>
                            </div>
                        </form>
                    </c:otherwise>
                </c:choose>
            </main>
        </div>

        <script>
            (function () {
                const dob = document.getElementById('dob');
                const avatar = document.getElementById('avatar');
                const preview = document.getElementById('avatarPreview');

                if (dob) {
                    dob.max = new Date().toISOString().split('T')[0];
                }

                if (avatar && preview) {
                    avatar.addEventListener('change', function () {
                        const file = this.files && this.files[0];
                        if (!file) {
                            return;
                        }
                        if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type) || file.size > 5 * 1024 * 1024) {
                            alert('Please choose a JPG, PNG, or WEBP image no larger than 5 MB.');
                            this.value = '';
                            return;
                        }
                        preview.src = URL.createObjectURL(file);
                    });
                }
            }());
        </script>
        <script src="${pageContext.request.contextPath}/assets/js/file-input.js"></script>
    </body>
</html>
