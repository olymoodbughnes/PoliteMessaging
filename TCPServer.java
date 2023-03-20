import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.security.*;
import java.lang.Object;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;


public class TCPServer {

    /**
     * Initialise a new server. To run the server, call run().
     */
    public TCPServer() {}

    /**
     * Runs the server.
     * @throws IOException 
     */


	/**
	 *Hungund, B., 2021. SHA-256 Hash in Java - GeeksforGeeks. [online] GeeksforGeeks. Available at: <https://www.geeksforgeeks.org/sha-256-hash-in-java/> [Accessed 21 April 2021].
	 */
	/***public method to get the SHA-256 checksum of a string***/
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException{

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(input.getBytes(StandardCharsets.UTF_8));

	}
	/***public method to convert hash to readable string***/
    public static String toHexString(byte[] hash){

		BigInteger number = new BigInteger(1, hash);
		StringBuilder hexString = new StringBuilder(number.toString(16));

		while(hexString.length() < 32){
			hexString.insert(0, '0');
		}

		return hexString.toString();

	}
	/***==========================================================END OF CITATION==========================================================================***/


	/***public void method to send requests to a peer***/
    public static void sendRequest(Writer w) throws IOException{
	/***Initialise a buffered reader for server console***/
	BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
	/***requests read from console line by line***/
	String requests = consoleReader.readLine();
		/***while the console input is not empty, read line.***/
		while(requests != null){
			/***Server side lexicon of available requests, non protocol compliant requests will be ignored***/
			if(requests.contains("GET?") || requests.contains("TIME?") || requests.contains("BYE!") || requests.contains("LIST?") || requests.contains("Topic: ") || requests.contains("Subject: ") || requests.contains("To: ")){
				w.write(requests + '\n');
				w.flush();
				requests = consoleReader.readLine();
			}
			/***If a request is unsupported skip sendRequest method***/
			break;
		}
		
	}
    public void run() throws IOException {

	/*** Set up to accept incoming TCP connections ***/
	

	int port = 20111;
	
	// Open the server socket
	System.out.println("Opening the server socket on port " + port);
	ServerSocket serverSocket = new ServerSocket(port);

	
	/*** Receive client connection ***/
	
	// Waits until a client connects
	System.out.println("Server waiting for client...");
	Socket clientSocket = serverSocket.accept();
	System.out.println("Client connected!");
	Date date = new Date();

	// Set up readers and writers for convenience
	BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
	
	Writer writer = new OutputStreamWriter(clientSocket.getOutputStream());
	/*** Variable declaration ***/
	//Protocol version
	int PVer = 1;
	//Peer identifier
	String identifier = "Yairo";
	//Messages
	String err = "ERROR: PEER PROTOCOL INCOMPATIBLE; CLOSING CONNECTION";
	//Send protocol request to client peer
	writer.write("PROTOCOL? " + PVer + " " + identifier + '\n');
	writer.flush();
	//peer message
	String msg;
	int count = 0;
	int isFound = 0;
	int isFound2 = 0;
	//Message hashes
	String hash = "";
	String hash2 = "";
	String hash3 = "";
	//Variable to divert next iteration to a specific if statement
	int rememberPath = 0;
	//Permanent rememberPath variable copy
	int rememberRemember = 0;
	//Four indicators for LIST? command, each indicate if a value is searched by peer, hence the name "indi"
	int indi1 = 0;
	int indi2 = 0;
	int indi3 = 0;
	int indi4 = 0;
	//Time requested by peers
	int reqTime = 0;
	//Reader helper variable
	String line3 = "";
	int sent = 0;
	//Peer requested headers for LIST? request, default value is leftEmpty to indicate it's not relevant for the search
	//TO
	String toBeChecked1 = "leftEmpty";
	//TOPIC
	String toBeChecked2 = "leftEmpty";
	//SUBJECT
	String toBeChecked3 = "leftEmpty";
	//String buffers to save outputs to peer
	StringBuffer sb = new StringBuffer();
	StringBuffer sb2 = new StringBuffer();
	StringBuffer sb3 = new StringBuffer();
	StringBuffer sb4 = new StringBuffer();
	//Indicator to divert next iteration to new message statements only
	int newmsg = 0;
	//Variable containing the number of times the next iteration will be diverted
	int sections = 14;
	//Variable to divert new msg iteration the same number of times as the number of lines a peer has requested to write
	int redirect = 0;
	//Permanent redirect variable copy to display "lines left" counter
	int reRedirect = 0;
	//Variable to avoid closing connection early while message is being redacted
	int slack = 0;
	//Variable to keep track of the number of found messages
	int actualCount = 0;
	//Variables to indicate that a specific optional header has been found
	int oneTime = 0;
	int oneTime2 = 0;
	int oneTime3 = 0;
	//FileWriter to write to a permanent database
	BufferedWriter toFile = new BufferedWriter(new FileWriter("./PM/PMmessages", true));
		
	
	
	while (true) {
		//reset count of found messages
	    actualCount = 0;
	    //next peer line becomes msg variable
	    msg = reader.readLine();
	    //sections variable is constantly subtracted
	    sections--;

		/***NEW MESSAGE DIVERSION***/
		//if the variable msg contains the words "new message", divert through here
		//also while a new msg is being written also divert through here
		if (msg.contains("New msg") || newmsg == 1){
			//reset String buffer values
			sb.setLength(0);
			sb2.setLength(0);
			
			//if at the start of a new message, enter if statement
			if(newmsg == 0){
				//reset third string buffer
				sb3.setLength(0);
				//indicate to peer what input is expected
				writer.write("Enter your name/email/identifier [press enter twice to submit]: " + '\n');
				//sections is reset to 14
				sections = 14;
				//indication to peer sends
				writer.flush();
				//unrecognised commands won't end connection as they are part of the new message
				slack = 1;
			}

			//switch new message indicator to one, indicating that a new message is being redacted
			newmsg = 1;
				//if user is writing their identifier, enter if statement
				if(sections == 13){
					//append message information and new line to empty string buffer
					sb3.append("From: " + msg);
					sb3.append('\n');
				
				} else if(sections == 12){
					//indicate to peer their next line to be filled
					writer.write("Name/email of recipient [press enter twice to submit]: " + '\n');
					writer.flush();

				} else if (sections == 11){
					//next peer input is appended to the string buffers
					sb3.append("To: " + msg);
					sb3.append('\n');
					sb4.append(msg);

				} else if(sections == 10){
					//indicate to peer their next line to be filled
					writer.write("Enter message subject [press enter twice to submit]: " + '\n');
					writer.flush();	
					

				} else if (sections == 9){
					//next peer input is appended to the string buffers
					sb3.append("Subject: " + msg);
					sb3.append('\n');
					sb4.append(msg);

				} else if(sections == 8){
					//indicate to peer their next line to be filled
					writer.write("Enter the topic [Example: #business] [press enter twice to submit]: " + '\n');
					writer.flush();	
				} else if (sections == 7){
					//next peer input is appended to the string buffers
					sb3.append("Topic: " + msg);
					sb3.append('\n');
					sb4.append(msg);

				} else if(sections == 6){
					//indicate to peer their next line to be filled
					writer.write("Enter the number of content lines [press enter twice to submit]: " + '\n');
					writer.flush();
				} else if (sections == 5){
					//next peer input is appended to the string buffers
					sb3.append("Contents: " + msg);
					sb3.append('\n');
					sb4.append(msg);
					//number of requested content lines is saved permanently and temporarily
					reRedirect = Integer.parseInt(msg);
					redirect = Integer.parseInt(msg);

				} else if(sections == 4){
					//indicate to peer their next line to be filled
					writer.write("Enter your message [press enter twice to submit]: " + '\n');
					writer.flush();	

				} else if (redirect != 1 && sections == 3){
					//redirect represents the number of lines left for the peer message
					//if peer runs out of lines, if statement won't be entered

					//indicate lines remaining
					writer.write("Lines remaining [" + (redirect - 1) + "/" + reRedirect + "]: " + '\n');
					writer.flush();
					//
					if(!(msg.contains("Lines "))){
						//if a line doesn't contain the lines indicator, append to string buffer
						sb4.append(msg);
						sb3.append(msg);
						sb3.append('\n');
					
					}
					//subtract 1 from the amount of lines left
					redirect--;
				} else if (sections == ((sections-redirect) + 1)){
					//append final line to string buffer
					sb3.append(msg);
					sb3.append('\n');
					//get SHA-256 from message contents
					try{
						//write the first message header with the hash
						toFile.write("Message-id: " + "SHA-256 " + toHexString(getSHA(sb4.toString())) + '\n');
					} catch(NoSuchAlgorithmException e){
						System.out.println("Exception thrown for incorrect algorithm: " + e);		
					}

					//get current time for the message written
					long UnixTime = System.currentTimeMillis() / 1000L;
					//calculate and add current time header
					toFile.write("Time-sent: " + UnixTime + '\n');
					toFile.write(sb3.toString());
					toFile.flush();
					//new message is finished
					newmsg = 0;
					//any new illegible messages will cause connection to close
					slack = 1;
				}

		}
	//if a new message is not being written enter if statement
	if(newmsg == 0){
		//get first word of message if it isn't just one word, single word will also be passed
	    switch(msg.contains(" ") ? msg.split(" ")[0] : msg)	{
		case "NOW":
			//let server make requests
			System.out.println("Do you wish to make a request? [Enter to skip]");
			break;
		case "FOUND":
			//let server make requests
			System.out.println("Do you wish to make a request? [Enter to skip]");
			sendRequest(writer);
			break;
		case "MESSAGES":
			//let server make requests
			System.out.println("Do you wish to make a request? [Enter to skip]");
			sendRequest(writer);
			break;
		case "BYE!":
			//close connection
			clientSocket.close();
			break;
		case "PROTOCOL?":
			//get individual words from response
			String VerId[] = msg.split(" ");
			String PeerVersion = VerId[1];
			String PeerId = VerId[2]; 
			
			//if there is a mismatch in protocol versions ente if statement
			if(PVer > Integer.parseInt(PeerVersion)) {
				//write error message
				writer.write(err + '\n');
				writer.flush();
				System.out.println(err + '\n');
				//close connection
				clientSocket.close();
			}
			//let server make requests
			System.out.println("Do you wish to make a request? [Enter to skip]");
			sendRequest(writer);
			break;

		case "TIME?":
			//calculate current time in UnixTime
			long UnixTime = System.currentTimeMillis() / 1000L;
			//respond with current time
			writer.write("NOW " + UnixTime + '\n');
			writer.flush();
			//let server make requests
			System.out.println("Do you wish to make a request? [Enter to skip]");
			sendRequest(writer);
			break;


			


		case "GET?":
			//instantiate a file reader to read current database and retrieve message
			FileReader fr = new FileReader("./PM/PMmessages");
			BufferedReader msgReader = new BufferedReader(fr);
			//get hash
			String getHash[] = msg.split(" ");
			String reqHash = getHash[2];
			String line;
			int auxCount = 0;
			//while not empty
			while((line = msgReader.readLine()) != null) {

				if(line.contains("SHA-256")) {
				//save hash into variable
				String foundMessage[] = line.split(" ");
				String indicator = foundMessage[0];
				hash = foundMessage[2];

				}
				
				//if hash is equal to requested hash
				if(hash.equals(reqHash)) {

					if (auxCount == 0){
						//output to user that it has been found
						writer.write("FOUND" + '\n');
						//activate found indicator
						isFound++;

					}

				auxCount++;
				writer.flush();
				writer.write(line + '\n');
				writer.flush();

				}
			}

			if(isFound == 0) {
				//if it is not found output sorry
				writer.write("SORRY" + '\n');
				writer.flush();

			}
			//reset variables
			auxCount = 0;
			isFound = 0;
			//close filereader
			fr.close();
			//let server make requests
			System.out.println("Do you wish to make a request? [Enter to skip]");
			sendRequest(writer);
		break;

		case "LIST?":
			//make new file reader
			FileReader fr2 = new FileReader("./PM/PMmessages");
			BufferedReader msgReader2 = new BufferedReader(fr2);
			//get hash
			String getReq[] = msg.split(" ");
			reqTime = Integer.parseInt(getReq[1]);
			String reqHead = getReq[2];

			if(Integer.parseInt(getReq[2]) > 0){
				//if the user requests to search by header redirect and remember value
				rememberPath = Integer.parseInt(getReq[2]) + 1;
				rememberRemember = rememberPath;
				break;

			}	

			String line2;
			int auxCount2 = 0;

			//while not empty
			while((line2 = msgReader2.readLine()) != null) {
				isFound2 = 0;

				if((line2.contains("SHA-256")) && auxCount2 == 1){
					actualCount++;
					auxCount2 = 0;	
					isFound2 = 1;

				}

			
				if(line2.contains("SHA-256")) {
					
					String foundMessage2[] = line2.split(" ");
					String indicator2 = foundMessage2[0];
					hash2 = foundMessage2[2];

				
				}
				
				
				
				if(line2.contains("Time-sent:")){
					String foundMessage3[] = line2.split(" ");
					int sentTime = Integer.parseInt(foundMessage3[1]);
					long UnixTime2 = System.currentTimeMillis() / 1000L;
					if(reqTime > UnixTime2){
						writer.write("ERROR: TIME ENTERED IS IN THE FUTURE");
						break;					
					}
					if (sentTime >= reqTime){
						auxCount2 = 1;
						sb.append(hash2);
						sb.append('\n');

					}	
				}

			}

			

			
			
			fr2.close();
			
			if(isFound2 != 1){
				if(auxCount2 == 1){
				actualCount++;
				}
				writer.write("MESSAGES " + actualCount + '\n');
				
				writer.write(sb.toString() + '\n');
				writer.flush();
				sb.setLength(0);
				actualCount = 0;
				isFound2 = 0;
			}
			//let server make requests
		System.out.println("Do you wish to make a request? [Enter to skip]");
		sendRequest(writer);
		break;

		

		
		default:
			
			sb2.setLength(0);
			actualCount = 0;
			indi1 = 0;
			indi2 = 0;
			indi3 = 0;
			indi4 = 0;
			if(rememberPath > 1 && msg.contains("To:")){
				String pos1[] = msg.split(" ");
				toBeChecked1 = pos1[1];
				rememberPath--;
				break;
			
			}
			
			if(rememberPath > 1 && msg.contains("Topic:")){

				String pos2[] = msg.split(" ");
				toBeChecked2 = pos2[1];
				rememberPath--;
				break;	
			
			}

			if(rememberPath > 1 && msg.contains("Subject:")){
				String pos3[] = msg.split(" ");
				toBeChecked3 = pos3[1];
				rememberPath--;
				break;
			}


			if(rememberPath == 1){


				FileReader fr3 = new FileReader("./PM/PMmessages");
				BufferedReader msgReader3 = new BufferedReader(fr3);

				//while not empty
				while((line3 = msgReader3.readLine()) != null) {
					
					
				
					if(line3.contains("SHA-256") && ((indi1 == 1) && (indi2 == 1) && (indi3 == 1) && (indi4 == 1))){
						System.out.println("added");
						sb2.append(hash3);
						sb2.append('\n');
						actualCount++;
					} 
					

					if((line3.contains("SHA-256"))) {
						indi1 = 0;
						indi2 = 0;
						indi3 = 0;
						indi4 = 0;

						String foundMessage3[] = line3.split(" ");
						String indicator3 = foundMessage3[0];
						hash3 = foundMessage3[2];
						
					}
					
					
				
					if(line3.contains("To:")){
						oneTime = 1;
						if(line3.contains(toBeChecked1) || toBeChecked1 == "leftEmpty"){

							indi1 = 1;

						} else {

							indi1 = 0;
						}

					} else if(oneTime == 0){

						indi1 = 0;

					}
					
					if(line3.contains("Topic:")){
						oneTime2 = 1;
						if(line3.contains(toBeChecked2) || toBeChecked2 == "leftEmpty")					{							
							
							indi2 = 1;

						} else {

							indi2 = 0;

						}
						
					}else if(oneTime2 == 0){

						indi2 = 0;

					}
					
					if(line3.contains("Subject:")){
						oneTime3 = 1;
						if(line3.contains(toBeChecked3) || toBeChecked3 == "leftEmpty"){

							indi3 = 1;

						} else {

							indi3 = 0;

						}
	
					}else if(oneTime3 == 0){

						indi3 = 0;

					}
					
					//if user was looking only for one criteria, activate the rest of the indicators as they are irrelevant
					if((indi1 == 1) && (toBeChecked2 == "leftEmpty") && (toBeChecked3 == "leftEmpty")){

						indi2 = 1;
						indi3 = 1;

					}
					//if user was looking only for one criteria, activate the rest of the indicators as they are irrelevant
					if((toBeChecked1 == "leftEmpty") && (indi2 == 1) && (toBeChecked3 == "leftEmpty")){

						indi1 = 1;
						indi3 = 1;

					}
					//if user was looking only for one criteria, activate the rest of the indicators as they are irrelevant
					if((toBeChecked1 == "leftEmpty") && (toBeChecked2 == "leftEmpty") && (indi3 == 1)){

						indi1 = 1;
						indi2 = 1;

					}
					//if user was looking only for two criteria, activate the rest of the indicators as they are irrelevant
					if((indi1 == 1) && (indi2 == 1) && (toBeChecked3 == "leftEmpty")){

						
						indi3 = 1;

					}
					//if user was looking only for two criteria, activate the rest of the indicators as they are irrelevant
					if((indi1 == 1) && (toBeChecked2 == "leftEmpty") && (indi3 == 1)){

						indi2 = 1;

					}
					//if user was looking only for two criteria, activate the rest of the indicators as they are irrelevant
					if((toBeChecked1 == "leftEmpty") && (indi2 == 1) && (indi3 == 1)){

						indi1 = 1;
						

					}
					
					if(line3.contains("Time-sent:")){
						String deco[] = line3.split(" ");
						String intostr = deco[1];
						
						if(Integer.parseInt(intostr) >= reqTime){
							
							indi4 = 1;

						} else {
							
							indi4 = 0;
						}
					}
				}
			}

			if(((indi1 == 1) && (indi2 == 1) && (indi3 == 1) && (indi4 == 1))){

				actualCount++;
				sb2.append(hash3);
				sb2.append('\n');

			}

			if(rememberPath == 1){

				System.out.println("here");
				writer.write("MESSAGES "+ actualCount + '\n');
				writer.write(sb2.toString() + '\n');
				writer.flush();
				sb2.setLength(0);
				toBeChecked1 = "leftEmpty";
				toBeChecked2 = "leftEmpty";
				toBeChecked3 = "leftEmpty";
				oneTime = 0;
				oneTime2 = 0;
				oneTime3 = 0;

			}
			if(rememberPath == 0 && slack == 0){

				clientSocket.close();

			}

			rememberPath = 0;
			//let server make requests
			System.out.println("Do you wish to make a request? [Enter to skip]");
			sendRequest(writer);
			sb2.setLength(0);
		break;

		}
	}
}

	// Close down the connection
	

    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
	TCPServer server = new TCPServer();
	server.run();
    }
}
