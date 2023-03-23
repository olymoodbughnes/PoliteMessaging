# PoliteMessaging
University project of a peer-to-peer, asynchronous messaging system  called "Polite Messaging", through the use of a TCP server written in Java.

---

![image description](Images/exampleofinteraction.jpg)

# Line by line explanation

### Establishing the connection
The first line is the input by the user, it shows the letters "nc" which indicates the start of a new connection. Followed by the local IP address and the port which will be used for the TCP connection, whihc in this instance is "20111"

### Protocol and User Identification
The server then responds with "PROTOCOL?", which the user responds to with the protocol version and username ID.

### "LIST?" request.
In the following line the user inputs "LIST? 0 0". The term "LIST?" requests a number which represents the total number of messages sent to the user that meet the two following criteria. The "0 0" that follows the term "LIST?" represent a time value in unix time, and the number of topic tags/headers that a message may have been attached with. e.g(#important, #priority).

Some examples of valid LIST? requests:
```
LIST?  1614680000 1

LIST?  1620000000 3
```
### "LIST?" request response.
The server then responds to the user by stating the number of messages that have been stored "MESSAGES 9", this is followed by the list of message ID's encrypted using SHA-256. These encrypted ID's will later be used to access those specific messages.

### New message creation

#### Sender ID
Upon entering a new message request, the server responds with a prompt which asks for the user's preferred identification.

#### Recipient ID
The user is then asked to provide the recipients identification.

#### Message contents
Once the user has entered both IDs, the user will asked to enter the message they want to send to the user whose ID they have previously entered.

#### Topic selection
The user enters the topics for the message to be sent.

#### Content length selection
The user chooses the number of lines available by entering an integer.

#### Message contents
User enters their message. Each time the user moves to the next line the server will remind them of the number of lines they have remaining.





---


![image description](Images/exampleofinteractionpart2.jpg)
---
Explanation starts from the line which contains "TIME?" Request.
  
### TIME? Request
This is a simple request which makes the server return the current time in unix time in the format (NOW [current unix time])

### GET? Request
This is the request used to read individual messages stored in the system, this is where the previously mentiones encrypted IDs are used.
A message can be viewed by typing this request followed by "SHA-256" and the message ID

Some examples of valid GET? requests
```
GET? SHA-256 e8fbe9c738b1d1d22861d8c858fcca9c04255ccb5b9b15838e3b65f803267afc

GET? SHA-256 2f63d9d94ec953481e9a0b40fafb44a7f3bfc5055a2842f18ef4fbc431997333
```

After entering the request the server will respond with either "SORRY", indicating that the message requested was not found, or "FOUND".
If the message is found it will be displayed after listing the details of the sender as well as the tags and contents of the message.

### BYE!
This request ends the connection.

---

### The rest of the sample communication.

![image description](Images/exampleofinteractionpart3.jpg)
