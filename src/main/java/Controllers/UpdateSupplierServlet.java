package Controllers;

import DAOs.SupplierDAO;
import Models.Supplier;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UpdateSupplierServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Integer id = parsePositiveInt(request.getParameter("id"));
        String taxId = trim(request.getParameter("taxNumber"));
        String name = trim(request.getParameter("name"));
        String email = trim(request.getParameter("email"));
        String phone = trim(request.getParameter("phone"));
        String address = trim(request.getParameter("address"));
        Integer status = parseStatus(request.getParameter("status"));

        String error = validate(id, taxId, name, email, phone, address, status);
        SupplierDAO supplierDAO = new SupplierDAO();
        if (error == null && supplierDAO.getSupplierByID(id) == null) {
            error = "Supplier was not found.";
        }
        if (error == null && supplierDAO.taxIdExists(taxId, id)) {
            error = "Tax ID is already used by another supplier.";
        }

        String detailUrl = request.getContextPath() + "/Supplier"
                + (id == null ? "" : "?id=" + id);
        if (error != null) {
            request.getSession().setAttribute("supplierError", error);
            response.sendRedirect(detailUrl);
            return;
        }

        Supplier supplier = new Supplier(id, taxId, name, email, phone, address,
                LocalDateTime.now(), LocalDateTime.now(), 0, status);
        if (supplierDAO.updateSupplier(supplier) == 0) {
            request.getSession().setAttribute("supplierError",
                    "Supplier could not be updated. Please try again.");
        } else {
            request.getSession().setAttribute("supplierMessage", "Supplier updated successfully.");
        }
        response.sendRedirect(detailUrl);
    }

    private String validate(Integer id, String taxId, String name, String email,
            String phone, String address, Integer status) {
        if (id == null) {
            return "Invalid supplier ID.";
        }
        if (taxId == null || !taxId.matches("[A-Za-z0-9-]{3,20}")) {
            return "Tax ID must contain 3 to 20 letters, numbers, or hyphens.";
        }
        if (name == null || name.length() < 2 || name.length() > 255) {
            return "Company name must contain between 2 and 255 characters.";
        }
        if (email == null || email.length() > 254
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            return "Please enter a valid email address.";
        }
        if (phone == null || !phone.matches("0[0-9]{9}")) {
            return "Phone number must contain 10 digits and start with 0.";
        }
        if (address == null || address.length() < 3 || address.length() > 255) {
            return "Address must contain between 3 and 255 characters.";
        }
        return status == null ? "Please select a valid status." : null;
    }

    private Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer parseStatus(String value) {
        return "0".equals(value) ? 0 : ("1".equals(value) ? 1 : null);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
