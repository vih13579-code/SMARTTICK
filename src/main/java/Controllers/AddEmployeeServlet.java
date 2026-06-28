/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.EmployeeDAO;
import Models.Employee;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;
import java.sql.Date;

/**
 *
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class AddEmployeeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            EmployeeDAO empDAO = new EmployeeDAO();
            int roleId = Integer.parseInt(request.getParameter("txtRoleId"));
            String name = request.getParameter("txtName");
            String password = request.getParameter("txtPass");
            String phone = request.getParameter("txtPhoneNumber");
            String email = request.getParameter("txtEmail");
            Date createdDate = Date.valueOf(request.getParameter("txtCreatedDate"));
            int status = Integer.parseInt(request.getParameter("txtStatus"));
            Part part = request.getPart("txtAvatar");
            String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            String avatar = request.getParameter("currentAvatar");

            if (!isValidName(name)) {
                request.setAttribute("errorMsg", "Name must not exceed 255 characters and only contain letters.");
                request.setAttribute("txtRoleId", roleId);
                request.setAttribute("txtName", name);
                request.setAttribute("txtPass", password);
                request.setAttribute("txtPhoneNumber", phone);
                request.setAttribute("txtEmail", email);
                request.setAttribute("txtCreatedDate", createdDate);
                request.setAttribute("txtStatus", status);
                request.setAttribute("currentAvatar", avatar);
                request.getRequestDispatcher("AddEmployeeView.jsp").forward(request, response);
                return;
            }

            if (empDAO.isEmailExists(email)) {
                request.setAttribute("errorMsg", "Email already exists! Please choose another email.");
                request.setAttribute("txtRoleId", roleId);
                request.setAttribute("txtName", name);
                request.setAttribute("txtPass", password);
                request.setAttribute("txtPhoneNumber", phone);
                request.setAttribute("txtEmail", email);
                request.setAttribute("txtCreatedDate", createdDate);
                request.setAttribute("txtStatus", status);
                request.setAttribute("currentAvatar", avatar);
                request.getRequestDispatcher("AddEmployeeView.jsp").forward(request, response);
                return;
            }

            if (!isValidPhoneNumber(phone)) {
                request.setAttribute("errorMsg", "Phone number must be exactly 10 digits.");
                request.setAttribute("txtRoleId", roleId);
                request.setAttribute("txtName", name);
                request.setAttribute("txtPass", password);
                request.setAttribute("txtPhoneNumber", phone);
                request.setAttribute("txtEmail", email);
                request.setAttribute("txtCreatedDate", createdDate);
                request.setAttribute("txtStatus", status);
                request.setAttribute("currentAvatar", avatar);
                request.getRequestDispatcher("AddEmployeeView.jsp").forward(request, response);
                return;
            }

            if (!isValidPassword(password)) {
                request.setAttribute("errorMsg", "Password must be at least 8 characters, including 1 uppercase letter and 1 special character!");
                request.setAttribute("txtRoleId", roleId);
                request.setAttribute("txtName", name);
                request.setAttribute("txtPass", password);
                request.setAttribute("txtPhoneNumber", phone);
                request.setAttribute("txtEmail", email);
                request.setAttribute("txtCreatedDate", createdDate);
                request.setAttribute("txtStatus", status);
                request.setAttribute("currentAvatar", avatar);
                request.getRequestDispatcher("AddEmployeeView.jsp").forward(request, response);
                return;
            }

            if (fileName != null && !fileName.isEmpty()) {
                String uploadPath = getServletContext().getRealPath("/") + "assets/imgs/Employee";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                part.write(uploadPath + File.separator + fileName);
                avatar = fileName;
            }
            if (avatar == null || avatar.isEmpty()) {
                avatar = request.getParameter("default.png");
            }
            request.getParameter("currentAvatar");

            Employee emp = new Employee(name, null, password, phone, email, null, createdDate, status, avatar, roleId);
            int result = empDAO.AddEmployee(emp);

            if (result > 0) {
                request.setAttribute("popupSuccessMsg", "Add successfully");
                request.getRequestDispatcher("AddEmployeeView.jsp").forward(request, response);
            } else {
                request.setAttribute("popupErrorMsg", "Add failed! Please try again.");
                request.getRequestDispatcher("AddEmployeeView.jsp").forward(request, response);
            }
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }

    private boolean isValidName(String name) {
        return name != null && name.length() <= 255 && name.matches("[a-zA-Z\\s]+");
    }

}
