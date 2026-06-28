package Controllers;

import DAOs.EmployeeDAO;
import Models.Email;
import Models.EmailUtils;
import Models.Employee;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.sql.Date;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
/**
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class UpdateEmployeeServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String id = request.getParameter("id");
            EmployeeDAO empDAO = new EmployeeDAO();
            Employee emp = empDAO.getEmployeeById(id);
            request.setAttribute("employee", emp);
            request.setAttribute("currentAvatar", emp.getAvatar());
            request.getRequestDispatcher("UpdateEmployeeView.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            EmployeeDAO empDAO = new EmployeeDAO();
            int employeeId = Integer.parseInt(request.getParameter("txtEmployeeId"));
            int roleId = Integer.parseInt(request.getParameter("txtRoleId"));
            String name = request.getParameter("txtName");
            String password = request.getParameter("txtPass");
            Date birthday = null;
            String birthdayStr = request.getParameter("txtBirthday");
            if (birthdayStr != null && !birthdayStr.isEmpty()) {
                birthday = Date.valueOf(birthdayStr);
            }
            String phone = request.getParameter("txtPhoneNumber");
            String email = request.getParameter("txtEmail");
            String gender = request.getParameter("txtGender");
            Date createdDate = Date.valueOf(request.getParameter("txtCreatedDate"));
            int status = Integer.parseInt(request.getParameter("txtStatus"));
            String avatar = request.getParameter("currentAvatar");
            Part part = request.getPart("txtAvatar");
            String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            
            if (!isValidName(name)) {
                request.setAttribute("errorMsg", "Name must not exceed 255 characters and only contain letters.");
                forwardToUpdatePage(request, response, employeeId, roleId, name, password, birthday, phone, email, gender, createdDate, status, avatar);
                return;
            }
            
            if (!isValidPassword(password) && !password.equals(request.getParameter("currentPassword"))) {
                request.setAttribute("errorMsg", "Password must be at least 8 characters, including 1 uppercase letter and 1 special character!");
                forwardToUpdatePage(request, response, employeeId, roleId, name, password, birthday, phone, email, gender, createdDate, status, avatar);
                return;
            }
            
            if (empDAO.isEmailExists(email) && !email.equals(request.getParameter("currentEmail"))) {
                request.setAttribute("errorMsg", "Email already exists! Please choose another email.");
                forwardToUpdatePage(request, response, employeeId, roleId, name, password, birthday, phone, email, gender, createdDate, status, avatar);
                return;
            }
            
            if (!isValidPhoneNumber(phone)) {
                request.setAttribute("errorMsg", "Phone number must be exactly 10 digits.");
                forwardToUpdatePage(request, response, employeeId, roleId, name, password, birthday, phone, email, gender, createdDate, status, avatar);
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
                avatar = request.getParameter("currentAvatar");
            }
            
            Employee emp = new Employee(employeeId, name, birthday, password, phone, email, gender, createdDate, status, avatar, roleId);
            int result = empDAO.UpdateEmployee(emp);
            
            if (result > 0) {
                if (!password.equals(request.getParameter("currentPassword"))) {
                    sendPasswordChangeEmail(email, password);
                }
                
                request.setAttribute("popupSuccessMsg", "Updated successfully");
                request.getRequestDispatcher("UpdateEmployeeView.jsp").forward(request, response);
            } else {
                request.setAttribute("popupErrorMsg", "Update failed! Please try again.");
                request.getRequestDispatcher("UpdateEmployeeView.jsp").forward(request, response);
            }
        } catch (NullPointerException e) {
            System.out.println(e);
        }
    }
    
    private void forwardToUpdatePage(HttpServletRequest request, HttpServletResponse response, int employeeId, int roleId, String name, String password, Date birthday, String phone, String email, String gender, Date createdDate, int status, String avatar)
            throws ServletException, IOException {
        request.setAttribute("txtEmployeeId", employeeId);
        request.setAttribute("txtRoleId", roleId);
        request.setAttribute("txtName", name);
        request.setAttribute("txtPass", password);
        request.setAttribute("txtBirthday", birthday);
        request.setAttribute("txtPhoneNumber", phone);
        request.setAttribute("txtEmail", email);
        request.setAttribute("txtGender", gender);
        request.setAttribute("txtCreatedDate", createdDate);
        request.setAttribute("txtStatus", status);
        request.setAttribute("currentAvatar", avatar);
        request.setAttribute("employee", new Employee(employeeId, name, birthday, password, phone, email, gender, createdDate, status, avatar, roleId));
        request.getRequestDispatcher("UpdateEmployeeView.jsp").forward(request, response);
    }
    
    private void sendPasswordChangeEmail(String emailAddress, String newPassword) {
        Email sendEmail = new Email();
        sendEmail.setTo(emailAddress);
        sendEmail.setSubject("Reset Password " + emailAddress);
        String emailContent = "Your password has been updated successfully.\n"
                + "Your new password is: " + newPassword + "\n"
                + "Note: Please do not share this password with others.";
        sendEmail.setContent(emailContent);
        try {
            EmailUtils.send(sendEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean isValidName(String name) {
        return name != null && name.length() <= 255 && name.matches("[a-zA-Z\\s]+");
    }
    
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    }
    
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }
    
}
