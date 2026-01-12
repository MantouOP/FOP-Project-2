import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple test to verify event creation and alerts work
 */
public class TestEventCreation extends Application {

    private EventManager eventManager;
    private DateTimeFormatter dateTimeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    @Override
    public void start(Stage primaryStage) {
        eventManager = new EventManager();

        Button testBtn = new Button("Test Create Event");
        testBtn.setOnAction(e -> testCreateEvent());

        Button testAlertBtn = new Button("Test Alert Only");
        testAlertBtn.setOnAction(e -> testAlert());

        VBox root = new VBox(10, testBtn, testAlertBtn);
        root.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Event Creation Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void testCreateEvent() {
        System.out.println("\n=== TESTING EVENT CREATION ===");

        LocalDateTime start = LocalDateTime.of(2026, 1, 15, 9, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 15, 10, 0);

        System.out.println("Creating event with:");
        System.out.println("  Title: Test Event");
        System.out.println("  Start: " + start);
        System.out.println("  End: " + end);

        Event event = eventManager.createEvent("Test Event", "This is a test", start, end);

        if (event != null) {
            System.out.println("\n✓ Event created successfully!");
            System.out.println("  Event ID: " + event.getEventId());
            System.out.println("  Title: " + event.getTitle());

            String successMsg = String.format(
                "Event created successfully!\n\nTitle: %s\nStart: %s\nEnd: %s",
                event.getTitle(),
                dateTimeFmt.format(event.getStartDateTime()),
                dateTimeFmt.format(event.getEndDateTime())
            );

            System.out.println("\nShowing alert with message:");
            System.out.println(successMsg);

            showAlert("Event Created", successMsg);
        } else {
            System.out.println("\n✗ Event creation failed!");
            showAlert("Error", "Event creation failed!");
        }
    }

    private void testAlert() {
        System.out.println("\n=== TESTING ALERT ONLY ===");
        showAlert("Test Alert", "This is a test alert.\nIt has multiple lines.\nDid you see it?");
    }

    private void showAlert(String title, String msg) {
        System.out.println("\n>>> Creating Alert <<<");
        System.out.println("Title: " + title);
        System.out.println("Message: " + msg);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);

        System.out.println("Calling showAndWait()...");
        alert.showAndWait();
        System.out.println("Alert was closed by user\n");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

