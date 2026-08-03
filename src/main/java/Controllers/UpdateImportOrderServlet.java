package Controllers;

import DAOs.ImportOrderDAO;
import DAOs.ProductDAO;
import DAOs.SupplierDAO;
import Models.ImportOrder;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Thuongnvce181966
 */
public class UpdateImportOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int importId = parsePositiveInt(request.getParameter("id"));
        if (importId < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid import order ID");
            return;
        }

        ImportOrderDAO importOrderDAO = new ImportOrderDAO();
        ImportOrder importOrder = importOrderDAO.getImportOrderDetailsByID(importId);
        if (importOrder == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Import order not found");
            return;
        }

        request.setAttribute("supplier", importOrder.getSupplier());
        request.setAttribute("suppliers", new SupplierDAO().getAllActivatedSuppliers());
        request.setAttribute("products", new ProductDAO().getAllAvailabilityProducts());
        request.setAttribute("importOrder", importOrder);
        request.setAttribute("isUpdateMode", true);
        request.getRequestDispatcher("ImportStockView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        int importId = parsePositiveInt(request.getParameter("importId"));
        if (importId < 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid import order ID");
            return;
        }

        ImportOrderDAO importOrderDAO = new ImportOrderDAO();
        if (importOrderDAO.getImportOrderByID(importId) == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Import order not found");
            return;
        }

        HttpSession session = request.getSession();
        boolean updated;
        String action = request.getParameter("action");

        if (request.getParameter("supplierId") != null) {
            int supplierId = parsePositiveInt(request.getParameter("supplierId"));
            updated = supplierId > 0
                    && new SupplierDAO().getSupplierByID(supplierId) != null
                    && importOrderDAO.updateImportOrderSupplier(importId, supplierId) == 1;
        } else if (request.getParameter("productEditedId") != null) {
            int productId = parsePositiveInt(request.getParameter("productEditedId"));
            if ("delete".equals(action)) {
                updated = productId > 0 && importOrderDAO.deleteImportOrderDetail(importId, productId);
            } else {
                int quantity = parsePositiveInt(request.getParameter("quantity"));
                long price = parsePositiveLong(request.getParameter("price"));
                updated = productId > 0 && quantity > 0 && price > 0
                        && importOrderDAO.updateImportOrderDetail(importId, productId, quantity, price);
            }
        } else if (request.getParameter("productId") != null) {
            int productId = parsePositiveInt(request.getParameter("productId"));
            int quantity = parsePositiveInt(request.getParameter("importQuantity"));
            long price = parsePositiveLong(request.getParameter("importPrice"));
            updated = productId > 0 && quantity > 0 && price > 0
                    && importOrderDAO.addImportOrderDetail(importId, productId, quantity, price);
        } else {
            updated = false;
        }

        if (!updated) {
            session.setAttribute("error", "Could not update the import order. Check duplicate products and current stock quantity.");
        } else {
            session.setAttribute("importUpdateSuccess", "Import order updated successfully.");
        }
        response.sendRedirect(request.getContextPath() + "/UpdateImportOrder?id=" + importId);
    }

    private int parsePositiveInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private long parsePositiveLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
