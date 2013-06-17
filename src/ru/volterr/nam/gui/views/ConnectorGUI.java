package ru.volterr.nam.gui.views;

import jade.core.AID;
import jade.gui.GuiEvent;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.Connector;
import ru.volterr.nam.gui.model.CellTreeRenderer;
import ru.volterr.nam.model.Link;
import ru.volterr.nam.model.Node;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JButton;
import javax.swing.ToolTipManager;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;


public class ConnectorGUI extends JFrame{

	private Connector myAgent;
	
	private JPanel contentPane;
	private JButton btnTest;
	private JButton btnStart;
	private JTree tree;
		private DefaultMutableTreeNode usersNode;
		private DefaultMutableTreeNode routersNode;
		private DefaultMutableTreeNode serversNode;
	private JLabel lblResult;
		
	VisualizationViewer<Node,Link> vv;
	private Layout<Node,Link> layout;
	private JMenu modeMenu;
		
	private JPanel panel;
	private JMenuBar menuBar;
	private JTextField timeField;
	
	/**
	 * Create the frame.
	 */
	public ConnectorGUI(Connector a) {
		setTitle("Connector - Main Menu");
		
		myAgent = a;
		
		initGraph();
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
		
		btnStart.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GuiEvent ge = new GuiEvent(this, Constants.STARTMODELING_GUIEVENT);
				try{
					ge.addParameter( Long.decode(timeField.getText()) );
					myAgent.postGuiEvent(ge);				
				}catch(NumberFormatException e){
					timeField.setText("wrong number!");
				}
			}
			
		});
		
		MouseListener ml = new MouseAdapter() {
		     public void mousePressed(MouseEvent e) {
		         int selRow = tree.getRowForLocation(e.getX(), e.getY());
		         TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		         
		         if(selRow != -1) {
		             if(e.getClickCount() == 2) {
		            	 GuiEvent ge = new GuiEvent(this, Constants.SHOW_GUI_GUIEVENT);
		            	 String agentName = ((DefaultMutableTreeNode) selPath.getLastPathComponent()).toString();
		            	 //System.out.println(agentName);
		            	 ge.addParameter(agentName);
		            	 myAgent.postGuiEvent(ge);
		             }
		         }
		     }
		 };
		 tree.addMouseListener(ml);
		
	}

	private void initComponents(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1074, 492);
		
		menuBar = new JMenuBar();
		menuBar.add(modeMenu);
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		btnTest = new JButton("Test");
		
		lblResult = new JLabel("Result:");
		
		panel = new JPanel();
		
		tree = new JTree();
		tree.setFont(new Font("Tahoma", Font.PLAIN, 15));
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new CellTreeRenderer());
		tree.setModel(new DefaultTreeModel(
				new DefaultMutableTreeNode("Agents") {
					{
						usersNode = new DefaultMutableTreeNode("Users");
							usersNode.add(new DefaultMutableTreeNode("Alpha"));
							usersNode.add(new DefaultMutableTreeNode("Bravo"));
							usersNode.add(new DefaultMutableTreeNode("Charlie"));
						add(usersNode);
						serversNode = new DefaultMutableTreeNode("Servers");
							serversNode.add(new DefaultMutableTreeNode("FTP"));
							serversNode.add(new DefaultMutableTreeNode("SAMBA"));
							serversNode.add(new DefaultMutableTreeNode("HTTP"));
						add(serversNode);
						routersNode = new DefaultMutableTreeNode("Routers");
							routersNode.add(new DefaultMutableTreeNode("Aegis7"));
							routersNode.add(new DefaultMutableTreeNode("Gattaca"));
							routersNode.add(new DefaultMutableTreeNode("Sprawl"));
							routersNode.add(new DefaultMutableTreeNode("TauVolantis"));
							routersNode.add(new DefaultMutableTreeNode("Titan"));
						add(routersNode);
					}
				}
			));
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		
		btnStart = new JButton("START");
		
		timeField = new JTextField();
		timeField.setText("120");
		timeField.setColumns(10);
		
		JLabel lblExperimentTime = new JLabel("Experiment Time:");
		lblExperimentTime.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		JLabel lblS = new JLabel("s");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(tree, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE)
					.addGap(32)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addComponent(lblResult)
									.addComponent(btnTest))
								.addContainerGap(135, Short.MAX_VALUE))
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGap(4)
								.addComponent(lblExperimentTime)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(timeField, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblS)
								.addGap(31)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnStart)
							.addContainerGap())))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(tree, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(33)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblExperimentTime)
						.addComponent(timeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblS))
					.addGap(18)
					.addComponent(btnStart)
					.addPreferredGap(ComponentPlacement.RELATED, 179, Short.MAX_VALUE)
					.addComponent(btnTest)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblResult)
					.addGap(108))
		);
		
		panel.add(vv);
		contentPane.setLayout(gl_contentPane);
	}
	
	private void initGraph(){
		//Layout
		layout = new 
				KKLayout<Node, Link>(myAgent.graph);	//1
				//FRLayout<Node, Link>(myAgent.graph);  //2
				//FRLayout2<Node, Link>(myAgent.graph); //2
				//ISOMLayout<Node, Link>(myAgent.graph); //not working
				//DAGLayout<Node, Link>(myAgent.graph);	//1
		layout.setSize(new Dimension(550,350));
		
		
		//visualization Component
		vv = new VisualizationViewer<Node, Link>(layout);
		vv.setPreferredSize(new Dimension(600,400));
		
		//panel.add(vv);
		//icons
		final Icon router_redicon = new ImageIcon("res/images/router_red.png");
		final Icon router_blueicon = new ImageIcon("res/images/router_blue.png");
		final Icon usericon = new ImageIcon("res/images/user.png");
		final Icon servericon = new ImageIcon("res/images/server.png");
		Transformer<Node, Icon> vertexIconTransformer =	new Transformer<Node, Icon>() {
			@Override
			public Icon transform(Node node) {
				//System.out.println(node.getType());
				switch(node.getType()){
				case Node.ROUTER_TYPE:
					switch(node.getStatus()){
						case Node.STATUS_UP:return router_blueicon;
						case Node.STATUS_DOWN:return router_redicon;
					}
				case Node.USER_TYPE:return usericon;
				case Node.SERVER_TYPE:return servericon;
				}
				return null;
			}
		};
		
		// Show vertex and edge labels
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setEdgeLabelTransformer(new ToBandwidthLabeller());
		vv.getRenderContext().setVertexIconTransformer(vertexIconTransformer);
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Node, Link>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.S);
		
		//repaint every 1000 ms
		vv.repaint(1000);
		// Create a graph mouse and add it to the visualization component
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		//gm.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);
		vv.addKeyListener(gm.getModeKeyListener());
		modeMenu = gm.getModeMenu();
		modeMenu.setText("Mouse Mode");
		modeMenu.setIcon(null); // I'm using this in a main menu
		modeMenu.setPreferredSize(new Dimension(80,20)); // Change the size
		
		
	}

	public synchronized void testresult(final String str){
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      lblResult.setText("Result: " + str);
		    }
		  });
	}
	
	public synchronized void graphRepaint(){
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      vv.repaint();
		    }
		  });
	}
}
