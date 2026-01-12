import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages all event operations including CRUD and recurring events
 */
public class EventManager {
    private CSVHandler csvHandler;
    private List<Event> events;
    private List<RecurringEvent> recurringEvents;
    private int nextEventId;

    public EventManager() {
        this.csvHandler = new CSVHandler();
        loadData();
        updateNextEventId();
    }

    /**
     * Update the next event ID based on existing events
     * Resets to 1 if no events exist
     */
    private void updateNextEventId() {
        // If no events exist, reset to 1
        if (events.isEmpty()) {
            nextEventId = 1;
            return;
        }

        // Otherwise, find the max ID and increment
        int maxId = 0;
        for (Event event : events) {
            if (event.getEventId() > maxId) {
                maxId = event.getEventId();
            }
        }
        nextEventId = maxId + 1;
    }

    /**
     * Get the next event ID and increment the counter
     */
    private int getAndIncrementEventId() {
        return nextEventId++;
    }

    /**
     * Load data from CSV files
     */
    private void loadData() {
        events = csvHandler.readEvents();
        recurringEvents = csvHandler.readRecurringEvents();
        updateNextEventId();
    }

    /**
     * Save data to CSV files
     */
    private void saveData() {
        csvHandler.writeEvents(events);
        csvHandler.writeRecurringEvents(recurringEvents);
    }

    /**
     * Create a new event
     */
    public Event createEvent(String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        int eventId = getAndIncrementEventId();
        Event event = new Event(eventId, title, description, startDateTime, endDateTime);
        events.add(event);
        saveData();
        return event;
    }

    /**
     * Create a new recurring event
     */
    public Event createRecurringEvent(String title, String description, LocalDateTime startDateTime, 
                                     LocalDateTime endDateTime, String recurrentInterval, 
                                     int recurrentTimes, LocalDate recurrentEndDate) {
        // Create the main event
        Event mainEvent = createEvent(title, description, startDateTime, endDateTime);
        
        // Create recurring configuration
        RecurringEvent recurringEvent = new RecurringEvent(mainEvent.getEventId(), recurrentInterval, 
                                                         recurrentTimes, recurrentEndDate);
        recurringEvents.add(recurringEvent);
        
        // Generate recurring event instances
        generateRecurringInstances(mainEvent, recurringEvent);
        
        saveData();
        return mainEvent;
    }

    /**
     * Generate instances of recurring events
     */
    private void generateRecurringInstances(Event baseEvent, RecurringEvent recurringConfig) {
        LocalDateTime currentStart = baseEvent.getStartDateTime();
        LocalDateTime currentEnd = baseEvent.getEndDateTime();
        int instancesCreated = 0;

        while (true) {
            // Check if we should stop based on times or end date
            if (recurringConfig.getRecurrentTimes() > 0 && 
                instancesCreated >= recurringConfig.getRecurrentTimes()) {
                break;
            }
            
            if (recurringConfig.getRecurrentEndDate() != null && 
                currentStart.toLocalDate().isAfter(recurringConfig.getRecurrentEndDate())) {
                break;
            }

            // Skip the first instance as it's already created
            if (instancesCreated > 0) {
                int eventId = getAndIncrementEventId();
                Event recurringInstance = new Event(eventId, baseEvent.getTitle(),
                                                  baseEvent.getDescription(), currentStart, currentEnd);
                events.add(recurringInstance);
            }

            // Calculate next occurrence
            currentStart = calculateNextDateTime(currentStart, recurringConfig);
            currentEnd = calculateNextDateTime(currentEnd, recurringConfig);
            instancesCreated++;
        }
    }

    /**
     * Calculate the next date/time based on recurring interval
     */
    private LocalDateTime calculateNextDateTime(LocalDateTime dateTime, RecurringEvent recurringConfig) {
        int value = recurringConfig.getIntervalValue();
        char type = recurringConfig.getIntervalType();

        switch (type) {
            case 'd': // days
                return dateTime.plusDays(value);
            case 'w': // weeks
                return dateTime.plusWeeks(value);
            case 'm': // months
                return dateTime.plusMonths(value);
            default:
                throw new IllegalArgumentException("Invalid recurring interval type: " + type);
        }
    }

    /**
     * Update an existing event
     */
    public boolean updateEvent(int eventId, String title, String description, 
                              LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Event event = findEventById(eventId);
        if (event != null) {
            event.setTitle(title);
            event.setDescription(description);
            event.setStartDateTime(startDateTime);
            event.setEndDateTime(endDateTime);
            saveData();
            return true;
        }
        return false;
    }

    /**
     * Delete an event
     */
    public boolean deleteEvent(int eventId) {
        Event event = findEventById(eventId);
        if (event != null) {
            events.remove(event);
            
            // Also remove recurring configuration if it exists
            recurringEvents.removeIf(re -> re.getEventId() == eventId);
            
            // Reset event ID counter if no events remain
            updateNextEventId();

            saveData();
            return true;
        }
        return false;
    }

    /**
     * Find event by ID
     */
    public Event findEventById(int eventId) {
        return events.stream()
                   .filter(e -> e.getEventId() == eventId)
                   .findFirst()
                   .orElse(null);
    }

    /**
     * Find recurring event configuration by event ID
     */
    public RecurringEvent getRecurringEventByEventId(int eventId) {
        return recurringEvents.stream()
                   .filter(re -> re.getEventId() == eventId)
                   .findFirst()
                   .orElse(null);
    }

    /**
     * Get all events
     */
    public List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }

    /**
     * Search events by date
     */
    public List<Event> searchEventsByDate(LocalDate date) {
        // Return events where the event interval overlaps the requested date
        return events.stream()
                   .filter(e -> {
                       LocalDate start = e.getStartDateTime().toLocalDate();
                       LocalDate end = e.getEndDateTime().toLocalDate();
                       // Overlap if event start <= date && event end >= date
                       return (!start.isAfter(date)) && (!end.isBefore(date));
                   })
                   .collect(Collectors.toList());
    }

    /**
     * Search events by date range
     */
    public List<Event> searchEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        // Return events where [eventStart, eventEnd] overlaps [startDate, endDate]
        return events.stream()
                   .filter(e -> {
                       LocalDate eventStart = e.getStartDateTime().toLocalDate();
                       LocalDate eventEnd = e.getEndDateTime().toLocalDate();
                       // overlap if eventStart <= endDate && eventEnd >= startDate
                       return (!eventStart.isAfter(endDate)) && (!eventEnd.isBefore(startDate));
                   })
                   .collect(Collectors.toList());
    }

    /**
     * Search events by title
     */
    public List<Event> searchEventsByTitle(String keyword) {
        return events.stream()
                   .filter(e -> e.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                   .collect(Collectors.toList());
    }

    /**
     * Create backup
     */
    public boolean createBackup(String backupFilePath) {
        return csvHandler.createBackup(backupFilePath);
    }

    /**
     * Restore from backup
     */
    public boolean restoreFromBackup(String backupFilePath, boolean append) {
        boolean success = csvHandler.restoreFromBackup(backupFilePath, append);
        if (success) {
            loadData(); // Reload data after restore
        }
        return success;
    }

    /**
     * Check for event conflicts
     */
    public List<Event> checkConflicts(LocalDateTime start, LocalDateTime end) {
        return events.stream()
                   .filter(e -> {
                       return (start.isBefore(e.getEndDateTime()) && end.isAfter(e.getStartDateTime()));
                   })
                   .collect(Collectors.toList());
    }

    /**
     * Get the next event ID that will be assigned
     * Useful for testing and debugging
     */
    public int getNextEventId() {
        return nextEventId;
    }
}