package Controllers;

import Utils.ProductImageStorage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProductImageServlet", urlPatterns = {"/product-images/*"})
public class ProductImageServlet extends HttpServlet {
    private static final Map<String, String[]> REAL_SEED_IMAGES;

    static {
        Map<String, String[]> images = new HashMap<>();
        images.put("casio-mtp-1302", new String[]{
            "Casio/61B9UZfwQlL.jpg", "Casio/61eyqQgAJ9L.jpg",
            "Casio/61eyqQgAJ9L (1).jpg", "Casio/512XPKCCAtL.jpg"
        });
        images.put("casio-edifice-efv", new String[]{
            "Casio/efv-100d-7avudf_bc099dee3ddd4b71811870fe66b56d42_master.jpg",
            "Casio/ytr5u_bd2a693003b044228ccc3136ddedb8d6_master.jpg",
            "Casio/yuigui_b0c54adff69c441381265746101eeaaa_master.jpg",
            "Casio/tu6u7r6_218fee120740481382562f10bd5cceac_master.jpg"
        });
        images.put("citizen-tsuyosa", new String[]{
            "Citizen/dong-ho-nam-citizen-mechanical-automatic-watch-nj0150-81e-mau-bac-den-67bd6c5c34c05-25022025140812.webp",
            "Citizen/dong-ho-nam-citizen-mechanical-automatic-watch-nj0150-81e-mau-bac-den-67bd6c5c3510c-25022025140812.webp",
            "Citizen/dong-ho-nam-citizen-mechanical-automatic-watch-nj0150-81e-mau-bac-den-67bd6c5c35409-25022025140812.webp",
            "Citizen/dong-ho-nam-citizen-mechanical-automatic-watch-nj0150-81e-mau-bac-den-67bd6c5c356ea-25022025140812.webp"
        });
        images.put("citizen-eco-drive", new String[]{
            "Citizen/dong-ho-nam-citizen-eco-drive-at8110-02e-at811002e-mau-den-68366c7841750-28052025085256.webp",
            "Citizen/dong-ho-nam-citizen-eco-drive-at8110-02e-at811002e-mau-den-68366c7841cff-28052025085256.webp",
            "Citizen/dong-ho-nam-citizen-eco-drive-at8110-02e-at811002e-mau-den-68366c784245f-28052025085256.webp",
            "Citizen/dong-ho-nam-citizen-eco-drive-at8110-02e-at811002e-mau-den-68366c78429f7-28052025085256.webp"
        });
        images.put("seiko-5-sports", new String[]{
            "Seiko/dong-ho-seiko-5-automatic-snkk31k1-5cdd48a97fd6e-16052019182529.webp",
            "Seiko/dong-ho-seiko-5-automatic-snkk31k1-5cdd48a986a4a-16052019182529.webp",
            "Seiko/dong-ho-seiko-5-automatic-snkk31k1-5cdd48a986a7a-16052019182529.webp",
            "Seiko/dong-ho-seiko-5-automatic-snkk31k1-5cdd48a991419-16052019182529.webp"
        });
        images.put("seiko-presage", new String[]{
            "Seiko/dong-ho-seiko-presage-cocktail-ssa346j1-kim-xang-62174617d582a-24022022154719.webp",
            "Seiko/s-l1600-1-207723258-508315809-1712591764-150x150.jpg",
            "Seiko/s-l1600-2-876417409-1536838001-1712591763-150x150.jpg",
            "Seiko/s-l1600-3-1219457712-1982248726-1712591762-150x150.jpg"
        });
        images.put("orient-bambino", new String[]{
            "Orient/dong-ho-orient-bambino-ra-ac0028s30b-1.jpg",
            "Orient/dong-ho-orient-bambino-ra-ac0028s30b-2.jpg",
            "Orient/dong-ho-orient-bambino-ra-ac0028s30b-3.jpg",
            "Orient/dong-ho-orient-bambino-ra-ac0028s30b-1.jpg"
        });
        images.put("orient-mako", new String[]{
            "Orient/Orient Mako 3 RN-AA0001B (RA-AA0001B19B) (1)_1729246126439.jpg",
            "Orient/Orient Mako 3 RN-AA0001B (RA-AA0001B19B) (2).jpg",
            "Orient/Orient Mako 3 RN-AA0001B (RA-AA0001B19B) (2)_1729246125294.jpg",
            "Orient/Orient Mako 3 RN-AA0001B (RA-AA0001B19B) (9).jpg"
        });
        images.put("tissot-prx", new String[]{
            "Tissot/t137-410-16-041-00-7-1670320785827-1712588603-150x150.jpg",
            "Tissot/t137-410-16-041-00-8-1670320768482-1712588602-150x150.jpg",
            "Tissot/t137-410-16-041-00-10-1670320780200-1712588603-150x150.jpg",
            "Tissot/t137-410-16-041-00-11-1670320775687-1712588602-150x150.jpg"
        });
        images.put("tissot-le-locle", new String[]{
            "Tissot/t006-407-11-033-00-1_1720693929-150x150.jpg",
            "Tissot/dong-ho-tissot-t006-407-11-033-00_2-ims-1712566650-150x150.jpg",
            "Tissot/dong-ho-tissot-t006-407-11-033-00_3-ims-1712566650-150x150.jpg",
            "Tissot/t006-1012758818-644885444-1712566651-150x150.jpg"
        });
        images.put("dw-petite", new String[]{
            "Daniel Wellington/dw00100304-1geotagktdv-721935525-1311607919-1712665793-150x150.jpg",
            "Daniel Wellington/dw00100304-2geotagktdv-1712665793-150x150.jpg",
            "Daniel Wellington/dw00100304-3geotagktdv-1712665794-150x150.jpg",
            "Daniel Wellington/dw00100304-4geotagktdv-1712665794-150x150.jpg"
        });
        images.put("dw-iconic", repeated("Daniel Wellington/DW00100437-150x150.webp"));
        images.put("casio-gshock", new String[]{
            "Casio/CASIO-GA-2100-1A1-150x150.jpg",
            "Casio/casio-ga-2100-1a1dr-150x150.jpg",
            "Casio/ga-2100-1a1-3-1712568326-150x150.jpg",
            "Casio/ga-2100-1a1-4-1712568327-150x150.jpg"
        });
        images.put("seiko-ladies", new String[]{
            "Seiko/1-1677413705609-1712591760-150x150.jpg",
            "Seiko/5cfe0226d6bca-10062019140926.webp",
            "Seiko/5cfe0226d7dbe-10062019140926.webp",
            "Seiko/5cfe0226dc08b-10062019140926.webp"
        });
        images.put("citizen-ladies", new String[]{
            "Citizen/dong-ho-citizen-em0500-73a-chinh-hang-2-1652263109304-1712584689-150x150.jpg",
            "Citizen/em0500-73a-1712584689-150x150.jpg",
            "Citizen/dong-ho-citizen-em0500-73a-chinh-hang-2-1652263109304-1712584689-150x150.jpg",
            "Citizen/em0500-73a-1712584689-150x150.jpg"
        });
        images.put("orient-star", new String[]{
            "Orient/1-khung-sp-2107913585-2130083978-1712568700-150x150.jpg",
            "Orient/re-av0004n00b-2-1639189956730-1712568697-150x150.jpg",
            "Orient/re-av0004n00b-3-1639189963038-1712568697-150x150.jpg",
            "Orient/re-av0004n00b-4-1639189966060-1712568698-150x150.jpg"
        });
        REAL_SEED_IMAGES = Collections.unmodifiableMap(images);
    }

    private static String[] repeated(String path) {
        return new String[]{path, path, path, path};
    }

    private static String realSeedPath(String fileName) {
        String[] variants = {"-main.svg", "-side.svg", "-back.svg", "-detail.svg"};
        for (int i = 0; i < variants.length; i++) {
            if (fileName.endsWith(variants[i])) {
                String prefix = fileName.substring(0, fileName.length() - variants[i].length());
                String[] images = REAL_SEED_IMAGES.get(prefix);
                return images == null ? null : "/assets/imgs/Products/real/" + images[i];
            }
        }
        return null;
    }

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

        String realPath = realSeedPath(fileName);
        String seedPath = realPath == null
                ? "/assets/imgs/Products/watches/" + fileName : realPath;
        try (InputStream input = getServletContext().getResourceAsStream(seedPath)) {
            if (input == null) {
                response.sendRedirect(request.getContextPath() + "/assets/imgs/Products/watches/watch-placeholder.svg");
                return;
            }
            String mime = getServletContext().getMimeType(seedPath);
            response.setContentType(mime == null ? "application/octet-stream" : mime);
            response.setHeader("Cache-Control", "no-cache");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }
        }
    }
}
