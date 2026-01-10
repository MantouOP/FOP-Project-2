import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

/**
 * Minimal Swing GUI demonstrating Month and List Views.
 * Extra feature for UI/visualization role.
 */
public class CalendarGUI {
    private EventManager eventManager;
    private CalendarView calendarView;
    private JFrame frame;
    private JPanel monthGrid;
    private JTextArea detailsArea;
    private JComboBox<Integer> monthBox;
    private JTextField yearField;
    private JLabel titleLabel;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CalendarGUI(EventManager em) {
        this.eventManager = em;
        this.calendarView = new CalendarView();
        initUI();
    }

    private void initUI() {
        frame = new JFrame("Calendar GUI");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLayout(new BorderLayout());

        // Add title and month selector panel at top
        JPanel titleMonthPanel = new JPanel(new BorderLayout());
        titleMonthPanel.setBackground(new Color(0xE9EEF4));
        titleMonthPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left side: Title
        titleLabel = new JLabel("Calendar");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x8688AD));

        // Center: Month/Year tabs with navigation
        JPanel monthTabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        monthTabPanel.setBackground(new Color(0xE9EEF4));

        JButton prevTabBtn = new JButton("◀");
        prevTabBtn.setToolTipText("Previous month");
        prevTabBtn.setBackground(new Color(0xBBC4F4));
        prevTabBtn.setForeground(Color.WHITE);
        prevTabBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x8EBAF4), 1));
        prevTabBtn.setPreferredSize(new Dimension(40, 36));
        prevTabBtn.addActionListener(e -> navigatePreviousMonth());

        // Month/Year tab display
        monthBox = new JComboBox<>();
        for (int m = 1; m <= 12; m++) monthBox.addItem(m);
        monthBox.setPreferredSize(new Dimension(80, 36));
        monthBox.addActionListener(e -> loadMonth());

        yearField = new JTextField(String.valueOf(LocalDate.now().getYear()), 6);
        yearField.setPreferredSize(new Dimension(60, 36));
        yearField.setBackground(Color.WHITE);
        yearField.setForeground(new Color(0x8688AD));

        JButton nextTabBtn = new JButton("▶");
        nextTabBtn.setToolTipText("Next month");
        nextTabBtn.setBackground(new Color(0xBBC4F4));
        nextTabBtn.setForeground(Color.WHITE);
        nextTabBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x8EBAF4), 1));
        nextTabBtn.setPreferredSize(new Dimension(40, 36));
        nextTabBtn.addActionListener(e -> navigateNextMonth());

        monthTabPanel.add(prevTabBtn);
        monthTabPanel.add(new JLabel("Month:"));
        monthTabPanel.add(monthBox);
        monthTabPanel.add(new JLabel("Year:"));
        monthTabPanel.add(yearField);
        monthTabPanel.add(nextTabBtn);

        titleMonthPanel.add(titleLabel, BorderLayout.WEST);
        titleMonthPanel.add(monthTabPanel, BorderLayout.CENTER);

        frame.add(titleMonthPanel, BorderLayout.NORTH);

        // Feature buttons panel - Row 1
        JPanel featureButtonsPanel = new JPanel(new GridLayout(2, 5, 8, 8));
        featureButtonsPanel.setBackground(new Color(0xE9EEF4));
        featureButtonsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton createEventBtn = new JButton("➕ Create Event");
        createEventBtn.setBackground(Color.WHITE);
        createEventBtn.setForeground(new Color(0x758BED));
        createEventBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        createEventBtn.setToolTipText("Create a new single event with title, description, start and end times");
        createEventBtn.addActionListener(e -> showCreateEventDialog());

        JButton createRecurringBtn = new JButton("🔄 Recurring Event");
        createRecurringBtn.setBackground(Color.WHITE);
        createRecurringBtn.setForeground(new Color(0x758BED));
        createRecurringBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        createRecurringBtn.setToolTipText("Create repeating events (daily, weekly, bi-weekly, or monthly) with specified occurrences");
        createRecurringBtn.addActionListener(e -> showCreateRecurringEventDialog());

        JButton searchBtn = new JButton("🔍 Search Events");
        searchBtn.setBackground(Color.WHITE);
        searchBtn.setForeground(new Color(0x758BED));
        searchBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        searchBtn.setToolTipText("Search events by date, title, or date range to find specific events");
        searchBtn.addActionListener(e -> showSearchDialog());

        JButton editDeleteBtn = new JButton("✏️ Edit/Delete");
        editDeleteBtn.setBackground(Color.WHITE);
        editDeleteBtn.setForeground(new Color(0x758BED));
        editDeleteBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        editDeleteBtn.setToolTipText("Find an event by ID, then update its details or remove it completely");
        editDeleteBtn.addActionListener(e -> showEditDeleteDialog());

        JButton backupBtn = new JButton("💾 Backup/Restore");
        backupBtn.setBackground(Color.WHITE);
        backupBtn.setForeground(new Color(0x758BED));
        backupBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        backupBtn.setToolTipText("Save all events to a backup file or restore from a previously saved backup");
        backupBtn.addActionListener(e -> showBackupDialog());

        // Row 2 buttons
        JButton weekViewBtn = new JButton("📅 Week View");
        weekViewBtn.setBackground(Color.WHITE);
        weekViewBtn.setForeground(new Color(0x758BED));
        weekViewBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        weekViewBtn.setToolTipText("View all events for a specific week in a day-by-day format");
        weekViewBtn.addActionListener(e -> showWeekViewDialog());

        JButton listBtn = new JButton("📋 List View");
        listBtn.setBackground(Color.WHITE);
        listBtn.setForeground(new Color(0x758BED));
        listBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        listBtn.setToolTipText("Display events within a date range as a sorted list");
        listBtn.addActionListener(e -> showListViewDialog());

        JButton remindersBtn = new JButton("🔔 Reminders");
        remindersBtn.setBackground(Color.WHITE);
        remindersBtn.setForeground(new Color(0x758BED));
        remindersBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        remindersBtn.setToolTipText("View upcoming events scheduled to start within a specified time window");
        remindersBtn.addActionListener(e -> showRemindersDialog());

        JButton statsBtn = new JButton("📊 Statistics");
        statsBtn.setBackground(Color.WHITE);
        statsBtn.setForeground(new Color(0x758BED));
        statsBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        statsBtn.setToolTipText("View calendar analytics: total events, events by month, busiest days, and upcoming events");
        statsBtn.addActionListener(e -> showStatisticsDialog());

        JButton calendarViewBtn = new JButton("📅 Calendar View");
        calendarViewBtn.setBackground(Color.WHITE);
        calendarViewBtn.setForeground(new Color(0x758BED));
        calendarViewBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xBBC4F4), 1));
        calendarViewBtn.setToolTipText("Display month view in CLI format with events marked");
        calendarViewBtn.addActionListener(e -> showCalendarViewDialog());

        // Add buttons to panel
        featureButtonsPanel.add(createEventBtn);
        featureButtonsPanel.add(createRecurringBtn);
        featureButtonsPanel.add(searchBtn);
        featureButtonsPanel.add(editDeleteBtn);
        featureButtonsPanel.add(backupBtn);
        featureButtonsPanel.add(weekViewBtn);
        featureButtonsPanel.add(listBtn);
        featureButtonsPanel.add(calendarViewBtn);
        featureButtonsPanel.add(remindersBtn);
        featureButtonsPanel.add(statsBtn);

        frame.add(featureButtonsPanel, BorderLayout.AFTER_LINE_ENDS);

        monthGrid = new JPanel(new GridLayout(0, 7, 4, 4));
        monthGrid.setBackground(new Color(0xCED4EB));
        frame.add(monthGrid, BorderLayout.CENTER);

        // Add keyboard listener to frame for global shortcuts
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_UP:
                        navigatePreviousMonth();
                        e.consume();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_DOWN:
                        navigateNextMonth();
                        e.consume();
                        break;
                    case KeyEvent.VK_P:
                        navigatePreviousMonth();
                        e.consume();
                        break;
                    case KeyEvent.VK_N:
                        navigateNextMonth();
                        e.consume();
                        break;
                    case KeyEvent.VK_T:
                        LocalDate today = LocalDate.now();
                        monthBox.setSelectedIndex(today.getMonthValue() - 1);
                        yearField.setText(String.valueOf(today.getYear()));
                        e.consume();
                        break;
                }
            }
        });

        // Allow changing month by mouse wheel when cursor is over the calendar grid
        monthGrid.addMouseWheelListener(e -> {
            int notches = e.getWheelRotation(); // negative = up, positive = down
            if (notches < 0) {
                navigatePreviousMonth();
            } else if (notches > 0) {
                navigateNextMonth();
            }
        });

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setBackground(Color.WHITE);
        detailsArea.setForeground(new Color(0x8688AD)); // Dark gray text
        detailsArea.setFont(detailsArea.getFont().deriveFont(13f));
        JScrollPane scroll = new JScrollPane(detailsArea);
        scroll.setPreferredSize(new Dimension(1000, 180));
        scroll.setBackground(Color.WHITE);
        frame.add(scroll, BorderLayout.SOUTH);

        // Add help panel at bottom with keyboard shortcuts
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        helpPanel.setBackground(new Color(0xE9EEF4));
        helpPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel helpLabel = new JLabel("<html><b>Keyboard Shortcuts:</b> ◀/▶ or ←/→ or P/N = Navigate months | T = Today | " +
                "<b>Mouse Wheel:</b> Scroll over calendar to change month | " +
                "<b>Hover:</b> Tooltips on buttons for more info</html>");
        helpLabel.setForeground(new Color(0x8688AD));
        helpLabel.setFont(helpLabel.getFont().deriveFont(11f));
        helpPanel.add(helpLabel);
        frame.add(helpPanel, BorderLayout.AFTER_LAST_LINE);

        // Initial load
        monthBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        loadMonth();

        frame.setFocusable(true);
        frame.setVisible(true);
    }

    private void showCreateEventDialog() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField(20);
        JTextField descField = new JTextField(20);

        // Use today's date + 1 hour as default
        LocalDate today = LocalDate.now();
        LocalDateTime defaultStart = today.atTime(14, 0);  // 2 PM today
        LocalDateTime defaultEnd = today.atTime(15, 0);    // 3 PM today

        JTextField startField = new JTextField(defaultStart.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 20);
        JTextField endField = new JTextField(defaultEnd.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 20);

        p.add(new JLabel("Title:"));
        p.add(titleField);
        p.add(new JLabel("Description:"));
        p.add(descField);
        p.add(new JLabel("Start (yyyy-MM-dd HH:mm):"));
        p.add(startField);
        p.add(new JLabel("End (yyyy-MM-dd HH:mm):"));
        p.add(endField);

        int res = JOptionPane.showConfirmDialog(frame, p, "Create Event", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String desc = descField.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Title cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                eventManager.createEvent(title, desc,
                        java.time.LocalDateTime.parse(startField.getText().trim(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        java.time.LocalDateTime.parse(endField.getText().trim(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                JOptionPane.showMessageDialog(frame, "Event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMonth();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCreateRecurringEventDialog() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField(20);
        JTextField descField = new JTextField(20);

        // Use today's date + 1 hour as default
        LocalDate today = LocalDate.now();
        LocalDateTime defaultStart = today.atTime(14, 0);  // 2 PM today
        LocalDateTime defaultEnd = today.atTime(15, 0);    // 3 PM today

        JTextField startField = new JTextField(defaultStart.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 20);
        JTextField endField = new JTextField(defaultEnd.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 20);
        JComboBox<String> intervalBox = new JComboBox<>(new String[]{"1d (Daily)", "1w (Weekly)", "2w (Bi-weekly)", "1m (Monthly)"});
        JSpinner occurrencesSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(5, 1, 100, 1));

        p.add(new JLabel("Title:"));
        p.add(titleField);
        p.add(new JLabel("Description:"));
        p.add(descField);
        p.add(new JLabel("Start (yyyy-MM-dd HH:mm):"));
        p.add(startField);
        p.add(new JLabel("End (yyyy-MM-dd HH:mm):"));
        p.add(endField);
        p.add(new JLabel("Interval:"));
        p.add(intervalBox);
        p.add(new JLabel("Occurrences:"));
        p.add(occurrencesSpinner);

        int res = JOptionPane.showConfirmDialog(frame, p, "Create Recurring Event", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String desc = descField.getText().trim();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Title cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String intervalStr = (String) intervalBox.getSelectedItem();
                String interval = intervalStr.substring(0, intervalStr.indexOf(" "));
                int occurrences = (Integer) occurrencesSpinner.getValue();

                eventManager.createRecurringEvent(title, desc,
                        java.time.LocalDateTime.parse(startField.getText().trim(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        java.time.LocalDateTime.parse(endField.getText().trim(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        interval, occurrences, null);
                JOptionPane.showMessageDialog(frame, "Recurring event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMonth();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showSearchDialog() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> searchType = new JComboBox<>(new String[]{"By Date", "By Title", "By Date Range"});
        JTextField input1 = new JTextField(20);
        JTextField input2 = new JTextField(20);

        // Set helpful default values
        LocalDate today = LocalDate.now();
        input1.setText(today.toString());
        input2.setText(today.plusDays(7).toString());

        p.add(new JLabel("Search Type:"));
        p.add(searchType);
        p.add(new JLabel("Query (yyyy-MM-dd or keyword):"));
        p.add(input1);
        p.add(new JLabel("End Date (yyyy-MM-dd):"));
        p.add(input2);

        int res = JOptionPane.showConfirmDialog(frame, p, "Search Events", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String type = (String) searchType.getSelectedItem();
                List<Event> results = null;

                if (type.equals("By Date")) {
                    String dateStr = input1.getText().trim();
                    if (dateStr.isEmpty()) dateStr = today.toString();
                    LocalDate date = LocalDate.parse(dateStr);
                    results = eventManager.searchEventsByDate(date);
                } else if (type.equals("By Title")) {
                    String keyword = input1.getText().trim();
                    if (keyword.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please enter a keyword to search for", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    results = eventManager.searchEventsByTitle(keyword);
                } else {
                    String startStr = input1.getText().trim();
                    String endStr = input2.getText().trim();
                    if (startStr.isEmpty()) startStr = today.toString();
                    if (endStr.isEmpty()) endStr = today.plusDays(7).toString();
                    LocalDate start = LocalDate.parse(startStr);
                    LocalDate end = LocalDate.parse(endStr);
                    results = eventManager.searchEventsByDateRange(start, end);
                }

                displaySearchResults(results, type);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDeleteDialog() {
        JPanel p = new JPanel(new FlowLayout());
        JTextField idField = new JTextField(10);
        p.add(new JLabel("Event ID:"));
        p.add(idField);

        int res = JOptionPane.showConfirmDialog(frame, p, "Find Event", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int eventId = Integer.parseInt(idField.getText().trim());
                Event event = eventManager.findEventById(eventId);

                if (event == null) {
                    JOptionPane.showMessageDialog(frame, "Event not found", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String[] options = {"Update", "Delete", "Cancel"};
                int choice = JOptionPane.showOptionDialog(frame,
                        "Title: " + event.getTitle() + "\nStart: " + event.getStartDateTime(),
                        "Event Details", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);

                if (choice == 0) {
                    showUpdateDialog(event);
                } else if (choice == 1) {
                    int confirm = JOptionPane.showConfirmDialog(frame, "Delete this event?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        eventManager.deleteEvent(eventId);
                        JOptionPane.showMessageDialog(frame, "Event deleted", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadMonth();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showUpdateDialog(Event event) {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField(event.getTitle(), 20);
        JTextField descField = new JTextField(event.getDescription(), 20);
        JTextField startField = new JTextField(event.getStartDateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 20);
        JTextField endField = new JTextField(event.getEndDateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 20);

        p.add(new JLabel("Title:"));
        p.add(titleField);
        p.add(new JLabel("Description:"));
        p.add(descField);
        p.add(new JLabel("Start:"));
        p.add(startField);
        p.add(new JLabel("End:"));
        p.add(endField);

        int res = JOptionPane.showConfirmDialog(frame, p, "Update Event", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                eventManager.updateEvent(event.getEventId(), titleField.getText(), descField.getText(),
                        java.time.LocalDateTime.parse(startField.getText(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        java.time.LocalDateTime.parse(endField.getText(), java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                JOptionPane.showMessageDialog(frame, "Event updated", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMonth();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showBackupDialog() {
        JPanel mainPanel = new JPanel(new GridLayout(0, 1, 5, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Operation selection
        JPanel operationPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        operationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Operation"));
        JRadioButton backupRb = new JRadioButton("💾 Create Backup", true);
        JRadioButton restoreRb = new JRadioButton("📂 Restore from Backup");
        ButtonGroup bg = new ButtonGroup();
        bg.add(backupRb);
        bg.add(restoreRb);
        operationPanel.add(backupRb);
        operationPanel.add(restoreRb);
        mainPanel.add(operationPanel);

        // File selection panel
        JPanel filePanel = new JPanel(new GridLayout(0, 1, 5, 5));
        filePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Backup File"));

        JPanel fileInputPanel = new JPanel(new BorderLayout(5, 5));
        JTextField fileField = new JTextField(System.getProperty("user.home") + "/calendar_backup.bak", 30);
        JButton browseBtn = new JButton("📁 Browse");
        browseBtn.addActionListener(e -> {
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
            fileChooser.setDialogType(backupRb.isSelected() ? javax.swing.JFileChooser.SAVE_DIALOG : javax.swing.JFileChooser.OPEN_DIALOG);
            fileChooser.setDialogTitle(backupRb.isSelected() ? "Save Backup" : "Open Backup");

            int result = fileChooser.showDialog(frame, backupRb.isSelected() ? "Save" : "Open");
            if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                fileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        fileInputPanel.add(fileField, BorderLayout.CENTER);
        fileInputPanel.add(browseBtn, BorderLayout.EAST);
        filePanel.add(fileInputPanel);
        mainPanel.add(filePanel);

        // Restore options panel
        JPanel restoreOptionsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        restoreOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Restore Options"));
        JRadioButton replaceRb = new JRadioButton("🗑️  Replace All Events (Delete existing, import from backup)", true);
        JRadioButton appendRb = new JRadioButton("➕ Append to Existing Events (Keep existing, add from backup)");
        ButtonGroup restoreGroup = new ButtonGroup();
        restoreGroup.add(replaceRb);
        restoreGroup.add(appendRb);
        restoreOptionsPanel.add(replaceRb);
        restoreOptionsPanel.add(appendRb);
        mainPanel.add(restoreOptionsPanel);

        // Enable/disable restore options based on operation selection
        backupRb.addActionListener(e -> {
            replaceRb.setEnabled(false);
            appendRb.setEnabled(false);
        });
        restoreRb.addActionListener(e -> {
            replaceRb.setEnabled(true);
            appendRb.setEnabled(true);
        });

        int res = JOptionPane.showConfirmDialog(frame, mainPanel,
            "💾 Backup & Restore Manager", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            try {
                String filePath = fileField.getText().trim();

                if (filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please specify a file path.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (backupRb.isSelected()) {
                    // CREATE BACKUP
                    if (eventManager.createBackup(filePath)) {
                        java.nio.file.Path backupPath = java.nio.file.Paths.get(filePath);
                        long fileSize = java.nio.file.Files.size(backupPath);
                        int eventCount = eventManager.getAllEvents().size();

                        StringBuilder successMsg = new StringBuilder();
                        successMsg.append("✅ BACKUP COMPLETED SUCCESSFULLY\n\n");
                        successMsg.append("📁 Location: ").append(backupPath.toAbsolutePath()).append("\n");
                        successMsg.append("📊 Events Backed Up: ").append(eventCount).append("\n");
                        successMsg.append("💾 File Size: ").append(String.format("%.2f KB", fileSize / 1024.0)).append("\n");
                        successMsg.append("🕐 Timestamp: ").append(java.time.LocalDateTime.now()).append("\n\n");
                        successMsg.append("You can now transfer this file to another PC or location.\n");
                        successMsg.append("To restore, use the Restore option and select this file.");

                        JTextArea resultArea = new JTextArea(successMsg.toString());
                        resultArea.setEditable(false);
                        resultArea.setLineWrap(true);
                        resultArea.setWrapStyleWord(true);
                        resultArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
                        JScrollPane scrollPane = new JScrollPane(resultArea);
                        scrollPane.setPreferredSize(new java.awt.Dimension(600, 250));

                        JOptionPane.showMessageDialog(frame, scrollPane,
                            "✅ Backup Successful", JOptionPane.INFORMATION_MESSAGE);

                        detailsArea.setText(successMsg.toString());
                    } else {
                        JOptionPane.showMessageDialog(frame,
                            "❌ Failed to create backup.\nCheck file path permissions.",
                            "Backup Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // RESTORE FROM BACKUP
                    java.nio.file.Path backupPath = java.nio.file.Paths.get(filePath);
                    if (!java.nio.file.Files.exists(backupPath)) {
                        JOptionPane.showMessageDialog(frame,
                            "❌ Backup file not found: " + filePath,
                            "File Not Found", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean append = appendRb.isSelected();

                    // Confirmation dialog before restore
                    String confirmMsg = append ?
                        "Restore will ADD events from backup to your existing events.\n\nContinue?" :
                        "⚠️  IMPORTANT: Restore will DELETE all current events and replace with backup.\n\nThis action cannot be undone!\n\nContinue?";

                    int confirmRes = JOptionPane.showConfirmDialog(frame, confirmMsg,
                        "Confirm Restore", JOptionPane.YES_NO_OPTION,
                        append ? JOptionPane.QUESTION_MESSAGE : JOptionPane.WARNING_MESSAGE);

                    if (confirmRes == JOptionPane.YES_OPTION) {
                        if (eventManager.restoreFromBackup(filePath, append)) {
                            loadMonth(); // Refresh calendar display

                            long fileSize = java.nio.file.Files.size(backupPath);
                            int eventCount = eventManager.getAllEvents().size();

                            StringBuilder successMsg = new StringBuilder();
                            successMsg.append("✅ RESTORE COMPLETED SUCCESSFULLY\n\n");
                            successMsg.append("📁 Source: ").append(backupPath.toAbsolutePath()).append("\n");
                            successMsg.append("📊 Total Events Now: ").append(eventCount).append("\n");
                            successMsg.append("🔄 Mode: ").append(append ? "APPENDED" : "REPLACED").append("\n");
                            successMsg.append("🕐 Restored: ").append(java.time.LocalDateTime.now()).append("\n\n");
                            successMsg.append("Your calendar has been updated with events from the backup.");

                            JTextArea resultArea = new JTextArea(successMsg.toString());
                            resultArea.setEditable(false);
                            resultArea.setLineWrap(true);
                            resultArea.setWrapStyleWord(true);
                            resultArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
                            JScrollPane scrollPane = new JScrollPane(resultArea);
                            scrollPane.setPreferredSize(new java.awt.Dimension(600, 250));

                            JOptionPane.showMessageDialog(frame, scrollPane,
                                "✅ Restore Successful", JOptionPane.INFORMATION_MESSAGE);

                            detailsArea.setText(successMsg.toString());
                        } else {
                            JOptionPane.showMessageDialog(frame,
                                "❌ Failed to restore from backup.\nThe file may be corrupted or in an invalid format.",
                                "Restore Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                    "❌ Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DayButtonListener implements ActionListener {
        private final LocalDate date;

        DayButtonListener(LocalDate date) {
            this.date = date;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Build formatted event text
            StringBuilder sb = new StringBuilder();
            sb.append("📅 EVENTS FOR ").append(date.getDayOfWeek()).append(", ").append(date).append("\n");
            sb.append("=".repeat(60)).append("\n\n");
            List<Event> evs = eventManager.searchEventsByDate(date);
            if (evs.isEmpty()) {
                sb.append("No events scheduled for this day.\n");
            } else {
                for (Event ev : evs) {
                    sb.append("┌─ EVENT ID: ").append(ev.getEventId()).append("\n");
                    sb.append("├─ 📌 Title: ").append(ev.getTitle()).append("\n");
                    sb.append("├─ ⏰ Time: ").append(ev.getStartDateTime().toLocalTime())
                            .append(" - ").append(ev.getEndDateTime().toLocalTime()).append("\n");
                    if (ev.getDescription() != null && !ev.getDescription().isEmpty()) {
                        sb.append("├─ 📝 Description: ").append(ev.getDescription()).append("\n");
                    }
                    // Duration
                    java.time.Duration duration = java.time.Duration.between(ev.getStartDateTime(), ev.getEndDateTime());
                    long minutes = duration.toMinutes();
                    long hours = minutes / 60;
                    minutes = minutes % 60;
                    sb.append("└─ Duration: ");
                    if (hours > 0) sb.append(hours).append("h ");
                    sb.append(minutes).append("m\n\n");
                }
            }

            // Update bottom details area
            detailsArea.setText(sb.toString());

            // Also show a scrollable dialog to ensure user sees titles/descriptions immediately
            JTextArea dialogArea = new JTextArea(sb.toString());
            dialogArea.setEditable(false);
            dialogArea.setLineWrap(true);
            dialogArea.setWrapStyleWord(true);
            dialogArea.setCaretPosition(0);
            JScrollPane dialogScroll = new JScrollPane(dialogArea);
            dialogScroll.setPreferredSize(new Dimension(600, 300));

            JOptionPane.showMessageDialog(frame, dialogScroll, "Events for " + date, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void displaySearchResults(List<Event> results, String searchType) {
        StringBuilder sb = new StringBuilder("Search Results (" + searchType + "):\n");
        sb.append("=".repeat(50)).append("\n\n");
        if (results == null || results.isEmpty()) {
            sb.append("No events found.");
        } else {
            results.stream().sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                    .forEach(ev -> {
                        sb.append("ID: " + ev.getEventId() + "\n");
                        sb.append("Date: " + ev.getStartDateTime().toLocalDate() + "\n");
                        sb.append("Time: " + ev.getStartDateTime().toLocalTime() + "\n");
                        sb.append("Title: " + ev.getTitle() + "\n");
                        if (!ev.getDescription().isEmpty()) {
                            sb.append("Description: " + ev.getDescription() + "\n");
                        }
                        sb.append("\n");
                    });
        }

        // Display in the bottom panel
        detailsArea.setText(sb.toString());

        // Also show in a pop-up dialog so user sees results immediately
        JTextArea resultArea = new JTextArea(sb.toString());
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(frame, scrollPane,
                "Search Results (" + searchType + ") - Found " + (results == null ? 0 : results.size()) + " events",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showListViewDialog() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1);
        JTextField startField = new JTextField(today.toString(), 15);
        JTextField endField = new JTextField(nextMonth.toString(), 15);
        p.add(new JLabel("Start (yyyy-MM-dd):"));
        p.add(startField);
        p.add(new JLabel("End (yyyy-MM-dd):"));
        p.add(endField);

        int res = JOptionPane.showConfirmDialog(frame, p, "List View Range", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String startInput = startField.getText().trim();
                String endInput = endField.getText().trim();

                if (startInput.isEmpty()) startInput = today.toString();
                if (endInput.isEmpty()) endInput = nextMonth.toString();

                LocalDate start = LocalDate.parse(startInput);
                LocalDate end = LocalDate.parse(endInput);
                if (end.isBefore(start)) throw new IllegalArgumentException("End date cannot be before start date");

                // Use searchEventsByDateRange which handles overlapping/multi-day events
                List<Event> filteredEvents = eventManager.searchEventsByDateRange(start, end).stream()
                        .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                        .collect(java.util.stream.Collectors.toList());

                // Build result text
                StringBuilder sb = new StringBuilder("Events from " + start + " to " + end + ":\n");
                sb.append("=".repeat(60)).append("\n\n");

                if (filteredEvents.isEmpty()) {
                    sb.append("No events found in this date range.\n");
                } else {
                    sb.append("Total: " + filteredEvents.size() + " events\n\n");
                    filteredEvents.forEach(ev -> {
                        sb.append("┌─ ID: " + ev.getEventId() + "\n");
                        sb.append("├─ Date: " + ev.getStartDateTime().toLocalDate().format(DATE_FMT) + "\n");
                        sb.append("├─ Time: " + ev.getStartDateTime().toLocalTime() + " - " + ev.getEndDateTime().toLocalTime() + "\n");
                        sb.append("├─ Title: " + ev.getTitle() + "\n");
                        if (!ev.getDescription().isEmpty()) {
                            sb.append("├─ Description: " + ev.getDescription() + "\n");
                        }
                        sb.append("└─\n\n");
                    });
                }

                // Display in bottom panel
                detailsArea.setText(sb.toString());

                // Also show in a separate "screen" (JFrame) like calendar view
                JTextArea resultArea = new JTextArea(sb.toString());
                resultArea.setEditable(false);
                resultArea.setLineWrap(true);
                resultArea.setWrapStyleWord(true);
                resultArea.setCaretPosition(0);
                JScrollPane scrollPane = new JScrollPane(resultArea);

                // CHANGED: use JFrame (JDialog has no setExtendedState)
                JFrame listFrame = new JFrame(
                        "List View: " + start + " to " + end + " (Found " + filteredEvents.size() + " events)"
                );
                listFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                listFrame.setLayout(new BorderLayout());
                listFrame.add(scrollPane, BorderLayout.CENTER);

                listFrame.setSize(1200, 800);
                listFrame.setLocationRelativeTo(frame);
                listFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // full-screen behavior
                listFrame.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showWeekViewDialog() {
        JPanel p = new JPanel(new FlowLayout());
        LocalDate today = LocalDate.now();
        // Get Monday of current week
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() % 7);
        JTextField dateField = new JTextField(weekStart.toString(), 15);
        p.add(new JLabel("Date in week (yyyy-MM-dd):"));
        p.add(dateField);

        int res = JOptionPane.showConfirmDialog(frame, p, "Week View", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String input = dateField.getText().trim();
                if (input.isEmpty()) {
                    input = weekStart.toString();
                }
                LocalDate parsedDate = LocalDate.parse(input);

                // Calculate Monday of the week containing parsedDate
                // DayOfWeek: MONDAY=1, TUESDAY=2, ..., SUNDAY=7
                LocalDate mondayOfWeek = parsedDate.minusDays(parsedDate.getDayOfWeek().getValue() - 1);

                // Build formatted week view in CLI format
                StringBuilder sb = new StringBuilder();
                sb.append("=== Week of ").append(mondayOfWeek).append(" ===\n\n");

                for (int i = 0; i < 7; i++) {
                    LocalDate currentDate = mondayOfWeek.plusDays(i);
                    List<Event> dayEvents = eventManager.searchEventsByDate(currentDate);

                    // Format: Sun 05, Mon 06, etc.
                    String dayName = currentDate.getDayOfWeek().toString().substring(0, 3);  // Sun, Mon, Tue, etc.
                    String dayNum = String.format("%02d", currentDate.getDayOfMonth());

                    sb.append(dayName).append(" ").append(dayNum).append(": ");

                    if (dayEvents.isEmpty()) {
                        sb.append("No events");
                    } else {
                        // Format multiple events on same day
                        for (int j = 0; j < dayEvents.size(); j++) {
                            Event ev = dayEvents.get(j);
                            if (j > 0) {
                                sb.append("\n").append("          ");  // Indent continuation lines
                            }
                            sb.append(ev.getTitle());
                            sb.append(" (").append(ev.getStartDateTime().toLocalTime()).append(")");
                            if (!ev.getDescription().isEmpty()) {
                                sb.append(" - ").append(ev.getDescription());
                            }
                        }
                    }
                    sb.append("\n");
                }

                String weekViewText = sb.toString();

                // Display in bottom panel
                detailsArea.setText(weekViewText);

                // Also show in pop-up dialog for immediate visibility
                JTextArea resultArea = new JTextArea(weekViewText);
                resultArea.setEditable(false);
                resultArea.setLineWrap(true);
                resultArea.setWrapStyleWord(true);
                resultArea.setCaretPosition(0);
                JScrollPane scrollPane = new JScrollPane(resultArea);
                scrollPane.setPreferredSize(new Dimension(600, 300));

                JOptionPane.showMessageDialog(frame, scrollPane,
                        "Week View: " + mondayOfWeek,
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDayViewDialog() {
        JPanel p = new JPanel(new FlowLayout());
        LocalDate today = LocalDate.now();
        JTextField dateField = new JTextField(today.toString(), 15);
        p.add(new JLabel("Date (yyyy-MM-dd):"));
        p.add(dateField);

        int res = JOptionPane.showConfirmDialog(frame, p, "Day View", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String input = dateField.getText().trim();
                if (input.isEmpty()) {
                    input = today.toString();
                }
                LocalDate date = LocalDate.parse(input);
                List<Event> dayEvents = eventManager.searchEventsByDate(date);

                StringBuilder sb = new StringBuilder();
                sb.append(date.getDayOfWeek()).append(", ").append(date).append("\n");
                sb.append("=".repeat(50)).append("\n\n");

                if (dayEvents.isEmpty()) {
                    sb.append("No events for this day.");
                } else {
                    dayEvents.forEach(ev -> {
                        sb.append("ID: ").append(ev.getEventId()).append("\n");
                        sb.append("Title: ").append(ev.getTitle()).append("\n");
                        sb.append("Time: ").append(ev.getStartDateTime().toLocalTime())
                                .append(" - ").append(ev.getEndDateTime().toLocalTime()).append("\n");
                        if (!ev.getDescription().isEmpty()) {
                            sb.append("Description: ").append(ev.getDescription()).append("\n");
                        }
                        sb.append("\n");
                    });
                }
                detailsArea.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRemindersDialog() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Preset reminder options
        JComboBox<String> reminderTypeBox = new JComboBox<>(new String[]{
                "15 minutes before",
                "30 minutes before",
                "1 hour before",
                "3 hours before",
                "1 day before",
                "Custom Hours",
                "Custom Days",
                "Custom Minutes",
                "Custom Months"
        });
        reminderTypeBox.setSelectedIndex(1); // Default: 30 minutes

        JSpinner minutesSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(30, 1, 1440, 5));
        JSpinner hoursSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 72, 1));
        JSpinner daysSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 365, 1));
        JSpinner monthsSpinner = new JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 12, 1));

        p.add(new JLabel("Reminder Type:"));
        p.add(reminderTypeBox);
        p.add(new JLabel("Custom Hours:"));
        p.add(hoursSpinner);
        p.add(new JLabel("Custom Days:"));
        p.add(daysSpinner);
        p.add(new JLabel("Custom Minutes:"));
        p.add(minutesSpinner);
        p.add(new JLabel("Custom Months:"));
        p.add(monthsSpinner);

        int res = JOptionPane.showConfirmDialog(frame, p, "Reminders Settings", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String selectedType = (String) reminderTypeBox.getSelectedItem();
                int minutes = 30; // default

                // Parse selected reminder type
                if (selectedType.contains("15 minutes")) {
                    minutes = 15;
                } else if (selectedType.contains("30 minutes")) {
                    minutes = 30;
                } else if (selectedType.contains("1 hour")) {
                    minutes = 60;
                } else if (selectedType.contains("3 hours")) {
                    minutes = 180;
                } else if (selectedType.contains("1 day")) {
                    minutes = 1440;
                } else if (selectedType.contains("Custom Hours")) {
                    int hours = (Integer) hoursSpinner.getValue();
                    minutes = hours * 60;
                } else if (selectedType.contains("Custom Days")) {
                    int days = (Integer) daysSpinner.getValue();
                    minutes = days * 1440;
                } else if (selectedType.contains("Custom Minutes")) {
                    minutes = (Integer) minutesSpinner.getValue();
                } else if (selectedType.contains("Custom Months")) {
                    int months = (Integer) monthsSpinner.getValue();
                    minutes = months * 43200; // 30 days per month approximation
                }

                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.time.LocalDateTime endTime = now.plusMinutes(minutes);

                List<Event> upcomingEvents = eventManager.getAllEvents().stream()
                        .filter(e -> {
                            java.time.LocalDateTime eventTime = e.getStartDateTime();
                            return eventTime.isAfter(now) && eventTime.isBefore(endTime);
                        })
                        .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                        .collect(java.util.stream.Collectors.toList());

                // Build notification display
                StringBuilder sb = new StringBuilder();
                sb.append("🔔 REMINDERS - Events in next ").append(formatMinutesToTime(minutes)).append("\n");
                sb.append("=".repeat(60)).append("\n\n");

                if (upcomingEvents.isEmpty()) {
                    sb.append("✓ No upcoming events in this timeframe.\n");
                } else {
                    sb.append("Total: ").append(upcomingEvents.size()).append(" event(s)\n\n");
                    upcomingEvents.forEach(ev -> {
                        java.time.Duration duration = java.time.Duration.between(now, ev.getStartDateTime());
                        String timeUntil = formatDuration(duration);
                        sb.append("┌─ 📌 ").append(ev.getTitle()).append("\n");
                        sb.append("├─ 🕐 Time: ").append(ev.getStartDateTime().toLocalTime()).append("\n");
                        sb.append("├─ 📅 Date: ").append(ev.getStartDateTime().toLocalDate()).append("\n");
                        sb.append("├─ ⏱️  Starts in: ").append(timeUntil).append("\n");
                        if (!ev.getDescription().isEmpty()) {
                            sb.append("├─ 📝 Details: ").append(ev.getDescription()).append("\n");
                        }
                        sb.append("└─ ID: ").append(ev.getEventId()).append("\n\n");
                    });
                }

                detailsArea.setText(sb.toString());

                // Show reminder notification dialog
                if (!upcomingEvents.isEmpty()) {
                    JTextArea resultArea = new JTextArea(sb.toString());
                    resultArea.setEditable(false);
                    resultArea.setLineWrap(true);
                    resultArea.setWrapStyleWord(true);
                    resultArea.setCaretPosition(0);
                    JScrollPane scrollPane = new JScrollPane(resultArea);
                    scrollPane.setPreferredSize(new Dimension(600, 350));

                    JOptionPane.showMessageDialog(frame, scrollPane,
                            "🔔 Reminders: " + upcomingEvents.size() + " event(s) in next " + formatMinutesToTime(minutes),
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "✓ No reminders needed.\nNo events scheduled in the next " + formatMinutesToTime(minutes) + ".",
                            "Reminders",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Helper method to format minutes to human-readable time
     */
    private String formatMinutesToTime(int minutes) {
        if (minutes < 60) {
            return minutes + " minute(s)";
        } else if (minutes < 1440) {
            int hours = minutes / 60;
            return hours + " hour(s)";
        } else {
            int days = minutes / 1440;
            return days + " day(s)";
        }
    }

    /**
     * Helper method to format duration
     */
    private String formatDuration(java.time.Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long mins = duration.toMinutes() % 60;

        StringBuilder sb = new StringBuilder();

        if (days > 0) {
            sb.append(days).append(" day");
            if (days > 1) sb.append("s");
            if (hours > 0 || mins > 0) sb.append(" ");
        }

        if (hours > 0) {
            sb.append(hours).append(" hour");
            if (hours > 1) sb.append("s");
            if (mins > 0) sb.append(" ");
        }

        if (mins > 0 || (days == 0 && hours == 0)) {
            sb.append(mins).append(" minute");
            if (mins != 1) sb.append("s");
        }

        return sb.toString();
    }

    private void loadMonth() {
        monthGrid.removeAll();
        Integer sel = (Integer) monthBox.getSelectedItem();
        int month = (sel == null) ? LocalDate.now().getMonthValue() : sel;
        int year;
        try {
            year = Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid year", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        YearMonth ym = YearMonth.of(year, month);
        Color monthColor = getMonthColor(month);
        monthGrid.setBackground(monthColor);

        // Update title with month and year
        titleLabel.setText(ym.getMonth().toString() + " " + year);

        // Weekday headers
        String[] days = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
        for (String d : days) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            lbl.setForeground(new Color(0x8688AD)); // Muted purple-grey text
            lbl.setBackground(monthColor);
            lbl.setOpaque(true);
            lbl.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x758BED), 2));
            monthGrid.add(lbl);
        }

        // Determine starting offset
        int firstDayOffset = ym.atDay(1).getDayOfWeek().getValue() % 7; // Sunday=0
        for (int i = 0; i < firstDayOffset; i++) {
            JLabel emptyLbl = new JLabel("");
            emptyLbl.setBackground(monthColor);
            emptyLbl.setOpaque(true);
            emptyLbl.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x758BED), 2));
            monthGrid.add(emptyLbl);
        }

        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate current = ym.atDay(day);
            // Use EventManager.searchEventsByDate which checks for event intervals overlapping the date
            List<Event> dayEvents = eventManager.searchEventsByDate(current);

            boolean hasEvents = !dayEvents.isEmpty();
            JButton dayBtn = new JButton(String.valueOf(day) + (hasEvents ? " •" : ""));
            dayBtn.setBackground(monthColor);
            dayBtn.setForeground(hasEvents ? new Color(0x758BED) : new Color(0x8688AD)); // Blue for event days, dark gray for regular
            dayBtn.setOpaque(true);
            dayBtn.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x758BED), 2));
            dayBtn.setFont(dayBtn.getFont().deriveFont(14f));

            // Set tooltip showing event details when hovering over a day with events
            if (hasEvents) {
                StringBuilder tooltip = new StringBuilder("<html>");
                dayEvents.forEach(ev -> {
                    tooltip.append("<b>").append(ev.getTitle()).append("</b><br>");
                    tooltip.append(ev.getStartDateTime().toLocalTime()).append(" - ").append(ev.getEndDateTime().toLocalTime());
                    if (!ev.getDescription().isEmpty()) {
                        tooltip.append("<br><i>").append(ev.getDescription()).append("</i>");
                    }
                    tooltip.append("<br><br>");
                });
                tooltip.append("</html>");
                dayBtn.setToolTipText(tooltip.toString());
            }

            dayBtn.addActionListener(new DayButtonListener(current));
            monthGrid.add(dayBtn);
        }

        // Fill trailing blanks to keep grid neat
        int totalCells = firstDayOffset + ym.lengthOfMonth();
        int trailing = (7 - (totalCells % 7)) % 7;
        for (int i = 0; i < trailing; i++) {
            JLabel emptyLbl = new JLabel("");
            emptyLbl.setBackground(monthColor);
            emptyLbl.setOpaque(true);
            emptyLbl.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0x758BED), 2));
            monthGrid.add(emptyLbl);
        }

        monthGrid.revalidate();
        monthGrid.repaint();

        // Display all events for the month in the details area
        detailsArea.setText("📅 EVENTS FOR " + ym.getMonth().toString() + " " + year + "\n");
        detailsArea.append("=".repeat(60) + "\n\n");

        // For month overview, include events that overlap the month range (handles multi-day events)
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();
        List<Event> monthEvents = eventManager.searchEventsByDateRange(monthStart, monthEnd).stream()
                .sorted((e1, e2) -> e1.getStartDateTime().compareTo(e2.getStartDateTime()))
                .collect(java.util.stream.Collectors.toList());

        if (monthEvents.isEmpty()) {
            detailsArea.append("No events scheduled for this month.\n");
        } else {
            detailsArea.append("Total Events: " + monthEvents.size() + "\n\n");
            for (Event ev : monthEvents) {
                detailsArea.append("┌─ 📌 " + ev.getTitle() + " (ID: " + ev.getEventId() + ")\n");
                detailsArea.append("├─ 📅 Date: " + ev.getStartDateTime().toLocalDate().format(DATE_FMT) + "\n");
                detailsArea.append("├─ ⏰ Time: " + ev.getStartDateTime().toLocalTime()
                        + " - " + ev.getEndDateTime().toLocalTime() + "\n");
                if (!ev.getDescription().isEmpty()) {
                    detailsArea.append("├─ 📝 Description: " + ev.getDescription() + "\n");
                }
                detailsArea.append("└─ Click on day number to see full details\n\n");
            }
        }
    }

    private Color getMonthColor(int month) {
        switch (month) {
            case 1:
                return new Color(0xFBECC6); // January
            case 2:
                return new Color(0xFEE1E8); // February
            case 3:
                return new Color(0xFFFFDB); // March
            case 4:
                return new Color(0xDBE6DB); // April
            case 5:
                return new Color(0xF0ECEC); // May
            case 6:
                return new Color(0xBEC6EB); // June
            case 7:
                return new Color(0xCDDEEF); // July
            case 8:
                return new Color(0xFFC9CC); // August
            case 9:
                return new Color(0xFCF2F3); // September
            case 10:
                return new Color(0xF3DBBE); // October
            case 11:
                return new Color(0xF3B0C3); // November
            case 12:
                return new Color(0xECD5E3); // December
            default:
                return Color.WHITE;
        }
    }

    private void showStatisticsDialog() {
        StringBuilder sb = new StringBuilder();

        List<Event> allEvents = eventManager.getAllEvents();

        if (allEvents.isEmpty()) {
            detailsArea.setText("📊 No events to analyze.");
            JOptionPane.showMessageDialog(frame,
                    "No events yet. Add events to see statistics.",
                    "Statistics",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // BASIC STATISTICS
        sb.append("╔════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    📊 COMPREHENSIVE STATISTICS                    ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════════╝\n\n");

        sb.append("📌 BASIC STATISTICS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("Total Events: ").append(allEvents.size()).append("\n");

        LocalDateTime now = LocalDateTime.now();
        long pastEvents = allEvents.stream().filter(e -> e.getEndDateTime().isBefore(now)).count();
        long upcomingEvents = allEvents.stream().filter(e -> e.getStartDateTime().isAfter(now)).count();
        long ongoingEvents = allEvents.stream().filter(e -> e.getStartDateTime().isBefore(now) && e.getEndDateTime().isAfter(now)).count();

        sb.append("  ✓ Completed: ").append(pastEvents).append("\n");
        sb.append("  → Ongoing: ").append(ongoingEvents).append("\n");
        sb.append("  ◇ Upcoming: ").append(upcomingEvents).append("\n\n");

        // BUSIEST DAY OF WEEK
        sb.append("📅 BUSIEST DAY OF WEEK\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        java.util.Map<java.time.DayOfWeek, Long> eventsByDay = allEvents.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.getStartDateTime().getDayOfWeek(),
                        java.util.stream.Collectors.counting()
                ));

        java.time.DayOfWeek busiestDay = eventsByDay.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(null);

        if (busiestDay != null) {
            sb.append("Busiest Day: ").append(busiestDay).append(" (").append(eventsByDay.get(busiestDay)).append(" events)\n\n");
            for (java.time.DayOfWeek day : java.time.DayOfWeek.values()) {
                long count = eventsByDay.getOrDefault(day, 0L);
                String bar = "■".repeat((int) (count * 3));
                sb.append(String.format("  %-10s: %s (%d)%n", day, bar, count));
            }
        }
        sb.append("\n");

        // PEAK HOURS ANALYSIS
        sb.append("🕐 PEAK HOURS ANALYSIS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        java.util.Map<Integer, Long> eventsByHour = allEvents.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.getStartDateTime().getHour(),
                        java.util.stream.Collectors.counting()
                ));

        Integer peakHour = eventsByHour.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(null);

        if (peakHour != null) {
            sb.append(String.format("⬆️  Peak Hour: %02d:00 (%d events)%n", peakHour, eventsByHour.get(peakHour)));
            for (int hour = 0; hour < 24; hour++) {
                long count = eventsByHour.getOrDefault(hour, 0L);
                if (count > 0) {
                    String bar = "█".repeat((int) Math.min(count * 2, 40));
                    sb.append(String.format("  %02d:00 │ %s (%d)%n", hour, bar, count));
                }
            }
        }
        sb.append("\n");

        // DURATION ANALYSIS
        sb.append("⏱️  EVENT DURATION ANALYSIS\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        java.util.List<Double> durationsInMinutes = allEvents.stream()
                .map(e -> (double) java.time.Duration.between(e.getStartDateTime(), e.getEndDateTime()).toMinutes())
                .collect(java.util.stream.Collectors.toList());

        if (!durationsInMinutes.isEmpty()) {
            double avgDuration = durationsInMinutes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            double totalDuration = durationsInMinutes.stream().mapToDouble(Double::doubleValue).sum();
            double minDuration = durationsInMinutes.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double maxDuration = durationsInMinutes.stream().mapToDouble(Double::doubleValue).max().orElse(0);

            sb.append(String.format("Average Duration: %.1f min (%.1f hours)%n", avgDuration, avgDuration / 60));
            sb.append(String.format("Total Duration: %.1f hours (%.1f days)%n", totalDuration / 60, totalDuration / 1440));
            sb.append(String.format("Shortest Event: %.0f min%n", minDuration));
            sb.append(String.format("Longest Event: %.0f min%n\n", maxDuration));

            long quickEvents = durationsInMinutes.stream().filter(d -> d < 30).count();
            long mediumEvents = durationsInMinutes.stream().filter(d -> d >= 30 && d < 120).count();
            long longEvents = durationsInMinutes.stream().filter(d -> d >= 120).count();

            sb.append("Duration Distribution:\n");
            sb.append(String.format("  Quick (< 30 min): %d (%.1f%%)%n", quickEvents, (quickEvents * 100.0) / allEvents.size()));
            sb.append(String.format("  Standard (30-120 min): %d (%.1f%%)%n", mediumEvents, (mediumEvents * 100.0) / allEvents.size()));
            sb.append(String.format("  Long (> 120 min): %d (%.1f%%)%n\n", longEvents, (longEvents * 100.0) / allEvents.size()));
        }

        // TIME OF DAY DISTRIBUTION
        sb.append("⏰ TIME OF DAY DISTRIBUTION\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        long morningEvents = allEvents.stream().filter(e -> e.getStartDateTime().getHour() >= 6 && e.getStartDateTime().getHour() < 12).count();
        long afternoonEvents = allEvents.stream().filter(e -> e.getStartDateTime().getHour() >= 12 && e.getStartDateTime().getHour() < 18).count();
        long eveningEvents = allEvents.stream().filter(e -> e.getStartDateTime().getHour() >= 18 && e.getStartDateTime().getHour() < 24).count();
        long nightEvents = allEvents.stream().filter(e -> e.getStartDateTime().getHour() >= 0 && e.getStartDateTime().getHour() < 6).count();

        sb.append(String.format("🌅 Morning (6AM-12PM): %d (%.1f%%)%n", morningEvents, (morningEvents * 100.0) / allEvents.size()));
        sb.append(String.format("☀️  Afternoon (12PM-6PM): %d (%.1f%%)%n", afternoonEvents, (afternoonEvents * 100.0) / allEvents.size()));
        sb.append(String.format("🌆 Evening (6PM-12AM): %d (%.1f%%)%n", eveningEvents, (eveningEvents * 100.0) / allEvents.size()));
        sb.append(String.format("🌙 Night (12AM-6AM): %d (%.1f%%)%n\n", nightEvents, (nightEvents * 100.0) / allEvents.size()));

        // BUSIEST MONTHS
        sb.append("📊 EVENTS BY MONTH\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        java.util.Map<String, Long> eventsByMonth = allEvents.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.getStartDateTime().toLocalDate().getMonth().toString() + " " + e.getStartDateTime().getYear(),
                        java.util.stream.Collectors.counting()
                ));

        eventsByMonth.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(e -> sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append(" events\n"));
        sb.append("\n");

        // BUSIEST DATES
        sb.append("🔥 BUSIEST DATES (Top 5)\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        java.util.Map<LocalDate, Long> eventsByDate = allEvents.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        e -> e.getStartDateTime().toLocalDate(),
                        java.util.stream.Collectors.counting()
                ));

        eventsByDate.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(e -> sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append(" events\n"));
        sb.append("\n");

        // SCHEDULE EFFICIENCY
        sb.append("⚡ SCHEDULE EFFICIENCY\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        double avgEventsPerDay = (double) allEvents.size() / (eventsByDate.size() > 0 ? eventsByDate.size() : 1);
        sb.append(String.format("Average Events per Day: %.1f%n", avgEventsPerDay));
        sb.append(String.format("Total Days with Events: %d%n\n", eventsByDate.size()));

        if (avgEventsPerDay > 8) {
            sb.append("⚠️  VERY HEAVY SCHEDULE\n");
            sb.append("  Recommendation: Consider rescheduling or delegating tasks.\n");
        } else if (avgEventsPerDay > 5) {
            sb.append("✓ BUSY SCHEDULE\n");
            sb.append("  Recommendation: Ensure adequate breaks between events.\n");
        } else if (avgEventsPerDay > 2) {
            sb.append("✓ BALANCED SCHEDULE\n");
            sb.append("  Recommendation: Well-managed calendar.\n");
        } else {
            sb.append("✓ LIGHT SCHEDULE\n");
            sb.append("  Recommendation: Good opportunity for focused work.\n");
        }

        detailsArea.setText(sb.toString());

        // Show in dialog too
        JTextArea resultArea = new JTextArea(sb.toString());
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setCaretPosition(0);
        resultArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(750, 500));

        JOptionPane.showMessageDialog(frame, scrollPane,
                "📊 Comprehensive Calendar Statistics",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCalendarViewDialog() {
        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        LocalDate today = LocalDate.now();

        // View type selection
        JComboBox<String> viewTypeBox = new JComboBox<>(new String[]{"Month View", "Week View"});
        viewTypeBox.setSelectedIndex(0);

        // Month selection
        JComboBox<Integer> monthBox = new JComboBox<>();
        for (int m = 1; m <= 12; m++) monthBox.addItem(m);
        monthBox.setSelectedIndex(today.getMonthValue() - 1);

        // Year field
        JTextField yearField = new JTextField(String.valueOf(today.getYear()), 10);

        // Date field for week view
        JTextField dateField = new JTextField(today.toString(), 15);

        p.add(new JLabel("View Type:"));
        p.add(viewTypeBox);
        p.add(new JLabel("Month:"));
        p.add(monthBox);
        p.add(new JLabel("Year:"));
        p.add(yearField);
        p.add(new JLabel("Date (for week):"));
        p.add(dateField);

        int res = JOptionPane.showConfirmDialog(frame, p, "Calendar View", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String viewType = (String) viewTypeBox.getSelectedItem();

                if ("Month View".equals(viewType)) {
                    displayMonthView(monthBox, yearField);
                } else if ("Week View".equals(viewType)) {
                    displayWeekViewCalendar(dateField);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayMonthView(JComboBox<Integer> monthBox, JTextField yearField) {
        try {
            int month = (Integer) monthBox.getSelectedItem();
            int year = Integer.parseInt(yearField.getText().trim());
            YearMonth ym = YearMonth.of(year, month);

            // Build CLI format calendar view
            StringBuilder sb = new StringBuilder();

            // Header with month and year
            sb.append("\n").append(ym.getMonth()).append(" ").append(year).append("\n");
            sb.append("Su Mo Tu We Th Fr Sa\n");

            // Get all events for this month
            List<Event> allEvents = eventManager.getAllEvents();
            java.util.Map<LocalDate, List<Event>> eventsByDate = allEvents.stream()
                    .filter(e -> {
                        LocalDate eventDate = e.getStartDateTime().toLocalDate();
                        return eventDate.getMonth() == ym.getMonth() && eventDate.getYear() == ym.getYear();
                    })
                    .collect(java.util.stream.Collectors.groupingBy(e -> e.getStartDateTime().toLocalDate()));

            LocalDate firstDay = ym.atDay(1);
            LocalDate lastDay = ym.atEndOfMonth();
            int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7;  // Sunday = 0

            // Print leading spaces
            for (int i = 0; i < firstDayOfWeek; i++) {
                sb.append("   ");
            }

            // Print days
            for (int day = 1; day <= lastDay.getDayOfMonth(); day++) {
                LocalDate currentDate = ym.atDay(day);
                List<Event> dayEvents = eventsByDate.getOrDefault(currentDate, new java.util.ArrayList<>());

                if (!dayEvents.isEmpty()) {
                    sb.append(String.format("%2d*", day));
                } else {
                    sb.append(String.format("%2d ", day));
                }

                if ((firstDayOfWeek + day) % 7 == 0 || day == lastDay.getDayOfMonth()) {
                    sb.append("\n");
                }
            }

            // Display events for the month
            sb.append("\n");
            java.util.Map<LocalDate, List<Event>> sortedEvents = eventsByDate.entrySet().stream()
                    .sorted(java.util.Map.Entry.comparingByKey())
                    .collect(java.util.stream.Collectors.toMap(
                            java.util.Map.Entry::getKey,
                            java.util.Map.Entry::getValue,
                            (e1, e2) -> e1,
                            java.util.LinkedHashMap::new));

            for (java.util.Map.Entry<LocalDate, List<Event>> entry : sortedEvents.entrySet()) {
                sb.append("* ").append(entry.getKey()).append(":\n");
                for (Event ev : entry.getValue()) {
                    sb.append("  ").append(ev.getStartDateTime().toLocalTime())
                            .append(" - ").append(ev.getTitle());
                    if (!ev.getDescription().isEmpty()) {
                        sb.append(" (").append(ev.getDescription()).append(")");
                    }
                    sb.append("\n");
                }
            }

            String calendarViewText = sb.toString();

            // Display in bottom panel
            detailsArea.setText(calendarViewText);

            // Also show in pop-up dialog
            JTextArea resultArea = new JTextArea(calendarViewText);
            resultArea.setEditable(false);
            resultArea.setLineWrap(true);
            resultArea.setWrapStyleWord(true);
            resultArea.setCaretPosition(0);
            resultArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(resultArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(frame, scrollPane,
                    "Calendar View: Month - " + ym,
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayWeekViewCalendar(JTextField dateField) {
        try {
            String input = dateField.getText().trim();
            if (input.isEmpty()) {
                input = LocalDate.now().toString();
            }
            LocalDate parsedDate = LocalDate.parse(input);

            // Calculate Monday of the week containing parsedDate
            LocalDate mondayOfWeek = parsedDate.minusDays(parsedDate.getDayOfWeek().getValue() - 1);

            // Build week calendar grid in CLI format
            StringBuilder sb = new StringBuilder();
            sb.append("=== Week of ").append(mondayOfWeek).append(" ===\n");
            sb.append("Su Mo Tu We Th Fr Sa\n");

            // Get all events for this week
            List<Event> allEvents = eventManager.getAllEvents();
            java.util.Map<LocalDate, List<Event>> eventsByDate = allEvents.stream()
                    .collect(java.util.stream.Collectors.groupingBy(e -> e.getStartDateTime().toLocalDate()));

            // Display 7 days starting from Sunday
            LocalDate sundayOfWeek = mondayOfWeek.minusDays(1);

            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = sundayOfWeek.plusDays(i);
                List<Event> dayEvents = eventsByDate.getOrDefault(currentDate, new java.util.ArrayList<>());

                if (!dayEvents.isEmpty()) {
                    sb.append(String.format("%2d*", currentDate.getDayOfMonth()));
                } else {
                    sb.append(String.format("%2d ", currentDate.getDayOfMonth()));
                }
            }
            sb.append("\n\n");

            // Display events for the week
            sb.append("Events for this week:\n");
            sb.append("=".repeat(40)).append("\n\n");

            boolean hasEvents = false;
            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = sundayOfWeek.plusDays(i);
                List<Event> dayEvents = eventsByDate.getOrDefault(currentDate, new java.util.ArrayList<>());

                if (!dayEvents.isEmpty()) {
                    hasEvents = true;
                    String dayName = currentDate.getDayOfWeek().toString();
                    sb.append(dayName).append(" ").append(currentDate).append(":\n");
                    for (Event ev : dayEvents) {
                        sb.append("  ").append(ev.getStartDateTime().toLocalTime())
                                .append(" - ").append(ev.getTitle());
                        if (!ev.getDescription().isEmpty()) {
                            sb.append(" (").append(ev.getDescription()).append(")");
                        }
                        sb.append("\n");
                    }
                    sb.append("\n");
                }
            }

            if (!hasEvents) {
                sb.append("No events for this week.\n");
            }

            String weekCalendarText = sb.toString();

            // Display in bottom panel
            detailsArea.setText(weekCalendarText);

            // Also show in pop-up dialog
            JTextArea resultArea = new JTextArea(weekCalendarText);
            resultArea.setEditable(false);
            resultArea.setLineWrap(true);
            resultArea.setWrapStyleWord(true);
            resultArea.setCaretPosition(0);
            resultArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(resultArea);
            scrollPane.setPreferredSize(new Dimension(600, 350));

            JOptionPane.showMessageDialog(frame, scrollPane,
                    "Calendar View: Week Calendar - " + mondayOfWeek,
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayWeekCalendarView(JTextField dateField) {
        try {
            String input = dateField.getText().trim();
            if (input.isEmpty()) {
                input = LocalDate.now().toString();
            }
            LocalDate parsedDate = LocalDate.parse(input);

            // Calculate Monday of the week containing parsedDate
            LocalDate mondayOfWeek = parsedDate.minusDays(parsedDate.getDayOfWeek().getValue() - 1);

            // Build week calendar grid in CLI format
            StringBuilder sb = new StringBuilder();
            sb.append("=== Week of ").append(mondayOfWeek).append(" ===\n");
            sb.append("Su Mo Tu We Th Fr Sa\n");

            // Get all events for this week
            List<Event> allEvents = eventManager.getAllEvents();
            java.util.Map<LocalDate, List<Event>> eventsByDate = allEvents.stream()
                    .collect(java.util.stream.Collectors.groupingBy(e -> e.getStartDateTime().toLocalDate()));

            // Display 7 days starting from Sunday
            LocalDate sundayOfWeek = mondayOfWeek.minusDays(1);

            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = sundayOfWeek.plusDays(i);
                List<Event> dayEvents = eventsByDate.getOrDefault(currentDate, new java.util.ArrayList<>());

                if (!dayEvents.isEmpty()) {
                    sb.append(String.format("%2d*", currentDate.getDayOfMonth()));
                } else {
                    sb.append(String.format("%2d ", currentDate.getDayOfMonth()));
                }
            }
            sb.append("\n\n");

            // Display events for the week
            sb.append("Events for this week:\n");
            sb.append("=".repeat(40)).append("\n\n");

            boolean hasEvents = false;
            for (int i = 0; i < 7; i++) {
                LocalDate currentDate = sundayOfWeek.plusDays(i);
                List<Event> dayEvents = eventsByDate.getOrDefault(currentDate, new java.util.ArrayList<>());

                if (!dayEvents.isEmpty()) {
                    hasEvents = true;
                    String dayName = currentDate.getDayOfWeek().toString();
                    sb.append(dayName).append(" ").append(currentDate).append(":\n");
                    for (Event ev : dayEvents) {
                        sb.append("  ").append(ev.getStartDateTime().toLocalTime())
                                .append(" - ").append(ev.getTitle());
                        if (!ev.getDescription().isEmpty()) {
                            sb.append(" (").append(ev.getDescription()).append(")");
                        }
                        sb.append("\n");
                    }
                    sb.append("\n");
                }
            }

            if (!hasEvents) {
                sb.append("No events for this week.\n");
            }

            String weekCalendarText = sb.toString();

            // Display in bottom panel
            detailsArea.setText(weekCalendarText);

            // Also show in pop-up dialog
            JTextArea resultArea = new JTextArea(weekCalendarText);
            resultArea.setEditable(false);
            resultArea.setLineWrap(true);
            resultArea.setWrapStyleWord(true);
            resultArea.setCaretPosition(0);
            resultArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(resultArea);
            scrollPane.setPreferredSize(new Dimension(600, 350));

            JOptionPane.showMessageDialog(frame, scrollPane,
                    "Calendar View: Week Calendar - " + mondayOfWeek,
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigatePreviousMonth() {
        Integer sel = (Integer) monthBox.getSelectedItem();
        int month = (sel == null) ? LocalDate.now().getMonthValue() : sel;
        int year;
        try {
            year = Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException ex) {
            return;
        }
        if (month == 1) {
            // wrap to December previous year
            monthBox.setSelectedIndex(11); // December (0-based index)
            yearField.setText(String.valueOf(year - 1));
        } else {
            // select previous month
            monthBox.setSelectedIndex(month - 2);
        }
        // monthBox action listener will call loadMonth()
    }

    private void navigateNextMonth() {
        Integer sel = (Integer) monthBox.getSelectedItem();
        int month = (sel == null) ? LocalDate.now().getMonthValue() : sel;
        int year;
        try {
            year = Integer.parseInt(yearField.getText().trim());
        } catch (NumberFormatException ex) {
            return;
        }
        if (month == 12) {
            // wrap to January next year
            monthBox.setSelectedIndex(0); // January
            yearField.setText(String.valueOf(year + 1));
        } else {
            // next month (0-based index)
            monthBox.setSelectedIndex(month);
        }
        // monthBox action listener will call loadMonth()
    }

    // Quick launcher
    public static void main(String[] args) {
        EventManager em = new EventManager();
        // Events are now loaded from events.csv file
        // Sample events with current/future dates are already in CSV:
        // - Weekly Standup: Jan 8, 2026 @ 10:00
        // - Project Kickoff: Jan 10, 2026 @ 09:00
        // - Design Review: Jan 15, 2026 @ 14:00
        // - Team Lunch: Jan 20, 2026 @ 12:00
        // - Demo: Jan 25, 2026 @ 16:00
        // - Client Meeting: Jan 22, 2026 @ 11:00
        // - Code Review: Feb 5, 2026 @ 15:00

        // Display startup notifications
        ReminderManager reminderManager = new ReminderManager(em);
        reminderManager.displayStartupNotification();
        reminderManager.displayTodaySchedule();

        SwingUtilities.invokeLater(() -> new CalendarGUI(em));
    }
}

