package Controllers;

import DAOs.InventoryStatisticDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InventoryStatisticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        InventoryStatisticDAO dao = new InventoryStatisticDAO();
        request.setAttribute("listInventoryMen", dao.getInventoryByCategory("Men's Watches"));
        request.setAttribute("listInventoryWomen", dao.getInventoryByCategory("Women's Watches"));
        request.setAttribute("listI", dao.getAllInventory());
        request.getRequestDispatcher("InventoryStatisticView.jsp").forward(request,response);
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException { doGet(request,response); }
}
