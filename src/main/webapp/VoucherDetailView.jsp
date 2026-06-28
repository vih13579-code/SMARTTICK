<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%-- 
    Document   : VoucherDetailView
    Created on : Mar 22, 2025, 6:17:16 PM
    Author     : CE181159-Nguyen Le Duy Minh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Voucher Detail</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" href="assets/css/orderlist.css">

        <style>
            .fixed-header {
                position: fixed;
                top: 0;
                left: 250px;
                width: calc(100% - 250px);
                background-color: white;
                z-index: 1050;
                padding: 10px 20px;
                /*box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);*/
            }

            .sidebar-container {
                position: fixed;
                top: 0;
                left: 0;
                width: 250px;
                height: 100vh;
                background-color: white;
                z-index: 1100;
                padding-top: 60px;
            }

            .main-layout {
                display: flex;
            }

            .content {
                flex-grow: 1;
                margin-left: 250px;
                margin-top: 120px;
                padding: 20px;
            }

            .detail-table th {
                width: 250px;
                background-color: #f5f5f5;
                font-weight: bold;
                padding: 12px;
            }

            .detail-table td {
                padding: 12px;
            }
        </style>
    </head>
    <body>
        <div class="sidebar-container">
            <jsp:include page="SidebarDashboard.jsp" />
        </div>

        <div class="main-layout">
            <div class="fixed-header">
                <jsp:include page="HeaderDashboard.jsp"></jsp:include>
            </div>

            <div class="content">
                <h2 class="mb-4">Voucher Details</h2>

                <c:if test="${not empty voucher}">
                    <table class="table table-bordered detail-table">
                        <tr>
                            <th>Voucher ID</th>
                            <td>#${voucher.voucherID}</td>
                        </tr>
                        <tr>
                            <th>Code</th>
                            <td>${voucher.voucherCode}</td>
                        </tr>
                        <tr>
                            <th>Type</th>
                            <td>
                                <c:choose>
                                    <c:when test="${voucher.voucherType == 1}">Percent (%)</c:when>
                                    <c:when test="${voucher.voucherType == 0}">Fixed price</c:when>
                                    <c:otherwise>Unknown</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th>Value</th>
                            <td>${voucher.voucherValue}</td>
                        </tr>
                        <tr>
                            <th>Max Discount Amount</th>
                            <td>${voucher.maxDiscountAmount}</td>
                        </tr>
                        <tr>
                            <th>Min Order Value</th>
                            <td>${voucher.minOrderValue}</td>
                        </tr>
                        <tr>
                            <th>Start Date</th>
                            <td>${voucher.startDate}</td>
                        </tr>
                        <tr>
                            <th>End Date</th>
                            <td>${voucher.endDate}</td>
                        </tr>
                        <tr>
                            <th>Used Count</th>
                            <td>${voucher.usedCount}</td>
                        </tr>
                        <tr>
                            <th>Max Used Count</th>
                            <td>${voucher.maxUsedCount}</td>
                        </tr>
                        <tr>
                            <th>Status</th>
                            <td>
                                <c:choose>
                                    <c:when test="${voucher.status == 1}">Active</c:when>
                                    <c:when test="${voucher.status == 0}">Inactive</c:when>
                                    <c:otherwise>Unknown</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th>Description</th>
                            <td>${voucher.description}</td>
                        </tr>
                    </table>

<!--                    <a href="ViewVoucherListServlet" class="btn btn-secondary mt-3">
                        <i class="fa-solid fa-arrow-left"></i> Back to list
                    </a>-->
                </c:if>

                <c:if test="${empty voucher}">
                    <div class="alert alert-warning">Voucher not found.</div>
                </c:if>
            </div>
        </div>
    </body>
</html>
