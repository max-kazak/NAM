package ru.volterr.nam.behaviours.router;

import java.util.Iterator;
import java.util.Set;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.RouterAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class RouterPingNeighbours extends CyclicBehaviour {

	RouterAgent myRouter;
	
	 private static int cidCnt = 0;
	 private String cidBase ;
	 private String lastCID ;
		
	 private Set<AID> neigh;
	 
	 private long 	dt,
	 				wakeuptime=0,
	 				delay;

	private Logger log;
	 
	public RouterPingNeighbours(RouterAgent myRouter){
			super(myRouter);
			this.myRouter = myRouter;
			delay = 5000;
			log = Logger.getMyLogger(this.getClass().getName());
	} 
	public RouterPingNeighbours(RouterAgent myRouter, long delay){
		super(myRouter);
		this.myRouter = myRouter;
		this.delay = delay;
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	private String genCID() 
	   { 
	      if (cidBase==null) {
	         cidBase = myRouter.getLocalName() + hashCode() +
	                      System.currentTimeMillis()%10000 + "_";
	      }
	      lastCID = cidBase + (cidCnt++);
	      return  lastCID; 
	   }
	
	@Override
	public void action() {
		if((dt=wakeuptime-System.currentTimeMillis())<0)
		{		
			neigh = myRouter.ports.keySet();
			ACLMessage ping = createPing();
			
			myRouter.send(ping);
			
			log.log(Logger.INFO,myAgent.getLocalName() + "#ping sended to all neighbours");
			
			myRouter.addBehaviour(new RouterRecvPong(myRouter, lastCID));
			
			wakeuptime = System.currentTimeMillis()+delay;
			dt=delay;
		}
		
		block(dt);
	}
	
	private ACLMessage createPing(){
		
		ACLMessage ping = new ACLMessage(ACLMessage.REQUEST);
		Iterator<AID> iter = neigh.iterator();
		while(iter.hasNext()){
			ping.addReceiver(iter.next());
		}
		ping.setConversationId(genCID());
		ping.setProtocol(Constants.REQUEST_PING);
		return ping;
	}

}
