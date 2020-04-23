/** Student Name - SANJANA RATAN
    Student ID - 1041413
    
    This class provides a GUI for the server to accept client connections and
    create threads for processing the operations requested by each client.
    It executes two different types of architecture - worker pool and thread per connection
    depending on the command line argument.
    If the argument is true, it executes worker pool architecture with 3 threads.
    Else it executes thread per connection architecture.
 */

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class Server {
	private static ExecutorService pool;
	private JFrame frame;
	private JScrollPane scrollPane;
	public JTextArea display;
	private JButton closeButton;
	private static String f;

	public static void main(String[] args) {
		
		Server s = new Server();
		// initialize the GUI application window
		s.initialize();
		
		// store the command line argument in a variable
		f = args[0];
		
		try {
			
			@SuppressWarnings("resource")
			// create Server Socket
			ServerSocket server = new ServerSocket(1236);
			// once socket is created, server starts running
			System.out.println("Server is running..");
			s.display.append("Server is running. Waiting for connection (PortNum: 1236)"+"\n");
			
			
			// initialize a counter to keep track of clients connected
			int i=0;
			
			// initialize a Thread Pool of 3 threads if argument is true
			if(f.equals("true")) {
				pool = Executors.newFixedThreadPool(3);
			}
			
			while(true){
			// accept client connection and increment counter
			Socket socket = server.accept();
			i++;
			
			s.display.append("********************"+"\n");
			s.display.append("Client connection: "+i+"\n");
			// create a new thread of every client connection and perform operations
			
			// if argument is true then use worker pool architecture
			// the application hangs if more than 3 clients connect as the number of threads 
			// that the server can handle is 3 at a time. 
			if(f.equals("true")) {
					Runnable run = new ThreadServer(socket,i,s);
					pool.execute(run);
			}
			// if argument is false use thread per connection architecture
			else {
				Thread t = new Thread(new ThreadServer(socket, i, s));
			    t.start();
			}
			
		  }
		}
		 catch(IOException e) {
			// if server cannot read client's data it shows an error
			 JOptionPane.showMessageDialog(null,
	                    "Error setting up socket.", "Error",
	                    JOptionPane.PLAIN_MESSAGE);
	         System.out.println("Error setting up socket.");
		 } 
	}
	
	// this function consists of GUI components
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 643, 484);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(54, 48, 535, 324);
		frame.getContentPane().add(scrollPane);
		
		display = new JTextArea();
		scrollPane.setViewportView(display);
		
		closeButton = new JButton("CLOSE");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when close button is clicked, server is closed
				int n = JOptionPane.showConfirmDialog(null,
                        "Do you really want to exit?", "Confirm",
                        JOptionPane.YES_NO_OPTION);
                if (n == 0) {
                    System.out.println("Server is offline.");
                    System.exit(0);
                }
            }
		});
		closeButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		closeButton.setBounds(227, 383, 119, 53);
		frame.getContentPane().add(closeButton);
		
		frame.setVisible(true);
	}
}

