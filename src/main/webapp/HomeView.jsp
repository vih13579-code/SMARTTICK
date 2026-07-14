<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>SMARTTICK - Authentic Watches</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<section class="hero">
    <div class="container hero-copy">
        <div class="eyebrow">SMARTTICK Selection 2026</div>
        <h1>Time defines style.</h1>
        <p>Explore standout watches from Casio, Citizen, Seiko, Orient, Tissot, and Daniel Wellington.</p>
        <div style="display:flex;gap:12px;margin-top:28px">
            <a class="btn btn-gold" href="${pageContext.request.contextPath}/Watches">Explore the Collection</a>
            <a class="btn btn-outline" style="color:#fff;border-color:#777;background:transparent" href="${pageContext.request.contextPath}/Mechanical">Mechanical Watches</a>
        </div>
    </div>
</section>

<section class="section">
    <div class="container">
        <div class="section-head">
            <div><h2>Featured Products</h2><div class="section-sub">Selected highlights from SMARTTICK</div></div>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/Watches">View All</a>
        </div>
        <div class="grid grid-4">
            <c:forEach items="${featuredProducts}" var="p">
                <article class="product-card">
                    <a class="product-image" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}">
                        <img src="${pageContext.request.contextPath}/product-images/${p.image}" alt="SMARTTICK watch image" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'">
                    </a>
                    <div class="product-body">
                        <div class="brand"><c:out value="${p.brandName}"/></div>
                        <a class="product-name" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}"><c:out value="${p.fullName}"/></a>
                        <div class="model"><c:out value="${p.model}"/></div>
                        <div class="price"><fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND</div>
                        <div class="stock">${p.stock} products in stock</div>
                        <div class="card-actions">
                            <a class="btn btn-outline" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}">Details</a>
                            <a class="btn btn-primary" href="${pageContext.request.contextPath}/AddToCart?id=${p.productId}">Add to Cart</a>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </div>
        <c:if test="${empty featuredProducts}"><div class="alert alert-danger">Product data could not be loaded. Please check the SMARTTICK database connection.</div></c:if>
    </div>
</section>

<section class="section alt">
    <div class="container">
        <div class="section-head"><div><h2>Shop by Style</h2><div class="section-sub">Focused collections that are easy to browse</div></div></div>
        <div class="grid grid-3">
            <a class="benefit" href="${pageContext.request.contextPath}/Men"><h3>Men's Watches</h3><p>Office-ready, classic, and sporty designs.</p></a>
            <a class="benefit" href="${pageContext.request.contextPath}/Women"><h3>Women's Watches</h3><p>Elegant, minimal, and easy to pair with outfits.</p></a>
            <a class="benefit" href="${pageContext.request.contextPath}/Mechanical"><h3>Mechanical Watches</h3><p>Mechanical movements for enthusiasts who love engineering.</p></a>
        </div>
    </div>
</section>

<section class="section"><div class="container"><div class="grid grid-3">
    <div class="benefit"><h3>Authentic Products</h3><p>Products with clear origin and transparent technical details.</p></div>
    <div class="benefit"><h3>Reliable Warranty</h3><p>Warranty terms are clearly listed for each watch model.</p></div>
    <div class="benefit"><h3>Dedicated Support</h3><p>Advice tailored to your budget, wrist size, and daily style.</p></div>
</div></div></section>
<jsp:include page="footer.jsp"/>
</body>
</html>
