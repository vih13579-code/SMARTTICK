<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="account-fragment profile-edit-fragment">
    <div class="account-fragment-head">
        <div>
            <span class="eyebrow">Personal information</span>
            <h2>Edit profile</h2>
            <p class="section-sub">Keep your contact information up to date.</p>
        </div>
    </div>

    <c:if test="${not empty profileError}">
        <div class="alert alert-danger"><c:out value="${profileError}"/></div>
    </c:if>

    <form class="profile-edit-form"
          action="${pageContext.request.contextPath}/updateCustomerProfile"
          method="post" enctype="multipart/form-data">
        <div class="profile-edit-fields">
            <div class="form-field">
                <label for="profileEmail">Email</label>
                <input id="profileEmail" type="email" class="form-control"
                       value="${fn:escapeXml(sessionScope.customer.email)}" readonly>
                <small class="form-hint">Your sign-in email cannot be changed here.</small>
            </div>

            <div class="form-field">
                <label for="fullName">Full name</label>
                <input id="fullName" type="text" class="form-control" name="fullname"
                       minlength="2" maxlength="100"
                       value="${fn:escapeXml(sessionScope.customer.fullName)}" required>
            </div>

            <div class="form-field">
                <label for="phoneNumber">Phone number</label>
                <input id="phoneNumber" name="phoneNumber" type="tel" class="form-control"
                       inputmode="numeric" maxlength="10" pattern="0[2-9][0-9]{8}"
                       value="${fn:escapeXml(sessionScope.customer.phoneNumber)}"
                       placeholder="Example: 0912345678"
                       aria-describedby="phoneHelp">
                <small id="phoneHelp" class="form-hint">
                    Enter 10 digits directly. No confirmation popup is required.
                </small>
            </div>

            <fieldset class="form-field profile-gender">
                <legend>Gender</legend>
                <label>
                    <input type="radio" name="gender" value="Male"
                           ${sessionScope.customer.gender eq 'Male' ? 'checked' : ''}> Male
                </label>
                <label>
                    <input type="radio" name="gender" value="Female"
                           ${sessionScope.customer.gender eq 'Female' ? 'checked' : ''}> Female
                </label>
                <label>
                    <input type="radio" name="gender" value="Other"
                           ${sessionScope.customer.gender eq 'Other' ? 'checked' : ''}> Other
                </label>
            </fieldset>

            <div class="form-field">
                <label for="birthday">Date of birth</label>
                <input id="birthday" type="date" class="form-control" name="birthday"
                       value="${fn:escapeXml(sessionScope.customer.birthday)}">
            </div>
        </div>

        <aside class="profile-avatar-editor">
            <span class="form-label">Avatar</span>
            <c:set var="customerAvatar" value="${sessionScope.customer.avatar}"/>
            <c:choose>
                <c:when test="${not empty customerAvatar && fn:startsWith(customerAvatar, 'http')}">
                    <img id="avatarPreview" class="profile-edit-avatar" src="${customerAvatar}"
                         alt="Customer avatar"
                         onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                </c:when>
                <c:when test="${not empty customerAvatar}">
                    <img id="avatarPreview" class="profile-edit-avatar"
                         src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/${customerAvatar}"
                         alt="Customer avatar"
                         onerror="this.src='${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg'">
                </c:when>
                <c:otherwise>
                    <img id="avatarPreview" class="profile-edit-avatar"
                         src="${pageContext.request.contextPath}/assets/imgs/CustomerAvatar/defaut.jpg"
                         alt="Customer avatar">
                </c:otherwise>
            </c:choose>
            <label class="avatar-upload" for="avatarInput">
                <i class="bi bi-camera"></i> Choose image
            </label>
            <input id="avatarInput" type="file" accept="image/jpeg,image/png,image/webp"
                   name="avatar" onchange="previewImage(event)">
            <small class="form-hint">JPG, PNG or WEBP. Maximum 10 MB.</small>
        </aside>

        <div class="profile-form-actions">
            <a href="${pageContext.request.contextPath}/viewCustomerProfile"
               class="btn btn-outline">Cancel</a>
            <button type="submit" class="btn btn-primary">Save changes</button>
        </div>
    </form>
</div>

<script src="${pageContext.request.contextPath}/assets/js/profile.js"></script>
<script>
    (function () {
        const birthday = document.getElementById("birthday");
        if (birthday) {
            birthday.max = new Date().toISOString().slice(0, 10);
        }
    }());
</script>
