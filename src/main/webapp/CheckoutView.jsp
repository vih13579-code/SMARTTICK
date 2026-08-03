

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="Models.Cart"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
              integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="./assets/css/checkout.css">
        <title>Checkout</title>
    </head>

    <body>
        <jsp:include page="header.jsp"></jsp:include>
            <main>
                <div class="container">
                    <p><a style=" text-decoration: none;
                          color: black;" href="HomeServlet">Home</a> › 
                        <a style=" text-decoration: none;
                           color: black;" href="shoppingCart.jsp">Shopping Cart</a> › 
                        Checkout Process</p>
                    <div class="row">
                        <div class="col-md-8">
                            <div class="title">
                                <div>
                                    <h1>Checkout</h1>
                                </div>
                                <!--                                <div><button type="submit"
                                                                             style="background-color: white; border: #0156ff solid 1px; color: #0156ff;"><b>Sign
                                                                            in</b></button></div>-->
                            </div>
                        </div>
                        <div class="col-md-4" style="text-align: right;">
                            <img src="./assets/imgs/CheckoutImg/status.jpg" alt="" width="350px">
                        </div>
                    </div>
                    <br>
                    <br>
                    <div class="row">
                        <div class="col-md-12" style="text-align: left;">
                            <h5>Shipping Address</h5>
                            <svg width="66%" height="2" viewBox="0 0 66% 2" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M0 1L924 1.00008" stroke="#CACDD8" />
                            </svg>
                        </div>

                    </div>
                    <div class="row">
                        <div class="col-md-8">
                            <form action="order" method="post" class="infor">
                                <div class="form-group">
                                    <div class="control-label">Fullname</div>
                                    <div class="">
                                        <input style="width: 70%; border: black solid 1px;" name="fullname" type="text" class="form-control" value="${sessionScope.customer.getFullName()}" placeholder="" readonly/> </div>

                            </div>

                            <div class="form-group">
                                <div class="control-label">Phone Number *</div>
                                <div class="">
                                    <input style="width: 70%; border: black solid 1px;" name="phone" type="text" class="form-control" value="${sessionScope.customer.getPhoneNumber()}" placeholder="" readonly/>
                                </div>
                            </div>
                            <div>
                                <div class="control-label">Address</div>
                                <div class="">
                                    <textarea rows="2" cols="50" style="
                                              width: 70%; border: black solid 1px;" name="address" type="text" class="form-control" value="" placeholder="" readonly>${shipAddress}</textarea>
                                </div>
                                <a href="ViewShippingAddress?action=forOrder">Change address</a>
                            </div>

                            <!--                            <div class="form-group">
                                                            <div class="">
                                                                <div class="control-label">Standard Rate</div>
                                                                <div>
                                                                    <input type="radio" id="check" name="way" value="ship" required> 
                                                                    <span style="display: flex; justify-content: space-between;">
                                                                        <p>Price may vary
                                                                            depending on
                                                                            the
                                                                            item/destination. Shop Staff will contact you.
                                                                        </p>
                                                                        <p><b><fmt:formatNumber value="30000" type="currency"/></b></p>
                                                                    </span>
                                                                </div>
                                                                <div class="control-label">Pickup from store</div>
                                                                <input type="radio" id="check" name="way" value="pick" required> <span style="display: flex; justify-content: space-between;">
                                                                    <p> 305 La Trobe St
                                                                        Melbourne
                                                                        3000</p>
                                                                    <p><b><fmt:formatNumber value="0" type="currency"/></b></p></span>
                                                            </div>
                                                        </div>-->
                            <input type="hidden" name="buyProductAction" value="confirm">

                            <button type="submit"
                                    style="background-color: #0156ff; border: #0156ff solid 1px; color: white;" value="Next">
                                Next
                            </button>
                        </form>
                    </div>
                    <div class="col-md-4" style="background-color: #f5f7ff; padding: 20px; margin-top: -10px; height: 80%;">
                        <h4>Order Summary</h4>
                        <svg width="100%" height="2" viewBox="0 0 100% 2" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M0 1L385 0.999966" stroke="#CACDD8" />
                        </svg>

                        <p> ${numOfItems} Items </p>
                        <c:forEach items="${sessionScope.cartSelected}" var="p">
                            <div style="display: flex; column-gap: 20px;">
                                <div><img
                                        src="./assets/imgs/Products/${p.getImage()}"
                                        alt="" width="70px"></div>
                                <div>
                                    <div>
                                        <p>${p.getFullName()}</p>
                                    </div>
                                    <div style="display: flex; column-gap: 10px; margin-top: -15px;">
                                        <div>Qty ${p.getQuantity()}</div>
                                        <div><b><fmt:formatNumber value="${p.getPrice() * p.getQuantity()}" type="currency"/></b></div>
                                    </div>
                                </div>
                            </div>
                            <br>
                        </c:forEach>
                        <h4>Total: <fmt:formatNumber value="${totalAmount}" type="currency" /></h4>
                    </div>
                </div>
            </div>
            <br>

        </main>
        <jsp:include page="footer.jsp"></jsp:include>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/axios/0.21.1/axios.min.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
    </body>

</html>
