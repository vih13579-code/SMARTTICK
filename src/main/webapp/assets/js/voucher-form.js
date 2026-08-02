(function () {
    "use strict";

    const form = document.getElementById("voucherForm");
    if (!form) {
        return;
    }

    const code = document.getElementById("voucherCode");
    const type = document.getElementById("voucherType");
    const value = document.getElementById("voucherValue");
    const maxGroup = document.getElementById("maxDiscountGroup");
    const maxDiscount = document.getElementById("maxDiscount");
    const minOrderValue = document.getElementById("minOrderValue");
    const endDate = document.getElementById("endDate");
    const decimalPattern = /^(?:0|[1-9][0-9]{0,15})(?:\.[0-9]{1,2})?$/;
    const wholeVndPattern = /^[1-9][0-9]{0,15}(?:\.0{1,2})?$/;
    const codePattern = /^[A-Z0-9_-]{3,30}$/;

    function errorElement(field) {
        return document.getElementById(field + "Error");
    }

    function setError(field, message) {
        const element = errorElement(field);
        const input = document.querySelector('[data-field="' + field + '"]');
        if (element) {
            element.textContent = message || "";
        }
        if (input) {
            input.classList.toggle("is-invalid", Boolean(message));
            input.setAttribute("aria-invalid", message ? "true" : "false");
        }
    }

    function decimal(input, field, label, allowZero) {
        const raw = input.value.trim();
        if (!raw) {
            setError(field, label + " is required.");
            return null;
        }
        if (!decimalPattern.test(raw)) {
            setError(field,
                    label + " must use a dot as the decimal separator "
                    + "and have at most 2 decimal places.");
            return null;
        }
        const number = Number(raw);
        if (!Number.isFinite(number) || (allowZero ? number < 0 : number <= 0)) {
            setError(field, allowZero
                    ? label + " cannot be negative."
                    : label + " must be greater than 0.");
            return null;
        }
        setError(field, "");
        return number;
    }

    function wholeThousandVnd(input, field, label) {
        const raw = input.value.trim();
        if (!raw) {
            setError(field, label + " is required.");
            return null;
        }
        const integerPart = raw.split(".")[0];
        if (!wholeVndPattern.test(raw)
                || integerPart.length < 4
                || !integerPart.endsWith("000")) {
            setError(field,
                    label + " must be a whole VND amount of at least 1,000 "
                    + "and a multiple of 1,000.");
            return null;
        }
        setError(field, "");
        return Number(integerPart);
    }

    function updateMaxDiscount() {
        const percent = type.value === "PERCENT";
        value.max = percent ? "100" : "";
        value.min = percent ? "1" : "0.01";
        maxGroup.hidden = !percent;
        maxDiscount.disabled = !percent;
        maxDiscount.required = percent;
        if (!percent) {
            maxDiscount.value = "";
            setError("maxDiscount", "");
        }
        setError("type", "");
    }

    code.addEventListener("input", function () {
        const start = code.selectionStart;
        code.value = code.value.toUpperCase();
        if (start !== null) {
            code.setSelectionRange(start, start);
        }
        setError("voucherCode", "");
    });
    type.addEventListener("change", updateMaxDiscount);

    [value, maxDiscount, minOrderValue, endDate].forEach(function (input) {
        input.addEventListener("input", function () {
            setError(input.dataset.field, "");
        });
    });

    form.addEventListener("submit", function (event) {
        let valid = true;
        code.value = code.value.trim().toUpperCase();

        if (!code.value) {
            setError("voucherCode", "Voucher Code is required.");
            valid = false;
        } else if (!codePattern.test(code.value)) {
            setError("voucherCode",
                    "Voucher Code must be 3-30 characters and contain only "
                    + "A-Z, 0-9, underscores, or hyphens.");
            valid = false;
        } else {
            setError("voucherCode", "");
        }

        if (type.value !== "PERCENT" && type.value !== "FIXED") {
            setError("type", "Type must be either PERCENT or FIXED.");
            valid = false;
        } else {
            setError("type", "");
        }

        const parsedValue = decimal(value, "value", "Value", false);
        if (parsedValue === null) {
            valid = false;
        } else if (type.value === "PERCENT"
                && (parsedValue < 1 || parsedValue > 100)) {
            setError("value", "PERCENT Value must be between 1 and 100.");
            valid = false;
        }

        if (type.value === "PERCENT"
                && wholeThousandVnd(
                        maxDiscount, "maxDiscount", "Max Discount") === null) {
            valid = false;
        }

        const parsedMinOrder = wholeThousandVnd(
                minOrderValue, "minOrderValue", "Min Order Value");
        if (parsedMinOrder === null) {
            valid = false;
        } else if (type.value === "FIXED" && parsedValue !== null
                && parsedMinOrder < parsedValue) {
            setError("minOrderValue",
                    "For a FIXED voucher, Min Order Value must be greater than "
                    + "or equal to Value.");
            valid = false;
        }

        if (!endDate.value) {
            setError("endDate", "End Date is required.");
            valid = false;
        } else {
            const parsedEndDate = new Date(endDate.value);
            if (Number.isNaN(parsedEndDate.getTime())) {
                setError("endDate", "End Date must be a valid date and time.");
                valid = false;
            } else if (parsedEndDate.getTime() <= Date.now()) {
                setError("endDate", "End Date must be later than the current time.");
                valid = false;
            } else {
                setError("endDate", "");
            }
        }

        if (!valid) {
            event.preventDefault();
            const firstInvalid = form.querySelector(".is-invalid");
            if (firstInvalid) {
                firstInvalid.focus();
            }
        }
    });

    const localNow = new Date(Date.now() - new Date().getTimezoneOffset() * 60000)
            .toISOString().slice(0, 16);
    endDate.min = localNow;
    updateMaxDiscount();
}());
