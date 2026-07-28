package DAOs;

import DB.DBContext;
import Models.RevenueStatistic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RevenueStatisticDAO {
    public ArrayList<RevenueStatistic> getRevenueByCategory(String categoryName) {
        ArrayList<RevenueStatistic> list=new ArrayList<>();
        String sql="SELECT MONTH(o.OrderedDate),SUM(od.Quantity*od.Price) FROM Products p JOIN Categories c ON p.CategoryID=c.CategoryID JOIN OrderDetails od ON p.ProductID=od.ProductID JOIN Orders o ON od.OrderID=o.OrderID WHERE c.Name=? AND o.Status=4 GROUP BY MONTH(o.OrderedDate) ORDER BY 1";
        try(Connection connection=new DBContext().getConnection();PreparedStatement statement=connection.prepareStatement(sql)){
            statement.setString(1,categoryName); try(ResultSet rs=statement.executeQuery()){while(rs.next())list.add(new RevenueStatistic(rs.getInt(1),rs.getLong(2)));}
        }catch(SQLException ex){System.err.println("Category revenue query failed: "+ex.getMessage());} return list;
    }
    public ArrayList<RevenueStatistic> getRevenueByDay(){return query("SELECT CONVERT(DATE,o.OrderedDate),COUNT(DISTINCT o.OrderID),SUM(od.Quantity*od.Price),SUM(od.Quantity) FROM Orders o JOIN OrderDetails od ON o.OrderID=od.OrderID WHERE o.Status=4 GROUP BY CONVERT(DATE,o.OrderedDate) ORDER BY 1",0);}
    public ArrayList<RevenueStatistic> getRevenueByMonth(){return query("SELECT YEAR(o.OrderedDate),MONTH(o.OrderedDate),COUNT(DISTINCT o.OrderID),SUM(od.Quantity*od.Price),SUM(od.Quantity) FROM Orders o JOIN OrderDetails od ON o.OrderID=od.OrderID WHERE o.Status=4 GROUP BY YEAR(o.OrderedDate),MONTH(o.OrderedDate) ORDER BY 1,2",1);}
    public ArrayList<RevenueStatistic> getRevenueByYear(){return query("SELECT YEAR(o.OrderedDate),COUNT(DISTINCT o.OrderID),SUM(od.Quantity*od.Price),SUM(od.Quantity) FROM Orders o JOIN OrderDetails od ON o.OrderID=od.OrderID WHERE o.Status=4 GROUP BY YEAR(o.OrderedDate) ORDER BY 1",2);}
    private ArrayList<RevenueStatistic> query(String sql,int type){ArrayList<RevenueStatistic> list=new ArrayList<>();try(Connection connection=new DBContext().getConnection();PreparedStatement statement=connection.prepareStatement(sql);ResultSet rs=statement.executeQuery()){while(rs.next()){if(type==0)list.add(new RevenueStatistic(rs.getDate(1),rs.getInt(2),rs.getLong(3),rs.getInt(4)));else if(type==1)list.add(new RevenueStatistic(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getLong(4),rs.getInt(5)));else list.add(new RevenueStatistic(rs.getInt(1),rs.getInt(2),rs.getLong(3),rs.getInt(4)));}}catch(SQLException ex){System.err.println("Revenue query failed: "+ex.getMessage());}return list;}
}
