
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        <form action="ExportStock" method="POST">
            <button type="submit">Export to Excel</button>
        </form>
        <c:forEach items="${dataStock}" var="stock">
            <div>
                <p>IO ID: ${stock.IOID}</p>
                <p>Employee Name: ${stock.employeeName}</p>
                <p>Supplier: ${stock.supplierName}</p>
            </div>
            <hr /> 
        </c:forEach>
    </body>
</html>
