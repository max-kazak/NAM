package ru.volterr.nam.gui.views;

import jade.core.AID;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepRenderer;


import ru.volterr.nam.agents.UserAgent;


import java.awt.Window.Type;

public class UserGUI extends JFrame {

	private JPanel contentPane;
	private JTable subscrTable;

	private UserAgent myAgent;
	private JLabel lblGateway;
	private JLabel lblGateway_1;
	private JPanel chartpanel;
	
	public UserGUI(UserAgent agent) {
		setType(Type.UTILITY);
		myAgent = agent;
		setTitle("User " + agent.getLocalName());
		
		initComponents();
	}

	private void loadSubscribers() {
		myAgent.getReceivers();
		DefaultTableModel model = (DefaultTableModel)subscrTable.getModel();
		//System.out.println("rows: "+model.getRowCount());
		/*for(int i = 0;i<model.getRowCount();i++)
			model.removeRow(i);*/
		model.getDataVector().removeAllElements();
		for(AID sub:myAgent.getReceivers())
			model.addRow(new Object[]{sub.getLocalName()});
			 
	}
	
	private void initComponents(){
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 914, 496);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		lblGateway = new JLabel("");
		lblGateway.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JScrollPane scrollPane = new JScrollPane();
		
		JLabel lblSubscribers = new JLabel("Receivers:");
		lblSubscribers.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		lblGateway_1 = new JLabel("Gateway: ");
		lblGateway_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		
		chartpanel = new JPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSubscribers)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblGateway_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblGateway))
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
					.addGap(10)
					.addComponent(chartpanel, GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblGateway_1)
						.addComponent(lblGateway))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSubscribers)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(18, Short.MAX_VALUE))
				.addComponent(chartpanel, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
		);
		
		subscrTable = new JTable();
		subscrTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Name"
			}
		));
		scrollPane.setViewportView(subscrTable);
		contentPane.setLayout(gl_contentPane);
	}
	
	public synchronized void update(){
			
			SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
			      lblGateway.setText(myAgent.getGateway().getLocalName()) ;
			      loadSubscribers();
			    }
			  });
	}
	
	public synchronized void drawgraph(){
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      graphcreate();
		    }
		  });
	}

	private void graphcreate(){
		JFreeChart chart = ChartFactory.createXYLineChart(
                "Intensity change",
                "days", "intense",
                myAgent.dataset,
                PlotOrientation.VERTICAL,
                false,   // legend
                true,   // tooltips
                false   // urls
            );
		
		
		
		chart.setBackgroundPaint(new Color(216, 216, 216));
        final XYPlot plot = chart.getXYPlot();
        //plot.getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
        
        XYStepRenderer renderer = new XYStepRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
//        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        renderer.setDefaultEntityRadius(6);
        plot.setRenderer(renderer);
        
        ChartPanel chartp = new ChartPanel(chart);
        JScrollPane scrPane = new JScrollPane(chartp);
        
        chartpanel.add(scrPane);
	}
}
