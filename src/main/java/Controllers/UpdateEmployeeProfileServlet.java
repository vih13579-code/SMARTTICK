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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet(name = "UpdateEmloyeeProfileServlet", urlPatterns = {"/UpdateEmployeeProfile"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class UpdateEmployeeProfileServlet extends HttpServlet {

    private static final long MAX_AVATAR_SIZE = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_GENDERS
            = new HashSet<>(Arrays.asList("Male", "Female", "Other"));
    private static final Set<String> ALLOWED_IMAGE_TYPES
            = new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/webp"));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/EmployeeLogin");
            return;
        }

        Employee loggedInEmployee = (Employee) session.getAttribute("employee");
        if (loggedInEmployee == null) {
            response.sendRedirect(request.getContextPath() + "/EmployeeLogin");
            return;
        }

        Employee employee = new EmployeeDAO().getEmployeeById(
                String.valueOf(loggedInEmployee.getEmployeeId()));
        if (employee != null) {
            session.setAttribute("employee", employee);
        } else {
            request.setAttribute("errorMessage", "Employee information could not be loaded.");
        }
        request.getRequestDispatcher("UpdateEmployeeProfile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/EmployeeLogin");
            return;
        }

        Employee loggedInEmployee = (Employee) session.getAttribute("employee");
        if (loggedInEmployee == null) {
            response.sendRedirect(request.getContextPath() + "/EmployeeLogin");
            return;
        }

        String fullName = trim(request.getParameter("fullName"));
        String gender = trim(request.getParameter("gender"));
        String phoneNumber = trim(request.getParameter("phone"));
        String birthday = trim(request.getParameter("dob"));

        String validationError = validate(fullName, gender, phoneNumber, birthday);
        if (validationError != null) {
            forwardWithError(request, response, validationError);
            return;
        }

        EmployeeDAO employeeDAO = new EmployeeDAO();
        Employee employee = employeeDAO.getEmployeeById(
                String.valueOf(loggedInEmployee.getEmployeeId()));
        if (employee == null) {
            forwardWithError(request, response, "Employee information could not be loaded.");
            return;
        }

        employee.setFullname(fullName);
        employee.setGender(gender);
        employee.setPhoneNumber(phoneNumber);
        employee.setBirthday(Date.valueOf(birthday));

        String previousAvatar = employee.getAvatar();
        String savedAvatar = null;
        try {
            Part avatarPart = request.getPart("avatar");
            if (avatarPart != null && avatarPart.getSize() > 0) {
                savedAvatar = saveAvatar(avatarPart, employee.getEmployeeId());
                employee.setAvatar(savedAvatar);
            }
        } catch (IllegalStateException | IOException ex) {
            forwardWithError(request, response, ex.getMessage() == null
                    ? "The avatar could not be saved." : ex.getMessage());
            return;
        }

        if (employeeDAO.updateEmployeeProfile(employee) == 0) {
            deleteAvatarQuietly(savedAvatar);
            forwardWithError(request, response, "Profile update failed. Please try again.");
            return;
        }

        if (savedAvatar != null) {
            deleteManagedAvatarQuietly(previousAvatar);
        }
        session.setAttribute("employee", employee);
        session.setAttribute("empromess", "Profile updated successfully.");
        response.sendRedirect(request.getContextPath() + "/ViewEmployeeProfile");
    }

    private String validate(String fullName, String gender, String phoneNumber, String birthday) {
        if (fullName == null || fullName.length() < 2 || fullName.length() > 100) {
            return "Full name must contain between 2 and 100 characters.";
        }
        if (!ALLOWED_GENDERS.contains(gender)) {
            return "Please select a valid gender.";
        }
        if (phoneNumber == null || !phoneNumber.matches("0[1-9][0-9]{8}")) {
            return "Phone number must contain 10 digits and start with 0.";
        }
        try {
            LocalDate date = LocalDate.parse(birthday);
            if (date.isAfter(LocalDate.now()) || date.isBefore(LocalDate.of(1900, 1, 1))) {
                return "Please enter a valid date of birth.";
            }
        } catch (RuntimeException ex) {
            return "Please enter a valid date of birth.";
        }
        return null;
    }

    private String saveAvatar(Part part, int employeeId) throws IOException {
        if (part.getSize() > MAX_AVATAR_SIZE) {
            throw new IOException("The avatar must not exceed 5 MB.");
        }

        String mimeType = part.getContentType() == null
                ? "" : part.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_IMAGE_TYPES.contains(mimeType)) {
            throw new IOException("Only JPG, PNG, or WEBP images are accepted.");
        }

        String extension;
        if ("image/png".equals(mimeType)) {
            extension = "png";
        } else if ("image/webp".equals(mimeType)) {
            extension = "webp";
        } else {
            extension = "jpg";
        }

        String uploadPath = getServletContext().getRealPath("/assets/imgs/EmployeeAvatar/");
        if (uploadPath == null || uploadPath.trim().isEmpty()) {
            throw new IOException("The employee avatar directory is unavailable.");
        }

        Path directory = java.nio.file.Paths.get(uploadPath).normalize().toAbsolutePath();
        Files.createDirectories(directory);
        Path temporary = Files.createTempFile(directory, "avatar-", ".upload");
        try {
            try (InputStream input = part.getInputStream()) {
                Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
            }
            if (!hasValidImageSignature(temporary, extension)) {
                throw new IOException("The selected file is not a valid image.");
            }

            String fileName = "employee-" + employeeId + "-"
                    + UUID.randomUUID().toString().replace("-", "") + "." + extension;
            Path target = directory.resolve(fileName).normalize();
            if (!target.startsWith(directory) || !directory.equals(target.getParent())) {
                throw new IOException("The avatar path is invalid.");
            }
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private boolean hasValidImageSignature(Path file, String extension) throws IOException {
        byte[] header = new byte[12];
        int read;
        try (InputStream input = Files.newInputStream(file)) {
            read = input.read(header);
        }
        if (read < 4) {
            return false;
        }
        if ("jpg".equals(extension)) {
            return (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF;
        }
        if ("png".equals(extension)) {
            return read >= 8
                    && (header[0] & 0xFF) == 0x89
                    && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47
                    && header[4] == 0x0D && header[5] == 0x0A
                    && header[6] == 0x1A && header[7] == 0x0A;
        }
        return read >= 12
                && header[0] == 'R' && header[1] == 'I'
                && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E'
                && header[10] == 'B' && header[11] == 'P';
    }

    private void deleteManagedAvatarQuietly(String fileName) {
        if (fileName != null
                && fileName.matches("employee-[0-9]+-[A-Za-z0-9]+\\.(jpg|png|webp)")) {
            deleteAvatarQuietly(fileName);
        }
    }

    private void deleteAvatarQuietly(String fileName) {
        if (fileName == null || !fileName.matches("[A-Za-z0-9._-]+")) {
            return;
        }
        try {
            String uploadPath = getServletContext().getRealPath("/assets/imgs/EmployeeAvatar/");
            if (uploadPath == null) {
                return;
            }
            Path directory = java.nio.file.Paths.get(uploadPath).normalize().toAbsolutePath();
            Path target = directory.resolve(fileName).normalize();
            if (target.startsWith(directory) && directory.equals(target.getParent())) {
                Files.deleteIfExists(target);
            }
        } catch (IOException ignored) {
            // A stale avatar must not turn a successful profile update into an error.
        }
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
            String message) throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("UpdateEmployeeProfile.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
