import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Main Calendar Application
 */
public class CalendarApp {
    private EventManager eventManager;
    private CalendarView calendarView;
    private ReminderManager reminderManager;
    private StatisticsManager statisticsManager;
    private Scanner scanner;

    public CalendarApp() {
        this.eventManager = new EventManager();
        this.calendarView = new CalendarView();
        this.reminderManager = new ReminderManager(eventManager);
        this.statisticsManager = new StatisticsManager(eventManager);
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        CalendarApp app = new CalendarApp();
        app.run();
    }

    public void run() {
        System.out.println("Welcome to Calendar and Scheduler App!");
        System.out.println("=====================================");
        
        // Display today's schedule and reminders at startup
        reminderManager.displayTodaySchedule();
        reminderManager.displayReminders(30); // Show events starting in next 30 minutes

        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    createEventMenu();
                    break;
                case 2:
                    viewCalendarMenu();
                    break;
                case 3:
                    searchEventsMenu();
                    break;
                case 4:
                    updateDeleteEventMenu();
                    break;
                case 5:
                    backupRestoreMenu();
                    break;
                case 6:
                    remindersAndStatsMenu();
                    break;
                case 7:
                    System.out.println("Thank you for using Calendar App. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Create Event");
        System.out.println("2. View Calendar");
        System.out.println("3. Search Events");
        System.out.println("4. Update/Delete Event");
        System.out.println("5. Backup/Restore");
        System.out.println("6. Reminders & Statistics");
        System.out.println("7. Exit");
    }

