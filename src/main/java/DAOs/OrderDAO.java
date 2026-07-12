package DAOs;

import DB.DBContext;
import Models.*;
import java.sql.*;
import java.util.*;

public class OrderDAO {
    public static final List<String> STATUSES=Arrays.asList("Cho xac nhan","Dang xu ly","Dang giao","Hoan thanh","Da huy");

    public Order create(String customerName, Collection<Cart> ignored) {
        int customerId=1;
        String insertOrder="INSERT Orders(customer_id,customer_name,status) OUTPUT INSERTED.order_id VALUES(?,?,N'Cho xac nhan')";
        String cartSql="SELECT p.product_id,p.product_name,p.unit_price,p.stock,c.quantity FROM Carts c JOIN Products p WITH(UPDLOCK,ROWLOCK) ON p.product_id=c.product_id WHERE c.customer_id=?";
        try(Connection c=DBContext.getConnection()){
            c.setAutoCommit(false);
            try{
                List<Cart> cart=new ArrayList<>();
                try(PreparedStatement ps=c.prepareStatement(cartSql)){ps.setInt(1,customerId);try(ResultSet rs=ps.executeQuery()){while(rs.next()){int q=rs.getInt("quantity");if(q>rs.getInt("stock"))throw new IllegalArgumentException("Khong du ton kho: "+rs.getString("product_name"));cart.add(new Cart(rs.getInt("product_id"),rs.getString("product_name"),rs.getLong("unit_price"),q,rs.getInt("stock")));}}}
                if(cart.isEmpty())throw new IllegalArgumentException("Gio hang dang trong");
                int orderId;
                try(PreparedStatement ps=c.prepareStatement(insertOrder)){ps.setInt(1,customerId);ps.setString(2,customerName);try(ResultSet rs=ps.executeQuery()){rs.next();orderId=rs.getInt(1);}}
                try(PreparedStatement detail=c.prepareStatement("INSERT OrderDetails(order_id,product_id,product_name,unit_price,quantity) VALUES(?,?,?,?,?)");PreparedStatement stock=c.prepareStatement("UPDATE Products SET stock=stock-? WHERE product_id=? AND stock>=?")){
                    for(Cart item:cart){detail.setInt(1,orderId);detail.setInt(2,item.getProductId());detail.setString(3,item.getProductName());detail.setLong(4,item.getUnitPrice());detail.setInt(5,item.getQuantity());detail.addBatch();stock.setInt(1,item.getQuantity());stock.setInt(2,item.getProductId());stock.setInt(3,item.getQuantity());stock.addBatch();}detail.executeBatch();int[] changed=stock.executeBatch();for(int x:changed)if(x==0)throw new IllegalArgumentException("Ton kho vua thay doi, vui long thu lai");
                }
                try(PreparedStatement ps=c.prepareStatement("DELETE Carts WHERE customer_id=?")){ps.setInt(1,customerId);ps.executeUpdate();}
                c.commit(); return findWithConnection(c,orderId);
            }catch(SQLException e){c.rollback();throw e;}catch(RuntimeException e){c.rollback();throw e;}
        }catch(SQLException e){throw dbError(e);}
    }

    public Order find(int id){try(Connection c=DBContext.getConnection()){return findWithConnection(c,id);}catch(SQLException e){throw dbError(e);}}
    private Order findWithConnection(Connection c,int id)throws SQLException{
        Order order=null;try(PreparedStatement ps=c.prepareStatement("SELECT order_id,customer_name,created_at,status FROM Orders WHERE order_id=?")){ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){if(rs.next())order=new Order(rs.getInt("order_id"),rs.getString("customer_name"),rs.getTimestamp("created_at").toLocalDateTime(),rs.getString("status"),new ArrayList<OrderDetail>());}}
        if(order==null)throw new IllegalArgumentException("Khong tim thay don hang");order.getDetails().addAll(details(c,id));return order;
    }
    public List<Order> all(){return search("","");}
    public List<Order> search(String q,String status){String key=q==null?"":q.trim();String st=status==null?"":status.trim();String sql="SELECT order_id,customer_name,created_at,status FROM Orders WHERE (?='' OR CONVERT(varchar(20),order_id) LIKE ? OR LOWER(customer_name) LIKE LOWER(?)) AND (?='' OR status=?) ORDER BY created_at DESC";List<Order> out=new ArrayList<>();try(Connection c=DBContext.getConnection();PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,key);ps.setString(2,"%"+key+"%");ps.setString(3,"%"+key+"%");ps.setString(4,st);ps.setString(5,st);try(ResultSet rs=ps.executeQuery()){while(rs.next()){int id=rs.getInt("order_id");out.add(new Order(id,rs.getString("customer_name"),rs.getTimestamp("created_at").toLocalDateTime(),rs.getString("status"),details(c,id)));}}return out;}catch(SQLException e){throw dbError(e);}}
    public void cancel(int id){execute("UPDATE Orders SET status=N'Da huy' WHERE order_id=? AND status=N'Cho xac nhan'",id);}
    public void updateStatus(int id,String status){if(!STATUSES.contains(status))throw new IllegalArgumentException("Trang thai khong hop le");execute("UPDATE Orders SET status=? WHERE order_id=? AND (status<>N'Da huy' OR ?=N'Da huy')",status,id,status);}
    private List<OrderDetail> details(Connection c,int id)throws SQLException{List<OrderDetail>d=new ArrayList<>();try(PreparedStatement ps=c.prepareStatement("SELECT product_id,product_name,unit_price,quantity FROM OrderDetails WHERE order_id=?")){ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){while(rs.next())d.add(new OrderDetail(rs.getInt("product_id"),rs.getString("product_name"),rs.getLong("unit_price"),rs.getInt("quantity")));}}return d;}
    private void execute(String sql,Object...v){try(Connection c=DBContext.getConnection();PreparedStatement ps=c.prepareStatement(sql)){for(int i=0;i<v.length;i++)ps.setObject(i+1,v[i]);if(ps.executeUpdate()==0)throw new IllegalArgumentException("Don hang khong ton tai hoac trang thai khong cho phep");}catch(SQLException e){throw dbError(e);}}
    private IllegalStateException dbError(SQLException e){return new IllegalStateException("Loi SQL Server: "+e.getMessage(),e);}
}
