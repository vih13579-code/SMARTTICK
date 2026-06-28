<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%-- 
    Document   : CustomerVoucherView
    Created on : Mar 18, 2025, 5:20:29 PM
    Author     : CE181159-Nguyen Le Duy Minh
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Models.CustomerVoucher" %> 
<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gift Voucher</title>
        <style>
            .bodyyyyyy {
                font-family: Arial, sans-serif;
                background-color: white;
                display: flex;
                flex-wrap: wrap;
                justify-content: center;
                align-items: center;
                gap: 15px;
            }
            h1{
                border-bottom: 1px solid black;
                padding-bottom: 20px
            }
            .voucher {
                display: flex;
                background-color: white;
                border-radius: 10px;
                overflow: hidden;
                border: 0.5px solid black;
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
                margin: 10px 0;
                position: relative;
                height: 130px;
            }

            .left {
                flex: 1;
                padding: 10px;
                color: black;
            }

            .left h2 {
                display: flex;
                align-items: center;
                font-size: 22px;
                margin-bottom: 10px;
            }

            .left p {
                margin: 5px 0;
                font-size: 12px;
            }

            .right {
                background-color: #ee4d2d;
                color: white;
                padding: 20px;
                display: flex;
                justify-content: center;
                align-items: center;
                font-size: 15px;
                font-weight: bold;
                width: 120px;
            }

            .quatity {
                position: absolute;
                top: 5px;
                right: 0px;
                background-color: #ee4d2d;
                color: white;
                font-size: 12px;
                font-weight: bold;
                border-radius: 5px;
                width: 40px;
                height: 25px;
                display: flex;
                justify-content: center;
                align-items: center;
            }

            .progress {
                width: 100%;
                height: 12px;
                margin-top: 10px;
            }

            .progress-bar {
                transition: width 0.4s ease;
            }
        </style>
    </head>

    <body >
        <main style="border-radius: 10px;
              box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
              padding: 15px;
              background-color: white; height: 100%">
            <h1>Your voucher</h1>
            <br>
            <div class="container-fluid">
                <div class="row">
                    <c:if test="${sessionScope.customerVoucher != null}">
                        <c:forEach var="vou" items="${sessionScope.customerVoucher}">
                            <div class="col-md-6">
                                <div class="voucher">

                                    <div class="right">
                                        <c:if test="${vou.getVoucherType() == 0}">
                                            <fmt:formatNumber type="currency" value="${vou.getVoucherValue()}"></fmt:formatNumber> Sale off
                                        </c:if>
                                        <c:if test="${vou.getVoucherType() == 1}">
                                            ${vou.getVoucherValue()}% Sale off
                                        </c:if>
                                    </div>


                                    <div class="left">
                                        <h2>
                                            <img class="logo" src="./assets/imgs/HeaderImgs/Vector-Header-Logo.svg" alt="Logo"> GIFT VOUCHER
                                        </h2>
                                        <div class="terms">
                                            <p>${vou.getDescription()}</p>
                                        </div>
                                        <c:if test="${vou.getExpirationDate() != null}">
                                            <c:set var="expirationDate" value="${vou.getExpirationDate()}" />
                                            <c:set var="formattedDate" value="${expirationDate.substring(8,10)}/${expirationDate.substring(5,7)}/${expirationDate.substring(0,4)}" />
                                            <p><b>Expiration Date:</b> ${formattedDate}</p>
                                        </c:if>

                                    </div>


                                    <div class="quatity">X${vou.getQuantity()}</div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:if>
                    <c:if test="${empty sessionScope.customerVoucher}">
                        <p style="text-align: center; font-size: 20px;" >You have no voucher.</p>
                    </c:if>
                </div>
            </div>

        </main>
    </body>

</html>
