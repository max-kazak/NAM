package ru.volterr.nam.agents;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.SortedSparseMultigraph;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import ru.volterr.nam.Pair;
import ru.volterr.nam.behaviours.connector.ConnNewLink;
import ru.volterr.nam.behaviours.connector.ConnRecvMsg;
import ru.volterr.nam.gui.model.ByNameLinkComparator;
import ru.volterr.nam.gui.model.ByNameVetexComparator;
import ru.volterr.nam.gui.views.ConnectorGUI;
import ru.volterr.nam.model.Link;
import ru.volterr.nam.model.Node;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;


public class Connector extends GuiAgent {
	
	private Logger log;
	private AgentContainer containercontr;
	
	private Map<AIDPair, List<Node>> routes = new HashMap<AIDPair, List<Node>>(); //Map< Pair<src,dest>, List<PE> >
	public SortedSparseMultigraph<Node, Link> graph;
	private ConnectorGUI  gui;
	
	public Connector(){
		super();
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
		//init graph
		Comparator<Node> vcomp = new ByNameVetexComparator();
		Comparator<Link> lcomp = new ByNameLinkComparator();
		graph = new SortedSparseMultigraph<Node, Link>(vcomp,lcomp);
		//init gui
		gui = new ConnectorGUI(this);
	}
	
	public void setup(){
		log.log(Logger.INFO, "Connector under the name " + getLocalName() + " has been launched");

		containercontr = getContainerController();
        
		// Register the connector service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Connector");
		sd.setName(getLocalName()+" Connector");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}catch (FIPAException fe) {
			log.log(Logger.SEVERE,"FIPAException:",fe);
		}
		
		//GUI
		gui.setVisible(true);
		
		//test example
		example();
		
