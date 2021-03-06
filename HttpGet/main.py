import time

import requests

if __name__ == '__main__':
    numberOfRequests = 100000

    url = "http://127.0.0.1:64237"
    startTime = time.time()
    for i in range(numberOfRequests):
        response = requests.get(url)

    endTime = (time.time() - startTime)
    print("Time taken to process " + str(numberOfRequests) + " requests: " + str(endTime) + "\n")
    print("Average time taken per request: " + str((endTime / numberOfRequests)) + "\n")
