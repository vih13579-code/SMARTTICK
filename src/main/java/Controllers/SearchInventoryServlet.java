package Controllers;

import DAOs.InventoryStatisticDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class SearchInventoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
        String keyword=request.getParameter("query");
        InventoryStatisticDAO dao=new InventoryStatisticDAO();
        request.setAttribute("listInventoryMen",dao.getInventoryByCategory("Đồng hồ nam"));
        request.setAttribute("listInventoryWomen",dao.getInventoryByCategory("Đồng hồ nữ"));
        request.setAttribute("listI",dao.searchInventory(keyword==null?"":keyword));
        request.setAttribute("searchQuery",keyword);
        request.getRequestDispatcher("InventoryStatisticView.jsp").forward(request,response);
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException { doGet(request,response); }
}
