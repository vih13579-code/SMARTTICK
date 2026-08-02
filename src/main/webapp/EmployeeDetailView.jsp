<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Employee Details | SMARTTICK</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    </head>
    <body class="admin-ops-page employee-page">
        <jsp:include page="SidebarDashboard.jsp"/>

        <div class="content">
            <jsp:include page="HeaderDashboard.jsp"/>

            <main class="admin-page-stack">
                <div class="admin-page-title">
                    <div>
                        <h1>Employee Details</h1>
                        <p>Review the selected employee account and access status.</p>
                    </div>
                    <div class="table-actions">
                        <a class="btn btn-secondary"
                           href="${pageContext.request.contextPath}/ViewEmployeeServlet">
                            Back
                        </a>
                        <a class="btn btn-primary"
                           href="${pageContext.request.contextPath}/UpdateEmployeeServlet?id=${employee.employeeId}">
                            Update Employee
                        </a>
                    </div>
                </div>

                <section class="profile-container employee-detail-card">
                    <div class="employee-avatar-panel">
                        <c:choose>
                            <c:when test="${not empty employee.avatar}">
                                <img class="employee-avatar-preview"
                                     src="${pageContext.request.contextPath}/assets/imgs/Employee/${employee.avatar}"
                                     alt="Employee avatar"
                                     onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Employee/defauft.png';">
                            </c:when>
                            <c:otherwise>
                                <img class="employee-avatar-preview"
                                     src="${pageContext.request.contextPath}/assets/imgs/Employee/defauft.png"
                                     alt="Default employee avatar">
                            </c:otherwise>
                        </c:choose>
                        <strong><c:out value="${employee.fullname}"/></strong>
                        <span><c:out value="${employee.email}"/></span>
                    </div>

                    <dl class="employee-detail-grid">
                        <div>
                            <dt>Employee ID</dt>
                            <dd><c:out value="${employee.employeeId}"/></dd>
                        </div>
                        <div>
                            <dt>Role</dt>
                            <dd>
                                <c:forEach items="${listR1}" var="role">
                                    <c:if test="${role.roleId == employee.roleId}">
                                        <c:out value="${role.roleName}"/>
                                    </c:if>
                                </c:forEach>
                            </dd>
                        </div>
                        <div>
                            <dt>Birthday</dt>
                            <dd>
                                <c:choose>
                                    <c:when test="${not empty employee.birthday}">
                                        <fmt:formatDate value="${employee.birthday}" pattern="dd/MM/yyyy"/>
                                    </c:when>
                                    <c:otherwise>Not provided</c:otherwise>
                                </c:choose>
                            </dd>
                        </div>
                        <div>
                            <dt>Phone</dt>
                            <dd><c:out value="${employee.phoneNumber}" default="Not provided"/></dd>
                        </div>
                        <div>
                            <dt>Gender</dt>
                            <dd><c:out value="${employee.gender}" default="Not provided"/></dd>
                        </div>
                        <div>
                            <dt>Created Date</dt>
                            <dd><fmt:formatDate value="${employee.createdDate}" pattern="dd/MM/yyyy"/></dd>
                        </div>
                        <div>
                            <dt>Status</dt>
                            <dd>
                                <c:choose>
                                    <c:when test="${employee.status == 1}">
                                        <span class="employee-status active">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="employee-status inactive">Disabled</span>
                                    </c:otherwise>
                                </c:choose>
                            </dd>
                        </div>
                    </dl>
                </section>
            </main>
        </div>
    </body>
</html>
