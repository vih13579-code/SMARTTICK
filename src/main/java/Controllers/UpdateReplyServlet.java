/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.RatingRepliesDAO;
import Models.RatingReplies;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateReplyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RatingRepliesDAO rrDAO = new RatingRepliesDAO();
        String answerUpdate = request.getParameter("answer");
        int replyID = Integer.parseInt(request.getParameter("replyID"));
        try {
            if (replyID > 0 && answerUpdate != null && !answerUpdate.isEmpty()) {
                RatingReplies reply = rrDAO.getReplyByRepyID(replyID);
                int result = rrDAO.UpdateReply(reply, answerUpdate);
                if (result > 0) {
                    response.getWriter().write("Success");
//                    response.sendRedirect("ViewFeedbackForManagerServlet?rateID=" + reply.getRateID() + "&success=success");

                } else {
                    response.getWriter().write("Failed");
//                    response.sendRedirect("ViewFeedbackForManagerServlet?rateID=" + reply.getRateID() + "&success=failed");
                }
            } else {
                response.getWriter().write("Invalid Input");
            }
        } catch (Exception e) {
            response.getWriter().write("Error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }
}
