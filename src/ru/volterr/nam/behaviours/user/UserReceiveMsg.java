package ru.volterr.nam.behaviours.user;

import ru.volterr.nam.Constants;
import ru.volterr.nam.agents.UserAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;

public class UserReceiveMsg extends CyclicBehaviour {

	protected UserAgent myUser;
	private ACLMessage L3msg;
	private ACLMessage L2msg;
	private Logger log;
	
	public UserReceiveMsg(UserAgent a){
		super(a);
		myUser = a;
		
		//init logger
		log = Logger.getMyLogger(this.getClass().getName());
	}
	
	@Override
	public void action() {
		L2msg = myUser.receive();
		if (L2msg != null) {
			switch(L2msg.getPerformative()){
				case ACLMessage.INFORM:
					if(L2msg.getProtocol().equals(Constants.INFORM_MESSAGE)){
						try{
							L3msg = (ACLMessage) L2msg.getContentObject();
							
							log.log(Logger.INFO,myUser.getLocalName() + "# received message(" + 
												L2msg.getConversationId() + ") from " + 
												L3msg.getSender().getLocalName() + ": " + 
												L3msg.getContent());
						}catch(Exception e){
							log.log(Logger.SEVERE,"can't cast to ACLMessage. " +
												"L2msg has wrong format and doesn't contain " +
												"L3msg inside of itself \nException: ",e);
						}
					}
					break;
				case ACLMessage.SUBSCRIBE:
					if(L2msg.getContent().equals("subscribe"))
						myUser.addSubscriber(L2msg.getSender());
					if(L2msg.getContent().equals("unsubscribe"))
						myUser.removeSubscriber(L2msg.getSender());
					break;
						
			}
			
		}
		// Блокируем поведение, пока в очереди сообщений агента
		// не появится хотя бы одно сообщение
		block();
	}

}
