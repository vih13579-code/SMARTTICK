/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class CustomerVoucher extends Voucher{

    private int customerID;
    private String expirationDate;
    private int quantity;

    public CustomerVoucher(int customerID, String expirationDate, int quantity) {
        this.customerID = customerID;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }

    public CustomerVoucher(int customerID, String expirationDate, int quantity, int voucherID, String voucherCode, int voucherValue, int voucherType, String startDate, String endDate, int usedCount, int maxUsedCount, int maxDiscountAmount, int minOrderValue, int status, String description) {
        super(voucherID, voucherCode, voucherValue, voucherType, startDate, endDate, usedCount, maxUsedCount, maxDiscountAmount, minOrderValue, status, description);
        this.customerID = customerID;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }


    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

   
}
