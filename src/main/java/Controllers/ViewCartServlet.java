/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.CartDAO;
import DAOs.ProductDAO;
import Models.Cart;
import Models.Customer;
import Models.Product;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@WebServlet(name = "ViewCartServlet", urlPatterns = {"/cart"})
public class ViewCartServlet extends HttpServlet {

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
        ProductDAO p = new ProductDAO();
        HttpSession session = request.getSession();
        if (session.getAttribute("customer") != null) {
            Customer cus = (Customer) session.getAttribute("customer");
            CartDAO c = new CartDAO();
            session.setAttribute("numOfProCartOfCus", c.getNumberOfProduct(cus.getId()));
            List<Cart> cartList = c.getCartOfAccountID(cus.getId());
            System.out.println(cus.getId());
            //List<Product> pList = p.getAllProducts();
            for (Cart cart : cartList) {
                Product product = p.getProductByID(cart.getProductID());
                System.out.println(product.getFullName() + " " + product.getDeleted());
                if (product.getDeleted() == 0) {
                    if (product.getStock() == 0) {
                        cart.setQuantity(0);
                    } else if (product.getStock() > 0 && (product.getStock() < cart.getQuantity())) {
                        cart.setQuantity(product.getStock());
                        c.updateProductQuantity(cart.getProductID(), product.getStock(), cus.getId());
                    }
                } else if (product.getDeleted() == 1) {
                    c.deleteProductOnCart(cart.getProductID(), cus.getId());
                }
            }

            session.setAttribute(
                    "cartList", cartList);
            request.getRequestDispatcher(
                    "CartView.jsp").forward(request, response);
        } else {
            // your code about Cart guest
            response.sendRedirect("customerLogin");
        }
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
