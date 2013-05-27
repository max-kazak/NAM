package ru.volterr.nam.behaviours.router;

import java.util.HashSet;
import java.util.Set;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.RouterAgent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

public class RouterRecvPong extends SimpleBehaviour{

	private long    timeOut, 
    				wakeupTime=0;
	private MessageTemplate template;
	private boolean finished;
	private Set<AID> aids = new HashSet<AID>();
	private RouterAgent myRouter;
	private Logger log;

	public RouterRecvPong(RouterAgent a, int millis, String cid){
		super(a);
		myRouter=a;
	    timeOut = millis;
	    template = MessageTemplate.and(MessageTemplate.MatchConversationId(cid),
	    								MessageTemplate.MatchProtocol(Constants.CONFIRM_PONG));
	    log = Logger.getMyLogger(this.getClass().getName());
	}
	public RouterRecvPong(RouterAgent a, String cid){
		super(a);
		myRouter=a;
	    timeOut = 2000;
	    template = MessageTemplate.and(MessageTemplate.MatchConversationId(cid),
	    								MessageTemplate.MatchProtocol(Constants.CONFIRM_PONG));
	    log = Logger.getMyLogger(this.getClass().getName());
	}

	@Override
	public void action() {
		ACLMessage	msg = myAgent.receive(template);

		if( msg != null) {
			log.log(Logger.INFO,myAgent.getLocalName() + "#pong received from "+msg.getSender().getLocalName());
			handle( msg );
			return;
		}
		
	    long dt = wakeupTime - System.currentTimeMillis();
	    
	    if ( dt > 0 ) 
	      	block(dt);
	    else {
	    	myRouter.updatePorts(aids);
	    	finished = true;	
	    }
		
	}

	@Override
	public boolean done() {
		return finished;
	}
	
	public void onStart() {
		wakeupTime = (timeOut<0 ? Long.MAX_VALUE
		              :System.currentTimeMillis() + timeOut);
	}
	
	public void handle( ACLMessage m) { 
		aids.add(m.getSender());
	}
	
	public void reset() {
		finished = false;
		super.reset();
  	}
  	
	public void reset(int dt) {
		timeOut= dt;
		reset();
  	}
	
}
