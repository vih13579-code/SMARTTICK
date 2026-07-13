/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.ProductRatingDAO;
import DAOs.RatingRepliesDAO;
import Models.RatingReplies;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for deleting an employee reply to customer feedback.
 * It also updates the feedback status after the reply is removed.
 *
 * @author TrucBQCE181355
 */
public class DeleteReplyServlet extends HttpServlet {

    /**
     * Processes requests for both GET and POST methods.
     * This servlet only supports the POST method.
     *
     * @param request HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input or output error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    /**
     * Handles the HTTP GET request.
     * GET requests are not supported by this servlet.
     *
     * @param request HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP POST request.
     * Deletes the selected reply, updates the feedback status,
     * and redirects back to the feedback detail page.
     *
     * @param request HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get reply ID from request
        String replyID = request.getParameter("replyID");

        try {

            // Convert reply ID to integer
            int id = Integer.parseInt(replyID);

            // Create DAO objects
            ProductRatingDAO prDAO = new ProductRatingDAO();
            RatingRepliesDAO rrDAO = new RatingRepliesDAO();

            // Retrieve reply information
            RatingReplies r = rrDAO.getReplyByRepyID(id);

            // Update feedback status
            prDAO.markReplyAsUnRead(id);

            // Delete the reply
            boolean isdelete = rrDAO.DeleteRatingReply(id);

            // Redirect based on deletion result
            if (isdelete) {
                response.sendRedirect("ViewFeedbackForManagerServlet?rateID="
                        + r.getRateID() + "&success=success");
            } else {
                response.sendRedirect("ViewFeedbackForManagerServlet?rateID="
                        + r.getRateID() + "&success=failed");
            }

        } catch (Exception e) {
            // Exception is intentionally ignored
        }
    }

    /**
     * Returns a brief description of this servlet.
     *
     * @return servlet description
     */
    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }
}