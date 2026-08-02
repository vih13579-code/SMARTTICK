<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Notifications | SMARTTICK</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
</head>
<body class="customer-notification-page">
<jsp:include page="header.jsp"/>

<main class="notification-main">
    <div class="container notification-shell">
        <section class="notification-hero">
            <div>
                <span class="eyebrow">Notifications</span>
                <h1>Review replies</h1>
                <p>Updates from the SMARTTICK team about reviews you have submitted.</p>
            </div>
            <div class="notification-summary" aria-label="Notification summary">
                <div>
                    <strong>${notificationCount}</strong>
                    <span>Total</span>
                </div>
                <div>
                    <strong>${unreadCount}</strong>
                    <span>Unread</span>
                </div>
            </div>
        </section>

        <c:if test="${not empty sessionScope.notificationError}">
            <div class="alert alert-danger"><c:out value="${sessionScope.notificationError}"/></div>
            <c:remove var="notificationError" scope="session"/>
        </c:if>

        <section class="notification-list" aria-label="Review reply notifications">
            <c:choose>
                <c:when test="${empty notifications}">
                    <div class="notification-empty">
                        <span class="notification-empty-icon" aria-hidden="true">&#128276;</span>
                        <h2>You are all caught up</h2>
                        <p>Replies to your product reviews will appear here.</p>
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/Watches">Continue shopping</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${notifications}" var="item">
                        <article class="notification-card ${item.read ? 'is-read' : 'is-unread'}">
                            <div class="notification-state" aria-hidden="true">
                                <span></span>
                            </div>
                            <div class="notification-copy">
                                <div class="notification-meta">
                                    <span class="notification-badge">
                                        <c:choose>
                                            <c:when test="${item.read}">Read</c:when>
                                            <c:otherwise>New reply</c:otherwise>
                                        </c:choose>
                                    </span>
                                    <c:if test="${not empty item.reviewDate}">
                                        <time><fmt:formatDate value="${item.reviewDate}" pattern="dd MMM yyyy"/></time>
                                    </c:if>
                                </div>
                                <h2><c:out value="${item.productName}" default="Product review"/></h2>
                                <c:if test="${not empty item.reviewComment}">
                                    <p class="notification-review">Your review: “<c:out value="${item.reviewComment}"/>”</p>
                                </c:if>
                                <div class="notification-reply">
                                    <span>SMARTTICK replied</span>
                                    <p><c:out value="${item.answer}"/></p>
                                </div>
                            </div>
                            <div class="notification-actions">
                                <c:if test="${item.productId > 0}">
                                    <a class="btn btn-outline"
                                       href="${pageContext.request.contextPath}/ProductDetailServlet?id=${item.productId}">
                                        View product
                                    </a>
                                </c:if>
                                <c:if test="${not item.read}">
                                    <form method="post" action="${pageContext.request.contextPath}/NotificationServlet">
                                        <input type="hidden" name="repliesID" value="${item.replyId}">
                                        <button class="btn btn-primary" type="submit">Mark as read</button>
                                    </form>
                                </c:if>
                            </div>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<jsp:include page="footer.jsp"/>
</body>
</html>
