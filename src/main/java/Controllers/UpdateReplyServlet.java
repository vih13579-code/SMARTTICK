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

public class UpdateReplyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        RatingRepliesDAO rrDAO = new RatingRepliesDAO();
        String answerUpdate = request.getParameter("answer");
        try {
            int replyID = Integer.parseInt(request.getParameter("replyID"));
            if (replyID > 0 && answerUpdate != null && !answerUpdate.trim().isEmpty()) {
                RatingReplies reply = rrDAO.getReplyByRepyID(replyID);
                if (reply == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reply not found");
                    return;
                }
                int result = rrDAO.UpdateReply(reply, answerUpdate.trim());
                if (result > 0) {
                    response.getWriter().write("Success");
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not update reply");
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Reply cannot be empty");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid reply ID");
        }
    }

    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }
}
