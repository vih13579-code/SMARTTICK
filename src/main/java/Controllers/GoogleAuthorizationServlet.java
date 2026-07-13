package Controllers;

import DAOs.GoogleLogin;
import DAOs.Iconstant;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author TranVTH
 */

@WebServlet(name = "GoogleAuthorizationServlet", urlPatterns = {"/google-auth"})
public class GoogleAuthorizationServlet extends HttpServlet {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!Iconstant.isConfigured()) {
            HttpSession session = request.getSession();
            session.setAttribute("message",
                    "Google login is not configured yet. Please set SMARTTICK_GOOGLE_CLIENT_ID, SMARTTICK_GOOGLE_CLIENT_SECRET and SMARTTICK_GOOGLE_REDIRECT_URI.");
            response.sendRedirect(resolveReturnPath(request));
            return;
        }

        HttpSession session = request.getSession(true);
        String state = generateState();
        session.setAttribute("googleOAuthState", state);
        session.setAttribute("googleOAuthSource", resolveSource(request));
        response.sendRedirect(new GoogleLogin().buildAuthorizationUrl(state));
    }

    private String resolveReturnPath(HttpServletRequest request) {
        String source = resolveSource(request);
        return request.getContextPath() + ("register".equals(source) ? "/register" : "/customerLogin");
    }

    private String resolveSource(HttpServletRequest request) {
        String source = request.getParameter("source");
        return "register".equalsIgnoreCase(source) ? "register" : "login";
    }

    private String generateState() {
        byte[] bytes = new byte[24];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
