import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.*;
import javafx.scene.layout.HBox;

public class GUIEventManager extends Application {
    private List<String> events; // List to store events
    private static final String EVENTS_FILE = "C:\\EventReminder\\events.txt";
    private static final String WHATSAPP_AUTOMATION_FILE = "C:\\EventReminder\\whatsapp_automation.txt";
    public String selectedDateString = "";
    public String selectedTime = "";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Event Manager");

        // Initialize the list of events
        events = loadEventsFromFile();

        // Creating buttons
        Button addBtn = new Button("Add Event");
        Button removeBtn = new Button("Remove Event");
        Button viewBtn = new Button("View Events");
        Button exitBtn = new Button("Exit");

        // Setting custom size for the buttons
        addBtn.setMinSize(175, 175);
        removeBtn.setMinSize(175, 175);
        viewBtn.setMinSize(175, 175);
        exitBtn.setMinSize(175, 175);

        // Adding action listeners to the buttons
        addBtn.setOnAction(e -> openAddEventWindow());
        removeBtn.setOnAction(e -> openRemoveEventWindow(primaryStage));
        viewBtn.setOnAction(e -> displayEvents(primaryStage));
        exitBtn.setOnAction(e -> {
            saveEventsToFile();
            primaryStage.close();
        });

        // Creating layout for buttons
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(addBtn, 0, 0);
        gridPane.add(removeBtn, 0, 1);
        gridPane.add(viewBtn, 1, 0);
        gridPane.add(exitBtn, 1, 1);

        Scene scene = new Scene(gridPane, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openAddEventWindow() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Event");
        dialog.setHeaderText("Please enter event details");
    
        // Set the button types
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
    
        // Create labels and controls
        Label nameLabel = new Label("Event Name:");
        TextField nameField = new TextField();
    
        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();
    
        // Add listener to update selectedDateString
        datePicker.setOnAction(event -> selectedDateString = datePicker.getValue().toString());
    
        Label timeLabel = new Label("Time:");
    
        ComboBox<String> hourComboBox = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            hourComboBox.getItems().add(String.format("%02d", i));
        }
        hourComboBox.getSelectionModel().selectFirst();
    
        ComboBox<String> minuteComboBox = new ComboBox<>();
        for (int i = 0; i < 60; i++) {
            minuteComboBox.getItems().add(String.format("%02d", i));
        }
        minuteComboBox.getSelectionModel().selectFirst();
    
        ComboBox<String> amPmComboBox = new ComboBox<>();
        amPmComboBox.getItems().addAll("AM", "PM");
        amPmComboBox.getSelectionModel().selectFirst();
    
        // Add listeners to update selectedTime
        hourComboBox.setOnAction(event -> updateSelectedTime(hourComboBox, minuteComboBox, amPmComboBox));
        minuteComboBox.setOnAction(event -> updateSelectedTime(hourComboBox, minuteComboBox, amPmComboBox));
        amPmComboBox.setOnAction(event -> updateSelectedTime(hourComboBox, minuteComboBox, amPmComboBox));
    
        Label descriptionLabel = new Label("Event Description:");
        TextArea descriptionTextArea = new TextArea();
    
        CheckBox automateCheckBox = new CheckBox("Send Automated Message");
    
        // Additional fields for automated message
        Label recipientLabel = new Label("Phone Number of the Recipient:");
        TextField recipientField = new TextField();
        recipientField.setDisable(true);
    
        Label messageLabel = new Label("Enter Message:");
        TextArea messageTextArea = new TextArea();
        messageTextArea.setDisable(true);
    
        // Add listeners to checkbox to show/hide additional fields
        automateCheckBox.setOnAction(event -> {
            if (automateCheckBox.isSelected()) {
                recipientField.setDisable(false);
                messageTextArea.setDisable(false);
            } else {
                recipientField.setDisable(true);
                messageTextArea.setDisable(true);
            }
        });
    
        // Add all controls to the dialog pane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.addRow(0, nameLabel, nameField);
        gridPane.addRow(1, dateLabel, datePicker);
        gridPane.addRow(2, timeLabel, new HBox(5, hourComboBox, new Label(":"), minuteComboBox, amPmComboBox));
        gridPane.addRow(3, descriptionLabel, descriptionTextArea);
        gridPane.addRow(4, automateCheckBox);
        gridPane.addRow(5, recipientLabel, recipientField);
        gridPane.addRow(6, messageLabel, messageTextArea);
    
        dialog.getDialogPane().setContent(gridPane);
    
        // Convert the result to a string when the addButton is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                String time = hourComboBox.getValue() + ":" + minuteComboBox.getValue() + " " + amPmComboBox.getValue();
                String eventDetails = nameField.getText() + ", Date: " + selectedDateString + ", Time: " + selectedTime + ", Description: " + descriptionTextArea.getText();
    
                // Add automated message details if checkbox is selected
                if (automateCheckBox.isSelected()) {
                    String automatedMessage = "Recipient: " + recipientField.getText() + ", Message: " + messageTextArea.getText();
                    writeMessageToFile(automatedMessage);
                }
    
