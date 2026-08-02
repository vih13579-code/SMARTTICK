<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Account | SMARTTICK</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=20260729-customer-account">
    <c:set var="initialProfilePage"
           value="${empty requestScope.profilePage ? 'CustomerProfileView.jsp' : requestScope.profilePage}"/>
</head>
<body class="customer-account-page">
<jsp:include page="header.jsp"/>

<main class="section account-section">
    <div class="container">
        <div class="section-head account-head">
            <div>
                <span class="eyebrow">Customer account</span>
                <h1>Hello, <c:out value="${sessionScope.customer.fullName}"/></h1>
                <p class="section-sub">Your profile and account activity, all in one place.</p>
            </div>
            <a class="btn btn-outline" href="${pageContext.request.contextPath}/Watches">
                Continue shopping
            </a>
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
            <aside class="account-sidebar">
                <div class="account-user-card">
                    <c:set var="customerAvatar" value="${sessionScope.customer.avatar}"/>
                    <c:choose>
                        <c:when test="${not empty customerAvatar && fn:startsWith(customerAvatar, 'http')}">
                            <img class="account-avatar" src="${customerAvatar}" alt="Customer avatar"
                                 onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                        </c:when>
                        <c:when test="${not empty customerAvatar}">
                            <img class="account-avatar"
                                 src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/${customerAvatar}"
                                 alt="Customer avatar"
                                 onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                        </c:when>
                        <c:otherwise>
                            <img class="account-avatar"
                                 src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg"
                                 alt="Customer avatar">
                        </c:otherwise>
                    </c:choose>
                    <strong><c:out value="${sessionScope.customer.fullName}"/></strong>
                    <span><c:out value="${sessionScope.customer.email}"/></span>
                </div>

                <nav class="account-nav" aria-label="Account navigation">
                    <a class="${initialProfilePage eq 'CustomerProfileView.jsp' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/viewCustomerProfile">
                        <i class="bi bi-person"></i> Profile
                    </a>
                    <a class="${initialProfilePage eq 'ChangeCustomerPasswordView.jsp' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/changeCustomerPassword">
                        <i class="bi bi-shield-lock"></i>
                        <c:choose>
                            <c:when test="${sessionScope.hasLocalPassword}">Change Password</c:when>
                            <c:otherwise>Set Password</c:otherwise>
                        </c:choose>
                    </a>
                    <a class="${initialProfilePage eq 'OrdersHistoryView.jsp' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/ViewOrderHistory">
                        <i class="bi bi-box-seam"></i> Order History
                    </a>
                    <a class="${initialProfilePage eq 'AddressView.jsp' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/ViewShippingAddress">
                        <i class="bi bi-geo-alt"></i> Addresses
                    </a>
                    <a class="${initialProfilePage eq 'CustomerVoucherView.jsp' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/ViewCustomerVoucher">
                        <i class="bi bi-ticket-perforated"></i> Voucher
                    </a>
                    <button type="button" class="danger" onclick="openDeleteAccountModal()">
                        <i class="bi bi-trash3"></i> Request Account Deletion
                    </button>
                    <a class="danger" href="${pageContext.request.contextPath}/Logout"
                       onclick="return confirm('Are you sure you want to log out?')">
                        <i class="bi bi-box-arrow-right"></i> Log Out
                    </a>
                </nav>
            </aside>

            <section class="account-content-host panel" id="content">
                <jsp:include page="${initialProfilePage}" flush="true"/>
            </section>
        </div>
    </div>
</main>

