
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<%@page import="DAOs.CartDAO" %>
<%@page import="Models.Cart"%>
<!DOCTYPE html>
<html lang="en">


    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
              crossorigin="anonymous">
        <link rel="stylesheet" href="./assets/css/cart.css">
        <link rel="stylesheet" href="./assets/css/popup.css">
        <title>Shopping Cart</title>
    </head>

    <body>
        <jsp:include page="header.jsp"></jsp:include>
            <main>
                <div class="container">
                    <div class="row">
                        <p><a style=" text-decoration: none;
                              color: black;" href="ProductListView">Home</a> › Login</p>
                        <h2>Shopping Cart</h2>
                    <c:set var="total" value="0" />
                    <c:if test="${sessionScope.customer != null}">
                        <c:if test="${cartList != null && !cartList.isEmpty()}">
                            <div class="col-md-8">
                                <table style="width: 100%; border-collapse: collapse; ">
                                    <tr style="height: 60px;">
                                        <th class="th" style="width: 3%;">
                                            <input type="checkbox" id="selectAll" onclick="toggleAll(this)">
                                        </th>
                                        <th class="th" style="width: 17%;">Item</th>
                                        <th class="th" style="width: 34%;"></th>
                                        <th class="th" style="width: 13%;">Price</th>
                                        <th class="th" style="width: 12%;">Qty</th>
                                        <th class="th" style="width: 16%;">Subtotal</th>
                                        <th class="th" style="width: 2%;"></th>
                                    </tr>
                                    <form id="cartSelected" action="order" method="post">
                                        <input type="text" name="orderUrl" value="Cart" hidden>
                                        <c:forEach items="${sessionScope.cartList}" var="p">
                                            <c:if test="${p.getQuantity() > 0}">
                                                <tr>
                                                    <td>
                                                        <input type="checkbox" name="cartSelected" class="cartSelected" value="${p.getProductID()}">
                                                    </td>
                                                    <td class="th"><img
                                                            src="./assets/imgs/Products/${p.getImage()}"
                                                            alt="" width="90px">
                                                    </td>
                                                    <td class="th"><a style="text-decoration: none; color: black;" href="ProductDetailServlet?id=${p.getProductID()}">${p.getFullName()}</a></td>
                                                    <td class="th">
                                                        <h6>
                                                            <fmt:formatNumber value="${p.getPrice()}" type="currency" />
                                                        </h6>
                                                    </td>
                                                    <td class="th">
                                                        <input 
                                                            style="width: 60%; height: 40px; padding-left: 10px; font-weight: bold; background-color: #f5f7ff; border: #f5f7ff solid 1px;"
                                                             type="number"
                                                             min="1"
                                                             max="${p.getStock()}"
                                                             title="Available stock: ${p.getStock()}"
                                                             value="${p.getQuantity()}"
                                                            name="quantity" 
                                                            id="quantity-${p.getProductID()}" 
                                                            onchange="updateQuantity(${p.getProductID()}, this.value)">
                                                    </td>
                                                    <td class="th">
                                                        <p id="price-${p.getProductID()}" hidden><fmt:formatNumber value="${p.getPrice()}" type="currency" /></p>
                                                        <h6 id="price-${p.getProductID()}">
                                                            <fmt:formatNumber value="${p.getPrice() * p.getQuantity()}" type="currency" />
                                                        </h6>
                                                    </td>
                                                    <td class="th">
                                                        <a href="deletePOC?id=${p.getProductID()}"><img src="./assets/imgs/ShoppingCartImg/x.jpg" alt=""
                                                                                                        width="25px" ></a>
                                                        <!--                                                        <a href=""><img src="./assets/imgs/ShoppingCartImg/pen.jpg" alt="" width="25px"
                                                                                                                                style="margin-top: 5px;"></a>-->
                                                    </td>
                                                </tr>
                                            </c:if>

                                            <c:set var="total" value="${total + (p.getPrice() * p.getQuantity())}" />
                                        </c:forEach>
                                        <c:forEach items="${sessionScope.cartList}" var="p">
                                            <c:if test="${p.getQuantity() == 0}">
                                                <tr>
                                                    <td>
                                                        <input disabled type="checkbox" name="cartSelected" value="${p.getProductID()}">
                                                    </td>
                                                    <td class="th"><img
                                                            src="./assets/imgs/Products/${p.getImage()}"
                                                            alt="" width="90px"></td>
                                                    <td class="th"><a style="text-decoration: none; color: black;" href="ProductDetailServlet?id=${p.getProductID()}">${p.getFullName()}</a></td>
                                                    <td class="th">
                                                        <h6>
                                                            <fmt:formatNumber value="${p.getPrice()}" type="currency" />
                                                        </h6>
                                                    </td>
                                                    <td class="th">
                                                        <input 
                                                            style="width: 60%; height: 40px; padding-left: 10px; font-weight: bold; background-color: #f5f7ff; border: #f5f7ff solid 1px;"
                                                            type="number" 
                                                            min="1" 
                                                            value="${p.getQuantity()}" 
                                                            name="quantity" disabled>
                                                    </td>
                                                    <td class="th">
                                                        <h6>
                                                            Sold out
                                                        </h6>
                                                    </td>
                                                    <td class="th">
                                                        <a href="deletePOC?id=${p.getProductID()}"><img src="./assets/imgs/ShoppingCartImg/x.jpg" alt=""
                                                                                                        width="25px" ></a>
                                                        <!--                                                        <a href=""><img src="./assets/imgs/ShoppingCartImg/pen.jpg" alt="" width="25px"
                                                                                                                                style="margin-top: 5px;"></a>-->
                                                    </td>
                                                </tr>
                                            </c:if>
                                        </c:forEach>
                                        <input name="buyProductAction" value="checkout" hidden="">         
                                    </form>
                                </table>
                                <br>
                                <div class="btnControl">
                                    <!--                            <div>
                                                                    <input type="submit" name="" id="" value="Continue Shopping"
                                                                           style="height:35px;width: 200px; border-radius: 20px; color: gray; background-color: white;">
                                                                    <button>Clear Shopping Cart</button>
                                                                </div>-->
                                </div>
                            </div>
                            <div class="col-md-4 right" style="background: white;">
                                <div style="background: #f5f7ff; padding: 20px 10px 10px 10px;" >
                                    <h3>Summary</h3>
                                    <div>

                                    </div>


                                    <svg width="385" height="2" viewBox="0 0 385 2" fill="none"
                                         xmlns="http://www.w3.org/2000/svg">
                                    <path d="M0 1.00003L385 0.999997" stroke="#CACDD8" />
                                    </svg>
                                    <div >
                                        <div class="totalPrice">
                                            <p>Subtotal</p>
                                            <p><span id="subTotal">0 VND</span></p>
                                        </div>
                                        <!--                            <div class="totalPrice">
                                                                        <p>Shipping</p>
                                                                        <p><fmt:formatNumber value="30000" type="currency" /></p>
                                                                    </div>-->
                                        <div class="totalPrice">
                                            <p>Order Total</p>
                                            <h4><span id="totalAmount">0 VND</span></h4>
                                        </div>
                                    </div>
                                    <div class="btnSummary">
                                        <c:if test="${cartList.isEmpty()}">
                                            <button form="cartSelected" type="submit"
                                                    style="background-color: #0156ff; border: #0156ff solid 1px; color: white;"
                                                    id="checkout" disabled>Proceed to Checkout
                                            </button>
                                        </c:if>
                                        <c:if test="${!cartList.isEmpty()}"> 
                                            <button form="cartSelected" type="submit"
                                                    style="background-color: #0156ff; border: #0156ff solid 1px; color: white;"
                                                    id="checkout">Proceed to Checkout
                                            </button>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${cartList == null || cartList.isEmpty()}">
                            <c:set var="total" value="0" />
                            <br>
                            <div style="padding: 100px; column-gap: 20px; text-align: center">
                                <img  width="100px" src="./assets/imgs/icon/empty-cart.png" alt="alt"/>
                                <h4 style="margin-top: 20px"> 
                                    No products on cart. Countinue to shopping
                                </h4>
                            </div>
                        </c:if>
                    </c:if>
                </div>
                <br>
                <br>
            </div>
              
            <div style="background-color: #f5f7ff;">
                <div class="container">
                    <div class="row" style="text-align: center; padding: 30px; background-color: #f5f7ff;">
                        <div class="col-md-4">
                            <img src="${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg" alt="" width="70px">
                            <h5>Product Support</h5>
                            <p style="width: 70%; margin: 0 auto;">Up to 3 years on-site warranty available for your
                                peace
                                of mind.</p>
                        </div>
                        <div class="col-md-4">
                            <img src="./assets/imgs/ShoppingCartImg/person.jpg" alt="" width="70px">
                            <h5>Personal Account</h5>
                            <p style="width: 70%; margin: 0 auto;">With big discounts, free delivery and a dedicated
                                support
                                specialist.</p>
                        </div>
                        <div class="col-md-4">
                            <img src="./assets/imgs/ShoppingCartImg/tag.jpg" alt="" width="70px">
                            <h5>Amazing Savings</h5>
                            <p style="width: 70%; margin: 0 auto;">Up to 70% off new Products, you can be sure of
                                the best
                                price.</p>
                        </div>
                    </div>
                </div>
            </div>

            <div style="display: none;" class="popup" id="orderPopup">
                <div class="popup-content">
                    <h4 id="message-content">${requestScope.message}Please choose at least one product to checkout.</h4>
                    <div style="display: flex; justify-content: center;">
                        <button onclick="closePopup()">OK</button>
                    </div>
                </div>
            </div>

            <!-- Bootstrap Modal -->
            <div class="modal fade" id="updatePopup" tabindex="-1" aria-labelledby="updatePopupLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <c:choose>
                                <c:when test="${sessionScope.message.contains('add your address') ||
                                                sessionScope.message.contains('add your phone number') ||
                                                sessionScope.message.contains('Go to your cart')}">
                                        <h5 class="modal-title text-danger">Notification</h5>
                                </c:when>
                                <c:otherwise>
                                    <h5 class="modal-title text-danger">Warning</h5>
                                </c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body text-dark">
                            <c:choose>
                                <c:when test="${sessionScope.message.contains('total amount too big')}">
                                    <p> You can buy product online with the total amount <br> under <b>100,000,000 VND</b>! 
                                        <br>
                                        Or if you want to buy, contact us at: 
                                        <a href="mailto:kieuthy@gmail.com" class="text-primary">kieuthy@gmail.com</a>
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <p>${sessionScope.message}</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="modal-footer">
                            <c:choose>
                                <c:when test="${sessionScope.message.contains('add your address')}">
                                    <a class="btn btn-primary text-white" href="ViewShippingAddress">OK</a>
                                    <a class="btn btn-secondary text-white" onclick='closeMessagePopup()'>Cancel</a>
                                </c:when>
                                <c:when test="${sessionScope.message.contains('add your phone number')}">
                                    <a class="btn btn-primary text-white" href="viewCustomerProfile">OK</a>
                                    <a class="btn  btn-secondary text-white" onclick='closeMessagePopup()'>Cancel</a>
                                </c:when>
                                <c:when test="${sessionScope.message.contains('Go to your cart')}">
                                    <a class="btn btn-primary text-white" href="cart">OK</a>
                                    <a class="btn btn-secondary text-white" onclick='closeMessagePopup()'>Cancel</a>
                                </c:when>
                                <c:otherwise>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">OK</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>


        </main>
        <jsp:include page="footer.jsp"></jsp:include>
            <script>
                window.onload = function () {
            <% if (session.getAttribute("message") != null) { %>
                    showMessage(true);
            <% }%>
                };
                function toggleAll(source) {
                    let checkboxes = document.querySelectorAll('.cartSelected');
                    checkboxes.forEach(checkbox => {
                        checkbox.checked = source.checked;
                    });
                    updateTotal();
                }
                function showMessage(show) {
                    if (show) {
                        var warningModal = new bootstrap.Modal(document.getElementById('updatePopup'));
                        warningModal.show();
                    }
                }
                function closeMessagePopup() {
                    var warningModal = bootstrap.Modal.getInstance(document.getElementById('updatePopup'));
                    if (warningModal) {
                        warningModal.hide();
                    }
                }

                function formatCurrency(amount) {
                    return new Intl.NumberFormat('vi-VN').format(amount) + ' VND';
                }


                function updateTotal() {
                    let total = 0;
                    document.querySelectorAll("input[name='cartSelected']:checked").forEach((checkbox) => {
                        let productId = checkbox.value;
                        let price = parseFloat(document.getElementById("price-" + productId).innerText.replace(/\D/g, ''));
                        let quantity = parseInt(document.getElementById("quantity-" + productId).value);
                        total += price * quantity;
                    });
                    document.getElementById("totalAmount").innerText = formatCurrency(total);
                    document.getElementById("subTotal").innerText = formatCurrency(total);
                }

                document.querySelectorAll("input[name='cartSelected']").forEach((checkbox) => {
                    checkbox.addEventListener("change", updateTotal);
                });

                document.querySelectorAll("input[name='quantity']").forEach((input) => {
                    input.addEventListener("change", updateTotal);
                });

                function showPopup() {
                    document.getElementById("orderPopup").style.display = "flex";
                }

                function closePopup() {
                    document.getElementById("orderPopup").style.display = "none";
                }
                function updateQuantity(productId, quantity) {
                    // Gửi dữ liệu tới Servlet qua AJAX
                    fetch('updateCart', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            productId: productId,
                            quantity: quantity
                        })
                    })
                            .then(response => response.text())
                            .then(data => {
                                console.log(data); // Kiểm tra phản hồi từ server
                                window.location.reload(); // Reload lại trang
                                //alert("Quantity updated successfully!");
                            })
                            .catch(error => {
                                console.error("Error updating quantity:", error);
                                //alert("Failed to update quantity. Please try again.");
                            });
                }

                document.getElementById("checkout").addEventListener("click", function () {
                    // Lấy tất cả các checkbox trong form
                    const checkboxes = document.querySelectorAll("input[name='cartSelected']");
                    let isChecked = false;

                    // Kiểm tra xem có ít nhất một checkbox được chọn không
                    checkboxes.forEach(checkbox => {
                        if (checkbox.checked) {
                            isChecked = true;
                        }
                    });

                    if (!isChecked) {
                        // Showing thông báo nếu chưa chọn checkbox nào
                        showPopup();
                    } else {
                        // Gửi form nếu có checkbox được chọn
                        document.getElementById("cartSelected").submit();
                    }
                });


                function toggleDisplay(divId) {
                    var x = document.getElementById(divId);
                    if (x.style.display === "none") {
                        x.style.display = "block";
                    } else {
                        x.style.display = "none";
                    }
                }

        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
    </body>
    <c:if test="${not empty sessionScope.message}">
        <c:remove var="message" scope="session"/>
    </c:if>
</html>
