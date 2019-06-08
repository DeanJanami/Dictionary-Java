/*
 * Dean Pakravan: 757389
 * Submission Date: 6th September 2018
 * Distributed Systems: Assignment 1
 * Multi-threaded dictionary: Multi-thread Server
 * 
 * Purpose of code: Handles multiple threads
 */

package Server;
import java.io.BufferedReader;
import org.json.*;
import java.io.BufferedWriter;
import java.io.*;
import java.net.*;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.swing.JTextArea;

public class MultiServer extends Thread {

	private Socket clientSocket;
	private int clientNo;
	private File filename;
	DictionaryServer server;
	
	private JTextArea readArea;
	
	public MultiServer(Socket clientSocket, DictionaryServer server, 
							JTextArea textArea, File filename, int clientNo) {
		this.clientNo = clientNo;
		this.clientSocket = clientSocket;
		this.server = server;
		this.readArea = textArea;
		this.filename = filename;
	}
	
	// Inherited method so must be public
	public void run() {
		 try {
            // Get the input/output streams for communicating to the client.
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    clientSocket.getOutputStream(), "UTF-8"));

            String clientMsg, command, word, meaning;
           
            while ((clientMsg = in.readLine()) != null) {
				// Our Clients MSG, seperated by spaces
				StringTokenizer input = new StringTokenizer(clientMsg, " ");
				
				// Keep track of each client request.
				readArea.append("Message from client " + clientNo + ": " +
										clientMsg + "\n");
				
				command = input.nextToken();
				
				// Check to see if its a query, add or delete
				try {
					JSONObject data = readDict();
					word = input.nextToken();
					  //Query word (search)
					if ("query".equals(command)) {
						if (checkWordExists(word, data, out)) {
							query(word, data, out);
						}
				        // If the word is not found, display error message.
						else {
				        	out.write(1 + "\n");
				        	out.flush();
				            Thread.sleep(100);
				            out.write("The word: \"" + word + "\" does not exist" + "\n");
				            out.flush();
						}
						
					// Add a new word to the dictionary.
					} else if ("add".equals(command)) {
						// Check the word in dictionary before adding it.
						if (!checkWordExists(word, data, out)) {
							meaning = input.nextToken("\n").trim();
							add(word, meaning, data, out);
						} else {
							out.write("The word: " + word + ", already exists" + "\n");
							out.flush();
						}
						
					// Delete an exited word.
					} else if ("delete".equals(command)) {
					// Check the word in dictionary before removing it.
						if (checkWordExists(word, data, out)) {
							delete(word, data, out);
						} else {
							out.write("The word: " + word + ", does not exist" + "\n");
							out.flush();
						}
					}
				
				readArea.append("Response sent" + "\n");
				}
				// If the client doesn't add a meaning to a word or forgets to add the word
				catch(NoSuchElementException e) {
					 out.write("Insufficient Arguments." + "\n");
					 out.flush();	 
				} catch (InterruptedException e) {
					 out.write("Server Interuppted." + "\n");
					 out.flush();
				}
			}
            
	     }
	     catch(SocketException e) {
	         readArea.append("Socket closed." + "\n");
	     }
	     catch(JSONException e) {
	         readArea.append("Error parsing the dictionary." + "\n");
	     }
	     catch(IOException e) {
	         readArea.append("Error sending/getting information." + "\n");
	     }
	     finally {
	            // Close the socket when done.
	            if (clientSocket != null) {
	                try {
	                    clientSocket.close();
	                    readArea.append("#####################" + "\n");
	                    readArea.append("Connection with client: " + clientNo +
	                            " has been terminated." + "\n");
	                }
	                catch (IOException e) {
	                    server.textArea.append("Error closing Client Socket." + "\n");
	                }
	            }
	     }
	}

	// Method to check if a word exists, returning True if so
    private boolean checkWordExists(String word, JSONObject dictionary, BufferedWriter out) {
		if (dictionary.has(word.toLowerCase())) {
			return true;
		}
		else {
			return false;
		}
	}

    // Method to Search/Query for the word
    private void query(String word, JSONObject dictionary, BufferedWriter out) {
    	try {
    			// If the word is in the dictionary, just display the meaning(s) of that word.
	            JSONArray w = dictionary.getJSONArray(word.toLowerCase());
	            String meaning = w.getString(0);
	            String[] splitmeaning = meaning.split(";");
	            out.write(Integer.toString(splitmeaning.length) + "\n");
	            out.flush();
	            for (int i = 0; i < splitmeaning.length; i++) {
	            	Thread.sleep(100);
		            out.write("Meaning " + (i+1) + ": " + splitmeaning[i] + "\n");
		            out.flush();     
	            }

    	} catch(IOException e) {
			readArea.append("Error sending response to client." + "\n");
    	} catch (JSONException e) {
    		readArea.append("Error parsing the dictionary." + "\n");
		} catch (InterruptedException e) {
			readArea.append("Server/Thread was interrupted" + "\n");
		} 
    }
 
    // Method to Add a word and its meaning(s) to the dictionary if it does not exist
    // Synchronized is used to lock all threads to operate the same method
    private synchronized void add(String word, String meaning, JSONObject dictionary,
                                     BufferedWriter out) {
    	try {
	        JSONArray newWordMeaning = new JSONArray();
	        newWordMeaning.put(meaning);
	        // Add the word into dictionary with its associated meaning(s).
	        dictionary.put(word.toLowerCase(), newWordMeaning);
	        // Inform the client the dictionary is updated
	        out.write("Dictionary Updated" + "\n");
	        out.flush();
	        updateDict(dictionary);
	    } catch(IOException e) {
	    	readArea.append("Error sending/getting information." + "\n");
    	} catch (JSONException e) {
    		readArea.append("Error parsing the dictionary." + "\n");
		} 
    }

    // Method to delete a word in the dictionary if it exists
    // Synchronized is used to lock all threads to operate the same method
    private synchronized void delete(String word, JSONObject dictionary, BufferedWriter out)
            throws IOException {
        dictionary.remove(word.toLowerCase());
        // Return the action result.
        out.write("Dictionary Updated" + "\n");
        out.flush();
        updateDict(dictionary);
    }

    // JSON implementation of reading the dictionary file
    private JSONObject readDict() throws JSONException {
    	try {
	        JSONTokener dictRead = new JSONTokener(new FileReader(filename));
	        JSONObject dataSet = new JSONObject(dictRead);
	        return dataSet;
    	} 
    	// If not dictionary exists, we create one
    	catch (FileNotFoundException e) {
    		JSONObject newDataSet = new JSONObject();
    		return newDataSet;
    	}
    }

    /* Update the JSON dictionary file when removing a word or adding
     * This method does not need to be synchronized since it comes from a synchronized method
     */
    private void updateDict(JSONObject dictionary) {
        FileWriter outputStream = null;
        try {
            outputStream = new FileWriter(filename, false);
            outputStream.write(dictionary.toString());
            outputStream.flush();
            outputStream.close();
            readArea.append("Dictionary Updated" + "\n");
        }
        catch (IOException e) {
            readArea.append("Error writing to the dictionary." + "\n");
        }
    }

}
