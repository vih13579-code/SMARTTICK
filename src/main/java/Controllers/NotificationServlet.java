/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.ProductRatingDAO;
import DAOs.RatingRepliesDAO;
import Models.Customer;
import Models.ProductRating;
import Models.Product;
import Models.RatingReplies;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer cus = (Customer) session.getAttribute("customer");

        if (cus == null) {
            response.setContentType("application/json");
            response.getWriter().write("[]");
            return;
        }

        int customerID = cus.getId();
        RatingRepliesDAO rrDAO = new RatingRepliesDAO();

        ProductRatingDAO pdDAO = new ProductRatingDAO();

        List<RatingReplies> list = rrDAO.getCustomerReplies(customerID);
        List<Product> listpd = new ArrayList<>();
        for (RatingReplies r : list) {
            Product p = pdDAO.getProductID(r.getRateID());
            
            listpd.add(p);
        }
        Map<Object, Object> dataMap = new HashMap<>();
        dataMap.put("replies", list);        // Danh sách RatingReplies
        dataMap.put("product", listpd);
        // Nếu có yêu cầu AJAX (chuyển sang JSON)
        if (request.getParameter("ajax") != null && request.getParameter("ajax").equals("true")) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String json = new Gson().toJson(dataMap);
            response.getWriter().write(json);
            return;
        }

        request.setAttribute("replies", list);
        request.setAttribute("products", listpd);
        request.getRequestDispatcher("notification.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String repliesIDStr = request.getParameter("repliesID");

        if (repliesIDStr != null) {
            try {
                int repliesID = Integer.parseInt(repliesIDStr);
                RatingRepliesDAO rrDAO = new RatingRepliesDAO();
                rrDAO.markReplyAsRead(repliesID); 

                if ("true".equals(request.getParameter("ajax"))) {
                    response.getWriter().write("Success");
                } else {
                    response.sendRedirect(request.getContextPath() + "/NotificationServlet");
                }
            } catch (NumberFormatException e) {
                response.getWriter().write("Invalid ID");
            }
        } else {
            response.getWriter().write("No ID provided");
        }
    }

    /**
     * Returns the servlet description.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }// </editor-fold>

}
