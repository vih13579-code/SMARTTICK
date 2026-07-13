/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.ProductRatingDAO;
import DAOs.RatingRepliesDAO;
import Models.Employee;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet responsible for handling employee replies to customer feedback.
 * It saves the reply and marks the feedback as read.
 *
 * @author TrucBQCE181355
 */
public class ReplyFeedbackServlet extends HttpServlet {

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
     * No processing is required for GET requests.
     *
     * @param request HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    /**
     * Handles the HTTP POST request.
     * Retrieves employee information, saves the feedback reply,
     * updates the feedback status, and redirects to the feedback list page.
     *
     * @param request HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get current employee from session
        HttpSession session = request.getSession();
        Employee em = (Employee) session.getAttribute("employee");

        // Retrieve request parameters
        int rateID = Integer.parseInt(request.getParameter("rateID"));
        int emID = em.getEmployeeId();
        String answer = request.getParameter("Answer");

        // Create DAO objects
        ProductRatingDAO prDAO = new ProductRatingDAO();
        RatingRepliesDAO rrDAO = new RatingRepliesDAO();

        try {

            // Save employee reply
            int count = rrDAO.addRatingReply(emID, rateID, answer);

            // Mark the feedback as read
            prDAO.updateisReadComment(rateID);

            // Redirect based on operation result
            if (count > 0) {
                response.sendRedirect(request.getContextPath()
                        + "/ViewListNewFeedbackServlet?&success=created");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/ViewListNewFeedbackServlet?&success=deleted");
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