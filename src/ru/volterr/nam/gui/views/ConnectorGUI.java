package ru.volterr.nam.gui.views;

import jade.gui.GuiEvent;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.Connector;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ConnectorGUI extends JFrame{

	private JPanel contentPane;
	private JButton btnAddRouter,btnAddUser,btnTest;
	private JTree tree;
	JLabel lblResult;
	
	private Connector myAgent;
	
	/**
	 * Create the frame.
	 */
	public ConnectorGUI(Connector a) {
		
		myAgent = a;
		
		initComponents();
		initListeners();
	}
	
	private void initListeners() {
		
		btnTest.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GuiEvent ge = new GuiEvent(this, Constants.TEST_GUIEVENT);
				myAgent.postGuiEvent(ge);
			}
			
		});
		
	}

	private void initComponents(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 641, 493);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		
		btnAddRouter = new JButton("Add Router");
		btnAddUser = new JButton("Add User");
		btnTest = new JButton("Test");
		
		lblResult = new JLabel("Result:");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(34)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(btnAddRouter)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnAddUser))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(35)
							.addComponent(btnTest)
							.addGap(18)
							.addComponent(lblResult)))
					.addContainerGap(411, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(23)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAddRouter)
						.addComponent(btnAddUser))
					.addGap(16)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnTest)
						.addComponent(lblResult))
					.addContainerGap(89, Short.MAX_VALUE))
		);
		
		tree = new JTree();
		scrollPane.setViewportView(tree);
		contentPane.setLayout(gl_contentPane);
	}

	public synchronized void testresult(final String str){
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      // Here, we can safely update the GUI
		      // because we'll be called from the
		      // event dispatch thread
		      lblResult.setText("Result: " + str);
		    }
		  });
	}
	
}
