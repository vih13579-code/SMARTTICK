<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Update Customer</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

        <style>
            .profile-content {
                flex: 1;
                padding: 30px;
                background: white;
                margin: 20px;
                border-radius: 5px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            .avatar-preview {
                width: 150px;
                height: 150px;
                border-radius: 50%;
                object-fit: cover;
            }

            .profile {
                background: white;
                display: flex;
                flex-wrap: wrap;
                align-items: center;
                justify-content: space-between;
                padding: 20px;
            }

            .info {
                width: 50%;
                min-width: 400px;
            }

            .avatar {
                width: 50%;
                min-width: 400px;
                text-align: center;
                padding: 20px;
            }

            /* CSS cho Modal */
            .phone {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.65);
                align-items: center;
                justify-content: center;
                padding: 24px;
            }
            .phone[style*="block"] {
                display: flex !important;
            }
            .phone-content {
                background-color: #2f2b24;
                color: #f7f3ea;
                margin: 0;
                padding: 28px;
                width: min(380px, 100%);
                border-radius: 18px;
                border: 1px solid rgba(216,173,90,.28);
                box-shadow: 0 30px 90px rgba(0,0,0,0.35);
                text-align: center;
            }
            .close-phone {
                float: right;
                font-size: 24px;
                cursor: pointer;
                color: #d8ad5a;
            }
            .phone-content h3 {
                margin: 12px 0 18px;
                color: #fff;
                font-size: 22px;
            }
            .phone-content .form-control {
                background: rgba(255,255,255,.08) !important;
                border-color: rgba(255,255,255,.18) !important;
                color: #fff !important;
                border-radius: 12px;
            }
            #phoneError{
                color: #ff9893;
                display: none;
            }

        </style>
    </head>
    <body>
        <form action="updateCustomerProfile" method="post" enctype="multipart/form-data">
            <div class="profile" style="box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ; ">
                <div class="info">
                    <h3>My Profile</h3>
                    <p class="text-muted">Manage your profile information to secure your account</p>

                    <div class="mb-3">
                        <label class="form-label">Email:</label>
                        <p>${sessionScope.customer.getEmail()}</p>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Name:</label>
                        <input type="text" class="form-control" name="fullname" value="${sessionScope.customer.getFullName()}" >
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Phone Number:</label>
                        <p>
                            ********<span id="phoneDisplay">${sessionScope.customer.getPhoneNumber() != null && sessionScope.customer.getPhoneNumber() != '' ? sessionScope.customer.getPhoneNumber().substring(sessionScope.customer.getPhoneNumber().length()-2) : '**'}</span> 
                            <a style="color: blue;" onclick="openModal()">Change</a>
                        </p>
                        <input id="phoneInput" name="phoneNumber" type="tel" value="${sessionScope.customer.getPhoneNumber()}" hidden>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Gender:</label>
                        <div>
                            <input type="radio" name="gender" value="Male" ${sessionScope.customer.getGender().trim().equalsIgnoreCase("Male") ? 'checked' : ''}> Male
                            <input type="radio" name="gender" value="Female" ${sessionScope.customer.getGender().trim().equalsIgnoreCase("Female") ? 'checked' : ''}> Female
                            <input type="radio" name="gender" value="Other" ${sessionScope.customer.getGender().trim().equalsIgnoreCase("Other") ? 'checked' : ''}> Other
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Date of Birth:</label>

                        <div class="row">
                            <div class="col">
                                <select class="form-select" name="day">
                                    <option>Day</option>
                                    <c:forEach var="i" begin="1" end="31">
                                        <option ${!sessionScope.customer.getBirthday().equals('') && sessionScope.customer.getBirthday().split("-")[2].equals(String.format("%02d", i)) ? 'selected' : ''}>${i}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col">
                                <select class="form-select" name="month">
                                    <option>Month</option>
                                    <c:forEach var="i" begin="1" end="12">
                                        <c:set var="formattedMonth" value="${String.format('%02d', i)}" />
                                        <option value="${formattedMonth}" ${!sessionScope.customer.getBirthday().equals('') && sessionScope.customer.getBirthday().split('-')[1] == formattedMonth ? 'selected' : ''}>${i}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col">
                                <select class="form-select" name="year">
                                    <option>Year</option>
                                    <c:forEach var="i" begin="1900" end="2024">
                                        <option ${!sessionScope.customer.getBirthday().equals('') && sessionScope.customer.getBirthday().split("-")[0].equals(String.valueOf(i)) ? 'selected' : ''}>${i}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="mb-3 avatar">
                    <label class="form-label">Avatar:</label>
                    <div class="d-block align-items-center">
                        <c:set var="customerAvatar" value="${sessionScope.customer.avatar}" />
                        <c:choose>
                            <c:when test="${not empty customerAvatar && fn:startsWith(customerAvatar, 'http')}">
                                <img id="avatarPreview" class="avatar-preview mb-3" src="${customerAvatar}" alt="Customer avatar" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                            </c:when>
                            <c:when test="${not empty customerAvatar}">
                                <img id="avatarPreview" class="avatar-preview mb-3" src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/${customerAvatar}" alt="Customer avatar" onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                            </c:when>
                            <c:otherwise>
                                <img id="avatarPreview" class="avatar-preview mb-3" src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg" alt="Customer avatar">
                            </c:otherwise>
                        </c:choose>

                        <br>
                        <input type="file" accept="image/*" class="form-control smart-file-input" name="avatar" onchange="previewImage(event)" >
                    </div>
                </div>
                <div class="d-flex justify-content-sm-between w-100">
                    <a href="viewCustomerProfile" class="btn btn-lg btn-warning">Cancel</a>
                    <button type="submit" class="btn btn-lg btn-primary">Save</button>
                </div>
            </div>
        </form>


        <!-- Phone Number Update Modal -->
        <div id="phoneModal" class="phone">
            <div class="phone-content">
                <span class="close-phone" onclick="closeModal()">&times;</span>
                <h3>Update Phone Number</h3>
                <input type="tel" id="newPhoneNumber" name="phoneNumber" class="form-control" placeholder="Enter new phone number" onchange="validatePhone()">

                <small id="phoneError">Invalid phone number!</small>
                <button onclick="updatePhone()" class="mt-2 btn btn-primary" id="saveButton" disabled>Save</button>
            </div>
        </div>

        <script>
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

                let phonePattern = /^0[2-9][0-9]{8}$/;  // Chỉ chấp nhận 10-11 số
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
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="./assets/js/file-input.js"></script>
        <script src="./assets/js/profile.js"></script>
    </body>
</html>
