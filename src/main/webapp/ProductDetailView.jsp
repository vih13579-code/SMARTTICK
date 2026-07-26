<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title><c:out value="${product.fullName}"/> | SMARTTICK</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<main class="container customer-page">
    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-danger"><c:out value="${sessionScope.message}"/></div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <section class="detail">
        <div>
            <div class="gallery-main">
                <img id="mainWatchImage"
                     src="${pageContext.request.contextPath}/product-images/${product.image}?v=real1"
                     onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'"
                     alt="SMARTTICK watch image">
            </div>
            <div class="thumbs">
                <c:if test="${not empty product.image}"><img src="${pageContext.request.contextPath}/product-images/${product.image}?v=real1" onclick="document.getElementById('mainWatchImage').src=this.src" alt="Main image"></c:if>
                <c:if test="${not empty product.image1}"><img src="${pageContext.request.contextPath}/product-images/${product.image1}?v=real1" onclick="document.getElementById('mainWatchImage').src=this.src" alt="Additional image"></c:if>
                <c:if test="${not empty product.image2}"><img src="${pageContext.request.contextPath}/product-images/${product.image2}?v=real1" onclick="document.getElementById('mainWatchImage').src=this.src" alt="Additional image"></c:if>
                <c:if test="${not empty product.image3}"><img src="${pageContext.request.contextPath}/product-images/${product.image3}?v=real1" onclick="document.getElementById('mainWatchImage').src=this.src" alt="Additional image"></c:if>
            </div>
        </div>

        <div>
            <span class="eyebrow"><c:out value="${product.brandName}"/></span>
            <h1><c:out value="${product.fullName}"/></h1>
            <div class="model">Model: <c:out value="${product.model}"/> | <c:out value="${product.categoryName}"/></div>
            <div class="detail-price"><fmt:formatNumber value="${product.price}" pattern="#,##0"/> VND</div>
            <p class="detail-copy"><c:out value="${product.description}"/></p>
            <p class="stock ${product.stock <= 0 ? 'out' : ''}">
                <c:choose>
                    <c:when test="${product.stock > 0}">In Stock: ${product.stock}</c:when>
                    <c:otherwise>Temporarily Out of Stock</c:otherwise>
                </c:choose>
            </p>

            <c:if test="${product.stock > 0}">
                <div class="purchase-box">
                    <label for="detailQuantity">Quantity</label>
                    <div class="quantity-row">
                        <input id="detailQuantity" class="form-control" type="number" min="1" max="${product.stock}" value="1">
                        <span>${product.stock} available</span>
                    </div>
                    <div class="card-actions">
                        <form method="post" action="${pageContext.request.contextPath}/AddToCart" onsubmit="return copyDetailQuantity(this)">
                            <input type="hidden" name="productID" value="${product.productId}">
                            <input type="hidden" name="quantity" value="1">
                            <button class="btn btn-primary" type="submit">Add to Cart</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/order" onsubmit="return copyDetailQuantity(this)">
                            <input type="hidden" name="buyProductAction" value="checkout">
                            <input type="hidden" name="orderUrl" value="buyNow">
                            <input type="hidden" name="productSelected" value="${product.productId}">
                            <input type="hidden" name="quantity" value="1">
                            <button class="btn btn-gold" type="submit">Buy Now</button>
                        </form>
                    </div>
                    <p class="bulk-note">Secure QR payment is available during checkout.</p>
                </div>
            </c:if>

            <a class="btn btn-outline continue-link" href="${pageContext.request.contextPath}/Watches">Continue Shopping</a>

            <div class="specs">
                <div class="spec-row"><strong>Brand</strong><span><c:out value="${product.brandName}"/></span></div>
                <div class="spec-row"><strong>Category</strong><span><c:out value="${product.categoryName}"/></span></div>
                <c:forEach items="${attributes}" var="a">
                    <div class="spec-row"><strong><c:out value="${a.attributeName}"/></strong><span><c:out value="${a.attributeInfor}"/></span></div>
                </c:forEach>
            </div>
        </div>
    </section>

    <section class="section alt review-section">
        <div class="section-head">
            <div>
                <h2>Product Reviews</h2>
                <p class="section-sub">Average rating: ${star}/5</p>
            </div>
        </div>
        <c:if test="${isOk}">
            <form method="post" action="${pageContext.request.contextPath}/ProductDetailServlet" class="panel">
                <input type="hidden" name="productId" value="${product.productId}">
                <label>Rating</label>
                <select class="form-select" name="star" required>
                    <option value="5">5 stars</option>
                    <option value="4">4 stars</option>
                    <option value="3">3 stars</option>
                    <option value="2">2 stars</option>
                    <option value="1">1 star</option>
                </select>
                <label class="mt-label">Review</label>
                <textarea name="comment" rows="4" required></textarea>
                <button class="btn btn-primary" type="submit">Submit Review</button>
            </form>
        </c:if>
        <c:forEach items="${dataRating}" var="r">
            <article class="panel">
                <strong><c:out value="${r.fullName}"/></strong><span> | ${r.star}/5</span>
                <p><c:out value="${r.comment}"/></p>
                <small>${r.createdDate}</small>
                <c:forEach items="${dataReplies}" var="reply">
                    <c:if test="${reply.rateID == r.rateID}">
                        <div class="alert alert-success"><strong>SMARTTICK reply:</strong> <c:out value="${reply.answer}"/></div>
                    </c:if>
                </c:forEach>
            </article>
        </c:forEach>
    </section>
</main>
<jsp:include page="footer.jsp"/>
<script>
    function copyDetailQuantity(form) {
        const quantity = document.getElementById('detailQuantity');
        const value = parseInt(quantity.value, 10);
        const max = parseInt(quantity.max, 10);
        if (!value || value < 1 || value > max) {
            quantity.focus();
            return false;
        }
        form.querySelector('input[name="quantity"]').value = value;
        return true;
    }
</script>
</body>
</html>
