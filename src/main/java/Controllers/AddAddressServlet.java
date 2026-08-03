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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Customer cus = (Customer) session.getAttribute("customer");

        if (cus != null) {
            String url = request.getParameter("currentAddressPage");
            AddressDAO add = new AddressDAO();
            String province = value(request.getParameter("province"));
            String commune = value(request.getParameter("commune"));
            String address = value(request.getParameter("address"));

            if (province.isEmpty() || commune.isEmpty() || address.length() < 5) {
                session.setAttribute("message",
                        "Please select a province/city and a ward, commune, or special zone.");
                redirectToAddressPage(request, response, url);
                return;
            }

            String addressDetails = address + ", " + commune + ", " + province;
            if (addressDetails.length() > 500) {
                session.setAttribute("message", "Address must not exceed 500 characters.");
                redirectToAddressPage(request, response, url);
                return;
            }

            if (request.getParameter("isDefault") != null) {
                int id = add.addAddress(new Address(cus.getId(), 1, addressDetails));
                if (id > 0) {
                    add.disableDefaultAddress(id, cus.getId());
                    session.setAttribute("message", "Add address successfully");
                } else {
                    session.setAttribute("message", "Could not add address. Please try again.");
                }
            } else {
                int id = add.addAddress(new Address(cus.getId(), 0, addressDetails));
                session.setAttribute("message", id > 0
                        ? "Add address successfully"
                        : "Could not add address. Please try again.");
            }
            redirectToAddressPage(request, response, url);
        }
    }

    private String value(String input) {
        return input == null ? "" : input.trim();
    }

    private void redirectToAddressPage(HttpServletRequest request, HttpServletResponse response, String page)
            throws IOException {
        String target = "forOrder".equalsIgnoreCase(page)
                ? "/ViewShippingAddress?action=forOrder"
                : "/ViewShippingAddress";
        response.sendRedirect(request.getContextPath() + target);
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
