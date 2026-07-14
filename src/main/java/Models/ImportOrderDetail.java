/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Thuongnvce181966
 */

public class ImportOrderDetail {

    private int ioid;
    private Product product;
    private int quantity;
    private long importPrice;

    public ImportOrderDetail() {
    }

    public ImportOrderDetail(int ioid, Product product, int quantity, long importPrice) {
        this.ioid = ioid;
        this.product = product;
        this.quantity = quantity;
        this.importPrice = importPrice;
    }

    public int getIoid() {
        return ioid;
    }

    public void setIoid(int ioid) {
        this.ioid = ioid;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(long importPrice) {
        this.importPrice = importPrice;
    }

    public String getPriceFormatted() {
        Locale vietnam = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vietnam);
        return currencyFormatter.format(importPrice);
    }
}
