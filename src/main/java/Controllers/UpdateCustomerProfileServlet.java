package Controllers;

import DAOs.CustomerDAO;
import Models.Customer;
import java.io.File;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet(name = "UpdateProfileServlet", urlPatterns = {"/updateCustomerProfile"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 10 * 1024 * 1024,
        maxRequestSize = 100 * 1024 * 1024)
public class UpdateCustomerProfileServlet extends HttpServlet {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[2-9][0-9]{8}$");
    private static final Set<String> GENDERS
            = new HashSet<>(Arrays.asList("Male", "Female", "Other"));
    private static final Set<String> IMAGE_TYPES
            = new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/webp"));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Customer customer = getAuthenticatedCustomer(request, response);
        if (customer == null) {
            return;
        }
        request.setAttribute("profilePage", "UpdateCustomerProfileView.jsp");
        request.getRequestDispatcher("/ProfileManagementView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Customer signedInCustomer = getAuthenticatedCustomer(request, response);
        if (signedInCustomer == null) {
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.getCustomerById(signedInCustomer.getId());
        if (customer == null) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/customerLogin?expired=1");
            return;
        }

        String fullName = clean(request.getParameter("fullname"));
        String phoneNumber = clean(request.getParameter("phoneNumber"));
        String gender = clean(request.getParameter("gender"));
        String birthdayText = clean(request.getParameter("birthday"));

        if (fullName.length() < 2 || fullName.length() > 100) {
            showError(request, response, "Full name must contain 2-100 characters.");
            return;
        }
        if (!phoneNumber.isEmpty() && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            showError(request, response,
                    "Phone number must contain 10 digits, start with 0, and use a valid prefix.");
            return;
        }
        if (!gender.isEmpty() && !GENDERS.contains(gender)) {
            showError(request, response, "Please select a valid gender.");
            return;
        }

        String birthday = null;
        if (!birthdayText.isEmpty()) {
            try {
                LocalDate parsedBirthday = LocalDate.parse(birthdayText);
                if (parsedBirthday.getYear() < 1900 || parsedBirthday.isAfter(LocalDate.now())) {
                    showError(request, response, "Please enter a valid date of birth.");
                    return;
                }
                birthday = parsedBirthday.toString();
            } catch (DateTimeException ex) {
                showError(request, response, "Please enter a valid date of birth.");
                return;
            }
        }

        Part avatarPart;
        try {
            avatarPart = request.getPart("avatar");
        } catch (IllegalStateException ex) {
            showError(request, response, "The selected avatar is too large.");
            return;
        }
        if (avatarPart != null && avatarPart.getSize() > 0) {
            String contentType = clean(avatarPart.getContentType()).toLowerCase();
            if (!IMAGE_TYPES.contains(contentType)) {
                showError(request, response, "Avatar must be a JPG, PNG, or WEBP image.");
                return;
            }
            String avatarName = customer.getId() + ".jpg";
            if (!saveAvatar(avatarPart, avatarName)) {
                showError(request, response, "The avatar could not be saved. Please try again.");
                return;
            }
            customer.setAvatar(avatarName);
        }

        customer.setFullName(fullName);
        customer.setPhoneNumber(phoneNumber.isEmpty() ? null : phoneNumber);
        customer.setGender(gender.isEmpty() ? null : gender);
        customer.setBirthday(birthday);

        if (customerDAO.updateCustomerProfile(customer) <= 0) {
            showError(request, response, "Your profile could not be updated. Please try again.");
            return;
        }

        HttpSession session = request.getSession();
        session.setAttribute("customer", customer);
        session.setAttribute("fullName", customer.getFullName());
        session.setAttribute("avatar", customer.getAvatar());
        session.setAttribute("message", "Profile updated successfully.");
        response.sendRedirect(request.getContextPath() + "/viewCustomerProfile");
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Customer customer = session == null ? null : (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin?expired=1");
        }
        return customer;
    }

    private boolean saveAvatar(Part avatarPart, String avatarName) {
        try {
            String uploadPath = getServletContext().getRealPath("/assets/imgs/CustomerAvatar");
            if (uploadPath == null) {
                getServletContext().log("Customer avatar path is unavailable.");
                return false;
            }
            File uploadDirectory = new File(uploadPath);
            if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
                return false;
            }
            avatarPart.write(new File(uploadDirectory, avatarName).getAbsolutePath());
            return true;
        } catch (IOException ex) {
            getServletContext().log("Could not save customer avatar", ex);
            return false;
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response,
            String message) throws ServletException, IOException {
        request.setAttribute("profileError", message);
        request.setAttribute("profilePage", "UpdateCustomerProfileView.jsp");
        request.getRequestDispatcher("/ProfileManagementView.jsp").forward(request, response);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
