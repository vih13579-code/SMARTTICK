<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%-- 
    Document   : EmployeeListView
    Created on : Feb 23, 2025, 8:44:33 PM
    Author     : CE181159-Nguyen Le Duy Minh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Employee Management</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <style>
            body {
                display: flex;
            }

            .sidebar {
                width: 250px;
                height: 97vh;
                background: #FFFFFF;
                color: black;
                padding-top: 20px;
                box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.3);
                transform: translateZ(0);
                position: relative;
                z-index: 10;
                border-radius: 10px;
                margin-top: 10px;
            }

            .sidebar a {
                color: #7A7D90;
                text-decoration: none;
                padding: 10px;
                display: block;
            }

            .sidebar a:hover {
                background: #7D69FF;
                color: white;
                width: 90%;
                font-weight: bold;

                border-top-right-radius: 10px;
                border-bottom-right-radius: 10px;
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;

            }

            .content {
                flex-grow: 1;
                padding: 12px;
                margin-left: 250px;
            }

            .header {
                display: flex;
                justify-content: right;
                align-items: center;
                padding: 10px;
                background: #FFFFFF;
                box-shadow: 5px 5px 15px rgba(0, 0, 0, 0.3);
                border-radius: 10px;
                height: 85px;
            }

            .icon {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                object-fit: cover;
            }

            .logo-side-bar {
                margin-left: 5%;
                margin-bottom: 3%;
            }
            /* ========================================================= */

            .table-container {
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0px 5px 15px rgba(0, 0, 0, 0.1);
            }

            table {
                border-radius: 10px;
                overflow: hidden;
            }

            thead {
                background: #7D69FF;
                color: white;
            }

            tbody tr:hover {
                background: #f2f2f2;
                transition: 0.3s;
            }

            .table-navigate{
                display: flex;
                justify-content: space-between;
            }
            .search-form {
                display: flex;
                justify-content: center;
                align-items: center;
                margin-top: 20px;
            }

            .search-form input {
                border-radius: 25px;
                padding: 10px 15px;
                border: 1px solid #ccc;
                width: 250px;
                font-size: 14px;
                margin-right: 10px;
                outline: none;
            }

            .search-form button {
                background-color: #007bff;
                border: none;
                border-radius: 25px;
                padding: 10px 15px;
                cursor: pointer;
            }

            .search-form button i {
                color: white;
                font-size: 18px;
            }

            .search-form input:focus {
                border-color: #0056b3;
            }

            .search-form button:hover {
                background-color: #0056b3;
            }
        </style>
    </head>
    <body>
        <jsp:include page="SidebarDashboard.jsp"></jsp:include>
            <div class="content">
            <jsp:include page="HeaderDashboard.jsp"></jsp:include>
                <br>
                <div class="table-navigate">
                    <form action="SearchEmployeeServlet" method="GET" class="search-form">
                        <input type="text" name="query" value="${searchQuery}" placeholder="Search by Name...">
                    <button type="submit"><i class="bx bx-search"></i></button>
                </form>
                <a href="AddEmployeeView.jsp" style="background-color: #4da3ff; color: white; text-decoration: none; padding: 1px 10px; border-radius: 5px; display: inline-flex; align-items: center; gap: 5px; cursor: pointer; margin-right: 10px;">
                    <i class='bx bx-plus'></i> Add Employee
                </a>
            </div>
            <br><!-- comment -->
            <div class="table-container">
                <div>
                    <h3>Employee</h3>
                </div>
                <table  class="table table-hover">
                    <thead>
                        <tr>
                            <th>Employee ID</th>
                            <th>Role Name</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>   
                        <c:forEach items="${listE}" var="e">
                            <tr>
                                <td>${e.employeeId}</td>
                                <c:forEach items="${listR}" var="r">
                                    <c:if test="${e.roleId == r.roleId}">
                                        <td>${r.roleName}</td>
                                    </c:if>
                                </c:forEach>
                                <td>${e.fullname}</td>
                                <td>${e.email}</td>
                                <c:choose>
                                    <c:when test="${e.status == 1}">
                                        <td><span style="background-color: #28a745; color: white; padding: 5px 12px; border-radius: 15px; font-size: 12px; display: inline-flex; align-items: center; gap: 5px; text-align: center;">
                                                <i class='bx bx-check-circle' style="font-size: 14px;"></i> Available
                                            </span></td>
                                        </c:when>
                                        <c:otherwise>
                                        <td><span style="background-color: #dc3545; color: white; padding: 5px 18px; border-radius: 15px; font-size: 12px; display: inline-flex; align-items: center; gap: 5px; text-align: center;">
                                                <i class='bx bx-x-circle' style="font-size: 14px;"></i> Disable
                                            </span></td>
                                        </c:otherwise>
                                    </c:choose>
                                <td>
                                    <a href="UpdateEmployeeServlet?id=${e.employeeId}" style="background-color: orange; color: white; text-decoration: none; padding: 3px 9px; border-radius: 5px; display: inline-block; cursor: pointer;">
                                        <i class='bx bx-edit'></i> Update
                                    </a>

                                    <a href="ViewEmployeeServlet?id=${e.employeeId}" style="background-color: red; color: white; text-decoration: none; padding: 3px 9px; border-radius: 5px; display: inline-block; cursor: pointer;">
                                        <i class='bx bx-detail'></i> Detail
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>