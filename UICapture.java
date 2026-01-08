import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class UICapture {
    public static void main(String[] args) {
        EventManager em = new EventManager();
        CalendarView view = new CalendarView();

        // Sample events across December 2025
        em.createEvent("Project Kickoff", "Initial meeting", LocalDateTime.of(2025, 12, 3, 9, 0), LocalDateTime.of(2025, 12, 3, 10, 0));
        em.createEvent("Design Review", "Review designs", LocalDateTime.of(2025, 12, 15, 14, 0), LocalDateTime.of(2025, 12, 15, 15, 30));
        em.createEvent("Team Lunch", "Holiday lunch", LocalDateTime.of(2025, 12, 18, 12, 0), LocalDateTime.of(2025, 12, 18, 13, 0));
        em.createEvent("Demo", "Product demo", LocalDateTime.of(2025, 12, 29, 16, 0), LocalDateTime.of(2025, 12, 29, 17, 0));

        List<Event> allEvents = em.getAllEvents();

        System.out.println("=== MONTH VIEW (Dec 2025) ===");
        view.displayMonthView(YearMonth.of(2025, 12), allEvents);

        System.out.println("\n=== LIST VIEW (2025-12-14 to 2025-12-20) ===");
        view.displayListView(LocalDate.of(2025, 12, 14), LocalDate.of(2025, 12, 20), allEvents);

        System.out.println("\n=== WEEK VIEW (start 2025-12-15) ===");
        view.displayWeekView(LocalDate.of(2025, 12, 15), allEvents);

        System.out.println("\n=== DAY VIEW (2025-12-18) ===");
        view.displayDayView(LocalDate.of(2025, 12, 18), allEvents);
    }
}