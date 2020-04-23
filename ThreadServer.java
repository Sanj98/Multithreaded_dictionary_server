/** Student Name - SANJANA RATAN
    Student ID - 1041413
    
    This class performs operations requested by the client when a thread 
    is created  by Server.java class. It creates input/output streams to 
    read and write data from and to the client. It calls 3 different methods
    for each operation - search, add and delete.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

// when a thread is created, this class is called
public class ThreadServer extends Thread {
	private Socket clientSocket;
	private int clientNum;
	private Server server;
	
	// Constructor initializes the socket and server to perform operations
	public ThreadServer(Socket clientSocket, int clientNum, Server server) {
		
		this.clientSocket = clientSocket;
		this.clientNum = clientNum;
		this.server = server;
	}
	
	@Override
	public void run(){
		try {
			// create Input/Output streams for receiving and sending data to and from client
			BufferedReader in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    clientSocket.getOutputStream(),"UTF-8"));
				
			String clientMsg, operation, word, meaning;
				
			while ((clientMsg = in.readLine())!= null) {
				// use string tokenizer to iterate over the message received from client
				StringTokenizer input = new StringTokenizer(clientMsg, " ");
				server.display.append("Message from client "+clientNum+": "+clientMsg+"\n");
				
				// first word determines the operation to perform
				operation = input.nextToken();
				
				switch(operation) {
				// if operation is Search, search method is called
				case "Search":
					JSONObject dataSet1 = readFile();
                    word = input.nextToken();
                    search(word, dataSet1, out);
                    break;
                // if operation is Add, add method is called
				case "Add":
					JSONObject dataSet2 = readFile();
                    word = input.nextToken();
                    meaning = input.nextToken("\n").trim();
                    add(word, meaning, dataSet2, out);
                    break;
				case "Delete":
				// if operation is Delete, delete method is called
					JSONObject dataSet3 = readFile();
                    word = input.nextToken();
                    delete(word,dataSet3,out);
                    break;
				}
				server.display.append("Response sent to client "+clientNum+"\n");
				}
		}
		// if socket connection is closed, an error is displayed
		catch(SocketException e) {
			server.display.append("Error: Socket is closed" + "\n");
		}
		// if server is unable to read dictionary file, an error is displayed
		catch(JSONException e) {
			server.display.append("Error: Unable to parse dictionary" + "\n");
        }
		// if server is unable to send/receive data to/from client, an error is displayed
        catch(IOException e) {
        	server.display.append("Error: Unable to receive/send information" + "\n");
        } 
		finally {
            // Close the socket when done.
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                    server.display.append("Client  " + clientNum + ": connection is closed"+"\n");
                    server.display.append("********************" + "\n");
                    
                }
                catch (IOException e) {
                	server.display.append("Error: Unable to close client connections" + "\n");
                    
                }
            }
        }
	
    }
	
	// Returning meaning of word to client
	private synchronized void search(String word, JSONObject data, BufferedWriter out) throws IOException, JSONException{
		word = word.toLowerCase(); 
		// if dictionary has word send meaning to client
		if(data.has(word)) {
        JSONArray w = data.getJSONArray(word.toLowerCase());
        String m = w.getString(0);
        out.write(m+"\n");
        out.flush();
	}
	else {
		// if dicionary does not have word, send "Word not found" to client
    	out.write("Word not found"+"\n");
    	out.flush();
    	server.display.append("Word not found in dictionary"+ "\n");	
	 }
	}
	
	// Adding word and meaning to dictionary
	private synchronized void add(String word, String meaning, JSONObject data, BufferedWriter out) 
			throws IOException, JSONException {
		// add word and meaning to dictionary in form of array
		JSONArray newMeaning = new JSONArray();
        newMeaning.put(meaning);
        data.put(word.toLowerCase(), newMeaning);
        // send message to client of dictionary update
		out.write("Dictionary Updated: Added Word" + "\n");
		out.flush();
		updateDic(data);
	}
	
	// Deleting word from dictionary
	private synchronized void delete(String word, JSONObject data, BufferedWriter out)
            throws IOException, JSONException {
		word = word.toLowerCase();
		// if dictionary has word, delete the word and send update message to client
		if(data.has(word)) {
		data.remove(word);
		out.write("Dictionary Updated: Deleted Word"+"\n");
		out.flush();
		}
		else {
		// if dictionary does not have the word, send "Word does not exist" to client
			out.write("Word does not exist"+"\n");
			out.flush();
			server.display.append("Word not found in dictionary"+ "\n");
		}
        // Updating the dictionary.
        updateDic(data);
    }
	
	// Reading the dictionary file 
	private JSONObject readFile() throws FileNotFoundException, JSONException {
		JSONTokener readDic = new JSONTokener(new FileReader("dictionary.json"));
		JSONObject dataSet = new JSONObject(readDic);
	    return dataSet;
	    }
	
	// Making changes to dictionary file when word is to be added and deleted
	private void updateDic(JSONObject data) {
        FileWriter outputStream = null;
        try {
            outputStream = new FileWriter("dictionary.json");
            outputStream.write(data.toString());
            outputStream.flush();
            outputStream.close();
            System.out.println("Dictionary updated");
        }
        catch (IOException e) {
        	server.display.append("Error: Could not update/write to dictionary." + "\n");
            
        }
    }
}
