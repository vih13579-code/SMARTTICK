/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

public class Order {

    private int orderID;
    private int accountID;
    private String fullName;
    private String phone;
    private String address;
    private long totalAmount;
    private String orderDate;
    private String deliveredDate;
    private int status;
    private int discount;
    public Order() {
    }

    public Order(String fullName, String phone, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    public Order(int accountID, String fullName, String phone, String address, long totalAmount) {
        this.accountID = accountID;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.totalAmount = totalAmount;
    }

    public Order(int orderID, int accountID, String fullName, String phone, String address, long totalAmount, String orderDate, int status) {
        this.orderID = orderID;
        this.accountID = accountID;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Order(int orderID, int accountID, String fullName, String address, String phone, String orderDate, String deliveredDate, int status, long totalAmount) {

        this.orderID = orderID;
        this.accountID = accountID;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;

        this.deliveredDate = deliveredDate;

        this.status = status;
    }

    public Order(int orderID, int accountID, String fullName, String phone, String address, long totalAmount, String orderDate, String deliveredDate, int status, int discount) {
        this.orderID = orderID;
        this.accountID = accountID;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.deliveredDate = deliveredDate;
        this.status = status;
        this.discount = discount;
    }

    public Order(int accountID, String fullName, String phone, String address, long totalAmount, int discount) {
        this.accountID = accountID;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.totalAmount = totalAmount;
        this.discount = discount;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(String deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}

