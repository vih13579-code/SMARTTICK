package Models;
import java.io.Serializable; import java.time.LocalDateTime; import java.util.*;
public class Order implements Serializable {
    private int id; private String customerName; private LocalDateTime createdAt; private String status; private List<OrderDetail> details;
    public Order(){}
    public Order(int id,String customerName,LocalDateTime createdAt,String status,List<OrderDetail> details){this.id=id;this.customerName=customerName;this.createdAt=createdAt;this.status=status;this.details=new ArrayList<>(details);}
    public int getId(){return id;} public String getCustomerName(){return customerName;} public LocalDateTime getCreatedAt(){return createdAt;} public String getStatus(){return status;} public void setStatus(String s){status=s;} public List<OrderDetail> getDetails(){return details;} public long getTotal(){return details.stream().mapToLong(OrderDetail::getSubtotal).sum();} public boolean isCancelable(){return "Cho xac nhan".equals(status);}
}
