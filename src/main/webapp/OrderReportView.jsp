
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h2 class="mt-3">Order Summary</h2>
<table class="table table-bordered">
    <tr><th>Total Orders</th><td>${stats.totalOrders}</td></tr>
    <tr><th>Total Revenue</th><td>${stats.totalRevenue} VND</td></tr>
    <tr><th>Pending</th><td>${stats.pending}</td></tr>
    <tr><th>Shipping</th><td>${stats.shipping}</td></tr>
    <tr><th>Delivered</th><td>${stats.delivered}</td></tr>
    <tr><th>Cancelled</th><td>${stats.cancelled}</td></tr>
</table>

<h3 class="mt-4">Top Customers</h3>
<table class="table table-striped">
    <thead>
        <tr>
            <th>Customer ID</th>
            <th>Full Name</th>
            <th>Total Orders</th>
            <th>Total Spent</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="c" items="${topCustomers}">
            <tr>
                <td>${c.customerID}</td>
                <td>${c.fullName}</td>
                <td>${c.totalOrders}</td>
                <td>${c.totalSpent}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>

    </body>
</html>
