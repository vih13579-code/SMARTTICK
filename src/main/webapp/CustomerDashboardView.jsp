<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>My Account | SMARTTICK</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<main class="section">
    <div class="container">
        <div class="section-head">
            <div>
                <span class="eyebrow">Customer Dashboard</span>
                <h2>Hello, <c:out value="${sessionScope.customer.fullName}"/></h2>
                <p class="section-sub">Track orders and manage your personal information.</p>
            </div>
        </div>

        <div class="stats">
            <div class="stat"><small>Total Orders</small><strong>${orderCount}</strong></div>
            <div class="stat"><small>Available Vouchers</small><strong>${voucherCount}</strong></div>
            <div class="stat"><small>Shipping Addresses</small><strong>${addressCount}</strong></div>
            <div class="stat"><small>Account Status</small><strong>Active</strong></div>
        </div>

        <div class="quick-links">
            <a class="benefit" href="${pageContext.request.contextPath}/viewCustomerProfile"><h3>Profile</h3><p>Update your account information.</p></a>
            <a class="benefit" href="${pageContext.request.contextPath}/changeCustomerPassword"><h3>Change Password</h3><p>Protect your account.</p></a>
            <a class="benefit" href="${pageContext.request.contextPath}/ViewOrderHistory"><h3>Order History</h3><p>View order status and purchase details.</p></a>
            <a class="benefit" href="${pageContext.request.contextPath}/ViewShippingAddress"><h3>Addresses</h3><p>Manage shipping addresses.</p></a>
            <a class="benefit" href="${pageContext.request.contextPath}/ViewCustomerVoucher"><h3>Voucher</h3><p>View your available offers.</p></a>
            <a class="benefit" href="${pageContext.request.contextPath}/cart"><h3>Cart</h3><p>Review selected products.</p></a>
            <a class="benefit" href="${pageContext.request.contextPath}/Watches"><h3>Continue Shopping</h3><p>Explore the SMARTTICK collection.</p></a>
            <a class="benefit" href="${pageContext.request.contextPath}/Logout"><h3>Log Out</h3><p>Sign out of the current account.</p></a>
        </div>

        <div class="panel">
            <h3>Recent Orders</h3>
            <div class="table-scroll">
                <table class="table">
                    <thead><tr><th>Order ID</th><th>Order Date</th><th>Total</th><th>Status</th></tr></thead>
                    <tbody>
                    <c:forEach items="${recentOrders}" var="o">
                        <tr>
                            <td>#${o.orderID}</td>
                            <td><c:out value="${o.orderDate}"/></td>
                            <td><fmt:formatNumber value="${o.totalAmount}" pattern="#,##0"/> VND</td>
                            <td>
                                <c:choose>
                                    <c:when test="${o.status == 1}">Pending Confirmation</c:when>
                                    <c:when test="${o.status == 2}">Confirmed</c:when>
                                    <c:when test="${o.status == 3}">Shipping</c:when>
                                    <c:when test="${o.status == 4}">Completed</c:when>
                                    <c:when test="${o.status == 5}">Canceled</c:when>
                                    <c:otherwise>Unknown</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty recentOrders}"><tr><td colspan="4">You do not have any orders yet.</td></tr></c:if>
                    </tbody>
                </table>
            </div>
        </div>

        <c:if test="${not empty purchasedProducts}">
            <section class="section">
                <div class="section-head"><div><h2>Purchased Products</h2></div></div>
                <div class="grid grid-4">
                    <c:forEach items="${purchasedProducts}" var="p">
                        <article class="product-card">
                            <a class="product-image" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}">
                                <img src="${pageContext.request.contextPath}/product-images/${p.image}" alt="SMARTTICK watch image">
                            </a>
                            <div class="product-body">
                                <span class="brand"><c:out value="${p.brandName}"/></span>
                                <a class="product-name" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}"><c:out value="${p.fullName}"/></a>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </section>
        </c:if>

        <section class="section">
            <div class="section-head"><div><h2>Recommended for You</h2></div><a href="${pageContext.request.contextPath}/Watches">View All</a></div>
            <div class="grid grid-4">
                <c:forEach items="${recommendedProducts}" var="p">
                    <article class="product-card">
                        <a class="product-image" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}">
                            <img src="${pageContext.request.contextPath}/product-images/${p.image}" alt="SMARTTICK watch image">
                        </a>
                        <div class="product-body">
                            <span class="brand"><c:out value="${p.brandName}"/></span>
                            <a class="product-name" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}"><c:out value="${p.fullName}"/></a>
                            <div class="price"><fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND</div>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </section>
    </div>
</main>
<jsp:include page="footer.jsp"/>
</body>
</html>
