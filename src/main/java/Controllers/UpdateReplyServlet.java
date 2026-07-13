/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.RatingRepliesDAO;
import Models.RatingReplies;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for updating an existing reply to customer feedback.
 *
 * @author TrucBQCE181355
 */
public class UpdateReplyServlet extends HttpServlet {

    /**
     * Handles the HTTP POST request.
     * Updates the reply content and returns the operation result.
     *
     * @param request HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Create DAO object
        RatingRepliesDAO rrDAO = new RatingRepliesDAO();

        // Retrieve request parameters
        String answerUpdate = request.getParameter("answer");
        int replyID = Integer.parseInt(request.getParameter("replyID"));

        try {

            // Validate input data
            if (replyID > 0 && answerUpdate != null && !answerUpdate.isEmpty()) {

                // Get existing reply information
                RatingReplies reply = rrDAO.getReplyByRepyID(replyID);

                // Update reply content
                int result = rrDAO.UpdateReply(reply, answerUpdate);

                // Return operation result
                if (result > 0) {
                    response.getWriter().write("Success");
//                  response.sendRedirect("ViewFeedbackForManagerServlet?rateID="
//                          + reply.getRateID() + "&success=success");

                } else {
                    response.getWriter().write("Failed");
//                  response.sendRedirect("ViewFeedbackForManagerServlet?rateID="
//                          + reply.getRateID() + "&success=failed");
                }

            } else {
                response.getWriter().write("Invalid Input");
            }

        } catch (Exception e) {
            // Return error message if an exception occurs
            response.getWriter().write("Error: " + e.getMessage());
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