

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="Models.Cart" %>
<%@page import="Models.Customer" %>
<%@page import="java.util.List" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
              rel="stylesheet"
              integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
              crossorigin="anonymous">
        <link rel="stylesheet" href="./assets/css/checkout.css">
        <link rel="stylesheet" href="./assets/css/popup.css" />
        <title>Checkout</title>
        <style>
            .popup-overlay {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.5);
                z-index: 1000;
            }




        </style>
    </head>

    <body>
        <jsp:include page="header.jsp"></jsp:include>
            <main>
                <div class="container">
                    <p><a style=" text-decoration: none;
                          color: black;" href="HomeServlet">Home</a> ›
                        <a style=" text-decoration: none;
                           color: black;" href="shoppingCart.jsp">Shopping Cart</a> ›
                        Checkout Process
                    </p>
                    <div class="row">
                        <div class="col-md-8">
                            <div class="title">
                                <div>
                                    <h1>Confirmation</h1>
                                </div>

                            </div>
                        </div>
                        <div class="col-md-4" style="text-align: right;">
                            <img src="./assets/imgs/CheckoutImg/status2.jpg" alt="" width="350px">
                        </div>
                    </div>
                    <br>
                    <br>
                    <div class="row">
                        <div class="col-md-12" style="text-align: left;">
                            <h5>Shipping Address</h5>
                            <svg width="66%" height="2" viewBox="0 0 66% 2" fill="none"
                                 xmlns="http://www.w3.org/2000/svg">
                            <path d="M0 1L924 1.00008" stroke="#CACDD8" />
                            </svg>
                        </div>

                    </div>
                    <div class="row ">
                        <div class="col-md-8">
                            <table style="width: 100%;">
                                <tr style="padding: 20px 20px 20px 0px;">
                                    <th style="width: 30%;"></th>
                                    <th style="width: 70%;"></th>
                                </tr>
                                <tr style="padding: 20px 20px 20px 0px;">
                                    <td style="padding: 20px 20px 20px 0px;"><b>Email:</b></td>
                                    <td style="padding: 20px 20px 20px 0px;">
                                    ${sessionScope.customer.getEmail()}</td>
                            </tr>
                            <tr style="padding: 20px 20px 20px 0px;">
                                <td style="padding: 20px 20px 20px 0px;"><b>Fullname</b></td>
                                <td style="padding: 20px 20px 20px 0px;">
                                    ${sessionScope.order.getFullName()}</td>
                            </tr>
                            <tr style="padding: 20px 20px 20px 0px;">
                                <td style="padding: 20px 20px 20px 0px;"><b>Phone number</b>
                                </td>
                                <td style="padding: 20px 20px 20px 0px;">
                                    ${sessionScope.order.getPhone()}</td>
                            </tr>
                            <tr style="padding: 20px 20px 20px 0px;">
                                <td style="padding: 20px 20px 20px 0px;"><b>Address</b></td>
                                <td style="padding: 20px 20px 20px 0px;">
                                    ${sessionScope.order.getAddress()}</td>
                            </tr>
                        </table>
                    </div>
                    <div class="col-md-4"
                         style="background-color: #f5f7ff; padding: 20px; margin-top: -10px; height: 80%;">
                        <div> 
                            <h4>Order Summary</h4>
                            <svg width="100%" height="2" viewBox="0 0 100% 2" fill="none"
                                 xmlns="http://www.w3.org/2000/svg">
                            <path d="M0 1L924 1.00008" stroke="#CACDD8" />
                            <c:set var="subtotal" value="0"></c:set>
                            <c:forEach items="${sessionScope.cartSelected}" var="p">
                                <div style="display: flex; column-gap: 20px;">
                                    <div><img src="./assets/imgs/Products//${p.getImage()}" alt=""
                                              width="70px"></div>
                                    <div>
                                        <div>
                                            <p>${p.getFullName()}</p>
                                        </div>
                                        <div
                                            style="display: flex; column-gap: 10px; margin-top: -15px;">
                                            <div>Qty ${p.getQuantity()}</div>
                                            <div><b>
                                                    <fmt:formatNumber
                                                        value="${p.getPrice() * p.getQuantity()}"
                                                        type="currency" />
                                                </b></div>
                                        </div>
                                    </div>
                                </div>
                                <br>
                                <c:set var="subtotal" value="${subtotal + (p.getPrice() * p.getQuantity())}"></c:set>
                            </c:forEach>
                        </div>
                        <svg width="100%" height="2" viewBox="0 0 100% 2" fill="none"
                             xmlns="http://www.w3.org/2000/svg">
                        <path d="M0 1L924 1.00008" stroke="#CACDD8" />
                        </svg>
                        <br>
                        <div
                            style="display: flex; justify-content: space-between; align-items: center; margin-top: 10px">


                            <c:if test="${sessionScope.customerVoucherUsing != null}">
                                <h6><img src="./assets/imgs/icon/coupon.png" alt="alt" width="20" height="20" style="margin-top: -5px"/>
                                    Your Voucher
                                </h6>
                                <div style="display: flex; align-items: center; column-gap: 10px">
                                    <a onclick="openVoucherModal()" style="color: blue; margin-top: -4px; cursor: pointer">${sessionScope.customerVoucherUsing.getVoucherCode()}</a>
                                    <a class="btn btn-close" href="order?action=cancelVoucher" ></a>
                                </div>
                            </c:if>
                            <c:if test="${sessionScope.customerVoucherUsing == null}">
                                <h6><img src="./assets/imgs/icon/coupon.png" alt="alt" width="20" height="20" style="margin-top: -5px"/>
                                    Apply Voucher(if have)
                                </h6>
                                <a onclick="openVoucherModal()"
                                   style="color: blue; margin-top: -6px; cursor: pointer">
                                    Select Voucher
                                </a>
                            </c:if>


                        </div>
                        <svg width="100%" height="2" viewBox="0 0 100% 2" fill="none"
                             xmlns="http://www.w3.org/2000/svg">
                        <path d="M0 1L924 1.00008" stroke="#CACDD8" />
                        </svg>
                        <div> <p id="selectedVoucher" style="margin-top: 10px;"></p>
                        </div>
                        <div style="display: flex; justify-content: space-between;">
                            <div style="text-align: left">
                                <h6 >Subtotal</h6>
                                <h6 >Discount</h6>
                                <h4 >Total</h4>
                            </div>
                            <div>
                                <h6 style="text-align: right;">
                                    <fmt:formatNumber value="${subtotal}" type="currency" />
                                </h6>
                                <h6 style="text-align: right;">
                                    <c:if test="${sessionScope.discount != null}">
                                        <c:set var="discount" value="${sessionScope.discount}" />
                                        <fmt:formatNumber value="${discount}" type="currency" />
                                    </c:if>
                                    <c:if test="${sessionScope.discount == null}">
                                        <fmt:formatNumber value="0" type="currency" />
                                    </c:if>
                                </h6>
                                <h4 style="text-align: right;">
                                    <c:if test="${totalAmount - discount <= 0}">
                                        <c:set var="total" value="0" />
                                        <fmt:formatNumber value="${total}" type="currency" />
                                    </c:if>
                                    <c:if test="${totalAmount - discount > 0}">
                                        <c:set var="total" value="${totalAmount - discount}" />
                                        <fmt:formatNumber value="${total}" type="currency" />
                                    </c:if>
                                </h4>
                            </div>

                        </div>

                    </div>

                </div>

            </div>
            <br>

            <div class="container">
                <form id="placeOrderForm" style="text-align: right" class="infor" action="order" method="POST">
                    <input type="number" name="totalAmount" value="${total}" hidden>
                    <input id="paymentToken" name="paymentToken" type="hidden" value="">
                    <button id="startPaymentButton" type="button" onclick="startDemoPayment()"
                            style="background-color: #0156ff; border: #0156ff solid 1px; color: white;">
                        Pay by demo QR
                    </button>
                    <input name="buyProductAction" value="placeOrder" hidden="">
                </form>
            </div>
            <br>
            <div id="demoPaymentOverlay" style="display:none;position:fixed;inset:0;background:rgba(3,10,20,.82);z-index:2000;align-items:center;justify-content:center;padding:20px;">
                <div style="width:min(94vw,520px);background:#fff;border-radius:22px;padding:28px;text-align:center;box-shadow:0 24px 70px rgba(0,0,0,.35);position:relative;">
                    <button type="button" onclick="closeDemoPayment()" aria-label="Close"
                            style="position:absolute;right:16px;top:12px;border:0;background:transparent;font-size:28px;color:#687386;">&times;</button>
                    <div style="font-size:12px;font-weight:700;letter-spacing:1.4px;color:#b07816;">SMARTTICK DEMO PAYMENT</div>
                    <h3 style="margin:8px 0 4px;color:#101828;">Scan to confirm payment</h3>
                    <p style="margin:0 0 14px;color:#667085;">No real money will be transferred</p>
                    <img id="demoPaymentQr" alt="Demo payment QR" width="300" height="300"
                         style="max-width:82vw;border:10px solid #fff;border-radius:14px;box-shadow:0 4px 22px rgba(0,0,0,.12);">
                    <h4 style="margin:12px 0 4px;color:#101828;"><fmt:formatNumber value="${total}" type="currency" /></h4>
                    <div id="demoPaymentStatus" style="font-weight:700;color:#b07816;margin-top:10px;">Creating secure demo QR...</div>
                    <p style="font-size:13px;color:#667085;margin:10px 0 0;">Use a phone on the same Wi-Fi. The order will be created automatically after scanning.</p>
                    <details style="margin-top:10px;text-align:left;font-size:12px;color:#667085;">
                        <summary>QR callback address</summary>
                        <div id="demoPaymentAddress" style="overflow-wrap:anywhere;margin-top:6px;"></div>
                    </details>
                </div>
            </div>
            <div style="display: none;" class="popup" id="orderPopup">
                <div class="popup-content">
                    <img src="https://cdn-icons-png.flaticon.com/512/845/845646.png" alt="success-tick" style="width: 82px; margin-bottom: 15px;" />
                    <h3>Order Successful</h3>
                    <p>Your order is waiting for acceptance by the shop.</p>
                    <div style="display: flex; justify-content: center; column-gap: 10px">
                        <a  class="btn btn-primary"
                            href="odetailforcus?id=${sessionScope.newOrder}">OK</a>
                        <a class="btn btn-secondary"
                           href="ProductListView">Back to home</a>
                    </div>
                </div>
            </div>
            <!-- Bootstrap Popup -->
            <div class="modal fade" id="voucherModal" tabindex="-1" aria-labelledby="voucherModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="voucherModalLabel">Select Your Voucher</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <c:if test="${empty sessionScope.customerVoucher}">
                                <p>No vouchers available.</p>
                            </c:if>
                            <c:forEach var="vou" items="${sessionScope.customerVoucher}">
                                <div class="voucher border p-3 mb-3 d-flex justify-content-between position-relative" style="border-radius: 12px;">
                                    <!-- Quantity Tag -->
                                    <div class="position-absolute top-0 start-0 bg-danger text-white px-2 py-1" style="border-top-left-radius: 12px;">
                                        X${vou.getQuantity()}
                                    </div>
                                    <div class="flex-grow-1 " style="padding-left: 30px">
                                        <div class="right fw-bold text-danger" style="font-size: 16px;">
                                            <c:choose>
                                                <c:when test="${vou.getVoucherType() == 0}">
                                                    <fmt:formatNumber type="currency" value="${vou.getVoucherValue()}"/> Sale Off
                                                </c:when>
                                                <c:otherwise>
                                                    ${vou.getVoucherValue()}% Sale Off
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="terms mt-3">
                                            <p class="text-muted" style="font-size: 14px;">${vou.getDescription()}</p>
                                        </div>
                                        <c:if test="${vou.getMaxUsedCount() != 0}">
                                            <div style="width: 80%;">
                                                <div class="progress mt-2" style="height: 12px;">
                                                    <div class="progress-bar bg-success" role="progressbar"
                                                         style="width: ${vou.getUsedCount() * 100 / vou.getMaxUsedCount()}%;"
                                                         aria-valuenow="${vou.getUsedCount()}" 
                                                         aria-valuemin="0" 
                                                         aria-valuemax="${vou.getMaxUsedCount()}">
                                                    </div>
                                                </div>
                                                <p class="text-muted mt-1" style="font-size: 12px;">
                                                    ${vou.getUsedCount()} / ${vou.getMaxUsedCount()} used
                                                </p>
                                            </div>
                                        </c:if>
                                        <c:if test="${vou.getExpirationDate() != null}">
                                            <c:set var="expirationDate" value="${vou.getExpirationDate()}" />
                                            <c:set var="formattedDate" value="${expirationDate.substring(8,10)}/${expirationDate.substring(5,7)}/${expirationDate.substring(0,4)}" />
                                            <p><b>Expiration Date:</b> ${formattedDate}</p>
                                        </c:if>
                                    </div>
                                    <!-- Select Button -->
                                    <a href="order?action=useVoucher&id=${vou.getVoucherID()}" class="btn btn-primary" style="height: 40px; align-self: center;">Select</a>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Bootstrap Modal -->
            <div class="modal fade" id="minOrder" tabindex="-1" aria-labelledby="warningModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title text-danger" id="warningModalLabel">Warning</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body text-dark">
                            <p>${sessionScope.message}</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">OK</button>
                        </div>
                    </div>
                </div>
            </div>
        </main>


        <jsp:include page="footer.jsp"></jsp:include>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/axios/0.21.1/axios.min.js"></script>
            <script
                src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
            crossorigin="anonymous"></script>
            <script>
                                    window.onload = function () {
            <% if (session.getAttribute("message") != null) { %>
                                        showMessage(true);
            <% }%>
                                    };
                                    function showMessage(show) {
                                        if (show) {
                                            var warningModal = new bootstrap.Modal(document.getElementById('minOrder'));
                                            warningModal.show();
                                        }
                                    }
                                    function closePopup() {
                                        var warningModal = bootstrap.Modal.getInstance(document.getElementById('minOrder'));
                                        if (warningModal) {
                                            warningModal.hide();
                                        }
                                    }

                                    function openVoucherModal() {
                                        var modal = new bootstrap.Modal(document.getElementById('voucherModal'));
                                        modal.show();
                                    }

                                    function closeVoucherModal() {
                                        var modalElement = document.getElementById('voucherModal');
                                        var modal = bootstrap.Modal.getInstance(modalElement);
                                        if (modal) {
                                            modal.hide();
                                        }
                                    }
                                    function applyVoucher(voucher) {
                                        document.getElementById("selectedVoucher").innerHTML = "Selected: " + voucher;
                                        closeVoucherPopup();
                                        // Redirect to servlet with the selected voucher
                                        window.location.href = "applyVoucherServlet?voucher=" + voucher;
                                    }
                                    function showPopup() {
                                        document.getElementById("orderPopup").style.display = "flex";
                                    }

                                    function closePopup() {
                                        document.getElementById("orderPopup").style.display = "none";
                                    }

                                    let demoPaymentTimer = null;

                                    async function startDemoPayment() {
                                        const button = document.getElementById("startPaymentButton");
                                        const overlay = document.getElementById("demoPaymentOverlay");
                                        const status = document.getElementById("demoPaymentStatus");
                                        button.disabled = true;
                                        overlay.style.display = "flex";
                                        status.style.color = "#b07816";
                                        status.textContent = "Creating secure demo QR...";
                                        try {
                                            const response = await fetch("${pageContext.request.contextPath}/demo-payment/start", {
                                                method: "POST",
                                                headers: {"X-Requested-With": "XMLHttpRequest"}
                                            });
                                            const data = await response.json();
                                            if (!response.ok || !data.ok) {
                                                throw new Error(data.message || "Cannot start demo payment.");
                                            }
                                            document.getElementById("paymentToken").value = data.token;
                                            document.getElementById("demoPaymentQr").src = data.qrUrl + "&t=" + Date.now();
                                            document.getElementById("demoPaymentAddress").textContent = data.confirmationUrl;
                                            status.textContent = "Waiting for QR scan...";
                                            beginPaymentPolling(data.token);
                                        } catch (error) {
                                            status.style.color = "#d92d20";
                                            status.textContent = error.message;
                                            button.disabled = false;
                                        }
                                    }

                                    function beginPaymentPolling(token) {
                                        clearInterval(demoPaymentTimer);
                                        demoPaymentTimer = setInterval(async function () {
                                            try {
                                                const response = await fetch("${pageContext.request.contextPath}/demo-payment/status?token=" + encodeURIComponent(token), {cache: "no-store"});
                                                const data = await response.json();
                                                if (data.status === "PAID") {
                                                    clearInterval(demoPaymentTimer);
                                                    const status = document.getElementById("demoPaymentStatus");
                                                    status.style.color = "#039855";
                                                    status.textContent = "Payment confirmed! Creating your order...";
                                                    setTimeout(function () {
                                                        document.getElementById("placeOrderForm").submit();
                                                    }, 1000);
                                                } else if (data.status === "EXPIRED" || data.status === "INVALID") {
                                                    clearInterval(demoPaymentTimer);
                                                    document.getElementById("demoPaymentStatus").style.color = "#d92d20";
                                                    document.getElementById("demoPaymentStatus").textContent = "QR expired. Close and try again.";
                                                    document.getElementById("startPaymentButton").disabled = false;
                                                }
                                            } catch (error) {
                                                // Keep polling through short local-network interruptions.
                                            }
                                        }, 1500);
                                    }

                                    function closeDemoPayment() {
                                        clearInterval(demoPaymentTimer);
                                        document.getElementById("demoPaymentOverlay").style.display = "none";
                                        document.getElementById("startPaymentButton").disabled = false;
                                    }

            <%
                String message = (String) session.getAttribute("orderStatus");
                if (message != null && message.equals("success")) {
                    out.print("showPopup();");
                    session.removeAttribute("orderStatus");
                }
            %>
        </script>
    </body>
    <c:if test="${not empty sessionScope.message}">
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.newOrder}">
        <c:remove var="newOrder" scope="session"/>
    </c:if>
</html>
