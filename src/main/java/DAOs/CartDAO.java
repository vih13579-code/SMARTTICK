package DAOs;

import DB.DBContext;
import Models.Cart;
import java.sql.*;
import java.util.*;
import javax.servlet.http.HttpSession;

public class CartDAO {
    private int customerId(HttpSession session) {
        Object id = session.getAttribute("customerId");
        return id instanceof Number ? ((Number) id).intValue() : 1;
    }

    public List<Cart> products() {
        String sql = "SELECT product_id, product_name, unit_price, stock FROM Products WHERE active=1 ORDER BY product_id";
        List<Cart> result = new ArrayList<>();
        try (Connection c = DBContext.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(map(rs, 0));
            return result;
        } catch (SQLException e) { throw dbError(e); }
    }

    public Map<Integer, Cart> get(HttpSession session) {
        String sql = "SELECT p.product_id,p.product_name,p.unit_price,p.stock,c.quantity FROM Carts c JOIN Products p ON p.product_id=c.product_id WHERE c.customer_id=? ORDER BY c.updated_at DESC";
        Map<Integer, Cart> result = new LinkedHashMap<>();
        try (Connection c = DBContext.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, customerId(session));
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) { Cart item=map(rs,rs.getInt("quantity")); result.put(item.getProductId(),item); } }
            return result;
        } catch (SQLException e) { throw dbError(e); }
    }

    public void add(HttpSession session, int productId) {
        String sql = "MERGE Carts AS t USING (SELECT ? customer_id, ? product_id) s ON t.customer_id=s.customer_id AND t.product_id=s.product_id " +
                "WHEN MATCHED AND t.quantity < (SELECT stock FROM Products WHERE product_id=s.product_id) THEN UPDATE SET quantity=t.quantity+1,updated_at=SYSDATETIME() " +
                "WHEN NOT MATCHED BY TARGET AND EXISTS(SELECT 1 FROM Products WHERE product_id=s.product_id AND active=1 AND stock>0) THEN INSERT(customer_id,product_id,quantity) VALUES(s.customer_id,s.product_id,1);";
        execute(sql, customerId(session), productId);
    }

    public void update(HttpSession session, int productId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("So luong phai lon hon 0");
        String sql = "UPDATE c SET quantity=?,updated_at=SYSDATETIME() FROM Carts c JOIN Products p ON p.product_id=c.product_id WHERE c.customer_id=? AND c.product_id=? AND ?<=p.stock";
        execute(sql, quantity, customerId(session), productId, quantity);
    }

    public void remove(HttpSession session, int productId) { execute("DELETE Carts WHERE customer_id=? AND product_id=?", customerId(session), productId); }
    public void clear(HttpSession session) { execute("DELETE Carts WHERE customer_id=?", customerId(session)); }

    private Cart map(ResultSet rs, int quantity) throws SQLException { return new Cart(rs.getInt("product_id"),rs.getString("product_name"),rs.getLong("unit_price"),quantity,rs.getInt("stock")); }
    private void execute(String sql,Object... values){try(Connection c=DBContext.getConnection();PreparedStatement ps=c.prepareStatement(sql)){for(int i=0;i<values.length;i++)ps.setObject(i+1,values[i]);int changed=ps.executeUpdate();if(changed==0)throw new IllegalArgumentException("Khong the cap nhat gio hang; kiem tra san pham va ton kho");}catch(SQLException e){throw dbError(e);}}
    private IllegalStateException dbError(SQLException e){return new IllegalStateException("Loi SQL Server: "+e.getMessage(),e);}
}
