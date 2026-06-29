/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controllers;

import DAOs.CustomerDAO;
import DAOs.ProductDAO;
import DAOs.ProductRatingDAO;
import DAOs.RatingRepliesDAO;
import Models.Customer;
import Models.ProductRating;
import Models.Product;
import Models.RatingReplies;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author TrucBQCE181355
 */
public class ViewFeedbackForManagerServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
          int rateID =Integer.parseInt(request.getParameter("rateID"));
          String isOK = request.getParameter("isOk");
           
        ProductRatingDAO pDAO = new ProductRatingDAO();
        ProductRating productRating = pDAO.getProductRating(rateID);
        
        int productID = productRating.getProductID();
        

        RatingRepliesDAO rrDAO = new RatingRepliesDAO();
        List<RatingReplies> listReplies = rrDAO.getAllRatingRepliesByRateID(productRating.getRateID());
        
        ProductDAO pdDAO = new ProductDAO();
        Product  pro = pdDAO.getProductByID(productID);
      
        CustomerDAO cDAO = new CustomerDAO();
        Customer cus = cDAO.getCustomerById(productRating.getCustomerID());
        
                
       request.setAttribute("Product", pro);
       request.setAttribute("cus", cus);
        request.setAttribute("rate", productRating);
        request.setAttribute("dataReplies", listReplies);

        request.getRequestDispatcher("ViewNewFeedback.jsp").forward(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns the servlet description.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }// </editor-fold>

}
