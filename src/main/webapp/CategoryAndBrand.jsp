
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.ArrayList, Models.Category, Models.Brand" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Category & Brand</title>
    </head>
    <body>
        <form action="BrandServlet" method="post">
            <label for="brandName">Create New Brand:</label>
            <input type="text" name="brandName" id="brandName" required>
            <button type="submit">Add Brand</button>
        </form>

        <form action="#" method="post">
            <label for="category">Select Category:</label>
            <select name="category" id="category">
                <c:forEach var="category" items="${categories}">
                    <option value="${category.id}">${category.name}</option>
                </c:forEach>
            </select>

            <label for="brand">Select Brand:</label>
            <select name="brand" id="brand">
                <c:forEach var="brand" items="${brands}">
                    <option value="${brand.id}">${brand.name}</option>
                </c:forEach>
            </select>

            <button type="submit">Submit</button>
        </form>
    </body>
</html>

