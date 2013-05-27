package ru.volterr.nam.gui.views;

import jade.core.AID;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ru.volterr.nam.agents.RouterAgent;
import ru.volterr.nam.behaviours.router.RouterPort;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Window.Type;

public class RouterGUI extends JFrame {

	private JPanel contentPane;

	private RouterAgent myAgent;
	private JTable portsTable;
	private JTable routeTable;
	private JLabel portsCnt;
	/**
	 * Create the frame.
	 */
	public RouterGUI(RouterAgent agent) {
		setType(Type.UTILITY);
		
		myAgent = agent;
		setTitle("Router " + agent.getLocalName());
		initComponents();
		
	}
	
	private void loadPorts() {
		portsCnt.setText(""+myAgent.ports.size());
		Iterator<RouterPort> iter = myAgent.ports.values().iterator();
		DefaultTableModel model = (DefaultTableModel)portsTable.getModel();
		//System.out.println("rows: "+model.getRowCount());
		/*for(int i = 0;i<model.getRowCount();i++)
			model.removeRow(0);*/
		model.getDataVector().removeAllElements();
		
		//System.out.println("rows:" + model.getRowCount() + "; in agent:" + myAgent.ports.size());
		while(iter.hasNext()){
			RouterPort port = iter.next();
			model.addRow(new Object[]{port.getLink().getA(), port.getLink().getZ(), port.getDrops()});
			 
		}
	}
	
	private void loadRoutes() {
		Iterator< Entry<AID, AID> > iter = myAgent.routetable.entrySet().iterator();
		DefaultTableModel model = (DefaultTableModel)routeTable.getModel();
		//System.out.println("rows: "+model.getRowCount());
		/*for(int i = 0;i<model.getRowCount();i++)
			model.removeRow(i);*/
		model.getDataVector().removeAllElements();
		while(iter.hasNext()){
			Entry<AID, AID> note = iter.next();
			model.addRow(new Object[]{note.getKey().getLocalName(),note.getValue().getLocalName()});
			 
		}
	}

	private void initComponents(){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 303, 451);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel lblPorts = new JLabel("Ports:");
		lblPorts.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JLabel lblRoutetable = new JLabel("RouteTable");
		lblRoutetable.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		portsCnt = new JLabel("0");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addComponent(lblPorts)
							.addPreferredGap(ComponentPlacement.RELATED, 174, Short.MAX_VALUE)
							.addComponent(portsCnt))
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblRoutetable, Alignment.LEADING))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPorts)
						.addComponent(portsCnt))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblRoutetable)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		routeTable = new JTable();
		routeTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Dest", "Next Hop"
			}
		));
		scrollPane_1.setViewportView(routeTable);
		
		portsTable = new JTable();
		portsTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Link A-point", "Link Z-point", "Drops"
			}
		));
		scrollPane.setViewportView(portsTable);
		contentPane.setLayout(gl_contentPane);
		
	}
	
	public synchronized void update(){
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      loadPorts();
		      loadRoutes();
		    }
		  });
	}
}
