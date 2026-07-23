/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Controllers;

import DAOs.StockDAO;
import Models.Stock;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportStockToFileServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
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
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
//        StockDAO stockDAO = new StockDAO();
//        List <Stock> list = stockDAO.GetAll();
//        if(list != null){
//            request.setAttribute("dataStock", list);
//            request.getRequestDispatcher("excelStock.jsp").forward(request, response);
//        }else {
//          response.sendRedirect(request.getContextPath() + "/ExportStockToFileServlet");
//                }
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
         response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=StockReportForWarehouse.xlsx");

        XSSFWorkbook b = new XSSFWorkbook();
        XSSFSheet sheet = b.createSheet("list");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StockDAO stockDAO = new StockDAO();
        List<Stock> list = stockDAO.GetAll();

        int rowNo = 0;
        XSSFRow row = sheet.createRow(rowNo++);
        int cellNum = 0;
        XSSFCell cell = row.createCell(cellNum++);
        cell.setCellValue("IOID");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Employee Name");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Supplier Name");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Import Date");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Total Cost");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Product ID");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Product Name");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Brand Name");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Category Name");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Quantity");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Import Price");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Retail Price");

        cell = row.createCell(cellNum++);
        cell.setCellValue("Profit Margin");

        for (Stock stock : list) {
            cellNum = 0;
            row = sheet.createRow(rowNo++);

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getIOID());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getEmployeeName());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getSupplierName());

            cell = row.createCell(cellNum++);
            cell.setCellValue(sdf.format(stock.getImportDate()));

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getTotalCost());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getProductID());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getProductName());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getBrandName());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getCategoryName());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getQuantity());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getImportPrice());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getRetailPrice());

            cell = row.createCell(cellNum++);
            cell.setCellValue(stock.getProfitMargin());
        }

        b.write(response.getOutputStream());
        b.close();
    }

    /** 
     * Returns the servlet description.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "SMARTTICK servlet";
    }// </editor-fold>

}
