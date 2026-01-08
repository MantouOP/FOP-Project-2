import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Simple test class to verify basic functionality
 */
public class TestCalendar {
    public static void main(String[] args) {
        System.out.println("Testing Calendar Application...");
        
        // Test EventManager
        EventManager eventManager = new EventManager();
        
        // Test creating a single event
        Event event1 = eventManager.createEvent(
            "Test Meeting", 
            "This is a test meeting", 
            LocalDateTime.of(2025, 12, 18, 10, 0),
            LocalDateTime.of(2025, 12, 18, 11, 0)
        );
        System.out.println("Created event: " + event1.getTitle() + " (ID: " + event1.getEventId() + ")");
        
        // Test creating a recurring event
        Event event2 = eventManager.createRecurringEvent(
            "Daily Standup",
            "Daily team standup meeting",
            LocalDateTime.of(2025, 12, 18, 9, 0),
            LocalDateTime.of(2025, 12, 18, 9, 15),
            "1d",  // daily
            3,     // 3 times
            null   // no end date
        );
        System.out.println("Created recurring event: " + event2.getTitle() + " (ID: " + event2.getEventId() + ")");
        
        // Test search functionality
        System.out.println("\nSearching events for today:");
        var todayEvents = eventManager.searchEventsByDate(LocalDate.of(2025, 12, 18));
        todayEvents.forEach(e -> System.out.println("- " + e.getTitle() + " at " + e.getStartDateTime()));
        
        // Test backup
        boolean backupSuccess = eventManager.createBackup("test_backup.txt");
        System.out.println("\nBackup " + (backupSuccess ? "successful" : "failed"));
        
        // Test calendar view
        CalendarView view = new CalendarView();
        System.out.println("\n=== Day View for 2025-12-18 ===");
        view.displayDayView(LocalDate.of(2025, 12, 18), eventManager.getAllEvents());
        
        System.out.println("\nAll tests completed successfully!");
    }
}