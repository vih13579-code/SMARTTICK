/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

public class Statistic {
    private String productName;
    private int soldQuantity;
    private String customerName;
    private String customerEmail;
    private int successfulOrders;
    private String modelName;
    private int stockQuantity;
 

 
   
    public Statistic(String customerName, String customerEmail, int successfulOrders) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.successfulOrders = successfulOrders;
    }

 

    public Statistic(String productName, int soldQuantity) {
        this.productName = productName;
        this.soldQuantity = soldQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public int getSuccessfulOrders() {
        return successfulOrders;
    }

    public void setSuccessfulOrders(int successfulOrders) {
        this.successfulOrders = successfulOrders;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }    
    
}
