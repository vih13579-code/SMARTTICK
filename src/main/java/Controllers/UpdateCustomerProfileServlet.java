/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.CustomerDAO;
import Models.Customer;
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

@WebServlet(name = "UpdateProfileServlet", urlPatterns = { "/updateCustomerProfile" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024
        * 100)
public class UpdateCustomerProfileServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("profilePage", "UpdateCustomerProfileView.jsp");
        request.getRequestDispatcher("ProfileManagementView.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        CustomerDAO cusDAO = new CustomerDAO();
        Customer cus = (Customer) session.getAttribute("customer");
        Part img = request.getPart("avatar");
        String fullname = request.getParameter("fullname");
        String phoneNumber = request.getParameter("phoneNumber");
        String gender = request.getParameter("gender");
        String day = request.getParameter("day");
        String month = request.getParameter("month");
        String year = request.getParameter("year");

        if (fullname != null && !fullname.trim().isEmpty()) {
            cus.setFullName(fullname.trim());
        }
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            cus.setPhoneNumber(phoneNumber.trim());
        }
        if (gender != null && !gender.trim().isEmpty()) {
            cus.setGender(gender.trim());
        }

        // Handle birthday only when all parts are present
        if (day != null && month != null && year != null) {
            if (!("Day".equalsIgnoreCase(day) && "Month".equalsIgnoreCase(month) && "Year".equalsIgnoreCase(year))) {
                if ("Day".equalsIgnoreCase(day) || "Month".equalsIgnoreCase(month) || "Year".equalsIgnoreCase(year)) {
                    session.setAttribute("messageFail", "Please select a complete and valid date!");
                    response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
                    return;
                }

                try {
                    int dayInt = Integer.parseInt(day.trim());
                    int monthInt = Integer.parseInt(month.trim());
                    int yearInt = Integer.parseInt(year.trim());

                    if (dayInt < 1 || dayInt > 31 || monthInt < 1 || monthInt > 12 || yearInt < 1900
                            || yearInt > 2100) {
                        session.setAttribute("messageFail", "Invalid date value!");
                        response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
                        return;
                    }

                    String dayStr = dayInt < 10 ? "0" + dayInt : String.valueOf(dayInt);
                    String monthStr = monthInt < 10 ? "0" + monthInt : String.valueOf(monthInt);

                    cus.setBirthday(yearInt + "-" + monthStr + "-" + dayStr);
                } catch (NumberFormatException e) {
                    session.setAttribute("messageFail", "Invalid date format!");
                    response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
                    return;
                }
            }
        }

        // Safely compute upload path inside the deployed webapp
        String uploadPath = getServletContext().getRealPath("/assets/imgs/CustomerAvatar/");
        if (uploadPath == null) {
            // Fallback to using context root then path
            uploadPath = getServletContext().getRealPath("/");
            if (uploadPath == null) {
                uploadPath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");
            }
            uploadPath = uploadPath + "assets" + System.getProperty("file.separator") + "imgs"
                    + System.getProperty("file.separator") + "CustomerAvatar" + System.getProperty("file.separator");
        }

        java.io.File uploadDir = new java.io.File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        if (img != null && img.getSize() > 0) {
            cus.setAvatar(cus.getId() + ".jpg");
            try {
                String outPath = uploadPath + java.io.File.separator + cus.getId() + ".jpg";
                img.write(outPath);
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("messageFail", "Failed to save uploaded avatar.");
                response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
                return;
            }
        }

        int rs = cusDAO.updateCustomerProfile(cus);
        if (rs == 0) {
            session.setAttribute("messageFail", "Update customer fail!");
        } else {
            session.setAttribute("customer", cus);
            session.setAttribute("message", "Update customer successful!");
        }

        response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
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
