/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAOs.ImportOrderDAO;
import DAOs.ImportOrderDetailDAO;
import DAOs.ProductDAO;
import DAOs.SupplierDAO;
import Models.ImportOrder;
import Models.ImportOrderDetail;
import Models.Product;
import Models.Supplier;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 *
 * @author Thuongnvce181966
 */

public class UpdateImportOrderServlet extends HttpServlet {

    SupplierDAO sd = new SupplierDAO();
    ProductDAO pd = new ProductDAO();
    ImportOrder io;
    ImportOrderDAO ioD = new ImportOrderDAO();
    ImportOrderDetailDAO iodD = new ImportOrderDetailDAO();
    ArrayList<Product> selectedProducts;
    Supplier s;
    int importId;
    ImportOrder importOrder;

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
//        processRequest(request, response);
        if (request.getParameter("id") != null) {
            importId = Integer.parseInt(request.getParameter("id"));
            io = ioD.getImportOrderByID(Integer.parseInt(request.getParameter("id")));
            importOrder = ioD.getImportOrderDetailsByID(importId);
            s = sd.getSupplierByID(io.getSupplierId());

            try {
                request.setAttribute("supplier", io.getSupplier());
                request.setAttribute("suppliers", sd.getAllActivatedSuppliers());
                request.setAttribute("products", pd.getAllProducts());
                request.setAttribute("importOrder", importOrder);
                request.getRequestDispatcher("ImportStockView.jsp").forward(request, response);
            } catch (NullPointerException e) {
                System.out.println(e);
            }

        } else if (request.getParameter("importStockId") != null) {
//        if (request.getParameter("id") != null) {
//            importId = Integer.parseInt(request.getParameter("id"));
//            io = ioD.getImportOrderByID(Integer.parseInt(request.getParameter("id")));
//            importOrder = ioD.getImportOrderDetailsByID(importId);
//            s = sd.getSupplierByID(io.getSupplierId());
//
//            try {
//                request.setAttribute("supplier", io.getSupplier());
//                request.setAttribute("suppliers", sd.getAllActivatedSuppliers());
//                request.setAttribute("products", pd.getAllProducts());
//                request.setAttribute("importOrder", importOrder);
//                request.getRequestDispatcher("ImportStockView.jsp").forward(request, response);
//            } catch (NullPointerException e) {
//                System.out.println(e);
//            }
//        } else if (request.getParameter("importStockId") != null) {
//            int count = ioD.importStock(Integer.parseInt(request.getParameter("importStockId")), iodD.calculateTotalPrice(importId));
//            if (count != -1) {
//                response.sendRedirect("ImportOrder?id=" + importId);
//            } else {
//                HttpSession session = request.getSession();
//                session.setAttribute("error", "There are no any product is imported");
//            }
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
//        processRequest(request, response);
        if (request.getParameter("supplierId") != null) {
            s = sd.getSupplierByID(Integer.parseInt(request.getParameter("supplierId")));
            io.setSupplier(s);
            ioD.updateImportOrderSupplier(Integer.parseInt(request.getParameter("supplierId")));
//            response.sendRedirect("UpdateImportOrder?id=" + importId);
//            return;
        }

        if (request.getParameter("deleteDetail") != null) {
            System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP Xoa ID" + request.getParameter("deleteDetail"));
            iodD.deleteDetailById(Integer.parseInt(request.getParameter("productDeletedId")), importId);
//            response.sendRedirect("UpdateImportOrder?id=" + importId);
//            return;
        } else {
            System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP 88888888888888888888888888");
            ImportOrderDetail updatedIOD = new ImportOrderDetail();
            updatedIOD.setProduct(pd.getProductByID(Integer.parseInt(request.getParameter("productEditedId"))));
            updatedIOD.setQuantity(Integer.parseInt(request.getParameter("quantity")));
            updatedIOD.setImportPrice(Integer.parseInt(request.getParameter("price")));
            iodD.updateDetailById(updatedIOD);
//            response.sendRedirect("UpdateImportOrder?id=" + importId);
//            return;
        }

        if (request.getParameter("productId") != null) {
            int pId = Integer.parseInt(request.getParameter("productId"));
            Product p = pd.getProductByID(pId);
            ImportOrderDetail d = new ImportOrderDetail();

            d.setIoid(importId);
            d.setProduct(p);
            d.setQuantity(Integer.parseInt(request.getParameter("importQuantity")));
            d.setImportPrice(Integer.parseInt(request.getParameter("importPrice")));

            boolean isContained = false;
            ArrayList<ImportOrderDetail> listDetail = iodD.getDetailsById(importId);

            for (ImportOrderDetail det : listDetail) {
                if (det.getProduct().getProductId() == pId) {
                    isContained = true;
                }
            }

            if (!isContained) {
                iodD.createImportOrderDetail(d);
            } else {
                String error = "Duplicated Product";
//                request.setAttribute("duplicatedProduct", error);
                HttpSession session = request.getSession();
                session.setAttribute("error", error);
            }
//            response.sendRedirect("UpdateImportOrder?id=" + importId);
        }

//        response.sendRedirect("UpdateImportOrder?id=" + importId);
        System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP    11232312323");

        try {
            request.setAttribute("supplier", s);
            request.setAttribute("suppliers", sd.getAllActivatedSuppliers());
            request.setAttribute("products", pd.getAllProducts());
            importOrder = ioD.getImportOrderDetailsByID(importId);
            request.setAttribute("importOrder", importOrder);
            request.getRequestDispatcher("ImportStockView.jsp").forward(request, response);
        } catch (NullPointerException e) {
            System.out.println(e);
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
