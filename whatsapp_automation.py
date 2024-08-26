import re
import pywhatkit
import pyautogui
import time

# Read the file
with open("whatsapp_automation_for_program.txt", "r") as file:
    content = file.read()

# Extract recipient and message using regular expressions
match = re.search(r'Recipient: ([^,]+), Message: ([^,]+),', content)
to_contact = match.group(1).strip()  # Extract the recipient
message = match.group(2).strip()     # Extract the message

# Send WhatsApp message
pywhatkit.sendwhatmsg_instantly(to_contact, message, 15)

# Wait for the message to be sent (adjust the sleep time as needed)
time.sleep(10)

# Click on the message box to focus
pyautogui.click(x=1394, y=961)  # Adjust the coordinates as per your screen resolution

# Press the enter key
pyautogui.press('enter')
