package Models;
import java.io.Serializable;
public class OrderDetail implements Serializable {
    private int productId; private String productName; private long unitPrice; private int quantity;
    public OrderDetail(){}
    public OrderDetail(int productId,String productName,long unitPrice,int quantity){this.productId=productId;this.productName=productName;this.unitPrice=unitPrice;this.quantity=quantity;}
    public int getProductId(){return productId;} public String getProductName(){return productName;} public long getUnitPrice(){return unitPrice;} public int getQuantity(){return quantity;} public long getSubtotal(){return unitPrice*quantity;}
}
