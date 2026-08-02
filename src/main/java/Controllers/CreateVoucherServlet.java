package Controllers;

import DAOs.VoucherDAO;
import DAOs.VoucherDAO.WriteResult;
import Models.Employee;
import Models.Voucher;
import Services.VoucherService;
import Services.VoucherService.ValidationResult;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CreateVoucherServlet extends HttpServlet {

    private static final Logger LOGGER =
            Logger.getLogger(CreateVoucherServlet.class.getName());
    private final VoucherService voucherService = new VoucherService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("formType", VoucherService.TYPE_PERCENT);
        request.getRequestDispatcher("/CreateVoucherView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ValidationResult validation = voucherService.validateForWrite(
                request.getParameter("voucherCode"),
                request.getParameter("type"),
                request.getParameter("value"),
                request.getParameter("maxDiscount"),
                request.getParameter("minOrderValue"),
                request.getParameter("endDate"),
                LocalDateTime.now());
        setFormAttributes(request, validation);

        if (!validation.isValid()) {
            forwardForm(request, response);
            return;
        }

        Voucher voucher = validation.getVoucher();
        VoucherDAO voucherDAO = new VoucherDAO();
        try {
            if (voucherDAO.existsByCode(voucher.getVoucherCode())) {
                addCodeDuplicateError(request, validation);
                forwardForm(request, response);
                return;
            }

            WriteResult result = voucherDAO.insertVoucher(voucher);
            if (result == WriteResult.DUPLICATE_CODE) {
                addCodeDuplicateError(request, validation);
                forwardForm(request, response);
                return;
            }
            if (result != WriteResult.SUCCESS) {
                request.setAttribute("formError",
                        "The voucher could not be created. Please try again.");
                forwardForm(request, response);
                return;
            }

            LOGGER.info("Voucher created: voucher_code=" + voucher.getVoucherCode()
                    + ", performed_by=" + auditActor(request));
            response.sendRedirect(request.getContextPath()
                    + "/ViewVoucherListServlet?success=createsuccess");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Cannot create voucher " + voucher.getVoucherCode(), ex);
            request.setAttribute("formError",
                    "The voucher could not be saved because of a data error. Please try again.");
            forwardForm(request, response);
        }
    }

    private void setFormAttributes(HttpServletRequest request, ValidationResult validation) {
        request.setAttribute("fieldErrors", validation.getErrors());
        request.setAttribute("formVoucherCode", validation.getVoucherCode());
        request.setAttribute("formType", validation.getType());
        request.setAttribute("formValue", validation.getValue());
        request.setAttribute("formMaxDiscount", validation.getMaxDiscount());
        request.setAttribute("formMinOrderValue", validation.getMinOrderValue());
        request.setAttribute("formEndDate", validation.getEndDate());
    }

    private void addCodeDuplicateError(HttpServletRequest request,
            ValidationResult validation) {
        Map<String, String> errors = new LinkedHashMap<>(validation.getErrors());
        errors.put("voucherCode", "Voucher Code already exists.");
        request.setAttribute("fieldErrors", errors);
    }

    private void forwardForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/CreateVoucherView.jsp").forward(request, response);
    }

    private String auditActor(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Employee employee =
                session == null ? null : (Employee) session.getAttribute("employee");
        return employee == null
                ? "unknown"
                : employee.getEmployeeId() + ":" + employee.getEmail();
    }
}
