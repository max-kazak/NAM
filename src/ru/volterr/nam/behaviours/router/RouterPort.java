package ru.volterr.nam.behaviours.router;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.util.LinkedList;

import ru.volterr.nam.agents.RouterAgent;
import ru.volterr.nam.model.Link;

public class RouterPort extends CyclicBehaviour {

	private Link link;
	private int drops = 0;
	
	private RouterAgent myRouter;
	
	private LinkedList<ACLMessage> stack = new LinkedList<ACLMessage>();
	private int stacksize = 2;
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
			if(stack.size()<stacksize){
				stack.add(msg);
				log.log(Logger.INFO,myAgent.getLocalName() + "#message is placed in a queue");
			}else{
				drops++;
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
		if((dt=wakeuptime-System.currentTimeMillis())<0)
		{
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
	}
	
	private void send(){
		log.log(Logger.INFO,myAgent.getLocalName() + "#message is sent to " + ((AID)sending.getAllReceiver().next()).getLocalName());

		myAgent.send(sending);
		sending = null;
		if(stack.size()>0){
			sending = stack.removeFirst();
			delay = getDelay();
			wakeuptime = System.currentTimeMillis() + delay;
			dt = delay;
			block(dt);
		}else{
			link.setStatus(myRouter.getAID(),Link.FREE_STATUS);
			block();
		}
	}
	

	private long getDelay(){
		return 1500*1000/link.getBandwidth();
	}
	private long getDelay(ACLMessage msg){
		//TODO
		return getDelay();
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
	
}
