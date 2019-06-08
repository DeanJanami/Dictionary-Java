/*
 * Dean Pakravan: 757389
 * Submission Date: 6th September 2018
 * Distributed Systems: Assignment 1
 * Multi-threaded dictionary: GUIServer class
 * 
 * Purpose of code: Provides a GUI for the DictionaryServer.java
 */

package Server;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUIServer {

	private JFrame frame;
	JTextArea textArea;

	public GUIServer(JTextArea textArea) {
		this.textArea = textArea;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().setBackground(Color.getHSBColor(0.394F, 0.86F, 0.3F));
		
		JButton btnNewButton = new JButton("Exit");
		btnNewButton.setBounds(410, 10, 70, 21);
		frame.getContentPane().add(btnNewButton);
		
	    JLabel textBox = new JLabel("Dictionary Server");
        textBox.setBounds(2, 2, 100, 21);
        textBox.setForeground(Color.getHSBColor(0.505F, 0.81F, 0.94F));
        frame.add(textBox);
        
        JLabel maker = new JLabel(" By: Dean Pakravan 757389");
        maker.setBounds(5, 530, 170, 21);
        maker.setForeground(Color.getHSBColor(0.505F, 0.81F, 0.955F));
        frame.add(maker);
		
		//This actual text area		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(11, 36, 460, 490);//(2, 28, 400, 433);
		frame.getContentPane().add(scrollPane);
		
		scrollPane.setViewportView(textArea);
		textArea.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		textArea.setEditable(false);

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(1);
			}
		});
	}

}
