package Filters;

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

@WebFilter(filterName = "CustomerAuthorizationFilter", urlPatterns = {
    "/customer/*", "/cart", "/AddToCart", "/updateCart", "/deletePOC",
    "/order", "/cancelOrder", "/ViewOrderHistory", "/odetailforcus",
    "/ViewShippingAddress", "/AddAddress", "/UpdateAddress", "/DeleteAddress",
    "/viewCustomerProfile", "/updateCustomerProfile", "/changeCustomerPassword",
    "/ViewCustomerVoucher", "/SaveVoucherServlet", "/NotificationServlet",
    "/RequestToDeleteAccount"
})
public class CustomerAuthorizationFilter implements Filter {
    @Override public void init(FilterConfig filterConfig) { }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/customerLogin?expired=1");
            return;
        }
        chain.doFilter(request, response);
    }
    @Override public void destroy() { }
}
