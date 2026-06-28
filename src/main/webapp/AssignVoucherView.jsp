<%--
    Author     : CE181159-Nguyen Le Duy Minh
    Since: 2026-06-29
--%>
<%-- 
    Document   : AssignVoucherView
    Created on : Mar 22, 2025, 9:03:30 PM
    Author     : CE181159-Nguyen Le Duy Minh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Assign Voucher</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">

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
            padding-top: 0px;
        }

        .content {
            margin-left: 250px;
            margin-top: 120px;
            padding: 20px;
        }

        .form-wrapper {
          
            align-content: center;
            max-width: 600px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 12px;
            /*box-shadow: 0 0 10px rgba(0,0,0,0.1);*/
            margin-left: 350px;
        }

        .form-wrapper h2 {
            text-align: center;
            margin-bottom: 20px;
            font-weight: 600;
        }

        .btn i {
            margin-right: 6px;
        }
    </style>
</head>
<body>

<div class="sidebar-container">
    <jsp:include page="SidebarDashboard.jsp" />
</div>

<div class="fixed-header">
    <jsp:include page="HeaderDashboard.jsp" />
</div>

<div class="content">
    <div class="form-wrapper">
        <h2>Assign Voucher to ${customer.fullName}</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fa-solid fa-triangle-exclamation me-2"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <form action="AssignVoucherServlet" method="post">
            <input type="hidden" name="customerID" value="${customer.id}" />

            <div class="mb-3">
                <label class="form-label">Voucher</label>
                <select name="voucherID" class="form-select" required>
                    <c:forEach items="${vouchers}" var="v">
                        <option value="${v.voucherID}">${v.voucherCode}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label class="form-label">Quantity</label>
                <input type="number" name="quantity" class="form-control" value="1" min="1" required>
            </div>

            <div class="mb-3">
                <label class="form-label">Expiration Date</label>
                <input type="datetime-local" name="expirationDate" class="form-control">
            </div>

            <div class="text-center">
                <button class="btn btn-success" type="submit">
                    <i class="fa-solid fa-ticket"></i> Assign Voucher
                </button>
            </div>
        </form>
    </div>
</div>

</body>
</html>
