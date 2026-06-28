<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Employee Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <style>
            body {
                display: flex;
                background: #f6f7fb;
            }
            .content {
                flex-grow: 1;
                padding: 12px;
                margin-left: 250px;
            }
            .table-container {
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
            }
            thead {
                background: #7D69FF;
                color: white;
            }
            .toolbar {
                display: flex;
                gap: 12px;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
            }
            .filter-form {
                display: flex;
                gap: 10px;
                align-items: center;
                flex-wrap: wrap;
            }
            .filter-form .form-control {
                width: 280px;
            }
            .filter-form .form-select {
                width: 165px;
            }
            .employee-avatar {
                width: 42px;
                height: 42px;
                border-radius: 50%;
                object-fit: cover;
                border: 1px solid #e5e7eb;
            }
            mark {
                padding: 0 2px;
                border-radius: 3px;
                background: #fff3cd;
            }
            @media (max-width: 900px) {
                body {
                    display: block;
                }
                .content {
                    margin-left: 0;
                }
                .filter-form .form-control,
                .filter-form .form-select,
                .filter-form .btn {
                    width: 100%;
                }
                .toolbar {
                    align-items: stretch;
                }
            }
        </style>
    </head>
    <body>
        <jsp:include page="SidebarDashboard.jsp"/>
        <div class="content">
            <jsp:include page="HeaderDashboard.jsp"/>
            <br>
            <div class="toolbar">
                <form action="ViewEmployeeServlet" method="GET" class="filter-form">
                    <input type="text" class="form-control" name="query" value="${searchQuery}" placeholder="ID, name, email, phone, role, status">
                    <select class="form-select" name="roleId">
                        <option value="">All roles</option>
                        <c:forEach items="${listR}" var="r">
                            <option value="${r.roleId}" ${selectedRoleId == r.roleId ? 'selected' : ''}><c:out value="${r.roleName}"/></option>
                        </c:forEach>
                    </select>
                    <select class="form-select" name="status">
                        <option value="">All status</option>
                        <option value="1" ${selectedStatus == 1 ? 'selected' : ''}>Available</option>
                        <option value="0" ${selectedStatus == 0 ? 'selected' : ''}>Disable</option>
                    </select>
                    <select class="form-select" name="sort">
                        <option value="id_asc" ${empty selectedSort || selectedSort == 'id_asc' ? 'selected' : ''}>ID ascending</option>
                        <option value="id_desc" ${selectedSort == 'id_desc' ? 'selected' : ''}>ID descending</option>
                        <option value="name_asc" ${selectedSort == 'name_asc' ? 'selected' : ''}>Name A-Z</option>
                        <option value="name_desc" ${selectedSort == 'name_desc' ? 'selected' : ''}>Name Z-A</option>
                        <option value="created_desc" ${selectedSort == 'created_desc' ? 'selected' : ''}>Newest</option>
                        <option value="created_asc" ${selectedSort == 'created_asc' ? 'selected' : ''}>Oldest</option>
                    </select>
                    <button type="submit" class="btn btn-primary"><i class="bx bx-search"></i></button>
                    <a href="ViewEmployeeServlet" class="btn btn-outline-secondary">Reset</a>
                </form>
                <a href="AddEmployeeView.jsp" class="btn btn-success">
                    <i class="bx bx-plus"></i> Add Employee
                </a>
            </div>
            <br>
            <div class="table-container">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h3 class="m-0">Employee</h3>
                    <span class="text-muted">${totalEmployees} result(s)</span>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Employee ID</th>
                                <th>Avatar</th>
                                <th>Role</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Status</th>
                                <th>Created Date</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${listE}" var="e">
                                <tr>
                                    <td>${e.employeeId}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty e.avatar}">
                                                <img class="employee-avatar" src="${e.avatar}" alt="${e.fullname}">
                                            </c:when>
                                            <c:otherwise>
                                                <span class="employee-avatar d-inline-flex align-items-center justify-content-center bg-light"><i class="bx bx-user"></i></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:forEach items="${listR}" var="r">
                                            <c:if test="${e.roleId == r.roleId}">
                                                <c:out value="${r.roleName}"/>
                                            </c:if>
                                        </c:forEach>
                                    </td>
                                    <td><c:out value="${e.fullname}"/></td>
                                    <td><c:out value="${e.email}"/></td>
                                    <td><c:out value="${e.phoneNumber}"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${e.status == 1}">
                                                <span class="badge bg-success"><i class="bx bx-check-circle"></i> Available</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger"><i class="bx bx-x-circle"></i> Disable</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${e.createdDate}</td>
                                    <td>
                                        <div class="d-flex flex-wrap gap-2">
                                            <a href="UpdateEmployeeServlet?id=${e.employeeId}" class="btn btn-sm btn-warning">
                                                <i class="bx bx-edit"></i> Update
                                            </a>
                                            <a href="ViewEmployeeServlet?id=${e.employeeId}" class="btn btn-sm btn-outline-primary">
                                                <i class="bx bx-detail"></i> Detail
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty listE}">
                                <tr>
                                    <td colspan="9" class="text-center text-muted py-4">No employees found.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>

                <c:if test="${totalPages > 1}">
                    <nav aria-label="Employee pagination">
                        <ul class="pagination justify-content-end">
                            <c:url var="prevUrl" value="ViewEmployeeServlet">
                                <c:param name="query" value="${searchQuery}"/>
                                <c:param name="roleId" value="${selectedRoleId}"/>
                                <c:param name="status" value="${selectedStatus}"/>
                                <c:param name="sort" value="${selectedSort}"/>
                                <c:param name="page" value="${currentPage - 1}"/>
                            </c:url>
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}"><a class="page-link" href="${prevUrl}">Previous</a></li>
                            <c:forEach begin="1" end="${totalPages}" var="p">
                                <c:url var="pageUrl" value="ViewEmployeeServlet">
                                    <c:param name="query" value="${searchQuery}"/>
                                    <c:param name="roleId" value="${selectedRoleId}"/>
                                    <c:param name="status" value="${selectedStatus}"/>
                                    <c:param name="sort" value="${selectedSort}"/>
                                    <c:param name="page" value="${p}"/>
                                </c:url>
                                <li class="page-item ${currentPage == p ? 'active' : ''}"><a class="page-link" href="${pageUrl}">${p}</a></li>
                            </c:forEach>
                            <c:url var="nextUrl" value="ViewEmployeeServlet">
                                <c:param name="query" value="${searchQuery}"/>
                                <c:param name="roleId" value="${selectedRoleId}"/>
                                <c:param name="status" value="${selectedStatus}"/>
                                <c:param name="sort" value="${selectedSort}"/>
                                <c:param name="page" value="${currentPage + 1}"/>
                            </c:url>
                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}"><a class="page-link" href="${nextUrl}">Next</a></li>
                        </ul>
                    </nav>
                </c:if>
            </div>
        </div>
    </body>
</html>
