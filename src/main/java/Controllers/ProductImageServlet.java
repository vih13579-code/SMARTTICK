package Controllers;

import Utils.ProductImageStorage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductImageServlet", urlPatterns = {"/product-images/*"})
public class ProductImageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String fileName = pathInfo == null ? "" : pathInfo.substring(pathInfo.lastIndexOf('/') + 1);
        Path uploaded = ProductImageStorage.resolve(fileName);
        if (uploaded != null && Files.isRegularFile(uploaded)) {
            String contentType = Files.probeContentType(uploaded);
            response.setContentType(contentType == null ? "application/octet-stream" : contentType);
            response.setHeader("Cache-Control", "public, max-age=86400");
            Files.copy(uploaded, response.getOutputStream());
            return;
        }

        String seedPath = "/assets/imgs/Products/watches/" + fileName;
        try (InputStream input = getServletContext().getResourceAsStream(seedPath)) {
            if (input == null) {
                response.sendRedirect(request.getContextPath() + "/assets/imgs/Products/watches/watch-placeholder.svg");
                return;
            }
            String mime = getServletContext().getMimeType(fileName);
            response.setContentType(mime == null ? "image/svg+xml" : mime);
            response.setHeader("Cache-Control", "public, max-age=86400");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
        }
    }
}
