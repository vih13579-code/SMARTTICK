package Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CustomerVoucher extends Voucher {

    private int customerId;
    private LocalDateTime expirationDate;
    private int quantity;

    public CustomerVoucher() {
    }

    public CustomerVoucher(int customerId, LocalDateTime expirationDate, int quantity,
            int voucherId, String voucherCode, String type, BigDecimal value,
            BigDecimal maxDiscount, BigDecimal minOrderValue, LocalDateTime endDate) {
        super(voucherId, voucherCode, type, value, maxDiscount, minOrderValue, endDate);
        this.customerId = customerId;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
