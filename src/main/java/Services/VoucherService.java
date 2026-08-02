package Services;

import Models.Voucher;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class VoucherService {

    public static final String TYPE_PERCENT = "PERCENT";
    public static final String TYPE_FIXED = "FIXED";

    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z0-9_-]{3,30}$");
    private static final Pattern DECIMAL_PATTERN =
            Pattern.compile("^(?:0|[1-9][0-9]{0,15})(?:\\.[0-9]{1,2})?$");
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal ONE_THOUSAND = new BigDecimal("1000");

    public ValidationResult validateForWrite(String rawCode, String rawType, String rawValue,
            String rawMaxDiscount, String rawMinOrderValue, String rawEndDate,
            LocalDateTime now) {
        Map<String, String> errors = new LinkedHashMap<>();
        String voucherCode = normalizeCode(rawCode);
        String type = clean(rawType).toUpperCase(Locale.ROOT);

        if (voucherCode.isEmpty()) {
            errors.put("voucherCode", "Voucher Code is required.");
        } else if (!CODE_PATTERN.matcher(voucherCode).matches()) {
            errors.put("voucherCode",
                    "Voucher Code must be 3-30 characters and contain only A-Z, "
                    + "0-9, underscores, or hyphens.");
        }

        BigDecimal value = parseDecimal(rawValue, "value", "Value", true, errors);
        BigDecimal minOrderValue =
                parseDecimal(rawMinOrderValue, "minOrderValue", "Min Order Value", true, errors);

        LocalDateTime endDate = null;
        if (clean(rawEndDate).isEmpty()) {
            errors.put("endDate", "End Date is required.");
        } else {
            try {
                endDate = LocalDateTime.parse(clean(rawEndDate));
            } catch (DateTimeParseException ex) {
                errors.put("endDate", "End Date must be a valid date and time.");
            }
        }

        if (!TYPE_PERCENT.equals(type) && !TYPE_FIXED.equals(type)) {
            errors.put("type", "Type must be either PERCENT or FIXED.");
        }

        if (value != null) {
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                errors.put("value", "Value must be greater than 0.");
            } else if (TYPE_PERCENT.equals(type)
                    && (value.compareTo(ONE) < 0 || value.compareTo(ONE_HUNDRED) > 0)) {
                errors.put("value", "PERCENT Value must be between 1 and 100.");
            }
        }

        BigDecimal maxDiscount = null;
        if (TYPE_PERCENT.equals(type)) {
            maxDiscount =
                    parseDecimal(rawMaxDiscount, "maxDiscount", "Max Discount", true, errors);
            if (maxDiscount != null && !isWholeThousandAmount(maxDiscount)) {
                errors.put("maxDiscount",
                        "Max Discount must be a whole VND amount of at least 1,000 "
                        + "and a multiple of 1,000.");
            }
        }

        if (minOrderValue != null && !isWholeThousandAmount(minOrderValue)) {
            errors.put("minOrderValue",
                    "Min Order Value must be a whole VND amount of at least 1,000 "
                    + "and a multiple of 1,000.");
        }
        if (TYPE_FIXED.equals(type) && value != null && minOrderValue != null
                && value.compareTo(BigDecimal.ZERO) > 0
                && !errors.containsKey("minOrderValue")
                && minOrderValue.compareTo(value) < 0) {
            errors.put("minOrderValue",
                    "For a FIXED voucher, Min Order Value must be greater than or equal to Value.");
        }

        if (endDate != null && !endDate.isAfter(now)) {
            errors.put("endDate", "End Date must be later than the current server time.");
        }

        Voucher voucher = errors.isEmpty()
                ? new Voucher(0, voucherCode, type, value, maxDiscount, minOrderValue, endDate)
                : null;
        return new ValidationResult(voucher, errors, voucherCode, type,
                clean(rawValue), TYPE_FIXED.equals(type) ? "" : clean(rawMaxDiscount),
                clean(rawMinOrderValue), clean(rawEndDate));
    }

    public BigDecimal calculateDiscount(Voucher voucher, BigDecimal orderTotal,
            LocalDateTime now) throws VoucherApplicationException {
        if (voucher == null) {
            throw new VoucherApplicationException("Voucher does not exist.");
        }
        if (orderTotal == null || orderTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new VoucherApplicationException("Voucher data is invalid.");
        }
        if (voucher.getEndDate() == null || now.isAfter(voucher.getEndDate())) {
            throw new VoucherApplicationException("Voucher has expired.");
        }
        if (voucher.getMinOrderValue() == null
                || orderTotal.compareTo(voucher.getMinOrderValue()) < 0) {
            throw new VoucherApplicationException(
                    "The order does not meet the minimum value required for this voucher.");
        }
        validateStoredVoucher(voucher);

        BigDecimal discount;
        if (TYPE_PERCENT.equals(voucher.getType())) {
            discount = orderTotal.multiply(voucher.getValue())
                    .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP)
                    .min(voucher.getMaxDiscount());
        } else {
            discount = voucher.getValue();
        }
        return discount.min(orderTotal).setScale(2, RoundingMode.HALF_UP);
    }

    public long toWholeVnd(BigDecimal amount) throws VoucherApplicationException {
        try {
            return amount.setScale(0, RoundingMode.HALF_UP).longValueExact();
        } catch (ArithmeticException ex) {
            throw new VoucherApplicationException("Voucher data is invalid.");
        }
    }

    public String normalizeCode(String rawCode) {
        return clean(rawCode).toUpperCase(Locale.ROOT);
    }

    private void validateStoredVoucher(Voucher voucher) throws VoucherApplicationException {
        String type = voucher.getType();
        BigDecimal value = voucher.getValue();
        BigDecimal maxDiscount = voucher.getMaxDiscount();
        BigDecimal minOrder = voucher.getMinOrderValue();

        boolean valid = CODE_PATTERN.matcher(normalizeCode(voucher.getVoucherCode())).matches()
                && (TYPE_PERCENT.equals(type) || TYPE_FIXED.equals(type))
                && value != null && value.compareTo(BigDecimal.ZERO) > 0
                && isWholeThousandAmount(minOrder);
        if (TYPE_PERCENT.equals(type)) {
            valid = valid && value.compareTo(ONE) >= 0
                    && value.compareTo(ONE_HUNDRED) <= 0
                    && isWholeThousandAmount(maxDiscount);
        } else {
            valid = valid && maxDiscount == null && minOrder.compareTo(value) >= 0;
        }
        if (!valid) {
            throw new VoucherApplicationException("Voucher data is invalid.");
        }
    }

    private boolean isWholeThousandAmount(BigDecimal amount) {
        return amount != null
                && amount.compareTo(ONE_THOUSAND) >= 0
                && amount.stripTrailingZeros().scale() <= 0
                && amount.remainder(ONE_THOUSAND).compareTo(BigDecimal.ZERO) == 0;
    }

    private BigDecimal parseDecimal(String raw, String field, String label, boolean required,
            Map<String, String> errors) {
        String value = clean(raw);
        if (value.isEmpty()) {
            if (required) {
                errors.put(field, label + " is required.");
            }
            return null;
        }
        if (!DECIMAL_PATTERN.matcher(value).matches()) {
            errors.put(field,
                    label + " must be a number using a dot as the decimal separator, "
                    + "with at most 16 integer digits and 2 decimal places.");
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            errors.put(field, label + " must be a valid number.");
            return null;
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public static final class ValidationResult {

        private final Voucher voucher;
        private final Map<String, String> errors;
        private final String voucherCode;
        private final String type;
        private final String value;
        private final String maxDiscount;
        private final String minOrderValue;
        private final String endDate;

        private ValidationResult(Voucher voucher, Map<String, String> errors,
                String voucherCode, String type, String value, String maxDiscount,
                String minOrderValue, String endDate) {
            this.voucher = voucher;
            this.errors = Collections.unmodifiableMap(new LinkedHashMap<>(errors));
            this.voucherCode = voucherCode;
            this.type = type;
            this.value = value;
            this.maxDiscount = maxDiscount;
            this.minOrderValue = minOrderValue;
            this.endDate = endDate;
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public Voucher getVoucher() {
            return voucher;
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public String getVoucherCode() {
            return voucherCode;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public String getMaxDiscount() {
            return maxDiscount;
        }

        public String getMinOrderValue() {
            return minOrderValue;
        }

        public String getEndDate() {
            return endDate;
        }
    }

    public static class VoucherApplicationException extends Exception {

        public VoucherApplicationException(String message) {
            super(message);
        }
    }
}
