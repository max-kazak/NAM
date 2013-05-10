package ru.volterr.nam.agents;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import ru.volterr.nam.Pair;
import ru.volterr.nam.behaviours.connector.ConnNewLink;
import ru.volterr.nam.behaviours.connector.ConnRecvMsg;
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
	
	private Map<AIDPair, List<AID>> routes = new HashMap<AIDPair, List<AID>>(); //Map< Pair<src,dest>, List<PE> >
	private ConnectorGUI  gui;
	
	public Connector(){
		super();
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
		
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
			break;
		}
		
	}
	
	private AID addRouter(String name){
		AgentController a;
		Object [] args = null;
		
		try {
			a = containercontr.createNewAgent(name, "ru.volterr.nam.agents.RouterAgent", args);
			a.start();
			return new AID(name,AID.ISLOCALNAME);
		} catch (StaleProxyException e) {
			log.log(Logger.SEVERE,"Create new agent exception:",e);
		}
		return null;	
	}
	private AID addRouter(AID agentid){
		return addRouter(agentid.getLocalName());
	}
	
	private void addUser(String name, String gateway, List<AID> subscribers, List<AID> subscriptions){
		AgentController a;
		Object [] args;
		
		args = new Object[3];
		args[0]=new AID(gateway,AID.ISLOCALNAME);
		args[1]=subscribers;
		args[2]=subscriptions;
		
		try {
			a = containercontr.createNewAgent(name, "ru.volterr.nam.agents.UserAgent", args);
			a.start();
		} catch (StaleProxyException e) {
			log.log(Logger.SEVERE,"Create new agent exception:",e);
		}
	}	
	private void addUser(AID agentid, String gateway, List<AID> subscribers, List<AID> subscriptions){
		addUser(agentid.getLocalName(), gateway, subscribers, subscriptions);
	}
	private void addUser(AID agentid, AID gatewayid, List<AID> subscribers, List<AID> subscriptions){
		addUser(agentid.getLocalName(), gatewayid.getLocalName(), subscribers, subscriptions);
	}
	
	private void addLink(Link link){
		addBehaviour(new ConnNewLink(link));
	}
	
	public AID getNextRouteHop(AID requester, AID src, AID dest){
		log.log(Logger.INFO,getLocalName() + "#getting next route hop for " + requester.getLocalName() + " in path (" + src.getLocalName() + ", " + dest.getLocalName() + ").");
		
		ArrayList<AID> route = (ArrayList<AID>) routes.get(new AIDPair(src,dest));
		
		if(route != null){
			int i = route.lastIndexOf(requester);
		
			if(i != -1){
				log.log(Logger.INFO, getLocalName() + "#" + "found route for " + requester.getLocalName() + " for path (" + src.getLocalName() + ", " + dest.getLocalName() + "). Next hop is " + route.get(i+1).getLocalName() );
				return route.get(i+1);
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
		AID gr = addRouter("Gattaca");
		AID sr = addRouter("Sprawl");
		AID ar = addRouter("Aegis7");
		
		//users
		AID a = new AID("Alpha",AID.ISLOCALNAME),
			b = new AID("Bravo",AID.ISLOCALNAME),
			c = new AID("Charlie",AID.ISLOCALNAME);
		addUser(a,"Gattaca",Arrays.asList(b,c),null);
		addUser(b,"Aegis7",Arrays.asList(c),null);
		addUser(c,"Sprawl",Arrays.asList(a),null);
		
		//nodes
		Node alpha = new Node(a),
			 bravo = new Node(b),
			 charlie = new Node(c),
			 gattaca = new Node(gr),
			 sprawl = new Node(sr),
			 aegis = new Node(ar);
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
		routes.put(  new AIDPair(a,b),new ArrayList<AID>( Arrays.asList(a,gr,ar,b) )  );
		routes.put(  new AIDPair(a,c),new ArrayList<AID>( Arrays.asList(a,gr,sr,c) )  );
		routes.put(  new AIDPair(b,a),new ArrayList<AID>( Arrays.asList(b,ar,gr,a) )  );
		routes.put(  new AIDPair(b,c),new ArrayList<AID>( Arrays.asList(b,ar,sr,c) )  );
		routes.put(  new AIDPair(c,b),new ArrayList<AID>( Arrays.asList(c,sr,ar,b) )  );
		routes.put(  new AIDPair(c,a),new ArrayList<AID>( Arrays.asList(c,sr,gr,a) )  );
		
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
