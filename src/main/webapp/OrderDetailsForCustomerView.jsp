<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setLocale value="vi_VN" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="Models.Order"%>
<%@page import="Models.OrderDetail"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <title>Order Details</title>
        <link rel="stylesheet" href="./assets/css/orderDetailForCus.css"/>
    </head>

    <body>
        <main style="background: white; padding: 20px; border-radius: 10px;">
            <div class="container">
                <div class="row">
                    <div class="col-md-4">
                        <h2>Order Details</h2>
                    </div>
                    <div class="col-md-8">
                        <c:if test="${sessionScope.order.getStatus() != 5}">
                            <div class="progress-container">
                                <div class="progress-bar">
                                    <div class="progress"></div>
                                </div>
                                <div class="progress-step active">
                                    <div class="circle">1</div>
                                    <div class="label">Waiting for acceptance</div>
                                </div>
                                <div class="progress-step">
                                    <div class="circle">2</div>
                                    <div class="label">Packaging</div>
                                </div>
                                <div class="progress-step">
                                    <div class="circle">3</div>
                                    <div class="label">Waiting for delivery</div>
                                </div>
                                <div class="progress-step">
                                    <div class="circle">4</div>
                                    <div class="label">Delivered</div>
                                </div>
                                <div class="progress-step">
                                    <div class="circle">5</div>
                                    <div class="label">Success</div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>
                <div class="row status">
                    <div class="col-md-6">
                        <p><b>OrderID: ${sessionScope.order.getOrderID()}</b></p>
                    </div>
                    <div class="col-md-6">
                        <p class="success text-end" style="color: red;">
                            <b>
                                <c:choose>
                                    <c:when test="${sessionScope.order.getStatus() == 1}">Waiting For Acceptance</c:when>
                                    <c:when test="${sessionScope.order.getStatus() == 2}">Packaging</c:when>
                                    <c:when test="${sessionScope.order.getStatus() == 3}">Waiting For Delivery</c:when>
                                    <c:when test="${sessionScope.order.getStatus() == 4}">Delivered</c:when>
                                    <c:otherwise>Cancelled</c:otherwise>
                                </c:choose>
                            </b>
                        </p>
                    </div>
                </div>
                <br>
                <div class="row">
                    <p><b>Order Item</b></p>
                    <div class="col-md-8">
                        <table style="width: 100%; border-collapse: collapse; text-align: left; font-family: Arial, sans-serif; color: #333;">
                            <tr style="height: 50px; border-bottom: 2px solid #eee; font-weight: bold; font-size: 16px;">
                                <th style="width: 17%; padding: 12px 8px;">Item</th>
                                <th style="width: 25%; padding: 12px 8px;"></th>
                                <th style="width: 20%; padding: 12px 8px; text-align: right;">Price</th>
                                <th style="width: 12%; padding: 12px 8px; text-align: center;">Qty</th>
                                <th style="width: 21%; padding: 12px 8px; text-align: right;">Subtotal</th>
                                <th style="width: 5%; padding: 12px 8px; text-align: right;">Review</th>
                            </tr>
                            <c:set var="subtotal" value="0"></c:set>
                            <c:forEach items="${sessionScope.orderDetail}" var="od">
                                <tr style="border-bottom: 1px solid #f2f2f2;">
                                    <td style="padding: 12px 8px;"> <a style="text-decoration: none; color: black;" href="ProductDetailServlet?id=${od.getProductID()}" ><img src="${pageContext.request.contextPath}/product-images/${od.getImage()}" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/Products/watches/watch-placeholder.svg'" alt="" style="width: 85px; border-radius: 8px;">  </a></td>
                                    <td style="padding: 12px 8px; font-size: 15px;"><a style="text-decoration: none; color: black;"  href="ProductDetailServlet?id=${od.getProductID()}" >${od.getProductName()}</a></td>
                                    <td style="padding: 12px 8px; text-align: right; font-weight: 500; color: #444;">
                                        <fmt:formatNumber value="${od.getPrice()}" type="currency"/>
                                    </td>
                                    <td style="padding: 12px 8px; text-align: center; font-weight: 500;">x${od.getQuantity()}</td>
                                    <td style="padding: 12px 8px; text-align: right; font-weight: 600; color: #2a9d8f;">
                                        <fmt:formatNumber value="${od.getPrice() * od.getQuantity()}" type="currency"/>
                                    </td>
                                     <td style="padding: 12px 8px;"> <a style="text-decoration: none; color: black;" href="ProductDetailServlet?id=${od.getProductID()}" ><img src="./assets/imgs/icon/review.png"} alt="" style="width: 35px; border-radius: 1px;">  </a></td>
                                </tr>
                                <c:set var="subtotal" value="${subtotal + (od.getPrice() * od.getQuantity())}"></c:set>
                            </c:forEach>
                        </table>

                    </div>
                    <div class="col-md-4"
                         style="border-radius: 5px; box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19); padding: 20px; height: 40%;">
                        <p><b>Shipping Address</b></p>
                        <p> <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg"
                                 xmlns:xlink="http://www.w3.org/1999/xlink">
                            <rect width="18" height="18" fill="url(#pattern0_2121_5434)" />
                            <defs>
                        <pattern id="pattern0_2121_5434" patternContentUnits="objectBoundingBox" width="1"
                                 height="1">
                            <use xlink:href="#image0_2121_5434" transform="scale(0.01)" />
                        </pattern>
                        <image id="image0_2121_5434" width="100" height="100" preserveAspectRatio="none"
                               xlink:href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAAAXNSR0IArs4c6QAABMZJREFUeAHtnUnIHEUYhh+XGJcgJi6XuCDicoge3A0hBz1IEFwPih71EnG5il4ED2oCiSCiIkQ9SMSoNxfwInhSVFAUJFHERBPFjSCKS1z6xR7oNDP/9FLTU5nvLRimu6e6luetqpmu+uobcDABEzABEzABEzABEzABEzABEzABEzCBrgSOAK4A7gdeAT4FfirO/yxfOv6k/ExxLgcO75qZ75tM4DTgEeBr4N+Wrz3Aw8Cpk5P3J00JnAw8DfzRUoRxoimNJ4GTmmbueAcTuBX4MYEQdXF+AG45OCufLUVgGfDMDISoC/MUoLwcliBwLPD6AGKMxHkNUJ4OYwiotQ4pxkiUt4CjxpQn/KUhhqmRCPV3fdk7VAjcNuAwVRdjdH5zpTyhD08Evs9AEP2i80/i8jlj1Ern/f5E6K5RPj2neOhLJaTKcnpkUTQdkgpmqnQ0zRIyaNJP80ypQKZKR/NlmsQMFzRrmwpi6nQuDadGOYWeGmSq9O6LKMirGfeQHREF0UJSqhadOp2PIwoyi6n1VMLoQTVcyOn5oy7k7+HUyHi4GokTTpNRxXN9tyCZ9ZpwghzITIBqT1XZwoX9GQvyczg1gC8zFuSLiIJoLbs6TOR0/GZEQbZkLMjmiIJcm7Eg10QU5HjgtwxF+RVYEVEQ1Xl7hoK8EFUM1Xt9hoKsiyyI6v5ORqK8HV0M1f9i4O8MRFEZQi7djmuEj2cgyGPjChb12tHAh3MU5SPgmKjwJ9X7TGDvHET5BjhjUqGiX78A+G5AUb4tNoieHx36tPqfDXw+gCg7gbOmFcaf/0/gBODlGYryEqCZAoeWBG4CdicU5ivgxpZlcPQaAf0CuxPY1UMYDU8bi/3qy2tp+7QHgcOAtYCmxj8A/lpCIHl2eL/Y0Lmp9PzQI1vf2pSANomeW0y9XAVcV76uLK95u3NTio5nAiZgAiZgAiZgAiZgAiZgAiZgAiZgAibQhcDqwuvoHYC2S9/VJYGW99xd5qU8lbdDuZatTfoycPinNnurRSR5JU0dTgG0D71qba+8VQaVJdz6unwc3l4ayNVFqELSsbYn35PIBZ/WQu4tep+8ktbzqZ6rTDLeUxkX2h+j1sq3FlC0O6kKoMmxHMLIU3UXZ8hywvwAIOuSJnlV48hTtrZNqOwLE2TV8WIi60S13ncLG6pHy6VYWamsKt29ag1Ex7qmpV/FeW/MUFgF3vRYVo0yDF9zKKtyTumDfdqw1BRKDvFUFxlfHFI9ZmXZzbWMmgPEWZRBXig0lMk6JuuwoaPD/FlAGyJNGdrdkKMixwHPLXCPmCbus4XxhRhkEfRQJQuPaYVe9M9lsD33Z5jzAHXbRYfdtH77SquXufQUPRPIErBpYaPEE5Muz0u9RdTm+iiQ29bzjd50WyaQ42bNttBmHX/QzaMPuXdMHR0ebNnIe0V/3oJMFWRbL8Itb87ZT8msh6Km6Q/qL0W+QJoWLGq8q1s28l7RNbP6mUWZ2Cj1h5dH9iLc4ebLADlqidoDJtX7F+CiDjyT3HLJQBs0J1U+t+vaqXVhErI9EtFfO2jfnv4LRIs4WguP9FKdVffro/7NRY+241tNwARMwARMwARMwARMwARMwARMwARMwARMwARSEfgPkiw2tiDXOIEAAAAASUVORK5CYII=" />
                        </defs>
                        </svg>
                        ${sessionScope.order.getFullName()}</p>
                        <p><svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg"
                                xmlns:xlink="http://www.w3.org/1999/xlink">
                            <rect width="18" height="18" fill="url(#pattern0_2121_5440)" />
                            <defs>
                        <pattern id="pattern0_2121_5440" patternContentUnits="objectBoundingBox" width="1"
                                 height="1">
                            <use xlink:href="#image0_2121_5440" transform="scale(0.01)" />
                        </pattern>
                        <image id="image0_2121_5440" width="100" height="100" preserveAspectRatio="none"
                               xlink:href="data:image/png;base64,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" />
                        </defs>
                        </svg>
                        ${sessionScope.order.getPhone()}</p>
                        <p> <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg"
                                 xmlns:xlink="http://www.w3.org/1999/xlink">
                            <rect width="18" height="18" fill="url(#pattern0_2121_5437)" />
                            <defs>
                        <pattern id="pattern0_2121_5437" patternContentUnits="objectBoundingBox" width="1"
                                 height="1">
                            <use xlink:href="#image0_2121_5437" transform="scale(0.01)" />
                        </pattern>
                        <image id="image0_2121_5437" width="100" height="100" preserveAspectRatio="none"
                               xlink:href="data:image/png;base64,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" />
                        </defs>
                        </svg>
                        ${sessionScope.order.getAddress()}</p>
                    </div>
                </div>
                <br>
                <div class="row" style="width: 68%;">
                    <p><b>Order Summary</b></p>
                    <div class="col-md-6">
                        <p>Subtotal</p>
                        <p>Discount</p>
                        <p>Total</p>
                    </div>
                    <div class="col-md-6" style="text-align: right;">
                        <p><b><fmt:formatNumber value="${subtotal}" type="currency"/></b></p>
                        <p><b><fmt:formatNumber value="${sessionScope.order.getDiscount()}" type="currency"/></b></p>
                        <p><b><fmt:formatNumber value="${sessionScope.order.getTotalAmount()}" type="currency"/></b></p>
                        <c:if test="${sessionScope.order.getStatus() == 1}">
                            <button style="height: 40px; width: 150px; border: 1px solid black; border-radius: 10px;" onclick="confirmDelete()">Cancel Order</button>
                        </c:if>
                    </div>
                </div>
            </div>

            <div id="confirmationModal" class="modal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.4); align-items: center; justify-content: center;">
                <div class="modal-content" style="width: 400px; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); text-align: center;">
                    <h3 style="margin-bottom: 20px; color: #333;">Are you sure you want to cancel this order?</h3>
                    <div style="display: flex; justify-content: center; gap: 15px;">
                        <button id="confirmBtn" style="background: #e74c3c; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer;"><a style="text-decoration: none; color: white;" href="cancelOrder?id=${sessionScope.order.getOrderID()}">Yes</a></button>
                        <button id="cancelBtn" style="background: #2ecc71; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer;">No</button>
                    </div>
                </div>
            </div>

            <!-- Bootstrap Modal -->
            <div class="modal fade" id="updatePopup" tabindex="-1" aria-labelledby="warningModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title text-danger" id="warningModalLabel">Warning</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body text-dark">
                            Are you sure to cancel this order?
                        </div>
                        <div class="modal-footer">
                            <a class="btn btn-danger" href="cancelOrder?id=${sessionScope.order.getOrderID()}">Yes</a>
                            <a class="btn btn-secondary"  href="">No</a>

                        </div>
                    </div>
                </div>
            </div>
            <c:set var="status" value="0"></c:set>
            <c:if test="${sessionScope.order.getStatus() < 4}">
                <c:set var="status" value="${sessionScope.order.getStatus() - 1}"></c:set>
            </c:if>
            <c:if test="${sessionScope.order.getStatus() == 4}">
                <c:set var="status" value="${sessionScope.order.getStatus()}"></c:set>
            </c:if>
        </main>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/axios/0.21.1/axios.min.js"></script>
        <script src="./assets/js/orderDetail.js"></script>
        <script>
                                function showWarning(show) {
                                    if (show) {
                                        var warningModal = new bootstrap.Modal(document.getElementById('updatePopup'));
                                        warningModal.show();
                                    }
                                }
                                function closePopup() {
                                    var warningModal = bootstrap.Modal.getInstance(document.getElementById('updatePopup'));
                                    if (warningModal) {
                                        warningModal.hide();
                                    }
                                }
                                let currentStep = ${status}; // Thay Ä‘á»•i bÆ°á»›c hiá»‡n táº¡i theo tráº¡ng thÃ¡i orders
                                const steps = document.querySelectorAll(".progress-step");
                                const progress = document.querySelector(".progress");

                                function updateProgress(step) {
                                    step = Math.max(0, Math.min(step, steps.length - 1));

                                    steps.forEach((stepElement, index) => {
                                        stepElement.classList.toggle("active", index <= step);
                                    });

                                    // Chia Ä‘á»u khoáº£ng cÃ¡ch giá»¯a cÃ¡c bÆ°á»›c (20%, 40%, 60%, 80%, 100%)
                                    const progressWidth = ((step + 1) / steps.length) * 100;
                                    progress.style.width = `${progressWidth}%`;
                                }

                                updateProgress(currentStep);

        </script>
    </body>

</html>

