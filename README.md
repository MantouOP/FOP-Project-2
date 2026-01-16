# JavaFX Calendar Application

[![GitHub Repository](https://img.shields.io/badge/GitHub-FOP--Project--2-blue?style=flat&logo=github)](https://github.com/MantouOP/FOP-Project-2)

**Repository**: [https://github.com/MantouOP/FOP-Project-2](https://github.com/MantouOP/FOP-Project-2)

---

A comprehensive, feature-rich calendar application built with JavaFX that provides event management, recurring events, reminders, statistics, and data backup capabilities.

## 📋 Project Summary

This JavaFX-based calendar application enables users to efficiently manage their events with an intuitive graphical interface. The application supports one-time and recurring events, provides intelligent reminders, offers detailed statistics and analytics, and includes data persistence with backup/restore functionality. All event data is stored in CSV files for easy portability and manual editing if needed.

## ✨ Features

- **Interactive Calendar View**
  - Month-by-month navigation with previous/next month buttons
  - Visual calendar grid displaying days with event indicators
  - Click on any day to view events scheduled for that date
  - Color-coded event dots on calendar cells for quick identification
  
- **Event Management**
  - **Create Events**: Add new events with title, description, start time, and end time
  - **Edit Events**: Modify existing event details
  - **Delete Events**: Remove single events or batch delete multiple events
  - **Event Details**: View comprehensive information for each event
  
- **Recurring Events**
  - Configure events to repeat at regular intervals (daily, weekly, bi-weekly, monthly)
  - Set recurrence by number of occurrences or end date
  - Automatic generation of recurring event instances
  - Independent management of recurring event patterns
  
- **Search & Filter**
  - Search events by keyword (searches titles and descriptions)
  - Filter events by date range
  - View all matching events in an organized list
  
- **Multiple View Modes**
  - **Calendar View**: Traditional month grid view with event indicators
  - **List View**: Chronological list of all events with full details
  - **Day View**: Focused view of all events on a selected date
  
- **Smart Reminders**
  - Automatic reminder popup on application startup
  - Shows upcoming events within the next 30 days
  - Time-remaining calculations for each upcoming event
  - Configurable reminder notifications
  
- **Statistics & Analytics**
  - Total event count and date range coverage
  - Busiest day of the week analysis
  - Peak hours analysis for event scheduling
  - Monthly distribution charts
  - Event duration statistics (average, shortest, longest)
  - Productivity insights and scheduling efficiency metrics
  - Upcoming events summary
  
- **Backup & Restore**
  - **Backup**: Create backup copies of all event data (events.csv and recurrent.csv)
  - **Restore**: Restore events from backup files
  - Custom file chooser for backup location selection
  - Data safety and disaster recovery
  
- **Data Persistence**
  - CSV-based storage for events (`events.csv`) and recurring events (`recurrent.csv`)
  - Automatic data loading on startup
  - Real-time data saving after modifications
  - Human-readable format for easy data inspection
  
- **User Interface**
  - Modern, clean design with custom CSS styling
  - Responsive layout that adapts to window resizing
  - Maximized window on startup for optimal viewing
  - Intuitive button-based navigation
  - Modal dialogs for all major operations

## 🔧 Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 11 or higher
  - Recommended: JDK 17 or JDK 21 (LTS versions)
  - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)

- **JavaFX SDK**: Version 17 or higher
  - Download from: [Gluon JavaFX Downloads](https://gluonhq.com/products/javafx/)
  - Extract the JavaFX SDK to a known location on your system (e.g., `C:\javafx-sdk-21` or `/opt/javafx-sdk-21`)

- **IDE (Optional but Recommended)**: IntelliJ IDEA, Eclipse, or NetBeans
  - IntelliJ IDEA Community Edition is recommended and free

## 📥 Setup & Installation

### 1. Clone or Download the Repository

```bash
git clone https://github.com/MantouOP/FOP-Project-2.git
cd FOP-Project-2
```

Or download the ZIP file from [GitHub](https://github.com/MantouOP/FOP-Project-2) and extract it to your desired location.

### 2. Verify Project Structure

Ensure your project directory contains the following files:

```
FOP-Project-2/
├── MainFX.java
├── Event.java
├── EventManager.java
├── RecurringEvent.java
├── CSVHandler.java
├── ReminderManager.java
├── StatisticsManager.java
├── style.css
├── events.csv
├── recurrent.csv
├── backup.csv
└── README.md
```

### 3. Configure JavaFX Library Path

Note the path where you extracted the JavaFX SDK. You will need the full path to the `lib` directory within the JavaFX SDK:

- **Windows**: `C:\javafx-sdk-21\lib`
- **macOS/Linux**: `/opt/javafx-sdk-21/lib`

## 🚀 How to Run

### ⚠️ Important: JavaFX Configuration Required

This project **requires JavaFX** to run. Since JavaFX is not included in the standard JDK (from version 11 onwards), you must specify the JavaFX modules and library path when running the application.

### Running in IntelliJ IDEA

#### Step 1: Open the Project
1. Open IntelliJ IDEA
2. Select **File → Open** and navigate to the `FOP-Project-2` folder
3. Click **OK** to open the project

#### Step 2: Configure VM Options
1. Locate the `MainFX.java` file in the Project Explorer
2. Right-click on `MainFX.java` and select **Run 'MainFX.main()'** (this will fail, but creates a run configuration)
3. Click on the **Run** menu and select **Edit Configurations...**
4. In the Run/Debug Configurations window, select the **MainFX** configuration
5. Find the **VM options** field (click **Modify options** → **Add VM options** if not visible)
6. Paste the following VM options (replace the path with your actual JavaFX SDK path):

**Windows:**
```
--module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.graphics
```

**macOS/Linux:**
```
--module-path "/opt/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.graphics
```

7. Click **Apply** and then **OK**

#### Step 3: Run the Application
1. Click the green **Run** button or press `Shift + F10`
2. The application window should open with the calendar view

### Running from Command Line

#### Step 1: Compile the Java Files

Navigate to the project directory and compile all Java files:

**Windows (PowerShell):**
```powershell
javac --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.graphics *.java
```

**macOS/Linux (Bash):**
```bash
javac --module-path "/opt/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.graphics *.java
```

#### Step 2: Run the Application

**Windows (PowerShell):**
```powershell
java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.graphics MainFX
```

**macOS/Linux (Bash):**
```bash
java --module-path "/opt/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.graphics MainFX
```

### Understanding the VM Options

The VM options used are:

- `--module-path "/path/to/javafx-sdk/lib"`: Specifies the location of the JavaFX modules
- `--add-modules javafx.controls,javafx.graphics`: Adds the required JavaFX modules to the module path
  - `javafx.controls`: UI controls (Button, Label, TextField, etc.)
  - `javafx.graphics`: Graphics and scene graph (Scene, Stage, shapes, colors, etc.)

### Running in Other IDEs

#### Eclipse
1. Right-click on the project → **Run As** → **Run Configurations**
2. Create a new **Java Application** configuration
3. Set **Main class** to `MainFX`
4. Go to the **Arguments** tab
5. In the **VM arguments** field, paste the VM options (adjust the path):
   ```
   --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.graphics
   ```
6. Click **Apply** and **Run**

#### NetBeans
1. Right-click on the project → **Properties**
2. Go to **Run** category
3. In **VM Options**, paste the VM options (adjust the path):
   ```
   --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.graphics
   ```
4. Click **OK** and run the project

## 📚 Usage Guide

### Adding a New Event
1. Click the **Add Event** button in the top toolbar
2. Fill in the event details:
   - **Title**: Event name (required)
   - **Description**: Additional details (optional)
   - **Start Time**: Format: `yyyy-MM-dd HH:mm` (e.g., `2026-01-20 14:30`)
   - **End Time**: Format: `yyyy-MM-dd HH:mm` (e.g., `2026-01-20 16:00`)
3. Click **Create** to save the event

### Creating a Recurring Event
1. Click the **Recurring Event** button
2. Enter the base event details (title, description, start/end time)
3. Configure recurrence pattern:
   - **Interval**: `1d` (daily), `1w` (weekly), `2w` (bi-weekly), `1m` (monthly)
   - **Occurrences**: Number of times to repeat (or leave as 0)
   - **End Date**: Last date for recurrence (format: `yyyy-MM-dd`)
4. Click **Create Recurring** to generate all event instances

### Searching for Events
1. Click the **Search** button
2. Enter a keyword to search in event titles and descriptions
3. Optionally, specify a date range (start and end dates)
4. Click **Search** to view matching events

### Editing an Event
1. Click the **Edit** button
2. Enter the Event ID of the event you want to modify
3. Update the desired fields
4. Click **Update** to save changes

### Deleting Events
1. Click the **Delete** button
2. Enter one or more Event IDs separated by commas (e.g., `1,3,5`)
3. Confirm the deletion

### Viewing Statistics
1. Click the **Statistics** button
2. Review comprehensive analytics including:
   - Event count and distribution
   - Busiest days and peak hours
   - Duration statistics
   - Productivity insights

### Creating Backups
1. Click the **Backup/Restore** button
2. Click **Create Backup**
3. Choose a location and filename for the backup
4. Both `events.csv` and `recurrent.csv` will be backed up

### Restoring from Backup
1. Click the **Backup/Restore** button
2. Click **Restore from Backup**
3. Select the backup file to restore
4. Confirm the restoration (this will replace current data)

## 📁 Data Files

- **events.csv**: Stores all event data (regular and recurring instances)
- **recurrent.csv**: Stores recurring event patterns and configuration
- **backup.csv**: Default backup file (custom names can be specified)

### CSV File Format

**events.csv format:**
```
eventId,title,description,startDateTime,endDateTime
1,Team Meeting,Discuss Q1 goals,2026-01-20 10:00,2026-01-20 11:00
```

**recurrent.csv format:**
```
eventId,recurrentInterval,recurrentTimes,recurrentEndDate
2,1w,10,2026-03-31
```

## 🎨 Customization

### Modifying Styles
Edit the `style.css` file to customize the application's appearance. The current theme includes:
- Dark mode color scheme
- Custom button styles
- Calendar cell styling
- Dialog and alert styling

### Adding Features
The application is modular with separate classes for:
- `EventManager`: Event CRUD operations
- `CSVHandler`: Data persistence
- `ReminderManager`: Notification logic
- `StatisticsManager`: Analytics and reporting

Extend these classes to add new functionality.

## 🐛 Troubleshooting

### Issue: "Error: JavaFX runtime components are missing"
**Solution**: Ensure you have:
1. Downloaded and extracted the JavaFX SDK
2. Specified the correct `--module-path` pointing to the JavaFX `lib` folder
3. Added the required modules with `--add-modules`

### Issue: Application window doesn't open
**Solution**: 
1. Check the console for error messages
2. Verify JDK version is 11 or higher
3. Ensure `events.csv` and `recurrent.csv` files exist (create empty files if needed)

### Issue: Events not saving
**Solution**:
1. Verify write permissions in the project directory
2. Check if CSV files are not open in another program
3. Review console output for error messages

### Issue: Date/Time format errors
**Solution**: Use the correct format for date/time inputs:
- Date format: `yyyy-MM-dd` (e.g., `2026-01-20`)
- DateTime format: `yyyy-MM-dd HH:mm` (e.g., `2026-01-20 14:30`)

## 📝 Notes

- The application automatically creates `events.csv` and `recurrent.csv` files if they don't exist
- Event IDs are automatically generated and incremented
- Deleting all events will reset the Event ID counter to 1
- The application shows a reminder popup for upcoming events (within 30 days) on startup
- Calendar grid adapts to window size for optimal viewing

## 👥 Contributing

To contribute to this project:
1. Fork the repository
2. Create a feature branch
3. Make your changes following the existing code structure
4. Test thoroughly with various scenarios
5. Submit a pull request with a detailed description

## 📄 License

This project is provided as-is for educational and personal use.

## 🙏 Acknowledgments

- Built with JavaFX for modern, cross-platform GUI
- Uses Java 11+ features including LocalDateTime API
- Inspired by modern calendar applications

---

**Version**: 1.0  
**Last Updated**: January 2026  
**Developed with**: Java 17 & JavaFX 21
