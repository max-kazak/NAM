package ru.volterr.nam.agents;

import java.util.HashMap;
import java.util.Map;

import ru.volterr.nam.behaviours.router.RouterPort;
import ru.volterr.nam.behaviours.router.RouterReceiveMsg;
import ru.volterr.nam.behaviours.router.RouterRequestRoute;
import ru.volterr.nam.behaviours.user.UserReceiveMsg;
import ru.volterr.nam.behaviours.user.UserStaticGenTraffic;
import ru.volterr.nam.model.Link;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;

public class RouterAgent extends Agent {

	public Map<AID,AID> routetable = new HashMap<AID,AID>(); //Map<RecvUser,NextAgent>
	public Map<AID,RouterPort> ports = new HashMap<AID,RouterPort>(); //direct connections to other routers/users
	
	private Logger log;
	
	public void setup(){
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
		
		log.log(Logger.INFO, "I am " + getLocalName() + ". Smart " + getLocalName() + "!");
		
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
	
}
