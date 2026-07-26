package Models;

public class ProductVariant {
    private int variantId;
    private int productId;
    private String colorName;
    private String colorHex;
    private String image;
    private int stock;
    private boolean active;

    public ProductVariant() {
    }

    public ProductVariant(int variantId, int productId, String colorName, String colorHex, String image, int stock, boolean active) {
        this.variantId = variantId;
        this.productId = productId;
        this.colorName = colorName;
        this.colorHex = colorHex;
        this.image = image;
        this.stock = stock;
        this.active = active;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
