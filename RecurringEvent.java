import java.time.LocalDate;

/**
 * Represents recurring event configuration
 */
public class RecurringEvent {
    private int eventId;
    private String recurrentInterval; // e.g., "1d", "1w", "2w", "1m"
    private int recurrentTimes; // 0 for indefinite until end date
    private LocalDate recurrentEndDate; // null if using recurrentTimes

    public RecurringEvent() {
    }

    public RecurringEvent(int eventId, String recurrentInterval, int recurrentTimes, LocalDate recurrentEndDate) {
        this.eventId = eventId;
        this.recurrentInterval = recurrentInterval;
        this.recurrentTimes = recurrentTimes;
        this.recurrentEndDate = recurrentEndDate;
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getRecurrentInterval() {
        return recurrentInterval;
    }

    public void setRecurrentInterval(String recurrentInterval) {
        this.recurrentInterval = recurrentInterval;
    }

    public int getRecurrentTimes() {
        return recurrentTimes;
    }

    public void setRecurrentTimes(int recurrentTimes) {
        this.recurrentTimes = recurrentTimes;
    }

    public LocalDate getRecurrentEndDate() {
        return recurrentEndDate;
    }

    public void setRecurrentEndDate(LocalDate recurrentEndDate) {
        this.recurrentEndDate = recurrentEndDate;
    }

    @Override
    public String toString() {
        return "RecurringEvent{" +
                "eventId=" + eventId +
                ", recurrentInterval='" + recurrentInterval + '\'' +
                ", recurrentTimes=" + recurrentTimes +
                ", recurrentEndDate=" + recurrentEndDate +
                '}';
    }

    /**
     * Convert recurring event to CSV format
     */
    public String toCSV() {
        String endDateStr = recurrentEndDate != null ? recurrentEndDate.toString() : "0";
        return eventId + "," + recurrentInterval + "," + recurrentTimes + "," + endDateStr;
    }

    /**
     * Create RecurringEvent from CSV line
     */
    public static RecurringEvent fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid CSV format for RecurringEvent");
        }

        LocalDate endDate = null;
        if (!parts[3].equals("0")) {
            endDate = LocalDate.parse(parts[3]);
        }

        return new RecurringEvent(
                Integer.parseInt(parts[0]),
                parts[1],
                Integer.parseInt(parts[2]),
                endDate
        );
    }

    /**
     * Get interval type and value
     */
    public int getIntervalValue() {
        return Integer.parseInt(recurrentInterval.substring(0, recurrentInterval.length() - 1));
    }

    public char getIntervalType() {
        return recurrentInterval.charAt(recurrentInterval.length() - 1);
    }
}