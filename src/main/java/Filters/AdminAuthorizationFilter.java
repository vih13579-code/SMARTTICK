package Filters;

import Models.Employee;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "AdminAuthorizationFilter", urlPatterns = {
    "/admin/*", "/ProductListServlet", "/CreateProductServlet", "/UpdateProductServlet",
    "/DeleteProductServlet", "/SearchProduct", "/ShopDashboardServlet",
    "/CustomerListServlet", "/SearchCustomerServlet", "/ViewEmployeeServlet",
    "/AddEmployeeServlet", "/UpdateEmployeeServlet", "/SearchEmployeeServlet",
    "/DeleteEmployeeServlet",
    "/ViewOrderListServlet", "/ViewOrderDetailServlet", "/UpdateOrderServlet",
    "/DeleteOrderServlet", "/PreviewDeleteOrderServlet", "/Supplier", "/SearchSupplier",
    "/CreateSupplier", "/UpdateSupplier", "/DeleteSupplier", "/ImportOrder",
    "/CreateImportOrder", "/ImportStock", "/UpdateImportOrder", "/DeleteImportOrder",
    "/Warehouse", "/ExportStock", "/ImportStatistic", "/StatisticManagementServlet",
    "/InventoryStatisticServlet", "/SearchInventoryServlet", "/RevenueStatisticServlet",
    "/ProductStatisticServlet", "/ViewVoucherListServlet", "/ViewVoucherDetailServlet",
    "/CreateVoucherServlet", "/UpdateVoucherServlet", "/DeleteVoucherServlet",
    "/SearchVoucherServlet",
    "/AssignVoucherServlet", "/ViewListNewFeedbackServlet", "/ViewFeedbackForManagerServlet",
    "/UpdateStatusCommentServlet", "/ReplyFeedbackServlet", "/UpdateReplyServlet",
    "/DeleteReplyServlet", "/NotificationServlet"
})
public class AdminAuthorizationFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) { }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        Employee employee = session == null ? null : (Employee) session.getAttribute("employee");
        if (employee == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/EmployeeLogin");
            return;
        }
        if (employee.getStatus() != 1 || employee.getRoleId() < 1 || employee.getRoleId() > 4) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }
    @Override public void destroy() { }
}
