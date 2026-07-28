<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
                                    <c:when test="${vou.getVoucherType() == 0}">
                                        <fmt:formatNumber type="currency" value="${vou.getVoucherValue()}"/> off
                                    </c:when>
                                    <c:otherwise>
                                        ${vou.getVoucherValue()}% off
                                    </c:otherwise>
                                </c:choose>
                            </h3>
                            <p><c:out value="${vou.getDescription()}"/></p>
                            <c:set var="expirationDate" value="${empty vou.expirationDate ? vou.endDate : vou.expirationDate}" />
                            <c:if test="${not empty expirationDate}">
                                <c:set var="formattedDate" value="${expirationDate.substring(8,10)}/${expirationDate.substring(5,7)}/${expirationDate.substring(0,4)}" />
                                <span class="voucher-expiry">Expires ${formattedDate}</span>
                            </c:if>
                        </div>
                        <strong class="voucher-count">x${vou.getQuantity()}</strong>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
