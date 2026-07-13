package Controllers;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import DAOs.ProductRatingDAO;
import Models.ProductRating;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet responsible for displaying the list of newly submitted product
 * feedback.
 *
 * @author TrucBQCE181355
 */
public class ViewListNewFeedbackServlet extends HttpServlet {

    /**
     * Processes requests for both GET and POST methods.
     * This servlet only supports the GET method, so all other requests
     * will receive a 405 (Method Not Allowed) response.
     *
     * @param request  HttpServletRequest object containing client request
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
     * Retrieves all new product feedback from the database and forwards
     * the data to the ViewListOfNewFeedback.jsp page.
     *
     * @param request  HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Create DAO object
        ProductRatingDAO prDAO = new ProductRatingDAO();

        // Retrieve list of new feedback
        List<ProductRating> list = prDAO.getNewFeedback();

        // Forward data to JSP if feedback exists
        if (list != null) {
            request.setAttribute("ProductRating", list);
            request.getRequestDispatcher("ViewListOfNewFeedback.jsp")
                    .forward(request, response);
        }
    }

    /**
     * Handles the HTTP POST request.
     * POST requests are not supported by this servlet.
     *
     * @param request  HttpServletRequest object containing client request
     * @param response HttpServletResponse object for sending response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an input or output error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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