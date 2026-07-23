<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <title>Profile Management</title>
        <style>
            #avatar {
                width: 150px;
                height: 150px;
                border-radius: 50%;
                object-fit: cover;
            }

            .edit-profile {
                font-size: 14px;
                color: gray;
                text-decoration: none;
            }

            .menu-item {
                display: block;
                padding: 10px;
                color: #333;
                text-decoration: none;
                transition: 0.3s;
            }

            .menu-item:hover {
                background: #f7f7f7;
            }

            .sale {
                font-weight: bold;
                color: red;
            }

            .new-badge {
                background: red;
                color: white;
                padding: 2px 5px;
                font-size: 12px;
                border-radius: 4px;
            }

            /* Dropdown */
            .droppeddown-menu {
                display: none;
                padding-left: 30px;
            }

            .droppeddown-menu a {
                display: block;
                padding: 8px;
                text-decoration: none;
                color: #333;
                transition: 0.3s;
            }

            .droppeddown-menu a:hover {
                background: #f1f1f1;
            }

            /* Hiá»‡n dropdown khi cÃ³ class 'active' */
            .droppeddown.active .droppeddown-menu {
                display: block;
            }
            .content{
                margin-top: 30px;
            }

            #confirmDeleteAccount .modal-content {
                border-radius: 12px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            }

            #confirmDeleteAccount .modal-header {
                background: #f8d7da;
                color: #721c24;
            }

            #confirmDeleteAccount .btn-danger {
                background-color: #dc3545;
                border-color: #dc3545;
            }

            #confirmDeleteAccount .btn-danger:hover {
                background-color: #c82333;
                border-color: #bd2130;
            }
            /* Popup styles */
            .popup {
                display: block;
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0, 0, 0, 0.5);
                justify-content: center;
                align-items: center;
            }
            .popup-content {
                background-color: white;
                padding: 30px;
                border-radius: 8px;
                text-align: center;
                width: 350px;
                margin: 150px auto;
            }
            .popup button {
                background-color: #007bff;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
            }
            .popup button:hover {
                background-color: #0056b3;
            }
            #avatar {
                width: 150px;
                height: 150px;
                border-radius: 50%;
                object-fit: cover;
            }
        </style>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/smarttick.css?v=customer-profile-dark">
        <c:set var="initialProfilePage" value="${empty param.profilePage ? (empty requestScope.profilePage ? 'CustomerProfileView.jsp' : requestScope.profilePage) : param.profilePage}" />

    </head>

    <body class="customer-profile-page">
        <jsp:include page="header.jsp"></jsp:include>
            <main class="profile-main" style=" background: rgb(245,245,245);">
                <!-- Sidebar -->
                <div class="container profile-shell" style="font-family: Arial, sans-serif; height: auto; ">
                    <div class="row">
                        <div class="sidebar col-md-3 profile-sidebar" style=" height: auto; padding: 20px;">
                            <div class="text-center profile-user-card">
                            <c:set var="customerAvatar" value="${sessionScope.customer.avatar}" />
                            <c:choose>
                                <c:when test="${not empty customerAvatar && fn:startsWith(customerAvatar, 'http')}">
                                    <img id="avatar" src="${customerAvatar}" alt="Customer avatar" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                                </c:when>
                                <c:when test="${not empty customerAvatar}">
                                    <img id="avatar" src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/${customerAvatar}" alt="Customer avatar" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                                </c:when>
                                <c:otherwise>
                                    <img id="avatar" src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg" alt="Customer avatar">
                                </c:otherwise>
                            </c:choose>


                            <h4>${sessionScope.customer.getFullName()}</h4>
                            <a class="text-center text-danger" href="${pageContext.request.contextPath}/Logout" onclick="return confirm('Are you sure to logout?')"><i class="bi bi-box-arrow-right"></i> Logout</a>
                        </div>

                        <div class="sidebar profile-menu">
                            <ul style="list-style-type: none; padding: 0; color: black; ">
                                <a href="ViewCustomerVoucher" class="menu-item"><i class="bi bi-ticket-perforated"></i> Voucher</a>

                                <div class="droppeddown">
                                    <a href="#" class="menu-item droppeddown-toggle" data-url="CustomerProfileView.jsp"><i class="bi bi-person"></i> My Information</a>
                                    <div class="droppeddown-menu">
                                        <a href="#" class="menu-item load-content" data-url="CustomerProfileView.jsp"><i class="bi bi-person-vcard"></i> My Profile</a>
                                        <a href="ViewShippingAddress?profilePage=AddressView.jsp" class="menu-item"><i class="bi bi-geo-alt"></i> Address</a>
                                        <c:if test="${empty sessionScope.customer.getGoogleId()}">
                                            <a href="${pageContext.request.contextPath}/changeCustomerPassword" class="menu-item"><i class="bi bi-shield-lock"></i> Change Password</a>
                                        </c:if>
                                        <a href="#" class="menu-item text-danger" onclick="openDeleteAccountModal()"><i class="bi bi-trash3"></i> Request To Delete Account</a>
                                    </div>
                                </div>
                                <a href="ViewOrderHistory" class="menu-item"><i class="bi bi-box-seam"></i> Order</a>
                            </ul>
                        </div>
                        </div>
                    <div class="col-md-9 content profile-content-host" id="content" data-initial-page="${initialProfilePage}" style="padding: 15px; border-radius: 5px; ">
                        <div class="profile-loading">Loading...</div>
                    </div>
                    </div>
                </div>
            </main>

            <div id="loadingScreen" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background-color:rgba(255, 255, 255, 0.8); justify-content:center; align-items:center; font-size:24px; color:#007bff; z-index:1050;">Loading...</div>
            <div class="modal fade" id="confirmDeleteAccount" tabindex="-1" aria-labelledby="confirmDeleteAccountLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">


                        <div class="modal-header">
                            <h5 class="modal-title" id="confirmDeleteAccountLabel">Confirm Account Deletion</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>

                        <div class="modal-body">
                        <c:if test="${empty sessionScope.customer.googleId}">
                            <p>Are you sure you want to delete your account? This action cannot be undone.</p>
                            <form id="deleteAccountForm" method="POST" action="requestToDeleteAccount">
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">Enter your password to confirm:</label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-danger" id="deleteButton">Delete Account</button>
                                </div>
                            </form>
                        </c:if>
                        <c:if test="${not empty sessionScope.customer.googleId}">
                            <p>Are you sure you want to delete your account? Please enter OTP. Please check your email.</p>
                            <form id="deleteAccountForm" method="POST" action="requestToDeleteAccount">
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">Enter OTP:</label>
                                    <input type="text" class="form-control" id="OTP" name="OTP" required>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-danger" id="deleteButton">Delete Account</button>
                                </div>
                            </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>

        <%
            String message = (String) session.getAttribute("message");
            System.out.println("Session message: " + message + request.getRequestURI());
        %>
        <%
            if (message != null) {
        %>
        <div id="cookiesPopup" style="position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 350px; display: flex; flex-direction: column; align-items: center; background-color: #fff; color: #000; text-align: center; border-radius: 20px; padding: 30px 30px 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); z-index: 1000;">
            <button class="close" onclick="closePopup()" style="width: 30px; font-size: 20px; color: #c0c5cb; align-self: flex-end; background-color: transparent; border: none; margin-bottom: 10px; cursor: pointer;">âœ–</button>
            <img src="https://cdn-icons-png.flaticon.com/512/845/845646.png" alt="success-tick" style="width: 82px; margin-bottom: 15px;" />
            <p style="margin-bottom: 40px; font-size: 18px;">${sessionScope.message}</p>
            <button class="accept" onclick="closePopup()" style="background-color: #28a745; border: none; border-radius: 5px; width: 200px; padding: 14px; font-size: 16px; color: white; box-shadow: 0px 6px 18px -5px rgba(40, 167, 69, 1); cursor: pointer;">OK</button>
        </div>


        <div id="overlay" style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 999;"></div>

        <%
                session.setAttribute("message", null);
            }
        %>

        <%
            String messageFail = (String) session.getAttribute("messageFail");
            System.out.println("Session message: " + message + request.getRequestURI());
        %>
        <%
            if (messageFail != null) {
        %>
        <div id="cookiesPopup1" style="position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 350px; display: flex; flex-direction: column; align-items: center; background-color: #fff; color: #000; text-align: center; border-radius: 20px; padding: 30px 30px 20px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); z-index: 1000;">
            <button class="close" onclick="closePopup1()" style="width: 30px; font-size: 20px; color: #c0c5cb; align-self: flex-end; background-color: transparent; border: none; margin-bottom: 10px; cursor: pointer;">âœ–</button>
            <img src="./assets/imgs/icon/fail.jpg" alt="fail-tick" style="width: 82px; margin-bottom: 15px; border-radius: 50%;" />
            <p style="margin-bottom: 40px; font-size: 18px;">${sessionScope.messageFail}</p>
            <button class="accept" onclick="closePopup1()" style="background-color: red; border: none; border-radius: 5px; width: 200px; padding: 14px; font-size: 16px; color: white; box-shadow: 0px 6px 18px -5px red; cursor: pointer;">OK</button>
        </div>


        <div id="overlay" style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 999;"></div>
        <%
                session.setAttribute("messageFail", null);
            }
        %>
        <script>
            function closePopup() {
                document.getElementById("cookiesPopup").style.display = "none";
                document.getElementById("overlay").style.display = "none";
            }
            function closePopup1() {
                document.getElementById("cookiesPopup1").style.display = "none";
                document.getElementById("overlay").style.display = "none";
            }
            function openDeleteAccountModal() {
                console.log("Calling servlet...");

                const loadingScreen = document.getElementById('loadingScreen');
                loadingScreen.style.display = 'flex';

                $.ajax({
                    url: 'requestToDeleteAccount',
                    type: 'GET',
                    success: function (response) {
                        console.log(response);
                        loadingScreen.style.display = 'none';
                        if (response.status === 'success') {
                            $('#confirmDeleteAccount').modal('show');
                        } else {
                            loadingScreen.style.display = 'none';
                            window.location.reload();
                        }
                    },
                    error: function (xhr, status, error) {
                        loadingScreen.style.display = 'none';
                        console.log("Error:", error);
                    }
                });
            }


            function confirmLogout() {
                if (confirm("Are you sure you want to log out?")) {
                    // Chuyá»ƒn hÆ°á»›ng tá»›i trang logout hoáº·c gá»i API logout
                    window.location.href = "${pageContext.request.contextPath}/Logout";
                }
            }


            document.getElementById('deleteAccountForm').addEventListener('submit', function (event) {
                const password = document.getElementById('confirmPassword').value;
                if (!password) {
                    event.preventDefault();
                    alert('Please enter your password to confirm account deletion.');
                }
            });

            document.addEventListener("DOMContentLoaded", function () {
                const dropdownToggle = document.querySelector(".droppeddown-toggle");
                const dropdown = document.querySelector(".droppeddown");

                dropdownToggle.addEventListener("click", function (event) {
                    event.preventDefault(); // NgÄƒn cháº·n reload trang
                    dropdown.classList.toggle("active"); // Add hoáº·c xÃ³a class active
                });
            });

            document.addEventListener("DOMContentLoaded", function () {
                let contentDiv = document.getElementById("content");
                const initialPage = contentDiv.dataset.initialPage || "CustomerProfileView.jsp";

                loadProfileContent(initialPage);


                // Xá»­ lÃ½ khi click vÃ o menu
                document.querySelectorAll(".load-content").forEach(item => {
                    item.addEventListener("click", function (event) {
                        event.preventDefault(); // NgÄƒn khÃ´ng cho chuyá»ƒn trang

                        let page = this.getAttribute("data-url"); // Láº¥y URL tá»« data-url
                        loadProfileContent(page);
                    });
                });

                function loadProfileContent(page) {
                    fetch(page)
                            .then(response => response.text())
                            .then(data => {
                                contentDiv.innerHTML = data;
                                executeScripts(contentDiv);
                            })
                            .catch(error => {
                                console.error("Error loading profile:", error);
                                contentDiv.innerHTML = "<div class='profile-loading'>Could not load this page.</div>";
                            });
                }

                // HÃ m cháº¡y láº¡i script trong ná»™i dung Ä‘Æ°á»£c load
                function executeScripts(element) {
                    const scripts = Array.from(element.querySelectorAll("script"));
                    for (const script of scripts) {
                        const newScript = document.createElement("script");
                        if (script.src) {
                            newScript.src = script.src;
                            newScript.async = false;
                        } else {
                            newScript.textContent = script.textContent;
                        }
                        document.body.appendChild(newScript);
                        script.remove();
                    }
                }
            });
            document.addEventListener("DOMContentLoaded", function () {
                let phoneInput = document.getElementById("newPhoneNumber");
                if (phoneInput) {
                    phoneInput.addEventListener("input", validatePhone);
                }
            });

            function validatePhone() {
                console.log("validatePhone function is loaded!");
                let phoneInput = document.getElementById("newPhoneNumber");
                let phoneError = document.getElementById("phoneError");
                let saveButton = document.getElementById("saveButton");

                let phonePattern = /^0[2-9][0-9]{8}$/;  // Chá»‰ cháº¥p nháº­n 10-11 sá»‘
                if (phonePattern.test(phoneInput.value)) {
                    console.log("none");
                    phoneError.style.display = "none";
                    saveButton.disabled = false;
                } else {
                    console.log("block");
                    phoneError.style.display = "block";
                    saveButton.disabled = true;
                }
            }
        </script>
        <script src="./assets/js/profile.js"></script>

        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
        <jsp:include page="footer.jsp"></jsp:include>

    </body>

</html>
