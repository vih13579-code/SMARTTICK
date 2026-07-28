<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Product Details | SMARTTICK</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin-ops.css?v=20260725-ops6">
</head>
<body class="dashboard-page admin-ops-page">
<div class="dashboard-shell">
    <jsp:include page="SidebarDashboard.jsp"/>
    <main class="dash-main">
        <div class="dash-hero">
            <div class="dash-title">
                <p>Product Management</p>
                <h1>Product Details</h1>
            </div>
            <div class="dash-actions">
                <a class="btn btn-outline" href="${pageContext.request.contextPath}/ProductListServlet">Back</a>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/UpdateProductServlet?id=${product.productId}">Edit</a>
            </div>
        </div>

        <section class="panel manager-detail">
            <div class="gallery-main">
                <img src="${pageContext.request.contextPath}/product-images/${product.image}"
                     onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'"
                     alt="Product image">
            </div>
            <div>
                <span class="brand"><c:out value="${product.brandName}"/></span>
                <h2><c:out value="${product.fullName}"/></h2>
                <p class="model"><c:out value="${product.model}"/></p>
                <div class="detail-price"><fmt:formatNumber value="${product.price}" pattern="#,##0"/> VND</div>
                <p class="detail-copy"><c:out value="${product.description}"/></p>
                <div class="specs">
                    <div class="spec-row"><strong>Category</strong><span><c:out value="${product.categoryName}"/></span></div>
                    <div class="spec-row"><strong>Stock</strong><span>${product.stock}</span></div>
                    <div class="spec-row">
                        <strong>Status</strong>
                        <span>${product.deleted == 0 ? 'On Sale' : 'Hidden'}</span>
                    </div>
                    <c:forEach items="${product.attributeDetails}" var="a">
                        <div class="spec-row">
                            <strong><c:out value="${a.attributeName}"/></strong>
                            <span><c:out value="${a.attributeInfor}"/></span>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>
    </main>
</div>
</body>
</html>




