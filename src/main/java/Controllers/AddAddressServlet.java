/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.AddressDAO;
import Models.Address;
import Models.Customer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@WebServlet(name = "AddAddressServlet", urlPatterns = {"/AddAddress"})
public class AddAddressServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/ViewShippingAddress");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin");
            return;
        }
        Customer cus = (Customer) session.getAttribute("customer");

        if (cus == null) {
            response.sendRedirect(request.getContextPath() + "/customerLogin");
            return;
        }

        String currentPage = clean(request.getParameter("currentAddressPage"));
        String province = clean(request.getParameter("province"));
        String district = clean(request.getParameter("district"));
        String ward = clean(request.getParameter("ward"));
        String address = clean(request.getParameter("address"));
        String redirect = request.getContextPath() + "/ViewShippingAddress"
                + ("forOrder".equalsIgnoreCase(currentPage) ? "?action=forOrder" : "");

        if (province.isEmpty() || district.isEmpty() || ward.isEmpty() || address.length() < 5) {
            session.setAttribute("addressError", "Please enter a complete and valid address.");
            response.sendRedirect(redirect);
            return;
        }

        AddressDAO addressDAO = new AddressDAO();
        boolean makeDefault = request.getParameter("isDefault") != null;
        int id = addressDAO.addAddress(new Address(cus.getId(), makeDefault ? 1 : 0,
                address + ", " + ward + ", " + district + ", " + province));
        if (id > 0) {
            if (makeDefault) {
                addressDAO.disableDefaultAddress(id, cus.getId());
            }
            session.setAttribute("message", "Add address successfully");
        } else {
            session.setAttribute("addressError", "Could not save the address. Please try again.");
        }
        response.sendRedirect(redirect);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns the servlet description.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }// </editor-fold>

}
