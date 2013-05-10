package ru.volterr.nam.behaviours.user;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

import java.io.IOException;
import java.util.Random;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.UserAgent;

public class UserStaticGenTraffic extends CyclicBehaviour{

	private long dt,
				 wakeuptime=0,
				 delay;
	private Random rand = new Random();
	
	
	protected UserAgent myUser;
	private Logger log;
	
	
	public UserStaticGenTraffic(UserAgent a, int delay) {
		super(a);
		myUser = a;
		this.delay=delay;
		dt=delay;
		
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	//generates and sends messages
	@Override
	public void action() {
		
		if((dt=wakeuptime-System.currentTimeMillis())<0)
		{		
			try {
				int i = rand.nextInt(myUser.subscribers.size());
				ACLMessage msgL3 = new ACLMessage(ACLMessage.INFORM);
				msgL3.setContent("42");
				msgL3.addReceiver(myUser.subscribers.get(i));
				msgL3.setSender(myUser.getAID());
				
				ACLMessage msgL2 = new ACLMessage(ACLMessage.INFORM);
				msgL2.setContentObject(msgL3);
				String id = Long.toString( System.currentTimeMillis() );
				msgL2.setConversationId(id);
				msgL2.addReceiver(myUser.gateway);
				msgL2.setProtocol(Constants.INFORM_MESSAGE);
				
				log.log(Logger.INFO,myUser.getLocalName() + "# sended message(" + id + ") to " 
												+ myUser.subscribers.get(i).getLocalName());
				myUser.send(msgL2);
			} catch (IOException e) {
				log.log(Logger.SEVERE,"problems with output stream exception: ",e);
			} catch(IllegalArgumentException e){
				log.log(Logger.WARNING, "User " + myUser.getLocalName() + " must have at least one destination user to generate traffic", e);
				block();
			} catch (Exception e){
				log.log(Logger.SEVERE,"Exception",e);
			}
			
			wakeuptime = System.currentTimeMillis()+delay;
			dt=delay;

		}
		
		block(dt);
	}

}
