<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setLocale value="vi_VN" />
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="account-fragment">
    <div class="account-fragment-head">
        <div>
            <span class="eyebrow">Voucher</span>
            <h2>Your Vouchers</h2>
            <p class="section-sub">Available offers for your next SMARTTICK order.</p>
        </div>
    </div>

    <c:if test="${not empty sessionScope.voucherMessage}">
        <div class="alert alert-info"><c:out value="${sessionScope.voucherMessage}"/></div>
        <c:remove var="voucherMessage" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${empty sessionScope.customerVoucher}">
            <div class="account-empty">You have no voucher.</div>
        </c:when>
        <c:otherwise>
            <div class="voucher-grid">
                <c:forEach var="vou" items="${sessionScope.customerVoucher}">
                    <article class="voucher-tile">
                        <div>
                            <span class="voucher-kicker">SMARTTICK Gift Voucher</span>
                            <h3>
                                <c:choose>
                                    <c:when test="${vou.type eq 'FIXED'}">
                                        <fmt:formatNumber type="currency" value="${vou.value}"/> off
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${vou.value}" pattern="0.##"/>% off
                                    </c:otherwise>
                                </c:choose>
                            </h3>
                            <p>Minimum order: <fmt:formatNumber type="currency" value="${vou.minOrderValue}"/></p>
                            <c:set var="expirationDate" value="${empty vou.expirationDate ? vou.endDate : vou.expirationDate}" />
                            <c:if test="${not empty expirationDate}">
                                <c:set var="formattedDate" value="${fn:substring(expirationDate,8,10)}/${fn:substring(expirationDate,5,7)}/${fn:substring(expirationDate,0,4)}" />
                                <span class="voucher-expiry">Expires ${formattedDate}</span>
                            </c:if>
                        </div>
                        <strong class="voucher-count">x${vou.getQuantity()}</strong>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="account-fragment-head" style="margin-top: 28px;">
        <div>
            <span class="eyebrow">Save Voucher</span>
            <h2>Available to Save</h2>
            <p class="section-sub">Save offers before checkout so they are ready in your cart.</p>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty availableVouchers}">
            <div class="account-empty">No new voucher available to save.</div>
        </c:when>
        <c:otherwise>
            <div class="voucher-grid">
                <c:forEach var="vou" items="${availableVouchers}">
                    <article class="voucher-tile">
                        <div>
                            <span class="voucher-kicker"><c:out value="${vou.getVoucherCode()}"/></span>
                            <h3>
                                <c:choose>
                                    <c:when test="${vou.type eq 'FIXED'}">
                                        <fmt:formatNumber type="currency" value="${vou.value}"/> off
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${vou.value}" pattern="0.##"/>% off
                                    </c:otherwise>
                                </c:choose>
                            </h3>
                            <p>Minimum order: <fmt:formatNumber type="currency" value="${vou.minOrderValue}"/></p>
                            <c:if test="${not empty vou.endDate}">
                                <c:set var="formattedDate" value="${fn:substring(vou.endDate,8,10)}/${fn:substring(vou.endDate,5,7)}/${fn:substring(vou.endDate,0,4)}" />
                                <span class="voucher-expiry">Expires ${formattedDate}</span>
                            </c:if>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/SaveVoucherServlet">
                            <input type="hidden" name="voucherID" value="${vou.voucherId}">
                            <button class="btn btn-primary" type="submit">Save</button>
                        </form>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
