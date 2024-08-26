import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.*;
import javax.swing.JOptionPane;

class Event1 {
    String event_name;
    String date_month;
    String time;
    String description;

    public Event1(String event_name, String date_month, String time, String description) {
        this.event_name = event_name;
        this.date_month = date_month;
        this.time = time;
        this.description = description;
    }

    public Event1() {
        this("", "", "", "");
    }
}

public class EventNotifier {
    public static void main(String[] args) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader("C:\\EventReminder\\events.txt"));
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            String current_date_month = dateFormat.format(now);
            String current_time = timeFormat.format(now);
            String line;

            while ((line = br.readLine()) != null) {
                String[] eventInfo = line.split(", ");
                if (eventInfo.length >= 4) { // Ensure there are at least 4 elements
                    String eventName = eventInfo[0];
                    String datePart = eventInfo[1].substring(eventInfo[1].indexOf(":") + 2); // Extract date
                    String timePart = eventInfo[2].substring(eventInfo[2].indexOf(":") + 2); // Extract time
                    String description = eventInfo[3]; // Remove unnecessary part of description

                    String userInputDateTime = datePart + " " + timePart;
                    String currentDateTime = dateFormat.format(now) + " " + timeFormat.format(now);
					System.out.print(userInputDateTime);
					System.out.println(currentDateTime);
                    if (userInputDateTime.equalsIgnoreCase(currentDateTime)) {
                        displayNotification(description);
                    } else {
                        System.out.println("No notification at this date and time.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void displayNotification(String message) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();

            Image image = Toolkit.getDefaultToolkit().getImage("icon.jpg"); // Replace with your icon path
            TrayIcon trayIcon = new TrayIcon(image, "Notification");
            trayIcon.setImageAutoSize(true);

            try {
                tray.add(trayIcon);
                trayIcon.displayMessage("Notification", message, TrayIcon.MessageType.INFO);

                // You can add additional actions or listeners for user interaction here

            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("SystemTray is not supported");
        }
    }
}
