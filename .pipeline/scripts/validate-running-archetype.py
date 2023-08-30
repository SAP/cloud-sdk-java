# Check that http://127.0.0.1:8080/hello is reachable. Maximum 60 retries.

import requests
import time

url = "http://127.0.0.1:8080/hello"

print("Checking that " + url + " is reachable.")
success = False

for i in range(60):
    try:
        response = requests.get(url)
        if response.status_code == 200:
            success = True
            break
    except:
        time.sleep(3)

if not success:
    print("Failed to connect to " + url + " after 60 retries.")
    exit(1)

print("Successfully connected to " + url + ".")




