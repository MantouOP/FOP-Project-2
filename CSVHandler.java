import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CSV file operations for events and recurring events
 */
public class CSVHandler {
    private static final String EVENTS_FILE = "events.csv";
    private static final String RECURRING_FILE = "recurrent.csv";

    /**
     * Read all events from CSV file
     */
    public List<Event> readEvents() {
        List<Event> events = new ArrayList<>();
        Path path = Paths.get(EVENTS_FILE);

        if (!Files.exists(path)) {
            return events; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    events.add(Event.fromCSV(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading events file: " + e.getMessage());
        }

        return events;
    }

    /**
     * Write all events to CSV file
     */
    public void writeEvents(List<Event> events) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(EVENTS_FILE))) {
            for (Event event : events) {
                writer.write(event.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing events file: " + e.getMessage());
        }
    }

    /**
     * Read all recurring events from CSV file
     */
    public List<RecurringEvent> readRecurringEvents() {
        List<RecurringEvent> recurringEvents = new ArrayList<>();
        Path path = Paths.get(RECURRING_FILE);

        if (!Files.exists(path)) {
            return recurringEvents; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    recurringEvents.add(RecurringEvent.fromCSV(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading recurring events file: " + e.getMessage());
        }

        return recurringEvents;
    }

    /**
     * Write all recurring events to CSV file
     */
    public void writeRecurringEvents(List<RecurringEvent> recurringEvents) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(RECURRING_FILE))) {
            for (RecurringEvent recurringEvent : recurringEvents) {
                writer.write(recurringEvent.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing recurring events file: " + e.getMessage());
        }
    }

    /**
     * Get the next available event ID
     */
    public int getNextEventId() {
        List<Event> events = readEvents();
        int maxId = 0;
        for (Event event : events) {
            if (event.getEventId() > maxId) {
                maxId = event.getEventId();
            }
        }
        return maxId + 1;
    }

    /**
     * Create backup of all data to a single file
     */
    public boolean createBackup(String backupFilePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(backupFilePath))) {
            // Write events section
            writer.write("# EVENTS");
            writer.newLine();
            List<Event> events = readEvents();
            for (Event event : events) {
                writer.write(event.toCSV());
                writer.newLine();
            }

            // Write recurring events section
            writer.newLine();
            writer.write("# RECURRING_EVENTS");
            writer.newLine();
            List<RecurringEvent> recurringEvents = readRecurringEvents();
            for (RecurringEvent recurringEvent : recurringEvents) {
                writer.write(recurringEvent.toCSV());
                writer.newLine();
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
            return false;
        }
    }

    /**
     * Restore data from backup file
     */
    public boolean restoreFromBackup(String backupFilePath, boolean append) {
        Path path = Paths.get(backupFilePath);
        if (!Files.exists(path)) {
            System.err.println("Backup file does not exist: " + backupFilePath);
            return false;
        }

        List<Event> events = append ? readEvents() : new ArrayList<>();
        List<RecurringEvent> recurringEvents = append ? readRecurringEvents() : new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            String currentSection = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("#")) {
                    currentSection = line;
                    continue;
                }

                if (currentSection.equals("# EVENTS")) {
                    events.add(Event.fromCSV(line));
                } else if (currentSection.equals("# RECURRING_EVENTS")) {
                    recurringEvents.add(RecurringEvent.fromCSV(line));
                }
            }

            writeEvents(events);
            writeRecurringEvents(recurringEvents);
            return true;

        } catch (IOException e) {
            System.err.println("Error restoring from backup: " + e.getMessage());
            return false;
        }
    }
}