package ru.volterr.nam.gui.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ReverseModeling extends JFrame {

	private JPanel contentPane;
	public JPanel linkspanel;
	public JPanel relaypanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReverseModeling frame = new ReverseModeling();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ReverseModeling() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 655, 447);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblLinksModelingResults = new JLabel("Links modeling results:");
		
		linkspanel = new JPanel();
		
		JLabel lblRelayModelingResults = new JLabel("Relay modeling results:");
		
		relaypanel = new JPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblLinksModelingResults)
					.addContainerGap(522, Short.MAX_VALUE))
				.addComponent(linkspanel, GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblRelayModelingResults)
					.addContainerGap())
				.addComponent(relaypanel, GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblLinksModelingResults)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(linkspanel, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblRelayModelingResults)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(relaypanel, GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
					.addContainerGap())
		);
		linkspanel.setLayout(new BorderLayout(0, 0));
		contentPane.setLayout(gl_contentPane);
	}

}
