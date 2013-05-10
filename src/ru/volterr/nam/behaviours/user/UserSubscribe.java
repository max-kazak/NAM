package ru.volterr.nam.behaviours.user;

import java.util.ArrayList;
import java.util.List;

import ru.volterr.nam.agents.UserAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class UserSubscribe extends OneShotBehaviour {
	
	List<AID> subscriptions;
	boolean subscr;
	private Logger log;
	
	/**
	 * 
	 * @param targets - subscriptions
	 * @param subscribe - true:subscribe, false:unsubscribe
	 */
	public UserSubscribe(List<AID> targets, boolean subscribe){
		super();
		this.subscr=subscribe;
		this.subscriptions = targets;
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	/**
	 * 
	 * @param targets - subscriptions
	 * @param subscribe - true:subscribe, false:unsubscribe
	 */
	public UserSubscribe(AID target, boolean subscribe){
		super();
		this.subscr=subscribe;
		this.subscriptions = new ArrayList<AID>();
		this.subscriptions.add(target);
	}
	
	@Override
	public void action() {
		
		ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
		for(AID i: subscriptions)
			msg.addReceiver(i);
		if(subscr){
			msg.setContent("subscribe");
			//message log
			log.log(Logger.INFO,myAgent.getLocalName() + "# subscribing to " + subscriptions.toString() );
		}
		else{
			msg.setContent("unsubscribe");
			//message log
			log.log(Logger.INFO,myAgent.getLocalName() + "# unsubscribing from " + subscriptions.toString() );
		}
		myAgent.send(msg);
	}

}
