/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.util.Date;

/**
 * 
 * @author LamVH
 */
public class Brand {

    private int brandId;
    private String name;
    private boolean status;
    private Date createdDate;
    private int productCount;

    public Brand(int brandId, String name) {
        this.brandId = brandId;
        this.name = name;
        this.status = true;
    }

    public Brand(int brandId, String name, boolean status, Date createdDate, int productCount) {
        this.brandId = brandId;
        this.name = name;
        this.status = status;
        this.createdDate = createdDate;
        this.productCount = productCount;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
