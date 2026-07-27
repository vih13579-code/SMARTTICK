/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.OrderDetailDAO;
import DAOs.ProductRatingDAO;
import DAOs.RatingRepliesDAO;
import Models.Customer;
import Models.Employee;
import Models.ProductRating;
import Models.RatingReplies;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import javax.mail.Session;

public class CommentServlet extends HttpServlet {

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


//
//        try {
//            String detailID = request.getParameter("productId");
//            ProductRatingDAO pDAO = new ProductRatingDAO();
//            int productId = Integer.parseInt(detailID);
//
//            boolean isOk = false; //Kiem tra xem da mua chua
//
//            List<ProductRating> listPro = pDAO.getAllProductRating(productId);
//
//            RatingRepliesDAO rrDAO = new RatingRepliesDAO();
//            List<RatingReplies> listReplies = rrDAO.getAllRatingRepliesByProduct(productId);
//            HttpSession session = request.getSession();
//            Customer cus = (Customer) session.getAttribute("customer");
//
//            OrderDetailDAO odDAO = new OrderDetailDAO();
//            List<Integer> list = odDAO.getCustomerByProductID(productId);
//
//            if (cus != null) {
//                isOk = list.contains(cus.getId());
//            }
//            request.setAttribute("productId", productId);
//            request.setAttribute("isOk", isOk);
//            request.setAttribute("dataRating", listPro);
//            request.setAttribute("dataReplies", listReplies);
//            for(ProductRating p : listPro){
//                System.out.println(p.isIsDeleted());
//                System.out.println(p.getComment());
//            }
//            request.getRequestDispatcher("viewFeedback.jsp").forward(request, response);
//        } catch (NumberFormatException e) {
//            System.out.println(e);
//        }


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

//        HttpSession session = request.getSession();
//        Customer cus = (Customer) session.getAttribute("customer");
//        int customerId = cus.getId();
//        
//        int productId = Integer.parseInt(request.getParameter("productId"));
//        int star = Integer.parseInt(request.getParameter("star"));
//        String comment = request.getParameter("comment");
//
//        ProductRatingDAO pDAO = new ProductRatingDAO();
//        pDAO.addProductRating(customerId, productId, star, comment);
//
//        response.sendRedirect("CommentServlet?productId=" + productId);

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
