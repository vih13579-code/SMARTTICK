package DAOs;
import Models.*; import java.time.LocalDateTime; import java.util.*; import java.util.concurrent.atomic.AtomicInteger; import java.util.stream.Collectors;
public class OrderDAO {
    public static final List<String> STATUSES=Arrays.asList("Cho xac nhan","Dang xu ly","Dang giao","Hoan thanh","Da huy");
    private static final Map<Integer,Order> DATA=new LinkedHashMap<>(); private static final AtomicInteger SEQ=new AtomicInteger(1002);
    static {DATA.put(1001,new Order(1001,"Le The Vinh",LocalDateTime.now().minusDays(5),"Hoan thanh",Arrays.asList(new OrderDetail(3,"Dong ho Minimal Steel",1890000,1))));DATA.put(1002,new Order(1002,"Le The Vinh",LocalDateTime.now().minusHours(3),"Cho xac nhan",Arrays.asList(new OrderDetail(2,"Dong ho Ocean Blue",3190000,1))));}
    public synchronized Order create(String customer,Collection<Cart> cart){if(cart.isEmpty())throw new IllegalArgumentException("Gio hang dang trong");List<OrderDetail>d=cart.stream().map(c->new OrderDetail(c.getProductId(),c.getProductName(),c.getUnitPrice(),c.getQuantity())).collect(Collectors.toList());Order o=new Order(SEQ.incrementAndGet(),customer,LocalDateTime.now(),"Cho xac nhan",d);DATA.put(o.getId(),o);return o;}
    public synchronized Order find(int id){Order o=DATA.get(id);if(o==null)throw new IllegalArgumentException("Khong tim thay don hang");return o;}
    public synchronized List<Order> all(){List<Order>r=new ArrayList<>(DATA.values());r.sort((a,b)->b.getCreatedAt().compareTo(a.getCreatedAt()));return r;}
    public synchronized List<Order> search(String q,String status){String k=q==null?"":q.toLowerCase();return all().stream().filter(o->(k.isEmpty()||String.valueOf(o.getId()).contains(k)||o.getCustomerName().toLowerCase().contains(k))&&(status==null||status.isEmpty()||o.getStatus().equals(status))).collect(Collectors.toList());}
    public synchronized void cancel(int id){Order o=find(id);if(!o.isCancelable())throw new IllegalArgumentException("Chi huy duoc don cho xac nhan");o.setStatus("Da huy");}
    public synchronized void updateStatus(int id,String status){if(!STATUSES.contains(status))throw new IllegalArgumentException("Trang thai khong hop le");Order o=find(id);if("Da huy".equals(o.getStatus())&&!"Da huy".equals(status))throw new IllegalArgumentException("Khong the mo lai don da huy");o.setStatus(status);}
}
