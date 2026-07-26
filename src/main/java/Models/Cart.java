/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;

public class Cart {

    private int productID;
    private int quantity;
    private String image;
    private String fullName;
    private long price;
    private int category;
    private int stock;
    private Integer variantId;
    private String colorName;
    private String colorHex;

    public Cart() {
    }

    public Cart(int productID, int quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }

    public Cart(int productID, int quantity, String image, String fullName, long price, int category) {
        this.productID = productID;
        this.quantity = quantity;
        this.image = image;
        this.fullName = fullName;
        this.price = price;
        this.category = category;
    }

    public Cart(int productID, int quantity, String image, String fullName, long price, int category, int stock) {
        this(productID, quantity, image, fullName, price, category);
        this.stock = stock;
    }

    public Cart(int productID, Integer variantId, int quantity, String image, String fullName, long price, int category,
            String colorName, String colorHex, int stock) {
        this.productID = productID;
        this.variantId = variantId;
        this.quantity = quantity;
        this.image = image;
        this.fullName = fullName;
        this.price = price;
        this.category = category;
        this.colorName = colorName;
        this.colorHex = colorHex;
        this.stock = stock;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

}
