/*
 * Dean Pakravan: 757389
 * Submission Date: 6th September 2018
 * Distributed Systems: Assignment 1
 * Multi-threaded dictionary: Server class
 * 
 * Purpose of code: Run a multi-threaded dictionary server to 
 * 					handle concurrent clients using JSON dictionary text
 */

package Server;
import java.net.*;
import java.io.*;
import javax.swing.*;

public class DictionaryServer {
	File dictfile;
	ServerSocket listeningSocket;
	Socket clientSocket;
	JTextArea textArea;
	
	public static void main(String[] args) {
		DictionaryServer server = new DictionaryServer();
		server.process(server, args);
	}

	private void process(DictionaryServer server, String[] args) {
		// Text area to display server information
		server.textArea = new JTextArea();
		new GUIServer(server.textArea);
		
        // Our Sockets
		ServerSocket listeningSocket = null;
		Socket clientSocket = null;
		
		
		if(args.length < 2) {
			System.out.println("Usage: java TCPServer <Port Number>" +
							" <Dictionary-file>");
			server.textArea.append("Usage: java TCPServer <Port Number>" +
							" <Dictionary-file>");
			System.exit(1);
		}
		
		// Our dictionary file
		server.dictfile = new File(args[1]);
		
		try {
			//Create a server socket listening on port argument
			int portNo = Integer.valueOf(args[0]).intValue();
			listeningSocket = new ServerSocket(portNo);
			
			// Counter to keep track of the number of clients
			int clientNo = 0;

			// Listen for incoming connections indefinitely
			while (true) {

				server.textArea.append("Server listening on port " + 
							portNo +" for a connection" + "\n");
				// Accept an incoming client connection request
				// This method will block until a connection request is received
				clientSocket = listeningSocket.accept();
				clientNo++;
				
				// This is the server dealing with concurrency
				// Multi-threading! 
				Thread clientThread = new Thread(
						new MultiServer(clientSocket, server, 
						server.textArea, server.dictfile, clientNo));
				clientThread.start();

				server.textArea.append("Client conection number " + 
									clientNo + " accepted:\n");
				server.textArea.append("Remote Port: " + clientSocket.getPort() +"\n");
				server.textArea.append("Remote Hostname: " + 
									clientSocket.getInetAddress().getHostName()+"\n");
				server.textArea.append("Local Port: " + clientSocket.getLocalPort()+"\n");
			}
		}
		
		catch(SocketException e) {
			server.textArea.append("Socket: " + e.getMessage()+"\n");
		}
		catch(IOException e) {
			server.textArea.append("IO: " + e.getMessage()+"\n");
		}
		finally {
			try {
				if (clientSocket != null) {
					clientSocket.close();
				}
			} catch (IOException e) {
				server.textArea.append("IO: " + e.getMessage()+"\n");
			}
		}
		
	}

}