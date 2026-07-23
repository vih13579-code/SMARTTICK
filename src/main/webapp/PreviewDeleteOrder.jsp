
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Preview Orders to Delete</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
        <h2>Orders to be Deleted (Older than ${deleteMonth} Month(s))</h2>
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>Order ID</th>
                    <th>Customer ID</th>
                    <th>Full Name</th>
                    <th>Phone Number</th>
                    <th>Address</th>
                    <th>Total Amount</th>
                    <th>Ordered Date</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty ordersToDelete}">
                        <c:forEach var="order" items="${ordersToDelete}">
                            <tr>
                                <td>${order.orderID}</td>
                                <td>${order.accountID}</td>
                                <td>${order.fullName}</td>
                                <td>${order.phone}</td>
                                <td>${order.address}</td>
                                <td>${order.totalAmount}</td>
                                <td>${order.orderDate}</td>
                                <td>${order.status}</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="8" class="text-center">No orders found for deletion.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
     
    </div>
</body>
</html>

