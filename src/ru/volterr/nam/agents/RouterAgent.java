package ru.volterr.nam.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ru.volterr.nam.Constants;
import ru.volterr.nam.behaviours.router.RouterCoffee;
import ru.volterr.nam.behaviours.router.RouterPingNeighbours;
import ru.volterr.nam.behaviours.router.RouterPort;
import ru.volterr.nam.behaviours.router.RouterReceiveMsg;
import ru.volterr.nam.behaviours.router.RouterRecvPing;
import ru.volterr.nam.behaviours.router.RouterRecvPong;
import ru.volterr.nam.behaviours.router.RouterRequestRoute;
import ru.volterr.nam.behaviours.user.UserReceiveMsg;
import ru.volterr.nam.behaviours.user.UserStaticGenTraffic;
import ru.volterr.nam.behaviours.user.UserSubscribe;
import ru.volterr.nam.gui.views.RouterGUI;
import ru.volterr.nam.model.Link;

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

public class RouterAgent extends GuiAgent {

	public Map<AID,AID> routetable = new HashMap<AID,AID>(); //Map<RecvUser,NextAgent>
	public Map<AID,RouterPort> ports = new HashMap<AID,RouterPort>(); //direct connections to other routers/users
	public double fprob = 1.0;	//failure probability
	
	private Logger log;
	
	private RouterGUI gui;
	
	public RouterAgent(){
		super();
		
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
					
	}
	
	public void setup(){
		
		//parse arguments
		Object[] args = getArguments();
		if(args!=null && args.length>0)
			try{
				this.fprob=(Double)args[0];
			}catch(Exception e){
				log.log(Logger.SEVERE, "Exception:", e);
			}
		
		log.log(Logger.INFO, "I am " + getLocalName() + ". Smart " + getLocalName() + "!");
		
		//init gui
		gui = new RouterGUI(this);
		gui.setVisible(false);
		
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Traffic-routing");
		sd.setName(getLocalName()+" Router");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}catch (FIPAException fe) {
			log.log(Logger.SEVERE,"FIPAException:",fe);
		}
		
		//add behaviours
		addBehaviour(new RouterReceiveMsg(this));
		addBehaviour(new RouterRecvPing());
		addBehaviour(new RouterPingNeighbours(this));
		addBehaviour(new RouterCoffee(this));
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
	
	
	
	public AID getNextRecv(AID dest){
		return routetable.get(dest);
	}

	
	//operations with new ports
	public void addPort(Link link){
		RouterPort port = new RouterPort(this,link);;
		if(link.getA().getId().equals(getAID())){
			ports.put(link.getZ().getId(), port);
		}else{
			ports.put(link.getA().getId(), port);
		}
		addBehaviour(port);
	}
	
	public void delPort(Link link){
		ports.remove(link.getA().getId());
		ports.remove(link.getZ().getId());
	}
	
	public RouterPort getPort(AID dest) {
		return ports.get(dest);
	}

	@Override
	public void onGuiEvent(GuiEvent ev) {
		// TODO Auto-generated method stub
		
	}
	
	public void showGui(){
		gui.setVisible(true);
		gui.update();
		
	}

	public void updatePorts(Set<AID> aids) {
		Set<Entry<AID, RouterPort>> set = ports.entrySet();
		Iterator<Entry<AID, RouterPort>> iter = set.iterator();
		Entry<AID, RouterPort> entry;
		while(iter.hasNext())
		{
			entry = iter.next();
			if( (entry.getValue().getState()==RouterPort.STATE_UP)&&(!aids.contains(entry.getKey())) ){
				entry.getValue().setState(RouterPort.STATE_DOWN);
			}
			if( (entry.getValue().getState()==RouterPort.STATE_DOWN)&&(aids.contains(entry.getKey())) ){
				entry.getValue().setState(RouterPort.STATE_UP);
			}
		}
	}

	public void splitcoffee() {
		DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd  = new ServiceDescription();
        sd.setType( "Connector" );
        dfd.addServices(sd);
        
        DFAgentDescription[] result;
		try {
			result = DFService.search(this, dfd);
	
			AID connectorid = result[0].getName();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(connectorid);
			msg.setProtocol(Constants.INFORM_COFFEE);
			msg.setConversationId(Constants.NULL_CID);
			msg.setContent("down");
			send(msg);
			//here router sleeps
			wait(10000);
			//here it wakes up
			msg.setContent("up");
			send(msg);
		}catch(Exception e){
			log.log(Logger.INFO, "Exception:", e);
		}
		
	}
	
}
