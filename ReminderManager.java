import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages event reminders and notifications
 */
public class ReminderManager {
    private EventManager eventManager;

    public ReminderManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    /**
     * Check for upcoming events and display reminders
     */
    public void checkUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> allEvents = eventManager.getAllEvents();
        
        List<Event> upcomingEvents = new ArrayList<>();
        
        for (Event event : allEvents) {
            if (event.getStartDateTime().isAfter(now)) {
                upcomingEvents.add(event);
            }
        }
        
        if (upcomingEvents.isEmpty()) {
            System.out.println("No upcoming events.");
            return;
        }
        
        // Sort by start time
        upcomingEvents.sort((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()));
        
        System.out.println("\n=== UPCOMING EVENTS REMINDER ===");
        
        // Show next 3 upcoming events
        int count = Math.min(3, upcomingEvents.size());
        for (int i = 0; i < count; i++) {
            Event event = upcomingEvents.get(i);
            Duration duration = Duration.between(now, event.getStartDateTime());
            
            String timeUntil = formatDuration(duration);
            System.out.printf("%s. %s - %s (in %s)%n",
                i + 1,
                event.getStartDateTime().toLocalDate(),
                event.getTitle(),
                timeUntil
            );
        }
    }

    /**
     * Get events that need reminders within specified minutes
     */
    public List<Event> getEventsNeedingReminder(int minutesBefore) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusMinutes(minutesBefore);
        List<Event> allEvents = eventManager.getAllEvents();
        List<Event> eventsNeedingReminder = new ArrayList<>();
        
        for (Event event : allEvents) {
            LocalDateTime eventStart = event.getStartDateTime();
            
            // Check if event starts within the reminder window
            if (eventStart.isAfter(now) && 
                (eventStart.isBefore(reminderTime) || eventStart.isEqual(reminderTime))) {
                eventsNeedingReminder.add(event);
            }
        }
        
        return eventsNeedingReminder;
    }

    /**
     * Display reminders for events starting soon
     */
    public void displayReminders(int minutesBefore) {
        List<Event> reminderEvents = getEventsNeedingReminder(minutesBefore);
        
        if (reminderEvents.isEmpty()) {
            return;
        }
        
        System.out.println("\n*** REMINDERS ***");
        for (Event event : reminderEvents) {
            System.out.printf("- %s starts in less than %d minutes!%n",
                event.getTitle(), minutesBefore);
            if (!event.getDescription().isEmpty()) {
                System.out.println("   " + event.getDescription());
            }
        }
    }

    /**
     * Format duration into human-readable string
     */
    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        
        StringBuilder sb = new StringBuilder();
        
        if (days > 0) {
            sb.append(days).append(" day");
            if (days > 1) sb.append("s");
            if (hours > 0 || minutes > 0) sb.append(" ");
        }
        
        if (hours > 0) {
            sb.append(hours).append(" hour");
            if (hours > 1) sb.append("s");
            if (minutes > 0) sb.append(" ");
        }
        
        if (minutes > 0 || (days == 0 && hours == 0)) {
            sb.append(minutes).append(" minute");
            if (minutes != 1) sb.append("s");
        }
        
        return sb.toString();
    }

    /**
     * Get events for today
     */
    public List<Event> getTodayEvents() {
        return eventManager.searchEventsByDate(LocalDateTime.now().toLocalDate());
    }

    /**
     * Display startup notification for next upcoming event
     */
    public void displayStartupNotification() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> allEvents = eventManager.getAllEvents();

        // Find next event after now
        Event nextEvent = null;
        Duration timeUntilNext = null;

        for (Event event : allEvents) {
            if (event.getStartDateTime().isAfter(now)) {
                Duration duration = Duration.between(now, event.getStartDateTime());
                if (nextEvent == null || duration.compareTo(timeUntilNext) < 0) {
                    nextEvent = event;
                    timeUntilNext = duration;
                }
            }
        }

        if (nextEvent != null) {
            String durationStr = formatDuration(timeUntilNext);
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║           🔔 UPCOMING EVENT NOTIFICATION 🔔               ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.printf("Your next event is coming soon in %s%n", durationStr);
            System.out.printf("📌 Event: %s%n", nextEvent.getTitle());
            System.out.printf("📅 Date: %s%n", nextEvent.getStartDateTime().toLocalDate());
            System.out.printf("🕐 Time: %s%n", nextEvent.getStartDateTime().toLocalTime());
            if (!nextEvent.getDescription().isEmpty()) {
                System.out.printf("📝 Details: %s%n", nextEvent.getDescription());
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        } else {
            System.out.println("\n✓ No upcoming events scheduled.\n");
        }
    }

    /**
     * Get next upcoming event with duration
     */
    public Event getNextEvent() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> allEvents = eventManager.getAllEvents();

        Event nextEvent = null;
        Duration timeUntilNext = null;

        for (Event event : allEvents) {
            if (event.getStartDateTime().isAfter(now)) {
                Duration duration = Duration.between(now, event.getStartDateTime());
                if (nextEvent == null || duration.compareTo(timeUntilNext) < 0) {
                    nextEvent = event;
                    timeUntilNext = duration;
                }
            }
        }

        return nextEvent;
    }

    /**
     * Get formatted duration string to next event
     */
    public String getNextEventDuration() {
        Event nextEvent = getNextEvent();
        if (nextEvent == null) {
            return "No upcoming events";
        }
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, nextEvent.getStartDateTime());
        return formatDuration(duration);
    }

    /**
     * Display today's schedule at startup
     */
    public void displayTodaySchedule() {
        List<Event> todayEvents = getTodayEvents();
        
        System.out.println("\n=== TODAY'S SCHEDULE ===");
        
        if (todayEvents.isEmpty()) {
            System.out.println("No events scheduled for today.");
        } else {
            todayEvents.sort((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()));
            
            for (Event event : todayEvents) {
                String status = getEventStatus(event);
                System.out.printf("%s %s - %s: %s %s%n",
                    status,
                    event.getStartDateTime().toLocalTime(),
                    event.getEndDateTime().toLocalTime(),
                    event.getTitle(),
                    event.getStartDateTime().isBefore(LocalDateTime.now()) ? "(✓ Completed)" : ""
                );
            }
        }
    }

    /**
     * Get status indicator for event
     */
    private String getEventStatus(Event event) {
        LocalDateTime now = LocalDateTime.now();
        
        if (event.getEndDateTime().isBefore(now)) {
            return "✓";
        } else if (event.getStartDateTime().isBefore(now) && event.getEndDateTime().isAfter(now)) {
            return "→";
        } else {
            return "○";
        }
    }
}