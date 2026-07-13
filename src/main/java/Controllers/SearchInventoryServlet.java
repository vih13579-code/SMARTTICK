package Controllers;

import DAOs.InventoryStatisticDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 
 * @author Thuongnvce181966
 */

public class SearchInventoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        String keyword=request.getParameter("query");
        InventoryStatisticDAO dao=new InventoryStatisticDAO();
        request.setAttribute("listInventoryMen",dao.getInventoryByCategory("Men's Watches"));
        request.setAttribute("listInventoryWomen",dao.getInventoryByCategory("Women's Watches"));
        request.setAttribute("listI",dao.searchInventory(keyword==null?"":keyword));
        request.setAttribute("searchQuery",keyword);
        request.getRequestDispatcher("InventoryStatisticView.jsp").forward(request,response);
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException { doGet(request,response); }
}
