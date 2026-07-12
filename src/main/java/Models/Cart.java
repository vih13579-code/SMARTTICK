package Models;
import java.io.Serializable;
public class Cart implements Serializable {
    private int productId; private String productName; private long unitPrice; private int quantity; private int stock;
    public Cart() {}
    public Cart(int productId,String productName,long unitPrice,int quantity,int stock){this.productId=productId;this.productName=productName;this.unitPrice=unitPrice;this.quantity=quantity;this.stock=stock;}
    public int getProductId(){return productId;} public String getProductName(){return productName;} public long getUnitPrice(){return unitPrice;} public int getQuantity(){return quantity;} public int getStock(){return stock;}
    public void setQuantity(int quantity){this.quantity=quantity;} public long getSubtotal(){return unitPrice*quantity;}
}
