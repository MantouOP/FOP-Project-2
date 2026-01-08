import java.time.LocalDateTime;

/**
 * Represents a single event in the calendar
 */
public class Event {
    private int eventId;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public Event() {
    }

    public Event(int eventId, String title, String description, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId=" + eventId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                '}';
    }

    /**
     * Convert event to CSV format
     */
    public String toCSV() {
        return eventId + "," + title + "," + description + "," + startDateTime + "," + endDateTime;
    }

    /**
     * Create Event from CSV line
     */
    public static Event fromCSV(String csvLine) {
        // Split into at most 5 parts to preserve commas in title/description
        String[] parts = csvLine.split(",", 5);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid CSV format for Event: " + csvLine);
        }

        String idStr = parts[0].trim();
        String title = parts[1].trim();
        String desc = parts[2].trim();
        String startStr = parts[3].trim();
        String endStr = parts[4].trim();

        return new Event(
                Integer.parseInt(idStr),
                title,
                desc,
                LocalDateTime.parse(startStr),
                LocalDateTime.parse(endStr)
        );
    }
}