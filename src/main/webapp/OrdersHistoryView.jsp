<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="vi_VN" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="Models.Order"%>
<%@page import="Models.OrderDetail"%>


<html>
    <head>
        <title>Order History</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 0;
            }

            .nav nav a:hover{
                text-decoration: none;
            }
            .nav nav{
                border-radius: 5px;
                display: flex;
                padding: 30px;
            }

            .nav a {
                text-decoration: none;
                color: black;
                font-weight: bold;
                padding-right: 20px;
                padding-left: 20px;
            }

            .nav nav a.active {
                color: red;
                border-bottom: 2px solid red;
            }

            .order-list {
                display: none;
            }

            .order-list.active {
                display: block;
            }
        </style>
    </head>
    <body>
        <div class="nav">
            <nav class="nav" style="background: white; box-shadow: 2px 2px 2px 2px lightgray;">
                <a href="#" class="tab-link" onclick="showTab('all', this)">All</a>
                <a href="#" class="tab-link" onclick="showTab('waiting', this)">Waiting for acceptance</a>
                <a href="#" class="tab-link" onclick="showTab('transport', this)">Packaging</a>
                <a href="#" class="tab-link" onclick="showTab('delivery', this)">Waiting for delivery</a>
                <a href="#" class="tab-link" onclick="showTab('delivered', this)">Delivered</a>
                <a href="#" class="tab-link" onclick="showTab('canceled', this)">Cancelled</a>
            </nav>
        </div>
        <div style="width: 100%; background: rgba(231, 220, 220, 0);">
            <br>
        </div>
        <div id="all" class="order-list active">
            <c:if test="${sessionScope.orderList != null && !sessionScope.orderList.isEmpty()}">
                <c:forEach items="${sessionScope.orderList}" var="o">
                    <div style="background-color: white; width: 100%; border-radius: 10px; box-shadow: 2px 2px 2px 2px lightgray;">
                        <div style="text-align: right; padding: 15px; border-bottom: 0.5px solid rgb(226, 214, 214); color:rgb(238,84,61); width: 95%; margin: 0 auto">
                            <c:choose>
                                <c:when test="${o.getStatus() == 1}">Waiting For Acceptance</c:when>
                                <c:when test="${o.getStatus() == 2}">Packaging</c:when>
                                <c:when test="${o.getStatus() == 3}">Waiting For Delivery</c:when>
                                <c:when test="${o.getStatus() == 4}">Delivered</c:when>
                                <c:otherwise>Cancelled</c:otherwise>
                            </c:choose>
                        </div>
                        <br>
                        <div class="container-fluid" style="border-bottom: 0.1px rgb(226, 214, 214) solid; width: 95%; margin: 0 auto">
                            <div class="row" style="display: flex; align-items: center;">
                                <c:forEach var="od" items="${sessionScope.orderDetailList}">
                                    <c:if test="${o.getOrderID() == od.getOrderID()}">
                                        <div class="col-md-2">
                                            <img style="border: 0.5px solid gray;" width="100px" height="auto"
                                                 src="${pageContext.request.contextPath}/product-images/${od.getImage()}" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'"
                                                 alt="">
                                        </div>
                                        <div class="col-md-8">
                                            <a href="odetailforcus?id=${o.getOrderID()}" style="text-decoration: none; color: black;">
                                                <div>
                                                    ${od.getProductName()}
                                                </div>
                                                <div>
                                                    Quantity: ${od.getQuantity()}
                                                </div>
                                            </a>
                                        </div>
                                        <div class="col-md-2" style="color: rgb(238,84,61); text-align: right;"><fmt:formatNumber value="${od.getPrice()}" type="currency"/></div>
                                    </div>
                                </c:if>
                            </c:forEach>
                            <br>
                        </div>
                        <div style="text-align: right; padding: 10px; margin-top: 10px; width: 95%; margin: 0 auto">Total: <h4 style="color: rgb(238,84,61);"><fmt:formatNumber value="${o.getTotalAmount()}" type="currency" /></h4>
                        </div>
                    </div>
                    <div style="width: 100%; background: rgba(231, 220, 220, 0);">
                        <br>
                    </div>
                </c:forEach>
            </c:if>
            <c:if test="${sessionScope.orderList.isEmpty()}">
                <div style="background: white; height: 400px; text-align: center; padding: 130px; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                    <img style="border-radius: 50%;" width="70px" src="./assets/imgs/icon/no-order.png" alt="alt"/>
                    <br>
                    <h6 style="padding: 10px">There is no order.</h6>
                </div>
            </c:if>
        </div>
        <% boolean hasWaiting = false;%>
        <div id="waiting" class="order-list">
            <c:forEach items="${sessionScope.orderList}" var="o">
                <c:if test="${o.getStatus() == 1}">
                    <% hasWaiting = true; %>
                    <div style="background-color: white; width: 100%; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                        <div style="text-align: right; padding: 15px; border-bottom: 0.5px solid rgb(226, 214, 214); color:rgb(238,84,61); width: 95%; margin: 0 auto">
                            <c:choose>
                                <c:when test="${o.getStatus() == 1}">Waiting For Acceptance</c:when>
                                <c:when test="${o.getStatus() == 2}">Packaging</c:when>
                                <c:when test="${o.getStatus() == 3}">Waiting For Delivery</c:when>
                                <c:when test="${o.getStatus() == 4}">Delivered</c:when>
                                <c:otherwise>Cancelled</c:otherwise>
                            </c:choose>
                        </div>
                        <br>
                        <div class="container-fluid" style="border-bottom: 0.1px rgb(226, 214, 214) solid; width: 95%; margin: 0 auto">
                            <div class="row" style="display: flex; align-items: center;">
                                <c:forEach var="od" items="${sessionScope.orderDetailList}">
                                    <c:if test="${o.getOrderID() == od.getOrderID()}">
                                        <div class="col-md-2">
                                            <img style="border: 0.5px solid gray;" width="100px" height="auto"
                                                 src="${pageContext.request.contextPath}/product-images/${od.getImage()}" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'"
                                                 alt="">
                                        </div>
                                        <div class="col-md-8">
                                            <a href="odetailforcus?id=${o.getOrderID()}" style="text-decoration: none; color: black;">
                                                <div>
                                                    ${od.getProductName()}
                                                </div>
                                                <div>
                                                    Quantity: ${od.getQuantity()}
                                                </div>
                                            </a>
                                        </div>
                                        <div class="col-md-2" style="color: rgb(238,84,61); text-align: right;"><fmt:formatNumber value="${od.getPrice()}" type="currency"/></div>
                                    </div>
                                </c:if>
                            </c:forEach>
                            <br>
                        </div>
                        <div style="text-align: right; padding: 10px; margin-top: 10px; width: 95%; margin: 0 auto">Total: <h4 style="color: rgb(238,84,61);"><fmt:formatNumber value="${o.getTotalAmount()}" type="currency" /></h4>
                        </div>
                    </div>
                    <div style="width: 100%; background: rgba(231, 220, 220, 0);">
                        <br>
                    </div>
                </c:if>
            </c:forEach>
            <%
                if (!hasWaiting) {
            %>
            <div style="background: white; height: 400px; text-align: center; padding: 130px; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                <img style="border-radius: 50%;" width="70px" src="./assets/imgs/icon/no-order.png" alt="alt"/>
                <br>
                <h6 style="padding: 10px">There is no order.</h6>
            </div>
            <%
                }
            %>
        </div>
        <% boolean hasPackaging = false; %>
        <div id="transport" class="order-list">
            <c:forEach items="${sessionScope.orderList}" var="o">
                <c:if test="${o.getStatus() == 2}">
                    <div style="background-color: white; width: 100%; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                        <div style="text-align: right; padding: 15px; border-bottom: 0.5px solid rgb(226, 214, 214); color:rgb(238,84,61); width: 95%; margin: 0 auto">
                            <c:choose>
                                <c:when test="${o.getStatus() == 1}">Waiting For Acceptance</c:when>
                                <c:when test="${o.getStatus() == 2}">Packaging</c:when>
                                <c:when test="${o.getStatus() == 3}">Waiting For Delivery</c:when>
                                <c:when test="${o.getStatus() == 4}">Delivered</c:when>
                                <c:otherwise>Cancelled</c:otherwise>
                            </c:choose>
                        </div>
                        <br>
                        <div class="container-fluid" style="border-bottom: 0.1px rgb(226, 214, 214) solid; width: 95%; margin: 0 auto">
                            <div class="row" style="display: flex; align-items: center;">
                                <c:forEach var="od" items="${sessionScope.orderDetailList}">
                                    <c:if test="${o.getOrderID() == od.getOrderID()}">
                                        <div class="col-md-2">
                                            <img style="border: 0.5px solid gray;" width="100px" height="auto"
                                                 src="${pageContext.request.contextPath}/product-images/${od.getImage()}" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'"
                                                 alt="">
                                        </div>
                                        <div class="col-md-8">
                                            <a href="odetailforcus?id=${o.getOrderID()}" style="text-decoration: none; color: black;">
                                                <div>
                                                    ${od.getProductName()}
                                                </div>
                                                <div>
                                                    Quantity: ${od.getQuantity()}
                                                </div>
                                            </a>
                                        </div>
                                        <div class="col-md-2" style="color: rgb(238,84,61); text-align: right;"><fmt:formatNumber value="${od.getPrice()}" type="currency"/></div>
                                    </div>
                                </c:if>
                            </c:forEach>
                            <br>
                        </div>
                        <div style="text-align: right; padding: 10px; margin-top: 10px; width: 95%; margin: 0 auto">Total: <h4 style="color: rgb(238,84,61);"><fmt:formatNumber value="${o.getTotalAmount()}" type="currency" /></h4>
                        </div>
                    </div>
                    <div style="width: 100%; background: rgba(231, 220, 220, 0);">
                        <br>
                    </div>
                    <% hasPackaging = true; %>
                </c:if>
            </c:forEach>
            <%
                if (!hasPackaging) {
            %>
            <div style="background: white; height: 400px; text-align: center; padding: 130px; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                <img style="border-radius: 50%;" width="70px" src="./assets/imgs/icon/no-order.png" alt="alt"/>
                <br>
                <h6 style="padding: 10px">There is no order.</h6>
            </div>
            <%
                }
            %>
        </div>
        <% boolean hasDelivery = false; %>
        <div id="delivery" class="order-list">
            <c:forEach items="${sessionScope.orderList}" var="o">
                <c:if test="${o.getStatus() == 3}">
                    <% hasDelivery = true; %>
                    <div style="background-color: white; width: 100%; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                        <div style="text-align: right; padding: 15px; border-bottom: 0.5px solid rgb(226, 214, 214); color:rgb(238,84,61); width: 95%; margin: 0 auto">
                            <c:choose>
                                <c:when test="${o.getStatus() == 1}">Waiting For Acceptance</c:when>
                                <c:when test="${o.getStatus() == 2}">Packaging</c:when>
                                <c:when test="${o.getStatus() == 3}">Waiting For Delivery</c:when>
                                <c:when test="${o.getStatus() == 4}">Delivered</c:when>
                                <c:otherwise>Cancelled</c:otherwise>
                            </c:choose>
                        </div>
                        <br>
                        <div class="container-fluid" style="border-bottom: 0.1px rgb(226, 214, 214) solid; width: 95%; margin: 0 auto">
                            <div class="row" style="display: flex; align-items: center;">
                                <c:forEach var="od" items="${sessionScope.orderDetailList}">
                                    <c:if test="${o.getOrderID() == od.getOrderID()}">
                                        <div class="col-md-2">
                                            <img style="border: 0.5px solid gray;" width="100px" height="auto"
                                                 src="${pageContext.request.contextPath}/product-images/${od.getImage()}" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'"
                                                 alt="">
                                        </div>
                                        <div class="col-md-8">
                                            <a href="odetailforcus?id=${o.getOrderID()}" style="text-decoration: none; color: black;">
                                                <div>
                                                    ${od.getProductName()}
                                                </div>
                                                <div>
                                                    Quantity: ${od.getQuantity()}
                                                </div>
                                            </a>
                                        </div>
                                        <div class="col-md-2" style="color: rgb(238,84,61); text-align: right;"><fmt:formatNumber value="${od.getPrice()}" type="currency"/></div>
                                    </div>
                                </c:if>
                            </c:forEach>
                            <br>
                        </div>
                        <div style="text-align: right; padding: 10px; margin-top: 10px; width: 95%; margin: 0 auto">Total: <h4 style="color: rgb(238,84,61);"><fmt:formatNumber value="${o.getTotalAmount()}" type="currency" /></h4>
                        </div>
                    </div>
                    <div style="width: 100%; background: rgba(231, 220, 220, 0);">
                        <br>
                    </div>
                </c:if>
            </c:forEach>
            <%
                if (!hasDelivery) {
            %>
            <div style="background: white; height: 400px; text-align: center; padding: 130px; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                <img style="border-radius: 50%;" width="70px" src="./assets/imgs/icon/no-order.png" alt="alt"/>
                <br>
                <h6 style="padding: 10px">There is no order.</h6>
            </div>
            <%
                }
            %>
        </div>
        <% boolean hasDelivered = false; %>
        <div id="delivered" class="order-list">
            <c:forEach items="${sessionScope.orderList}" var="o">
                <c:if test="${o.getStatus() == 4}">
                    <%  hasDelivered = true; %>
                    <div style="background-color: white; width: 100%; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                        <div style="text-align: right; padding: 15px; border-bottom: 0.5px solid rgb(226, 214, 214); color:rgb(238,84,61); width: 95%; margin: 0 auto">
                            <c:choose>
                                <c:when test="${o.getStatus() == 1}">Waiting For Acceptance</c:when>
                                <c:when test="${o.getStatus() == 2}">Packaging</c:when>
                                <c:when test="${o.getStatus() == 3}">Waiting For Delivery</c:when>
                                <c:when test="${o.getStatus() == 4}">Delivered</c:when>
                                <c:otherwise>Cancelled</c:otherwise>
                            </c:choose>
                        </div>
                        <br>
                        <div class="container-fluid" style="border-bottom: 0.1px rgb(226, 214, 214) solid; width: 95%; margin: 0 auto">
                            <div class="row" style="display: flex; align-items: center;">
                                <c:forEach var="od" items="${sessionScope.orderDetailList}">
                                    <c:if test="${o.getOrderID() == od.getOrderID()}">
                                        <div class="col-md-2">
                                            <img style="border: 0.5px solid gray;" width="100px" height="auto"
                                                 src="${pageContext.request.contextPath}/product-images/${od.getImage()}" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'"
                                                 alt="">
                                        </div>
                                        <div class="col-md-8">
                                            <a href="odetailforcus?id=${o.getOrderID()}" style="text-decoration: none; color: black;">
                                                <div>
                                                    ${od.getProductName()}
                                                </div>
                                                <div>
                                                    Quantity: ${od.getQuantity()}
                                                </div>
                                            </a>
                                        </div>
                                        <div class="col-md-2" style="color: rgb(238,84,61); text-align: right;"><fmt:formatNumber value="${od.getPrice()}" type="currency"/></div>
                                    </div>
                                </c:if>
                            </c:forEach>
                            <br>
                        </div>
                        <div style="text-align: right; padding: 10px; margin-top: 10px; width: 95%; margin: 0 auto">Total: <h4 style="color: rgb(238,84,61);"><fmt:formatNumber value="${o.getTotalAmount()}" type="currency" /></h4>
                        </div>
                    </div>
                    <div style="width: 100%; background: rgba(231, 220, 220, 0);">
                        <br>
                    </div>
                </c:if>
            </c:forEach>
            <%
                if (!hasDelivered) {
            %>
            <div style="background: white; height: 400px; text-align: center; padding: 130px; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                <img style="border-radius: 50%;" width="70px" src="./assets/imgs/icon/no-order.png" alt="alt"/>
                <br>
                <h6 style="padding: 10px">There is no order.</h6>
            </div>
            <%
                }
            %>
        </div>
        <%
            boolean hasCancelOrder = false;
        %>
        <div id="canceled" class="order-list">
            <c:forEach items="${sessionScope.orderList}" var="o">
                <c:if test="${o.getStatus() == 5}">
                    <div style="background-color: white; width: 100%;box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                        <div style="text-align: right; padding: 15px; border-bottom: 0.5px solid rgb(226, 214, 214); color:rgb(238,84,61); width: 95%; margin: 0 auto">
                            <c:choose>
                                <c:when test="${o.getStatus() == 1}">Waiting For Acceptance</c:when>
                                <c:when test="${o.getStatus() == 2}">Packaging</c:when>
                                <c:when test="${o.getStatus() == 3}">Waiting For Delivery</c:when>
                                <c:when test="${o.getStatus() == 4}">Delivered</c:when>
                                <c:otherwise>Cancelled</c:otherwise>
                            </c:choose>
                        </div>
                        <br>
                        <div class="container-fluid" style="border-bottom: 0.1px rgb(226, 214, 214) solid; width: 95%; margin: 0 auto">
                            <div class="row" style="display: flex; align-items: center;">
                                <c:forEach var="od" items="${sessionScope.orderDetailList}">
                                    <c:if test="${o.getOrderID() == od.getOrderID()}">
                                        <div class="col-md-2">
                                            <img style="border: 0.5px solid gray;" width="100px" height="auto"
                                                 src="${pageContext.request.contextPath}/product-images/${od.getImage()}" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'"
                                                 alt="">
                                        </div>
                                        <div class="col-md-8">
                                            <a href="odetailforcus?id=${o.getOrderID()}" style="text-decoration: none; color: black;">
                                                <div>
                                                    ${od.getProductName()}
                                                </div>
                                                <div>
                                                    Quantity: ${od.getQuantity()}
                                                </div>
                                            </a>
                                        </div>
                                        <div class="col-md-2" style="color: rgb(238,84,61); text-align: right;"><fmt:formatNumber value="${od.getPrice()}" type="currency"/></div>
                                    </div>
                                </c:if>
                            </c:forEach>
                            <br>
                        </div>
                        <div style="text-align: right; padding: 10px; margin-top: 10px; width: 95%; margin: 0 auto">Total: <h4 style="color: rgb(238,84,61);"><fmt:formatNumber value="${o.getTotalAmount()}" type="currency" /></h4>
                        </div>
                    </div>
                    <div style="width: 100%; background: rgba(231, 220, 220, 0);">
                        <br>
                    </div>
                    <% hasCancelOrder = true;%>
                </c:if>
            </c:forEach>
            <%
                if (!hasCancelOrder) {
            %>
            <div style="background: white; height: 400px; text-align: center; padding: 130px; box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ;">
                <img style="border-radius: 50%;" width="70px" src="./assets/imgs/icon/no-order.png" alt="alt"/>
                <br>
                <h6 style="padding: 10px">There is no order.</h6>
            </div>
            <%
                }
            %>
        </div>
        <div style="width: 100%; background: rgba(231, 220, 220, 0);">
            <br>
        </div>
    </div>

    <script src="./assets/js/order.js"></script>
</body>
</html>

