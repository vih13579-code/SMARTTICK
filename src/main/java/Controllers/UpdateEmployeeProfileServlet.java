/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.EmployeeDAO;
import Models.Employee;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.sql.Date;
import java.text.SimpleDateFormat;

@WebServlet(name = "UpdateEmloyeeProfileServlet", urlPatterns = {"/UpdateEmployeeProfile"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100
)
public class UpdateEmployeeProfileServlet extends HttpServlet {

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
        request.getRequestDispatcher("UpdateEmployeeProfile.jsp").forward(request, response);
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
        HttpSession session = request.getSession();
        EmployeeDAO emDAO = new EmployeeDAO();
        Employee em = (Employee) session.getAttribute("employee");
        if (em == null) {
            response.sendRedirect(request.getContextPath() + "/EmployeeLogin");
        }

        Part img = request.getPart("avatar");

        String fullName = request.getParameter("fullName");
        String gender = request.getParameter("gender");
        String phoneNumber = request.getParameter("phone");
        String birthday = request.getParameter("dob");

        Date date = Date.valueOf(birthday);

        em.setFullname(fullName);
        em.setGender(gender);
        em.setPhoneNumber(phoneNumber);
        em.setBirthday(date);

        String uploadPath = getServletContext().getRealPath("/");
        uploadPath = uploadPath.replace("target", "src");
        uploadPath = uploadPath.replace("SMARTTICK-1.0-SNAPSHOT", "main");
        uploadPath += "webapp\\assets\\imgs\\EmployeeAvatar\\";
        if (img != null && img.getSize() > 0) {
            em.setAvatar(em.getEmployeeId() + ".jpg");
            for (Part part : request.getParts()) {
                part.write(uploadPath + em.getEmployeeId() + ".jpg");
            }
            try {
                Thread.sleep(2500); // 15 giây = 15000 mili giây
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (emDAO.updateEmployeeProfile(em) == 0) {
            session.setAttribute("empromess", "Update profile fail!");
        } else {
            session.setAttribute("empromess", "Update profile success!");
        }

        response.sendRedirect(request.getContextPath() + "/ViewEmployeeProfile");
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
