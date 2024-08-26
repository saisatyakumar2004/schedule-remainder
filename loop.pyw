import os
import datetime
import time
import re
import subprocess

# Flag to track if WhatsApp has been opened for the current event
whatsapp_opened = False

# Function to check if current date and time match with the specified date and time
def check_date_time_match(file_date, file_time):
    current_datetime = datetime.datetime.now().replace(second=0, microsecond=0)  # Remove seconds and microseconds
    formatted_file_datetime = datetime.datetime.strptime(file_date + " " + file_time, "%Y-%m-%d %H:%M").replace(second=0, microsecond=0)  # Remove seconds and microseconds
    return current_datetime == formatted_file_datetime

# Read date and time information from whatsapp_automation.txt
def read_date_time():
    matched_lines = []
    lines_to_keep = []

    with open("C:\\EventReminder\\whatsapp_automation.txt", "r") as file:
        for line in file:
            date_match = re.search(r'Date: (\d{4}-\d{2}-\d{2})', line)
            time_match = re.search(r'Time: (\d{2}:\d{2})', line)
            if date_match and time_match:
                file_date = date_match.group(1)
                file_time = time_match.group(1)
                if check_date_time_match(file_date, file_time):
                    matched_lines.append(line.strip())
                else:
                    lines_to_keep.append(line.strip())

    # Rewrite the file excluding the matched line
    with open("C:\\EventReminder\\whatsapp_automation.txt", "w") as file:
        for line in lines_to_keep:
            file.write(line + "\n")

    return matched_lines


# Main loop to continuously check and run the program
while True:
    matched_lines = read_date_time()
    if matched_lines:
        if not whatsapp_opened:  # Only open WhatsApp if it hasn't been opened for the current event
            with open("whatsapp_automation_for_program.txt", "w") as output_file:
                for line in matched_lines:
                    output_file.write(line + "\n")
            # Run whatsapp_automation.py in a separate process
            subprocess.Popen(["python", "C:\\EventReminder\\whatsapp_automation.py"], creationflags=subprocess.DETACHED_PROCESS)
            whatsapp_opened = True  # Set the flag to True indicating WhatsApp has been opened
    else:
        whatsapp_opened = False  # Reset the flag if there are no matched lines
        # Clear the content of whatsapp_automation_for_program.txt if there are no matched lines
        open("whatsapp_automation_for_program.txt", "w").close()
    time.sleep(30)  # Sleep for 30 seconds before checking again
