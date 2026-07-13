/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * 
 */ @author LamVH
package Models;

import java.util.Date;

public class Category {

    private int categoryId;
    private String name;
    private boolean status;
    private Date createdDate;
    private int productCount;

    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
        this.status = true;
    }

    public Category(int categoryId, String name, boolean status, Date createdDate, int productCount) {
        this.categoryId = categoryId;
        this.name = name;
        this.status = status;
        this.createdDate = createdDate;
        this.productCount = productCount;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
}
