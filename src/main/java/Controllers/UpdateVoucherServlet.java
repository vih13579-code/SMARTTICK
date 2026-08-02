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
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UpdateVoucherServlet extends HttpServlet {

    private static final Logger LOGGER =
            Logger.getLogger(UpdateVoucherServlet.class.getName());
    private static final DateTimeFormatter INPUT_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private final VoucherService voucherService = new VoucherService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer voucherId = parseVoucherId(request.getParameter("voucherID"));
        if (voucherId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Voucher voucher = new VoucherDAO().getVoucher(voucherId);
        if (voucher == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("voucherId", voucher.getVoucherId());
        request.setAttribute("formVoucherCode", voucher.getVoucherCode());
        request.setAttribute("formType", voucher.getType());
        request.setAttribute("formValue", voucher.getValue().toPlainString());
        request.setAttribute("formMaxDiscount", voucher.getMaxDiscount() == null
                ? "" : voucher.getMaxDiscount().toPlainString());
        request.setAttribute("formMinOrderValue",
                voucher.getMinOrderValue().toPlainString());
        request.setAttribute("formEndDate",
                voucher.getEndDate().format(INPUT_DATE_TIME));
        forwardForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Integer voucherId = parseVoucherId(request.getParameter("voucherID"));
        if (voucherId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ValidationResult validation = voucherService.validateForWrite(
                request.getParameter("voucherCode"),
                request.getParameter("type"),
                request.getParameter("value"),
                request.getParameter("maxDiscount"),
                request.getParameter("minOrderValue"),
                request.getParameter("endDate"),
                LocalDateTime.now());
        request.setAttribute("voucherId", voucherId);
        setFormAttributes(request, validation);
        if (!validation.isValid()) {
            forwardForm(request, response);
            return;
        }

        Voucher voucher = validation.getVoucher();
        voucher.setVoucherId(voucherId);
        VoucherDAO voucherDAO = new VoucherDAO();
        try {
            if (voucherDAO.existsByCodeExcludingId(voucher.getVoucherCode(), voucherId)) {
                addCodeDuplicateError(request, validation);
                forwardForm(request, response);
                return;
            }

            WriteResult result = voucherDAO.updateVoucher(voucher);
            if (result == WriteResult.DUPLICATE_CODE) {
                addCodeDuplicateError(request, validation);
                forwardForm(request, response);
                return;
            }
            if (result == WriteResult.NOT_FOUND) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (result != WriteResult.SUCCESS) {
                request.setAttribute("formError",
                        "The voucher could not be updated. Please try again.");
                forwardForm(request, response);
                return;
            }

            LOGGER.info("Voucher updated: voucher_code=" + voucher.getVoucherCode()
                    + ", performed_by=" + auditActor(request));
            response.sendRedirect(request.getContextPath()
                    + "/ViewVoucherListServlet?success=updatesuccess");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Cannot update voucher " + voucher.getVoucherCode(), ex);
            request.setAttribute("formError",
                    "The voucher could not be saved because of a data error. Please try again.");
            forwardForm(request, response);
        }
    }

    private Integer parseVoucherId(String rawId) {
        if (rawId == null || !rawId.trim().matches("[1-9][0-9]*")) {
            return null;
        }
        try {
            return Integer.valueOf(rawId.trim());
        } catch (NumberFormatException ex) {
            return null;
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
        request.getRequestDispatcher("/UpdateVoucherView.jsp").forward(request, response);
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
