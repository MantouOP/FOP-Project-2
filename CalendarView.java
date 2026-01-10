import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles calendar display formatting and views
 */
public class CalendarView {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");

    /**
     * Display calendar in month view
     */
    public void displayMonthView(YearMonth yearMonth, List<Event> events) {
        System.out.println("\n" + MONTH_YEAR_FORMATTER.format(yearMonth).toUpperCase());
        System.out.println("Su Mo Tu We Th Fr Sa");

        // Group events by date for quick lookup
        Map<LocalDate, List<Event>> eventsByDate = events.stream()
                .collect(Collectors.groupingBy(e -> e.getStartDateTime().toLocalDate()));

        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Sunday = 0

        // Print leading spaces
        for (int i = 0; i < firstDayOfWeek; i++) {
            System.out.print("   ");
        }

        // Print days
        for (int day = 1; day <= lastDay.getDayOfMonth(); day++) {
            LocalDate currentDate = yearMonth.atDay(day);
            List<Event> dayEvents = eventsByDate.get(currentDate);
            
            if (dayEvents != null && !dayEvents.isEmpty()) {
                System.out.printf("%2d*", day);
            } else {
                System.out.printf("%2d ", day);
            }

            if ((firstDayOfWeek + day) % 7 == 0 || day == lastDay.getDayOfMonth()) {
                System.out.println();
            }
        }

        // Display events for the month
        displayEventsForMonth(eventsByDate, yearMonth);
    }

