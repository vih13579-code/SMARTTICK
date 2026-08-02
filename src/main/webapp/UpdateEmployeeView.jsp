<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="formRole" value="${requestScope.txtRoleId != null ? txtRoleId : employee.roleId}"/>
<c:set var="formName" value="${requestScope.txtName != null ? txtName : employee.fullname}"/>
<c:set var="formBirthday" value="${requestScope.txtBirthday != null ? txtBirthday : employee.birthday}"/>
<c:set var="formPhone" value="${requestScope.txtPhoneNumber != null ? txtPhoneNumber : employee.phoneNumber}"/>
<c:set var="formEmail" value="${requestScope.txtEmail != null ? txtEmail : employee.email}"/>
<c:set var="formGender" value="${requestScope.txtGender != null ? txtGender : employee.gender}"/>
<c:set var="formStatus" value="${requestScope.txtStatus != null ? txtStatus : employee.status}"/>
<c:set var="formAvatar" value="${requestScope.currentAvatar != null ? currentAvatar : employee.avatar}"/>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Update Employee | SMARTTICK</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    </head>
    <body class="admin-ops-page employee-page">
        <jsp:include page="SidebarDashboard.jsp"/>

        <div class="content">
            <jsp:include page="HeaderDashboard.jsp"/>

            <main class="admin-page-stack">
                <div class="admin-page-title">
                    <div>
                        <h1>Update Employee</h1>
                        <p>Edit staff information, role, and account status.</p>
                    </div>
                    <a class="btn btn-secondary"
                       href="${pageContext.request.contextPath}/ViewEmployeeServlet">
                        Back to Employees
                    </a>
                </div>

                <c:if test="${not empty errorMsg}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${errorMsg}"/>
                    </div>
                </c:if>
                <c:if test="${not empty popupErrorMsg}">
                    <div class="alert alert-danger" role="alert">
                        <c:out value="${popupErrorMsg}"/>
                    </div>
                </c:if>
                <c:if test="${not empty popupSuccessMsg}">
                    <div class="alert alert-success" role="alert">
                        <c:out value="${popupSuccessMsg}"/>
                        <a href="${pageContext.request.contextPath}/ViewEmployeeServlet">
                            Return to the employee list.
                        </a>
                    </div>
                </c:if>

                <form class="employee-form-card"
                      action="${pageContext.request.contextPath}/UpdateEmployeeServlet"
                      method="post" enctype="multipart/form-data">
                    <input type="hidden" name="txtEmployeeId" value="${employee.employeeId}">

                    <div class="employee-form-fields">
                        <div>
                            <label class="form-label" for="employeeRole">Role</label>
                            <select class="form-select" id="employeeRole" name="txtRoleId" required>
                                <option value="2" ${formRole == 2 ? 'selected' : ''}>Shop Manager</option>
                                <option value="3" ${formRole == 3 ? 'selected' : ''}>Order Manager</option>
                                <option value="4" ${formRole == 4 ? 'selected' : ''}>Warehouse Manager</option>
                            </select>
                        </div>

                        <div>
                            <label class="form-label" for="employeeName">Full Name</label>
                            <input class="form-control" id="employeeName" type="text"
                                   name="txtName" maxlength="255"
                                   value="${fn:escapeXml(formName)}" required>
                        </div>

                        <div>
                            <label class="form-label" for="employeePassword">New Password</label>
                            <input class="form-control" id="employeePassword" type="password"
                                   name="txtPass" minlength="8" maxlength="50"
                                   autocomplete="new-password"
                                   placeholder="Leave blank to keep the current password">
                        </div>

                        <div>
                            <label class="form-label" for="employeeBirthday">Birthday</label>
                            <input class="form-control" id="employeeBirthday" type="date"
                                   name="txtBirthday" value="${formBirthday}">
                        </div>

                        <div>
                            <label class="form-label" for="employeePhone">Phone</label>
                            <input class="form-control" id="employeePhone" type="tel"
                                   name="txtPhoneNumber" maxlength="10" inputmode="numeric"
                                   value="${fn:escapeXml(formPhone)}" required>
                        </div>

                        <div>
                            <label class="form-label" for="employeeEmail">Email</label>
                            <input class="form-control" id="employeeEmail" type="email"
                                   name="txtEmail" maxlength="254"
                                   value="${fn:escapeXml(formEmail)}" required>
                        </div>

                        <div>
                            <label class="form-label" for="employeeGender">Gender</label>
                            <select class="form-select" id="employeeGender" name="txtGender">
                                <option value="Male" ${formGender == 'Male' ? 'selected' : ''}>Male</option>
                                <option value="Female" ${formGender == 'Female' ? 'selected' : ''}>Female</option>
                                <option value="Other" ${formGender == 'Other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>

                        <div>
                            <label class="form-label" for="employeeCreatedDate">Created Date</label>
                            <input class="form-control" id="employeeCreatedDate" type="date"
                                   value="${employee.createdDate}" readonly>
                        </div>

                        <div>
                            <label class="form-label" for="employeeStatus">Status</label>
                            <select class="form-select" id="employeeStatus" name="txtStatus" required>
                                <option value="1" ${formStatus == 1 ? 'selected' : ''}>Active</option>
                                <option value="0" ${formStatus == 0 ? 'selected' : ''}>Disabled</option>
                            </select>
                        </div>

                        <div class="employee-form-actions">
                            <a class="btn btn-secondary"
                               href="${pageContext.request.contextPath}/ViewEmployeeServlet">
                                Cancel
                            </a>
                            <button class="btn btn-primary" type="submit">Save Changes</button>
                        </div>
                    </div>

                    <div class="employee-avatar-editor">
                        <label class="form-label" for="employeeAvatar">Avatar</label>
                        <img id="avatarPreview" class="employee-avatar-preview"
                             src="${pageContext.request.contextPath}/assets/imgs/Employee/${not empty formAvatar ? formAvatar : 'defauft.png'}"
                             alt="Employee avatar preview"
                             onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Employee/defauft.png';">
                        <input type="hidden" name="currentAvatar"
                               value="${fn:escapeXml(formAvatar)}">
                        <input class="form-control smart-file-input" id="employeeAvatar"
                               type="file" name="txtAvatar"
                               accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp">
                        <small>JPG, PNG, or WEBP. Maximum 5 MB.</small>
                    </div>
                </form>
            </main>
        </div>

        <script>
            (function () {
                const fileInput = document.getElementById("employeeAvatar");
                const preview = document.getElementById("avatarPreview");
                fileInput.addEventListener("change", function () {
                    const file = this.files && this.files[0];
                    if (!file) {
                        return;
                    }
                    if (file.size > 5 * 1024 * 1024) {
                        alert("The avatar must not exceed 5 MB.");
                        this.value = "";
                        return;
                    }
                    preview.src = URL.createObjectURL(file);
                });
            }());
        </script>
    </body>
</html>
