package ru.volterr.nam.agents;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.SortedSparseMultigraph;

import ru.volterr.nam.AIDPair;
import ru.volterr.nam.Constants;
import ru.volterr.nam.Pair;
import ru.volterr.nam.behaviours.connector.ConnNewLink;
import ru.volterr.nam.behaviours.connector.ConnRecvMsg;
import ru.volterr.nam.gui.model.BandTransformer;
import ru.volterr.nam.gui.model.ByNameLinkComparator;
import ru.volterr.nam.gui.model.ByNameVetexComparator;
import ru.volterr.nam.gui.views.ConnectorGUI;
import ru.volterr.nam.model.Link;
import ru.volterr.nam.model.Node;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
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
	private Map<AID,Node> nodes = new HashMap<AID,Node>();
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
		addBehaviour(new ConnRecvMsg(this));
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

	private void startModeling(Long l){
		AMSAgentDescription [] agents = null;
		SearchConstraints c = new SearchConstraints();
        c.setMaxResults ( new Long(-1) );
        
		try {
			agents = AMSService.search( this, new AMSAgentDescription (), c );
			
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setProtocol(Constants.INFORM_STARTMODELING);
			msg.setConversationId(Constants.NULL_CID);
			msg.setContentObject(l);
			for(AMSAgentDescription i:agents){
				msg.addReceiver(i.getName());
			}
			
			log.log(Logger.INFO,getLocalName() + "#starting modeling procedure. stand by...");
			
			send(msg);
			
		} catch (Exception e) {
			log.log(Logger.SEVERE, "Exception: " ,e);
		} 
	}
	
	@Override
	protected void onGuiEvent(GuiEvent ev) {
		switch(ev.getType()){
		case Constants.TEST_GUIEVENT:
			testguimethod();
			//example();
			break;
		case Constants.SHOW_GUI_GUIEVENT:
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.addReceiver(new AID((String) ev.getParameter(0),AID.ISLOCALNAME));
			msg.setProtocol(Constants.REQUEST_SHOW_GUI);
			msg.setConversationId(Constants.NULL_CID);
			send(msg);
			break;
		case Constants.STARTMODELING_GUIEVENT:
			startModeling( ((Long)ev.getParameter(0)) * 1000 );
			//example();
			break;
		}
		
	}
	
	private Node addRouter(AID agentid,double fprob){
		AgentController a;
		Object [] args = null;
		Node node = new Node(agentid,Node.ROUTER_TYPE);
		
		nodes.put(agentid, node);
		
		args = new Object[1];
		args[0]=new Double(fprob);
		
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
	private Node addRouter(AID agentid){
		return addRouter(agentid,1.0);
	}
	
	private Node addUser(AID agentid, String gateway, List<AID> subscriptions){
		AgentController a;
		Object [] args;
		
		args = new Object[2];
		args[0]=new AID(gateway,AID.ISLOCALNAME);
		args[1]=subscriptions;
		
		Node node = new Node(agentid,Node.USER_TYPE);
		nodes.put(agentid, node);
		
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
	
	private Node addUser(AID agentid, AID gatewayid, List<AID> subscriptions){
		return addUser(agentid, gatewayid.getLocalName(), subscriptions);
	}
	
	private Node addServer(AID agentid, String gateway){
		AgentController a;
		Object [] args;
		
		args = new Object[1];
		args[0]=new AID(gateway,AID.ISLOCALNAME);
		
		Node node = new Node(agentid,Node.SERVER_TYPE);
		nodes.put(agentid, node);
		
		try {
			a = containercontr.createNewAgent(agentid.getLocalName(), "ru.volterr.nam.agents.ServerAgent", args);
			a.start();
			graph.addVertex(node);
			gui.graphRepaint();
			return node;
		} catch (StaleProxyException e) {
			log.log(Logger.SEVERE,"Create new agent exception:",e);
		}
		return null;
	}	
	
	private Node addServer(AID agentid, AID gatewayid){
		return addServer(agentid, gatewayid.getLocalName());
	}
	
	private void addLink(Link link){
		addBehaviour(new ConnNewLink(link));
		graph.addEdge(link, link.getA(), link.getZ(), link.getType());
		gui.graphRepaint();
	}
	
	private DijkstraShortestPath<Node,Link> alg = null;
	
	/**
	 * @param src
	 * @param dest
	 * @return true if path found, false if not
	 */
	public boolean findNewRoute(AID src, AID dest){
		if(alg == null){
			alg = new DijkstraShortestPath<Node,Link>(graph, new BandTransformer());
		}
		
		Node snode = new Node(src,Node.USER_TYPE);
		Node dnode = new Node(dest,Node.USER_TYPE);
		
		AIDPair pair = new AIDPair(src,dest);
		
		List<Link> path = alg.getPath(snode, dnode);	//list of links in a path
		
		List<Node> route =transLinkNodePath(path, snode, dnode);
		
		if(route!=null)
		{
			routes.put(pair, route);
			return true;
		}else{
			//TODO needs expiration time for route
			//routes.put(pair, null);
			return false;
		}
		
	}
	
	public AID getNextRouteHop(AID requester, AID src, AID dest){
		log.log(Logger.INFO,getLocalName() + "#getting next route hop for " + requester.getLocalName() + " in path (" + src.getLocalName() + ", " + dest.getLocalName() + ").");
		
		AIDPair p = new AIDPair(src,dest);
		
		if(routes.containsKey(p)){
			ArrayList<Node> route = (ArrayList<Node>) routes.get(p);
			
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
		}else{
			log.log(Logger.INFO,getLocalName() + "#finding path (" + src.getLocalName() + ", " + dest.getLocalName() + ") in graph. by request of "+requester.getLocalName());
			if(findNewRoute(src,dest))	
				return getNextRouteHop(requester, src, dest);
			else
				return null;
		}
	}
	
	private List<Node> transLinkNodePath(List<Link> lpath,Node src, Node dest){
		List<Node> npath = new ArrayList<Node>();
		Node a,z;
		
		npath.add(src);
		
		for(Link link:lpath){
			a=link.getA();
			z=link.getZ();
			if(npath.get(npath.size()-1).equals(a))
				npath.add(z);
			else{
				if(npath.get(npath.size()-1).equals(z))
					npath.add(a);
				else 
					return null;
			}
		}
		
		if(npath.get(npath.size()-1).equals(dest))
			return npath;
		else
			return null;
	}
	
	//false is down true - up
	public void nodeStatusChange(AID agent, boolean updown){
		if(!updown)
			nodes.get(agent).setStatus(Node.STATUS_DOWN);
		else
			nodes.get(agent).setStatus(Node.STATUS_UP);
		gui.graphRepaint();
	}
	
	private void example(){
		
		//routers
		Node gattaca = addRouter(new AID("Gattaca",AID.ISLOCALNAME)),
			 sprawl = addRouter(new AID("Sprawl",AID.ISLOCALNAME),0.5),
			 aegis = addRouter(new AID("Aegis7",AID.ISLOCALNAME)),
			 tauvolantis = addRouter(new AID("TauVolantis",AID.ISLOCALNAME)),
			 titan = addRouter(new AID("Titan",AID.ISLOCALNAME));
		//servers
		AID ftp = new AID("FTP",AID.ISLOCALNAME),
			samba = new AID("SAMBA",AID.ISLOCALNAME),
			http = new AID("HTTP",AID.ISLOCALNAME);
		Node ftpnode = addServer(ftp,tauvolantis.getId()),
			 sambanode = addServer(samba,titan.getId()),
			 httpnode = addServer(http,tauvolantis.getId());
		//users
		AID a = new AID("Alpha",AID.ISLOCALNAME),
			b = new AID("Bravo",AID.ISLOCALNAME),
			c = new AID("Charlie",AID.ISLOCALNAME);
		Node alpha = addUser(a,gattaca.getId(),new ArrayList<AID>(Arrays.asList(ftp,samba,http))),
			 bravo = addUser(b,aegis.getId(),new ArrayList<AID>(Arrays.asList(ftp,samba))),
			 charlie = addUser(c,sprawl.getId(),new ArrayList<AID>(Arrays.asList(http)));
		//links
		Link alpha_gattaca = new Link(alpha, gattaca, 1000),	//users to defaultgateway - TODO automated
			 bravo_aegis = new Link(bravo, aegis, 1000),
			 charlie_sprawl = new Link(charlie, sprawl, 1000),
			 
			 ftp_tauvolantis = new Link(ftpnode,tauvolantis, 1000),	//servers to defaultgateway - TODO automated
			 samba_titan = new Link(sambanode,titan, 1000),
			 http_tauvolantis = new Link(httpnode,tauvolantis, 1000),
			 
			 gattaca_aegis = new Link(gattaca, aegis, 1000),	//interconnections between routers
			 gattaca_sprawl = new Link(gattaca, sprawl, 2000),
			 sprawl_aegis = new Link(sprawl, aegis, 500),
			 tauvolantis_gattaca = new Link(tauvolantis,gattaca, 1000),
			 titan_tauvolantis = new Link(titan,tauvolantis, 2000),
			 titan_aegis = new Link(titan,aegis, 3000);
		
		addLink(alpha_gattaca);
		addLink(bravo_aegis);
		addLink(charlie_sprawl);
		
		addLink(ftp_tauvolantis);
		addLink(samba_titan);
		addLink(http_tauvolantis);
		
		addLink(gattaca_aegis);
		addLink(gattaca_sprawl);
		addLink(sprawl_aegis);
		addLink(tauvolantis_gattaca);
		addLink(titan_tauvolantis);
		addLink(titan_aegis);
		
		//routes
		/*routes.put(  new AIDPair(a,b),new ArrayList<Node>( Arrays.asList(alpha,gattaca,aegis,bravo) )  );
		routes.put(  new AIDPair(a,c),new ArrayList<Node>( Arrays.asList(alpha,gattaca,sprawl,charlie) )  );
		routes.put(  new AIDPair(b,a),new ArrayList<Node>( Arrays.asList(bravo,aegis,gattaca,alpha) )  );
		routes.put(  new AIDPair(b,c),new ArrayList<Node>( Arrays.asList(bravo,aegis,sprawl,charlie) )  );
		routes.put(  new AIDPair(c,b),new ArrayList<Node>( Arrays.asList(charlie,sprawl,aegis,bravo) )  );
		routes.put(  new AIDPair(c,a),new ArrayList<Node>( Arrays.asList(charlie,sprawl,gattaca,alpha) )  );
		*/
		//log.log(Logger.INFO,getLocalName() + "#example topology created:" + 
		//					"\nRouters: " + gr.getLocalName() + ", " + sr.getLocalName() + ", " + ar.getLocalName() + 
		//					"\nUsers: " + a.getLocalName() + ", " + b.getLocalName() + ", " + c.getLocalName() + 
		//					"\nroutes: " + routes.toString());
		
		
	}
	
	public void testguimethod(){
		System.out.println("Cought event from Connector GUI");
		gui.testresult("OK");
		AID id = new AID("Sprawl",AID.ISLOCALNAME);
		if(nodes.get(id).getStatus()==Node.STATUS_UP)
			nodeStatusChange(id, false);
		else
			nodeStatusChange(id, true);
	}
}
