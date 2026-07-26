<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Account | SMARTTICK</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=customer-profile-restored">
    <c:set var="initialProfilePage" value="${empty param.profilePage ? (empty requestScope.profilePage ? 'CustomerProfileView.jsp' : requestScope.profilePage) : param.profilePage}" />
</head>
<body class="customer-account-page">
<jsp:include page="header.jsp"/>

<main class="section account-section">
    <div class="container profile-shell">
        <div class="section-head account-head">
            <div>
                <span class="eyebrow">Customer Account</span>
                <h2>Hello, <c:out value="${sessionScope.customer.fullName}"/></h2>
                <p class="section-sub">Manage your profile, orders, vouchers, addresses, and password.</p>
            </div>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/customer/dashboard">Dashboard</a>
        </div>

        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-success"><c:out value="${sessionScope.message}"/></div>
            <c:remove var="message" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.messageFail}">
            <div class="alert alert-danger"><c:out value="${sessionScope.messageFail}"/></div>
            <c:remove var="messageFail" scope="session"/>
        </c:if>

        <div class="account-layout">
            <aside class="account-sidebar profile-sidebar">
                <div class="account-user-card profile-user-card">
                    <c:set var="customerAvatar" value="${sessionScope.customer.avatar}" />
                    <c:choose>
                        <c:when test="${not empty customerAvatar && fn:startsWith(customerAvatar, 'http')}">
                            <img id="avatar" class="account-avatar" src="${customerAvatar}" alt="Customer avatar" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                        </c:when>
                        <c:when test="${not empty customerAvatar}">
                            <img id="avatar" class="account-avatar" src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/${customerAvatar}" alt="Customer avatar" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                        </c:when>
                        <c:otherwise>
                            <img id="avatar" class="account-avatar" src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg" alt="Customer avatar">
                        </c:otherwise>
                    </c:choose>
                    <strong><c:out value="${sessionScope.customer.fullName}"/></strong>
                    <span><c:out value="${sessionScope.customer.email}"/></span>
                </div>

                <nav class="account-nav profile-menu" aria-label="Account navigation">
                    <a class="menu-item" href="${pageContext.request.contextPath}/viewCustomerProfile" data-page="CustomerProfileView.jsp"><i class="bi bi-person"></i> Profile</a>
                    <c:if test="${empty sessionScope.customer.googleId}">
                        <a class="menu-item" href="${pageContext.request.contextPath}/changeCustomerPassword" data-page="ChangeCustomerPasswordView.jsp"><i class="bi bi-shield-lock"></i> Change Password</a>
                    </c:if>
                    <a class="menu-item" href="${pageContext.request.contextPath}/ViewOrderHistory" data-page="OrdersHistoryView.jsp"><i class="bi bi-box-seam"></i> Order History</a>
                    <a class="menu-item" href="${pageContext.request.contextPath}/ViewShippingAddress" data-page="AddressView.jsp"><i class="bi bi-geo-alt"></i> Addresses</a>
                    <a class="menu-item" href="${pageContext.request.contextPath}/ViewCustomerVoucher" data-page="CustomerVoucherView.jsp"><i class="bi bi-ticket-perforated"></i> Voucher</a>
                    <button class="menu-item" type="button" onclick="openDeleteAccountModal()"><i class="bi bi-trash3"></i> Request Account Deletion</button>
                    <a class="menu-item danger" href="${pageContext.request.contextPath}/Logout" onclick="return confirm('Are you sure to logout?')"><i class="bi bi-box-arrow-right"></i> Log Out</a>
                </nav>
            </aside>

            <section class="account-content-host profile-content-host panel" id="content" data-initial-page="${initialProfilePage}">
                <div class="profile-loading">Loading...</div>
            </section>
        </div>
    </div>
</main>

<div id="loadingScreen" class="account-loading">Loading...</div>

<div class="modal fade" id="confirmDeleteAccount" tabindex="-1" aria-labelledby="confirmDeleteAccountLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmDeleteAccountLabel">Confirm Account Deletion</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <c:choose>
                    <c:when test="${empty sessionScope.customer.googleId}">
                        <p>Are you sure you want to delete your account? This action cannot be undone.</p>
                        <form id="deleteAccountForm" method="POST" action="${pageContext.request.contextPath}/requestToDeleteAccount">
                            <label for="confirmPassword" class="form-label">Enter your password to confirm</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <p>Are you sure you want to delete your account? Please enter the OTP sent to your email.</p>
                        <form id="deleteAccountForm" method="POST" action="${pageContext.request.contextPath}/requestToDeleteAccount">
                            <label for="OTP" class="form-label">Enter OTP</label>
                            <input type="text" class="form-control" id="OTP" name="OTP" required>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline" data-bs-dismiss="modal">Cancel</button>
                <button type="submit" form="deleteAccountForm" class="btn btn-primary">Submit Request</button>
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
<script>
    function openDeleteAccountModal() {
        const loadingScreen = document.getElementById('loadingScreen');
        loadingScreen.style.display = 'flex';
        $.ajax({
            url: '${pageContext.request.contextPath}/requestToDeleteAccount',
            type: 'GET',
            success: function (response) {
                loadingScreen.style.display = 'none';
                if (!response || response.status === 'success') {
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmDeleteAccount')).show();
                } else {
                    window.location.reload();
                }
            },
            error: function () {
                loadingScreen.style.display = 'none';
                bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmDeleteAccount')).show();
            }
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        const contentDiv = document.getElementById("content");
        const initialPage = contentDiv.dataset.initialPage || "CustomerProfileView.jsp";
        setActiveNav(initialPage);
        loadProfileContent(initialPage);

        document.querySelectorAll(".account-nav a[data-page]").forEach(item => {
            item.addEventListener("click", function (event) {
                const page = this.getAttribute("data-page");
                if (!page || page === "AddressView.jsp" || page === "OrdersHistoryView.jsp" || page === "CustomerVoucherView.jsp" || page === "ChangeCustomerPasswordView.jsp") {
                    return;
                }
                event.preventDefault();
                setActiveNav(page);
                loadProfileContent(page);
            });
        });

        function setActiveNav(page) {
            document.querySelectorAll(".account-nav a[data-page]").forEach(link => {
                link.classList.toggle("active", link.getAttribute("data-page") === page);
            });
        }

        function loadProfileContent(page) {
            fetch(page)
                .then(response => response.text())
                .then(html => {
                    const parsed = new DOMParser().parseFromString(html, "text/html");
                    const headAssets = parsed.head
                        ? Array.from(parsed.head.querySelectorAll('style, link[rel="stylesheet"]')).map(node => node.outerHTML).join("")
                        : "";
                    const body = parsed.body && parsed.body.innerHTML.trim() ? parsed.body.innerHTML : html;
                    contentDiv.innerHTML = headAssets + body;
                    executeScripts(contentDiv);
                })
                .catch(() => {
                    contentDiv.innerHTML = "<div class='profile-loading'>Could not load this page.</div>";
                });
        }

        function executeScripts(element) {
            element.querySelectorAll("script").forEach(script => {
                const newScript = document.createElement("script");
                if (script.src) {
                    newScript.src = script.src;
                    newScript.async = false;
                } else {
                    newScript.textContent = script.textContent;
                }
                document.body.appendChild(newScript);
                script.remove();
            });
        }
    });
</script>
</body>
</html>
