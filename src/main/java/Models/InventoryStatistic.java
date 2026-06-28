/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.sql.Date;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class InventoryStatistic {
    private String categoryName;
    private String brandName;
    private String model;
    private String fullName;
    private int stockQuantity;
    private String supplierName;
    private Date importDate;
    private long productImportPrice;
    private String modelName;

    public InventoryStatistic(String modelName, int stockQuantity) {
        this.stockQuantity = stockQuantity;
        this.modelName = modelName;
    }
    
    

    public InventoryStatistic(String categoryName, String brandName, String model, String fullName, int stockQuantity, String supplierName, Date importDate, long productImportPrice) {
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.model = model;
        this.fullName = fullName;
        this.stockQuantity = stockQuantity;
        this.supplierName = supplierName;
        this.importDate = importDate;
        this.productImportPrice = productImportPrice;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public long getProductImportPrice() {
        return productImportPrice;
    }

    public void setProductImportPrice(long productImportPrice) {
        this.productImportPrice = productImportPrice;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    
}