                return eventDetails;
            }
            return null;
        });
    
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(events::add); // Add event if present
        saveEventsToFile(); // Save events to file
    
        // Print selected date and time
        System.out.println("Selected Date: " + selectedDateString);
        System.out.println("Selected Time: " + selectedTime);
    }
    
    private void updateSelectedTime(ComboBox<String> hourComboBox, ComboBox<String> minuteComboBox, ComboBox<String> amPmComboBox) {
        selectedTime = hourComboBox.getValue() + ":" + minuteComboBox.getValue() + " " + amPmComboBox.getValue();
    }
    
    
    

    private void writeMessageToFile(String message) {
        // Get current date and time
        String convertedTime = convert12HourTo24Hour(selectedTime);
    
        // Append date and time to the message
        String messageWithDateTime = message + ", Date: " + selectedDateString + ", Time: " + convertedTime;
    
        // Write message to file
        try (PrintWriter writer = new PrintWriter(new FileWriter(WHATSAPP_AUTOMATION_FILE, true))) {
            writer.println(messageWithDateTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void openRemoveEventWindow(Stage primaryStage) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Remove Event");
        dialog.setHeaderText("Please select the event to remove");

        // Set the button types
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(cancelButton);

        // Create a ListView to display events
        ListView<String> eventListView = new ListView<>();
        eventListView.getItems().addAll(events);

        // Add the ListView to the dialog pane
        dialog.getDialogPane().setContent(eventListView);

        // Handle event removal
        eventListView.setOnMouseClicked(e -> {
            String selectedEvent = eventListView.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Remove Event");
                alert.setContentText("Are you sure you want to remove this event?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    events.remove(selectedEvent);
                    saveEventsToFile(); // Save events to file
                    dialog.close();
                    openRemoveEventWindow(primaryStage); // Reopen window to reflect changes
                }
            }
        });
        dialog.getDialogPane().setPrefWidth(700); // Adjust width as needed
        dialog.getDialogPane().setPrefHeight(600); // Adjust height as needed
        dialog.showAndWait();
    }

    private void displayEvents(Stage primaryStage) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Events List");

        // Set the button types
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButton);

        // Create a ListView to display events
        ListView<String> eventListView = new ListView<>();
        eventListView.getItems().addAll(events);

        // Add the ListView to the dialog pane
        dialog.getDialogPane().setContent(eventListView);

        // Add an Edit button
        ButtonType editButtonType = new ButtonType("Edit", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().add(editButtonType);

        // Handle edit button action
        dialog.getDialogPane().lookupButton(editButtonType).addEventFilter(ActionEvent.ACTION, event -> {

            String selectedEvent = eventListView.getSelectionModel().getSelectedItem();
            if (selectedEvent != null) {
                // Prompt user for new event details
                Optional<String> editedEvent = promptEditEvent(selectedEvent);
                if (editedEvent.isPresent()) {
                    int selectedIndex = eventListView.getSelectionModel().getSelectedIndex();
                    events.set(selectedIndex, editedEvent.get());

                    saveEventsToFile(); // Save events to file
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Event Editor");
                    alert.setHeaderText(null);
                    alert.setContentText("Event Edited successfully!");
                    alert.showAndWait();

                    eventListView.getItems().set(selectedIndex, editedEvent.get());
                }
            }
        });

        // Set preferred width and height
        dialog.getDialogPane().setPrefWidth(700); // Adjust width as needed
        dialog.getDialogPane().setPrefHeight(600); // Adjust height as needed

        dialog.showAndWait();
    }

    private Optional<String> promptEditEvent(String selectedEvent) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Event");
        dialog.setHeaderText("Please edit event details");

        // Set the button types
        ButtonType editButtonType = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);

        // Create labels and controls
        Label nameLabel = new Label("Event Name:");
        TextField nameField = new TextField();
        nameField.setText(selectedEvent.split(", Date:")[0]); // Set initial value

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.parse(selectedEvent.split(", Date:")[1].split(", Time:")[0].trim())); // Set initial value

        Label timeLabel = new Label("Time:");

        ComboBox<String> hourComboBox = new ComboBox<>();
        for (int i = 1; i <= 12; i++) {
            hourComboBox.getItems().add(String.format("%02d", i));
        }
        hourComboBox.getSelectionModel().select(selectedEvent.split("Time:")[1].trim().substring(0, 2)); // Set initial value

        ComboBox<String> minuteComboBox = new ComboBox<>();
        for (int i = 0; i < 60; i++) {
            minuteComboBox.getItems().add(String.format("%02d", i));
        }
        minuteComboBox.getSelectionModel().select(selectedEvent.split("Time:")[1].trim().substring(3, 5)); // Set initial value

        ComboBox<String> amPmComboBox = new ComboBox<>();
        amPmComboBox.getItems().addAll("AM", "PM");
        amPmComboBox.getSelectionModel().select(selectedEvent.split("Time:")[1].trim().substring(6, 8)); // Set initial value

        Label descriptionLabel = new Label("Event Description:");
        TextArea descriptionTextArea = new TextArea();
        descriptionTextArea.setText(selectedEvent.split("Description:")[1].trim()); // Set initial value

        // Add all controls to the dialog pane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.addRow(0, nameLabel, nameField);
        gridPane.addRow(1, dateLabel, datePicker);
        gridPane.addRow(2, timeLabel, new javafx.scene.layout.HBox(5, hourComboBox, new Label(":"), minuteComboBox, amPmComboBox));
        gridPane.addRow(3, descriptionLabel, descriptionTextArea);

        dialog.getDialogPane().setContent(gridPane);

        // Convert the result to a string when the editButton is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == editButtonType) {
                String time = hourComboBox.getValue() + ":" + minuteComboBox.getValue() + " " + amPmComboBox.getValue();
                return nameField.getText() + ", Date: " + datePicker.getValue() + ", Time: " + time + ", Description: " + descriptionTextArea.getText();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private static String convert12HourTo24Hour(String time12Hour) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
        try {
            Date date = inputFormat.parse(time12Hour);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveEventsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EVENTS_FILE))) {
            for (String event : events) {
                writer.println(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadEventsFromFile() {
        List<String> loadedEvents = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(EVENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                loadedEvents.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loadedEvents;
    }

    public static void main(String[] args) {
        launch(args);
    }
}