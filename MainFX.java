import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainFX extends Application {
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
    private final DateTimeFormatter dateTimeFmt24 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DateTimeFormatter dateDisplayFmt = DateTimeFormatter.ofPattern("d MMM yyyy");

    private EventManager eventManager;
    private ReminderManager reminderManager;

    private YearMonth currentYearMonth;
    private LocalDate selectedDate;

    private GridPane calendarGrid;
    private Text calendarTitle;
    // Removed: selectedHeader, eventListView, eventItems (sidebar removed)

    @Override
    public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        eventManager = new EventManager();
        reminderManager = new ReminderManager(eventManager);

        currentYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("app-root");

        // Top navigation
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER);
        Button btnPrev = new Button("<< Previous");
        btnPrev.getStyleClass().add("primary-button");
        btnPrev.setOnAction(e -> previousMonth());
        Button btnNext = new Button("Next >>");
        btnNext.getStyleClass().add("primary-button");
        btnNext.setOnAction(e -> nextMonth());
        calendarTitle = new Text();
        calendarTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        calendarTitle.setFill(Color.WHITE);
        header.getChildren().addAll(btnPrev, calendarTitle, btnNext);
        header.getStyleClass().add("nav-bar");

        FlowPane featureBar = new FlowPane(10, 10);
        featureBar.setAlignment(Pos.CENTER_LEFT);
        Button addBtnTop = new Button("Add Event");
        addBtnTop.getStyleClass().add("primary-button");
        addBtnTop.setOnAction(e -> showCreateDialog(primaryStage));
        Button recurBtnTop = new Button("Recurring Event");
        recurBtnTop.getStyleClass().add("primary-button");
        recurBtnTop.setOnAction(e -> showRecurringDialog(primaryStage));
        Button searchBtnTop = new Button("Search");
        searchBtnTop.getStyleClass().add("primary-button");
        searchBtnTop.setOnAction(e -> showSearchDialog(primaryStage));
        Button editBtnTop = new Button("Edit");
        editBtnTop.getStyleClass().add("primary-button");
        editBtnTop.setOnAction(e -> showEditDialog(primaryStage));
        Button deleteBtnTop = new Button("Delete");
        deleteBtnTop.getStyleClass().add("primary-button");
        deleteBtnTop.setOnAction(e -> showDeleteDialog(primaryStage));
        Button calBtnTop = new Button("Calendar View");
        calBtnTop.getStyleClass().add("primary-button");
        calBtnTop.setOnAction(e -> showCalendarViewDialog(primaryStage));
        Button listBtnTop = new Button("List View");
        listBtnTop.getStyleClass().add("primary-button");
        listBtnTop.setOnAction(e -> showListViewDialog(primaryStage));
        Button remindersBtnTop = new Button("Reminders");
        remindersBtnTop.getStyleClass().add("primary-button");
        remindersBtnTop.setOnAction(e -> showRemindersDialog(primaryStage));
        Button backupBtnTop = new Button("Backup/Restore");
        backupBtnTop.getStyleClass().add("primary-button");
        backupBtnTop.setOnAction(e -> showBackupDialog(primaryStage));
        Button statsBtnTop = new Button("Statistics");
        statsBtnTop.getStyleClass().add("primary-button");
        statsBtnTop.setOnAction(e -> showStatsDialog(primaryStage));
        featureBar.getChildren().addAll(addBtnTop, recurBtnTop, searchBtnTop, editBtnTop, deleteBtnTop, calBtnTop, listBtnTop, remindersBtnTop, backupBtnTop, statsBtnTop);

        VBox topContainer = new VBox(10, header, featureBar);
        topContainer.setPadding(new Insets(0, 0, 10, 0));
        root.setTop(topContainer);

        // Calendar grid - clicks are handled directly on each day cell
        calendarGrid = new GridPane();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);
        calendarGrid.setAlignment(Pos.CENTER);
        calendarGrid.setPadding(new Insets(20, 0, 20, 0));
        calendarGrid.getStyleClass().add("calendar-grid");
        calendarGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        calendarGrid.setFocusTraversable(true); // Allow calendar to receive focus
        setCalendarColumnConstraints();


        HBox calendarWrapper = new HBox(calendarGrid);
        calendarWrapper.setAlignment(Pos.CENTER);
        HBox.setHgrow(calendarGrid, Priority.ALWAYS);
        root.setCenter(calendarWrapper);

        // Sidebar removed - events now shown in dialog when clicking days

        populateCalendar(currentYearMonth);
        showReminderPopup(30);

        Scene scene = new Scene(root, 900, 800); // Reduced width since no sidebar
        var cssUrl = getClass().getResource("style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }
        primaryStage.setTitle("JavaFX Calendar App");
        var iconStream = getClass().getResourceAsStream("app-icon.png");
        if (iconStream != null) {
            primaryStage.getIcons().add(new Image(iconStream));
        }
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        populateCalendar(currentYearMonth);
    }

    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        populateCalendar(currentYearMonth);
    }

    // Helper method to parse date/time - accepts both 12-hour (with AM/PM) and 24-hour formats
    private LocalDateTime parseDateTime(String dateTimeStr) throws DateTimeParseException {
        dateTimeStr = dateTimeStr.trim();
        try {
            // Try 12-hour format with AM/PM first
            return LocalDateTime.parse(dateTimeStr, dateTimeFmt);
        } catch (DateTimeParseException e1) {
            try {
                // Try 24-hour format
                return LocalDateTime.parse(dateTimeStr, dateTimeFmt24);
            } catch (DateTimeParseException e2) {
                // If both fail, throw exception
                throw new DateTimeParseException("Invalid date/time format", dateTimeStr, 0);
            }
        }
    }

    // Removed: selectDate and updateSidebar methods (sidebar functionality removed)

    private void populateCalendar(YearMonth yearMonth) {
        calendarTitle.setText(yearMonth.getMonth().toString() + " " + yearMonth.getYear());
        calendarGrid.getChildren().clear();

        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayName = new Label(daysOfWeek[i]);
            dayName.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            dayName.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(dayName, Priority.ALWAYS);
            calendarGrid.add(dayName, i, 0);
        }

        LocalDate firstOfMonth = yearMonth.atDay(1);
        int dayOfWeekValue = firstOfMonth.getDayOfWeek().getValue();
        int daysInMonth = yearMonth.lengthOfMonth();

        int row = 1;
        int col = dayOfWeekValue - 1;

        for (int day = 1; day <= daysInMonth; day++) {
            final LocalDate date = yearMonth.atDay(day);
            VBox dayNode = new VBox();
            dayNode.setPrefSize(80, 80);
            dayNode.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            dayNode.getStyleClass().add("day-card");
            dayNode.setUserData(date); // Store date for click handling
            dayNode.setPickOnBounds(true); // Ensure VBox captures all clicks within its bounds
            GridPane.setHgrow(dayNode, Priority.ALWAYS);
            GridPane.setVgrow(dayNode, Priority.ALWAYS);

            if (LocalDate.now().equals(date)) {
                dayNode.getStyleClass().add("today");
            }
            if (date.equals(selectedDate)) {
                dayNode.getStyleClass().add("selected-day");
            }

            // Create content based on whether day has events
            if (!eventManager.searchEventsByDate(date).isEmpty()) {
                Circle dot = new Circle(6, Color.RED);
                dot.setMouseTransparent(true); // Allow clicks to pass through
                Label dayNum = new Label(String.valueOf(day));
                dayNum.setMouseTransparent(true); // Allow clicks to pass through
                dayNum.setPadding(new Insets(5));
                dayNum.getStyleClass().add("day-label");

                HBox rowBox = new HBox(4, dot, dayNum);
                rowBox.setAlignment(Pos.CENTER_LEFT);
                rowBox.setMouseTransparent(true); // Allow clicks to pass through to parent VBox
                rowBox.setPadding(new Insets(5));

                dayNode.getChildren().add(rowBox);
            } else {
                Label dayLabel = new Label(String.valueOf(day));
                dayLabel.setPadding(new Insets(5));
                dayLabel.getStyleClass().add("day-label");
                dayLabel.setMouseTransparent(true); // Allow clicks to pass through to parent VBox
                dayNode.getChildren().add(dayLabel);
            }

            // Add click handler for this day cell
            final VBox cellNode = dayNode;
            dayNode.setOnMouseClicked(e -> {
                System.out.println("Day clicked: " + date);

                javafx.application.Platform.runLater(() -> {
                    selectedDate = date;

                    // Clear all selections
                    for (var node : calendarGrid.getChildren()) {
                        if (node instanceof VBox vbox && vbox.getStyleClass().contains("day-card")) {
                            vbox.getStyleClass().remove("selected-day");
                        }
                    }

                    // Highlight this cell
                    cellNode.getStyleClass().add("selected-day");

                    // Show dialog with events for this day
                    showDayEventsDialog(date);
                });
            });

            calendarGrid.add(dayNode, col, row);
            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void showDayEventsDialog(LocalDate date) {
        List<Event> events = eventManager.searchEventsByDate(date);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Events on " + dateDisplayFmt.format(date));
        alert.setHeaderText(dateDisplayFmt.format(date));

        if (events.isEmpty()) {
            alert.setContentText("No events scheduled for this day.");
        } else {
            StringBuilder content = new StringBuilder();
            content.append("Total Events: ").append(events.size()).append("\n\n");

            for (int i = 0; i < events.size(); i++) {
                Event ev = events.get(i);
                content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                content.append("Event #").append(i + 1).append("\n");
                content.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                content.append("ID: ").append(ev.getEventId()).append("\n");
                content.append("Title: ").append(ev.getTitle()).append("\n");
                content.append("Description: ").append(ev.getDescription().isEmpty() ? "(none)" : ev.getDescription()).append("\n");
                content.append("Start: ").append(dateTimeFmt.format(ev.getStartDateTime())).append("\n");
                content.append("End: ").append(dateTimeFmt.format(ev.getEndDateTime())).append("\n");

                Duration duration = Duration.between(ev.getStartDateTime(), ev.getEndDateTime());
                long hours = duration.toHours();
                long minutes = duration.toMinutes() % 60;
                content.append("Duration: ");
                if (hours > 0) content.append(hours).append("h ");
                if (minutes > 0) content.append(minutes).append("m");
                content.append("\n\n");
            }

            TextArea textArea = new TextArea(content.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            textArea.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 13px;");

            alert.getDialogPane().setContent(textArea);
            alert.getDialogPane().setPrefSize(600, 500);
        }

        // Apply CSS styling
        var cssUrl = getClass().getResource("style.css");
        if (cssUrl != null) {
            alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }

        alert.showAndWait();
    }

    // Helper method to create event cell factory for ListViews
    private javafx.util.Callback<ListView<Event>, ListCell<Event>> createEventCellFactory() {
        return view -> new ListCell<>() {
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(""); // Clear any previous style
                    setPrefHeight(0); // Set height to 0 to hide empty cells
                    setMaxHeight(0);
                } else {
                    String displayText = String.format(
                        "Event ID: %d\nTitle: %s\nDescription: %s\nDate: %s - %s",
                        item.getEventId(),
                        item.getTitle(),
                        item.getDescription(),
                        dateTimeFmt.format(item.getStartDateTime()),
                        dateTimeFmt.format(item.getEndDateTime())
                    );
                    setText(displayText);
                    setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    setPrefHeight(120);
                    setMaxHeight(120);
                    setStyle("-fx-padding: 10px;");
                }
            }
        };
    }

    private void showCreateDialog(Stage owner) {
        Dialog<Event> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Add Event");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField titleField = new TextField();
        TextArea descField = new TextArea();
        descField.setPrefRowCount(3);
        descField.getStyleClass().add("input-area");
        TextField startField = new TextField(dateTimeFmt24.format(selectedDate.atTime(9, 0)));
        TextField endField = new TextField(dateTimeFmt24.format(selectedDate.atTime(10, 0)));
        startField.getStyleClass().add("input-field");
        endField.getStyleClass().add("input-field");

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.getStyleClass().add("dialog-form");
        form.addRow(0, new Label("Title"), titleField);
        form.addRow(1, new Label("Description"), descField);
        form.addRow(2, new Label("Start (yyyy-MM-dd HH:mm)"), startField);
        form.addRow(3, new Label("End (yyyy-MM-dd HH:mm)"), endField);
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return createEventFromInputs(titleField.getText(), descField.getText(), startField.getText(), endField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ev -> {
            if (ev != null) {
                System.out.println("Event created: " + ev.getTitle() + " (ID: " + ev.getEventId() + ")");
                populateCalendar(currentYearMonth); // Refresh calendar to show new event
                // Show success message with event details
                String successMsg = String.format("Event created successfully!\n\nTitle: %s\nStart: %s\nEnd: %s",
                        ev.getTitle(),
                        dateTimeFmt.format(ev.getStartDateTime()),
                        dateTimeFmt.format(ev.getEndDateTime()));
                showAlert("Event Created", successMsg);
            } else {
                System.out.println("Event creation returned null");
            }
        });
    }

    private Event createEventFromInputs(String title, String desc, String startStr, String endStr) {
        try {
            // Validate title is not blank
            if (title == null || title.trim().isEmpty()) {
                System.out.println("Error: Title is blank");
                showAlert("Missing Title", "Please enter a title name.");
                return null;
            }

            System.out.println("Creating event: " + title);
            System.out.println("Start: " + startStr + ", End: " + endStr);

            LocalDateTime start = parseDateTime(startStr);
            LocalDateTime end = parseDateTime(endStr);

            if (!end.isAfter(start)) {
                System.out.println("Error: End time not after start time");
                showAlert("Invalid range", "End time must be after start time.");
                return null;
            }

            List<Event> conflicts = eventManager.checkConflicts(start, end);
            if (!conflicts.isEmpty()) {
                System.out.println("Found " + conflicts.size() + " conflicting events");
                String summary = conflicts.stream()
                        .limit(3)
                        .map(ev -> dateTimeFmt.format(ev.getStartDateTime()) + " " + ev.getTitle())
                        .collect(Collectors.joining("\n"));
                boolean proceed = showConfirm("Time conflict", "Overlaps with existing events:\n" + summary + "\nCreate anyway?");
                if (!proceed) {
                    System.out.println("User cancelled due to conflict");
                    return null;
                }
            }

            Event createdEvent = eventManager.createEvent(title, desc, start, end);
            System.out.println("Event created successfully with ID: " + createdEvent.getEventId());
            return createdEvent;
        } catch (DateTimeParseException ex) {
            System.out.println("Parse error: " + ex.getMessage());
            showAlert("Invalid date/time", "Use format yyyy-MM-dd hh:mm AM/PM or yyyy-MM-dd HH:mm");
            return null;
        }
    }

    private void showRecurringDialog(Stage owner) {
        Dialog<Event> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Add Recurring Event");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField titleField = new TextField();
        TextArea descField = new TextArea();
        descField.setPrefRowCount(3);
        descField.getStyleClass().add("input-area");
        TextField startField = new TextField(dateTimeFmt24.format(selectedDate.atTime(9, 0)));
        TextField endField = new TextField(dateTimeFmt24.format(selectedDate.atTime(10, 0)));
        startField.getStyleClass().add("input-field");
        endField.getStyleClass().add("input-field");

        ComboBox<String> intervalBox = new ComboBox<>(FXCollections.observableArrayList("Daily", "Weekly", "Bi-Weekly", "Monthly"));
        intervalBox.getSelectionModel().selectFirst();
        TextField timesField = new TextField("5");
        TextField endDateField = new TextField(""); // optional

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.getStyleClass().add("dialog-form");
        form.addRow(0, new Label("Title"), titleField);
        form.addRow(1, new Label("Description"), descField);
        form.addRow(2, new Label("Start (yyyy-MM-dd HH:mm)"), startField);
        form.addRow(3, new Label("End (yyyy-MM-dd HH:mm)"), endField);
        form.addRow(4, new Label("Interval"), intervalBox);
        form.addRow(5, new Label("Times (0 = until end date)"), timesField);
        form.addRow(6, new Label("End date (yyyy-MM-dd, optional)"), endDateField);
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return createRecurringFromInputs(titleField.getText(), descField.getText(), startField.getText(), endField.getText(), intervalBox.getValue(), timesField.getText(), endDateField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ev -> {
            if (ev != null) {
                System.out.println("Recurring event created: " + ev.getTitle() + " (ID: " + ev.getEventId() + ")");
                populateCalendar(currentYearMonth); // Refresh calendar
                // Show success message with recurring event details
                RecurringEvent recEvent = eventManager.getRecurringEventByEventId(ev.getEventId());
                if (recEvent != null) {
                    String successMsg = String.format("Recurring event created successfully!\n\nTitle: %s\nStart: %s\nEnd: %s\nInterval: %s\nOccurrences: %d",
                            ev.getTitle(),
                            dateTimeFmt.format(ev.getStartDateTime()),
                            dateTimeFmt.format(ev.getEndDateTime()),
                            recEvent.getRecurrentInterval(),
                            recEvent.getRecurrentTimes());
                    showAlert("Recurring Event Created", successMsg);
                } else {
                    showAlert("Event Created", "Event created successfully!");
                }
            } else {
                System.out.println("Recurring event creation returned null");
            }
        });
    }

    private Event createRecurringFromInputs(String title, String desc, String startStr, String endStr, String intervalLabel, String timesStr, String endDateStr) {
        try {
            // Validate title is not blank
            if (title == null || title.trim().isEmpty()) {
                System.out.println("Error: Title is blank");
                showAlert("Missing Title", "Please enter a title name.");
                return null;
            }

            LocalDateTime start = parseDateTime(startStr);
            LocalDateTime end = parseDateTime(endStr);
            if (!end.isAfter(start)) {
                showAlert("Invalid range", "End time must be after start time.");
                return null;
            }
            String interval = switch (intervalLabel) {
                case "Daily" -> "1d";
                case "Weekly" -> "1w";
                case "Bi-Weekly" -> "2w";
                default -> "1m";
            };
            int times = Integer.parseInt(timesStr);
            LocalDate recurEnd = endDateStr.isBlank() ? null : LocalDate.parse(endDateStr, dateFmt);
            return eventManager.createRecurringEvent(title, desc, start, end, interval, times, recurEnd);
        } catch (DateTimeParseException ex) {
            showAlert("Invalid date/time", "Use format yyyy-MM-dd hh:mm AM/PM or yyyy-MM-dd HH:mm for start/end, yyyy-MM-dd for end date.");
            return null;
        } catch (NumberFormatException ex) {
            showAlert("Invalid number", "Times must be a number (0 to use end date).");
            return null;
        } catch (Exception ex) {
            showAlert("Error", ex.getMessage());
            return null;
        }
    }

    private void showSearchDialog(Stage owner) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Search Events");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        dialog.getDialogPane().setMinWidth(600);  // Set minimum width
        dialog.getDialogPane().setMinHeight(700); // Set minimum height for better visibility

        TextField titleField = new TextField();
        TextField descField = new TextField();
        TextField idField = new TextField();
        TextField startField = new TextField(); // Empty by default - no date filter
        TextField endField = new TextField();   // Empty by default - no date filter
        startField.setPromptText("yyyy-MM-dd (optional)");
        endField.setPromptText("yyyy-MM-dd (optional)");
        CheckBox upcomingBox = new CheckBox("Only upcoming");

        ListView<Event> results = new ListView<>();
        results.setCellFactory(createEventCellFactory());
        results.setPrefHeight(450);  // Increased to show more events
        results.setMinHeight(450);   // Set minimum height
        results.setMaxHeight(600);   // Increased max height for better scrolling
        results.setFixedCellSize(-1); // Use variable cell size (not fixed)

        Label resultCountLabel = new Label("Search results will appear here");
        resultCountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        Button run = new Button("Search");
        run.getStyleClass().add("primary-button");

        // Create a show all button
        Button showAllBtn = new Button("Show All");
        showAllBtn.getStyleClass().add("primary-button");

        // Runnable for the search logic (can be called by Search)
        Runnable performSearch = () -> {
            System.out.println("\n=== SEARCHING EVENTS ===");

            List<Event> matches = eventManager.getAllEvents();
            System.out.println("Total events in system: " + matches.size());

            LocalDate startDate = null;
            LocalDate endDate = null;
            try {
                if (!startField.getText().isBlank()) startDate = LocalDate.parse(startField.getText(), dateFmt);
                if (!endField.getText().isBlank()) endDate = LocalDate.parse(endField.getText(), dateFmt);
            } catch (Exception ex) {
                startDate = null;
                endDate = null;
            }
            Integer idFilter = null;
            try {
                if (!idField.getText().isBlank()) idFilter = Integer.parseInt(idField.getText());
            } catch (NumberFormatException ignored) {}

            // Log search criteria
            System.out.println("Search Criteria:");
            if (!titleField.getText().isBlank()) {
                System.out.println("  - Title contains: \"" + titleField.getText() + "\"");
            }
            if (!descField.getText().isBlank()) {
                System.out.println("  - Description contains: \"" + descField.getText() + "\"");
            }
            if (idFilter != null) {
                System.out.println("  - Event ID: " + idFilter);
            }
            if (startDate != null) {
                System.out.println("  - Start date: " + startDate);
            }
            if (endDate != null) {
                System.out.println("  - End date: " + endDate);
            }
            if (upcomingBox.isSelected()) {
                System.out.println("  - Only upcoming events");
            }

            final LocalDate startDateFinal = startDate;
            final LocalDate endDateFinal = endDate;
            final Integer idFilterFinal = idFilter;
            final String titleKw = titleField.getText().toLowerCase();
            final String descKw = descField.getText().toLowerCase();
            final boolean upcomingOnly = upcomingBox.isSelected();

            matches = matches.stream()
                    .filter(ev -> {
                        if (idFilterFinal != null && ev.getEventId() != idFilterFinal) return false;
                        if (!titleKw.isBlank() && !ev.getTitle().toLowerCase().contains(titleKw)) return false;
                        if (!descKw.isBlank() && !ev.getDescription().toLowerCase().contains(descKw)) return false;
                        if (startDateFinal != null || endDateFinal != null) {
                            LocalDate s = startDateFinal != null ? startDateFinal : LocalDate.MIN;
                            LocalDate t = endDateFinal != null ? endDateFinal : LocalDate.MAX;
                            LocalDate evStart = ev.getStartDateTime().toLocalDate();
                            LocalDate evEnd = ev.getEndDateTime().toLocalDate();
                            if (evStart.isAfter(t) || evEnd.isBefore(s)) return false;
                        }
                        if (upcomingOnly && !ev.getStartDateTime().isAfter(LocalDateTime.now())) return false;
                        return true;
                    })
                    .sorted(java.util.Comparator.comparing(Event::getStartDateTime))
                    .toList();

            // Log search results
            System.out.println("\nSearch Results: " + matches.size() + " event(s) found");
            if (!matches.isEmpty()) {
                System.out.println("Found events:");
                for (Event ev : matches) {
                    System.out.println("  - ID: " + ev.getEventId() +
                                     " | " + dateTimeFmt.format(ev.getStartDateTime()) +
                                     " | \"" + ev.getTitle() + "\"");
                }
            } else {
                System.out.println("  No events match the search criteria");
            }
            System.out.println("========================\n");

            results.getItems().setAll(matches);
            System.out.println("GUI ListView updated with " + results.getItems().size() + " event(s)");

            // Verify what's actually in the ListView
            System.out.println("Verifying ListView contents:");
            for (int i = 0; i < results.getItems().size(); i++) {
                Event ev = results.getItems().get(i);
                System.out.println("  ListView[" + i + "]: ID=" + ev.getEventId() +
                                 ", Title=\"" + ev.getTitle() + "\"");
            }
            System.out.println();

            // Update result count label
            if (matches.isEmpty()) {
                resultCountLabel.setText("No events found");
                resultCountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #e74c3c;");
            } else {
                resultCountLabel.setText("Found " + matches.size() + " event(s) - Scroll down to see all");
                resultCountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #27ae60;");
            }
        };

        run.setOnAction(e -> {
            System.out.println("Search button clicked");
            performSearch.run();
        });

        showAllBtn.setOnAction(e -> {
            System.out.println("\n📋 SHOW ALL button clicked!");
            System.out.println("Loading all events...");

            List<Event> allEvents = eventManager.getAllEvents().stream()
                    .sorted(java.util.Comparator.comparing(Event::getStartDateTime))
                    .toList();

            System.out.println("Total events in system: " + allEvents.size());
            if (!allEvents.isEmpty()) {
                System.out.println("All events:");
                for (Event ev : allEvents) {
                    System.out.println("  - ID: " + ev.getEventId() +
                                     " | " + dateTimeFmt.format(ev.getStartDateTime()) +
                                     " | \"" + ev.getTitle() + "\"");
                }
            }

            results.getItems().setAll(allEvents);

            if (allEvents.isEmpty()) {
                resultCountLabel.setText("No events in system");
                resultCountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #e74c3c;");
            } else {
                resultCountLabel.setText("Showing all " + allEvents.size() + " event(s) - Scroll down to see all");
                resultCountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #27ae60;");
            }
            System.out.println("====================\n");
        });

        GridPane form = new GridPane();
        form.setHgap(6);
        form.setVgap(5);
        form.setPadding(new Insets(8));
        form.getStyleClass().add("dialog-form");
        int r = 0;
        form.addRow(r++, new Label("Title contains"), titleField);
        form.addRow(r++, new Label("Description contains"), descField);
        form.addRow(r++, new Label("Event ID (exact)"), idField);
        form.addRow(r++, new Label("Start date (yyyy-MM-dd)"), startField);
        form.addRow(r++, new Label("End date (yyyy-MM-dd)"), endField);
        form.addRow(r++, upcomingBox);

        HBox buttonBox = new HBox(10, run, showAllBtn);
        VBox box = new VBox(8, form, buttonBox, resultCountLabel, results);
        box.setPadding(new Insets(8));
        dialog.getDialogPane().setContent(box);
        dialog.showAndWait();
    }

    private void showAlert(String title, String msg) {
        System.out.println("=== SHOWING ALERT ===");
        System.out.println("Title: " + title);
        System.out.println("Message: " + msg);
        System.out.println("====================");

        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null); // Remove header for cleaner look
        var cssUrl = getClass().getResource("style.css");
        if (cssUrl != null) {
            alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }
        alert.showAndWait();
        System.out.println("Alert closed\n");
    }

    private boolean showConfirm(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        var cssUrl = getClass().getResource("style.css");
        if (cssUrl != null) {
            alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }
        return alert.showAndWait().filter(ButtonType.YES::equals).isPresent();
    }

    private void setCalendarColumnConstraints() {
        calendarGrid.getColumnConstraints().clear();
        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7.0);
            cc.setHgrow(Priority.ALWAYS);
            calendarGrid.getColumnConstraints().add(cc);
        }
    }

    private void showCalendarViewDialog(Stage owner) {
        System.out.println("\n=== CALENDAR VIEW ===");
        System.out.println("Opening calendar view dialog");
        System.out.println("Initial date: " + dateFmt.format(selectedDate));

        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Calendar View");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        var viewType = new ComboBox<>(FXCollections.observableArrayList("Month", "Week"));
        viewType.getSelectionModel().selectFirst();
        var datePicker = new DatePicker(selectedDate);

        // Calendar grid for visual display
        GridPane calendarGridView = new GridPane();
        calendarGridView.setHgap(3);
        calendarGridView.setVgap(3);
        calendarGridView.setPadding(new Insets(10));
        calendarGridView.getStyleClass().add("calendar-grid");
        calendarGridView.setMinSize(500, 400);

        // Title label for month/year or week
        Label titleLabel = new Label();
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: #758BED;");

        Runnable render = () -> {
            calendarGridView.getChildren().clear();
            String type = viewType.getValue();
            LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : selectedDate;
            YearMonth ym = YearMonth.from(date);

            System.out.println("\nRendering calendar view:");
            System.out.println("  View type: " + type);
            System.out.println("  Date: " + dateFmt.format(date));

            if ("Week".equals(type)) {
                // Week view
                LocalDate monday = date.minusDays(date.getDayOfWeek().getValue() - 1); // Monday start
                titleLabel.setText("Week of " + dateFmt.format(monday));

                System.out.println("  Week starts: " + dateFmt.format(monday));
                System.out.println("  Week ends: " + dateFmt.format(monday.plusDays(6)));

                int totalEvents = 0;
                // Day headers
                String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                for (int i = 0; i < 7; i++) {
                    Label headerLabel = new Label(dayNames[i]);
                    headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                    headerLabel.setStyle("-fx-text-fill: white; -fx-background-color: #758BED; -fx-padding: 8; -fx-alignment: center;");
                    headerLabel.setMaxWidth(Double.MAX_VALUE);
                    calendarGridView.add(headerLabel, i, 0);
                }

                // Day cells
                for (int i = 0; i < 7; i++) {
                    LocalDate cur = monday.plusDays(i);
                    List<Event> dayEvents = eventManager.searchEventsByDate(cur);
                    boolean hasEvents = !dayEvents.isEmpty();
                    totalEvents += dayEvents.size();

                    VBox dayCell = new VBox(5);
                    dayCell.setPadding(new Insets(8));
                    dayCell.setMinSize(70, 80);
                    dayCell.setMaxWidth(Double.MAX_VALUE);

                    if (cur.equals(LocalDate.now())) {
                        dayCell.setStyle("-fx-background-color: linear-gradient(to bottom, #8886DD, #758BED); -fx-border-color: #8688AD; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
                    } else if (hasEvents) {
                        dayCell.setStyle("-fx-background-color: linear-gradient(to bottom, #C4E2FA, #B2D5FF); -fx-border-color: #758BED; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
                    } else {
                        dayCell.setStyle("-fx-background-color: white; -fx-border-color: #BBC4F4; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
                    }

                    Label dayNum = new Label(String.valueOf(cur.getDayOfMonth()));
                    dayNum.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                    dayNum.setStyle("-fx-text-fill: " + (cur.equals(LocalDate.now()) ? "white" : "#8688AD") + ";");

                    if (hasEvents) {
                        Label eventIndicator = new Label("• " + dayEvents.size() + " event" + (dayEvents.size() > 1 ? "s" : ""));
                        eventIndicator.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
                        eventIndicator.setStyle("-fx-text-fill: #758BED;");
                        dayCell.getChildren().addAll(dayNum, eventIndicator);

                        // Add tooltip with event details
                        StringBuilder tooltip = new StringBuilder();
                        for (Event ev : dayEvents) {
                            tooltip.append(ev.getTitle()).append("\n");
                            tooltip.append(ev.getStartDateTime().toLocalTime()).append(" - ").append(ev.getEndDateTime().toLocalTime()).append("\n\n");
                        }
                        Tooltip tip = new Tooltip(tooltip.toString().trim());
                        Tooltip.install(dayCell, tip);
                    } else {
                        dayCell.getChildren().add(dayNum);
                    }

                    calendarGridView.add(dayCell, i, 1);
                }

                System.out.println("  Total events in week: " + totalEvents);
                System.out.println("  Days with events: " + (int)java.util.stream.IntStream.range(0, 7)
                    .filter(i -> !eventManager.searchEventsByDate(monday.plusDays(i)).isEmpty())
                    .count());
            } else { // Month view
                LocalDate first = ym.atDay(1);
                int offset = first.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
                int days = ym.lengthOfMonth();
                titleLabel.setText(ym.getMonth() + " " + ym.getYear());

                System.out.println("  Month: " + ym.getMonth() + " " + ym.getYear());
                System.out.println("  Days in month: " + days);

                int totalEvents = 0;
                int daysWithEvents = 0;

                // Day headers
                String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                for (int i = 0; i < 7; i++) {
                    Label headerLabel = new Label(dayNames[i]);
                    headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                    headerLabel.setStyle("-fx-text-fill: white; -fx-background-color: #758BED; -fx-padding: 8; -fx-alignment: center;");
                    headerLabel.setMaxWidth(Double.MAX_VALUE);
                    calendarGridView.add(headerLabel, i, 0);
                }

                // Empty cells before first day
                int row = 1;
                int col = 0;
                for (int i = 1; i < offset; i++) {
                    VBox emptyCell = new VBox();
                    emptyCell.setMinSize(70, 60);
                    emptyCell.setStyle("-fx-background-color: #E9EEF4; -fx-border-color: #CED4EB; -fx-border-width: 1;");
                    calendarGridView.add(emptyCell, col++, row);
                }
                col = offset - 1;

                // Day cells
                for (int d = 1; d <= days; d++) {
                    LocalDate ddate = ym.atDay(d);
                    List<Event> dayEvents = eventManager.searchEventsByDate(ddate);
                    boolean hasEvents = !dayEvents.isEmpty();

                    if (hasEvents) {
                        totalEvents += dayEvents.size();
                        daysWithEvents++;
                    }

                    VBox dayCell = new VBox(3);
                    dayCell.setPadding(new Insets(5));
                    dayCell.setMinSize(70, 60);
                    dayCell.setMaxWidth(Double.MAX_VALUE);

                    if (ddate.equals(LocalDate.now())) {
                        dayCell.setStyle("-fx-background-color: linear-gradient(to bottom, #8886DD, #758BED); -fx-border-color: #8688AD; -fx-border-width: 2; -fx-border-radius: 3; -fx-background-radius: 3;");
                    } else if (hasEvents) {
                        dayCell.setStyle("-fx-background-color: linear-gradient(to bottom, #C4E2FA, #B2D5FF); -fx-border-color: #758BED; -fx-border-width: 2; -fx-border-radius: 3; -fx-background-radius: 3;");
                    } else {
                        dayCell.setStyle("-fx-background-color: white; -fx-border-color: #BBC4F4; -fx-border-width: 1; -fx-border-radius: 3; -fx-background-radius: 3;");
                    }

                    Label dayNum = new Label(String.valueOf(d));
                    dayNum.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                    dayNum.setStyle("-fx-text-fill: " + (ddate.equals(LocalDate.now()) ? "white" : "#8688AD") + ";");

                    if (hasEvents) {
                        Circle eventDot = new Circle(3);
                        eventDot.setFill(ddate.equals(LocalDate.now()) ? Color.WHITE : Color.web("#758BED"));
                        HBox header = new HBox(5, dayNum, eventDot);
                        header.setAlignment(Pos.CENTER_LEFT);
                        dayCell.getChildren().add(header);

                        // Add tooltip with event details
                        StringBuilder tooltip = new StringBuilder();
                        for (Event ev : dayEvents) {
                            tooltip.append(ev.getTitle()).append("\n");
                            tooltip.append(ev.getStartDateTime().toLocalTime()).append(" - ").append(ev.getEndDateTime().toLocalTime()).append("\n\n");
                        }
                        Tooltip tip = new Tooltip(tooltip.toString().trim());
                        Tooltip.install(dayCell, tip);
                    } else {
                        dayCell.getChildren().add(dayNum);
                    }

                    calendarGridView.add(dayCell, col, row);

                    col++;
                    if (col == 7) {
                        col = 0;
                        row++;
                    }
                }

                // Fill remaining cells
                while (col > 0 && col < 7) {
                    VBox emptyCell = new VBox();
                    emptyCell.setMinSize(70, 60);
                    emptyCell.setStyle("-fx-background-color: #E9EEF4; -fx-border-color: #CED4EB; -fx-border-width: 1;");
                    calendarGridView.add(emptyCell, col++, row);
                }

                System.out.println("  Total events in month: " + totalEvents);
                System.out.println("  Days with events: " + daysWithEvents);
            }
            System.out.println("  Calendar view rendered successfully");
            System.out.println("====================\n");
        };

        viewType.valueProperty().addListener((obs, o, n) -> render.run());
        datePicker.valueProperty().addListener((obs, o, n) -> render.run());

        render.run();

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.getStyleClass().add("dialog-form");
        form.addRow(0, new Label("View type"), viewType, new Label("Date"), datePicker);

        VBox box = new VBox(10, form, titleLabel, calendarGridView);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.TOP_CENTER);
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().setMinWidth(600);
        dialog.getDialogPane().setMinHeight(500);
        dialog.showAndWait();
    }

    private void showListViewDialog(Stage owner) {
        System.out.println("\n=== LIST VIEW ===");
        System.out.println("Opening list view dialog");

        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("List View");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        TextField startField = new TextField(dateFmt.format(selectedDate.withDayOfMonth(1)));
        TextField endField = new TextField(dateFmt.format(selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())));

        System.out.println("Default date range:");
        System.out.println("  Start: " + startField.getText() + " (first day of month)");
        System.out.println("  End: " + endField.getText() + " (last day of month)");

        ListView<Event> list = new ListView<>();
        list.setCellFactory(createEventCellFactory());

        Button load = new Button("Load");
        load.getStyleClass().add("primary-button");
        load.setOnAction(e -> {
            try {
                System.out.println("\nLoading events for list view...");
                LocalDate s = LocalDate.parse(startField.getText(), dateFmt);
                LocalDate t = LocalDate.parse(endField.getText(), dateFmt);

                System.out.println("Date range:");
                System.out.println("  Start: " + dateFmt.format(s));
                System.out.println("  End: " + dateFmt.format(t));

                List<Event> events = eventManager.searchEventsByDateRange(s, t).stream()
                        .sorted(java.util.Comparator.comparing(Event::getStartDateTime))
                        .toList();

                System.out.println("\nFound " + events.size() + " event(s) in date range");
                if (!events.isEmpty()) {
                    System.out.println("Events:");
                    for (Event ev : events) {
                        System.out.println("  - ID: " + ev.getEventId() +
                                         " | " + dateTimeFmt.format(ev.getStartDateTime()) +
                                         " | \"" + ev.getTitle() + "\"");
                    }
                } else {
                    System.out.println("  No events in this date range");
                }

                list.getItems().setAll(events);
                System.out.println("List view updated with " + events.size() + " event(s)");
                System.out.println("====================\n");
            } catch (Exception ex) {
                System.out.println("✗ Invalid date format: " + ex.getMessage());
                showAlert("Invalid date", "Use yyyy-MM-dd");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.getStyleClass().add("dialog-form");
        form.addRow(0, new Label("Start"), startField);
        form.addRow(1, new Label("End"), endField);

        VBox box = new VBox(10, form, load, list);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.showAndWait();
    }

    private void showRemindersDialog(Stage owner) {
        System.out.println("\n=== REMINDERS ===");
        System.out.println("Opening reminders dialog");

        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Reminders");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList(
                "30 minutes before",
                "1 hour before",
                "1 day before",
                "Custom Minutes",
                "Custom Hours",
                "Custom Days"
        ));
        typeBox.getSelectionModel().selectFirst();

        Spinner<Integer> minutesSpinner = new Spinner<>(1, 1440, 30, 5);
        Spinner<Integer> hoursSpinner = new Spinner<>(1, 72, 1, 1);
        Spinner<Integer> daysSpinner = new Spinner<>(1, 365, 1, 1);

        // Enable only the relevant custom input
        Runnable refreshInputs = () -> {
            String v = typeBox.getValue();
            boolean cm = v.contains("Custom Minutes");
            boolean ch = v.contains("Custom Hours");
            boolean cd = v.contains("Custom Days");
            minutesSpinner.setDisable(!cm);
            hoursSpinner.setDisable(!ch);
            daysSpinner.setDisable(!cd);
        };
        typeBox.setOnAction(e -> refreshInputs.run());
        refreshInputs.run();

        ListView<Event> list = new ListView<>();
        list.setCellFactory(createEventCellFactory());

        Button load = new Button("Show upcoming");
        load.getStyleClass().add("primary-button");

        // Create a show all button
        Button showAllBtn = new Button("Show All");
        showAllBtn.getStyleClass().add("primary-button");

        // Runnable for loading reminders (can be called by Show upcoming)
        Runnable loadReminders = () -> {
            try {
                System.out.println("\nLoading upcoming events for reminders...");
                String sel = typeBox.getValue();
                int minutes = switch (sel) {
                    case "30 minutes before" -> 30;
                    case "1 hour before" -> 60;
                    case "1 day before" -> 1440;
                    case "Custom Minutes" -> minutesSpinner.getValue();
                    case "Custom Hours" -> hoursSpinner.getValue() * 60;
                    case "Custom Days" -> daysSpinner.getValue() * 1440;
                    default -> 30;
                };

                System.out.println("Reminder settings:");
                System.out.println("  Type: " + sel);
                System.out.println("  Time window: " + minutes + " minute(s)");

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime cutoff = now.plusMinutes(minutes);
                System.out.println("  Current time: " + dateTimeFmt.format(now));
                System.out.println("  Cutoff time: " + dateTimeFmt.format(cutoff));

                List<Event> upcoming = upcomingWithin(minutes);

                System.out.println("\nFound " + upcoming.size() + " upcoming event(s) within time window");
                if (!upcoming.isEmpty()) {
                    System.out.println("Upcoming events:");
                    for (Event ev : upcoming) {
                        Duration d = Duration.between(now, ev.getStartDateTime());
                        String timeUntil = formatDuration(d);
                        System.out.println("  - ID: " + ev.getEventId() +
                                         " | " + dateTimeFmt.format(ev.getStartDateTime()) +
                                         " | \"" + ev.getTitle() + "\" (in " + timeUntil + ")");
                    }
                } else {
                    System.out.println("  No events starting within the next " + minutes + " minute(s)");
                }

                list.getItems().setAll(upcoming);
                showReminderPopup(minutes);
                System.out.println("Reminders view updated");
                System.out.println("====================\n");
            } catch (Exception ex) {
                System.out.println("✗ Error loading reminders: " + ex.getMessage());
                showAlert("Invalid number", "Please enter valid reminder values.");
            }
        };

        load.setOnAction(e -> {
            System.out.println("Show upcoming button clicked");
            loadReminders.run();
        });

        showAllBtn.setOnAction(e -> {
            System.out.println("\n📋 SHOW ALL button clicked!");
            System.out.println("Loading all upcoming events...");

            LocalDateTime now = LocalDateTime.now();
            List<Event> allUpcoming = eventManager.getAllEvents().stream()
                    .filter(ev -> ev.getStartDateTime().isAfter(now))
                    .sorted(java.util.Comparator.comparing(Event::getStartDateTime))
                    .toList();

            System.out.println("Found " + allUpcoming.size() + " upcoming event(s)");
            if (!allUpcoming.isEmpty()) {
                System.out.println("All upcoming events:");
                for (Event ev : allUpcoming) {
                    Duration d = Duration.between(now, ev.getStartDateTime());
                    String timeUntil = formatDuration(d);
                    System.out.println("  - ID: " + ev.getEventId() +
                                     " | " + dateTimeFmt.format(ev.getStartDateTime()) +
                                     " | \"" + ev.getTitle() + "\" (in " + timeUntil + ")");
                }
            } else {
                System.out.println("  No upcoming events");
            }

            list.getItems().setAll(allUpcoming);
            System.out.println("Reminders view updated with all upcoming events");
            System.out.println("====================\n");
        });

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.getStyleClass().add("dialog-form");
        form.addRow(0, new Label("Reminder Type"), typeBox);
        form.addRow(1, new Label("Custom Minutes"), minutesSpinner);
        form.addRow(2, new Label("Custom Hours"), hoursSpinner);
        form.addRow(3, new Label("Custom Days"), daysSpinner);

        HBox buttonBox = new HBox(10, load, showAllBtn);
        VBox box = new VBox(10, form, buttonBox, list);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.showAndWait();
    }

    private List<Event> upcomingWithin(int minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.plusMinutes(minutes);
        return eventManager.getAllEvents().stream()
                .filter(ev -> ev.getStartDateTime().isAfter(now) && !ev.getStartDateTime().isAfter(cutoff))
                .sorted(java.util.Comparator.comparing(Event::getStartDateTime))
                .toList();
    }

    /**
     * Format duration in days, hours, and minutes
     * @param duration The duration to format
     * @return Formatted string like "2 days, 3 hours, 15 min" or "45 min" or "1 hour, 30 min"
     */
    private String formatDuration(Duration duration) {
        long totalMinutes = duration.toMinutes();

        if (totalMinutes <= 0) {
            return "now";
        }

        long days = totalMinutes / (24 * 60);
        long hours = (totalMinutes % (24 * 60)) / 60;
        long minutes = totalMinutes % 60;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append(days == 1 ? " day" : " days");
        }

        if (hours > 0) {
            if (result.length() > 0) result.append(", ");
            result.append(hours).append(hours == 1 ? " hour" : " hours");
        }

        if (minutes > 0) {
            if (result.length() > 0) result.append(", ");
            result.append(minutes).append(" min");
        }

        // If only days (no hours or minutes), still show it
        if (result.length() == 0) {
            result.append("0 min");
        }

        return result.toString();
    }

    private void showReminderPopup(int minutesBefore) {
        List<Event> upcoming = upcomingWithin(minutesBefore);
        if (upcoming.isEmpty()) return;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reminders");

        // Format the time window in days, hours, minutes
        String timeWindowFormatted = formatDuration(Duration.ofMinutes(minutesBefore));
        alert.setHeaderText("Events starting within " + timeWindowFormatted);

        StringBuilder sb = new StringBuilder();
        upcoming.stream().limit(5).forEach(ev -> {
            Duration d = Duration.between(LocalDateTime.now(), ev.getStartDateTime());
            String timeUntil = formatDuration(d);
            sb.append("[#").append(ev.getEventId()).append("] ")
                    .append(dateTimeFmt.format(ev.getStartDateTime()))
                    .append(" - ").append(ev.getTitle())
                    .append(" (in ").append(timeUntil).append(")\n");
        });
        alert.setContentText(sb.toString());
        var cssUrl = getClass().getResource("style.css");
        if (cssUrl != null) {
            alert.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        }
        alert.showAndWait();
    }

    private void showBackupDialog(Stage owner) {
        System.out.println("\n=== BACKUP / RESTORE ===");
        System.out.println("Opening backup/restore dialog");

        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Backup / Restore");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        // Backup section
        TextField backupPath = new TextField("backup.csv");
        backupPath.setPrefWidth(300);
        Button browseSaveBtn = new Button("Browse...");
        browseSaveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Backup File");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            fileChooser.setInitialFileName("backup.csv");
            File file = fileChooser.showSaveDialog(owner);
            if (file != null) {
                backupPath.setText(file.getAbsolutePath());
            }
        });

        Button backupBtn = new Button("Create Backup");
        backupBtn.getStyleClass().add("primary-button");
        backupBtn.setOnAction(e -> {
            System.out.println("\nCreating backup...");
            System.out.println("  Backup file: " + backupPath.getText());
            System.out.println("  Total events: " + eventManager.getAllEvents().size());

            boolean ok = eventManager.createBackup(backupPath.getText());

            if (ok) {
                System.out.println("✓ Backup created successfully!");
                System.out.println("  Location: " + backupPath.getText());
                showAlert("Backup", "Backup created at: " + backupPath.getText());
            } else {
                System.out.println("✗ Backup failed!");
                showAlert("Backup", "Backup failed.");
            }
            System.out.println("====================\n");
        });

        // Restore section
        TextField restorePath = new TextField("backup.csv");
        restorePath.setPrefWidth(300);
        Button browseOpenBtn = new Button("Browse...");
        browseOpenBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Backup File");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            File file = fileChooser.showOpenDialog(owner);
            if (file != null) {
                restorePath.setText(file.getAbsolutePath());
            }
        });

        CheckBox appendBox = new CheckBox("Append (otherwise replace)");
        Button restoreBtn = new Button("Restore");
        restoreBtn.getStyleClass().add("primary-button");
        restoreBtn.setOnAction(e -> {
            System.out.println("\nRestoring from backup...");
            System.out.println("  Backup file: " + restorePath.getText());
            System.out.println("  Mode: " + (appendBox.isSelected() ? "Append" : "Replace"));
            System.out.println("  Current events before restore: " + eventManager.getAllEvents().size());

            boolean ok = eventManager.restoreFromBackup(restorePath.getText(), appendBox.isSelected());

            if (ok) {
                System.out.println("✓ Restore completed successfully!");
                System.out.println("  Total events after restore: " + eventManager.getAllEvents().size());
                showAlert("Restore", "Restore complete from: " + restorePath.getText());
                populateCalendar(currentYearMonth); // Refresh calendar after restore
            } else {
                System.out.println("✗ Restore failed!");
                showAlert("Restore", "Restore failed.");
            }
            System.out.println("====================\n");
        });

        VBox box = new VBox(15,
                new Label("📁 BACKUP"),
                new HBox(8, new Label("Save to:"), backupPath, browseSaveBtn, backupBtn),
                new Separator(),
                new Label("📂 RESTORE"),
                new HBox(8, new Label("Open from:"), restorePath, browseOpenBtn, restoreBtn),
                appendBox);
        box.setPadding(new Insets(15));
        dialog.getDialogPane().setContent(box);
        dialog.showAndWait();
    }

    private void showStatsDialog(Stage owner) {
        System.out.println("\n=== STATISTICS ===");
        System.out.println("Generating statistics report...");

        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Statistics");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        // Set dialog to a more compact size
        dialog.setResizable(true);
        dialog.getDialogPane().setPrefSize(900, 600);
        dialog.getDialogPane().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        List<Event> all = eventManager.getAllEvents();
        if (all.isEmpty()) {
            System.out.println("No events found - cannot generate statistics");
            System.out.println("====================\n");
            showAlert("Statistics", "No events yet. Add events to see statistics.");
            return;
        }

        System.out.println("Analyzing " + all.size() + " event(s)...");

        // Calculate key metrics for console
        LocalDateTime now = LocalDateTime.now();
        long past = all.stream().filter(ev -> ev.getEndDateTime().isBefore(now)).count();
        long ongoing = all.stream().filter(ev -> ev.getStartDateTime().isBefore(now) && ev.getEndDateTime().isAfter(now)).count();
        long upcoming = all.stream().filter(ev -> ev.getStartDateTime().isAfter(now)).count();

        System.out.println("\n📊 QUICK STATISTICS SUMMARY:");
        System.out.println("  Total events: " + all.size());
        System.out.println("  Completed: " + past);
        System.out.println("  Ongoing: " + ongoing);
        System.out.println("  Upcoming: " + upcoming);

        StringBuilder sb = new StringBuilder();

        // Basic counts
        sb.append("╔════════════════════════════════════════════════════════╗\n");
        sb.append("║                    📊 CALENDAR STATS                     ║\n");
        sb.append("╚════════════════════════════════════════════════════════╝\n\n");
        sb.append("Total Events: ").append(all.size()).append("\n");
        sb.append("Next Event ID: ").append(eventManager.getNextEventId()).append("\n");
        // Reuse already calculated variables
        sb.append("  ✓ Completed: ").append(past).append("\n");
        sb.append("  → Ongoing: ").append(ongoing).append("\n");
        sb.append("  ◇ Upcoming: ").append(upcoming).append("\n\n");

        // Busiest day of week
        sb.append("📅 BUSIEST DAY OF WEEK\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        var eventsByDay = all.stream().collect(Collectors.groupingBy(e -> e.getStartDateTime().getDayOfWeek(), Collectors.counting()));
        var busiestDay = eventsByDay.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        if (busiestDay != null) {
            sb.append("Busiest: ").append(busiestDay).append(" (").append(eventsByDay.get(busiestDay)).append(" events)\n");
            for (var day : java.time.DayOfWeek.values()) {
                long c = eventsByDay.getOrDefault(day, 0L);
                String bar = "■".repeat((int) Math.min(c * 3, 30));
                sb.append(String.format("  %-10s: %s (%d)%n", day, bar, c));
            }
        }
        sb.append("\n");

        // Peak hours
        sb.append("🕐 PEAK HOURS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        var eventsByHour = all.stream().collect(Collectors.groupingBy(e -> e.getStartDateTime().getHour(), Collectors.counting()));
        var peakHour = eventsByHour.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        if (peakHour != null) {
            sb.append(String.format("Peak: %02d:00 (%d events)%n", peakHour, eventsByHour.get(peakHour)));
            for (int h = 0; h < 24; h++) {
                long c = eventsByHour.getOrDefault(h, 0L);
                if (c > 0) {
                    String bar = "█".repeat((int) Math.min(c * 2, 40));
                    sb.append(String.format("  %02d:00 │ %s (%d)%n", h, bar, c));
                }
            }
        }
        sb.append("\n");

        // Duration analysis
        sb.append("⏱️  DURATION ANALYSIS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        var durations = all.stream()
                .map(ev -> (double) Duration.between(ev.getStartDateTime(), ev.getEndDateTime()).toMinutes())
                .toList();
        if (!durations.isEmpty()) {
            double avg = durations.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double total = durations.stream().mapToDouble(Double::doubleValue).sum();
            double min = durations.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double max = durations.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            sb.append(String.format("Average: %.1f min (%.1f h)%n", avg, avg / 60));
            sb.append(String.format("Total: %.1f h (%.1f days)%n", total / 60, total / 1440));
            sb.append(String.format("Shortest: %.0f min | Longest: %.0f min%n", min, max));
            long quick = durations.stream().filter(d -> d < 30).count();
            long standard = durations.stream().filter(d -> d >= 30 && d < 120).count();
            long longDur = durations.stream().filter(d -> d >= 120).count();
            sb.append(String.format("Quick (<30m): %d%n", quick));
            sb.append(String.format("Standard (30-120m): %d%n", standard));
            sb.append(String.format("Long (>120m): %d%n", longDur));
        }
        sb.append("\n");

        // Time of day distribution
        sb.append("⏰ TIME OF DAY\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        long morning = all.stream().filter(e -> e.getStartDateTime().getHour() >= 6 && e.getStartDateTime().getHour() < 12).count();
        long afternoon = all.stream().filter(e -> e.getStartDateTime().getHour() >= 12 && e.getStartDateTime().getHour() < 18).count();
        long evening = all.stream().filter(e -> e.getStartDateTime().getHour() >= 18 && e.getStartDateTime().getHour() < 24).count();
        long night = all.stream().filter(e -> e.getStartDateTime().getHour() < 6).count();
        long total = all.size();
        sb.append(String.format("Morning (6-12): %d (%.1f%%)%n", morning, morning * 100.0 / total));
        sb.append(String.format("Afternoon (12-18): %d (%.1f%%)%n", afternoon, afternoon * 100.0 / total));
        sb.append(String.format("Evening (18-24): %d (%.1f%%)%n", evening, evening * 100.0 / total));
        sb.append(String.format("Night (0-6): %d (%.1f%%)%n", night, night * 100.0 / total));
        sb.append("\n");

        // Events by month
        sb.append("📊 EVENTS BY MONTH\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        var eventsByMonth = all.stream()
                .collect(Collectors.groupingBy(ev -> ev.getStartDateTime().getMonth().toString() + " " + ev.getStartDateTime().getYear(), Collectors.counting()));
        eventsByMonth.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(e -> sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append(" events\n"));
        sb.append("\n");

        // Busiest dates
        sb.append("🔥 BUSIEST DATES (Top 5)\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        var eventsByDate = all.stream().collect(Collectors.groupingBy(ev -> ev.getStartDateTime().toLocalDate(), Collectors.counting()));
        eventsByDate.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(e -> sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append(" events\n"));
        sb.append("\n");

        // Schedule efficiency
        sb.append("⚡ SCHEDULE EFFICIENCY\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        double avgPerDay = (double) all.size() / Math.max(1, eventsByDate.size());
        sb.append(String.format("Average events per active day: %.1f%n", avgPerDay));
        sb.append(String.format("Days with events: %d%n", eventsByDate.size()));
        if (avgPerDay > 8) sb.append("⚠️  VERY HEAVY: Consider rescheduling.\n");
        else if (avgPerDay > 5) sb.append("✓ BUSY: Ensure breaks.\n");
        else if (avgPerDay > 2) sb.append("✓ BALANCED schedule.\n");
        else sb.append("✓ LIGHT schedule.\n");

        // Additional console output with key insights
        System.out.println("\n📈 KEY INSIGHTS:");

        // Busiest day (already calculated above)
        if (busiestDay != null) {
            System.out.println("  Busiest day of week: " + busiestDay + " (" + eventsByDay.get(busiestDay) + " events)");
        }

        // Peak hour (already calculated above)
        if (peakHour != null) {
            System.out.println("  Peak hour: " + String.format("%02d:00", peakHour) + " (" + eventsByHour.get(peakHour) + " events)");
        }

        // Duration stats (already calculated above)
        if (!durations.isEmpty()) {
            double avg = durations.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            System.out.println("  Average event duration: " + String.format("%.1f", avg) + " minutes (" + String.format("%.1f", avg/60) + " hours)");
        }

        // Time of day (already calculated above)
        System.out.println("  Time distribution: Morning=" + morning + ", Afternoon=" + afternoon + ", Evening=" + evening + ", Night=" + night);

        // Busiest date
        var busiestDate = eventsByDate.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        if (busiestDate != null) {
            System.out.println("  Busiest date: " + busiestDate + " (" + eventsByDate.get(busiestDate) + " events)");
        }

        // Schedule assessment
        System.out.println("  Schedule assessment: " +
            (avgPerDay > 8 ? "VERY HEAVY" :
             avgPerDay > 5 ? "BUSY" :
             avgPerDay > 2 ? "BALANCED" : "LIGHT"));
        System.out.println("  Days with events: " + eventsByDate.size());
        System.out.println("  Average events per active day: " + String.format("%.1f", avgPerDay));

        System.out.println("\nStatistics report generated successfully");
        System.out.println("====================\n");

        TextArea area = new TextArea(sb.toString());
        area.setEditable(false);
        area.setWrapText(true);
        area.setFont(Font.font("Monospaced", 12));
        area.setPrefSize(800, 500);
        area.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        area.setStyle("-fx-text-alignment: center;");

        VBox box = new VBox(area);
        box.setPadding(new Insets(10));
        VBox.setVgrow(area, Priority.ALWAYS);
        dialog.getDialogPane().setContent(box);
        dialog.showAndWait();
    }

    private void showEditDialog(Stage owner) {
        System.out.println("\n=== EDIT EVENT ===");
        Event selected = promptEventByIdOrTitle(owner);
        if (selected == null) {
            System.out.println("Edit cancelled - no event selected");
            return;
        }

        System.out.println("Editing event:");
        System.out.println("  ID: " + selected.getEventId());
        System.out.println("  Title: \"" + selected.getTitle() + "\"");
        System.out.println("  Description: \"" + selected.getDescription() + "\"");
        System.out.println("  Start: " + dateTimeFmt.format(selected.getStartDateTime()));
        System.out.println("  End: " + dateTimeFmt.format(selected.getEndDateTime()));

        final Event sel = selected;

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Edit Event");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField titleField = new TextField(selected.getTitle());
        TextArea descField = new TextArea(selected.getDescription());
        descField.setPrefRowCount(3);
        descField.getStyleClass().add("input-area");
        TextField startField = new TextField(dateTimeFmt24.format(selected.getStartDateTime()));
        TextField endField = new TextField(dateTimeFmt24.format(selected.getEndDateTime()));
        startField.getStyleClass().add("input-field");
        endField.getStyleClass().add("input-field");

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.getStyleClass().add("dialog-form");
        form.addRow(0, new Label("Title"), titleField);
        form.addRow(1, new Label("Description"), descField);
        form.addRow(2, new Label("Start (yyyy-MM-dd HH:mm)"), startField);
        form.addRow(3, new Label("End (yyyy-MM-dd HH:mm)"), endField);
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    LocalDateTime start = parseDateTime(startField.getText());
                    LocalDateTime end = parseDateTime(endField.getText());
                    if (!end.isAfter(start)) {
                        showAlert("Invalid range", "End time must be after start time.");
                        return false;
                    }
                    final LocalDateTime startFinal = start;
                    final LocalDateTime endFinal = end;
                    List<Event> conflicts = eventManager.checkConflicts(startFinal, endFinal).stream()
                            .filter(ev -> ev.getEventId() != sel.getEventId())
                            .toList();
                    if (!conflicts.isEmpty()) {
                        System.out.println("Time conflict detected with " + conflicts.size() + " event(s)");
                        String summary = conflicts.stream().limit(3)
                                .map(ev -> dateTimeFmt.format(ev.getStartDateTime()) + " " + ev.getTitle())
                                .collect(Collectors.joining("\n"));
                        if (!showConfirm("Time conflict", "Overlaps with existing events:\n" + summary + "\nSave anyway?")) {
                            System.out.println("Edit cancelled due to conflict");
                            return false;
                        }
                    }

                    System.out.println("\nUpdating event ID " + sel.getEventId() + ":");
                    System.out.println("  Old Title: \"" + sel.getTitle() + "\" → New Title: \"" + titleField.getText() + "\"");
                    System.out.println("  Old Description: \"" + sel.getDescription() + "\" → New Description: \"" + descField.getText() + "\"");
                    System.out.println("  Old Start: " + dateTimeFmt.format(sel.getStartDateTime()) + " → New Start: " + dateTimeFmt.format(startFinal));
                    System.out.println("  Old End: " + dateTimeFmt.format(sel.getEndDateTime()) + " → New End: " + dateTimeFmt.format(endFinal));

                    boolean success = eventManager.updateEvent(sel.getEventId(), titleField.getText(), descField.getText(), startFinal, endFinal);
                    if (success) {
                        System.out.println("✓ Event updated successfully!");
                        showAlert("Success", "Event updated successfully!");
                    } else {
                        System.out.println("✗ Failed to update event - event not found");
                        showAlert("Error", "Failed to update event. Event not found.");
                    }
                    System.out.println("==================\n");
                    return success;
                } catch (DateTimeParseException ex) {
                    System.out.println("✗ Invalid date/time format: " + ex.getMessage());
                    showAlert("Invalid date/time", "Use format yyyy-MM-dd hh:mm AM/PM or yyyy-MM-dd HH:mm");
                    return false;
                }
            }
            System.out.println("Edit cancelled by user");
            return false;
        });

        dialog.showAndWait().ifPresent(success -> {
            if (success) {
                populateCalendar(currentYearMonth); // Refresh calendar after edit
            }
        });
    }

    private void deleteSelected() {
        showDeleteDialog(null);
    }

    private void showDeleteDialog(Stage owner) {
        System.out.println("\n=== DELETE EVENT ===");
        Dialog<Void> dialog = new Dialog<>();
        if (owner != null) dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Delete Event");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        TextField idField = new TextField();
        TextField titleField = new TextField();
        ListView<Event> results = new ListView<>();
        results.setCellFactory(createEventCellFactory());

        // No preloaded selection since sidebar is removed

        // Create a runnable for searching/refreshing the list
        Runnable performSearch = () -> {
            System.out.println("\nSearching for events to delete...");
            List<Event> all = eventManager.getAllEvents();
            Integer idVal = null;
            try {
                if (!idField.getText().isBlank()) idVal = Integer.parseInt(idField.getText());
            } catch (NumberFormatException ignored) {}
            final Integer idValFinal = idVal;
            final String titleKw = titleField.getText().toLowerCase();

            // Log search criteria
            if (idValFinal != null) {
                System.out.println("  Search by ID: " + idValFinal);
            }
            if (!titleKw.isBlank()) {
                System.out.println("  Search by Title: \"" + titleKw + "\"");
            }

            List<Event> matches = all.stream()
                    .filter(ev -> {
                        if (idValFinal != null && ev.getEventId() != idValFinal) return false;
                        if (!titleKw.isBlank() && !ev.getTitle().toLowerCase().contains(titleKw)) return false;
                        return true;
                    })
                    .toList();

            System.out.println("Found " + matches.size() + " event(s) matching criteria");
            if (!matches.isEmpty()) {
                for (Event ev : matches) {
                    System.out.println("  - ID: " + ev.getEventId() + " | \"" + ev.getTitle() + "\" | " + dateTimeFmt.format(ev.getStartDateTime()));
                }
            }

            results.getItems().setAll(matches);
            results.getSelectionModel().clearSelection(); // Clear selection after refresh
        };

        Button findBtn = new Button("Find");
        findBtn.getStyleClass().add("primary-button");
        findBtn.setOnAction(e -> performSearch.run());

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("primary-button");
        deleteBtn.setOnAction(e -> {
            Event target = results.getSelectionModel().getSelectedItem();
            if (target == null) {
                System.out.println("✗ No event selected for deletion");
                showAlert("No selection", "Select an event to delete.");
                return;
            }

            System.out.println("\nAttempting to delete event:");
            System.out.println("  ID: " + target.getEventId());
            System.out.println("  Title: \"" + target.getTitle() + "\"");
            System.out.println("  Description: \"" + target.getDescription() + "\"");
            System.out.println("  Start: " + dateTimeFmt.format(target.getStartDateTime()));
            System.out.println("  End: " + dateTimeFmt.format(target.getEndDateTime()));

            if (showConfirm("Delete", "Delete event #" + target.getEventId() + " - " + target.getTitle() + "?")) {
                boolean success = eventManager.deleteEvent(target.getEventId());
                if (success) {
                    System.out.println("✓ Event deleted successfully!");
                    System.out.println("Refreshing event list...");
                    System.out.println("==================\n");
                    populateCalendar(currentYearMonth); // Refresh calendar after delete

                    // Auto-refresh the search results to show remaining events
                    performSearch.run();
                } else {
                    System.out.println("✗ Failed to delete event - event not found");
                    System.out.println("==================\n");
                }
            } else {
                System.out.println("Delete cancelled by user");
                System.out.println("==================\n");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.getStyleClass().add("dialog-form");
        form.addRow(0, new Label("Event ID"), idField);
        form.addRow(1, new Label("Title contains"), titleField);

        HBox actions = new HBox(10, findBtn, deleteBtn);
        VBox box = new VBox(10, form, actions, results);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        dialog.showAndWait();
    }

    private Event promptEventByIdOrTitle(Stage owner) {
        Dialog<Event> dialog = new Dialog<>();
        if (owner != null) dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Find Event by ID or Title");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idField = new TextField();
        TextField titleField = new TextField();

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(10));
        form.getStyleClass().add("dialog-form");
        form.addRow(0, new Label("Event ID"), idField);
        form.addRow(1, new Label("Title contains"), titleField);
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String idText = idField.getText().trim();
                String titleText = titleField.getText().trim();

                // Validate that at least one field is filled
                if (idText.isBlank() && titleText.isBlank()) {
                    showAlert("Input Required", "Please enter either an Event ID or a title to search.");
                    return null;
                }

                List<Event> all = eventManager.getAllEvents();
                Integer idVal = null;

                // Try to parse ID if provided
                if (!idText.isBlank()) {
                    try {
                        idVal = Integer.parseInt(idText);
                    } catch (NumberFormatException e) {
                        showAlert("Invalid ID", "Event ID must be a number. Please enter a valid numeric ID.");
                        return null;
                    }
                }

                final Integer idValFinal = idVal;
                final String titleKw = titleText.toLowerCase();

                // Filter events based on ID and/or title
                List<Event> matches = all.stream()
                        .filter(ev -> {
                            boolean idMatch = (idValFinal != null && ev.getEventId() == idValFinal);
                            boolean titleMatch = (!titleKw.isBlank() && ev.getTitle().toLowerCase().contains(titleKw));

                            // If only ID is provided, match by ID
                            if (idValFinal != null && titleKw.isBlank()) {
                                return idMatch;
                            }
                            // If only title is provided, match by title
                            if (idValFinal == null && !titleKw.isBlank()) {
                                return titleMatch;
                            }
                            // If both are provided, match either one
                            return idMatch || titleMatch;
                        })
                        .toList();

                if (matches.isEmpty()) {
                    String criteria = "";
                    if (idVal != null && !titleKw.isBlank()) {
                        criteria = "ID '" + idVal + "' or title containing '" + titleText + "'";
                    } else if (idVal != null) {
                        criteria = "ID '" + idVal + "'";
                    } else {
                        criteria = "title containing '" + titleText + "'";
                    }
                    showAlert("No Match", "No event found with " + criteria);
                    return null;
                }

                if (matches.size() == 1) {
                    return matches.get(0);
                }

                // Multiple matches - let user choose
                Map<String, Event> labelMap = new HashMap<>();
                List<String> labels = matches.stream()
                        .map(ev -> {
                            String label = "ID: " + ev.getEventId() + " | " + dateTimeFmt.format(ev.getStartDateTime()) + " | " + ev.getTitle();
                            labelMap.put(label, ev);
                            return label;
                        })
                        .toList();
                ChoiceDialog<String> chooser = new ChoiceDialog<>(labels.get(0), labels);
                chooser.setTitle("Choose Event");
                chooser.setHeaderText("Multiple matches found (" + matches.size() + " events)");
                chooser.setContentText("Select event:");
                return chooser.showAndWait().map(labelMap::get).orElse(null);
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
