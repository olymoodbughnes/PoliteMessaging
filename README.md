# PoliteMessaging
University project of a peer-to-peer, asynchronous messaging system  called "Polite Messaging", through the use of a TCP server.
---
![image description](Images/exampleofinteraction.jpg)

From top to bottom, the first line is the input by the user, it shows the letters "nc" which indicates the start of a new connection. Followed by the local IP address and the port which will be used for the TCP connection, whihc in this instance is "20111"

The server then responds with "PROTOCOL?", which the user responds to with the protocol version and username ID.

In the following line the user inputs "LIST? 0 0". The term "LIST?" requests a number which represents the total number of messages sent to the user that meet the two following criteria. The "0 0" that follows the term "LIST?" represent a time value in unix time, and the number of topic tags/headers that a message may have been attached with. e.g(#important, #priority).

Some examples of valid LIST? requests:
```
LIST?  1614680000 1

LIST?  1620000000 3
```

The server then responds to the user by stating the number of messages that have been stored "MESSAGES 9", this is followed by the list of message ID's encrypted using SHA-256. These encrypted ID's will later be used to access those specific messages.


---


![image description](Images/exampleofinteractionpart2.jpg)

![image description](Images/exampleofinteractionpart3.jpg)

![image description](Images/Howtorun.jpg)

![image description](Images/doc1.jpg)

![image description](Images/doc2.jpg)

![image description](Images/doc3.jpg)
