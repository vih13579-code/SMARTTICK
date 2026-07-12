/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.AddressDAO;
import Models.Address;
import Models.Customer;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "UpdateAddressServlet", urlPatterns = {"/UpdateAddress"})
public class UpdateAddressServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer cus = (Customer) session.getAttribute("customer");
        if (cus != null) {
            AddressDAO add = new AddressDAO();
            int id = Integer.parseInt(request.getParameter("id"));
            String url = request.getParameter("currentAddressPage");
            System.out.println("Context: " + url);
            if (request.getParameter("action") != null && request.getParameter("action").equals("setAsDefault")) {
                add.setAsDefault(id);
                add.disableDefaultAddress(id, cus.getId());
            } else {
                String province = request.getParameter("province");
                String district = request.getParameter("district");
                String ward = request.getParameter("ward");
                String address = request.getParameter("address");
                String addressDetails = address + ", " + ward + ", " + district + ", " + province;
                Address addObj = add.getAddressByID(id);
                if (addObj.getIsDefault() == 1) {
                    addObj.setAddressDetails(addressDetails);
                    add.updateAddress(addObj);
                } else {
                    if (request.getParameter("isDefault") != null) {
                        add.updateAddress(new Address(id, cus.getId(), 1, addressDetails));
                        add.disableDefaultAddress(id, cus.getId());
                    } else {
                        add.updateAddress(new Address(id, cus.getId(), 0, addressDetails));
                    }
                }
            }
            session.setAttribute("message", "Update address successfully");
            if (url.equalsIgnoreCase("addressPage")) {
                response.sendRedirect("ViewShippingAddress");
            } else if (url.equalsIgnoreCase("forOrder")) {
                response.sendRedirect("ViewShippingAddress?action=forOrder");
            }
        }
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
