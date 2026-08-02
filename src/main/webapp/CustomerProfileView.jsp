<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="account-fragment profile-overview">
    <div class="account-fragment-head">
        <div>
            <span class="eyebrow">Profile overview</span>
            <h2>Account overview</h2>
            <p class="section-sub">Manage your personal information, orders, addresses and vouchers.</p>
        </div>
        <a href="${pageContext.request.contextPath}/updateCustomerProfile"
           class="btn btn-primary">
            <i class="bi bi-pencil-square"></i> Edit profile
        </a>
    </div>

    <div class="account-stats">
        <a href="${pageContext.request.contextPath}/ViewOrderHistory" class="account-stat">
            <span class="account-stat-icon"><i class="bi bi-bag-check"></i></span>
            <span>Total orders</span>
            <strong>${orderCount}</strong>
        </a>
        <a href="${pageContext.request.contextPath}/ViewCustomerVoucher" class="account-stat">
            <span class="account-stat-icon"><i class="bi bi-ticket-perforated"></i></span>
            <span>Saved vouchers</span>
            <strong>${voucherCount}</strong>
        </a>
        <a href="${pageContext.request.contextPath}/ViewShippingAddress" class="account-stat">
            <span class="account-stat-icon"><i class="bi bi-geo-alt"></i></span>
            <span>Addresses</span>
            <strong>${addressCount}</strong>
        </a>
        <a href="${pageContext.request.contextPath}/changeCustomerPassword" class="account-stat">
            <span class="account-stat-icon"><i class="bi bi-shield-check"></i></span>
            <span>Sign-in password</span>
            <strong class="account-status-text">
                <c:choose>
                    <c:when test="${hasLocalPassword}">Ready</c:when>
                    <c:otherwise>Not set</c:otherwise>
                </c:choose>
            </strong>
        </a>
    </div>

    <div class="profile-summary-grid">
        <section class="profile-details-card">
            <div class="profile-card-title">
                <h3>Personal information</h3>
                <span class="badge badge-ok">Active</span>
            </div>
            <dl class="profile-details-list">
                <div>
                    <dt>Full name</dt>
                    <dd><c:out value="${sessionScope.customer.fullName}" default="Not provided"/></dd>
                </div>
                <div>
                    <dt>Email</dt>
                    <dd><c:out value="${sessionScope.customer.email}"/></dd>
                </div>
                <div>
                    <dt>Phone number</dt>
                    <dd>
                        <c:choose>
                            <c:when test="${not empty sessionScope.customer.phoneNumber}">
                                <c:out value="${sessionScope.customer.phoneNumber}"/>
                            </c:when>
                            <c:otherwise>Not provided</c:otherwise>
                        </c:choose>
                    </dd>
                </div>
                <div>
                    <dt>Gender</dt>
                    <dd><c:out value="${sessionScope.customer.gender}" default="Not provided"/></dd>
                </div>
                <div>
                    <dt>Date of birth</dt>
                    <dd>
                        <c:choose>
                            <c:when test="${not empty sessionScope.customer.birthday}">
                                <c:out value="${sessionScope.customer.birthday}"/>
                            </c:when>
                            <c:otherwise>Not provided</c:otherwise>
                        </c:choose>
                    </dd>
                </div>
            </dl>
        </section>

        <section class="profile-avatar-card">
            <c:set var="customerAvatar" value="${sessionScope.customer.avatar}"/>
            <c:choose>
                <c:when test="${not empty customerAvatar && fn:startsWith(customerAvatar, 'http')}">
                    <img class="profile-overview-avatar" src="${customerAvatar}" alt="Customer avatar"
                         onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                </c:when>
                <c:when test="${not empty customerAvatar}">
                    <img class="profile-overview-avatar"
                         src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/${customerAvatar}"
                         alt="Customer avatar"
                         onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                </c:when>
                <c:otherwise>
                    <img class="profile-overview-avatar"
                         src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg"
                         alt="Customer avatar">
                </c:otherwise>
            </c:choose>
            <strong><c:out value="${sessionScope.customer.fullName}"/></strong>
            <span>Member since
                <c:choose>
                    <c:when test="${not empty sessionScope.customer.createDate}">
                        <c:out value="${fn:substring(sessionScope.customer.createDate, 0, 10)}"/>
                    </c:when>
                    <c:otherwise>SMARTTICK</c:otherwise>
                </c:choose>
            </span>
        </section>
    </div>

    <section class="recent-orders-card">
        <div class="profile-card-title">
            <div>
                <span class="eyebrow">Recent activity</span>
                <h3>Latest orders</h3>
            </div>
            <a href="${pageContext.request.contextPath}/ViewOrderHistory">View all</a>
        </div>
        <div class="table-scroll">
            <table class="table account-order-table">
                <thead>
                    <tr>
                        <th>Order</th>
                        <th>Date</th>
                        <th>Total</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${recentOrders}" var="order">
                        <tr>
                            <td>#${order.orderID}</td>
                            <td><c:out value="${order.orderDate}"/></td>
                            <td><fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/> VND</td>
                            <td>
                                <span class="order-status">
                                    <c:choose>
                                        <c:when test="${order.status == 1}">Waiting for acceptance</c:when>
                                        <c:when test="${order.status == 2}">Packaging</c:when>
                                        <c:when test="${order.status == 3}">Waiting for delivery</c:when>
                                        <c:when test="${order.status == 4}">Delivered</c:when>
                                        <c:when test="${order.status == 5}">Cancelled</c:when>
                                        <c:otherwise>Unknown</c:otherwise>
                                    </c:choose>
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty recentOrders}">
                        <tr>
                            <td colspan="4" class="account-table-empty">No orders yet.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </section>
</div>
