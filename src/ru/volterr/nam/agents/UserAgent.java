package ru.volterr.nam.agents;

import java.util.ArrayList;
import java.util.List;

import ru.volterr.nam.behaviours.user.UserReceiveMsg;
import ru.volterr.nam.behaviours.user.UserStaticGenTraffic;
import ru.volterr.nam.behaviours.user.UserSubscribe;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;

public class UserAgent extends Agent {

	public List<AID> subscribers = new ArrayList<AID>();
	public AID gateway;
	public int dt = 1000;
	
	private Logger log;
	
	public void setup(){
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
		
		//parse arguments
		Object[] args = getArguments();
		if(args!=null && args.length>0)
			try{
				this.gateway=(AID)args[0];
				if (args[1] != null)
					this.subscribers.addAll( (List<AID>) args[1] );
				if (args[2] != null)
					addBehaviour(new UserSubscribe((List<AID>) args[2],true));
			}catch(Exception e){
				log.log(Logger.SEVERE, "Exception:", e);
			}
		
		log.log(Logger.INFO, "I am " + getLocalName() + ". Damn " + getLocalName() + "!");
		
		// Register the user generating service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Traffic-generating");
		sd.setName(getLocalName()+" User");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		addBehaviour(new UserReceiveMsg(this));
		addBehaviour(new UserStaticGenTraffic(this, dt));
		
	}
	
	
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
	}
	
	synchronized public void addSubscribers(List<AID> targets){
		subscribers.addAll(targets);
	}
	
	synchronized public void addSubscriber(AID target){
		subscribers.add(target);
	}
	
	synchronized public void removeSubscribers(List<AID> targets){
		subscribers.removeAll(targets);
	}
	
	synchronized public void removeSubscriber(AID target){
		subscribers.remove(target);
	}

}
