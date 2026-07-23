package Controllers;

import DAOs.RevenueStatisticDAO;
import Models.RevenueStatistic;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RevenueStatisticServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        RevenueStatisticDAO dao=new RevenueStatisticDAO(); String period=request.getParameter("timePeriod"); if(period==null)period="month";
        ArrayList<RevenueStatistic> data="day".equals(period)?dao.getRevenueByDay():"year".equals(period)?dao.getRevenueByYear():dao.getRevenueByMonth();
        request.setAttribute("time",period);request.setAttribute("revenueData",data);request.setAttribute("listRevenueMen",dao.getRevenueByCategory("Men's Watches"));request.setAttribute("listRevenueWomen",dao.getRevenueByCategory("Women's Watches"));request.getRequestDispatcher("RevenueStatisticView.jsp").forward(request,response);
    }
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{doGet(request,response);}
}