<div class="modal fade" id="confirmDeleteAccount" tabindex="-1"
     aria-labelledby="confirmDeleteAccountLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content account-delete-modal">
            <div class="modal-header">
                <div>
                    <span class="eyebrow">Account security</span>
                    <h2 class="modal-title" id="confirmDeleteAccountLabel">Request account deletion</h2>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="modal"
                        aria-label="Close"></button>
            </div>
            <form id="deleteAccountForm" method="post"
                  action="${pageContext.request.contextPath}/requestToDeleteAccount">
                <div class="modal-body">
                    <div class="account-delete-warning">
                        <i class="bi bi-exclamation-triangle"></i>
                        <div>
                            <strong>This action cannot be undone.</strong>
                            <p>Your profile will be disabled and you will be signed out immediately.</p>
                        </div>
                    </div>

                    <div id="deleteAccountStatus" class="alert alert-danger" hidden></div>
                    <input type="hidden" id="deleteChallenge" name="challenge">

                    <div id="deletePasswordGroup" class="delete-verification" hidden>
                        <label for="confirmPassword" class="form-label">
                            Enter your SMARTTICK password to confirm
                        </label>
                        <input type="password" class="form-control" id="confirmPassword"
                               name="confirmPassword" autocomplete="current-password" disabled>
                    </div>

                    <div id="deleteOtpGroup" class="delete-verification" hidden>
                        <p>A 6-digit verification code was sent to
                            <strong><c:out value="${sessionScope.customer.email}"/></strong>.
                            The code expires in 5 minutes.</p>
                        <label for="deleteOtp" class="form-label">Verification code</label>
                        <input type="text" inputmode="numeric" pattern="[0-9]{6}" maxlength="6"
                               class="form-control" id="deleteOtp" name="OTP" disabled>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline" data-bs-dismiss="modal">Keep account</button>
                    <button type="submit" id="deleteAccountSubmit" class="btn btn-danger" disabled>
                        Delete my account
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
<script>
    const deleteModalElement = document.getElementById("confirmDeleteAccount");
    const deleteModal = bootstrap.Modal.getOrCreateInstance(deleteModalElement);
    const deleteForm = document.getElementById("deleteAccountForm");
    const deleteStatus = document.getElementById("deleteAccountStatus");
    const deletePasswordGroup = document.getElementById("deletePasswordGroup");
    const deleteOtpGroup = document.getElementById("deleteOtpGroup");
    const deletePassword = document.getElementById("confirmPassword");
    const deleteOtp = document.getElementById("deleteOtp");
    const deleteSubmit = document.getElementById("deleteAccountSubmit");

    function resetDeleteAccountDialog() {
        deleteStatus.hidden = true;
        deleteStatus.textContent = "";
        deletePasswordGroup.hidden = true;
        deleteOtpGroup.hidden = true;
        deletePassword.disabled = true;
        deleteOtp.disabled = true;
        deletePassword.value = "";
        deleteOtp.value = "";
        deleteSubmit.disabled = true;
        document.getElementById("deleteChallenge").value = "";
    }

    async function openDeleteAccountModal() {
        resetDeleteAccountDialog();
        deleteSubmit.textContent = "Preparing...";
        deleteModal.show();

        try {
            const response = await fetch(
                    "${pageContext.request.contextPath}/requestToDeleteAccount",
                    {headers: {"Accept": "application/json"}, credentials: "same-origin"});
            const payload = await response.json();

            if (!response.ok || payload.status !== "success") {
                throw new Error(payload.message || "Could not prepare account deletion.");
            }

            document.getElementById("deleteChallenge").value = payload.challenge;
            if (payload.verification === "password") {
                deletePasswordGroup.hidden = false;
                deletePassword.disabled = false;
                deletePassword.required = true;
                deletePassword.focus();
            } else {
                deleteOtpGroup.hidden = false;
                deleteOtp.disabled = false;
                deleteOtp.required = true;
                deleteOtp.focus();
            }
            deleteSubmit.disabled = false;
            deleteSubmit.textContent = "Delete my account";
        } catch (error) {
            deleteStatus.textContent = error.message;
            deleteStatus.hidden = false;
            deleteSubmit.textContent = "Delete my account";
        }
    }

    deleteForm.addEventListener("submit", function () {
        deleteSubmit.disabled = true;
        deleteSubmit.textContent = "Deleting...";
    });

    deleteModalElement.addEventListener("hidden.bs.modal", resetDeleteAccountDialog);
</script>
</body>
</html>
