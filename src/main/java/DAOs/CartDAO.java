package DAOs;
import Models.Cart; import javax.servlet.http.HttpSession; import java.util.*;
public class CartDAO {
    private static final Map<Integer,Cart> PRODUCTS=new LinkedHashMap<>();
    static { PRODUCTS.put(1,new Cart(1,"Dong ho Classic 40",2490000,0,8)); PRODUCTS.put(2,new Cart(2,"Dong ho Ocean Blue",3190000,0,5)); PRODUCTS.put(3,new Cart(3,"Dong ho Minimal Steel",1890000,0,12)); }
    public List<Cart> products(){return new ArrayList<>(PRODUCTS.values());}
    @SuppressWarnings("unchecked") public Map<Integer,Cart> get(HttpSession s){Object v=s.getAttribute("vinhCart");if(v==null){v=new LinkedHashMap<Integer,Cart>();s.setAttribute("vinhCart",v);}return (Map<Integer,Cart>)v;}
    public void add(HttpSession s,int id){Cart p=PRODUCTS.get(id);if(p==null)throw new IllegalArgumentException("San pham khong ton tai");Map<Integer,Cart> c=get(s);int q=c.containsKey(id)?c.get(id).getQuantity()+1:1;update(s,id,q);}
    public void update(HttpSession s,int id,int q){Cart p=PRODUCTS.get(id);if(p==null||q<1||q>p.getStock())throw new IllegalArgumentException("So luong khong hop le");get(s).put(id,new Cart(p.getProductId(),p.getProductName(),p.getUnitPrice(),q,p.getStock()));}
    public void remove(HttpSession s,int id){get(s).remove(id);} public void clear(HttpSession s){get(s).clear();}
}
