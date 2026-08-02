<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Employee Management | SMARTTICK</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    </head>
    <body class="admin-ops-page employee-page">
        <jsp:include page="SidebarDashboard.jsp"/>

        <div class="content">
            <jsp:include page="HeaderDashboard.jsp"/>

            <main class="admin-page-stack">
                <div class="admin-page-title">
                    <div>
                        <h1>Employees</h1>
                        <p>Search staff accounts and manage roles and access status.</p>
                    </div>
                    <a class="btn btn-primary"
                       href="${pageContext.request.contextPath}/AddEmployeeServlet">
                        <i class="ti-plus"></i>
                        Add Employee
                    </a>
                </div>

                <c:if test="${param.success == 'deletesuccess'}">
                    <div class="alert alert-success" role="alert">
                        Employee disabled successfully.
                    </div>
                </c:if>
                <c:if test="${param.success == 'deletefailed'}">
                    <div class="alert alert-danger" role="alert">
                        The employee could not be disabled.
                    </div>
                </c:if>
                <c:if test="${param.error == 'notfound'}">
                    <div class="alert alert-danger" role="alert">
                        The requested employee was not found.
                    </div>
                </c:if>
                <c:if test="${not empty sessionScope.employeeMessage}">
                    <div class="alert alert-success" role="alert">
                        <c:out value="${sessionScope.employeeMessage}"/>
                    </div>
                    <c:remove var="employeeMessage" scope="session"/>
                </c:if>

                <div class="admin-toolbar employee-toolbar">
                    <form action="${pageContext.request.contextPath}/SearchEmployeeServlet"
                          method="get" class="employee-search-form">
                        <label class="visually-hidden" for="employeeSearch">Search employees</label>
                        <input id="employeeSearch" class="form-control" type="search"
                               name="query" value="<c:out value="${searchQuery}"/>"
                               placeholder="Search by employee name">
                        <button class="btn btn-primary" type="submit">
                            <i class="ti-search"></i>
                            Search
                        </button>
                        <c:if test="${not empty searchQuery}">
                            <a class="btn btn-secondary"
                               href="${pageContext.request.contextPath}/ViewEmployeeServlet">
                                Reset
                            </a>
                        </c:if>
                    </form>
                </div>

                <section class="table-container" aria-label="Employee list">
                    <div class="employee-table-summary">
                        <strong>
                            <c:choose>
                                <c:when test="${not empty searchQuery}">Search results</c:when>
                                <c:otherwise>Staff accounts</c:otherwise>
                            </c:choose>
                        </strong>
                        <span><c:out value="${fn:length(listE)}"/> employee(s)</span>
                    </div>

                    <div class="table-responsive">
                        <table class="table employee-table">
                            <thead>
                                <tr>
                                    <th scope="col">ID</th>
                                    <th scope="col">Role</th>
                                    <th scope="col">Full Name</th>
                                    <th scope="col">Email</th>
                                    <th scope="col">Status</th>
                                    <th scope="col">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty listE}">
                                        <c:forEach items="${listE}" var="employee">
                                            <tr>
                                                <td><c:out value="${employee.employeeId}"/></td>
                                                <td>
                                                    <c:forEach items="${listR}" var="role">
                                                        <c:if test="${employee.roleId == role.roleId}">
                                                            <c:out value="${role.roleName}"/>
                                                        </c:if>
                                                    </c:forEach>
                                                </td>
                                                <td><c:out value="${employee.fullname}"/></td>
                                                <td><c:out value="${employee.email}"/></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${employee.status == 1}">
                                                            <span class="employee-status active">Active</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="employee-status inactive">Disabled</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="table-actions">
                                                        <a href="${pageContext.request.contextPath}/ViewEmployeeServlet?id=${employee.employeeId}">
                                                            Details
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/UpdateEmployeeServlet?id=${employee.employeeId}">
                                                            Update
                                                        </a>
                                                        <c:if test="${employee.status == 1}">
                                                            <form action="${pageContext.request.contextPath}/DeleteEmployeeServlet"
                                                                  method="post"
                                                                  onsubmit="return confirm('Disable this employee account?');">
                                                                <input type="hidden" name="employeeId"
                                                                       value="${employee.employeeId}">
                                                                <button class="btn btn-secondary btn-sm" type="submit">
                                                                    Disable
                                                                </button>
                                                            </form>
                                                        </c:if>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td class="empty-state" colspan="6">
                                                No employees found.
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>
        </div>
    </body>
</html>
