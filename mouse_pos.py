import pyautogui
import time

print("Move the mouse to the desired position within the next 5 seconds...")
time.sleep(5)  # Wait for 5 seconds

# Get the current mouse pointer position
current_pos = pyautogui.position()

# Print the coordinates
print("Current mouse pointer position:", current_pos)