    /**
     * Display events for the month
     */
    private void displayEventsForMonth(Map<LocalDate, List<Event>> eventsByDate, YearMonth yearMonth) {
        System.out.println("\nEvents for " + MONTH_YEAR_FORMATTER.format(yearMonth) + ":");
        System.out.println("=" .repeat(40));

        eventsByDate.entrySet().stream()
                .filter(entry -> entry.getKey().getMonth() == yearMonth.getMonth() && 
                                entry.getKey().getYear() == yearMonth.getYear())
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    System.out.println(DATE_FORMATTER.format(entry.getKey()) + ":");
                    entry.getValue().forEach(event -> 
                        System.out.println("  " + TIME_FORMATTER.format(event.getStartDateTime()) + 
                                         " - " + event.getTitle())
                    );
                    System.out.println();
                });
    }

    /**
     * Display week view
     */
    public void displayWeekView(LocalDate weekStart, List<Event> events) {
        System.out.println("\n=== Week of " + DATE_FORMATTER.format(weekStart) + " ===");

        Map<LocalDate, List<Event>> eventsByDate = events.stream()
                .collect(Collectors.groupingBy(e -> e.getStartDateTime().toLocalDate()));

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStart.plusDays(i);
            List<Event> dayEvents = eventsByDate.getOrDefault(currentDate, List.of());

            String dayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // e.g., Sun
            String prefix = String.format("%s %02d: ", dayName, currentDate.getDayOfMonth());

            if (dayEvents.isEmpty()) {
                System.out.println(prefix + "No events");
            } else {
                String line = dayEvents.stream()
                        .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                        .map(ev -> ev.getTitle() + " (" + TIME_FORMATTER.format(ev.getStartDateTime()) + ")")
                        .collect(Collectors.joining(", "));
                System.out.println(prefix + line);
            }
        }
    }

    /**
     * Display day view
     */
    public void displayDayView(LocalDate date, List<Event> events) {
        System.out.println("\n=== " + date.getDayOfWeek() + ", " + DATE_FORMATTER.format(date) + " ===");

        List<Event> dayEvents = events.stream()
                .filter(e -> e.getStartDateTime().toLocalDate().equals(date))
                .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                .collect(Collectors.toList());

        if (dayEvents.isEmpty()) {
            System.out.println("No events for this day.");
        } else {
            for (Event event : dayEvents) {
                System.out.printf("%s - %s: %s%n",
                    TIME_FORMATTER.format(event.getStartDateTime()),
                    TIME_FORMATTER.format(event.getEndDateTime()),
                    event.getTitle());
                if (!event.getDescription().isEmpty()) {
                    System.out.println("  " + event.getDescription());
                }
                System.out.println();
            }
        }
    }

    /**
     * Display list view for a date range
     */
    public void displayListView(LocalDate startDate, LocalDate endDate, List<Event> events) {
        System.out.printf("\n=== Events from %s to %s ===%n", 
            DATE_FORMATTER.format(startDate), DATE_FORMATTER.format(endDate));

        List<Event> filteredEvents = events.stream()
                .filter(e -> {
                    LocalDate eventDate = e.getStartDateTime().toLocalDate();
                    return !eventDate.isBefore(startDate) && !eventDate.isAfter(endDate);
                })
                .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                .collect(Collectors.toList());

        if (filteredEvents.isEmpty()) {
            System.out.println("No events found in the specified date range.");
        } else {
            for (Event event : filteredEvents) {
                System.out.printf("%s %s: %s%n",
                    DATE_FORMATTER.format(event.getStartDateTime().toLocalDate()),
                    TIME_FORMATTER.format(event.getStartDateTime()),
                    event.getTitle());
                if (!event.getDescription().isEmpty()) {
                    System.out.println("  " + event.getDescription());
                }
                System.out.println();
            }
        }
    }

    /**
     * Display search results
     */
    public void displaySearchResults(List<Event> results, String searchType) {
        System.out.println("\n=== Search Results (" + searchType + ") ===");
        
        if (results.isEmpty()) {
            System.out.println("No events found.");
        } else {
            for (Event event : results) {
                System.out.printf("ID: %d | %s %s: %s%n",
                    event.getEventId(),
                    DATE_FORMATTER.format(event.getStartDateTime().toLocalDate()),
                    TIME_FORMATTER.format(event.getStartDateTime()),
                    event.getTitle());
                if (!event.getDescription().isEmpty()) {
                    System.out.println("  " + event.getDescription());
                }
                System.out.println();
            }
        }
    }

    /**
     * Display event details
     */
    public void displayEventDetails(Event event) {
        System.out.println("\n=== Event Details ===");
        System.out.println("ID: " + event.getEventId());
        System.out.println("Title: " + event.getTitle());
        System.out.println("Description: " + event.getDescription());
        System.out.println("Start: " + event.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        System.out.println("End: " + event.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    /**
     * Display calendar in week view (grid format)
     */
    public void displayWeekCalendarView(LocalDate weekStart, List<Event> events) {
        // Calculate Monday of the week
        LocalDate mondayOfWeek = weekStart.minusDays(weekStart.getDayOfWeek().getValue() - 1);

        System.out.println("\n=== Week of " + DATE_FORMATTER.format(mondayOfWeek) + " ===");
        System.out.println("Su Mo Tu We Th Fr Sa");

        // Group events by date for quick lookup
        Map<LocalDate, List<Event>> eventsByDate = events.stream()
                .collect(Collectors.groupingBy(e -> e.getStartDateTime().toLocalDate()));

        // Display 7 days starting from Monday (convert to Sunday-Saturday order for display)
        // We need to start from Sunday, so if mondayOfWeek is Monday (1), we go back 1 day to Sunday
        LocalDate sundayOfWeek = mondayOfWeek.minusDays(1);

        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = sundayOfWeek.plusDays(i);
            List<Event> dayEvents = eventsByDate.get(currentDate);

            if (dayEvents != null && !dayEvents.isEmpty()) {
                System.out.printf("%2d*", currentDate.getDayOfMonth());
            } else {
                System.out.printf("%2d ", currentDate.getDayOfMonth());
            }
        }
        System.out.println();

        // Display events for the week
        displayEventsForWeekCalendar(eventsByDate, mondayOfWeek);
    }

    /**
     * Display events for the week calendar
     */
    private void displayEventsForWeekCalendar(Map<LocalDate, List<Event>> eventsByDate, LocalDate mondayOfWeek) {
        System.out.println("\nEvents for this week:");
        System.out.println("=" .repeat(40));

        LocalDate sundayOfWeek = mondayOfWeek.minusDays(1);

        boolean hasEvents = false;
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = sundayOfWeek.plusDays(i);
            List<Event> dayEvents = eventsByDate.get(currentDate);

            if (dayEvents != null && !dayEvents.isEmpty()) {
                hasEvents = true;
                String dayName = currentDate.getDayOfWeek().toString();
                System.out.println(dayName + " " + DATE_FORMATTER.format(currentDate) + ":");
                dayEvents.forEach(event ->
                    System.out.println("  " + TIME_FORMATTER.format(event.getStartDateTime()) +
                                     " - " + event.getTitle())
                );
                System.out.println();
            }
        }

        if (!hasEvents) {
            System.out.println("No events for this week.");
        }
    }
}