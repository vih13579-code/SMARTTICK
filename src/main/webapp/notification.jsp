<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Notifications | SMARTTICK</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<main class="container customer-page">
    <section class="section alt">
        <div class="section-head">
            <div>
                <span class="eyebrow">Notifications</span>
                <h2>Review Replies</h2>
                <p class="section-sub">Replies from SMARTTICK to your product reviews.</p>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty replies}">
                <div class="panel">You have no notifications.</div>
            </c:when>
            <c:otherwise>
                <div class="catalog-grid">
                    <c:forEach items="${replies}" var="reply" varStatus="loop">
                        <c:set var="product" value="${products[loop.index]}"/>
                        <article class="panel">
                            <span class="eyebrow">
                                <c:choose>
                                    <c:when test="${reply.isRead}">Read</c:when>
                                    <c:otherwise>Unread</c:otherwise>
                                </c:choose>
                            </span>
                            <h3><c:out value="${product.fullName}" default="Product review"/></h3>
                            <p><c:out value="${reply.answer}"/></p>
                            <div class="card-actions">
                                <c:if test="${not empty product.productId && product.productId > 0}">
                                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${product.productId}">View Product</a>
                                </c:if>
                                <c:if test="${!reply.isRead}">
                                    <form method="post" action="${pageContext.request.contextPath}/NotificationServlet">
                                        <input type="hidden" name="repliesID" value="${reply.replyID}">
                                        <button class="btn btn-primary" type="submit">Mark as Read</button>
                                    </form>
                                </c:if>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>
<jsp:include page="footer.jsp"/>
</body>
</html>
