<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Authentic Watches | SMARTTICK</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css"></head><body>
<jsp:include page="header.jsp" />
<main class="section"><div class="container">
  <div class="section-head"><div><span class="eyebrow">SMARTTICK Collection</span><h2>Featured Watches</h2><p class="section-sub">Find the watch that fits your style and budget.</p></div><strong>${products.size()} products</strong></div>
  <form class="filter-panel" action="${pageContext.request.contextPath}/Watches" method="get"><div class="filter-grid">
    <input class="form-control" name="q" value="<c:out value='${keyword}'/>" placeholder="Search by name or model">
    <select class="form-select" name="category"><option value="">All Categories</option><c:forEach items="${categories}" var="c"><option value="${c.name}" ${selectedCategory == c.name ? 'selected' : ''}><c:out value="${c.name}"/></option></c:forEach></select>
    <select class="form-select" name="brand"><option value="">All Brands</option><c:forEach items="${brands}" var="b"><option value="${b.name}" ${selectedBrand == b.name ? 'selected' : ''}><c:out value="${b.name}"/></option></c:forEach></select>
    <input class="form-control" type="number" min="0" name="minPrice" value="${minPrice}" placeholder="Min Price">
    <input class="form-control" type="number" min="0" name="maxPrice" value="${maxPrice}" placeholder="Max Price">
    <select class="form-select" name="sort"><option value="">Newest</option><option value="price_asc" ${sort=='price_asc'?'selected':''}>Price: Low to High</option><option value="price_desc" ${sort=='price_desc'?'selected':''}>Price: High to Low</option><option value="name_asc" ${sort=='name_asc'?'selected':''}>Name A-Z</option></select>
    <button class="btn btn-primary" type="submit">Filter</button>
  </div></form>
  <c:choose><c:when test="${empty products}"><div class="panel"><h3>No Products Found</h3><p>Try changing your keyword or filters.</p></div></c:when><c:otherwise><div class="grid grid-4">
  <c:forEach items="${products}" var="p"><article class="product-card"><a class="product-image" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}"><img loading="lazy" src="${pageContext.request.contextPath}/product-images/${p.image}" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'" alt="SMARTTICK watch image"></a><div class="product-body"><span class="brand"><c:out value="${p.brandName}"/></span><a class="product-name" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}"><c:out value="${p.fullName}"/></a><span class="model"><c:out value="${p.model}"/> · <c:out value="${p.categoryName}"/></span><div class="price"><fmt:formatNumber value="${p.price}" pattern="#,##0"/> VND</div><span class="stock ${p.stock <= 0 ? 'out' : ''}"><c:choose><c:when test="${p.stock > 0}">In Stock: ${p.stock}</c:when><c:otherwise>Temporarily Out of Stock</c:otherwise></c:choose></span><div class="card-actions"><a class="btn btn-outline" href="${pageContext.request.contextPath}/ProductDetailServlet?id=${p.productId}">Details</a><c:if test="${p.stock > 0}"><a class="btn btn-primary" href="${pageContext.request.contextPath}/AddToCart?id=${p.productId}">Add to Cart</a></c:if></div></div></article></c:forEach>
  </div></c:otherwise></c:choose>
</div></main><jsp:include page="footer.jsp" /></body></html>