    private void createEventMenu() {
        System.out.println("\n=== CREATE EVENT ===");
        System.out.println("1. Single Event");
        System.out.println("2. Recurring Event");
        System.out.println("3. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                createSingleEvent();
                break;
            case 2:
                createRecurringEvent();
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void createSingleEvent() {
        System.out.println("\n--- Create Single Event ---");
        
        String title = getStringInput("Enter event title: ");
        String description = getStringInput("Enter event description (optional): ");
        
        LocalDateTime startDateTime = getDateTimeInput("Enter start date and time (yyyy-MM-dd HH:mm): ");
        LocalDateTime endDateTime = getDateTimeInput("Enter end date and time (yyyy-MM-dd HH:mm): ");

        if (endDateTime.isBefore(startDateTime)) {
            System.out.println("Error: End time cannot be before start time.");
            return;
        }

        // Check for conflicts
        List<Event> conflicts = eventManager.checkConflicts(startDateTime, endDateTime);
        if (!conflicts.isEmpty()) {
            System.out.println("Warning: This event conflicts with existing events:");
            conflicts.forEach(event -> System.out.println("- " + event.getTitle() + " at " + event.getStartDateTime()));
            
            String continueChoice = getStringInput("Do you want to continue anyway? (y/n): ");
            if (!continueChoice.equalsIgnoreCase("y")) {
                return;
            }
        }

        Event event = eventManager.createEvent(title, description, startDateTime, endDateTime);
        System.out.println("Event created successfully with ID: " + event.getEventId());
    }

    private void createRecurringEvent() {
        System.out.println("\n--- Create Recurring Event ---");
        
        String title = getStringInput("Enter event title: ");
        String description = getStringInput("Enter event description (optional): ");
        
        LocalDateTime startDateTime = getDateTimeInput("Enter start date and time (yyyy-MM-dd HH:mm): ");
        LocalDateTime endDateTime = getDateTimeInput("Enter end date and time (yyyy-MM-dd HH:mm): ");

        if (endDateTime.isBefore(startDateTime)) {
            System.out.println("Error: End time cannot be before start time.");
            return;
        }

        System.out.println("\nRecurring Options:");
        System.out.println("1. Daily");
        System.out.println("2. Weekly");
        System.out.println("3. Bi-weekly");
        System.out.println("4. Monthly");
        System.out.println("5. Custom interval");

        int intervalChoice = getIntInput("Choose recurring pattern: ");
        String interval = "";
        
        switch (intervalChoice) {
            case 1: interval = "1d"; break;
            case 2: interval = "1w"; break;
            case 3: interval = "2w"; break;
            case 4: interval = "1m"; break;
            case 5:
                int value = getIntInput("Enter interval value: ");
                String type = getStringInput("Enter interval type (d=day, w=week, m=month): ");
                interval = value + type;
                break;
            default:
                System.out.println("Invalid choice. Using daily (1d).");
                interval = "1d";
        }

        System.out.println("\nRecurring End Options:");
        System.out.println("1. After N occurrences");
        System.out.println("2. Until specific date");

        int endChoice = getIntInput("Choose end condition: ");
        int recurrentTimes = 0;
        LocalDate recurrentEndDate = null;

        if (endChoice == 1) {
            recurrentTimes = getIntInput("Enter number of occurrences: ");
        } else if (endChoice == 2) {
            recurrentEndDate = getDateInput("Enter end date (yyyy-MM-dd): ");
        } else {
            System.out.println("Invalid choice. Using 10 occurrences.");
            recurrentTimes = 10;
        }

        Event event = eventManager.createRecurringEvent(title, description, startDateTime, endDateTime, 
                                                       interval, recurrentTimes, recurrentEndDate);
        System.out.println("Recurring event created successfully with ID: " + event.getEventId());
    }

    private void viewCalendarMenu() {
        System.out.println("\n=== VIEW CALENDAR ===");
        System.out.println("1. Month View (with Navigation)");
        System.out.println("2. Week View");
        System.out.println("3. Day View");
        System.out.println("4. List View (Date Range)");
        System.out.println("5. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");
        List<Event> allEvents = eventManager.getAllEvents();

        switch (choice) {
            case 1:
                monthViewWithNavigation(allEvents);
                break;
            case 2:
                LocalDate weekStart = getDateInput("Enter start of week (yyyy-MM-dd): ");
                calendarView.displayWeekView(weekStart, allEvents);
                break;
            case 3:
                LocalDate day = getDateInput("Enter date (yyyy-MM-dd): ");
                calendarView.displayDayView(day, allEvents);
                break;
            case 4:
                LocalDate startDate = getDateInput("Enter start date (yyyy-MM-dd): ");
                LocalDate endDate = getDateInput("Enter end date (yyyy-MM-dd): ");
                if (endDate.isBefore(startDate)) {
                    System.out.println("Error: End date cannot be before start date.");
                } else {
                    calendarView.displayListView(startDate, endDate, allEvents);
                }
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void monthViewWithNavigation(List<Event> allEvents) {
        YearMonth yearMonth = getYearMonthInput("Enter month and year (yyyy-MM): ");

        while (true) {
            calendarView.displayMonthView(yearMonth, allEvents);

            System.out.println("\n=== NAVIGATION ===");
            System.out.println("1. Previous Month (or press 'P')");
            System.out.println("2. Next Month (or press 'N')");
            System.out.println("3. Jump to Specific Month");
            System.out.println("4. Back to View Calendar Menu (or press 'Q')");
            System.out.println("\nKeyboard Shortcuts: P=Previous, N=Next, J=Jump, Q=Quit");

            String input = getStringInput("Enter your choice: ").trim().toUpperCase();

            int choice = 0;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // Handle keyboard shortcuts
                if (input.equals("P")) {
                    choice = 1;
                } else if (input.equals("N")) {
                    choice = 2;
                } else if (input.equals("J")) {
                    choice = 3;
                } else if (input.equals("Q")) {
                    choice = 4;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }
            }

            switch (choice) {
                case 1:
                    yearMonth = yearMonth.minusMonths(1);
                    break;
                case 2:
                    yearMonth = yearMonth.plusMonths(1);
                    break;
                case 3:
                    yearMonth = getYearMonthInput("Enter month and year (yyyy-MM): ");
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void searchEventsMenu() {
        System.out.println("\n=== SEARCH EVENTS ===");
        System.out.println("1. Search by Date");
        System.out.println("2. Search by Date Range");
        System.out.println("3. Search by Title");
        System.out.println("4. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");
        List<Event> results;

        switch (choice) {
            case 1:
                LocalDate date = getDateInput("Enter date to search (yyyy-MM-dd): ");
                results = eventManager.searchEventsByDate(date);
                calendarView.displaySearchResults(results, "by Date");
                break;
            case 2:
                LocalDate startDate = getDateInput("Enter start date (yyyy-MM-dd): ");
                LocalDate endDate = getDateInput("Enter end date (yyyy-MM-dd): ");
                results = eventManager.searchEventsByDateRange(startDate, endDate);
                calendarView.displaySearchResults(results, "by Date Range");
                break;
            case 3:
                String keyword = getStringInput("Enter title keyword: ");
                results = eventManager.searchEventsByTitle(keyword);
                calendarView.displaySearchResults(results, "by Title");
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void updateDeleteEventMenu() {
        System.out.println("\n=== UPDATE/DELETE EVENT ===");
        
        int eventId = getIntInput("Enter Event ID: ");
        Event event = eventManager.findEventById(eventId);
        
        if (event == null) {
            System.out.println("Event not found with ID: " + eventId);
            return;
        }

        calendarView.displayEventDetails(event);
        
        System.out.println("\n1. Update Event");
        System.out.println("2. Delete Event");
        System.out.println("3. Cancel");
        
        int choice = getIntInput("Enter your choice: ");
        
        switch (choice) {
            case 1:
                updateEvent(event);
                break;
            case 2:
                String confirm = getStringInput("Are you sure you want to delete this event? (y/n): ");
                if (confirm.equalsIgnoreCase("y")) {
                    if (eventManager.deleteEvent(eventId)) {
                        System.out.println("Event deleted successfully.");
                    } else {
                        System.out.println("Failed to delete event.");
                    }
                }
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void updateEvent(Event event) {
        System.out.println("\n--- Update Event ---");
        System.out.println("Leave blank to keep current value.");
        
        String newTitle = getStringInput("Enter new title [" + event.getTitle() + "]: ");
        String newDescription = getStringInput("Enter new description [" + event.getDescription() + "]: ");
        
        System.out.println("Current start time: " + event.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        LocalDateTime newStart = getDateTimeInputOptional("Enter new start time (yyyy-MM-dd HH:mm): ", event.getStartDateTime());
        
        System.out.println("Current end time: " + event.getEndDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        LocalDateTime newEnd = getDateTimeInputOptional("Enter new end time (yyyy-MM-dd HH:mm): ", event.getEndDateTime());

        if (newEnd.isBefore(newStart)) {
            System.out.println("Error: End time cannot be before start time.");
            return;
        }

        String title = newTitle.isEmpty() ? event.getTitle() : newTitle;
        String description = newDescription.isEmpty() ? event.getDescription() : newDescription;

        if (eventManager.updateEvent(event.getEventId(), title, description, newStart, newEnd)) {
            System.out.println("Event updated successfully.");
        } else {
            System.out.println("Failed to update event.");
        }
    }

    private void backupRestoreMenu() {
        System.out.println("\n=== BACKUP/RESTORE ===");
        System.out.println("1. Create Backup");
        System.out.println("2. Restore from Backup");
        System.out.println("3. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                String backupPath = getStringInput("Enter backup file path (e.g., backup.txt): ");
                if (eventManager.createBackup(backupPath)) {
                    System.out.println("Backup completed successfully to: " + backupPath);
                } else {
                    System.out.println("Failed to create backup.");
                }
                break;
            case 2:
                String restorePath = getStringInput("Enter backup file path to restore from: ");
                String appendChoice = getStringInput("Append to existing events? (y/n): ");
                boolean append = appendChoice.equalsIgnoreCase("y");
                
                if (eventManager.restoreFromBackup(restorePath, append)) {
                    System.out.println("Restore completed successfully.");
                } else {
                    System.out.println("Failed to restore from backup.");
                }
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private void remindersAndStatsMenu() {
        System.out.println("\n=== REMINDERS & STATISTICS ===");
        System.out.println("1. View Upcoming Events");
        System.out.println("2. Check Reminders");
        System.out.println("3. View Calendar Statistics");
        System.out.println("4. View Productivity Insights");
        System.out.println("5. Back to Main Menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1:
                reminderManager.checkUpcomingEvents();
                break;
            case 2:
                int minutes = getIntInput("Check reminders for events starting in how many minutes? ");
                reminderManager.displayReminders(minutes);
                break;
            case 3:
                statisticsManager.displayStatistics();
                break;
            case 4:
                statisticsManager.displayProductivityInsights();
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Helper methods for input
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                String input = getStringInput(prompt);
                return LocalDate.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }

    private LocalDateTime getDateTimeInput(String prompt) {
        while (true) {
            try {
                String input = getStringInput(prompt);
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date/time format. Please use yyyy-MM-dd HH:mm.");
            }
        }
    }

    private LocalDateTime getDateTimeInputOptional(String prompt, LocalDateTime defaultValue) {
        String input = getStringInput(prompt);
        if (input.isEmpty()) {
            return defaultValue;
        }
        
        while (true) {
            try {
                return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date/time format. Please use yyyy-MM-dd HH:mm or leave blank.");
                input = getStringInput(prompt);
                if (input.isEmpty()) {
                    return defaultValue;
                }
            }
        }
    }

    private YearMonth getYearMonthInput(String prompt) {
        while (true) {
            try {
                String input = getStringInput(prompt);
                return YearMonth.parse(input, DateTimeFormatter.ofPattern("yyyy-MM"));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid month/year format. Please use yyyy-MM.");
            }
        }
    }
}