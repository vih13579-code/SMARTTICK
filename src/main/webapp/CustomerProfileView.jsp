<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
    <head>
        <title>My Profile</title>
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
            .readonly-select {
                pointer-events: none;
                background-color: #e9ecef;
            }

        </style>
    </head>
    <body >
        <div class="profile customer-profile-card" style="box-shadow: 2px 2px 2px 2px lightgray; border-radius: 10px ; ">
            <div class="info">
                <h3>My Profile</h3>
                <p class="text-muted">Manage your profile information to secure your account</p>

                <div class="mb-3">
                    <label class="form-label">Email:</label>
                    <p>${sessionScope.customer.getEmail()}</p>
                </div>

                <div class="mb-3">
                    <label class="form-label">Name:</label>
                    <input type="text" class="form-control" name="fullname" value="${sessionScope.customer.getFullName()}" readonly>
                </div>

                <div class="mb-3">
                    <label class="form-label">Phone Number:</label>
                    <p>
                        ********<span id="phoneDisplay">${sessionScope.customer.getPhoneNumber() != null && sessionScope.customer.getPhoneNumber() != '' ? sessionScope.customer.getPhoneNumber().substring(sessionScope.customer.getPhoneNumber().length()-2) : '**'}</span> 
                    </p>
                </div>

                <div class="mb-3">
                    <label class="form-label">Gender:</label>
                    <div>
                        <input disabled type="radio" name="gender" value="Male" ${sessionScope.customer.getGender().trim().equalsIgnoreCase("Male") ? 'checked' : ''}> Male
                        <input disabled type="radio" name="gender" value="Female" ${sessionScope.customer.getGender().trim().equalsIgnoreCase("Female") ? 'checked' : ''}> Female
                        <input disabled type="radio" name="gender" value="Other" ${sessionScope.customer.getGender().trim().equalsIgnoreCase("Other") ? 'checked' : ''}> Other
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label">Date of Birth:</label>

                    <div class="row">
                        <div class="col">
                            <select class="form-select readonly-select" name="day" readonly>
                                <option>Day</option>
                                <c:forEach var="i" begin="1" end="31">
                                    <option ${!sessionScope.customer.getBirthday().equals('') && sessionScope.customer.getBirthday().split("-")[2].equals(String.format("%02d", i)) ? 'selected' : ''}>${i}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col">
                            <select class="form-select readonly-select" name="month" readonly>
                                <option>Month</option>
                                <c:forEach var="i" begin="1" end="12">
                                    <c:set var="formattedMonth" value="${String.format('%02d', i)}" />
                                    <option value="${formattedMonth}" ${!sessionScope.customer.getBirthday().equals('') && sessionScope.customer.getBirthday().split('-')[1] == formattedMonth ? 'selected' : ''}>${i}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col">
                            <select class="form-select readonly-select" name="year" readonly>
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
                </div>
            </div>
            <a href="updateCustomerProfile" class="btn btn-danger">Update</a>
        </div>

        <script src="./assets/js/profile.js"></script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    </body>

</html>
