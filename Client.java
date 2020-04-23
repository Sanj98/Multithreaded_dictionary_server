/** Student Name - SANJANA RATAN
    Student ID - 1041413
    
    This class provides a GUI for the client to perform the 3 operations -
    query, add and delete. It consists of input/output streams to send and receive data
    to and from the server. It calls 3 methods for different buttons clicked - 
    searchWord, addWord and deleteWord. 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {
	private JFrame frame;
	private JButton Search;
	private JButton Add;
	private JButton Delete;
	private JTextField wordTF;
	private JTextArea meaningTF;
	private JLabel label1;
	private JLabel label2;
	String word;
	String meaning;
	
	public static void main(String[] args) {
		Socket socket = null;
		Client ob= new Client();
		
		try{
		// Create a socket
			socket = new Socket("localhost", 1236);	
		}
		catch(IOException e) {
			e.printStackTrace();
		}	
		// Initialize the GUI application window
		ob.initialize(socket);
	}

	// consists of the GUI components
	void initialize(Socket socket) {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.ORANGE);
		frame.getContentPane().setLayout(null);
		
		Search = new JButton("SEARCH");
		Search.setFont(new Font("Tahoma", Font.PLAIN, 18));
		Search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		// when SEARCH button is clicked searchWord function is called
				searchWord(socket);
			}
		});
		Search.setBounds(50, 331, 111, 52);
		frame.getContentPane().add(Search);
		
		Add = new JButton("ADD");
		Add.setFont(new Font("Tahoma", Font.PLAIN, 18));
		Add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		// when ADD button is clicked addWord function is called
				addWord(socket);
			}
		});
		Add.setBounds(263, 334, 111, 52);
		frame.getContentPane().add(Add);
		
		Delete = new JButton("DELETE");
		Delete.setFont(new Font("Tahoma", Font.PLAIN, 18));
		Delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		// when DELETE button is clicked deleteWord function is called
				deleteWord(socket);
			}
		});
		Delete.setBounds(477, 331, 111, 52);
		frame.getContentPane().add(Delete);
		
		label1 = new JLabel("WORD : ");
		label1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		label1.setBounds(153, 81, 86, 29);
		frame.getContentPane().add(label1);
		
		label2= new JLabel("MEANING(s) : ");
		label2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		label2.setBounds(103, 164, 140, 93);
		frame.getContentPane().add(label2);
		
		wordTF = new JTextField();
		wordTF.setBounds(238, 76, 331, 65);
		frame.getContentPane().add(wordTF);
		
		meaningTF = new JTextArea();
		meaningTF.setBounds(238, 152, 331, 140);
		meaningTF.setLineWrap(true);
		meaningTF.setText("Add multiple meanings in this format - \n meaning 1;meaning 2;meaning3;");
		frame.getContentPane().add(meaningTF);
		frame.setBounds(100, 100, 662, 497);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);			
	}
	
	// Search button operation
	public void searchWord(Socket socket) {
		// get the word typed by user
		word = wordTF.getText();
        if (!(word.equals(""))) {
        // send word to server
            String clientMsg = "Search " + word + "\n";
            if (outputStream(clientMsg, socket)) {
                BufferedReader input = null;
                try {
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                            "UTF-8"));
                    meaningTF.setText("");
                   // storing meaning received from server
                    String received = input.readLine();
                  // if the word is not in the dictionary, server replies with "Word not found" message
                  // client displays error because word is not found in dictionary
                    if(received.equals("Word not found")) {
                    	JOptionPane.showMessageDialog(null,
                                "Word not found", "Error",
                                JOptionPane.PLAIN_MESSAGE);
                    	                   
                    }
                    else {
                    // if the word is in the dictionary, server replies with the meaning
                    // client displays the meaning to user
                    	showMeaning(received);
                    }
                }
                catch (IOException e) {
                    noConnectionError();
                }
            }
        }
	}
	
	// Add button operation
	public void addWord(Socket socket) {
		// get the word and meaning from user
		word = wordTF.getText();
		meaning = meaningTF.getText();
		if (!(word.equals(""))) {
			// if user does not type the meaning, prompt user to "Enter meaning"
			if(meaning.equals("")) {
				JOptionPane.showMessageDialog(null,
                        "Please enter the meaning", "Error",
                        JOptionPane.PLAIN_MESSAGE);
			}
			// if user does not end meaning with semicolon, prompt user to "Add ;"
			else if (!(meaning.contains(";")) ||
                    (meaning.lastIndexOf(";") !=
                            meaning.length() - 1)) {
                JOptionPane.showMessageDialog(null,
                        "Please enter (;) at the end",
                        "Error", JOptionPane.PLAIN_MESSAGE);
            }
			// send the word with meaning to the server
			else {
				String clientMsg = "Add " + word + " " + meaning + "\n" ;
				meaningTF.setText("");
				if(outputStream(clientMsg, socket)){
					BufferedReader input = null;
	            	try {
	            		input = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
	            	// display a message to user that dictionary has been updated
	            		JOptionPane.showMessageDialog(null, input.readLine(), "Update",JOptionPane.PLAIN_MESSAGE);	
	            	}
	            	catch(IOException e) {
	            		noConnectionError();
	            	}	
	            }
			}
		}
	}
	
	// Delete button operation
	public void deleteWord(Socket socket) {
		// get the word from user
		word = wordTF.getText();
		if (!(word.equals(""))) {
			// send word to server
			String clientMsg = "Delete " + word + "\n";
			if(outputStream(clientMsg, socket)){
				BufferedReader input = null;
				try {
					input = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
					// storing message received from server
					String deletedMsg = input.readLine();
					// if word is not in the dictionary, server replies with "Word does not exist"
					// else server replies with "Deleted word"
					// this message from the server is shown to user
                    JOptionPane.showMessageDialog(null, deletedMsg, "Update", JOptionPane.PLAIN_MESSAGE);
                }
				catch(IOException e) {
					noConnectionError();
				}
            }
		}	
	}
	
	// this function displays the meaning to user in the TextArea
	private void showMeaning(String input) {
        StringTokenizer meaning = new StringTokenizer(input, ";");
        int count = 1;
        while (meaning.hasMoreTokens()) {
        	meaningTF.append(count + ". " + meaning.nextToken() + "\n");
           count ++;
       }    
    }
	
	// this function handles input/output exception
	// shows an error if the client failed to send/receive data to/from server
	private void noConnectionError() {
	        JOptionPane.showMessageDialog(null,
	                "Error communicating with Server.", "Error",
	                JOptionPane.PLAIN_MESSAGE);
	}
	
	// this function creates an OutputStream and sends clients data to server
	private boolean outputStream(String message, Socket socket) {
	        try {
	        	 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
	                     socket.getOutputStream(), "UTF-8"));
	            out.write(message);
	            out.flush();
	            return true;
	        }
	        catch (IOException e) {
	            noConnectionError();
	            return false;
	        }
	}
}