		//behaviours
		addBehaviour(new ConnRecvMsg());
	}
	
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			log.log(Logger.SEVERE,"FIPAException:",fe);
		}
		
	}

	@Override
	protected void onGuiEvent(GuiEvent ev) {
		switch(ev.getType()){
		case Constants.TEST_GUIEVENT:
			testguimethod();
			//example();
			break;
		}
		
	}
	
	private Node addRouter(AID agentid){
		AgentController a;
		Object [] args = null;
		Node node = new Node(agentid,Node.ROUTER_TYPE);
		
		try {
			a = containercontr.createNewAgent(agentid.getLocalName(), "ru.volterr.nam.agents.RouterAgent", args);
			a.start();
			graph.addVertex(node);
			gui.graphRepaint();
			return node;
		} catch (StaleProxyException e) {
			log.log(Logger.SEVERE,"Create new agent exception:",e);
		}
		return null;	
	}
		
	private Node addUser(AID agentid, String gateway, List<AID> subscribers, List<AID> subscriptions){
		AgentController a;
		Object [] args;
		
		args = new Object[3];
		args[0]=new AID(gateway,AID.ISLOCALNAME);
		args[1]=subscribers;
		args[2]=subscriptions;
		
		Node node = new Node(agentid,Node.USER_TYPE);
		
		try {
			a = containercontr.createNewAgent(agentid.getLocalName(), "ru.volterr.nam.agents.UserAgent", args);
			a.start();
			graph.addVertex(node);
			gui.graphRepaint();
			return node;
		} catch (StaleProxyException e) {
			log.log(Logger.SEVERE,"Create new agent exception:",e);
		}
		return null;
	}	
	private Node addUser(AID agentid, AID gatewayid, List<AID> subscribers, List<AID> subscriptions){
		return addUser(agentid, gatewayid.getLocalName(), subscribers, subscriptions);
	}
	
	private void addLink(Link link){
		addBehaviour(new ConnNewLink(link));
		graph.addEdge(link, link.getA(), link.getZ(), link.getType());
		gui.graphRepaint();
	}
	
	public AID getNextRouteHop(AID requester, AID src, AID dest){
		log.log(Logger.INFO,getLocalName() + "#getting next route hop for " + requester.getName() + " in path (" + src.getLocalName() + ", " + dest.getLocalName() + ").");
		
		ArrayList<Node> route = (ArrayList<Node>) routes.get(new AIDPair(src,dest));
		
		if(route != null){
			
			int i = route.lastIndexOf(new Node(requester,Node.ROUTER_TYPE));
		
			if(i != -1){
				log.log(Logger.INFO, getLocalName() + "#" + "found route for " + requester.getName() + " for path (" + src.getLocalName() + ", " + dest.getLocalName() + "). Next hop is " + route.get(i+1).getName() );
				return route.get(i+1).getId();
			}
			else{
				log.log(Logger.WARNING,getLocalName() + "#" + requester.getLocalName() + " isn't in the path (" + src.getLocalName() + ", " + dest.getLocalName() + "). Can't return next hop");
				return null;
			}
		}else{
			log.log(Logger.WARNING,getLocalName() + "#can't find route for path (" + src.getLocalName() + ", " + dest.getLocalName() + ").");
			return null;
		}
			
		
	}
	
	private void example(){
		
		//routers
		Node gattaca = addRouter(new AID("Gattaca",AID.ISLOCALNAME)),
			 sprawl = addRouter(new AID("Sprawl",AID.ISLOCALNAME)),
			 aegis = addRouter(new AID("Aegis7",AID.ISLOCALNAME));
		//users
		AID a = new AID("Alpha",AID.ISLOCALNAME),
			b = new AID("Bravo",AID.ISLOCALNAME),
			c = new AID("Charlie",AID.ISLOCALNAME);
		Node alpha = addUser(a,gattaca.getId(),new ArrayList<AID>(Arrays.asList(a,b)),null),
			 bravo = addUser(b,aegis.getId(),new ArrayList<AID>(Arrays.asList(c)),null),
			 charlie = addUser(c,sprawl.getId(),new ArrayList<AID>(Arrays.asList(a)),null);
		//links
		Link alpha_gattaca = new Link(alpha, gattaca, 1000),
			 bravo_aegis = new Link(bravo, aegis, 1000),
			 charlie_sprawl = new Link(charlie, sprawl, 1000),
			 gattaca_aegis = new Link(gattaca, aegis, 1000),
			 gattaca_sprawl = new Link(gattaca, sprawl, 2000),
			 sprawl_aegis = new Link(sprawl, aegis, 500);
		
		addLink(alpha_gattaca);
		addLink(bravo_aegis);
		addLink(charlie_sprawl);
		addLink(gattaca_aegis);
		addLink(gattaca_sprawl);
		addLink(sprawl_aegis);
		
		//routes
		routes.put(  new AIDPair(a,b),new ArrayList<Node>( Arrays.asList(alpha,gattaca,aegis,bravo) )  );
		routes.put(  new AIDPair(a,c),new ArrayList<Node>( Arrays.asList(alpha,gattaca,sprawl,charlie) )  );
		routes.put(  new AIDPair(b,a),new ArrayList<Node>( Arrays.asList(bravo,aegis,gattaca,alpha) )  );
		routes.put(  new AIDPair(b,c),new ArrayList<Node>( Arrays.asList(bravo,aegis,sprawl,charlie) )  );
		routes.put(  new AIDPair(c,b),new ArrayList<Node>( Arrays.asList(charlie,sprawl,aegis,bravo) )  );
		routes.put(  new AIDPair(c,a),new ArrayList<Node>( Arrays.asList(charlie,sprawl,gattaca,alpha) )  );
		
		//log.log(Logger.INFO,getLocalName() + "#example topology created:" + 
		//					"\nRouters: " + gr.getLocalName() + ", " + sr.getLocalName() + ", " + ar.getLocalName() + 
		//					"\nUsers: " + a.getLocalName() + ", " + b.getLocalName() + ", " + c.getLocalName() + 
		//					"\nroutes: " + routes.toString());
		
		
	}
	
	public void testguimethod(){
		System.out.println("Cought event from Connector GUI");
		gui.testresult("OK");
	}
}
