package ru.volterr.nam.behaviours.router;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

import java.util.LinkedList;

import ru.volterr.nam.agents.RouterAgent;
import ru.volterr.nam.model.Link;

public class RouterPort extends CyclicBehaviour {

	public static final int STATE_UP = 0;
	public static final int STATE_DOWN = 1;
	
	private Link link;
	private int drops = 0;
	private int state = STATE_UP;
	
	private RouterAgent myRouter;
	
	private LinkedList<ACLMessage> stack = new LinkedList<ACLMessage>();
	private int stacksize = 20, cursize=0;
	private ACLMessage sending;
	
	private long dt,
				 wakeuptime =0,
				 delay;
	
	private Logger log;
	
	//constructors
	public RouterPort(RouterAgent myRouter){
		this.myRouter = myRouter;
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
	}
	public RouterPort(RouterAgent myRouter, Link link){
		this.myRouter = myRouter;
		this.link = link;
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	//getters and setters
	public Link getLink() {
		return link;
	}
	public void setLink(Link link) {
		this.link = link;
	}
	public LinkedList<ACLMessage> getStack() {
		return stack;
	}
	
	
	public void post(ACLMessage msg){
		switch(link.getStatus(myRouter.getAID())){
		case Link.BUSY_STATUS:
			int size;
			try {
				size = (Integer)(  ( (ACLMessage)msg.getContentObject() ).getContentObject()  );
			} catch (UnreadableException e) {
				log.log(Logger.SEVERE, "Check correctness of incapsulation", e);
				size = 1;
			}
			cursize+=size;
			if(cursize<stacksize){
				stack.add(msg);
				log.log(Logger.INFO,myAgent.getLocalName() + "#message is placed in a queue");
			}else{
				cursize-=size;
				drops++;
				stack.add(msg);
				stack.removeFirst();
				log.log(Logger.INFO,myAgent.getLocalName() + "#queue overflow on link " + link.getName());
			}
			break;
		case Link.FREE_STATUS:
			link.setStatus(myRouter.getAID(),Link.BUSY_STATUS);
			sending = msg;
			wakeuptime = System.currentTimeMillis() + getDelay();
			restart();
			break;
		}
	}
	
	@Override
	public void action() {
		if(state==STATE_UP){
			if((dt=wakeuptime-System.currentTimeMillis())<0){
				//time is up or not setted up
				if (sending != null){
						send();
				}else{
					//unexpected launch. back to blocking state 
					block();
				}
			}else{
				//still not time
				block(dt);
			}
		}else{
			log.log(Logger.INFO,myAgent.getLocalName() + "#Router on other side is still not available ");
			//nobody listening
			block();
		}
	}
	
	private void send(){
		log.log(Logger.INFO,myAgent.getLocalName() + "#message is sent to " + ((AID)sending.getAllReceiver().next()).getLocalName());

		myAgent.send(sending);
		int size;
		try {
			size = (Integer)(  ( (ACLMessage)sending.getContentObject() ).getContentObject()  );
		} catch (UnreadableException e) {
			log.log(Logger.SEVERE, "Check correctness of incapsulation", e);
			size = 1;
		}
		cursize-=size;
		sending = null;
		
			if(stack.size()>0){
				sending = stack.removeFirst();
				delay = getDelay(sending);
				wakeuptime = System.currentTimeMillis() + delay;
				dt = delay;
				block(dt);
			}else{
				link.setStatus(myRouter.getAID(),Link.FREE_STATUS);
				block();
			}
	}
	

	private long getDelay(){
		return 1500*100/link.getBandwidth();
	}
	private long getDelay(ACLMessage msg){
		long delay;
		try {
			int size = (Integer)(  ( (ACLMessage)msg.getContentObject() ).getContentObject()  );
			delay = getDelay()*size;
		} catch (UnreadableException e) {
			log.log(Logger.SEVERE, "Check correctness of incapsulation", e);
			return getDelay();
		}
		return delay;
	}
	public int getStacksize() {
		return stacksize;
	}
	public void setStacksize(int stacksize) {
		this.stacksize = stacksize;
	}
	public int getDrops() {
		return drops;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		if(state==STATE_DOWN)
			wakeuptime=Long.MAX_VALUE;
		else
			wakeuptime=0;
		this.state = state;
	}
	
}
