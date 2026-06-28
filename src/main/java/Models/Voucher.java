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
public class Voucher {

    private int voucherID;
    private String voucherCode;
    private int voucherValue;
    private int voucherType;
    private String startDate;
    private String endDate;
    private int usedCount;
    private int maxUsedCount;
    private int maxDiscountAmount;
    private int minOrderValue;
    private int status;
    private String description;

    public Voucher() {
    }

    public Voucher(int voucherID, String voucherCode, int voucherValue, int voucherType, String startDate, String endDate, int usedCount, int maxUsedCount, int maxDiscountAmount, int minOrderValue, int status, String description) {
        this.voucherID = voucherID;
        this.voucherCode = voucherCode;
        this.voucherValue = voucherValue;
        this.voucherType = voucherType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usedCount = usedCount;
        this.maxUsedCount = maxUsedCount;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minOrderValue = minOrderValue;
        this.status = status;
        this.description = description;
    }

    public int getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(int voucherID) {
        this.voucherID = voucherID;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public int getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(int voucherValue) {
        this.voucherValue = voucherValue;
    }

    public int getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(int voucherType) {
        this.voucherType = voucherType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public int getMaxUsedCount() {
        return maxUsedCount;
    }

    public void setMaxUsedCount(int maxUsedCount) {
        this.maxUsedCount = maxUsedCount;
    }

    public int getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(int maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public int getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(int minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
