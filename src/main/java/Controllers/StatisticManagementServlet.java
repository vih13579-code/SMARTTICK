package Controllers;

import DAOs.StatisticDAO;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CE181159-Nguyen Le Duy Minh
 * @since 2026-06-29
 */
public class StatisticManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String period = request.getParameter("period");
        if (period == null || period.trim().isEmpty()) {
            period = "month";
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDateExclusive;
        switch (period) {
            case "today":
                startDate = today;
                endDateExclusive = today.plusDays(1);
                break;
            case "yesterday":
                startDate = today.minusDays(1);
                endDateExclusive = today;
                break;
            case "week":
                startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
                endDateExclusive = startDate.plusDays(7);
                break;
            case "year":
                startDate = today.with(TemporalAdjusters.firstDayOfYear());
                endDateExclusive = startDate.plusYears(1);
                break;
            case "custom":
                startDate = parseDate(request.getParameter("startDate"), today.withDayOfMonth(1));
                endDateExclusive = parseDate(request.getParameter("endDate"), today).plusDays(1);
                if (!endDateExclusive.isAfter(startDate)) {
                    endDateExclusive = startDate.plusDays(1);
                }
                break;
            case "month":
            default:
                period = "month";
                startDate = today.with(TemporalAdjusters.firstDayOfMonth());
                endDateExclusive = startDate.plusMonths(1);
                break;
        }

        StatisticDAO dao = new StatisticDAO();
        Gson gson = new Gson();
        Map<String, Long> overview = dao.getOverview(startDate, endDateExclusive);
        request.setAttribute("overview", overview);
        request.setAttribute("revenueChartJson", gson.toJson(dao.getDailyRevenue(startDate, endDateExclusive)));
        request.setAttribute("ordersChartJson", gson.toJson(dao.getDailyOrders(startDate, endDateExclusive)));
        request.setAttribute("customerGrowthJson", gson.toJson(dao.getCustomerGrowth(startDate, endDateExclusive)));
        request.setAttribute("topProducts", dao.getTopSellingProducts(startDate, endDateExclusive, 5));
        request.setAttribute("topCustomers", dao.getTopCustomers(startDate, endDateExclusive, 5));
        request.setAttribute("period", period);
        request.setAttribute("startDate", startDate.toString());
        request.setAttribute("endDate", endDateExclusive.minusDays(1).toString());
        request.getRequestDispatcher("StatisticManagementView.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private LocalDate parseDate(String value, LocalDate fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            return fallback;
        }
    }
}
