package Controllers;

import DAOs.EmployeeDAO;
import Models.Employee;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class UpdateEmployeeServlet extends HttpServlet {

    private static final Set<String> ALLOWED_GENDERS
            = new HashSet<>(Arrays.asList("Male", "Female", "Other"));
    private static final Set<String> ALLOWED_IMAGE_TYPES
            = new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/webp"));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer employeeId = parsePositiveInt(request.getParameter("id"));
        Employee employee = employeeId == null ? null
                : new EmployeeDAO().getEmployeeById(String.valueOf(employeeId));
        if (employee == null || employee.getRoleId() == 1) {
            response.sendRedirect(request.getContextPath()
                    + "/ViewEmployeeServlet?error=notfound");
            return;
        }

        request.setAttribute("employee", employee);
        request.setAttribute("currentAvatar", employee.getAvatar());
        request.getRequestDispatcher("UpdateEmployeeView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer employeeId = parsePositiveInt(request.getParameter("txtEmployeeId"));
        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee existingEmployee = employeeId == null ? null
                : employeeDAO.getEmployeeById(String.valueOf(employeeId));
        if (existingEmployee == null || existingEmployee.getRoleId() == 1) {
            response.sendRedirect(request.getContextPath()
                    + "/ViewEmployeeServlet?error=notfound");
            return;
        }

        Integer roleId = parseRole(request.getParameter("txtRoleId"));
        String name = trim(request.getParameter("txtName"));
        String newPassword = request.getParameter("txtPass");
        String birthdayValue = trim(request.getParameter("txtBirthday"));
        String phone = trim(request.getParameter("txtPhoneNumber"));
        String email = trim(request.getParameter("txtEmail"));
        String gender = trim(request.getParameter("txtGender"));
        Integer status = parseStatus(request.getParameter("txtStatus"));

        Date birthday = parseBirthday(birthdayValue);
        String validationError = validate(roleId, name, newPassword, birthdayValue,
                birthday, phone, email, gender, status);
        if (validationError == null
                && !email.equalsIgnoreCase(existingEmployee.getEmail())
                && employeeDAO.isEmailExists(email)) {
            validationError = "Email is already used by another employee.";
        }
        if (validationError != null) {
            forwardWithError(request, response, existingEmployee, roleId, name,
                    birthday, phone, email, gender, status, validationError);
            return;
        }

        String previousAvatar = existingEmployee.getAvatar();
        String savedAvatar = null;
        try {
            Part avatarPart = request.getPart("txtAvatar");
            if (avatarPart != null && avatarPart.getSize() > 0) {
                savedAvatar = saveAvatar(avatarPart);
            }
        } catch (IllegalStateException | IOException ex) {
            forwardWithError(request, response, existingEmployee, roleId, name,
                    birthday, phone, email, gender, status,
                    ex.getMessage() == null ? "The avatar could not be saved." : ex.getMessage());
            return;
        }

        String password = newPassword == null || newPassword.trim().isEmpty()
                ? existingEmployee.getPassword() : employeeDAO.getMD5(newPassword);
        String avatar = savedAvatar == null ? previousAvatar : savedAvatar;
        Employee updatedEmployee = new Employee(
                existingEmployee.getEmployeeId(),
                name,
                birthday,
                password,
                phone,
                email,
                gender,
                existingEmployee.getCreatedDate(),
                status,
                avatar,
                roleId
        );

        if (employeeDAO.UpdateEmployee(updatedEmployee) != 1) {
            deleteAvatarQuietly(savedAvatar);
            forwardWithError(request, response, existingEmployee, roleId, name,
                    birthday, phone, email, gender, status,
                    "The employee could not be updated. Please try again.");
            return;
        }

        if (savedAvatar != null) {
            deleteManagedAvatarQuietly(previousAvatar);
        }
        HttpSession session = request.getSession();
        session.setAttribute("employeeMessage", "Employee updated successfully.");
        response.sendRedirect(request.getContextPath() + "/ViewEmployeeServlet");
    }

    private String validate(Integer roleId, String name, String password,
            String birthdayValue, Date birthday, String phone, String email,
            String gender, Integer status) {
        if (roleId == null) {
            return "Please select a valid employee role.";
        }
        if (name == null || name.length() < 2 || name.length() > 255
                || !name.matches("[\\p{L}\\p{M}][\\p{L}\\p{M} .'-]*")) {
            return "Full name must contain between 2 and 255 valid characters.";
        }
        if (password != null && !password.trim().isEmpty()
                && !password.matches("^(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,50}$")) {
            return "The new password must contain 8–50 characters, uppercase, and a special character.";
        }
        if (birthdayValue != null && !birthdayValue.isEmpty() && birthday == null) {
            return "Please enter a valid birthday.";
        }
        if (birthday != null) {
            LocalDate value = birthday.toLocalDate();
            if (value.isAfter(LocalDate.now()) || value.isBefore(LocalDate.of(1900, 1, 1))) {
                return "Please enter a valid birthday.";
            }
        }
        if (phone == null || !phone.matches("0[0-9]{9}")) {
            return "Phone number must contain 10 digits and start with 0.";
        }
        if (email == null || email.length() > 254
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return "Please enter a valid email address.";
        }
        if (gender != null && !gender.isEmpty() && !ALLOWED_GENDERS.contains(gender)) {
            return "Please select a valid gender.";
        }
        return status == null ? "Please select a valid account status." : null;
    }

    private String saveAvatar(Part part) throws IOException {
        String mimeType = part.getContentType() == null
                ? "" : part.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_IMAGE_TYPES.contains(mimeType)) {
            throw new IOException("Only JPG, PNG, or WEBP images are accepted.");
        }

        String extension = "image/png".equals(mimeType) ? "png"
                : ("image/webp".equals(mimeType) ? "webp" : "jpg");
        String uploadPath = getServletContext().getRealPath("/assets/imgs/Employee/");
        if (uploadPath == null || uploadPath.trim().isEmpty()) {
            throw new IOException("The employee avatar directory is unavailable.");
        }

        Path directory = java.nio.file.Paths.get(uploadPath).normalize().toAbsolutePath();
        Files.createDirectories(directory);
        String fileName = "employee-management-"
                + UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path target = directory.resolve(fileName).normalize();
        if (!target.startsWith(directory) || !directory.equals(target.getParent())) {
            throw new IOException("The avatar path is invalid.");
        }
        try (InputStream input = part.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }

    private void forwardWithError(HttpServletRequest request,
            HttpServletResponse response, Employee existingEmployee, Integer roleId,
            String name, Date birthday, String phone, String email, String gender,
            Integer status, String message) throws ServletException, IOException {
        request.setAttribute("employee", existingEmployee);
        request.setAttribute("txtRoleId", roleId);
        request.setAttribute("txtName", name);
        request.setAttribute("txtBirthday", birthday);
        request.setAttribute("txtPhoneNumber", phone);
        request.setAttribute("txtEmail", email);
        request.setAttribute("txtGender", gender);
        request.setAttribute("txtStatus", status);
        request.setAttribute("currentAvatar", existingEmployee.getAvatar());
        request.setAttribute("errorMsg", message);
        request.getRequestDispatcher("UpdateEmployeeView.jsp").forward(request, response);
    }

    private void deleteManagedAvatarQuietly(String fileName) {
        if (fileName != null
                && fileName.matches("employee-management-[A-Za-z0-9]+\\.(jpg|png|webp)")) {
            deleteAvatarQuietly(fileName);
        }
    }

    private void deleteAvatarQuietly(String fileName) {
        if (fileName == null || !fileName.matches("[A-Za-z0-9._-]+")) {
            return;
        }
        try {
            String uploadPath = getServletContext().getRealPath("/assets/imgs/Employee/");
            if (uploadPath == null) {
                return;
            }
            Path directory = java.nio.file.Paths.get(uploadPath).normalize().toAbsolutePath();
            Path target = directory.resolve(fileName).normalize();
            if (target.startsWith(directory) && directory.equals(target.getParent())) {
                Files.deleteIfExists(target);
            }
        } catch (IOException ignored) {
            // A stale file must not turn a successful update into an error.
        }
    }

    private Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer parseRole(String value) {
        Integer role = parsePositiveInt(value);
        return role != null && role >= 2 && role <= 4 ? role : null;
    }

    private Integer parseStatus(String value) {
        return "0".equals(value) ? 0 : ("1".equals(value) ? 1 : null);
    }

    private Date parseBirthday(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Date.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
