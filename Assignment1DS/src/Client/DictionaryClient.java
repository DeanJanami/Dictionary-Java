/*
 * Dean Pakravan: 757389
 * Submission Date: 6th September 2018
 * Distributed Systems: Assignment 1
 * Multi-threaded dictionary: Client class
 * 
 * Purpose of code: A client to interact with the Dictionary server
 */

package Client;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class DictionaryClient extends JFrame implements ActionListener{
	
	// Serial ID since we extend JFrame
	private static final long serialVersionUID = 1587106369410734446L;
	// Text Areas
	JTextArea readArea;
    JTextField writeArea;
	JScrollPane scrollPane;
    JLabel textBox;
    // Buttons
    JButton btnQuery;
    JButton btnAdd;
    JButton btnDelete;
    JButton btnExit;
    // Socket to connect to server
    Socket socket;

    public DictionaryClient(String[] args) {
    	// Prepare the GUI
    	Container contentPane = getContentPane();
        this.setSize(500, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
		contentPane.setBackground(Color.getHSBColor(0.567F, 0.96F, 0.1632F));
        
		// Label next to text box
        textBox = new JLabel("To server: ");
        textBox.setBounds(5, 30, 70, 21);
        textBox.setForeground(Color.WHITE);
        add(textBox);
        
        JLabel label = new JLabel("Dictionary Client");
        label.setBounds(180, 5, 150, 21);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Times New Roman", Font.BOLD, 18));
        add(label);
        
        JLabel usage = new JLabel("Usage: Enter the word in the box and press desired operation");
        usage.setBounds(5, 85, 470, 21);
        usage.setForeground(Color.WHITE);
        add(usage);
        
        
        JLabel info = new JLabel("Seperate additional meanings with \";\". "
        		+ "No spaces between different meanings");
        info.setBounds(5, 100, 470, 21);
        info.setForeground(Color.WHITE);
        add(info);
        
        JLabel info2 = new JLabel("E.g: word meaning1;meaning2;meaning3");
        info2.setBounds(5, 115, 470, 21);
        info2.setForeground(Color.WHITE);
        add(info2);
        
        JLabel maker = new JLabel(" By: Dean Pakravan 757389");
        maker.setBounds(5, 525, 170, 21);
        maker.setForeground(Color.WHITE);
        add(maker);
        
        // The text box to type message to the server
        writeArea = new JTextField();
        writeArea.setBounds(65, 30, 330, 21);
        add(writeArea);

        // Query a word (button)
        btnQuery = new JButton("Query");
        btnQuery.setBounds(65, 60, 70, 21);
        btnQuery.addActionListener(this);
        add(btnQuery);
        
        // Add a new word (button)
        btnAdd = new JButton("Add");
        btnAdd.setBounds(200, 60, 70, 21);
        btnAdd.addActionListener(this);
        add(btnAdd);
        
        // Delete a word (button)
        btnDelete = new JButton("Delete");
        btnDelete.setBounds(330, 60, 70, 21);
        btnDelete.addActionListener(this);
        add(btnDelete);
        
        // Close the client
        btnExit = new JButton("Exit");
        btnExit.setBounds(390, 525, 70, 21);
        btnExit.addActionListener(this);
        add(btnExit);

        // Add read text area to scroll pane
        // User cannot write in this box
        readArea = new JTextArea();
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 140, 460, 380);
		add(scrollPane);
		
		scrollPane.setViewportView(readArea);
		readArea.setEditable(false);
        readArea.setFont(new Font("Times New Roman", Font.PLAIN, 15));


        this.setVisible(true);

        // Start the client
		connectServer(args);
    }
	

	public static void main(String[] args) throws IOException{
		new DictionaryClient(args);		

	}
	private void connectServer(String[] args) {
		// Check that command line arguments exist
		if (args.length < 2) {
			readArea.append("Usage: java TCPClient "
					+ "<ServerAddress> <ServerPort>");
			System.out.println("Usage: java TCPClient "
					+ "<ServerAddress> <ServerPort>");
			System.exit(1);
		}
		try {
			int portNo = Integer.valueOf(args[1]).intValue();
			//Type "localhost" if not a specific IP address
			InetAddress serverAdd = InetAddress.getByName(args[0]);
			socket = new Socket(serverAdd, portNo);
			readArea.append("Connection established" + "\n");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch(SocketException e) {
			readArea.append("Socket: " + e.getMessage() +"\n");
			readArea.append("Server must be listening before starting client" + "\n");
			readArea.append("Press \"Exit\" and try again");
		}
		catch(IOException e) {
			readArea.append("IO: " + e.getMessage()+ "\n");
		}
	}
	
	// Method to perform an action after a button has been clicked
	@Override
	public void actionPerformed(ActionEvent e) {	
		 Boolean query = false, add = false, delete = false;
		 // Check which button has been pressed
		 if (e.getSource().equals(btnQuery)) {
			 query = true;
		 }
		 else if (e.getSource().equals(btnAdd)) {
		     add = true;
		 }
		 else if (e.getSource().equals(btnDelete)) {
		 	 delete = true;
		 }
		 else if (e.getSource().equals(btnExit)) {
			 System.exit(1);
		 }
		 try {
			 writeMsg(query, add, delete);
		 } catch (IOException e1) {
			 readArea.append("Error writing/retrieving response from server" + "\n");
		 }
	 }

	// Method to write our message to the server
	private void writeMsg(Boolean query, Boolean add, Boolean delete) throws IOException{
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), "UTF-8"));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), "UTF-8"));		
			String inputStr = writeArea.getText();
			if ("".equals(inputStr)) {
				readArea.append("Please enter a word into the text field" + "\n");
			}
			else {
				if (query) {
					out.write("query " + inputStr + "\n");
					out.flush();
					multipleMeanings(in);
				} else if (add) {
					out.write("add " + inputStr + "\n");
					out.flush();
					receive(in);
				} else if (delete) {
					out.write("delete " + inputStr + "\n");
					out.flush();
					receive(in);
				}
			}
		} catch(SocketException e) {
			readArea.append("Socket: " + e.getMessage() +"\n");
			readArea.append("Cannot reach the server. Please exit and reconnect" + "\n");
		}
	}
	
	/* Method to display multiple meanings if the 
	 client queries a word that has more than one meaning */
	private void multipleMeanings(BufferedReader in) {
		try {
			readArea.append("Message sent" + "\n");
			
			/* Since read accepts the ASCII value of the charecter,
			* minus 48 to get the interger value
			  This value is the number of meanings the printed word will have */
			int numMeanings = in.read()-48;

			for (int i = 0; i < numMeanings+1; i ++) {
				String accept = in.readLine();
				readArea.append(accept + "\n");
			}
			
		} catch (IOException e) {
			readArea.append("Error retriving response from server" + "\n");
		} 
		
	}
	
	private void receive(BufferedReader in) {
		try {
			readArea.append("Message sent" + "\n");
			String accept = in.readLine();
			readArea.append("Message received: " + accept + "\n");
		} catch (IOException e) {
			readArea.append("Error retriving response from server" + "\n");
		}
	}
}