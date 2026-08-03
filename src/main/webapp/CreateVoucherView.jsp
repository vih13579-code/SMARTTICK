<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create Voucher | SMARTTICK</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/flatpickr.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/themes/dark.css">
</head>
<body class="admin-ops-page voucher-admin-page">
    <jsp:include page="SidebarDashboard.jsp"/>

    <main class="content">
        <jsp:include page="HeaderDashboard.jsp"/>

        <div class="admin-page-title">
            <div>
                <h1>Create Voucher</h1>
                <p>Create a promotion with clear value and expiry conditions.</p>
            </div>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ViewVoucherListServlet">
                <i class="fa-solid fa-arrow-left"></i> Back to list
            </a>
        </div>

        <c:if test="${not empty formError}">
            <div class="alert alert-danger" role="alert">
                <i class="fa-solid fa-triangle-exclamation"></i>
                <c:out value="${formError}"/>
            </div>
        </c:if>

        <section class="voucher-form-wrapper">
            <form id="voucherForm" action="${pageContext.request.contextPath}/CreateVoucherServlet"
                  method="post" novalidate>
                <div class="voucher-form-grid">
                    <div class="voucher-field">
                        <label class="form-label" for="voucherCode">Voucher Code</label>
                        <input id="voucherCode" class="form-control ${not empty fieldErrors.voucherCode ? 'is-invalid' : ''}"
                               data-field="voucherCode" type="text" name="voucherCode"
                               value="<c:out value='${formVoucherCode}'/>"
                               minlength="3" maxlength="30" pattern="[A-Z0-9_-]{3,30}"
                               autocomplete="off" required aria-describedby="voucherCodeError">
                        <small class="field-hint">3-30 characters: A-Z, 0-9, underscore or hyphen.</small>
                        <div class="field-error" id="voucherCodeError"><c:out value="${fieldErrors.voucherCode}"/></div>
                    </div>

                    <div class="voucher-field">
                        <label class="form-label" for="voucherType">Type</label>
                        <select id="voucherType" class="form-select ${not empty fieldErrors.type ? 'is-invalid' : ''}"
                                data-field="type" name="type" required aria-describedby="typeError">
                            <option value="PERCENT" ${formType eq 'PERCENT' ? 'selected' : ''}>Percent (%)</option>
                            <option value="FIXED" ${formType eq 'FIXED' ? 'selected' : ''}>Fixed Amount</option>
                        </select>
                        <div class="field-error" id="typeError"><c:out value="${fieldErrors.type}"/></div>
                    </div>

                    <div class="voucher-field">
                        <label class="form-label" for="voucherValue">Value</label>
                        <input id="voucherValue" class="form-control ${not empty fieldErrors.value ? 'is-invalid' : ''}"
                               data-field="value" type="number" name="value"
                               value="<c:out value='${formValue}'/>"
                               min="${formType eq 'FIXED' ? '0.01' : '1'}" step="0.01"
                               max="${formType eq 'PERCENT' ? '100' : ''}"
                               inputmode="decimal" required
                               aria-describedby="valueError">
                        <small class="field-hint">PERCENT: enter a value from 1 to 100.</small>
                        <div class="field-error" id="valueError"><c:out value="${fieldErrors.value}"/></div>
                    </div>

                    <div class="voucher-field" id="maxDiscountGroup">
                        <label class="form-label" for="maxDiscount">Max Discount</label>
                        <input id="maxDiscount" class="form-control ${not empty fieldErrors.maxDiscount ? 'is-invalid' : ''}"
                               data-field="maxDiscount" type="number" name="maxDiscount"
                               value="<c:out value='${formMaxDiscount}'/>"
                               min="1000" step="1000" inputmode="numeric"
                               aria-describedby="maxDiscountError">
                        <small class="field-hint">PERCENT only: enter at least 1,000 VND in increments of 1,000.</small>
                        <div class="field-error" id="maxDiscountError"><c:out value="${fieldErrors.maxDiscount}"/></div>
                    </div>

                    <div class="voucher-field">
                        <label class="form-label" for="minOrderValue">Min Order Value</label>
                        <input id="minOrderValue" class="form-control ${not empty fieldErrors.minOrderValue ? 'is-invalid' : ''}"
                               data-field="minOrderValue" type="number" name="minOrderValue"
                               value="<c:out value='${formMinOrderValue}'/>"
                               min="1000" step="1000" inputmode="numeric" required
                               aria-describedby="minOrderValueError">
                        <small class="field-hint">Enter at least 1,000 VND in increments of 1,000.</small>
                        <div class="field-error" id="minOrderValueError"><c:out value="${fieldErrors.minOrderValue}"/></div>
                    </div>

                    <div class="voucher-field">
                        <label class="form-label" for="endDate">End Date</label>
                        <input id="endDate" class="form-control ${not empty fieldErrors.endDate ? 'is-invalid' : ''}"
                               data-field="endDate" type="datetime-local" name="endDate"
                               value="<c:out value='${formEndDate}'/>" required
                               aria-describedby="endDateError">
                        <small class="field-hint">Uses the application server's local timezone.</small>
                        <div class="field-error" id="endDateError"><c:out value="${fieldErrors.endDate}"/></div>
                    </div>
                </div>

                <div class="voucher-form-actions">
                    <button class="btn btn-primary" type="submit">
                        <i class="fa-solid fa-plus"></i> Create Voucher
                    </button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ViewVoucherListServlet">Cancel</a>
                </div>
            </form>
        </section>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/flatpickr@4.6.13/dist/flatpickr.min.js"></script>
    <script>
        flatpickr("#endDate", {
            enableTime: true,
            dateFormat: "Y-m-d\\TH:i",
            altInput: true,
            altFormat: "m/d/Y h:i K",
            disableMobile: true
        });
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/voucher-form.js?v=20260803-english-date"></script>
</body>
</html>
